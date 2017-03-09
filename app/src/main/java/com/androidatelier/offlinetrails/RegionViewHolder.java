package com.androidatelier.offlinetrails;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RegionViewHolder extends RecyclerView.ViewHolder {
  public final TextView nameView;
  private final View mapButton;
  private final View deleteButton;

  public RegionViewHolder(View itemView) {
    super(itemView);
    nameView = (TextView) itemView.findViewById(R.id.name);
    mapButton = itemView.findViewById(R.id.map);
    deleteButton = itemView.findViewById(R.id.delete);
  }
}