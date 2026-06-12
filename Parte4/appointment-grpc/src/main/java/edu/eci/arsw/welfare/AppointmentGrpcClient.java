package edu.eci.arsw.welfare;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AppointmentGrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        AppointmentServiceGrpc.AppointmentServiceBlockingStub stub = AppointmentServiceGrpc.newBlockingStub(channel);
        
        // 1. Solicitar cita
        AppointmentRequest request = AppointmentRequest.newBuilder()
                .setStudentId("1001")
                .setServiceType(ServiceType.PSYCHOLOGY)
                .setDate("2026-06-20")
                .build();

        AppointmentResponse appointmentResponse = stub.requestAppointment(request);

        System.out.println("=== CITA CREADA ===");
        System.out.println("ID: " + appointmentResponse.getAppointmentId());
        System.out.println("Estudiante: " + appointmentResponse.getStudentId());
        System.out.println("Servicio: " + appointmentResponse.getServiceType());
        System.out.println("Fecha: " + appointmentResponse.getDate());
        System.out.println("Estado: " + appointmentResponse.getStatus());

        String appointmentId = appointmentResponse.getAppointmentId();

        // 2. Consultar citas activas

        AppointmentRequest request1 = AppointmentRequest.newBuilder()
                .setStudentId("1001")
                .setServiceType(ServiceType.MEDICINE)
                .setDate("2020-08-30")
                .build();

        stub.requestAppointment(request1);


        StudentRequest studentRequest = StudentRequest.newBuilder()
                .setStudentId("1001")
                .build();

        AppointmentList appointmentList = stub.getAppointments(studentRequest);

        System.out.println("\n=== CITAS ACTIVAS ===");

        for (Appointment appointment : appointmentList.getAppointmentsList()) {

            System.out.println(
                    appointment.getAppointmentId()
                            + " | "
                            + appointment.getServiceType()
                            + " | "
                            + appointment.getStatus()
                            + " | "
                            + appointment.getDate());
        }

        // 3. Cancelar cita
        CancelRequest cancelRequest = CancelRequest.newBuilder()
                .setAppointmentId(appointmentId)
                .build();

        CancelResponse cancelResponse = stub.cancelAppointment(cancelRequest);

        System.out.println("\n=== CANCELACIÓN ===");
        System.out.println(cancelResponse.getMessage());

        // 4. Consultar nuevamente
        AppointmentList updatedList = stub.getAppointments(studentRequest);

        System.out.println("\n=== CITAS ACTIVAS DESPUÉS DE CANCELAR ===");

        if (updatedList.getAppointmentsCount() == 0) {

            System.out.println("No hay citas activas.");

        } else {

            for (Appointment appointment : updatedList.getAppointmentsList()) {

                System.out.println(
                        appointment.getAppointmentId()
                                + " | "
                                + appointment.getServiceType()
                                + " | "
                                + appointment.getStatus()
                                + " | "
                                + appointment.getDate());
            }
        }

        channel.shutdown();
    }
}
