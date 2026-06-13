package edu.eci.arsw.client;

import edu.eci.arsw.movie.MovieRequest;
import edu.eci.arsw.movie.MovieResponse;
import edu.eci.arsw.movie.MovieServiceGrpc;
import edu.eci.arsw.recommendation.RecommendationList;
import edu.eci.arsw.recommendation.RecommendationRequest;
import edu.eci.arsw.recommendation.RecommendationServiceGrpc;
import edu.eci.arsw.review.ReviewList;
import edu.eci.arsw.review.ReviewRequest;
import edu.eci.arsw.review.ReviewServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MovieGrpcClient {
    public static void main(String[] args) {
        ManagedChannel movieChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ManagedChannel reviewChannel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        ManagedChannel recommendationChannel = ManagedChannelBuilder
                .forAddress("localhost", 50053)
                .usePlaintext()
                .build();

        MovieServiceGrpc.MovieServiceBlockingStub movieStub = MovieServiceGrpc.newBlockingStub(movieChannel);
        ReviewServiceGrpc.ReviewServiceBlockingStub reviewStub = ReviewServiceGrpc.newBlockingStub(reviewChannel);
        RecommendationServiceGrpc.RecommendationServiceBlockingStub recommendationStub = RecommendationServiceGrpc.newBlockingStub(recommendationChannel);

        MovieRequest request = MovieRequest.newBuilder()
                .setId(1)
                .build();
        MovieResponse response = movieStub.getMovie(request);
        if (response.getFound()) {
            System.out.println("Película: " + response.getTitle()
                    + " - " + response.getDirector()
                    + " - " + response.getYear());
        } else {
            System.out.println("Película no encontrada");
        }

        ReviewList reviews = reviewStub.getReviews(
                ReviewRequest.newBuilder()
                        .setMovieId(1)
                        .build());

        System.out.println("Reseñas:");
        reviews.getReviewsList().forEach(review -> {
            System.out.println(
                    "- " + review.getAuthor() + ": " + review.getComment() + " (" + review.getRating() + " estrellas)");
        });

        RecommendationList recommendations = recommendationStub.getRecommendations(
                RecommendationRequest.newBuilder()
                        .setMovieId(1)
                        .build());
        System.out.println("Recomendaciones:");
        recommendations.getTitlesList().forEach(title -> {
            System.out.println("- " + title);
        });

        movieChannel.shutdown();
        reviewChannel.shutdown();
        recommendationChannel.shutdown();
    }
}
