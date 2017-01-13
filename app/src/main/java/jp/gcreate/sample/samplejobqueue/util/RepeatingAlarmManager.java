/*
 * Copyright 2017 gen0083
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.gcreate.sample.samplejobqueue.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.service.BootReceiver;
import timber.log.Timber;

public class RepeatingAlarmManager {
    private       AlarmManager  alarmManager;
    private       Context       appContext;
    private final PendingIntent pendingIntent;

    @Inject
    public RepeatingAlarmManager(Context context) {
        this.appContext = context.getApplicationContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(appContext, BootReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void scheduleJob() {
        setEnabledBroadcastReceiver(true);
        Timber.d("enabled BootReceiver");

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                  System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60),
                                  TimeUnit.SECONDS.toMillis(60),
                                  pendingIntent);
        Timber.d("set repeating alarm");
    }

    public void cancelJob() {
        setEnabledBroadcastReceiver(false);
        Timber.d("disabled BootReceiver");

        alarmManager.cancel(pendingIntent);
        Timber.d("cancel repeating alarm");
    }

    private void setEnabledBroadcastReceiver(boolean isEnabled) {
        ComponentName  receiver = new ComponentName(appContext, BootReceiver.class);
        PackageManager pm       = appContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                                      isEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                      PackageManager.DONT_KILL_APP);
    }
}
