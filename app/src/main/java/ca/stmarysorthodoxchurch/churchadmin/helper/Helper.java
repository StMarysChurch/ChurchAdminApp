package ca.stmarysorthodoxchurch.churchadmin.helper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import java.util.concurrent.TimeUnit;

import ca.stmarysorthodoxchurch.churchadmin.Service.UpdateService;

/**
 * Created by roneythomas on 2018-03-08.
 */

public class Helper {
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, UpdateService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setPeriodic(TimeUnit.DAYS.toMillis(1));
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true);
        builder.setRequiresCharging(true);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
