package com.example.moviesapp_benhadar;

public class MyMovieData {
    private String movieName;
    private String movieDate;
    private String movieImage;
    private String movieDescription;
    private int movieId;

    public MyMovieData(int movieId, String movieName, String movieDate, String movieImage) {
        this.movieName = movieName;
        this.movieDate = movieDate;
        this.movieImage = movieImage;
        this.movieId = movieId;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public String getMovieDescription() {
        return movieDescription;
    }
}
