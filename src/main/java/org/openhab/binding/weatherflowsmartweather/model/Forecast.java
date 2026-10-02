package org.openhab.binding.weatherflowsmartweather.model;

import java.util.Arrays;

import org.openhab.binding.weatherflowsmartweather.util.ForecastUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forecast {
    private static final Logger logger = LoggerFactory.getLogger(Forecast.class);

    HourlyForecast[] hourly;
    DailyForecast[] daily;

    public HourlyForecast[] getHourly() {
        return hourly;
    }

    public void setHourly(HourlyForecast[] hourly) {
        this.hourly = hourly;
    }

    public DailyForecast[] getDaily() {
        return daily;
    }

    public void setDaily(DailyForecast[] daily) {
        this.daily = daily;
    }

    public void enrichForecast() {
        enrichForecast(0, 0);
    }

    public void enrichForecast(int keepHourly, int keepDaily) {
        int dayElem = -1;

        if (keepHourly > 0) {
            if (this.hourly.length > keepHourly) {
                this.hourly = Arrays.copyOf(this.hourly, keepHourly);
            }
        }

        if (keepDaily > 0) {
            if (this.daily.length > keepDaily) {
                this.daily = Arrays.copyOf(this.daily, keepDaily);
            }
        }

        logger.debug("Enriching {} hourly forecasts", hourly.length);
        logger.debug("Enriching {} daily forecasts", daily.length);
        for (HourlyForecast hour : hourly) {
            DailyForecast myDay;
            for (int i = 0; i < daily.length; i++) {
                if (hour.local_day == daily[i].day_num) {
                    logger.debug("Hourly for day {} corresponds with daily element {}", hour.local_day, i);
                    dayElem = i;
                }
            }
            if (dayElem == -1) {
                logger.debug("Hourly {} not matching Daily {}", hour, daily[0]);
                throw (new RuntimeException("Could not find daily forecast corresponding to hour for day:"
                        + hour.local_day + ", hour:" + hour.local_hour));
            }

            myDay = daily[dayElem];

            if (hour.time >= myDay.sunrise && hour.time < myDay.sunset) {
                // we are after sunrise today but before sunset today
                hour.setSun_risen_at_start(true);
                hour.setSun_set_at_start(false);
                hour.setNext_sunset(myDay.getSunset());
                if (dayElem + 1 < daily.length)
                    hour.setNext_sunrise(daily[dayElem + 1].getSunrise());
            } else if (hour.time < myDay.sunrise) {
                // we are before sunrise today
                hour.setSun_risen_at_start(false);
                hour.setSun_set_at_start(true);
                hour.setNext_sunrise(myDay.getSunrise());
                hour.setNext_sunset(myDay.getSunset());
            } else if (hour.time >= myDay.sunset) {
                // we are after sunset today
                hour.setSun_risen_at_start(false);
                hour.setSun_set_at_start(true);
                if (dayElem + 1 < daily.length) {
                    hour.setNext_sunrise(daily[dayElem + 1].getSunrise());
                    hour.setNext_sunset(daily[dayElem + 1].getSunset());
                }
            }

            long hourEnd = hour.getTime() + 3600;

            if (hour.getTime() < myDay.getSunrise() && hourEnd >= myDay.getSunrise())
                hour.setSun_rises_this_hour(true);
            if (hour.getTime() < myDay.getSunset() && hourEnd >= myDay.getSunset())
                hour.setSun_sets_this_hour(true);
            hour.setWi_icon(ForecastUtils.mapWeatherIcon(hour.getIcon()));
        }

        for (DailyForecast day : getDaily()) {
            day.setWi_icon(ForecastUtils.mapWeatherIcon(day.getIcon()));
        }
    }
}
