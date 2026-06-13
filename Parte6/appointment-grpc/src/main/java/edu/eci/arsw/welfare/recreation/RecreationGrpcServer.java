package edu.eci.arsw.welfare.recreation;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecreationGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50054)
                .addService(new RecreationServiceImpl())
                .build();
        server.start();
        System.out.println("Recreation Service iniciado en puerto 50054");
        server.awaitTermination();
    }

    static class RecreationServiceImpl extends RecreationServiceGrpc.RecreationServiceImplBase {
        private final Map<String, RecreationData> reservations = new HashMap<>();
        public RecreationServiceImpl() {

            RecreationData reservation = new RecreationData("1001", "Ping Pong Table");
            reservations.put(reservation.reservationId, reservation);
        }

        @Override
        public void reserveResource(RecreationReservationRequest request, StreamObserver<RecreationReservationResponse> responseObserver) {
            RecreationData reservation = new RecreationData(request.getStudentId(), request.getResource());
            reservations.put(reservation.reservationId, reservation);
            RecreationReservationResponse response = RecreationReservationResponse.newBuilder()
                    .setSuccess(true)
                    .setReservationId(reservation.reservationId)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getRecreationReservations(RecreationStudentRequest request, StreamObserver<RecreationReservationList> responseObserver) {
            RecreationReservationList.Builder response = RecreationReservationList.newBuilder();
            for (RecreationData reservation : reservations.values()) {
                if (reservation.studentId.equals(request.getStudentId())) {
                    response.addReservations(reservation.toReservation());
                }
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    private static class RecreationData {
        String reservationId;
        String studentId;
        String resource;

        RecreationData(String studentId, String resource) {
            this.reservationId = UUID.randomUUID().toString();
            this.studentId = studentId;
            this.resource = resource;
        }

        public RecreationReservation toReservation() {
            return RecreationReservation.newBuilder()
                    .setReservationId(reservationId)
                    .setStudentId(studentId)
                    .setResource(resource)
                    .build();
        }
    }
}