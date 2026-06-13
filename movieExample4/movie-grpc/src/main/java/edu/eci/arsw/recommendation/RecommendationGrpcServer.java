package edu.eci.arsw.recommendation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class RecommendationGrpcServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50053)
                .addService(new RecommendationServiceImpl())
                .build();
        server.start();
        System.out.println("Recommendation gRPC Service iniciado en puerto 50053");
        server.awaitTermination();
    }

    static class RecommendationServiceImpl extends RecommendationServiceGrpc.RecommendationServiceImplBase {
        private Map<Integer, List<String>> recommendations = new HashMap<>();

        public RecommendationServiceImpl() {
            recommendations.put(1, List.of("Inception", "Tenet", "The Prestige"));
            recommendations.put(2, List.of("John Wick", "Blade Runner", "Terminator"));
            recommendations.put(3, List.of("Interstellar", "Memento", "Tenet"));
        }

        @Override
        public void getRecommendations(RecommendationRequest request, StreamObserver<RecommendationList> responseObserver) {
            RecommendationList response = RecommendationList.newBuilder()
                    .addAllTitles(recommendations.getOrDefault(request.getMovieId(), List.of()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}