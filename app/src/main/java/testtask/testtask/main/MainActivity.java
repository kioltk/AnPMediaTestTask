package testtask.testtask.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


public class MainActivity extends ActionBarActivity {

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
        recycler = (RecyclerView) findViewById(R.id.recycler);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        errorContainer = findViewById(R.id.errorContainer);
        errorButton = findViewById(R.id.errorButton);
        loading = findViewById(R.id.loading);
        resetButton = findViewById(R.id.reset);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPictures();
            }
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recycler.setLayoutManager(layoutManager);
        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int oldy = 0;
            float alpha = 1.0f;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


                if( (alpha>0 || dy < 0) && (alpha<1 || dy > 0 )){
                    alpha -= dy * 0.01f;
                }

                if (alpha < 0) {
                    resetButton.setVisibility(View.GONE);
                } else {
                    resetButton.setVisibility(View.VISIBLE);
                    resetButton.setAlpha(alpha);
                }
                Log.d("Scrolling", "dy=" + dy + ", alpha=" + alpha);

            }
        });
        boolean firstLoading = getSharedPreferences("main",MODE_MULTI_PROCESS).getBoolean("firstLoading",true);
        if(firstLoading)
            fetchPictures();
        else
            loadCache();
        setSupportActionBar(toolbar);
    }

    private void loadCache() {
        if(task==null) {
            loading.setVisibility(View.VISIBLE);
            errorContainer.setVisibility(View.GONE);
                task = new CacheLoadingTask() {
                    @Override
                    protected void onSuccess(ArrayList<Picture> result) {
                        task = null;
                        recycler.setAdapter(new PicturesAdapter(result));
                        loading.setVisibility(View.GONE);

                    }

                    @Override
                    protected void onError(Exception exp) {
                        task = null;
                        loading.setVisibility(View.GONE);
                        errorContainer.setVisibility(View.VISIBLE);
                    }
                };
            task.execute();
        }
    }

    private void fetchPictures() {
        if(task==null) {
            loading.setVisibility(View.VISIBLE);
            errorContainer.setVisibility(View.GONE);
            task = new PicturesFetchingTask(query) {
                @Override
                protected void onSuccess(ArrayList<Picture> result) {
                    task = null;
                    getSharedPreferences("main", MODE_MULTI_PROCESS).edit().putBoolean("firstLoading", false).apply();
                    recycler.setAdapter(new PicturesAdapter(result));
                    loading.setVisibility(View.GONE);

                }

                @Override
                protected void onError(Exception exp) {
                    task = null;
                    loading.setVisibility(View.GONE);
                    errorContainer.setVisibility(View.VISIBLE);
                }
            };
            task.execute();
        }
    }

}
