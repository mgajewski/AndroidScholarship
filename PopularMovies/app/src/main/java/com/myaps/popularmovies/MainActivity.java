package com.myaps.popularmovies;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myaps.popularmovies.adapters.MoviesAdapter;
import com.myaps.popularmovies.services.MoviesService;
import com.myaps.popularmovies.tasks.ReloadMoviesTask;
import com.myaps.popularmovies.utils.ConnectivityHelper;
import com.myaps.popularmovies.utils.FavouritesHelper;
import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.IItemClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InjectMenu;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_main)
//@OptionsMenu(R.menu.main_menu)
public class MainActivity extends AppCompatActivity implements IItemClickListener<JSONObject> {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bean
    MoviesAdapter moviesAdapter;
    @Bean
    MoviesService moviesService;
    @Bean
    ConnectivityHelper connectivityHelper;

    @NonConfigurationInstance
    @Bean
    ReloadMoviesTask reloadMoviesTask;

    @ViewById(R.id.rv_movies)
    RecyclerView recyclerView;

    @ViewById(R.id.tv_error_message_display)
    TextView errorMessageDisplay;

    @ViewById(R.id.tv_not_connected)
    TextView noConnectionDisplay;

    @ViewById(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;

    MenuItem menuMostPopularItem;
    MenuItem menuTopRatedItem;
    MenuItem menuFavouritesItem;
    MenuItem menuRefreshItem;
    @InjectMenu
    void setMenu(Menu mainMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, mainMenu);
        menuMostPopularItem = mainMenu.findItem(R.id.action_popular);
        menuTopRatedItem = mainMenu.findItem(R.id.action_rated);
        menuFavouritesItem = mainMenu.findItem(R.id.action_fav);
        menuRefreshItem = mainMenu.findItem(R.id.action_refresh);

        if (currentSortType == MoviesService.SortType.MOST_POPULAR) {
            menuMostPopularItem.setChecked(true);
            menuTopRatedItem.setChecked(false);
            menuFavouritesItem.setChecked(false);
        } else if (currentSortType == MoviesService.SortType.TOP_RATED) {
            menuMostPopularItem.setChecked(false);
            menuTopRatedItem.setChecked(true);
            menuFavouritesItem.setChecked(false);
        } else if (currentSortType == MoviesService.SortType.FAVOURITES) {
            menuMostPopularItem.setChecked(false);
            menuTopRatedItem.setChecked(false);
            menuFavouritesItem.setChecked(true);
        }
    }
    @OptionsItem(R.id.action_rated)
    void actionTopRated(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            menuMostPopularItem.setChecked(false);
            menuFavouritesItem.setChecked(false);
            currentSortType = MoviesService.SortType.TOP_RATED;
            refresh();
        }
    }
    @OptionsItem(R.id.action_popular)
    void actionMostPopular(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            menuTopRatedItem.setChecked(false);
            menuFavouritesItem.setChecked(false);
            currentSortType = MoviesService.SortType.MOST_POPULAR;
            refresh();
        }
    }
    @OptionsItem(R.id.action_fav)
    void actionFavourites(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            menuTopRatedItem.setChecked(false);
            menuMostPopularItem.setChecked(false);
            currentSortType = MoviesService.SortType.FAVOURITES;
            refresh();
        }
    }

    @OptionsItem(R.id.action_refresh)
    void actionRefresh() {
        reloadMoviesData(true);
    }

    @InstanceState
    MoviesService.SortType currentSortType = MoviesService.SortType.MOST_POPULAR;
    @InstanceState
    boolean initialized = false;
    @InstanceState
    boolean isLoading;

    @Bean
    FavouritesHelper favHelper;

    @AfterViews
    void afterViewsInjection() {
        if (!initialized) {
            favHelper.init(getContentResolver());
            initialized = true;
        }

        onConnectionChange(connectivityHelper.isConnected());

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);

        moviesAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(moviesAdapter);
        refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectivityHelper.init(this::onConnectionChange);
        refresh(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityHelper.dispose();
    }

    private void reloadMoviesData(boolean rewind) {
        //if (currentSortType == MoviesService.SortType.FAVOURITES) {
            setLoadingState(true);
            loadingIndicator.setVisibility(View.VISIBLE);
            reloadMoviesTask.reloadMovies(currentSortType, rewind);
//        } else {
//            if (connectivityHelper.isConnected()) {
//                setLoadingState(true);
//                loadingIndicator.setVisibility(View.VISIBLE);
//                reloadMoviesTask.reloadMovies(currentSortType);
//            }
//        }
    }

    /**
     * Makes reload data from api only if current provider is empty.
     * Sets provider into adapter so notifyDataChange is called and
     * recyclerview is refreshed
     */
    private void refresh(boolean rewind) {
        if (currentSortType == MoviesService.SortType.FAVOURITES) {
            reloadMoviesData(false);
        } else {
            if (moviesService.getDataProvider(currentSortType).getCount() == 0 ) {
                reloadMoviesData(true);
            } else {
                showMoviesDataView();
                IDataProvider provider = moviesService.getDataProvider(currentSortType);
                moviesAdapter.setDataProvider(provider);
                if (rewind) {
                    recyclerView.scrollToPosition(0);
                }
            }
        }
    }
    private void refresh() {
        refresh(true);
    }


    private void showMoviesDataView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        noConnectionDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        if (currentSortType == MoviesService.SortType.FAVOURITES) {
            errorMessageDisplay.setText(R.string.no_favourites);
        } else {
            errorMessageDisplay.setText(R.string.error_message);
        }
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(JSONObject movie) {
        Intent intent = new Intent(this, DetailsActivity_.class);
        intent.putExtra(Intent.EXTRA_TEXT, movie.toString());
        startActivity(intent);
    }

    public void moviesReloadTaskFinished(boolean result, boolean rewind) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        setLoadingState(false);
        if (result == true) {
            IDataProvider dataProvider = moviesService.getDataProvider(currentSortType);
            moviesAdapter.setDataProvider(dataProvider);
            showMoviesDataView();
            if (rewind) {
                recyclerView.scrollToPosition(0);
            }
        } else {
            showErrorMessage();
        }
    }

    private void onConnectionChange(boolean isConnected) {
        if (isConnected) {
            setTitle(R.string.app_name);
        } else {
            setTitle(R.string.app_name_offline);
        }
    }

    private void setLoadingState(boolean isLoading) {
        this.isLoading = isLoading;
        boolean enabled = !isLoading;
        if (menuTopRatedItem != null) {
            menuTopRatedItem.setEnabled(enabled);
        }
        if (menuMostPopularItem != null) {
            menuMostPopularItem.setEnabled(enabled);
        }

        if (menuFavouritesItem != null) {
            menuFavouritesItem.setEnabled(enabled);
        }
        if (menuRefreshItem != null) {
            menuRefreshItem.setEnabled(enabled);
        }
    }
}
