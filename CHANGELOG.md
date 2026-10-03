# Changelog

## 5.2.0

First release of this fork, for openHAB 5.2.

- Builds against the openHAB 5.2 add-on parent POM (Java 21).
- Discovery reports each hub and Tempest once instead of on every broadcast, and skips devices that already exist.
- New UDP listener: one socket on port 50222, a single reader thread, and a clean shutdown.
- The forecast uses openHAB's shared HTTP client with normal certificate checks, and reports a wrong station id or token on the Thing status.
- The hub and Tempest start as UNKNOWN, go ONLINE on their first status message, and go OFFLINE after 3 minutes without data.
- Disabling the Tempest no longer causes errors on the hub.
- Unmeasured readings are UNDEF instead of 0. Humidity is shown in %.
- Air, Sky and Air Quality sensors are no longer supported. Hub, Tempest and forecast Things, channels and rule triggers are unchanged.
- Code passes openHAB static code analysis. Routine messages log at debug or trace.
- Unit tests for the UDP message parser.
