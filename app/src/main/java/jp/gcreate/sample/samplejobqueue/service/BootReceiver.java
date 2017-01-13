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

package jp.gcreate.sample.samplejobqueue.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jp.gcreate.sample.samplejobqueue.util.RepeatingAlarmManager;
import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive: Context:%s, Intent:%s", context, intent);
        if (intent.getAction() == null) {
            Timber.d("start MyJobService");
            context.startService(new Intent(context, MyJobService.class));
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Timber.d("device booting. set alarm to fire repeating job.");
            RepeatingAlarmManager manager = new RepeatingAlarmManager(context);
            manager.scheduleJob();
        }
    }
}
