package com.myaps.popularmovies.utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by mgajewski on 2017-01-30.
 */

public class JsonUtils {
    private static final String IMAGE_URL_NAME = "poster_path";

    public static String getImageUrl(JSONObject item) {
        try {
            return item.getString(IMAGE_URL_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getFromJson(Class<T> resultClass, JSONObject item) {

        try {
            Field keysField = resultClass.getField("Keys");
            Object value = keysField.get(null);
            HashMap<String, IStaticSetter<T, ? super Object>> Keys = (HashMap<String, IStaticSetter<T, ? super Object>>) value;
            T result = resultClass.newInstance();


            for (String key : Keys.keySet()) {
                Object o = item.get(key);
                if (o.getClass() == JSONArray.class) {
                    JSONArray json = (JSONArray)o;
                    Object[] array = new Object[json.length()];
                    for (int i = 0; i < json.length(); ++i) {
                        array[i] = json.get(i);
                    }
                    Keys.get(key).setValue(result, array);
                } else if (o.getClass() == JSONObject.class) {
                    //TODO: Not implemented yet. Not needed in current usecase
                } else
                {
                    Keys.get(key).setValue(result, o);
                }
            }

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
