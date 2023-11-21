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

### Units of internal data

Following units are used for internal data, and converted to user preferred ones on display

* Distance - int in km
* Time - zoned datetime if available, utc if not. User-entered local datetimes are assumed to have configured timezone
* Fuel - dL
