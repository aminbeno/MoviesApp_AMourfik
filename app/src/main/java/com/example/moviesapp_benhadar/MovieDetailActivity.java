package com.example.moviesapp_benhadar;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MovieDetailActivity"; 
    private TextView descriptionTextView;
    private TextView Name;
    private ImageView img;
    private String trailerKey;
    private RequestQueue requestQueue;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        descriptionTextView = findViewById(R.id.Details);
        img = findViewById(R.id.imageview);
        Name = findViewById(R.id.textName);
        requestQueue = Volley.newRequestQueue(this);

        int movieId = getIntent().getIntExtra("movieId", -1);
        Log.d(TAG, "ID du film reçu : " + movieId);

        if (movieId != -1) {
            fetchMovieDetails(movieId);
        } else {
            Toast.makeText(this, "Erreur : ID du film manquant", Toast.LENGTH_SHORT).show();
        }

        Button playButton = findViewById(R.id.playButton);
        if (playButton != null) {
            playButton.setOnClickListener(v -> playTrailer());
        }

        // Initialisation de la carte commentée car l'ID 'map' est commenté dans le XML
        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        */
    }

    private void fetchMovieDetails(int movieId) {
        String TMDB_API_KEY = "6658d71c055170858336028dd17ae52c";
        String movieDetailsUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + TMDB_API_KEY;
        String movieVideosUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + TMDB_API_KEY;

        JsonObjectRequest movieDetailsRequest = new JsonObjectRequest(Request.Method.GET, movieDetailsUrl, null,
                response -> {
                    try {
                        String movieName = response.getString("title");
                        String movieDescription = response.getString("overview");
                        String imageUrl = "https://image.tmdb.org/t/p/w500" + response.getString("poster_path");

                        if (Name != null) Name.setText(movieName);
                        if (descriptionTextView != null) descriptionTextView.setText(movieDescription);
                        if (img != null && !isFinishing()) {
                            Glide.with(MovieDetailActivity.this).load(imageUrl).into(img);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Erreur JSON détails", e);
                    }
                }, error -> Log.e(TAG, "Erreur Volley détails : " + error.getMessage()));

        JsonObjectRequest movieVideosRequest = new JsonObjectRequest(Request.Method.GET, movieVideosUrl, null,
                response -> {
                    try {
                        if (response.has("results")) {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject video = results.getJSONObject(i);
                                if (video.getString("type").equalsIgnoreCase("Trailer")) {
                                    trailerKey = video.getString("key");
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Erreur JSON videos", e);
                    }
                }, error -> Log.e(TAG, "Erreur Volley videos"));

        requestQueue.add(movieDetailsRequest);
        requestQueue.add(movieVideosRequest);
    }

    private void playTrailer() {
        if (trailerKey != null && !trailerKey.isEmpty()) {
            Intent intent = new Intent(this, VideoPlayer.class);
            intent.putExtra("videoUrl", "https://www.youtube.com/embed/" + trailerKey);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Bande-annonce non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng cinemaLocation = new LatLng(33.596460, -7.615480);
        mMap.addMarker(new MarkerOptions().position(cinemaLocation).title("Cinéma"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cinemaLocation, 15));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
