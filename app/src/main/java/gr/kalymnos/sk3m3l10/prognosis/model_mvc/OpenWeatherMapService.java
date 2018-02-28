package gr.kalymnos.sk3m3l10.prognosis.model_mvc;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gr.kalymnos.sk3m3l10.prognosis.common.weather.Weather;

/**
 * The weather service from OpenWeatherMap.com
 */

public class OpenWeatherMapService implements WeatherService {
    @Override
    public Weather getCurrentWeather(String cityName) {
        return null;
    }

    @Override
    public Weather getCurrentWeather(Location location) {
        return null;
    }

    @Override
    public List<Weather> getWeatherForecast(String cityName) {
        return null;
    }

    @Override
    public List<Weather> getWeatherForecast(Location location) {
        return null;
    }

    /*
        This classs will be responsible for retrieving weather values,
        after parsing a json http response.
    */
    private static class JsonAssembler {

        private static final String CLASS_TAG = JsonAssembler.class.getSimpleName();

        // Json keys
        private static final String CITY_NAME = "name";
        private static final String CITY = "city";

        private static final int TYPE_CURRENT_WEATHER=0;
        private static final int TYPE_FORECAST=1;

        private int type;
        private JSONObject rootObj;

        private JsonAssembler(String json, int responseType) throws JSONException {
            this.rootObj = new JSONObject(json);
            this.type=responseType;
        }

        private String getCityName(){
            String city=null;


            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return rootObj.optString(CITY_NAME);
                case TYPE_FORECAST:
                    JSONObject cityObj = rootObj.optJSONObject(CITY);
                    return cityObj.optString(CITY_NAME);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": Unknown responseType.");
            }
        }



    }
}
