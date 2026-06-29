package com.sen.repository;

public class SellerRatingProjection {
    private final double averageScore;
    private final long totalReviews;

    public SellerRatingProjection(double averageScore, long totalReviews) {
        this.averageScore = averageScore;
        this.totalReviews = totalReviews;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public long getTotalReviews() {
        return totalReviews;
    }
}
