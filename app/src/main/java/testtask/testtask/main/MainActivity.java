package testtask.testtask.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import testtask.testtask.R;
import testtask.testtask.core.CacheLoadingTask;
import testtask.testtask.core.PicturesFetchingTask;
import testtask.testtask.main.adapters.PicturesAdapter;
import testtask.testtask.models.Picture;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private Toolbar toolbar;
    private View errorContainer;
    private View errorButton;
    private AsyncTask task;
    private View loading;
    private String query = "kittens";
    private View resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        errorContainer = findViewById(R.id.errorContainer);
        errorButton = findViewById(R.id.errorButton);
        loading = findViewById(R.id.loading);
        resetButton = findViewById(R.id.reset);
        setSupportActionBar(toolbar);

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPictures();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo remove pictures from bd and download it again
            }
        });

        // We use GAP_HANDLING_NONE cause pictures define their size by themselves
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recycler.setLayoutManager(layoutManager);
        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            float alpha = 1.0f;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }
            /**
             * If scrolling down, dy is possitive, up - negative.
             * */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                // Alpha shouldn't be bigger then 1 and less then 0, but if it is - we just don't
                // increase or decrease.
                if ((alpha > 0 || dy < 0) && (alpha < 1 || dy > 0)) {
                    alpha -= dy * 0.01f;
                }
                // If alpha is 0, we still can press it, so we have to hide it at all.
                if (alpha <= 0) {
                    resetButton.setVisibility(View.GONE);
                } else {
                    resetButton.setVisibility(View.VISIBLE);
                    resetButton.setAlpha(alpha);
                }
                Log.d("Scrolling", "dy=" + dy + ", alpha=" + alpha);

            }
        });
        // Checkin "visited" state
        boolean firstVisit = getSharedPreferences("main", MODE_MULTI_PROCESS).getBoolean("firstVisit", true);
        if (firstVisit)
            fetchPictures();
        else
            loadCache();
    }
    /**
     * Creates asynchronous task to load pictures from cache. If there is no images, it will try
     * to download it from web. Then it binds to new adapter and attaches it to recyclerView.
     *
     * Will not work if task already exists.
     * */
    private void loadCache() {
        if (task == null) {
            loading.setVisibility(View.VISIBLE);
            errorContainer.setVisibility(View.GONE);
            task = new CacheLoadingTask() {
                @Override
                protected void onSuccess(ArrayList<Picture> result) {
                    task = null;
                    recycler.setAdapter(new PicturesAdapter(result));
                    // Toggling controlls visibility to loaded
                    loading.setVisibility(View.GONE);
                    if (result.isEmpty()) {
                        fetchPictures();
                    }
                }

                @Override
                protected void onError(Exception exp) {
                    task = null;
                    // Toggling controlls visibility to loading
                    loading.setVisibility(View.GONE);
                    errorContainer.setVisibility(View.VISIBLE);
                }
            };
            task.execute();
        }
    }
    /**
     * Creates asynchronous task to load pictures web.
     * Then it binds to new adapter and attaches it to recyclerView.
     *
     * Will not work if task already exists.
     * */
    private void fetchPictures() {
        if (task == null) {
            // Toggling controlls visibility to loading
            loading.setVisibility(View.VISIBLE);
            errorContainer.setVisibility(View.GONE);
            task = new PicturesFetchingTask(query) {
                @Override
                protected void onSuccess(ArrayList<Picture> result) {
                    task = null;
                    // Saving state "visitied"
                    getSharedPreferences("main", MODE_MULTI_PROCESS).edit().putBoolean("firstVisit", false).apply();
                    recycler.setAdapter(new PicturesAdapter(result));
                    // Toggling controlls visibility to loaded
                    loading.setVisibility(View.GONE);
                }

                @Override
                protected void onError(Exception exp) {

                    // Toggling controlls visibility to error
                    task = null;
                    loading.setVisibility(View.GONE);
                    errorContainer.setVisibility(View.VISIBLE);
                }
            };
            task.execute();
        }
    }
}