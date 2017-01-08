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

package jp.gcreate.sample.samplejobqueue;

import android.app.Application;

import com.evernote.android.job.JobManager;

import javax.inject.Inject;

import jp.gcreate.sample.samplejobqueue.di.ApplicationComponent;
import jp.gcreate.sample.samplejobqueue.di.ApplicationModule;
import jp.gcreate.sample.samplejobqueue.di.DaggerApplicationComponent;
import jp.gcreate.sample.samplejobqueue.job.MyJobCreator;
import timber.log.Timber;

public class CustomApp extends Application {
    ApplicationComponent applicationComponent;
    @Inject
    MyJobCreator myJobCreator;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                                                         .applicationModule(new ApplicationModule(this))
                                                         .build();
        applicationComponent.inject(this);

        Timber.plant(new Timber.DebugTree());
        JobManager.create(this).addJobCreator(myJobCreator);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
