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

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.CustomApp;
import jp.gcreate.sample.samplejobqueue.api.GitHubService;
import jp.gcreate.sample.samplejobqueue.model.JobHistory;
import jp.gcreate.sample.samplejobqueue.model.OrmaDatabase;
import jp.gcreate.sample.samplejobqueue.model.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MyJobService extends JobService {
    private static final int JOB_ID = 1;

    @Inject
    GitHubService gitHubService;
    @Inject
    OrmaDatabase ormaDatabase;

    public static void scheduleJobs(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, MyJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
               .setPeriodic(TimeUnit.SECONDS.toMillis(60))
               .setPersisted(true);
        scheduler.schedule(builder.build());
    }

    public static void cancelJobs(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(JOB_ID);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Timber.d("onStartJob: params=%s", params);
        gitHubService.getRepositoriesList("gen0083")
                .enqueue(new Callback<List<Repository>>() {
                    @Override
                    public void onResponse(Call<List<Repository>> call,
                                           Response<List<Repository>> response) {
                        if (response.isSuccessful()) {
                            List<Repository> result = response.body();
                            Timber.d("repositories count=%d", result.size());
                            for (Repository repository : result) {
                                Timber.d(repository.toString());
                            }
                            ZonedDateTime now = ZonedDateTime.now();
                            JobHistory previous = ormaDatabase.selectFromJobHistory()
                                                              .orderByCheckedDateDesc()
                                                              .getOrNull(0);
                            Duration duration;
                            if (previous == null) {
                                duration = Duration.ZERO;
                            } else {
                                duration = Duration.between(previous.checkedDate, now);
                            }
                            ormaDatabase.insertIntoJobHistory(new JobHistory(now, duration, result.size()));

                            jobFinishedWithLog(params);
                        } else {
                            Timber.d("request was not successful: code=%d", response.code());
                            jobFinishedWithLog(params);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Repository>> call, Throwable t) {
                        Timber.e("request failed: %s", t);
                        t.printStackTrace();
                        jobFinishedWithLog(params);
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Timber.d("onStopJob: params=%s", params);
        jobFinishedWithLog(params);
        return false;
    }

    private void jobFinishedWithLog(JobParameters params) {
        Timber.d("Scheduled job was finished. %s", new Date());
        jobFinished(params, false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.v("onCreate");
        ((CustomApp) getApplicationContext())
                .getApplicationComponent()
                .inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.v("onDestroy");
    }
}
