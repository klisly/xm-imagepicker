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

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.iterlog.imgaepicker.R;
import cn.iterlog.xmimagepicker.Configs;
import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.PickerActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class MainFragment extends Fragment implements MainContract.View {

    private MainContract.Presenter mPresenter;

    private ImageView chooseIv;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull MainContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_main, container, false);
        chooseIv = (ImageView) root.findViewById(R.id.iv);

        root.findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoose();
            }
        });
        root.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureEditChoose();
            }
        });
        root.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureChoose();
            }
        });
        root.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoChoose();
            }
        });
        root.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoPreviewChoose();
            }
        });
        root.findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiVideoChoose();
            }
        });
        root.findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiVideoPictureChoose();
            }
        });
        root.findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiVpChoose();
            }
        });
        setHasOptionsMenu(false);
        setRetainInstance(true);
        return root;
    }


    @Override
    public void showSingleChoose() {

        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(false);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_PICTURE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        PickerActivity.openActivity(getActivity(), 12);
    }

    public void showPictureChoose() {
        Gallery.init(getActivity().getApplication());
        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(false);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_PICTURE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        PickerActivity.openActivity(getActivity(), 12);
    }

    public void showPictureEditChoose() {
        Gallery.init(getActivity().getApplication());
        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(true);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_PICTURE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        PickerActivity.openActivity(getActivity(), 12);
    }


    public void showVideoChoose() {
        Gallery.init(getActivity().getApplication());
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).setPreviewVideo(false);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        PickerActivity.openActivity(getActivity(), 12);
    }


    public void showVideoPreviewChoose() {
        Gallery.init(getActivity().getApplication());
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).setPreviewVideo(true);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        PickerActivity.openActivity(getActivity(), 12);
    }

    @Override
    public void showMultiChoose() {

//         Configs.getInstance(getActivity().getApplicationContext()).addMedia( Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_PICTURE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        Configs.getInstance(getActivity().getApplicationContext()).setImageSize(4);
        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(false);
        Configs.getInstance(getActivity().getApplicationContext()).setPreviewVideo(true);
        Configs.getInstance(getActivity().getApplicationContext()).setMultiChoose(true);
//        setMultiChoose(false);

        PickerActivity.openActivity(getActivity(), 12);
    }

    public void showMultiVideoChoose() {
        Gallery.init(getActivity().getApplication());
//         Configs.getInstance(getActivity().getApplicationContext()).addMedia( Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(false);
        Configs.getInstance(getActivity().getApplicationContext()).setPreviewVideo(true);
        Configs.getInstance(getActivity().getApplicationContext()).setMultiChoose(true);
        Configs.getInstance(getActivity().getApplicationContext()).setImageSize(4);
        PickerActivity.openActivity(getActivity(), 12);
    }

    public void showMultiVideoPictureChoose() {
        Gallery.init(getActivity().getApplication());
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_PICTURE);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(false);
        Configs.getInstance(getActivity().getApplicationContext()).setPreviewVideo(true);
        Configs.getInstance(getActivity().getApplicationContext()).setImageSize(6);
        Configs.getInstance(getActivity().getApplicationContext()).setVideoSize(2);
        Configs.getInstance(getActivity().getApplicationContext()).setSimpleType(true);
        Configs.getInstance(getActivity().getApplicationContext()).setMultiChoose(true);
        PickerActivity.openActivity(getActivity(), 12);
    }

    public void showMultiVpChoose() {
        Gallery.init(getActivity().getApplication());
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_PICTURE);
        Configs.getInstance(getActivity().getApplicationContext()).addMedia(Configs.getInstance(getActivity().getApplicationContext()).MEDIA_MOVIE);
        Configs.getInstance(getActivity().getApplicationContext()).THUMB_SIZE = 256;
        Configs.getInstance(getActivity().getApplicationContext()).setEditImage(false);
        Configs.getInstance(getActivity().getApplicationContext()).setPreviewVideo(true);
        Configs.getInstance(getActivity().getApplicationContext()).setImageSize(6);
        Configs.getInstance(getActivity().getApplicationContext()).setVideoSize(2);
        Configs.getInstance(getActivity().getApplicationContext()).setSimpleType(false);
        Configs.getInstance(getActivity().getApplicationContext()).setMultiChoose(true);
        PickerActivity.openActivity(getActivity(), 12);
    }

    @Override
    public void showChooseImage(boolean isVideo, Uri uri) {
        if (!isVideo) {
            chooseIv.setImageURI(uri);
        } else {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            chooseIv.setImageBitmap(bm);
        }
    }
}
