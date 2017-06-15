package com.androidatelier.offlinetrails;


import android.content.Context;
import android.content.Intent;
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
  private Context context;

  public RegionAdapter(Context context) {
    regions = new ArrayList<>();
    this.context = context;
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

    final RegionViewHolder regionViewHolder = new RegionViewHolder(view);

    mapButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d("test", "map click");
        Intent intent = new Intent(context, SavedMapActivity.class);

        byte[] metadata = regions.get(regionViewHolder.getAdapterPosition()).getMetadata();
        String mapTitle = new String(metadata);

        intent.putExtra("map_title", mapTitle);
        context.startActivity(intent);
      }
    });
    return regionViewHolder;
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