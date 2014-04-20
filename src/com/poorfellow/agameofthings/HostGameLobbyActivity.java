package com.poorfellow.agameofthings;

import android.support.v7.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.cast.RemoteMediaPlayer.OnMetadataUpdatedListener;
import com.google.android.gms.cast.RemoteMediaPlayer.OnStatusUpdatedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.poorfellow.agameofthings.chromecast.ChromecastHelper;
import android.support.v7.app.MediaRouteActionProvider;
import com.google.android.gms.cast.RemoteMediaPlayer.OnStatusUpdatedListener;
import com.google.android.gms.cast.RemoteMediaPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 3/2/14.
 */
public class HostGameLobbyActivity
        extends ActionBarActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {
    private Map<String, String> playerMap;
    private BroadcastReceiver scanChangedReceiver;
    private IntentFilter scanChangedFilter;
    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;
    private ChromecastHelper chromecastHelper;
    private CastDevice selectedDevice;
    private MediaRouterCallback mediaRouterCallback;
    private RemoteMediaPlayer remoteMediaPlayer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);
        final ScrollView playersScroll = (ScrollView) findViewById(R.id.joiningPlayersScollView);
        playerMap = new HashMap<String, String>();
        chromecastHelper = ChromecastHelper.getInstance(this);
        mediaRouteSelector = chromecastHelper.getMediaRouteSelector();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        actionBar.show();

        scanChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        mediaRouter = chromecastHelper.getMediaRouter();
        mediaRouterCallback = new MediaRouterCallback();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("STATUS", "Inside on create options menu");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.media_router_action_bar, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteButton mediaRouteButton = (MediaRouteButton) mediaRouteMenuItem.getActionView();

        MediaRouteActionProvider mediaActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);

        mediaActionProvider.setRouteSelector(mediaRouteSelector);
        mediaRouter.addCallback(mediaRouteSelector, mediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
        Log.d("STATUS", "The media button is enabled? " + mediaActionProvider.getMediaRouteButton().isEnabled());
        Log.d("STATUS", "The media button is in layout? " + mediaActionProvider.getMediaRouteButton().isInLayout());
        return true;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }*/

    public void onPause() {
        if (isFinishing()) {
            mediaRouter.removeCallback(mediaRouterCallback);
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        mediaRouter.addCallback(mediaRouteSelector, mediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    public void OnDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo info) {
            selectedDevice = CastDevice.getFromBundle(info.getExtras());
            chromecastHelper.setConnectedDevice(selectedDevice);
            chromecastHelper.launchReciever();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo info) {
            //make a teardown method
            selectedDevice = null;

        }
    }

}