package com.androidatelier.offlinetrails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "OfflineTrails";

  public final static Charset CHARSET = Charset.forName("UTF-8");

  private MapView mapView;
  private MapboxMap map = null;

  private ProgressBar progressBar;

  // Offline objects
  private OfflineManager offlineManager;
  private OfflineRegion offlineRegion;

  private View regionNameContainer;
  private Button regionNameOkButton;
  private Button regionNameCancelButton;
  private EditText regionNameEditText;

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

    progressBar = (ProgressBar) findViewById(R.id.progress);

    regionNameContainer = findViewById(R.id.region_name_container);
    regionNameOkButton =  (Button) findViewById(R.id.region_name_ok_button);
    regionNameCancelButton = (Button) findViewById(R.id.region_name_cancel_button);
    regionNameEditText = (EditText) findViewById(R.id.region_name_edit_text);

    regionNameOkButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String name = regionNameEditText.getText().toString().trim();
        if (!name.isEmpty()) {
          downloadMap(name);
          regionNameContainer.setVisibility(View.GONE);
        } else {
          Toast.makeText(MainActivity.this, getString(R.string.region_name_toast),
              Toast.LENGTH_LONG).show();
        }
      }
    });

    regionNameCancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        regionNameContainer.setVisibility(View.GONE);
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
      showRegionName();
    }
  }
  private void showRegionName() {
    regionNameContainer.setVisibility(View.VISIBLE);
  }

  private void downloadMap(String name) {
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

    byte[] metadata = name.getBytes(CHARSET);

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
          progressBar.setVisibility(View.GONE);
          return;
        }

        // Log what is being currently downloaded
        Log.d(TAG, String.format("%s/%s resources; %s bytes downloaded.",
                String.valueOf(status.getCompletedResourceCount()),
                String.valueOf(status.getRequiredResourceCount()),
                String.valueOf(status.getCompletedResourceSize())));
        progressBar.setProgress((int) status.getCompletedResourceCount());
        progressBar.setMax((int) status.getRequiredResourceCount());
      }

      @Override
      public void onError(OfflineRegionError error) {
        Log.e(TAG, "onError reason: " + error.getReason());
        Log.e(TAG, "onError message: " + error.getMessage());
        progressBar.setVisibility(View.GONE);
      }

      @Override
      public void mapboxTileCountLimitExceeded(long limit) {
        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
        progressBar.setVisibility(View.GONE);
      }
    });

    progressBar.setProgress(0);
    progressBar.setMax(100);
    progressBar.setVisibility(View.VISIBLE);

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