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


import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.api.GitHubService;
import jp.gcreate.sample.samplejobqueue.model.OrmaDatabase;

public class MyJobCreator implements JobCreator {
    private GitHubService gitHubService;
    private OrmaDatabase  ormaDatabase;

    @Inject
    public MyJobCreator(GitHubService gitHubService, OrmaDatabase ormaDatabase) {
        this.gitHubService = gitHubService;
        this.ormaDatabase = ormaDatabase;
    }

    @Override
    public Job create(String tag) {
        switch (tag) {
            case MyJob.TAG:
                return new MyJob(gitHubService, ormaDatabase);
            default:
                return null;
        }
    }
}
