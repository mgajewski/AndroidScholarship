package com.myaps.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myaps.popularmovies.services.MoviesService;
import com.myaps.popularmovies.utils.IDataProvider;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_SORT_TYPE = "com.myaps.popularmovies:sort_type";

    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private TextView errorMessageDisplay;
    private TextView noConnectionDisplay;
    private ProgressBar loadingIndicator;

    private MenuItem menuMostPopularItem;
    private MenuItem menuTopRatedItem;
    private MenuItem menuRefreshItem;

    private MoviesService.SortType currentSortType = MoviesService.SortType.MOST_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        noConnectionDisplay = (TextView) findViewById(R.id.tv_not_connected);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);

        moviesAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(moviesAdapter);

        if (savedInstanceState != null) {
            String sortType = savedInstanceState.getString(KEY_SORT_TYPE, MoviesService.SortType.MOST_POPULAR.name());
            currentSortType = MoviesService.SortType.valueOf(sortType);
        }

        refresh();
    }

    private void reloadMoviesData() {
        if (checkConnectivity()) {
            new ReloadMoviesTask().execute();
        }
    }

    /**
     * Makes reload data from api only if current provider is empty.
     * Sets provider into adapter so notifyDataChange is called and
     * recyclerview is refreshed
     */
    private void refresh() {
        boolean isConnected = checkConnectivity();
        if (MoviesService.instance.getDataProvider(currentSortType).getCount() == 0 ) {
            if (isConnected) {
                reloadMoviesData();
            }
        } else {
            showMoviesDataView();
            IDataProvider provider = MoviesService.instance.getDataProvider(currentSortType);
            moviesAdapter.setDataProvider(provider);
            recyclerView.scrollToPosition(0);
        }
    }

    private void showMoviesDataView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        noConnectionDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SORT_TYPE, currentSortType.name());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        menuMostPopularItem = menu.findItem(R.id.action_popular);
        menuTopRatedItem = menu.findItem(R.id.action_rated);
        menuRefreshItem = menu.findItem(R.id.action_refresh);

        if (currentSortType == MoviesService.SortType.MOST_POPULAR) {
            menuMostPopularItem.setChecked(true);
            menuTopRatedItem.setChecked(false);
        } else if (currentSortType == MoviesService.SortType.TOP_RATED) {
            menuMostPopularItem.setChecked(false);
            menuTopRatedItem.setChecked(true);
        }
        checkConnectivity();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int groupId = item.getGroupId();

        if (id == R.id.action_refresh) {
            reloadMoviesData();
            return true;
        }

        if (groupId == R.id.group_sort_type) {
            if (!item.isChecked()) {
                item.setChecked(true);
                if (id == R.id.action_popular) {
                    menuTopRatedItem.setChecked(false);
                    currentSortType = MoviesService.SortType.MOST_POPULAR;
                } else if (id == R.id.action_rated) {
                    menuMostPopularItem.setChecked(false);
                    currentSortType = MoviesService.SortType.TOP_RATED;
                }

                refresh();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(JSONObject movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movie.toString());
        startActivity(intent);
    }

    public class ReloadMoviesTask extends AsyncTask<Void, Float, Boolean> implements MoviesService.LoadingProgressListener {

        public ReloadMoviesTask() {
            MoviesService.instance.setLoadingProgressListener(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loadingIndicator.setProgress(0);
            setMenuItemsEnabled(false);
            setRefreshButtonEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = MoviesService.instance.reloadMovies(currentSortType);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            setMenuItemsEnabled(true);
            setRefreshButtonEnabled(true);
            if (result == true) {
                IDataProvider dataProvider = MoviesService.instance.getDataProvider(currentSortType);
                moviesAdapter.setDataProvider(dataProvider);
                showMoviesDataView();
                recyclerView.scrollToPosition(0);
            } else {
                showErrorMessage();
            }
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            if (values != null && values.length > 0) {
                //loadingIndicator.setProgress((int)(values[0] * 100));
            }
        }

        @Override
        public void onLoadingProgress(float progress) {
            publishProgress(progress);
        }
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.isConnected();
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        if(isConnected) {
            setMenuItemsEnabled(true);
            noConnectionDisplay.setVisibility(View.INVISIBLE);
        }
        else {
            setMenuItemsEnabled(false);
            noConnectionDisplay.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            loadingIndicator.setVisibility(View.INVISIBLE);
        }
        return isConnected;
    }

    private void setMenuItemsEnabled(boolean enabled) {
        if (menuTopRatedItem != null) {
            menuTopRatedItem.setEnabled(enabled);
        }
        if (menuMostPopularItem != null) {
            menuMostPopularItem.setEnabled(enabled);
        }
    }

    private void setRefreshButtonEnabled(boolean enabled) {
        if (menuRefreshItem != null) {
            menuRefreshItem.setEnabled(enabled);
        }
    }
}
