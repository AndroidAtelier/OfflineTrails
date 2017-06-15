package com.androidatelier.offlinetrails;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
    ImageButton mapButton = (ImageButton) view.findViewById(R.id.map);
    mapButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d("test", "map click");
      }
    });
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