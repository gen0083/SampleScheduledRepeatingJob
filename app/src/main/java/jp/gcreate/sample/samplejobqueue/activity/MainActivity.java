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

package jp.gcreate.sample.samplejobqueue.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;

import java.util.List;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.R;
import jp.gcreate.sample.samplejobqueue.api.GitHubService;
import jp.gcreate.sample.samplejobqueue.databinding.ActivityMainBinding;
import jp.gcreate.sample.samplejobqueue.jobs.MyJob;
import jp.gcreate.sample.samplejobqueue.model.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    @Inject
    GitHubService gitHubService;
    @Inject
    JobManager jobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        getApplicationComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gitHubService.getRepositoriesList("gen0083")
                     .enqueue(new Callback<List<Repository>>() {
                         @Override
                         public void onResponse(Call<List<Repository>> call,
                                                Response<List<Repository>> response) {
                             if (response.isSuccessful()) {
                                 List<Repository> body = response.body();
                                 StringBuilder    sb   = new StringBuilder();
                                 Repository       one  = body.get(0);
                                 sb.append("repositories count:" + body.size() + "\n");
                                 sb.append("owner: " + one.getOwner() + "\n");
                                 sb.append("repository: " + one + "\n");
                                 binding.text.setText(sb.toString());
                             } else {
                                 Timber.d("onResponse failed:[%d]%s", response.code(),
                                          response.message());
                             }

                         }

                         @Override
                         public void onFailure(Call<List<Repository>> call, Throwable t) {
                             Timber.e("onFailure: %s", t.getMessage());
                             t.printStackTrace();
                         }
                     });
    }

    public void registerJob() {
        Timber.d("register job was clicked.");
        jobManager.addJobInBackground(new MyJob("gen0083"));
    }

    public void cancelJob() {
        Timber.d("cancel job was clicked.");
        jobManager.cancelJobsInBackground(null, TagConstraint.ANY, MyJob.JOB_TAG);
    }
}
