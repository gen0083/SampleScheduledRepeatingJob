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

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Repository {
    @SerializedName("name")
    private String  name;
    @SerializedName("full_name")
    private String  fullName;
    @SerializedName("owner")
    private Owner   owner;
    @SerializedName("private")
    private Boolean isPrivate;
    @SerializedName("description")
    private String  description;
    @SerializedName("url")
    private String  url;
    @SerializedName("created_at")
    private Date    createdAt;
    @SerializedName("updated_at")
    private Date  updatedAt;
    @SerializedName("stargazers_count")
    private int     stargazersCount;
    @SerializedName("watchers_count")
    private int     watchersCount;
    @SerializedName("language")
    private String  language;
    @SerializedName("forks_count")
    private int     forksCount;
    @SerializedName("open_issues_count")
    private int     openIssuesCount;
    @SerializedName("forks")
    private int     forks;
    @SerializedName("open_issues")
    private int     openIssues;
    @SerializedName("watchers")
    private int watchers;
    @SerializedName("default_branch")
    private String defaultBranch;

    public Repository() {}

    public Repository(String name, String fullName, Owner owner, Boolean isPrivate,
                      String description, String url, Date createdAt, Date updatedAt,
                      int stargazersCount, int watchersCount, String language, int forksCount,
                      int openIssuesCount, int forks, int openIssues, int watchers,
                      String defaultBranch) {
        this.name = name;
        this.fullName = fullName;
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.description = description;
        this.url = url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stargazersCount = stargazersCount;
        this.watchersCount = watchersCount;
        this.language = language;
        this.forksCount = forksCount;
        this.openIssuesCount = openIssuesCount;
        this.forks = forks;
        this.openIssues = openIssues;
        this.watchers = watchers;
        this.defaultBranch = defaultBranch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(int stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(int watchersCount) {
        this.watchersCount = watchersCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public void setOpenIssuesCount(int openIssuesCount) {
        this.openIssuesCount = openIssuesCount;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    @Override
    public String toString() {
        return String.format("<%s[%s]: name:%s(%s), url:%s, description:%s, language:%s, [watch:%d, star:%d, fork:%d] create:%s update:%s>",
                             getClass().getSimpleName(), hashCode(), name, fullName, url, description, language, watchersCount, stargazersCount, forksCount, createdAt, updatedAt);
    }
}
