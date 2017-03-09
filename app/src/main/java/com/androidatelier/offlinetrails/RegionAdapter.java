package com.androidatelier.offlinetrails;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.offline.OfflineRegion;

import java.util.ArrayList;


public class RegionAdapter extends RecyclerView.Adapter<RegionViewHolder> {
  private final ArrayList<OfflineRegion> regions;

  public RegionAdapter() {
    regions = new ArrayList<>();
  }

  public void setRegions(OfflineRegion[] regions) {
    this.regions.clear();
    for (OfflineRegion region : regions) {
      this.regions.add(region);
    }
  }

  @Override
  public RegionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.region_list_item, parent, false);
    return new RegionViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final RegionViewHolder holder, int position) {
    final OfflineRegion region = regions.get(position);
    byte[] metadata = region.getMetadata();
    String name = new String(metadata);
    holder.nameView.setText(name);
  }

  @Override
  public int getItemCount() {
    return regions.size();
  }
}