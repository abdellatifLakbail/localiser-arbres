package com.localiserarbres;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE =1;
    Location currentLocation ;
    FusedLocationProviderClient fusedLocationProviderClient ;

    ImageButton iconLogout, iconMenu, iconDownload, iconDownloadJson;

    PopupMenu popupMenu;

    private Double newLatitude, newLongitude;


    private ArrayList<Marker>  markers = new ArrayList<>();

    private static final int ADD_TREE_REQUEST_CODE =1;

    private String newName,newTaille;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("arbres");


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(MainActivity.this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLasLocation();


        //icon menu
        iconMenu = findViewById(R.id.menu);
        popupMenu = new PopupMenu(this, iconMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                change(item.getItemId());
                return true;
            }
        });


        //icon Menu
        iconMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });


        //icon log out
        iconLogout = findViewById(R.id.myIconLogOut);
        iconLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Log Out ");
                builder.setMessage("Are you sure you want to log out ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        //icon download data from database to my app
        myRef =database.getReference("arbres");
        iconDownload = findViewById(R.id.download);
        ArrayList<Arbre> arbres = new ArrayList<>();
        iconDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Download Data To My Maps");
                builder.setMessage("Are you sure ? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for(DataSnapshot arbreSnapshot : snapshot.getChildren()){
                                    Map<String, Object> arbreMap = (Map<String, Object>) arbreSnapshot.getValue();

                                    if(arbreMap != null){
                                        String name = (String) arbreMap.get("name");
                                        Double latitude = (Double) arbreMap.get("latitude");
                                        Double longitude = (Double) arbreMap.get("longitude");
                                        String taille = (String) arbreMap.get("taille");

                                        if(name != null && latitude != null && longitude != null && taille != null){
                                            LatLng position = new LatLng(latitude,longitude);
                                            myMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.tree)).title(name).snippet("taille : "+taille));
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();



            }
        });


        myRef =database.getReference("arbres");
        //icon download file json
        iconDownloadJson = findViewById(R.id.downloadJson);
        iconDownloadJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Download File Json ");
                builder.setMessage("Are you sure ? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    JSONArray jsonArray = new JSONArray();

                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        Map<String, Object> dataMap = (Map<String, Object>) dataSnapshot.getValue();
                                        JSONObject jsonObject = new JSONObject(dataMap);
                                        jsonArray.put(jsonObject);
                                    }
                                    File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"FirebaseExports");

                                    if(!exportDir.exists()){
                                        exportDir.mkdirs();
                                    }

                                    File file = new File(exportDir,"firebase_export_"+System.currentTimeMillis()+".json");
                                    FileWriter writer = new FileWriter(file);
                                    writer.write(jsonArray.toString());
                                    writer.flush();
                                    writer.close();

                                    Toast.makeText(MainActivity.this,"Export reussi : "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this,"Erreur : "+e.getMessage(),Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this,"Erreur Firebase: "+error.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


    }

    private void change(int itemId) {
        if(itemId == R.id.noneMap){
            myMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
        if(itemId == R.id.normalMap){
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        if(itemId == R.id.satelliteMap){
            myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if (itemId == R.id.terrainMap){
            myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        if(itemId == R.id.hybridMap){
            myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    private void getLasLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            Toast.makeText(this, "there is a problem in acces fine location",Toast.LENGTH_LONG).show();
            return;
        }else{
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        currentLocation = location;
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MainActivity.this);
                    }else {
                        Toast.makeText(MainActivity.this,"there is a problem in current location",Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"problem when acces to my location",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        if(currentLocation != null){
            myMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title("My Location"));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude() , currentLocation.getLongitude()),15));
        }
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myMap.setMyLocationEnabled(true);

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if(currentLocation == null){
                    currentLocation = new Location("");
                }

                Log.d("MapClick", "Clicked location: " + latLng.latitude + ", " + latLng.longitude);

                Toast.makeText(MainActivity.this," "+latLng.latitude+" "+latLng.longitude,Toast.LENGTH_LONG).show();
                newLatitude = latLng.latitude;
                newLongitude = latLng.longitude;
                Intent intent = new Intent(MainActivity.this,AjouterArbre.class);
                intent.putExtra("newlatitude",newLatitude);
                intent.putExtra("newlongitude",newLongitude);
                startActivityForResult(intent, ADD_TREE_REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_TREE_REQUEST_CODE){
            if (resultCode == RESULT_OK && data != null){
                newName =data.getStringExtra("nameValue");
                newTaille= data.getStringExtra("spinnerValue");
                //Toast.makeText(MainActivity.this," "+newName+" | "+newTaille,Toast.LENGTH_LONG).show();
                addMarker(newLatitude,newLongitude,newName,newTaille);



                myRef =database.getReference("arbres");

                // Récupération du nombre d'arbres existants
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long arbreCount = dataSnapshot.getChildrenCount();
                        String newArbreId = "arbre" + (arbreCount + 1);

                        Arbre arbre = new Arbre(newLatitude, newLongitude, newName, newTaille);

                        // Création de la structure complète sous le nouvel ID
                        Map<String, Object> arbreValues = new HashMap<>();
                        arbreValues.put("name", arbre.nameArbre);
                        arbreValues.put("latitude", arbre.latArbre);
                        arbreValues.put("longitude", arbre.longArbre);
                        arbreValues.put("taille", arbre.tailleArbre);

                        //myRef.child("arbres").child(newArbreId).setValue(arbre);

                        // Écriture atomique de toutes les valeurs
                        myRef.child(newArbreId).setValue(arbreValues)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firebase", "Nouvel arbre ajouté avec ID: " + newArbreId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Erreur d'écriture", e);
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Erreur de lecture", databaseError.toException());
                    }
                });


            }
        }
    }

    private void addMarker(double lat, double lon , String newName,String newTaille) {
        Marker marker = null;

        marker= myMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lon))
                .title(newName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.tree))
                .snippet(newTaille));

        if(AjouterArbre.code == 0){
            if(marker != null){
                marker.remove();
                AjouterArbre.code = 1;
            }
        }

        markers.add(marker);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLasLocation();
            }else{
                Toast.makeText(this, "Location Permission is Denied, Please allow the permission ",Toast.LENGTH_LONG).show();
            }
        }
    }
}