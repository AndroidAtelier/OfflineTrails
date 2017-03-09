package com.androidatelier.offlinetrails;


import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

public class ListPresenter {
  private final OfflineManager offlineManager;
  private final ListContract.View view;

  public ListPresenter(OfflineManager offlineManager, ListContract.View view) {
    this.offlineManager = offlineManager;
    this.view = view;
  }

  public void load() {
    offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
      @Override
      public void onList(OfflineRegion[] offlineRegions) {
        if (offlineRegions == null || offlineRegions.length == 0) {
          view.showEmptyMessage();
          return;
        }
        view.setRegions(offlineRegions);
      }

      @Override
      public void onError(String error) {
        view.showMessage(error);
      }
    });
  }
}
