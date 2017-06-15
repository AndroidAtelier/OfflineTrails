package com.androidatelier.offlinetrails;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

public class ListActivity extends AppCompatActivity implements ListContract.View {
  private RecyclerView recyclerView;
  private TextView emptyView;

  private ListPresenter presenter;
  private RegionAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);

    recyclerView = (RecyclerView) findViewById(R.id.list);
    emptyView = (TextView) findViewById(R.id.empty);

    adapter = new RegionAdapter(this);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);

    OfflineManager offlineManager = OfflineManager.getInstance(this);
    presenter = new ListPresenter(offlineManager, this);

    presenter.load();
  }

  @Override
  public void setRegions(OfflineRegion[] regions) {
    recyclerView.setVisibility(View.VISIBLE);
    emptyView.setVisibility(View.GONE);
    adapter.setRegions(regions);
  }

  @Override
  public void showMessage(String msg) {
    recyclerView.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
    emptyView.setText(msg);
  }

  @Override
  public void showEmptyMessage() {
    showMessage(getString(R.string.empty));
  }
}