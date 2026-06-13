package edu.eci.arsw.gateway;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import edu.eci.arsw.movie.MovieRequest;
import edu.eci.arsw.movie.MovieResponse;
import edu.eci.arsw.movie.MovieServiceGrpc;
import edu.eci.arsw.recommendation.RecommendationList;
import edu.eci.arsw.recommendation.RecommendationRequest;
import edu.eci.arsw.recommendation.RecommendationServiceGrpc;
import edu.eci.arsw.review.Review;
import edu.eci.arsw.review.ReviewList;
import edu.eci.arsw.review.ReviewRequest;
import edu.eci.arsw.review.ReviewServiceGrpc;

public class MovieGateway {
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
        RecommendationServiceGrpc.RecommendationServiceBlockingStub recommendationStub = RecommendationServiceGrpc
                .newBlockingStub(recommendationChannel);

        MovieRequest movieRequest = MovieRequest.newBuilder()
                .setId(1)
                .build();

        MovieResponse movie = movieStub.getMovie(movieRequest);

        ReviewRequest reviewRequest = ReviewRequest.newBuilder()
                .setMovieId(1)
                .build();

        ReviewList reviews = reviewStub.getReviews(reviewRequest);

        RecommendationRequest recommendationRequest = RecommendationRequest.newBuilder()
                .setMovieId(1)
                .build();

        RecommendationList recommendations = recommendationStub.getRecommendations(recommendationRequest);

        System.out.println("Película: " + movie.getTitle());
        System.out.println("Director: " + movie.getDirector());
        System.out.println("Año: " + movie.getYear());

        System.out.println("\nReseñas:");

        for (Review review : reviews.getReviewsList()) {

            System.out.println(
                    "- "
                            + review.getComment()
                            + " Rating: "
                            + review.getRating());
        }

        System.out.println("\nRecomendaciones:");

        for (String title : recommendations.getTitlesList()) {

            System.out.println("- " + title);
        }
    }
}
