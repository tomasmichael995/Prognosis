package gr.kalymnos.sk3m3l10.prognosis.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import gr.kalymnos.sk3m3l10.prognosis.util.NotificationUtils;

/**
 *  WeatherReminderJobService extends JobService because
 *  this is what Firebases Job-dispatcher requires.
 *
 *  Also JobService runs from the main thread by default
 *  so we will implement make our calculations in a seperate
 *  thread instead.
 */

public class WeatherReminderJobService extends JobService {

    private AsyncTask backgroundTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.backgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = WeatherReminderJobService.this;
                NotificationUtils.showWeatherNotification(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                /* Signal here that the job is finished
                   (because the job is finised only when AsyncTask is finish).
                   */
                jobFinished(params,false);
            }
        };

        this.backgroundTask.execute();
        return true; /* Signals that our service is still doing some work (because of the thread)*/
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
