package edu.eci.arsw.welfare.medical;

import java.util.HashMap;
import java.util.Map;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;


public class MedicalGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new MedicalServiceImpl())
                .build();
        server.start();
        System.out.println("Medical gRPC Server iniciado en puerto 50052");
        server.awaitTermination();
    }

    static class MedicalServiceImpl extends MedicalServiceGrpc.MedicalServiceImplBase {
        private final Map<Integer, Specialty> specialties = new HashMap<>();

        public MedicalServiceImpl() {
            specialties.put(1, Specialty.newBuilder()
                    .setId(1)
                    .setName("General Medicine")
                    .build());

            specialties.put(2, Specialty.newBuilder()
                    .setId(2)
                    .setName("Psychology")
                    .build());

            specialties.put(3, Specialty.newBuilder()
                    .setId(3)
                    .setName("Dentistry")
                    .build());
        }

        @Override
        public void getSpecialties(MedicalRequest request, StreamObserver<SpecialtyList> responseObserver) {
            SpecialtyList.Builder response = SpecialtyList.newBuilder();
            response.addAllSpecialties(specialties.values());
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

}
