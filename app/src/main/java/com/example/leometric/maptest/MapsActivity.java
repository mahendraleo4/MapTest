package com.example.leometric.maptest;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static final int PERMISSION_ACCESS_LOCATION = 123;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Button btn_push_data;
    Button btn_push_read;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        userList = new ArrayList<User>();
        btn_push_data = findViewById(R.id.btn_push_data);
        btn_push_read = findViewById(R.id.btn_push_read);

//
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//master changes
///home/leometric/Android/Sdk
///home/leometric/Android/Sdk

        //commit from branch one
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);  //getting network provider

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        readDataFromDatabase();

       btn_push_data.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           }
       });

        btn_push_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOnMap();
            }
        });
    }

    private void showOnMap() {

        PolylineOptions options = new PolylineOptions().width(5).color(getResources().getColor(R.color.green)).geodesic(true);

        for(int i=0;i<userList.size();i++){
            if(userList.get(i).getLat()!=null){
                // Add a marker in Sydney and move the camera
                System.out.println("inside showmap:"+userList.get(i).getLat()+" long"+userList.get(i).getLng());
                LatLng sydney = new LatLng(Double.parseDouble(userList.get(i).getLat()), Double.parseDouble(userList.get(i).getLng()));
                Marker mak=mMap.addMarker(new MarkerOptions().position(sydney).title(userList.get(i).getLocation_name()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                setMarkerBounce(mak);
                options.add(sydney);
//                mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(getResources().getColor(R.color.green))
//                        .add(sydney)  // Sydney
//                );
            }
        }
         mMap.addPolyline(options);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setIndoorEnabled(true);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("MAHI", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAHI", "Can't find style. Error: ", e);
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(18.5679, 73.9143);
        Marker mak=mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Viman nagar"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        setMarkerBounce(mak);
//        try {
//            KmlLayer layer = new KmlLayer(mMap, R.raw.animatedupdate_example, getApplicationContext());
//            layer.addLayerToMap();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    private void setMarkerBounce(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed/duration), 0);
                marker.setAnchor(0.5f, 1.0f +  t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                } else {
                   // setMarkerBounce(marker);
                }
            }
        });
    }
    public void pushDataIntoDatabase(String data){
        User user=new User("mahendra","18.5569","73.9063","Pune");
        String key=myRef.child("user").child("007").push().getKey();
        myRef.child("user").child("007").child(key).setValue(user);
       // myRef.setValue(data);
         user=new User("kunar","18.5585","73.9063","Viman nagar");
        key=myRef.child("user").child("008").push().getKey();
        myRef.child("user").child("008").child(key).setValue(user);

        user=new User("kunar","18.5569"," 73.9107","Viman nagar");
        key=myRef.child("user").child("009").push().getKey();
        myRef.child("user").child("009").child(key).setValue(user);
    }

    public void readDataFromDatabase(){
        myRef.child("user").child("008").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("DATA", "onChildAdded: data" + dataSnapshot.getChildrenCount());
                User user =dataSnapshot.getValue(User.class);
                Log.d("DATA", "Value is: " + user.time);
                userList.add(user);
                showOnMap();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("inside onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("inside onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("inside onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("inside onCancelled");
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        System.out.println("inside onLocation changed");
// Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            String fnialAddress = builder.toString(); //This is the complete address.
            // System.out.println("FINAL ADDRESS " +fnialAddress);
            // addressField.setText(fnialAddress); //This will display the final address.
            User user=new User("mahendra",String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),fnialAddress);
            user.setTime(String.valueOf(ServerValue.TIMESTAMP));
            String key=myRef.child("user").child("008").push().getKey();
            myRef.child("user").child("008").child(key).setValue(user);
        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        System.out.println("inside onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        System.out.println("inside onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {

        System.out.println("inside onProviderDisabled");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                } else {
                    // permission denied
                }
                return;
            }
        }
    }

}
