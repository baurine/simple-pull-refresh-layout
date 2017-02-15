# SimplePullRefreshLayout

[![](https://jitpack.io/v/baurine/simple-pull-refresh-layout.svg)](https://jitpack.io/#baurine/simple-pull-refresh-layout)

A very simple, less features and easy to understand PullRefreshLayout, likes SwipeRefreshLayout, but much simple.

## Note

1. [SimplePullRefreshLayout Note](./note/simple-pullrefreshlayout-note.md)

## Samples

![](./note/art/1_sample.gif)

## Getting Started

Add JitPack as library source in your project build.gradle:

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

Then, add dependency in your app module build.gradle:

    dependencies {
        compile 'com.github.baurine:simple-pull-refresh-layout:${latest-version}'
    }

`latest-version`: see header JitPack badge.

## Usage

It's nearly the same with SwipeRefreshLayout：

XML:

    <com.baurine.simplepullrefreshlayout.SimplePullRefreshLayout
        android:id="@+id/pull_refresh_layout"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#000000"
        app:drag_coefficient="0.7"
        app:extra_height="20dp">

        <!-- you can replace this default SimpleRefreshStateView by yourself implementation
        <com.baurine.simplepullrefreshlayout.SimpleRefreshStateView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"/>
        -->

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </com.baurine.simplepullrefreshlayout.SimplePullRefreshLayout>

Java Code:

    private void initViews() {
      ...
      pullRefreshLayout = (SimplePullRefreshLayout) findViewById(R.id.pull_refresh_layout);
      pullRefreshLayout.setOnRefreshListener(new SimplePullRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
              pullRefreshLayout.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      pullRefreshLayout.setRefreshing(false);
                  }
              }, 2000);
          }
      });
    }

    public void onClick(View view) {
      pullRefreshLayout.setRefreshing(true);
      pullRefreshLayout.postDelayed(new Runnable() {
          @Override
          public void run() {
              pullRefreshLayout.setRefreshing(false);
          }
      }, 2000);
    }

You can define yourself RefreshStateView and put it inside SimplePullRefreshLayout as its first child view, just implement RefreshStateView interface:

    public interface RefreshStateView {
        void setPercent(float percent);

        void setRefreshing(boolean refreshing);
    }

custom attributes:

attr | type | default value
-----|------|--------------
extra_height     | demension | same with RefreshStateView height
drag_coefficient | float     | 0.7

License
-------

    Copyright 2017 baurine.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
