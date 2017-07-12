package com.leadbolt.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.apptracker.android.listener.AppModuleListener;
import com.apptracker.android.track.AppTracker;
// Leadbolt SDK imports

public class MainActivity extends Activity {
	
	/*
	------------------------------------------------------------------------------------------------------------------------
    This sample app is intended for developers including Leadbolt SDK in their apps
	 
	Please ensure you the required Android Permissions in your Manifest file - INTERNET and ACCESS_NETWORK_STATE
	Please ensure Google Play Services library is included in your Android Project so Google AID can be retrieved

	You will need a Leadbolt Publisher account to retrieve your App specific API Key
	------------------------------------------------------------------------------------------------------------------------
	*/

    // Leadbolt SDK configurations
    private static final String APP_API_KEY 		    = "is2byYEVjbXiFjVjaYIt6sM4aEIqMWZ3"; // change this to your App specific API KEY
    private static final String LOCATION_CODE		    = "inapp";
    private static final String LOCATION_CODE_VIDEO 	= "video";

    // setup some buttons for demo
    private Button cache;
    private Button show;
    private Button cacheV;
    private Button showV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cache = (Button) findViewById(R.id.button_cache);
        show = (Button) findViewById(R.id.button_show);
        cacheV = (Button) findViewById(R.id.button_cacheV);
        showV = (Button) findViewById(R.id.button_showV);

        if(savedInstanceState == null)
        {
            // Set the Leadbolt Event listener to get notified of different stages of the Ad life-cycle
            AppTracker.setModuleListener(leadboltListener);
            // Initialize Leadbolt SDK
            AppTracker.startSession(getApplicationContext(), APP_API_KEY);

            if(!AppTracker.isAdReady(LOCATION_CODE)) {
                show.setEnabled(false); // disable the button till Ad is available to be displayed
            }
            if(!AppTracker.isAdReady(LOCATION_CODE_VIDEO)) {
                showV.setEnabled(false);
            }

            // Bind Leadbolt SDK actions to buttons
            cacheDirectDeal(cache);
            showDirectDeal(show);

            cacheRewardedVideo(cacheV);
            showRewardedVideo(showV);
        }
    }

    private void cacheDirectDeal(Button b)
    {

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppTracker.destroyModule();
                AppTracker.loadModuleToCache(getApplicationContext(), LOCATION_CODE);
            }
        });
    }

    private void showDirectDeal(Button b)
    {
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppTracker.loadModule(getApplicationContext(), LOCATION_CODE);
            }
        });
    }

    private void cacheRewardedVideo(Button b)
    {

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppTracker.destroyModule();
                AppTracker.loadModuleToCache(getApplicationContext(), LOCATION_CODE_VIDEO);
            }
        });
    }

    private void showRewardedVideo(Button b)
    {
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppTracker.loadModule(getApplicationContext(), LOCATION_CODE_VIDEO);
            }
        });
    }

    private AppModuleListener leadboltListener = new AppModuleListener() {

        @Override
        public void onModuleCached(final String placement) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast("Module "+placement+" cached successfully");

                    // Ad has been cached, now enable the Show Ad button
                    if(placement.equals(LOCATION_CODE)) {
                        show.setEnabled(true);
                    } else if(placement.equals(LOCATION_CODE_VIDEO)) {
                        showV.setEnabled(true);
                    }
                }
            });
        }
        @Override
        public void onModuleClicked(String placement) {
            toast("Ad clicked");
        }
        @Override
        public void onModuleClosed(final String placement) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(placement.equals(LOCATION_CODE)) {
                        toast("Ad closed");
                    } else if(placement.equals(LOCATION_CODE_VIDEO)) {
                        toast("Rewarded Video closed");
                    }
                }
            });
        }
        @Override
        public void onModuleFailed(String placement, String error, boolean isCache) {
            if(isCache) {
                toast("Ad failed to cache - "+error);
            } else {
                toast("Ad failed to load - "+error);
            }
        }

        @Override
        public void onModuleLoaded(String placement) {
            // Ad has been shown, now disable to the Show Ad button
            if(placement.equals(LOCATION_CODE)) {
                toast("Ad displayed");
                show.setEnabled(false);
            } else if(placement.equals(LOCATION_CODE_VIDEO)) {
                toast("Rewarded Video displayed");
                showV.setEnabled(false);
            }
        }
        @Override
        public void onMediaFinished(boolean viewCompleted) {
            if(viewCompleted) {
                toast("User finished watching Rewarded Video");
            } else {
                toast("User skipped watching Rewarded Video");
            }
        }
    };

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
