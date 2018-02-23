package gr.kalymnos.sk3m3l10.prognosis.model_mvc;

import android.text.style.TtsSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.kalymnos.sk3m3l10.prognosis.common.weather.CityWeather;
import gr.kalymnos.sk3m3l10.prognosis.common.weather.Weather;
import gr.kalymnos.sk3m3l10.prognosis.common.weather_units.OpenWeatherMapUnits;

/**
 * A mock weather service
 */

/*
* new CityWeather("Kalpaca","GR",1519138800,
                    "rain","Great day, no clouds",
                    15,8,86,55,
                    56.4,new OpenWeatherMapUnits.OpenWeatherMetric())
*
* */

public class FakeWeatherService implements WeatherService {

    private static final int WEATHER_ITEMS_MAX_SIZE= 100;
    private static final int TIME_MILLI = 1519387200;

    String[] weatherValues = {"clear sky","few clouds","scattered clouds","broken clouds","shower rain","thunderstorm","snow","mist"};
    String[] descriptions = {"Great day, seize it","Some clouds in the sky","Clouds scattered along the sky","Broken clouds","Raining heavilly","Raining cats and dogs","Snowing","Misty mountains"};

    @Override
    public List<Weather> getWeatherForecast(String cityName) {
        List<Weather> list = new ArrayList<>();
        int itemSize = this.returnNumber(WEATHER_ITEMS_MAX_SIZE);

        Random r = new Random();

        for (int i=0; i<itemSize; i++){
            // This will point to a random weather and description value
            int index = r.nextInt(this.weatherValues.length);
            Weather weather = new CityWeather(cityName,null,TIME_MILLI,weatherValues[index],
                    descriptions[index],getRandomTemp(),getRandomTemp(),getRandomHumidity(),
                    getRandomPressure(),getRandomWind(),new OpenWeatherMapUnits.OpenWeatherMetric());
            list.add(weather);
        }
        return list;
    }

    private int returnNumber(int limit){
        return new Random().nextInt(limit)+1;
    }

    @Override
    public List<Weather> getWeatherForecast(String cityName, String countryCode) {
        return null;
    }

    @Override
    public List<Weather> getWeatherForecast(double lat, double lon) {
        return null;
    }

    private int getRandomTemp(){
        // Returns a temperature from -50 to 50
        Random r = new Random();
        return r.nextInt(101)-50;
    }

    private int getRandomHumidity(){
        // Returns random humidity 0-100 (%)
        Random r = new Random();
        return r.nextInt(101);
    }

    private int getRandomPressure(){
        Random r = new Random();
        return r.nextInt(600)+500;
    }

    private double getRandomWind(){
        Random r = new Random();
        int integerPart = r.nextInt(10);
        int fractionalPart = r.nextInt(10);
        return new Double(String.format("%d.%d",integerPart,fractionalPart));
    }

}