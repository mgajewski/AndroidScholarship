package com.myaps.popularmovies.model;

import com.myaps.popularmovies.utils.IStaticSetter;

import java.util.HashMap;

/**
 * Created by mgajewski on 2017-03-24.
 */

public class Review {
    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * Code for automatic mapping of Movie class fields to REST service data
     * Not using Gson (or Retrofit for REST layer) just for Java exercise :)
     */

    public static final HashMap<String, IStaticSetter<Review, ? super Object>> Keys = new HashMap<>();

    /**
     * Java 8 dependent code
     */
    static
    {
        Keys.put("id", Review::setId);
        Keys.put("author", Review::setAuthor);
        Keys.put("content", Review::setContent);
        Keys.put("url", Review::setUrl);
    }
    /**
     *  End of Java 8 dependent code
     */


    private static void setId(Review object, Object id) {
        object.setId((String)id);
    }
    private static void setAuthor(Review object, Object author) {
        object.setAuthor((String)author);
    }
    private static void setContent(Review object, Object content) {
        object.setContent((String)content);
    }
    private static void setUrl(Review object, Object url) {
        object.setUrl((String)url);
    }
}
