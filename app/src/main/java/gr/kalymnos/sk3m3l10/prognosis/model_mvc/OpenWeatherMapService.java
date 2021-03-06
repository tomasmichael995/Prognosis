package gr.kalymnos.sk3m3l10.prognosis.model_mvc;

import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.prognosis.common.weather.OpenWeather;
import gr.kalymnos.sk3m3l10.prognosis.common.weather.Weather;
import gr.kalymnos.sk3m3l10.prognosis.common.weather_units.OpenWeatherMapUnits;
import gr.kalymnos.sk3m3l10.prognosis.util.NetworkUtils;

/**
 * The weather service from OpenWeatherMap.com
 */

public class OpenWeatherMapService implements WeatherService {

    private static final String CLASS_TAG = OpenWeatherMapService.class.getSimpleName();

    @Override
    public Weather getCurrentWeather(String cityName){
        URL url = Utilities.buildUrlWithCityQuery(cityName,Utilities.CURRENT_WEATHER_URL);
        try {

            String httpResponse = NetworkUtils.getResponseFromHttpUrl(url);

            // we will try to assemble a Weather obj only if httpResponse is not empty
            if (!TextUtils.isEmpty(httpResponse)){
                JsonAssembler assembler = new JsonAssembler(httpResponse,JsonAssembler.TYPE_CURRENT_WEATHER);
                return this.assembleWeather(assembler);
            }

        } catch (IOException e) {
            Log.e(CLASS_TAG,e.getMessage());
        } catch (JSONException e) {
            Log.e(CLASS_TAG,e.getMessage());
        }
        return null;
    }

    @Override
    public Weather getCurrentWeather(Location location) {
        URL url = Utilities.buildUrlWithLocationQuery(location,Utilities.CURRENT_WEATHER_URL);
        try {

            String httpResponse = NetworkUtils.getResponseFromHttpUrl(url);

            // we will try to assemble a Weather obj only if httpResponse is not empty
            if (!TextUtils.isEmpty(httpResponse)){

                JsonAssembler assembler = new JsonAssembler(httpResponse,JsonAssembler.TYPE_CURRENT_WEATHER);

                // We query weather from device location, so set the flag before returning the object.
                OpenWeather locationWeather = (OpenWeather) this.assembleWeather(assembler);
                locationWeather.setObtainedFromDeviceLocation(true);
                return locationWeather;

            }


        } catch (IOException e) {
            Log.e(CLASS_TAG,e.getMessage());
        } catch (JSONException e) {
            Log.e(CLASS_TAG,e.getMessage());
        }
        return null;
    }

    @Override
    public List<Weather> getWeatherForecast(String cityName) {
        URL url = Utilities.buildUrlWithCityQuery(cityName,Utilities.FORECAST_URL);
        try {

            String httpResponse = NetworkUtils.getResponseFromHttpUrl(url);
            // we will try to assemble a Weather obj only if httpResponse is not empty
            if (!TextUtils.isEmpty(httpResponse)){
                JsonAssembler assembler = new JsonAssembler(httpResponse,JsonAssembler.TYPE_FORECAST);
                return this.assembleForecast(assembler);
            }

        } catch (IOException e) {
            Log.e(CLASS_TAG,e.getMessage());
        } catch (JSONException e) {
            Log.e(CLASS_TAG,e.getMessage());
        }
        return null;
    }

    @Override
    public List<Weather> getWeatherForecast(Location location) {
        URL url = Utilities.buildUrlWithLocationQuery(location,Utilities.FORECAST_URL);
        try {

            String httpResponse = NetworkUtils.getResponseFromHttpUrl(url);

            // we will try to assemble a Weather obj only if httpResponse is not empty
            if (!TextUtils.isEmpty(httpResponse)){
                JsonAssembler assembler = new JsonAssembler(httpResponse,JsonAssembler.TYPE_FORECAST);

                // We query forecast from device location, so set the flag before returning the objects.
                List<Weather> forecast = this.assembleForecast(assembler);
                for (Weather w : forecast){
                    OpenWeather openWeather = (OpenWeather) w;
                    openWeather.setObtainedFromDeviceLocation(true);
                }
                return forecast;
            }


        } catch (IOException e) {
            Log.e(CLASS_TAG,e.getMessage());
        } catch (JSONException e) {
            Log.e(CLASS_TAG,e.getMessage());
        }
        return null;
    }

    private List<Weather> assembleForecast(JsonAssembler assembler) {
        List<Weather> forecast = new ArrayList<>();
        for (int i=0; i<assembler.getForecastSize(); i++){
            forecast.add(this.assembleWeather(assembler,i));
        }
        return forecast;
    }

    private Weather assembleWeather(JsonAssembler assembler, int index){
        String city = assembler.getCityName();
        String country = assembler.getCountryCode();
        String icon = assembler.getIcon(index);
        long time = assembler.getTimeMilli(index);
        String mainWeather = assembler.getMainWeather(index);
        String description = assembler.getDescription(index);
        int tempMax = assembler.getMaxTemp(index);
        int tempMin = assembler.getMinTemp(index);
        int humidity = assembler.getHumidity(index);
        int pressure = assembler.getPressure(index);
        double windSpeed = assembler.getWind(index);
        return new OpenWeather(city,country,icon,time,mainWeather,description,tempMax,tempMin,humidity
                ,pressure,windSpeed, new OpenWeatherMapUnits.OpenWeatherMetric());
    }
    
    private Weather assembleWeather(JsonAssembler assembler){
        String city = assembler.getCityName();
        String country = assembler.getCountryCode();
        String icon = assembler.getIcon();
        long time = assembler.getTimeMilli();
        String mainWeather = assembler.getMainWeather();
        String description = assembler.getDescription();
        int tempMax = assembler.getMaxTemp();
        int tempMin = assembler.getMinTemp();
        int humidity = assembler.getHumidity();
        int pressure = assembler.getPressure();
        double windSpeed = assembler.getWind();
        return new OpenWeather(city,country,icon,time,mainWeather,description,tempMax,tempMin,humidity
                ,pressure,windSpeed, new OpenWeatherMapUnits.OpenWeatherMetric());
    }

    /*
        This classs will be responsible for retrieving weather values,
        after parsing a json http response.
    */
    private static class JsonAssembler {

        private static final String CLASS_TAG = JsonAssembler.class.getSimpleName();

        /*--------------------------JSON KEYS--------------------*/
        private static final String CITY_NAME = "name";
        private static final String DATE_TIME = "dt";
        private static final String CITY = "city";
        private static final String SYS = "sys";
        private static final String COUNTRY = "country";
        private static final String LIST = "list";
        private static final String MAIN = "main";
        private static final String TEMP_MIN = "temp_min";
        private static final String TEMP_MAX = "temp_max";
        private static final String WEATHER = "weather";
        private static final String DESCRIPTION = "description";
        private static final String HUMIDITY = "humidity";
        private static final String PRESSURE = "pressure";
        private static final String WIND = "wind";
        private static final String SPEED = "speed";
        private static final String ICON = "icon";
        /*-------------------------------------------------------*/

        // Json response types (from .../weather? or .../forecast?)
        private static final int TYPE_CURRENT_WEATHER=0;
        private static final int TYPE_FORECAST=1;

        private int type;
        private JSONObject rootObj;

        private JsonAssembler(String json, int responseType) throws JSONException {
            this.rootObj = new JSONObject(json);
            this.type=responseType;
        }

        private String getCityName(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return rootObj.optString(CITY_NAME);
                case TYPE_FORECAST:
                    JSONObject city = rootObj.optJSONObject(CITY);
                    return city.optString(CITY_NAME);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": Unknown responseType.");
            }
        }

        private String getCountryCode(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    JSONObject sys = this.rootObj.optJSONObject(SYS);
                    return sys.optString(COUNTRY);
                case TYPE_FORECAST:
                    JSONObject city = rootObj.optJSONObject(CITY);
                    return city.optString(COUNTRY);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": Unknown responseType.");
            }
        }

        private long getTimeMilli(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optLong(DATE_TIME);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        // This is used for forecasts where every Weather object has its own time-milli.
        private long getTimeMilli(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optLong(DATE_TIME);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "type must be TYPE_FORECAST for this call.");
            }
        }


        /*  
            The temperature in the api is a double, 
            though the optInt() is able to cast it to an integer.
        */
        private int getMaxTemp(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONObject(MAIN).optInt(TEMP_MAX);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private int getMaxTemp(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONObject(MAIN)
                            .optInt(TEMP_MAX);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private int getMinTemp(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONObject(MAIN).optInt(TEMP_MIN);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private int getMinTemp(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONObject(MAIN)
                            .optInt(TEMP_MIN);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private String getMainWeather(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONArray(WEATHER).optJSONObject(0)
                            .optString(MAIN);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private String getMainWeather(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONArray(WEATHER)
                            .optJSONObject(0).optString(MAIN);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private String getDescription(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONArray(WEATHER).optJSONObject(0).optString(DESCRIPTION);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private String getDescription(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONArray(WEATHER)
                            .optJSONObject(0).optString(DESCRIPTION);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private String getIcon(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONArray(WEATHER).optJSONObject(0).optString(ICON);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private String getIcon(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONArray(WEATHER)
                            .optJSONObject(0).optString(ICON);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private int getHumidity(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONObject(MAIN).optInt(HUMIDITY);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private int getHumidity(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONObject(MAIN)
                            .optInt(HUMIDITY);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private int getPressure(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONObject(MAIN).optInt(PRESSURE);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private int getPressure(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONObject(MAIN)
                            .optInt(PRESSURE);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private double getWind(){
            switch (this.type){
                case TYPE_CURRENT_WEATHER:
                    return this.rootObj.optJSONObject(WIND).optDouble(SPEED);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_CURRENT_WEATHER for this call.");
            }
        }

        private double getWind(int index){
            switch (this.type){
                case TYPE_FORECAST:
                    JSONArray list = this.rootObj.optJSONArray(LIST);
                    return list.optJSONObject(index).optJSONObject(WIND)
                            .optDouble(SPEED);
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

        private int getForecastSize(){
            switch (this.type){
                case TYPE_FORECAST:
                    return this.rootObj.optJSONArray(LIST).length();
                default:
                    throw new IllegalArgumentException(CLASS_TAG+": responseType " +
                            "must be TYPE_FORECAST for this call.");
            }
        }

    }

    /**
     * Network utilities for OpenWeatherMap.com
     */

    private static class Utilities {

        // OpenWeatherMap.org parameters
        private static final String QUERY_PARAM = "q";
        private static final String FORMAT_PARAM = "mode";
        private static final String UNITS_PARAM = "units";
        private static final String API_KEY_PARAM = "appid";
        private static final String LAT_PARAM = "lat";
        private static final String LON_PARAM = "lon";

        // OpenWeatherMap.org parameter values
        private static final String FORMAT_JSON = "json";
        private static final String UNITS_METRIC = "metric";
        private static final String API_KEY = "b2b88ba02f81b823def0b5ca5dd0471f";


        // Urls
        private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
        private static final String CURRENT_WEATHER_URL = BASE_URL+"weather";
        private static final String FORECAST_URL = BASE_URL+"forecast";

        private static final String CLASS_TAG = Utilities.class.getSimpleName();

        private Utilities(){}

        private static URL buildUrlWithCityQuery(String cityQuery, String apiUrl) {
            Uri weatherQueryUri = Uri.parse(apiUrl).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, cityQuery)
                    .appendQueryParameter(FORMAT_PARAM, FORMAT_JSON)
                    .appendQueryParameter(UNITS_PARAM, UNITS_METRIC)
                    .appendQueryParameter(API_KEY_PARAM,API_KEY)
                    .build();
            try {
                URL weatherQueryUrl = new URL(weatherQueryUri.toString());
                Log.d(CLASS_TAG, "Current weather URL: " + weatherQueryUrl);
                return weatherQueryUrl;
            } catch (MalformedURLException e) {
                Log.e(CLASS_TAG, e.getMessage());
                return null;
            }
        }

        private static URL buildUrlWithLocationQuery(Location location, String apiUrl) {
            Uri weatherQueryUri = Uri.parse(apiUrl).buildUpon()
                    .appendQueryParameter(LAT_PARAM, String.valueOf(location.getLatitude()))
                    .appendQueryParameter(LON_PARAM, String.valueOf(location.getLongitude()))
                    .appendQueryParameter(FORMAT_PARAM, FORMAT_JSON)
                    .appendQueryParameter(UNITS_PARAM, UNITS_METRIC)
                    .appendQueryParameter(API_KEY_PARAM,API_KEY)
                    .build();
            try {
                URL weatherQueryUrl = new URL(weatherQueryUri.toString());
                Log.d(CLASS_TAG, "Location URL: " + weatherQueryUrl);
                return weatherQueryUrl;
            } catch (MalformedURLException e) {
                Log.e(CLASS_TAG, e.getMessage());
                return null;
            }
        }
    }
}
