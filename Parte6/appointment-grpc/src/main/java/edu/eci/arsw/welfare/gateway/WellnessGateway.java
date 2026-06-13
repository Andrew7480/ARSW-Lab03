package edu.eci.arsw.welfare.gateway;

import edu.eci.arsw.welfare.appointment.*;
import edu.eci.arsw.welfare.medical.*;
import edu.eci.arsw.welfare.gym.*;
import edu.eci.arsw.welfare.recreation.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class WellnessGateway {

    private static final String HOST = "localhost";

    private final ManagedChannel appointmentChannel;
    private final ManagedChannel medicalChannel;
    private final ManagedChannel gymChannel;
    private final ManagedChannel recreationChannel;

    private final AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub;
    private final MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub;
    private final GymServiceGrpc.GymServiceBlockingStub gymStub;
    private final RecreationServiceGrpc.RecreationServiceBlockingStub recreationStub;

    public WellnessGateway() {
        appointmentChannel = ManagedChannelBuilder.forAddress(HOST, 50051).usePlaintext().build();
        medicalChannel    = ManagedChannelBuilder.forAddress(HOST, 50052).usePlaintext().build();
        gymChannel        = ManagedChannelBuilder.forAddress(HOST, 50053).usePlaintext().build();
        recreationChannel = ManagedChannelBuilder.forAddress(HOST, 50054).usePlaintext().build();

        appointmentStub = AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        medicalStub     = MedicalServiceGrpc.newBlockingStub(medicalChannel);
        gymStub         = GymServiceGrpc.newBlockingStub(gymChannel);
        recreationStub  = RecreationServiceGrpc.newBlockingStub(recreationChannel);
    }

    public AppointmentResponse requestAppointment(String studentId, ServiceType serviceType, String date) {
        try {
            AppointmentRequest request = AppointmentRequest.newBuilder()
                    .setStudentId(studentId)
                    .setServiceType(serviceType)
                    .setDate(date)
                    .build();
            return appointmentStub.requestAppointment(request);
        } catch (StatusRuntimeException e) {
            System.out.println("AppointmentService no disponible: " + e.getStatus());
            return null;
        }
    }

    public GymReservationResponse reserveGymSession(String studentId, String timeSlot) {
        try {
            GymReservationRequest request = GymReservationRequest.newBuilder()
                    .setStudentId(studentId)
                    .setHour(timeSlot)
                    .build();
            return gymStub.reserveSession(request);
        } catch (StatusRuntimeException e) {
            System.out.println("GymService no disponible: " + e.getStatus());
            return null;
        }
    }

    public RecreationReservationResponse reserveRecreationResource(String studentId, String resourceId) {
        try {
            RecreationReservationRequest request = RecreationReservationRequest.newBuilder()
                    .setStudentId(studentId)
                    .setResource(resourceId)
                    .build();
            return recreationStub.reserveResource(request);
        } catch (StatusRuntimeException e) {
            System.out.println("RecreationService no disponible: " + e.getStatus());
            return null;
        }
    }

    public void getStudentWellnessSummary(String studentId) {
        // Cada servicio se consulta de forma independiente:
        // si uno falla, el resumen continúa con los datos disponibles.
        AppointmentList appointments;
        try {
            appointments = appointmentStub.getAppointments(
                    StudentRequest.newBuilder().setStudentId(studentId).build());
        } catch (StatusRuntimeException e) {
            System.out.println("[AppointmentService no disponible: " + e.getStatus() + "]");
            appointments = AppointmentList.getDefaultInstance();
        }

        GymReservationList gymReservations;
        try {
            gymReservations = gymStub.getGymReservations(
                    GymStudentRequest.newBuilder().setStudentId(studentId).build());
        } catch (StatusRuntimeException e) {
            System.out.println("[GymService no disponible: " + e.getStatus() + "]");
            gymReservations = GymReservationList.getDefaultInstance();
        }

        RecreationReservationList recreationReservations;
        try {
            recreationReservations = recreationStub.getRecreationReservations(
                    RecreationStudentRequest.newBuilder().setStudentId(studentId).build());
        } catch (StatusRuntimeException e) {
            System.out.println("[RecreationService no disponible: " + e.getStatus() + "]");
            recreationReservations = RecreationReservationList.getDefaultInstance();
        }

        SpecialtyList specialties;
        try {
            specialties = medicalStub.getSpecialties(MedicalRequest.newBuilder().build());
        } catch (StatusRuntimeException e) {
            System.out.println("[MedicalService no disponible: " + e.getStatus() + "]");
            specialties = SpecialtyList.getDefaultInstance();
        }

        System.out.println("\n==============================");
        System.out.println("WELLNESS SUMMARY");
        System.out.println("==============================");
        System.out.println("\nESTUDIANTE: " + studentId);

        System.out.println("\nCITAS:");
        for (Appointment appointment : appointments.getAppointmentsList()) {
            System.out.println(
                    appointment.getServiceType()
                            + " | " + appointment.getDate()
                            + " | " + appointment.getStatus());
        }

        System.out.println("\nESPECIALIDADES DISPONIBLES:");
        for (Specialty specialty : specialties.getSpecialtiesList()) {
            System.out.println("- " + specialty.getName());
        }

        System.out.println("\nRESERVAS GYM:");
        for (GymReservation reservation : gymReservations.getReservationsList()) {
            System.out.println("- " + reservation.getDate() + " " + reservation.getHour());
        }

        System.out.println("\nRECURSOS RECREATIVOS:");
        for (RecreationReservation reservation : recreationReservations.getReservationsList()) {
            System.out.println("- " + reservation.getResource());
        }
    }

    public void shutdown() {
        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        gymChannel.shutdown();
        recreationChannel.shutdown();
    }

    public static void main(String[] args) {
        WellnessGateway gateway = new WellnessGateway();

        AppointmentResponse appointment = gateway.requestAppointment("1001", ServiceType.MEDICINE, "2026-06-20");
        System.out.println("=== CITA CREADA ===");
        if (appointment != null) {
            System.out.println("ID: " + appointment.getAppointmentId());
        }

        GymReservationResponse gymReservation = gateway.reserveGymSession("1001", "08:00");
        System.out.println("\n=== GYM ===");
        if (gymReservation != null) {
            System.out.println("Reserva ID: " + gymReservation.getReservationId());
        }

        RecreationReservationResponse recreationReservation = gateway.reserveRecreationResource("1001", "Ping Pong Table");
        System.out.println("\n=== RECREACIÓN ===");
        if (recreationReservation != null) {
            System.out.println("Reserva ID: " + recreationReservation.getReservationId());
        }

        gateway.getStudentWellnessSummary("1001");
        gateway.shutdown();
    }
}
