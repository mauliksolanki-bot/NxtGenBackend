#!/usr/bin/env bash
set -euo pipefail
release_id=${1:?Release ID is required}
server_host=${2:-172.198.161.188}
root=/opt/nxtgen/backend
[[ $release_id =~ ^[0-9]+$ ]] || { echo 'Invalid release ID.' >&2; exit 1; }
[[ $server_host =~ ^[a-zA-Z0-9.-]+$ ]] || { echo 'Invalid server host.' >&2; exit 1; }
release="$root/releases/$release_id"
test -s "$release/app.jar"
chmod 755 "$root" "$root/releases" "$release"
chmod 644 "$release/app.jar"
exec 9>"$root/.deployment.lock"
flock -w 600 9
previous=$(readlink "$root/current" || true)
temporary="$root/.current-$release_id"
[[ ! -e "$temporary" && ! -L "$temporary" ]] || { echo 'Temporary release link already exists.' >&2; exit 1; }
[[ ! -e "$root/current" || -L "$root/current" ]] || { echo 'current must be a symlink.' >&2; exit 1; }

rollback() {
    if [[ -n "$previous" ]]; then
        ln -s "$previous" "$temporary"
        mv -Tf "$temporary" "$root/current"
        sudo -n systemctl restart nxtgen-api.service
        echo 'Restored the previous backend JAR. Database migrations are not rolled back.' >&2
    else
        sudo -n systemctl stop nxtgen-api.service
        unlink "$root/current"
        echo 'First deployment failed; the backend service was stopped.' >&2
    fi
}

ln -s "$release" "$temporary"
mv -Tf "$temporary" "$root/current"
if ! sudo -n systemctl restart nxtgen-api.service; then
    rollback
    exit 1
fi

# Stop stale health responses from the prior process; systemctl restart waits for it to stop.
healthy=false
for attempt in $(seq 1 180); do
    if systemctl is-active --quiet nxtgen-api.service && curl --fail --silent --max-time 5 \
        http://127.0.0.1:8080/api/health | grep -Eq '"status"[[:space:]]*:[[:space:]]*"ok"'; then
        healthy=true
        break
    fi
    sleep 2
done

if [[ $healthy != true ]]; then
    echo 'API startup failed. Check: sudo journalctl -u nxtgen-api.service -n 100 --no-pager' >&2
    rollback
    exit 1
fi

if ! curl --fail --silent --max-time 15 -H "Host: $server_host" http://127.0.0.1/api/health \
    | grep -Eq '"status"[[:space:]]*:[[:space:]]*"ok"'; then
    echo 'The API is healthy on port 8080, but the Nginx /api proxy failed.' >&2
    echo 'Check the existing frontend Nginx configuration; the healthy API remains running.' >&2
    exit 1
fi
echo "Backend release $release_id is healthy at http://$server_host/api/health"
