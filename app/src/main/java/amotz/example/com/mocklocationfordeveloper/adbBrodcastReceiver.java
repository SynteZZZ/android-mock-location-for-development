package amotz.example.com.mocklocationfordeveloper;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import java.util.HashSet;

public class adbBrodcastReceiver extends BroadcastReceiver {

    MockLocationProvider mockGPS;
    MockLocationProvider mockWifi;
    String logTag = "MockGpsadbBrodcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        if ("stop.mock".equals(intent.getAction())) {
            if (mockGPS != null) {
                mockGPS.shutdown();
            }
            if (mockWifi != null) {
                mockWifi.shutdown();
            }
        } else {
            mockGPS = new MockLocationProvider(LocationManager.GPS_PROVIDER, context);
            mockWifi = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, context);

            double lat, lon, alt, recency;
            float accurate;
            String type;

            lat = Double.parseDouble(intent.getStringExtra("lat") != null ? intent.getStringExtra("lat") : "0");
            lon = Double.parseDouble(intent.getStringExtra("lon") != null ? intent.getStringExtra("lon") : "0");
            alt = Double.parseDouble(intent.getStringExtra("alt") != null ? intent.getStringExtra("alt") : "0");

            accurate = Float.parseFloat(intent.getStringExtra("accurate") != null ? intent.getStringExtra("accurate") : "0");

            recency = Double.parseDouble(intent.getStringExtra("recency") != null ? intent.getStringExtra("recency") : "0");

            type = parseType(intent);

            Log.i(logTag,
                    String.format("Setting %s mock to Latitude=%f, Longitude=%f Altitude=%f Accuracy=%f Recency=%g",
                            "".equals(type) ? "gps, network" : type,
                            lat, lon, alt, accurate, recency));

            HashSet<MockLocationProvider> toMock = new HashSet<>();

            if ("".equals(type)) {
                toMock.add(mockGPS);
                toMock.add(mockWifi);
            } else if (isGpsType(type)) {
                toMock.add(mockGPS);
            } else if (isNetworkType(type)) {
                toMock.add(mockWifi);
            }

            for (MockLocationProvider mockLocationProvider : toMock) {
                mockLocationProvider.pushLocation(lat, lon, alt, accurate, recency);
            }
        }
    }

    private String parseType(Intent intent) {
        String type = "";
        String extra = intent.getStringExtra("type");
        if (extra != null) {
            if (isGpsType(extra) || isNetworkType(extra)) {
                type = extra;
            }
        }
        return type;
    }

    private boolean isNetworkType(String extra) {
        return LocationManager.NETWORK_PROVIDER.equals(extra);
    }

    private boolean isGpsType(String extra) {
        return LocationManager.GPS_PROVIDER.equals(extra);
    }
}
