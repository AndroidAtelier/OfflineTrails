package com.androidatelier.offlinetrails;


import com.mapbox.mapboxsdk.offline.OfflineRegion;

public interface ListContract {
  interface View {
    void setRegions(OfflineRegion[] regions);
    void showEmptyMessage();
    void showMessage(String msg);
  }
}
