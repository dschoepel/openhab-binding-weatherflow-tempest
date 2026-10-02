These files provide a set of widgets for use in HABPanel.

__WeatherCard__

The Weather Card widget displays a set of weather conditions and forecast data provided by the WeatherFlow "Better Forecast" service. It's based on the Weather Card widget by Rainer Groll [1].

[1] https://community.openhab.org/t/ui-widget-weather/106842

To install:

- Create and configure a "Better Forecast" thing.
- Create a new item for the forecast and link the Enriched forecast data channel for the Better Forecast thing to the new item. It should be filled with forecast data in JSON format.
- create an empty file called dummy.js in your $OPENHAB/conf/html directory.
- In the OpenHAB web ui, go to Developer Tools -> Widgets.
- Click on the "+" button and paste the contents of the weathercard.yaml file into the text box. 
- Make sure that you have not added any spaces or empty lines and that the content has not been reformatted.
- Click Save.
- Add the widget to a page and configure the widget by clicking on "Configure Cell" from the settings icon for
  the cell containing the widget. 
- Specify the "Better forecast" item you wish to use as the source.

__Rapid Wind__

The Rapid Wind widget provides a simple display of wind speed and direction, as provided by Tempest sensors. This data is different from the regular 1-minute observations.

_Note:_ currently this widget is very basic and only a single sensor is supported at a time. Placing more than one widget on a panel will result in all but the last widget being disabled.

To install:

- copy rapidWind.js to your $OPENHAB/conf/html directory.
- In HABPanel, go to a panel and click the pencil icon next to the panel name.
- Click on "Add Widgets" and then click the gear icon next to "Custom Widgets"
- Click on Import Widget -> Import From File...
- Navigate to rapid-wind-widget.json and select the file.

To use:

- Note the ThingUID for the Tempest thing. You can find this PaperUI by going to Config -> Things and finding the Tempest thing in the list. It will look something like "weatherflowsmartweather:tempest:HB-00000000:ST-000000000" 
- In HABPanel, go to a panel and click the pencil icon next to the panel name.
- Click on the "Add Widget..." button and select the "Rapid Wind Thing" widget. This will add the widget to the panel.
- Click on the 3 dots icon in the upper right hand portion of the widget. Choose "Edit..." from the popup menu.
- Give the widget a name, and enter the ThingUID for the sensor from the first step into the "Tempest Thing UID" setting box.
- Click Save. After a few seconds, you should start to see data being updated.
