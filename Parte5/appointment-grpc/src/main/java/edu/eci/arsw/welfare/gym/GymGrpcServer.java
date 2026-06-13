package edu.eci.arsw.welfare.gym;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GymGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50053)
                .addService(new GymServiceImpl())
                .build();
        server.start();
        System.out.println("Gym Service iniciado en puerto 50053");
        server.awaitTermination();
    }

    static class GymServiceImpl extends GymServiceGrpc.GymServiceImplBase {
        private final Map<String, GymReservationData> reservations = new HashMap<>();

        public GymServiceImpl() {
            GymReservationData reservation = new GymReservationData("1001", "2026-06-15", "18:00");
            reservations.put(reservation.reservationId, reservation);
        }

        @Override
        public void reserveSession(GymReservationRequest request, StreamObserver<GymReservationResponse> responseObserver) {
            GymReservationData reservation = new GymReservationData(request.getStudentId(), request.getDate(), request.getHour());
            reservations.put(reservation.reservationId, reservation);
            GymReservationResponse response = GymReservationResponse.newBuilder()
                    .setSuccess(true)
                    .setReservationId(reservation.reservationId)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getGymReservations(GymStudentRequest request, StreamObserver<GymReservationList> responseObserver) {
            GymReservationList.Builder response = GymReservationList.newBuilder();
            for (GymReservationData reservation : reservations.values()) {
                if (reservation.studentId.equals(request.getStudentId())) {
                    response.addReservations(reservation.toReservation());
                }
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    private static class GymReservationData {

        String reservationId;
        String studentId;
        String date;
        String hour;

        GymReservationData(String studentId, String date, String hour) {
            this.reservationId = UUID.randomUUID().toString();
            this.studentId = studentId;
            this.date = date;
            this.hour = hour;
        }

        public GymReservation toReservation() {
            return GymReservation.newBuilder()
                    .setReservationId(reservationId)
                    .setStudentId(studentId)
                    .setDate(date)
                    .setHour(hour)
                    .build();
        }
    }
}