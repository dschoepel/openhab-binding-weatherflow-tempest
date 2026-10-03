__Rules__

This binding provides a number of triggers that can be used with the New Rules Engine (NRE) in OpenHAB 2.5. The rules for the NRE can be managed in PaperUI under the "Rules" tab. If you don't have the NRE addon installed, you can do so through the PaperUI "Addons" tab.

Triggers cause a rule to be executed, and with the triggers provided with the binding, you can cause rules to run when a lightning strike is detected, precipitation starts, or when a rapid wind event is received. These triggers are in addition to those you can use to act when standard weather measurements change.


__Example__

Let's create a rule that sends a cloud notification when things are windy. Note that you'll need to have OpenHAB cloud access set up, and a phone configured to receive notifications. Instructions for setting this up can be found from the OpenHAB community website. Also note that there is no provision for throttling messages, so if the wind speed is repeatedly over our threshold, you'll get multiple messages.

1. Click on the Rules tab in PaperUI.
2. Click on the "+" icon and select "New Rule..." to create a new rule.
3. Click on the "+" icon next to "When..." and select "when a rapid wind event occurs" from the dropdown.
4. Click "Next" and choose the Smartweather Tempest you want to trigger from. Click "OK".
5. Click on the "+" icon next to "then..." and choose "execute a given script" from the dropdown. Click "Next".
6. Choose "ECMAScript" from the script type dropdown.
7. In the Script text box, enter the script below and click "OK".
8. Click on the "checkmark" icon to save and enable the script. 
9. Monitor the output of your openhab log file to see that the script is executing properly. You should get a mobile notification when the wind speed climbs above the threshold specified in the script.
10. You can disable the rule by clicking on the "Alarm clock" icon next to the rule, or by deleting it.

```javascript
// send a mobile notification when the reported wind speed 
// is above a desired threshold.
//
// the smartweather triggers provide 2 arguments that we can
// use in our scripts: 
//   event, which is an instance of org.eclipse.smarthome.core.events.Event
//   eventData, which is an instance of RapidWindData, PrecipitationStartedData or LightningStrikeData, all located 
//   in org.openhab.binding.weatherflowsmartweather.model. 
// 
// you can examine these classes to see what data is available for use.
//
// in our case, we'll use the windSpeed data from the RapidWindData object held in eventData, which is made available 
// to us by the Rapid Wind trigger. Note that all of the values provided by the SmartWeather binding have units of 
// measurement associated with them, so we'll need to make sure that we work with values in our preferred measurement 
// system, which might vary from the system's.
var log	= Java.type("org.slf4j.LoggerFactory").getLogger("org.eclipse.smarthome.model.script.Rules");

// an easier to use handle for the notification system
var NotificationAction = org.openhab.io.openhabcloud.NotificationAction;

// our "it's windy" threshold speed, in MPH
var threshold = 7.0;

log.warn( "RapidWind Rule:  event=" + (event.type) );

// let's get the 
var windSpeed = eventData.windSpeed;
log.warn( "RapidWind Rule: windSpeed=" + (windSpeed) );

// this example uses english customary units; you could instead use SI units.
var MPH = Java.type("org.eclipse.smarthome.core.library.unit.ImperialUnits").MILES_PER_HOUR;

// let's convert the native windspeed to miles per hour
var windSpeed2 = windSpeed.toUnit(MPH);

log.warn( "RapidWind Rule:  windSpeed=" + (windSpeed2) );

if(windSpeed2.floatValue() > threshold) 
  NotificationAction.sendBroadcastNotification(, "whoa, it's pretty windy here: " + windSpeed2);
```

Another example that updates an item with the new wind speed value of the rapid wind event

```
var log	= Java.type("org.slf4j.LoggerFactory").getLogger("org.openhab.model.script.Rules");

log.warn( "RapidWind Rule:  event=" + (event.type) );

// let's get the 
var windSpeed = eventData.windSpeed;
log.warn( "RapidWind Rule: windSpeed=" + (windSpeed) );

// this example uses english customary units; you could instead use SI units.
var MPH = Java.type("org.openhab.core.library.unit.ImperialUnits").MILES_PER_HOUR;

// let's convert the native windspeed to miles per hour
var windSpeed2 = windSpeed.toUnit(MPH);

log.warn( "RapidWind Rule2:  windSpeed=" + (windSpeed2) );

// We assume that the item we should update is called "MyRapidWindSpeedItem"
events.postUpdate("MyRapidWindSpeedItem", windSpeed2);
```
