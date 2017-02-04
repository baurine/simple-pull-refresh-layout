package com.baurine.simplepullrefreshlayoutsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baurine.multitypeadapter.MultiTypeAdapter;
import com.baurine.simplepullrefreshlayout.SimplePullRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private SimplePullRefreshLayout pullRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MultiTypeAdapter adapter = new MultiTypeAdapter();
        adapter.addItem(new ImageItem(R.color.eggplant, R.drawable.icon_1));
        adapter.addItem(new ImageItem(R.color.saffron, R.drawable.icon_2));
        adapter.addItem(new ImageItem(R.color.sienna, R.drawable.icon_3));
        recyclerView.setAdapter(adapter);

        pullRefreshLayout = (SimplePullRefreshLayout) findViewById(R.id.pull_refresh_layout);
        pullRefreshLayout.setOnRefreshListener(new SimplePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public void onClick(View view) {
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
