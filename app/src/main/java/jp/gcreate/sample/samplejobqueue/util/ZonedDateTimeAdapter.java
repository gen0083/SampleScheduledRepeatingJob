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

import com.github.gfx.android.orma.annotation.StaticTypeAdapter;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

@StaticTypeAdapter(
        targetType = ZonedDateTime.class,
        serializedType = long.class
)
public class ZonedDateTimeAdapter {
    public static long serialize(ZonedDateTime source) {
        return DateTimeUtils.toDate(source.toInstant()).getTime();
    }

    public static ZonedDateTime deserialize(long serialized) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(serialized), ZoneId.systemDefault());
    }
}
