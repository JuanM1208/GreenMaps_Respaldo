package com.example.greenmapsrespaldo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    boolean isPermissionGranted;
    FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);

        checkMyPermission();
        initMap();
        mLocationClient = new FusedLocationProviderClient(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrLoc();
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.nav_home:
                        Toast.makeText(MainActivity.this, "Inicio", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Bienvenida.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_water:
                        Toast.makeText(MainActivity.this, "Agua", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_air:
                        Toast.makeText(MainActivity.this, "Aire", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_green:
                        Toast.makeText(MainActivity.this, "Áreas verdes", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_info:
                        Toast.makeText(MainActivity.this, "Acerca de", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(MainActivity.this, "Configuración", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_contact:
                        Toast.makeText(MainActivity.this, "Contacto", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_share:
                        Toast.makeText(MainActivity.this, "Comparte", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_rate:
                        Toast.makeText(MainActivity.this, "Califícanos", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void initMap() {
        if (isPermissionGranted) {
            if (isGPSenable()){
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
    }

    private boolean isGPSenable(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnable){
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Permiso de GPS");
                    builder.setMessage("Esta app necesita el GPS para funcionar. Por favor habilite el GPS");
                    builder.setPositiveButton("Ok", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getCurrLoc() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 14);
        mMap.moveCamera(cameraUpdate);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MainActivity.this, "Permiso otorgado", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        //Zonas de calidad del aire
        Circle atemajac = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.719431295977163, -103.35580117535605))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle oblatos = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.697645069101288, -103.29581775695488))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle vallarta = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.678608, -103.399396))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle centro = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.672422131856187, -103.3332153993359))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle tlaquepaque = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.639333297236206, -103.31312905549588))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle lomaDorada = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.627740376622047, -103.26409837363586))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle aguilas = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.630096526853336, -103.4167356924431))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle miravalle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.612687160151275, -103.34339308333261))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle lasPintas = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.57678391504256, -103.32647964788045))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        Circle santaFe = mMap.addCircle(new CircleOptions()
                .center(new LatLng(20.52919926636695, -103.37719039248793))
                .radius(1200)
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(128, 192, 192, 192))
        );

        // Add a marker in Gdl and move the camera
        LatLng gdl = new LatLng(20.6736, -103.344);
        /*mMap.addMarker(new MarkerOptions()
                .position(gdl)
                .title("Marker in Sydney"));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gdl, 11));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnable){
                Toast.makeText(this, "GPS habilitado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS deshabilitado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}