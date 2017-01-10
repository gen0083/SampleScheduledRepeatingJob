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

import org.junit.Test;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThreeTenAbpTest {
    @Test
    public void compute_duration() {
        // ZoneOffset.UTC.normalized()を使っているのはZoneId.systemDefaultなどZoneIdにアクセスしようとすると
        // org.threeten.bp.zone.ZoneRulesException: No time-zone data files registeredというエラーが発生するため。
        // これはApplicationクラスでAndroidThreeTen.init()を呼び出していないため起こる
        ZonedDateTime beforeDate = ZonedDateTime.of(2017, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC.normalized());
        // 本来ならこう書ける
//        ZonedDateTime beforeDate = ZonedDateTime.of(2017, 1, 1, 10, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime afterDate = ZonedDateTime.of(2017,1,1,10,11,12,0, ZoneOffset.UTC.normalized());
        System.out.println(beforeDate);
        System.out.println(afterDate);

        Duration d = Duration.between(beforeDate, afterDate);
        System.out.println("toString:" + d);
        System.out.println("minutes:" + d.toMinutes());

        assertThat(d.toMinutes(), is(11L));
    }

    @Test
    public void deserialize_ZonedDateTime_from_long_value() {
        // 2017-1-1T10:10:10Z
        long millis = 1483265410000L;
        ZonedDateTime actual = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC.normalized());
        assertThat(actual.toString(), is("2017-01-01T10:10:10Z"));
    }

    @Test
    public void serialize_ZonedDateTime_to_long_value() {
        ZonedDateTime time = ZonedDateTime.of(2017,1,1,10,10,10,0, ZoneOffset.UTC.normalized());
        long actual = DateTimeUtils.toDate(time.toInstant()).getTime();
        assertThat(actual, is(1483265410000L));

        long epochSecond = time.toEpochSecond();
        assertThat(epochSecond, not(1483265410000L)); // 1483265410L
        System.out.println(epochSecond);
        // toEpochSecondではミリ秒の情報が抜け落ちるため、java.util.Dateへの変換を考えると使えない
        // Dateを介在させないならepochSecondを使うほうが楽
        // こんな感じでZonedDateTimeに変換できる
//        ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC.normalized());
    }

    @Test
    public void format_test() {
        ZonedDateTime time = ZonedDateTime.of(2017,1,1,10,10,10,0, ZoneOffset.UTC.normalized());
        DateTimeFormatter shortStyle = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        DateTimeFormatter longStyle = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        DateTimeFormatter fullStyle = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        DateTimeFormatter isoZonedDateTime = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        DateTimeFormatter isoLocalDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter customStyle = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");

        System.out.println("short:" + shortStyle.format(time));
        System.out.println("full:" + fullStyle.format(time));
        System.out.println("long:" + longStyle.format(time));
        System.out.println("iso_zoned_date_time:" + isoZonedDateTime.format(time));
        System.out.println("iso_local_date_time:" + isoLocalDateTime.format(time));
        System.out.println("custom:" + customStyle.format(time));
    }
}
