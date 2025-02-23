# sschm - Simple Stupid Car Health Manager

* Fuel efficiency
* Manage maintenance cycle
* Self-hosted

TODO - screenshots


# Usage

```bash
cd deploy
docker compose -p sschm up -d
```

Then access to `localhost:8080`


# Build

## Requirements

* Java 21
* Docker

## Build

### UI

```
cd ui
npm run build
```

### Server

```
sbt compile
```

### Build a docker image

```
sbt Docker/publishLocal

# check version
docker image ls | rg sschm

# make sure you logged in to ghcr

# tag and push it
docker image tag sschm:0.1.0-SNAPSHOT ghcr.io/minebreaker/sschm:latest
docker push ghcr.io/minebreaker/sschm:latest
```


## Units of internal data

Following units are used for internal data, and converted to user preferred ones on display

* Distance - int in km
* Time - zoned datetime if available, utc if not. User-entered local datetimes are assumed to have configured timezone
* Fuel - dL
