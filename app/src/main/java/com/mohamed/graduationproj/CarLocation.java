package com.mohamed.graduationproj;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CarLocation extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManager;
    String provider;
    TextView policeActive;
    Button callPolice;
    Boolean requestActive = false;
    RelativeLayout container;
    ParseGeoPoint carLocation = new ParseGeoPoint(0, 0);
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_location);
        policeActive = (TextView) findViewById(R.id.textView);
        callPolice = (Button) findViewById(R.id.policeButton);
        container = (RelativeLayout) findViewById(R.id.container);

        setUpMapIfNeeded();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        final Location location = locationManager.getLastKnownLocation(provider);
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateLocation(location);

            }
        });


    }

    public void callPolice(View view) {
        if (requestActive == false) {
            ParseObject call = new ParseObject("CallPolice");
            call.put("carOwnerUsername", ParseUser.getCurrentUser().getUsername());
            ParseACL parseACL = new ParseACL();
            parseACL.setPublicWriteAccess(true);
            parseACL.setPublicReadAccess(true);
            call.setACL(parseACL);
            call.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        policeActive.setText("police Searching....");
                        callPolice.setText("cancel Request");
                        requestActive = true;
                    }
                }
            });
        } else {
            policeActive.setText("police is deActive....");
            callPolice.setText("Call Police");
            requestActive = false;

            ParseQuery<ParseObject> query = new ParseQuery<>("CallPolice");
            query.whereEqualTo("carOwnerUsername", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (parseObjects.size() > 0) {
                            for (ParseObject object : parseObjects) {
                                object.deleteInBackground();
                            }
                        }
                    }
                }
            });


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void updateLocation(final Location location) {

        final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
//        ParseQuery<ParseObject> query = new ParseQuery<>("Location");
//        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> parseObjects, ParseException e) {
//                if (e == null) {
//                    if (parseObjects.size() > 0) {
//                        for (ParseObject object : parseObjects) {
//                            object.put("userLocation", userLocation);
//                            object.saveInBackground();
//                        }
//                    }
//                }
//            }
//        });
        ParseQuery<ParseObject> query1 = new ParseQuery<>("Location");
        query1.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    if (parseObjects.size() > 0) {
                        for (ParseObject car : parseObjects) {
                            carLocation = car.getParseGeoPoint("carLocation");

                        }
                    }
                }
            }
        });


        if (carLocation.getLatitude() != 0 && carLocation.getLongitude() != 0) {
            Log.i("appInfo", carLocation.toString());
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location")));
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(carLocation.getLatitude(), carLocation.getLongitude())).title("CarLocation")));
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateLocation(location);
            }
        }, 5000);

    }


    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        updateLocation(location);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
