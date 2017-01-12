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

package jp.gcreate.sample.samplejobqueue.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.api.GitHubService;
import jp.gcreate.sample.samplejobqueue.model.JobHistory;
import jp.gcreate.sample.samplejobqueue.model.OrmaDatabase;
import jp.gcreate.sample.samplejobqueue.model.Repository;
import retrofit2.Response;
import timber.log.Timber;

public class MyJob extends Job {
    public static final  String JOB_TAG         = "fetch_repositories_from_github";
    private static final int    PRIORITY        = 1;
    private static final int    MAX_RETRY_COUNT = 3;
    @Inject
    GitHubService gitHubService;
    @Inject
    JobManager    jobManager;
    @Inject
    OrmaDatabase  ormaDatabase;
    private String user;

    public MyJob(String user) {
        super(new Params(PRIORITY)
                      .requireUnmeteredNetwork()
                      .setDelayMs(TimeUnit.SECONDS.toMillis(60))
                      .groupBy(JOB_TAG)
                      .singleInstanceBy(JOB_TAG)
                      .addTags(JOB_TAG)
                      .persist());
        this.user = user;
    }

    @Override
    public void onAdded() {
        // Job has been saved to disk.
        // This is a good place to dispatch a UI event to indicate the job will eventually run.
        Timber.d("onAdded:");
    }

    @Override
    public void onRun() throws Throwable {
        // Job logic goes here.
        // All work done here should be synchronous, a job is removed from the queue once
        // onRun() finishes.
        Timber.d("onRun:");

        try {
            Response<List<Repository>> response = gitHubService.getRepositoriesList(user)
                                                               .execute();
            if (!response.isSuccessful()) {
                Timber.d("request was failed(code:%d). job was rescheduled", response.code());
            }

            List<Repository> repositories = response.body();
            Timber.d("repositories count=%d", repositories.size());
            for (Repository repository : repositories) {
                Timber.d(repository.toString());
            }
            ZonedDateTime checkedDate = ZonedDateTime.now();
            Timber.d("scheduled job was finished. %s", checkedDate);
            JobHistory previous = ormaDatabase.selectFromJobHistory()
                                              .orderByCheckedDateDesc()
                                              .getOrNull(0);
            Duration duration;
            if (previous == null) {
                duration = Duration.ZERO;
            } else {
                duration = Duration.between(previous.checkedDate, checkedDate);
            }
            ormaDatabase.insertIntoJobHistory(new JobHistory(checkedDate, duration, repositories.size()));

            // Add job itself to recur tasks.
            jobManager.addJobInBackground(new MyJob(user));
            Timber.d("re-scheduling job");
        } catch (IOException e) {
            Timber.e("got exceptions job was rescheduled:%s", e);
            e.printStackTrace();
            shouldReRunOnThrowable(e, getCurrentRunCount(), MAX_RETRY_COUNT);
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Timber.d("onCancel: reason=%d, throwable=%s", cancelReason, throwable);
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
                                                     int maxRunCount) {
        Timber.d("retry %d times", runCount);
        Timber.d("shouldReRunOnThrowable: throwable=%s, runCount=%d, max=%d", throwable, runCount,
                 maxRunCount);
        if (runCount > maxRunCount) {
            Timber.d("failed over max retry counts(%d), so job was canceled.", maxRunCount);
            return RetryConstraint.CANCEL;
        }
        return RetryConstraint.createExponentialBackoff(runCount, TimeUnit.SECONDS.toMillis(30));
    }
}
