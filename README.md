# WeatherFlow Tempest Binding

This binding reads live weather data from a WeatherFlow Tempest station.
The Tempest hub broadcasts its readings on the local network over UDP (port 50222), so no cloud account is needed for live data.
The binding can also fetch the WeatherFlow "Better Forecast" for your station from the WeatherFlow cloud.

This is a fork of William Welliver's [WeatherFlow Smart Weather binding](https://git.sr.ht/~hww3/org.openhab.binding.weatherflowsmartweather), updated for openHAB 5.2 and limited to the Tempest.
The binding id is still `weatherflowsmartweather`, so Things and Items created with the original binding keep working.

## Supported Things

| Thing type        | Description                                                                     |
|-------------------|---------------------------------------------------------------------------------|
| `hub`             | The Tempest hub (bridge). Reports signal strength, firmware and uptime.         |
| `tempest`         | The Tempest sensor, attached to a hub. Reports all weather observations.        |
| `better-forecast` | Hourly and daily forecast for your station from the WeatherFlow cloud.          |

The older WeatherFlow Air, Sky and Air Quality sensors are not supported.

## Discovery

The hub and the Tempest are discovered automatically from their UDP broadcasts as long as openHAB is on the same network as the hub.
Each device is added to the Inbox once. A manual scan from the Inbox reports devices again, in case you removed one.
The `better-forecast` Thing is added manually.

## Thing Configuration

The `hub` and `tempest` Things need no configuration.

### `better-forecast` Thing Configuration

| Name                    | Type    | Description                                                         | Default | Required |
|-------------------------|---------|---------------------------------------------------------------------|---------|----------|
| `station_id`            | integer | Your station id (see below)                                         | N/A     | yes      |
| `token`                 | text    | A personal access token from tempestwx.com                          | N/A     | yes      |
| `system_of_measurement` | text    | `Metric` or `US_Customary`                                          | N/A     | yes      |
| `keep_hourly`           | integer | Maximum number of hourly forecasts to keep. `0` keeps all.          | 0       | no       |
| `keep_daily`            | integer | Maximum number of daily forecasts to keep. `0` keeps all.           | 0       | no       |

To find your station id, log in at [tempestwx.com](https://tempestwx.com) and open your station.
The id is the number at the end of the address, for example `https://tempestwx.com/station/12345`.
Create a token at [tempestwx.com/settings/tokens](https://tempestwx.com/settings/tokens).

The forecast is refreshed every 15 minutes.
If the id or token is wrong, the Thing goes OFFLINE with a message saying which.

## Channels

### Hub

| Channel            | Type     | Description                         |
|--------------------|----------|-------------------------------------|
| `rssi`             | Number   | Wi-Fi signal strength of the hub    |
| `firmware_version` | String   | Hub firmware version                |
| `uptime`           | Number   | Seconds since the hub started       |
| `lastReport`       | DateTime | Time of the last hub status message |

### Tempest

| Channel                | Type                      | Description                              |
|------------------------|---------------------------|------------------------------------------|
| `epoch`                | DateTime                  | Time of the observation                  |
| `temperature`          | Number:Temperature        | Air temperature                          |
| `humidity`             | Number:Dimensionless      | Relative humidity (%)                    |
| `pressure`             | Number:Pressure           | Station pressure                         |
| `wind_lull`            | Number:Speed              | Minimum wind speed in the interval       |
| `wind_avg`             | Number:Speed              | Average wind speed                       |
| `wind_gust`            | Number:Speed              | Maximum wind speed in the interval       |
| `wind_direction`       | Number:Angle              | Wind direction                           |
| `illuminance`          | Number:Illuminance        | Illuminance                              |
| `uv`                   | Number:Dimensionless      | UV index                                 |
| `solar_radiation`      | Number:Intensity          | Solar radiation                          |
| `rain_accumulated`     | Number:Length             | Rain over the report interval            |
| `precipitation_type`   | String                    | `0` none, `1` rain, `2` hail             |
| `strike_count`         | Number                    | Lightning strikes in the interval        |
| `strike_distance`      | Number:Length             | Average lightning strike distance        |
| `battery_level`        | Number:ElectricPotential  | Battery voltage (advanced)               |
| `report_interval`      | Number:Time               | Observation report interval (advanced)   |
| `wind_sample_interval` | Number:Time               | Wind sample interval (advanced)          |
| `sensor_status`        | String                    | `OK`, or the sensor faults reported      |
| `rssi`                 | Number                    | Tempest signal strength at the hub (dBm) |
| `hub_rssi`             | Number                    | Hub signal strength at the Tempest (dBm) |
| `uptime`               | Number                    | Seconds since the Tempest started        |
| `firmware_version`     | String                    | Tempest firmware revision                |
| `lastReport`           | DateTime                  | Time of the last device status message   |

Readings the Tempest could not measure are set to UNDEF.

### Better Forecast

| Channel             | Type     | Description                                                    |
|---------------------|----------|----------------------------------------------------------------|
| `epoch`             | DateTime | Time of the current conditions in the forecast                 |
| `station_name`      | String   | Station name                                                   |
| `forecast_raw`      | String   | Forecast JSON as returned by WeatherFlow                       |
| `forecast_enriched` | String   | Forecast JSON with extra fields to make UI widgets easier      |

## Rule Triggers

The binding adds three rule triggers, available under "Add Trigger" in the rule editor.
Each one takes the Tempest Thing as its "Sensor Thing".

| Trigger                       | Fires when                          |
|-------------------------------|-------------------------------------|
| `RapidWindTrigger`            | A rapid wind reading arrives (every 3 seconds) |
| `LightningStrikeTrigger`      | A lightning strike is detected      |
| `PrecipitationStartedTrigger` | Rain starts                         |

The trigger's `event` output carries the event data.

## Thing Status

The hub and Tempest Things start as UNKNOWN and go ONLINE when their first status message arrives.
If no data arrives for 3 minutes they go OFFLINE with a "No data received" message.
The Tempest shows BRIDGE_OFFLINE while its hub is offline.

## Building

The binding builds on its own against the openHAB 5.2 add-on parent POM, with Java 21 and Maven 3.9 or later:

```shell
mvn spotless:apply
mvn clean install
```

Copy `target/org.openhab.binding.weatherflowsmartweather-5.2.0.jar` into the `addons` folder of your openHAB server.

## Credits

Original binding by William Welliver.
