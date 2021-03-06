package gr.kalymnos.sk3m3l10.prognosis.common.weather;

import gr.kalymnos.sk3m3l10.prognosis.common.weather_units.WeatherUnits;
import gr.kalymnos.sk3m3l10.prognosis.util.DateUtils;
import gr.kalymnos.sk3m3l10.prognosis.model_mvc.FakeWeatherImageProvider;

/**
 * A container which holds weather data fetched from a web service.
 */

public abstract class Weather {

    protected WeatherUnits weatherUnit;
    protected String mainWeather,description,tempHigh,tempLow,humidity,pressure,wind;
    protected DateUtils dateUtils;
    private long timeMilli;

    public Weather(long timeMilli, String mainWeather, String description,
                   int tempHigh, int tempLow, int humidity,
                   int pressure, double wind, WeatherUnits weatherUnit) {
        this.timeMilli = timeMilli;
        this.mainWeather = mainWeather;
        this.description = description;
        this.tempHigh = String.valueOf(tempHigh);
        this.tempLow = String.valueOf(tempLow);
        this.humidity = String.valueOf(humidity);
        this.pressure = String.valueOf(pressure);
        this.wind = String.format("%.1f",wind);
        this.weatherUnit=weatherUnit;
    }

    public final String getDate(){
        DateUtils dateUtils = new DateUtils(this.timeMilli);
        return dateUtils.getDate();
    }

    public final String getApproxHour(){
        return new DateUtils(this.timeMilli).getApproximateTime();
    }

    public final long getTimeMilli(){
        return this.timeMilli;
    }

    public final String getMainWeather() {
        return mainWeather;
    }

    public final String getDescription() {
        return description;
    }

    public final String getTempHigh() {
        return tempHigh+" "+this.weatherUnit.getTempUnit();
    }

    public final String getTempLow() {
        return tempLow+" "+this.weatherUnit.getTempUnit();
    }

    public final String getTempHighWithSymbol() {
        return tempHigh+this.weatherUnit.getTempUnitSymbol();
    }

    public final String getTempLowWithSymbol() {
        return tempLow+this.weatherUnit.getTempUnitSymbol();
    }

    public final String getHumidity() {
        return humidity+" "+this.weatherUnit.getHumidityUnit();
    }

    public final String getPressure() {
        return pressure+" "+this.weatherUnit.getPressureUnit();
    }

    public final String getWind() {
        return wind+" "+this.weatherUnit.getWindUnit();
    }

    public final String getWindWithSymbol() {
        return wind+" "+this.weatherUnit.getWindUnitSymbol();
    }

    public abstract int getImage();
}
