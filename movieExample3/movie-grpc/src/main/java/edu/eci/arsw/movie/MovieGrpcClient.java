package edu.eci.arsw.movie;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MovieGrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        MovieServiceGrpc.MovieServiceBlockingStub stub = MovieServiceGrpc.newBlockingStub(channel);
        MovieRequest request = MovieRequest.newBuilder()
                .setId(1)
                .build();
        MovieResponse response = stub.getMovie(request);
        if (response.getFound()) {
            System.out.println("Película: " + response.getTitle()
                    + " - " + response.getDirector()
                    + " - " + response.getYear());
        } else {
            System.out.println("Película no encontrada");
        }
        channel.shutdown();
    }
}
