package gr.kalymnos.sk3m3l10.prognosis.util;

import android.content.Context;
import android.content.SharedPreferences;

import gr.kalymnos.sk3m3l10.prognosis.R;

/**
 * Utilities for app settings.
 */

public class SettingsUtils {
    private SharedPreferences settingPreferences = null;
    private Context context;

    public SettingsUtils(Context context, SharedPreferences settingPreferences){
        this.context=context;
        this.settingPreferences=settingPreferences;
    }

    public String getCityName(){
        String locationKey = this.context.getString(R.string.pref_location_key);
        String defaultValue = this.context.getString(R.string.pref_location_default);
        return this.settingPreferences.getString(locationKey,defaultValue);
    }

    public boolean isDeviceLocationEnabled(){
        String deviceLocationSettingKey = this.context.getString(R.string.pref_enable_gps_search_key);
        boolean defaultLocationSettingValue = this.context.getResources().getBoolean(R.bool.gps_search_by_default);
        return this.settingPreferences.getBoolean(deviceLocationSettingKey,defaultLocationSettingValue);
    }

    public String getCityPrefKey(){
        return this.context.getString(R.string.pref_location_key);
    }

    public String getCityDefaultValue(){
        return this.context.getString(R.string.pref_location_default);
    }

    public String getDeviceLocationPrefKey(){
        return this.context.getString(R.string.pref_enable_gps_search_key);
    }

    public boolean getDeviceLocationDefaultValue(){
        return this.context.getResources().getBoolean(R.bool.gps_search_by_default);
    }

    public String getNotificationEnabledPrefKey(){
        return this.context.getString(R.string.pref_notifications_enabled_key);
    }

    public boolean getNotificationEnabledDefaultValue(){
        return this.context.getResources().getBoolean(R.bool.weather_notifications_by_default);
    }

    public String getNotificationTimePrefKey(){
        return this.context.getString(R.string.pref_notification_time_key);
    }

    public String getNotificationTimeDefaultEntry(){
        return this.context.getString(R.string.pref_notification_time_entry_three);
    }

}
