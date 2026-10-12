# NxtGenRestAPI deployment

The pipeline builds Java 21, runs tests, and deploys the `development` branch to `172.198.161.188` using the frontend's existing `nxtgen-ubuntu-ssh` service connection. Ubuntu runs the API with systemd; Nginx's existing `/api/` proxy forwards requests to port 8080.

## One-time setup on Ubuntu

Copy this backend repository's `deployment` folder onto the VM. Use the same Ubuntu account as the SSH service connection:

Keep `bootstrap-ubuntu.sh`, `backend.env.example`, and `nxtgen-api.service` together in the same directory. Renaming the script to `bootstrap-ubuntu-backend.sh` is supported; the companion filenames must stay unchanged.

```bash
sudo bash deployment/bootstrap-ubuntu.sh YOUR_UBUNTU_USERNAME
sudo nano /opt/nxtgen/backend/shared/backend.env
```

Enter `PROD_DB_URL`, `PROD_DB_USERNAME`, and `PROD_DB_PASSWORD` for your existing database. Bootstrap generates a persistent `PROD_JWT_SECRET` once. Preserve that value between deployments. The environment file is root-owned with mode 600; no database passwords are needed in the pipeline YAML.

If setup stopped after creating `shared/backend.env`, rerunning it preserves that file. Edit `/opt/nxtgen/backend/shared/backend.env` directly; changes to `backend.env.example` will not overwrite an existing runtime file.

Use quoted values as shown in the example file. This is a systemd environment file, not a shell script. Do not use `export`, shell substitutions, or commit the completed file. Existing environment files are preserved if bootstrap is run again.

Bootstrap installs Java 21 and configures `nxtgen-api.service`. It allows the deployment account to restart/stop only this service with passwordless sudo. It does not change your frontend deployment or Nginx configuration. Ubuntu needs the `openjdk-21-jre-headless` package available in its apt repositories; Ubuntu 24.04 supports this setup.

## Azure DevOps pipeline

1. Commit `azure-pipelines.yml`, `.gitattributes`, and `deployment` into branch **development** of **NxtGenRestAPI**:
   `https://dev.azure.com/mauliksolanki-bot/NxtGen/_git/NxtGenRestAPI`.
2. Create a new pipeline using **Azure Repos Git → NxtGenRestAPI → Existing Azure Pipelines YAML file → development → /azure-pipelines.yml**.
3. Authorize this backend pipeline to use **nxtgen-ubuntu-ssh**. The username/password are already stored there; reuse them.
4. Run the pipeline. Pushes to development build and deploy automatically. For pull requests, configure a development branch-policy build-validation rule; deployment remains limited to development runs.

The Build stage runs `mvn clean verify` and publishes the executable JAR. Deploy uploads it, switches the `current` symlink, restarts systemd, and waits for `/api/health`. Database migrations run during startup, so the health check allows several minutes.

## Verify and troubleshoot

```bash
systemctl status nxtgen-api.service --no-pager
sudo journalctl -u nxtgen-api.service -n 100 --no-pager
curl http://127.0.0.1:8080/api/health
curl -H 'Host: 172.198.161.188' http://127.0.0.1/api/health
```

Public health URL: `http://172.198.161.188/api/health`. The UI continues at `http://172.198.161.188/login`. Port 8080 is bound to loopback and does not need an inbound Azure firewall rule. Allow outbound connectivity to your database host and port.

If the local API health check passes but Nginx fails, the pipeline reports the proxy issue and leaves the healthy API running. Frontend bootstrap must have configured the `/api/` upstream to `http://127.0.0.1:8080`.

## Files and rollback

```text
/opt/nxtgen/backend/
  releases/<build-id>/app.jar
  releases/<build-id>/activate-release.sh
  current -> releases/<build-id>
  shared/backend.env
```

The frontend remains in `/opt/nxtgen/releases` and `/opt/nxtgen/current`.

Failed API startup restores the previous JAR and restarts it. For the first deployment, the service is stopped if startup fails. Database migrations are not reversed by JAR rollback; schema changes must remain compatible with the previous release.

Manual rollback to a retained release:

```bash
bash /opt/nxtgen/backend/releases/BUILD_ID/activate-release.sh BUILD_ID 172.198.161.188
```

After editing runtime environment settings, run `sudo systemctl restart nxtgen-api.service`.
