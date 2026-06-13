package edu.eci.arsw.welfare.appointment;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppointmentGrpcServer1 {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new AppointmentServiceImpl())
                .build();
        server.start();
        System.out.println("Appointment gRPC Server iniciado en puerto 50051");
        server.awaitTermination();
    }

    static class AppointmentServiceImpl extends AppointmentServiceGrpc.AppointmentServiceImplBase {
        private final Map<String, AppointmentData> appointments = new HashMap<>();

        public AppointmentServiceImpl() {
            AppointmentData sampleAppointment = new AppointmentData(
                    "1001",
                    ServiceType.DENTISTRY,
                    "2024-07-01T10:00:00",
                    Status.REQUESTED);
            appointments.put(sampleAppointment.id, sampleAppointment);
            AppointmentData sampleAppointment2 = new AppointmentData(
                    "1001",
                    ServiceType.PSYCHOLOGY,
                    "2024-07-02T14:00:00",
                    Status.REQUESTED);
            appointments.put(sampleAppointment2.id, sampleAppointment2);
        }

        @Override
        public void requestAppointment(AppointmentRequest request,
                StreamObserver<AppointmentResponse> responseObserver) {
            AppointmentData appointment = new AppointmentData(
                    request.getStudentId(),
                    request.getServiceType(),
                    request.getDate(),
                    Status.REQUESTED);
            appointments.put(appointment.id, appointment);
            responseObserver.onNext(appointment.toResponse());
            responseObserver.onCompleted();
        }

        @Override
        public void cancelAppointment(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
            String appointmentId = request.getAppointmentId();
            if (appointments.containsKey(appointmentId)) {
                appointments.get(appointmentId).status = Status.CANCELLED;
                CancelResponse response = CancelResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Cita cancelada exitosamente")
                        .build();
                responseObserver.onNext(response);
            } else {
                CancelResponse response = CancelResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Cita no encontrada")
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }

        @Override
        public void getAppointments(StudentRequest request, StreamObserver<AppointmentList> responseObserver) {
            AppointmentList.Builder listBuilder = AppointmentList.newBuilder();
            for (AppointmentData appointment : appointments.values()) {
                if (appointment.studentId.equals(request.getStudentId()) && appointment.status != Status.CANCELLED) {
                    Appointment response = Appointment.newBuilder()
                            .setAppointmentId(appointment.id)
                            .setStudentId(appointment.studentId)
                            .setServiceType(appointment.serviceType)
                            .setDate(appointment.date)
                            .setStatus(appointment.status)
                            .build();
                    listBuilder.addAppointments(response);
                }
            }
            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        }
    }

    private static class AppointmentData {

        String id;
        String studentId;
        ServiceType serviceType;
        String date;
        Status status;

        AppointmentData(
                String studentId,
                ServiceType serviceType,
                String date,
                Status status) {

            this.id = UUID.randomUUID().toString();
            this.studentId = studentId;
            this.serviceType = serviceType;
            this.date = date;
            this.status = status;
        }

        public AppointmentResponse toResponse() {
            return AppointmentResponse.newBuilder()
                    .setAppointmentId(id)
                    .setStudentId(studentId)
                    .setServiceType(serviceType)
                    .setDate(date)
                    .setStatus(status)
                    .setSuccess(true)
                    .build();
        }
    }
}
