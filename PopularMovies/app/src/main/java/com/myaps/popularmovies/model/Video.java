package com.myaps.popularmovies.model;

import com.myaps.popularmovies.utils.IStaticSetter;
import java.util.HashMap;

/**
 * Created by mgajewski on 2017-03-24.
 */

public class Video {
    private String id;
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIso_639_1() {
        return iso_639_1;
    }
    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }
    public String getIso_3166_1() {
        return iso_3166_1;
    }
    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }



    /**
     * Code for automatic mapping of Movie class fields to REST service data
     * Not using Gson (or Retrofit for REST layer) just for Java exercise :)
     */

    public static final HashMap<String, IStaticSetter<Video, ? super Object>> Keys = new HashMap<>();

    /**
     * Java 8 dependent code
     */
    static
    {
        Keys.put("id", Video::setId);
        Keys.put("iso_639_1", Video::setIso_639_1);
        Keys.put("iso_3166_1", Video::setIso_3166_1);
        Keys.put("key", Video::setKey);
        Keys.put("name", Video::setName);
        Keys.put("site", Video::setSite);
        Keys.put("size", Video::setSize);
        Keys.put("type", Video::setType);
    }
    /**
     *  End of Java 8 dependent code
     */


    private static void setId(Video object, Object id) {
        object.setId((String)id);
    }
    private static void setIso_639_1(Video object, Object iso_639_1) {
        object.setIso_639_1((String)iso_639_1);
    }
    private static void setIso_3166_1(Video object, Object iso_3166_1) {
        object.setIso_3166_1((String)iso_3166_1);
    }
    private static void setKey(Video object, Object key) {
        object.setKey((String)key);
    }
    private static void setName(Video object, Object name) {
        object.setName((String)name);
    }
    private static void setSite(Video object, Object site) {
        object.setSite((String)site);
    }
    private static void setSize(Video object, Object size) {
        object.setSize((int)size);
    }
    private static void setType(Video object, Object type) {
        object.setType((String)type);
    }
}
