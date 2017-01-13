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

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jp.gcreate.sample.samplejobqueue.api.GitHubService;
import jp.gcreate.sample.samplejobqueue.model.JobHistory;
import jp.gcreate.sample.samplejobqueue.model.OrmaDatabase;
import jp.gcreate.sample.samplejobqueue.model.Repository;
import retrofit2.Response;
import timber.log.Timber;

public class MyJob extends Job {
    public static final String TAG = "MyJob";

    private GitHubService gitHubService;
    private OrmaDatabase  ormaDatabase;

    public MyJob(GitHubService gitHubService, OrmaDatabase ormaDatabase) {
        this.gitHubService = gitHubService;
        this.ormaDatabase = ormaDatabase;
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        try {
            Response<List<Repository>> response = gitHubService.getRepositoriesList("gen0083")
                                                               .execute();
            if (!response.isSuccessful()) {
                Timber.d("request was failed(code:%d). job was rescheduled", response.code());
                return Result.RESCHEDULE;
            }

            List<Repository> repositories = response.body();
            Timber.d("repositories count=%d", repositories.size());
            for (Repository repository : repositories) {
                Timber.d(repository.toString());
            }
            ZonedDateTime now = ZonedDateTime.now();
            Timber.d("scheduled job was finished. %s", now);
            JobHistory previous = ormaDatabase.selectFromJobHistory()
                                              .orderByCheckedDateDesc()
                                              .getOrNull(0);
            Duration duration = (previous == null) ? Duration.ZERO
                                : Duration.between(previous.checkedDate, now);
            ormaDatabase.insertIntoJobHistory(new JobHistory(now, duration, repositories.size()));
            return Result.SUCCESS;
        } catch (IOException e) {
            Timber.e("got exceptions job was rescheduled:%s", e);
            e.printStackTrace();
            return Result.RESCHEDULE;
        }
    }

    public static void scheduleJobs() {
        new JobRequest.Builder(TAG)
                .setPersisted(true)
                // flexも指定したほうが、指定したintervalで起動しやすくなる。
                // flexを省略した場合、intervalと同じ数値が設定され、0~intervalの間隔でジョブが実行される
                // 例えば15分だけを指定した場合、ジョブの実行間隔が2分→28分→2分...となった
                // これはintervalの間ではジョブが1回までしか起動しないという制約に引っかかるために起こる現象と思われる
                // interval15, flex5の場合、ジョブの実行間隔は10分から15分の間で安定していた
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    public static void cancelJobs() {
        JobManager      manager  = JobManager.instance();
        Set<JobRequest> requests = manager.getAllJobRequestsForTag(TAG);
        Timber.d("JobRequest size=%d", requests.size());
        for (JobRequest request : requests) {
            manager.cancel(request.getJobId());
            Timber.d("JobRequest(%s) was canceled.", request);
        }
    }
}
