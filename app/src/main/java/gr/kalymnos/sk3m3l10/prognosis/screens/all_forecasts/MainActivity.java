package gr.kalymnos.sk3m3l10.prognosis.screens.all_forecasts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.prognosis.R;
import gr.kalymnos.sk3m3l10.prognosis.common.weather.CityWeather;
import gr.kalymnos.sk3m3l10.prognosis.common.weather.LocationWeather;
import gr.kalymnos.sk3m3l10.prognosis.common.weather_units.OpenWeatherMapUnits;
import gr.kalymnos.sk3m3l10.prognosis.common.weather.Weather;
import gr.kalymnos.sk3m3l10.prognosis.model_mvc.FakeWeatherService;
import gr.kalymnos.sk3m3l10.prognosis.model_mvc.WeatherService;
import gr.kalymnos.sk3m3l10.prognosis.screens.detail.DetailActivity;
import gr.kalymnos.sk3m3l10.prognosis.view_mvc.WeatherViewMvc;

import static gr.kalymnos.sk3m3l10.prognosis.view_mvc.WeatherViewMvc.WeatherItemListener;

public class MainActivity extends AppCompatActivity implements WeatherItemListener{

    private WeatherService weatherService;
    List<Weather> weatherList;

    private WeatherViewMvc view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWeatherItemClicked(int itemPosition) {
        Weather w = this.weatherList.get(itemPosition);

        Bundle extras = new Bundle();
        extras.putInt(DetailActivity.IMAGE_KEY,w.getImage());
        extras.putString(DetailActivity.DATE_KEY,w.getDate());
        extras.putString(DetailActivity.DESCRIPTION_KEY,w.getDescription());
        extras.putString(DetailActivity.HIGH_TEMP_KEY,w.getTempHighWithSymbol());
        extras.putString(DetailActivity.LOW_TEMP_KEY,w.getTempLowWithSymbol());
        extras.putString(DetailActivity.HUMIDITY_KEY,w.getHumidity());
        extras.putString(DetailActivity.PRESSURE_KEY,w.getPressure());
        extras.putString(DetailActivity.WIND_KEY,w.getWindWithSymbol());

        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtras(extras);

        this.startActivity(intent);
    }
}
