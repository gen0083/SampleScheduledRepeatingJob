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

package jp.gcreate.sample.samplejobqueue.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

@Table
public class JobHistory {
    @PrimaryKey
    public long id;

    @Column(helpers = Column.Helpers.ORDERS)
    public ZonedDateTime checkedDate;

    @Column
    public Duration duration;

    @Column
    public int repositoriesCount;

    public JobHistory() {}

    public JobHistory(ZonedDateTime checkedDate, Duration duration, int repositoriesCount) {
        this.checkedDate = checkedDate;
        this.duration = duration;
        this.repositoriesCount = repositoriesCount;
    }
}
