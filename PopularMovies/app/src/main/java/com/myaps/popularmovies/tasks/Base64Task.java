package com.myaps.popularmovies.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.io.ByteArrayOutputStream;

/**
 * Created by mgajewski on 2017-05-02.
 */

@EBean
public class Base64Task {

    public static class Callback {
        public void result(String base64Image) {}
        public void result(Bitmap bitmap) {}
    }

    Callback taskCallback;

    @Background
    public void encode(Bitmap bitmap, Callback callback) {
        taskCallback = callback;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bai = baos.toByteArray();
        String result = Base64.encodeToString(bai, Base64.DEFAULT);
        updateUI(result);
    }

    @Background
    public void decode(String base64Image, Callback callback) {
        taskCallback = callback;
        byte[] data = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap result;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        result = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        updateUI(result);
    }

    @UiThread
    void updateUI(String result) {
        if (taskCallback != null) {
            taskCallback.result(result);
        }
    }

    @UiThread
    void updateUI(Bitmap result) {
        if (taskCallback != null) {
            taskCallback.result(result);
        }
    }
}
