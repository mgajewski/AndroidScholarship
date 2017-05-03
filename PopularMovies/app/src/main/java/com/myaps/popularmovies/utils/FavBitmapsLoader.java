package com.myaps.popularmovies.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.LruCache;
import android.widget.ImageView;

import com.myaps.popularmovies.tasks.Base64Task;
import com.myaps.popularmovies.tasks.LoadFavB64ImageTask;
import com.myaps.popularmovies.tasks.LoadFavB64ImageTask_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.Console;

/**
 * Created by mgajewski on 2017-05-02.
 */

@EBean(scope = EBean.Scope.Singleton)
public class FavBitmapsLoader {
    public interface Callback {
        void onSuccess();
        void onError();
    }

    private static LruCache<Integer, Bitmap> memoryCache = new LruCache<>(100);

    @EBean
    public static class LoadRequestBuilder {
        Context context;
        int sourceMovieId;
        int errorResId = 0;
        int placeholderResId = 0;
        ImageView target;
        Callback callback;

        @Bean
        LoadFavB64ImageTask loadDataTask;
        @Bean
        Base64Task decodeBitmapTask;

        LoadRequestBuilder(Context ctx) {
            context = ctx;
        }

        public LoadRequestBuilder load(int movieId) {
            sourceMovieId = movieId;
            return this;
        }

        public LoadRequestBuilder error(int errorImageId) {
            this.errorResId = errorImageId;
            return this;
        }

        public LoadRequestBuilder placeholder(int placeHolderImageId) {
            this.placeholderResId = placeHolderImageId;
            return this;
        }

        public void into(ImageView imageView, Callback callback) {
            this.callback = callback;
            target = imageView;
            if (target != null && placeholderResId > 0) {
                target.setImageResource(placeholderResId);
            }
            Bitmap bmp = getBitmapFromMemCache(sourceMovieId);
            if (bmp != null) {
                resolveBitmap(bmp);
            } else {
                loadBitmap();
            }
        }

        private void loadBitmap() {
            loadDataTask.run(sourceMovieId, data -> {
                if (data != null) {
                    decodeBitmapTask.decode(data, new Base64Task.Callback(){
                        @Override
                        public void result(Bitmap bitmap) {
                            resolveBitmap(bitmap);
                        }
                    });
                } else {
                    error();
                }
            });
        }

        private void resolveBitmap(Bitmap bmp) {
            if (bmp != null) {
                addBitmapToMemoryCache(sourceMovieId, bmp);
                if (target != null) {
                    target.setImageBitmap(bmp);
                }
                if (callback != null) {
                    callback.onSuccess();
                }
            } else {
                error();
            }
        }

        private void error() {
            if (target != null && errorResId > 0) {
                target.setImageResource(errorResId);
            }
            if (callback != null) {
                callback.onError();
            }
        }

        private void addBitmapToMemoryCache(int key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                memoryCache.put(key, bitmap);
            }
        }

        private Bitmap getBitmapFromMemCache(int key) {
            return memoryCache.get(key);
        }
    }

    public LoadRequestBuilder with(Context ctx) {
        return FavBitmapsLoader_.LoadRequestBuilder_.getInstance_(ctx);
    }
}
