// An angularjs module for widgets that display WeatherFlow SmartWeather "rapid-wind" events
//
// TODO: Support multiple Tempest devices within the service and multiple listeners.
//       Do we need to unregister listeners?
var myModule = angular.module("rapidWind", []);

myModule.service("RapidWindService", ["$rootScope",  "OHService", function($rootScope,  OHService){
    this.$rootScope = $rootScope;
    this.OHService = OHService;
    var onChange = 0;
    var thingUid = 0;
    var liveUpdatesEnabled = false;

    // this is the type of event, used to filter things we don't care about.
    var eventType = "RapidWindEvent";

    // this is the topic that rapid wind events are published on.
    var targetTopic = "openhab/things/{uid}/rapidwind";

    this.setOnChange = function(uid, f) { thingUid = uid; targetTopic = targetTopic.replace("{uid}", uid); onChange = f; };

    this.messageReceived = function(event, oh2event) {
        if(event.type != eventType) return;
        console.log("RapidWindEvent " + oh2event);
        if(event.topic === targetTopic && onChange != 0) {
            onChange(oh2event);
        }
    };

    this.registerEventSource = function(rapidWindService) {
        if (typeof(EventSource) !== "undefined") {
            var source = new EventSource('/rest/events?topics=openhab/things/*/rapidwind');
            liveUpdatesEnabled = true;
            source.onmessage = function (event) {
                try {
                    var evtdata = JSON.parse(event.data);
                    var topicparts = evtdata.topic.split('/');

                    if(evtdata.type === 'RapidWindEvent') {
                        var payload = JSON.parse(evtdata.payload);
                        evtdata.payload = payload
                        rapidWindService.messageReceived(evtdata, payload)
                    }
                } catch (e) {
                    console.warn('SSE event issue: ' + e.message);
                }
            }
            source.onerror = function (event) {
                console.error('SSE error, closing EventSource');
                liveUpdatesEnabled = false;
                this.close();
                $timeout(loadItems, 5000);
            }

        }
    };

    this.registerEventSource(this);
    console.log("RapidWindService started.");
}]);

// Controller that listens to Rapid Wind events for a given thing and updates its model for use by templates.
myModule.controller("RapidWindCtrl", ["$scope", "RapidWindService", function($scope, RapidWindService) {
    RapidWindService.setOnChange( $scope.config.sensorUid, function(payload) {
        //$scope.skyName = RapidWindService.OHService.getThingByUid($scope.config.skyUid).label;
        $scope.epoch = payload.epoch;
        $scope.windSpeed = payload.windSpeed;
        $scope.windDirection = payload.windDirection;
    });
} ]);