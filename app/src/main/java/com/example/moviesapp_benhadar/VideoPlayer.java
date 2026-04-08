package com.example.moviesapp_benhadar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayer extends AppCompatActivity {
    private WebView webView;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_item_list); // Using the layout that contains WebView

        // Get the video URL from the intent
        videoUrl = getIntent().getStringExtra("videoUrl");

        // Initialize the WebView
        webView = findViewById(R.id.webView);
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            // Load the video URL in the WebView
            webView.loadUrl(videoUrl);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Adjust the layout or configuration of your WebView here
        if (webView != null && videoUrl != null) {
            webView.loadUrl(videoUrl);
        }
    }
}
