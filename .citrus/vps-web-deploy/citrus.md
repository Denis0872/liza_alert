# CITRUS Delivery Record

- Change set: `vps-web-deploy`
- Goal: deploy the standalone `liza_alert_web` frontend to the VPS and keep `liza_alert_backend` available behind `/api/`

## Environment

- Host: `185.21.8.116:2222`
- Public domain: `https://lizaalertspb.ru`

## Actions performed

- built `liza_alert_web` locally with `npm run build`
- uploaded built static assets to `/var/www/liza_alert_web`
- updated nginx so that:
  - `/` serves static web files from `/var/www/liza_alert_web`
  - `/api/` proxies to `http://127.0.0.1:8080/api/`
- reloaded nginx after config validation

## Validation

- `curl -I https://lizaalertspb.ru`
- `curl https://lizaalertspb.ru`
- `curl https://lizaalertspb.ru/api/v1/lost-cases`
- `curl http://127.0.0.1:8080/api/v1/lost-cases` on the VPS

## Outcome

- public domain now serves the standalone web frontend
- backend remains reachable through `/api/`
- nginx and `liza-alert-backend.service` are both active
