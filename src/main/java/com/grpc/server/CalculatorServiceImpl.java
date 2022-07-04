package com.grpc.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNumber())
                .build();

        responseObserver.onNext(sumResponse);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {

        Integer number = request.getNumber();
        Integer divisor = 2;

        while (number > 1) {
            if (number % divisor == 0) {
                number /= divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());
            } else {
                ++divisor;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputerAverageRequest> computerAverage(StreamObserver<ComputerAverageResponse> responseObserver) {

        StreamObserver<ComputerAverageRequest> requestObserver = new StreamObserver<ComputerAverageRequest>() {
            int sum = 0, count = 0;

            @Override
            public void onNext(ComputerAverageRequest computerAverageRequest) {
                sum += computerAverageRequest.getNumber();
                ++count;
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                double average = (double) sum / count;
                responseObserver.onNext(
                        ComputerAverageResponse.newBuilder()
                                .setAverage(average)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
