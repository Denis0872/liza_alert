# CITRUS Delivery Record

- Change set: `vps-update-to-latest`
- Goal: update the VPS deployment to the latest repository commit and verify runtime health

## Environment

- Host: `185.21.8.116:2222`
- Deploy directory: `/home/denis/apps/liza_alert`
- Service: `liza-alert.service`

## Actions performed

- compared local HEAD with deployed VPS commit
- updated the VPS repository with `git pull --ff-only`
- rebuilt the application on the VPS with Maven
- restarted the managed `systemd` service
- removed the old stray `nohup` Java process so `systemd` owns port `8080`

## Validation

- `git rev-parse HEAD` on local and VPS
- `systemctl status liza-alert.service`
- `ss -ltnp` for port `8080`
- `curl http://localhost/api/v1/lost-cases` on VPS
- `curl http://185.21.8.116/api/v1/lost-cases` externally

## Outcome

- VPS now runs commit `a6f1ed7`
- `liza-alert.service` is active and owns port `8080`
- API responds with HTTP `200` both locally on the VPS and externally through `nginx`

## Notes

- an old `nohup` Java process from the first manual start had to be removed because it held port `8080`
- after cleanup, only the `systemd`-managed Java process remains bound to the application port
