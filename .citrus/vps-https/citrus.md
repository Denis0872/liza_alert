# CITRUS Delivery Record

- Change set: `vps-https`
- Goal: enable HTTPS for the VPS deployment using Let's Encrypt and nginx

## Environment

- Host: `185.21.8.116:2222`
- Public hostname used for certificate: `278122.fornex.cloud`
- Email used for Let's Encrypt: `denispetergof@gmail.com`

## Actions performed

- verified that `278122.fornex.cloud` resolves to the VPS and serves HTTP successfully
- installed `certbot` and `python3-certbot-nginx`
- updated nginx `server_name` to `278122.fornex.cloud`
- requested a Let's Encrypt certificate with nginx integration
- enabled automatic HTTP -> HTTPS redirect

## Validation

- `curl -I http://278122.fornex.cloud`
- `curl -I https://278122.fornex.cloud`
- `curl https://278122.fornex.cloud/api/v1/lost-cases`
- `certbot certificates`
- `nginx -t`
- `systemctl is-active nginx liza-alert.service`

## Outcome

- HTTPS is enabled for `https://278122.fornex.cloud`
- HTTP now redirects to HTTPS with `301`
- API is available over HTTPS and returns `200`
- Certbot renewal timer is installed by the package setup

## Notes

- the certificate is issued for `278122.fornex.cloud`, not for the bare IP address
- external access should now use the hostname URL for a valid certificate chain
