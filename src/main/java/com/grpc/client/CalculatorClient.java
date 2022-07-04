package com.grpc.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        //unary connection
        //calculatorSum(channel);

        //server stream
//        calculatorPrimeNumberDecomposition(channel);

        //client stream
        calculateAverage(channel);

        channel.shutdown();
    }

    private static void calculateAverage(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputerAverageRequest> requestStreamObserver = asyncClient.computerAverage(new StreamObserver<ComputerAverageResponse>() {
            @Override
            public void onNext(ComputerAverageResponse computerAverageResponse) {
                System.out.println("Received a response from the server");
                System.out.println(computerAverageResponse.getAverage());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us data");
                latch.countDown();
            }
        });

        for (int value = 1; value < 50; value++) {
            requestStreamObserver.onNext(ComputerAverageRequest.newBuilder()
                    .setNumber(value)
                    .build());
        }

        requestStreamObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void calculatorPrimeNumberDecomposition(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        Integer number = 10000000;
        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                        .setNumber(number)
                        .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });
    }

    private static void calculatorSum(ManagedChannel channel) {

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest request = SumRequest.newBuilder()
                .setFirstNumber(10)
                .setSecondNumber(34)
                .build();
        SumResponse response = stub.sum(request);

        System.out.println(request.getFirstNumber() + " + " + request.getSecondNumber() + " = " + response.getSumResult());

    }
}
