# CITRUS Delivery Record

- Change set: `vps-security-hardening`
- Goal: harden SSH access and recover the runtime after detecting suspicious CPU load from the PostgreSQL container

## Environment

- Host: `185.21.8.116:2222`
- Runtime hostname: `278122.fornex.cloud`

## Actions performed

- investigated CPU saturation on the VPS
- identified a suspicious binary `/tmp/mysql` running inside the PostgreSQL container
- rotated PostgreSQL password to a strong random value
- restricted PostgreSQL host exposure from `0.0.0.0:5434` to `127.0.0.1:5434`
- recreated the PostgreSQL container to remove suspicious `/tmp` artifacts
- updated the application `systemd` unit with the new database password
- enabled HTTPS for `278122.fornex.cloud` using Let's Encrypt and nginx redirect
- enforced SSH key-only access:
  - `AuthenticationMethods publickey`
  - `PasswordAuthentication no`
  - `KbdInteractiveAuthentication no`
  - `PermitRootLogin prohibit-password`
- copied working SSH authorized keys from `denis` to `daniil`
- enabled passwordless sudo for `daniil`

## Validation

- `top`, `ps`, `docker top liza-alert-postgres`
- `curl https://278122.fornex.cloud/api/v1/lost-cases`
- `ssh -o PreferredAuthentications=password -o PubkeyAuthentication=no ... denis@185.21.8.116`
- `ssh daniil@185.21.8.116` with key authentication
- `sudo -n true` as `daniil`

## Outcome

- suspicious high-CPU process no longer exists in the PostgreSQL container
- PostgreSQL is no longer exposed publicly, only on `127.0.0.1:5434`
- site and API are healthy over HTTPS
- SSH password login is blocked; public keys are required
- `daniil` can log in via key and use `sudo` without password

## Notes

- this record intentionally does not store the rotated PostgreSQL password value
- the VPS still receives heavy SSH brute-force traffic, but password login is now disabled
