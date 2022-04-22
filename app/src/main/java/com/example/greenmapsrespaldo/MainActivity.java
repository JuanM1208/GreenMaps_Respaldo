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
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    int flagAire = 0;
    int flagAgua = 0;
    int flagVerde = 0;

    //Puntos del mapa
    CircleOptions aire = new CircleOptions()
            .center(new LatLng(20.6736, -103.344))
            .radius(1200)
            .strokeWidth(7)
            .strokeColor(Color.GRAY)
            .fillColor(Color.argb(128, 192, 192, 192));

    CircleOptions agua = new CircleOptions()
            .center(new LatLng(20.6736, -103.344))
            .radius(500)
            .strokeWidth(6)
            .strokeColor(Color.argb(255, 0, 155, 255 ))
            .fillColor(Color.argb(128, 51, 255, 255));

    CircleOptions verde = new CircleOptions()
            .center(new LatLng(20.6736, -103.344))
            .radius(250)
            .strokeWidth(5)
            .strokeColor(Color.GREEN)
            .fillColor(Color.argb(128, 182, 255, 0));

    Circle rSantiago, rVerde, lagCaj; //Cuerpos de agua
    Circle atemajac, oblatos, vallarta, centro, tlaquepaque, lomaDorada, aguilas, miravalle, lasPintas, santaFe; //Puntos de aire
    Circle pMetro, bColomos, bPrimavera, pGonzGallo, pSoli, pSanRafa, cerroReina, pAlcalde, pAguaAzul, zoo, barranca, pMorelos, pAvilaC, country, pSanJacinto, bCentinela, pMontenegro; //Areas verdes

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

                        if (flagAgua == 1) {
                            break;
                        }

                        if (flagAire == 1){
                            atemajac.setVisible(false); oblatos.setVisible(false); vallarta.setVisible(false); centro.setVisible(false); tlaquepaque.setVisible(false);
                            lomaDorada.setVisible(false); aguilas.setVisible(false); miravalle.setVisible(false); lasPintas.setVisible(false); santaFe.setVisible(false);
                            flagAire = 0;
                        }

                        if (flagVerde == 1){
                            pMetro.setVisible(false); bColomos.setVisible(false); bPrimavera.setVisible(false); pGonzGallo.setVisible(false); pSoli.setVisible(false); pSanRafa.setVisible(false);
                            cerroReina.setVisible(false); pAlcalde.setVisible(false); pAguaAzul.setVisible(false); zoo.setVisible(false); barranca.setVisible(false); pMorelos.setVisible(false);
                            pAvilaC.setVisible(false); country.setVisible(false); pSanJacinto.setVisible(false); bCentinela.setVisible(false); pMontenegro.setVisible(false);
                            flagVerde = 0;
                        }

                        flagAgua = 1;

                        rSantiago = mMap.addCircle(agua);
                        rSantiago.setCenter(new LatLng(20.74588387632154, -103.30451464376783));
                        rSantiago.setVisible(true);
                        rSantiago.setClickable(true);

                        rVerde = mMap.addCircle(agua);
                        rVerde.setCenter(new LatLng(20.71947382618508, -103.25119267170733));
                        rVerde.setVisible(true);
                        rVerde.setClickable(true);

                        lagCaj = mMap.addCircle(agua);
                        lagCaj.setCenter(new LatLng(20.41854716110271, -103.32117624041832));
                        lagCaj.setVisible(true);
                        lagCaj.setClickable(true);

                        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                            @Override
                            public void onCircleClick(@NonNull Circle circle) {
                                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                final View popupView = getLayoutInflater().inflate(R.layout.popup_agua, null);
                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();
                            }
                        });

                        break;
                    case R.id.nav_air:
                        Toast.makeText(MainActivity.this, "Aire", Toast.LENGTH_SHORT).show();

                        if (flagAgua == 1) {
                            rSantiago.setVisible(false); rVerde.setVisible(false); lagCaj.setVisible(false);
                            flagAgua = 0;
                        }

                        if (flagAire == 1){
                            break;
                        }

                        if (flagVerde == 1){
                            pMetro.setVisible(false); bColomos.setVisible(false); bPrimavera.setVisible(false); pGonzGallo.setVisible(false); pSoli.setVisible(false); pSanRafa.setVisible(false);
                            cerroReina.setVisible(false); pAlcalde.setVisible(false); pAguaAzul.setVisible(false); zoo.setVisible(false); barranca.setVisible(false); pMorelos.setVisible(false);
                            pAvilaC.setVisible(false); country.setVisible(false); pSanJacinto.setVisible(false); bCentinela.setVisible(false); pMontenegro.setVisible(false);
                            flagVerde = 0;
                        }

                        flagAire = 1;

                        atemajac = mMap.addCircle(aire);
                        atemajac.setCenter(new LatLng(20.719431295977163, -103.35580117535605));
                        atemajac.setVisible(true);
                        atemajac.setClickable(true);

                        oblatos = mMap.addCircle(aire);
                        oblatos.setCenter(new LatLng(20.697645069101288, -103.29581775695488));
                        oblatos.setVisible(true);
                        oblatos.setClickable(true);

                        vallarta = mMap.addCircle(aire);
                        vallarta.setCenter(new LatLng(20.678608, -103.399396));
                        vallarta.setVisible(true);
                        vallarta.setClickable(true);

                        centro = mMap.addCircle(aire);
                        centro.setCenter(new LatLng(20.672422131856187, -103.3332153993359));
                        centro.setVisible(true);
                        centro.setClickable(true);

                        tlaquepaque = mMap.addCircle(aire);
                        tlaquepaque.setCenter(new LatLng(20.639333297236206, -103.31312905549588));
                        tlaquepaque.setVisible(true);
                        tlaquepaque.setClickable(true);

                        lomaDorada = mMap.addCircle(aire);
                        lomaDorada.setCenter(new LatLng(20.627740376622047, -103.26409837363586));
                        lomaDorada.setVisible(true);
                        lomaDorada.setClickable(true);

                        aguilas = mMap.addCircle(aire);
                        aguilas.setCenter(new LatLng(20.630096526853336, -103.4167356924431));
                        aguilas.setVisible(true);
                        aguilas.setClickable(true);

                        miravalle = mMap.addCircle(aire);
                        miravalle.setCenter(new LatLng(20.612687160151275, -103.34339308333261));
                        miravalle.setVisible(true);
                        miravalle.setClickable(true);

                        lasPintas = mMap.addCircle(aire);
                        lasPintas.setCenter(new LatLng(20.57678391504256, -103.32647964788045));
                        lasPintas.setVisible(true);
                        lasPintas.setClickable(true);

                        santaFe = mMap.addCircle(aire);
                        santaFe.setCenter(new LatLng(20.52919926636695, -103.37719039248793));
                        santaFe.setVisible(true);
                        santaFe.setClickable(true);

                        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                            @Override
                            public void onCircleClick(@NonNull Circle circle) {
                                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                final View popupView = getLayoutInflater().inflate(R.layout.popup_aire, null);
                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();
                            }
                        });

                        break;
                    case R.id.nav_green:
                        Toast.makeText(MainActivity.this, "Áreas verdes", Toast.LENGTH_SHORT).show();

                        if (flagAgua == 1) {
                            rSantiago.setVisible(false); rVerde.setVisible(false); lagCaj.setVisible(false);
                            flagAgua = 0;
                        }

                        if (flagAire == 1){
                            atemajac.setVisible(false); oblatos.setVisible(false); vallarta.setVisible(false); centro.setVisible(false); tlaquepaque.setVisible(false);
                            lomaDorada.setVisible(false); aguilas.setVisible(false); miravalle.setVisible(false); lasPintas.setVisible(false); santaFe.setVisible(false);
                            flagAire = 0;
                        }

                        if (flagVerde == 1){
                            break;
                        }

                        flagVerde = 1;

                        pMetro = mMap.addCircle(verde);
                        pMetro.setCenter(new LatLng(20.67251732563299, -103.44046661765249));
                        pMetro.setVisible(true);
                        pMetro.setClickable(true);

                        bColomos = mMap.addCircle(verde);
                        bColomos.setCenter(new LatLng(20.708405605073253, -103.39439377096463));
                        bColomos.setVisible(true);
                        bColomos.setClickable(true);

                        bPrimavera = mMap.addCircle(verde);
                        bPrimavera.setCenter(new LatLng(20.655978455929677, -103.52752540969983));
                        bPrimavera.setVisible(true);
                        bPrimavera.setClickable(true);

                        pGonzGallo = mMap.addCircle(verde);
                        pGonzGallo.setCenter(new LatLng(20.64758403407237, -103.3370436717078));
                        pGonzGallo.setVisible(true);
                        pGonzGallo.setClickable(true);

                        pSoli = mMap.addCircle(verde);
                        pSoli.setCenter(new LatLng(20.661878738507795, -103.2689496838224));
                        pSoli.setVisible(true);
                        pSoli.setClickable(true);

                        pSanRafa = mMap.addCircle(verde);
                        pSanRafa.setCenter(new LatLng(20.652914435610317, -103.29776166928336));
                        pSanRafa.setVisible(true);
                        pSanRafa.setClickable(true);

                        cerroReina = mMap.addCircle(verde);
                        cerroReina.setCenter(new LatLng(20.634987605203605, -103.23966491371455));
                        cerroReina.setVisible(true);
                        cerroReina.setClickable(true);

                        pAlcalde = mMap.addCircle(verde);
                        pAlcalde.setCenter(new LatLng(20.690509332409462, -103.35066117248687));
                        pAlcalde.setVisible(true);
                        pAlcalde.setClickable(true);

                        pAguaAzul = mMap.addCircle(verde);
                        pAguaAzul.setCenter(new LatLng(20.65964819954858, -103.3480307849272));
                        pAguaAzul.setVisible(true);
                        pAguaAzul.setClickable(true);

                        zoo = mMap.addCircle(verde);
                        zoo.setCenter(new LatLng(20.728729256954846, -103.30705763282965));
                        zoo.setVisible(true);
                        zoo.setClickable(true);

                        barranca = mMap.addCircle(verde);
                        barranca.setCenter(new LatLng(20.708335206853167, -103.27821181800107));
                        barranca.setVisible(true);
                        barranca.setClickable(true);

                        pMorelos = mMap.addCircle(verde);
                        pMorelos.setCenter(new LatLng(20.68050607332156, -103.34072179723735));
                        pMorelos.setVisible(true);
                        pMorelos.setClickable(true);

                        pAvilaC = mMap.addCircle(verde);
                        pAvilaC.setCenter(new LatLng(20.71212245497867, -103.37229934669014));
                        pAvilaC.setVisible(true);
                        pAvilaC.setClickable(true);

                        country = mMap.addCircle(verde);
                        country.setCenter(new LatLng(20.70634194117349, -103.37234226203344));
                        country.setVisible(true);
                        country.setClickable(true);

                        pSanJacinto = mMap.addCircle(verde);
                        pSanJacinto.setCenter(new LatLng(20.66338260872832, -103.29738006524146));
                        pSanJacinto.setVisible(true);
                        pSanJacinto.setClickable(true);

                        bCentinela = mMap.addCircle(verde);
                        bCentinela.setCenter(new LatLng(20.763644586096575, -103.37529305996419));
                        bCentinela.setVisible(true);
                        bCentinela.setClickable(true);

                        pMontenegro = mMap.addCircle(verde);
                        pMontenegro.setCenter(new LatLng(20.57433779775093, -103.31570121143199));
                        pMontenegro.setVisible(true);
                        pMontenegro.setClickable(true);

                        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                            @Override
                            public void onCircleClick(@NonNull Circle circle) {
                                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                final View popupView = getLayoutInflater().inflate(R.layout.popup_verde, null);
                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();
                            }
                        });

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