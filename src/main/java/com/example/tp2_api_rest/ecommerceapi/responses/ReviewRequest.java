package com.example.tp2_api_rest.ecommerceapi.responses;


public class ReviewRequest {
    private String comment;
    private int rating;

    // Constructeurs
    public ReviewRequest() {}

    public ReviewRequest(String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
    }

    // Getters et Setters
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
