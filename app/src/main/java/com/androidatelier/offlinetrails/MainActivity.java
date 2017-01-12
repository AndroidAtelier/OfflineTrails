package com.androidatelier.offlinetrails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "OfflineTrails";

  // JSON encoding/decoding
  public final static String JSON_CHARSET = "UTF-8";
  public final static String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

  private MapView mapView;
  private MapboxMap map = null;

  // Offline objects
  private OfflineManager offlineManager;
  private OfflineRegion offlineRegion;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MapboxAccountManager.start(this, getString(R.string.access_token));

    offlineManager = OfflineManager.getInstance(this);

    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
      }
    });
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.download_map:
        checkZoomLevel();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void checkZoomLevel() {
    double zoom = map.getCameraPosition().zoom;
    if (zoom < 12.5) {
      Toast.makeText(this, R.string.please_zoom_in_more, Toast.LENGTH_LONG).show();
    } else {
      downloadMap();
    }
  }

  private void downloadMap() {
    LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
    Toast.makeText(this, bounds.toString(), Toast.LENGTH_SHORT).show();

    // Create offline definition using the current
    // style and boundaries of visible map area
    String styleURL = map.getStyleUrl();
    double minZoom = map.getCameraPosition().zoom;
    double maxZoom = map.getMaxZoom();
    float pixelRatio = this.getResources().getDisplayMetrics().density;
    OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
            styleURL, bounds, minZoom, maxZoom, pixelRatio);

    byte[] metadata = null;

    // Create the offline region and launch the download
    offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
      @Override
      public void onCreate(OfflineRegion offlineRegion) {
        Log.d(TAG, "Offline region created. " );
        MainActivity.this.offlineRegion = offlineRegion;
        launchDownload();
      }

      @Override
      public void onError(String error) {
        Log.e(TAG, "Error: " + error);
      }
    });
  }

  private void launchDownload() {
    // Set up an observer to handle download progress and
    // notify the user when the region is finished downloading
    offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
      @Override
      public void onStatusChanged(OfflineRegionStatus status) {

        if (status.isComplete()) {
          // Download complete
          Toast.makeText(MainActivity.this, "Region downloaded successfully.", Toast.LENGTH_SHORT).show();
          return;
        }

        // Log what is being currently downloaded
        Log.d(TAG, String.format("%s/%s resources; %s bytes downloaded.",
                String.valueOf(status.getCompletedResourceCount()),
                String.valueOf(status.getRequiredResourceCount()),
                String.valueOf(status.getCompletedResourceSize())));
      }

      @Override
      public void onError(OfflineRegionError error) {
        Log.e(TAG, "onError reason: " + error.getReason());
        Log.e(TAG, "onError message: " + error.getMessage());
      }

      @Override
      public void mapboxTileCountLimitExceeded(long limit) {
        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
      }
    });

    // Change the region state
    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
  }


  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  public void onPause() {
    mapView.onPause();
    super.onPause();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    mapView.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
}