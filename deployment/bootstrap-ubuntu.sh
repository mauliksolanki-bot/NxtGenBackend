#!/usr/bin/env bash
# Run once on the VM: sudo bash deployment/bootstrap-ubuntu.sh YOUR_UBUNTU_USERNAME
set -euo pipefail
deploy_user=${1:?Usage: sudo bash bootstrap-ubuntu.sh USERNAME}
root=/opt/nxtgen/backend
script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
[[ $EUID -eq 0 ]] || { echo 'Run this setup script with sudo.' >&2; exit 1; }
[[ $deploy_user =~ ^[a-z_][a-z0-9_-]*\$?$ ]] || { echo 'Invalid deployment username.' >&2; exit 1; }
id "$deploy_user" >/dev/null

# Required companion files must be copied alongside this script, even if renamed.
[[ -f "$script_dir/nxtgen-api.service" ]] || {
    echo "Missing $script_dir/nxtgen-api.service. Copy the backend deployment folder to the VM before running setup." >&2
    exit 1
}
if [[ ! -f "$root/shared/backend.env" && ! -f "$script_dir/backend.env.example" ]]; then
    echo "Missing $script_dir/backend.env.example. Copy the backend deployment folder to the VM before running setup." >&2
    exit 1
fi
deploy_group=$(id -gn "$deploy_user")

apt-get update
DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-21-jre-headless curl openssl util-linux
id nxtgen-api >/dev/null 2>&1 || useradd --system --home-dir /var/lib/nxtgen-api --shell /usr/sbin/nologin nxtgen-api
install -d -m 755 /opt/nxtgen
install -d -m 755 -o "$deploy_user" -g "$deploy_group" "$root" "$root/releases"
install -d -m 755 -o root -g root "$root/shared"

if [[ ! -f "$root/shared/backend.env" ]]; then
    install -m 600 -o root -g root "$script_dir/backend.env.example" "$root/shared/backend.env"
    jwt_secret=$(openssl rand -hex 32)
    sed -i "s/^PROD_JWT_SECRET=.*/PROD_JWT_SECRET=\"$jwt_secret\"/" "$root/shared/backend.env"
fi
install -m 644 -o root -g root "$script_dir/nxtgen-api.service" /etc/systemd/system/nxtgen-api.service

# Only grant service restart/stop; the pipeline doesn't need unrestricted sudo.
systemctl_path=$(command -v systemctl)
sudoers_file=$(mktemp)
trap 'rm -f "$sudoers_file"' EXIT
printf '%s ALL=(root) NOPASSWD: %s restart nxtgen-api.service, %s stop nxtgen-api.service\n' \
    "$deploy_user" "$systemctl_path" "$systemctl_path" > "$sudoers_file"
visudo -cf "$sudoers_file"
install -m 440 -o root -g root "$sudoers_file" /etc/sudoers.d/nxtgen-api-deployment
systemctl daemon-reload
systemctl enable nxtgen-api.service
echo 'Backend setup complete. Fill database settings before the first deployment:'
echo "sudo nano $root/shared/backend.env"
echo 'The pipeline will start the API after uploading the first JAR.'
