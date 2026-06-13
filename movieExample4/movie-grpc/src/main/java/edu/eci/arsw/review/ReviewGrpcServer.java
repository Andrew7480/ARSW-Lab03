package edu.eci.arsw.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ReviewGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new ReviewServiceImpl())
                .build();
        server.start();
        System.out.println("Review gRPC Service iniciado en puerto 50052");
        server.awaitTermination();
    }

    static class ReviewServiceImpl extends ReviewServiceGrpc.ReviewServiceImplBase {
        private Map<Integer, List<Review>> reviews = new HashMap<>();

        public ReviewServiceImpl() {
            reviews.put(1, List.of(Review.newBuilder()
                    .setAuthor("Juan")
                    .setComment("Excelente película")
                    .setRating(5)
                    .build(),
                    Review.newBuilder()
                            .setAuthor("Ana")
                            .setComment("Muy buena")
                            .setRating(4)
                            .build()));

            reviews.put(2, List.of(Review.newBuilder()
                    .setAuthor("Pedro")
                    .setComment("Un clásico")
                    .setRating(5)
                    .build()));
        }

        @Override
        public void getReviews(ReviewRequest request, StreamObserver<ReviewList> responseObserver) {
            ReviewList.Builder response = ReviewList.newBuilder();
            List<Review> movieReviews = reviews.get(request.getMovieId());
            if (movieReviews != null) {
                response.addAllReviews(movieReviews);
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }
}