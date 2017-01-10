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

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.github.gfx.android.orma.Relation;
import com.github.gfx.android.orma.widget.OrmaRecyclerViewAdapter;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.R;
import jp.gcreate.sample.samplejobqueue.api.GitHubService;
import jp.gcreate.sample.samplejobqueue.databinding.ActivityMainBinding;
import jp.gcreate.sample.samplejobqueue.databinding.ItemRecyclerViewBinding;
import jp.gcreate.sample.samplejobqueue.jobs.MyJob;
import jp.gcreate.sample.samplejobqueue.model.JobHistory;
import jp.gcreate.sample.samplejobqueue.model.OrmaDatabase;
import timber.log.Timber;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    @Inject
    OrmaDatabase ormaDatabase;
    @Inject
    GitHubService gitHubService;
    @Inject
    JobManager jobManager;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        getApplicationComponent().inject(this);

        adapter = new Adapter(this, ormaDatabase.relationOfJobHistory().orderByCheckedDateDesc());
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void registerJob() {
        Timber.d("register job was clicked.");
        jobManager.addJobInBackground(new MyJob("gen0083"));
    }

    public void cancelJob() {
        Timber.d("cancel job was clicked.");
        jobManager.cancelJobsInBackground(null, TagConstraint.ANY, MyJob.JOB_TAG);
    }

    public void clearHistory() {
        Timber.d("clear history");
        ormaDatabase.deleteFromJobHistory().executeAsSingle().subscribe();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemRecyclerViewBinding binding;

        public VH(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ItemRecyclerViewBinding getBinding() {
            return binding;
        }
    }

    static class Adapter extends OrmaRecyclerViewAdapter<JobHistory, VH> {

        public Adapter(@NonNull Context context,
                       @NonNull Relation<JobHistory, ?> relation) {
            super(context, relation);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_recycler_view, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            ItemRecyclerViewBinding binding = holder.getBinding();
            JobHistory history = getItem(position);
            binding.setHistory(history);
        }
    }
}
