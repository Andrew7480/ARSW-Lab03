package edu.eci.arsw.welfare;

import edu.eci.arsw.welfare.appointment.*;
import edu.eci.arsw.welfare.medical.*;
import edu.eci.arsw.welfare.gym.*;
import edu.eci.arsw.welfare.recreation.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WelfareClient {
    public static void main(String[] args) {
        ManagedChannel appointmentChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ManagedChannel medicalChannel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        ManagedChannel gymChannel = ManagedChannelBuilder
                .forAddress("localhost", 50053)
                .usePlaintext()
                .build();

        ManagedChannel recreationChannel = ManagedChannelBuilder
                .forAddress("localhost", 50054)
                .usePlaintext()
                .build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub = AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub = MedicalServiceGrpc.newBlockingStub(medicalChannel);
        GymServiceGrpc.GymServiceBlockingStub gymStub = GymServiceGrpc.newBlockingStub(gymChannel);
        RecreationServiceGrpc.RecreationServiceBlockingStub recreationStub = RecreationServiceGrpc.newBlockingStub(recreationChannel);

        System.out.println("========== APPOINTMENTS ==========");

        StudentRequest appointmentRequest = StudentRequest.newBuilder()
                .setStudentId("1001")
                .build();

        AppointmentList appointments = appointmentStub.getAppointments(appointmentRequest);

        for (Appointment appointment : appointments.getAppointmentsList()) {
            System.out.println(
                    appointment.getServiceType()
                            + " | "
                            + appointment.getDate()
                            + " | "
                            + appointment.getStatus());
        }

        System.out.println("\n========== SPECIALTIES ==========");

        SpecialtyList specialties = medicalStub.getSpecialties(MedicalRequest.newBuilder().build());

        for (Specialty specialty : specialties.getSpecialtiesList()) {
            System.out.println(
                    specialty.getId()
                            + " - "
                            + specialty.getName());
        }

        System.out.println("\n========== GYM RESERVATIONS ==========");

        GymStudentRequest gymRequest = GymStudentRequest.newBuilder()
                .setStudentId("1001")
                .build();

        GymReservationList gymReservations = gymStub.getGymReservations(gymRequest);

        for (GymReservation reservation :  gymReservations.getReservationsList()) {
            System.out.println(
                    reservation.getDate()
                            + " "
                            + reservation.getHour());
        }

        System.out.println("\n========== RECREATION ==========");

        RecreationStudentRequest recreationRequest = RecreationStudentRequest.newBuilder()
                .setStudentId("1001")
                .build();

        RecreationReservationList recreationReservations = recreationStub.getRecreationReservations(recreationRequest);

        for (RecreationReservation reservation : recreationReservations.getReservationsList()) {
            System.out.println(reservation.getResource());
        }

        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        gymChannel.shutdown();
        recreationChannel.shutdown();
    }
}