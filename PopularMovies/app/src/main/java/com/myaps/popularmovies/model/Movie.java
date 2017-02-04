package com.myaps.popularmovies.model;

import com.myaps.popularmovies.utils.IStaticSetter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by mgajewski on 2017-01-31.
 */

public class Movie {

    private int id;
    private String posterPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private Integer[] genreIds;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private double popularity;
    private int voteCount;
    private boolean video;
    private double voteAverage;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public boolean isAdult() {
        return adult;
    }
    public void setAdult(boolean adult) {
        this.adult = adult;
    }
    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public Integer[] getGenreIds() {
        return genreIds;
    }
    public void setGenreIds(Integer[] genreIds) {
        this.genreIds = genreIds;
    }
    public String getOriginalTitle() {
        return originalTitle;
    }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
    public String getOriginalLanguage() {
        return originalLanguage;
    }
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBackdropPath() {
        return backdropPath;
    }
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
    public double getPopularity() {
        return popularity;
    }
    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
    public int getVoteCount() {
        return voteCount;
    }
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    public boolean hasVideo() {
        return video;
    }
    public void setVideo(boolean video) {
        this.video = video;
    }
    public double getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }






    /**
     * Code for automatic mapping of Movie class fields to REST service data
     * Not using Gson (or Retrofit for REST layer) just for Java exercise :)
     */

    public static final HashMap<String, IStaticSetter<Movie, ? super Object>> Keys = new HashMap<>();

    /**
     * Java 8 dependent code
     * If no Java 8 available comment out below static area
     */
    static
    {
        Keys.put("id", Movie::setId);
        Keys.put("poster_path", Movie::setPosterPath);
        Keys.put("adult", Movie::setAdult);
        Keys.put("overview", Movie::setOverview);
        Keys.put("release_date", Movie::setReleaseDate);
        Keys.put("genre_ids", Movie::setGenreIds);
        Keys.put("original_title", Movie::setOriginalTitle);
        Keys.put("original_language", Movie::setOriginalLanguage);
        Keys.put("title", Movie::setTitle);
        Keys.put("backdrop_path", Movie::setBackdropPath);
        Keys.put("popularity", Movie::setPopularity);
        Keys.put("vote_count", Movie::setVoteCount);
        Keys.put("video", Movie::setVideo);
        Keys.put("vote_average", Movie::setVoteAverage);
    }
    /**
     *  End of Java 8 dependent code
     *
     *  If No Java 8 available uncomment below static area
     */
    /*
    static
    {
        Keys.put("id", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setId(object, value);
            }
        });
        Keys.put("poster_path", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setPosterPath(object, value);
            }
        });
        Keys.put("adult",new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setAdult(object, value);
            }
        });
        Keys.put("overview", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setOverview(object, value);
            }
        });
        Keys.put("release_date", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setReleaseDate(object, value);
            }
        });
        Keys.put("genre_ids", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setGenreIds(object, value);
            }
        });
        Keys.put("original_title", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setOriginalTitle(object, value);
            }
        });
        Keys.put("original_language", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setOriginalLanguage(object, value);
            }
        });
        Keys.put("title", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setTitle(object, value);
            }
        });
        Keys.put("backdrop_path", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setBackdropPath(object, value);
            }
        });
        Keys.put("popularity", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setPopularity(object, value);
            }
        });
        Keys.put("vote_count", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setVoteCount(object, value);
            }
        });
        Keys.put("video", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setVideo(object, value);
            }
        });
        Keys.put("vote_average", new IStaticSetter<Movie, Object>() {
            @Override
            public void setValue(Movie object, Object value) {
                Movie.setVoteAverage(object, value);
            }
        });
    }
    */

    private static void setId(Movie object, Object id) {
        object.setId((int)id);
    }
    private static void setPosterPath(Movie object, Object posterPath) {
        object.setPosterPath((String)posterPath);
    }
    private static void setOverview(Movie object, Object overview) {
        object.setOverview((String)overview);
    }
    private static void setReleaseDate(Movie object, Object releaseDate) {
        object.setReleaseDate((String)releaseDate);
    }
    private static void setAdult(Movie object, Object adult) {
        object.setAdult((boolean)adult);
    }
    private static void setGenreIds(Movie object, Object genreIds) {
        Object[] array = (Object[])genreIds;
        Integer[] arrayCopy = Arrays.copyOf(array, array.length, Integer[].class);
        object.setGenreIds(arrayCopy);
    }
    private static void setOriginalTitle(Movie object, Object originalTitle) {
        object.setOriginalTitle((String)originalTitle);
    }
    private static void setOriginalLanguage(Movie object, Object originalLanguage) {
        object.setOriginalLanguage((String)originalLanguage);
    }
    private static void setTitle(Movie object, Object  title) {
        object.setTitle((String)title);
    }
    private static void setBackdropPath(Movie object, Object backdropPath) {
        object.setBackdropPath((String)backdropPath);
    }
    private static void setPopularity(Movie object, Object popularity) {
        object.setPopularity((double)popularity);
    }
    private static void setVoteCount(Movie object, Object voteCount) {
        object.setVoteCount((int)voteCount);
    }
    private static void setVideo(Movie object, Object video) {
        object.setVideo((boolean)video);
    }
    private static void setVoteAverage(Movie object, Object voteAverage) {
        //From some reason voteAvarage sometimes is double and sometimes is integer :(
        if (voteAverage.getClass() == Double.class) {
            object.setVoteAverage((double)voteAverage);
        } else if (voteAverage.getClass() == Integer.class) {
            int va = (int) voteAverage;
            object.setVoteAverage((double) va);
        }
    }
}
