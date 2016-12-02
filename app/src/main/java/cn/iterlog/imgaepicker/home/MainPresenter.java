/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.iterlog.imgaepicker.home;

import android.net.Uri;
import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link MainFragment}), retrieves the data and updates
 * the UI as required.
 */
public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mView;

    public MainPresenter(@NonNull MainContract.View statisticsView) {
        mView = checkNotNull(statisticsView, "StatisticsView cannot be null!");
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void showSingleChoose() {
        mView.showSingleChoose();
    }

    @Override
    public void showMultiChoose() {
        mView.showMultiChoose();
    }

    @Override
    public void setImage(boolean isVideo, Uri uri) {
        mView.showChooseImage(isVideo, uri);
    }
}
