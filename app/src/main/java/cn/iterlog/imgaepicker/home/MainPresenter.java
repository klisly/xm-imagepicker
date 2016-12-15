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
