package com.github.tadayosi.tensorflow.serving.client;

import java.util.Optional;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import tensorflow.serving.Classification;
import tensorflow.serving.GetModelMetadata;
import tensorflow.serving.GetModelStatus;
import tensorflow.serving.ModelServiceGrpc;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;
import tensorflow.serving.RegressionOuterClass;

public class TensorFlowServingClient implements TensorFlowServingApi, AutoCloseable {

    private static final String DEFAULT_TARGET = "localhost:8500";

    private final ManagedChannel channel;
    private final ModelServiceGrpc.ModelServiceBlockingStub modelService;
    private final PredictionServiceGrpc.PredictionServiceBlockingStub predictionService;

    private TensorFlowServingClient(String target, ChannelCredentials credentials) {
        this.channel = Grpc.newChannelBuilder(target, credentials).build();
        this.modelService = ModelServiceGrpc.newBlockingStub(channel);
        this.predictionService = PredictionServiceGrpc.newBlockingStub(channel);
    }

    public static TensorFlowServingClient newInstance() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() {
        channel.shutdown();
    }

    @Override
    public GetModelStatus.GetModelStatusResponse getModelStatus(GetModelStatus.GetModelStatusRequest request) {
        return modelService.getModelStatus(request);
    }

    @Override
    public GetModelMetadata.GetModelMetadataResponse getModelMetadata(
        GetModelMetadata.GetModelMetadataRequest request) {
        return predictionService.getModelMetadata(request);
    }

    @Override
    public Classification.ClassificationResponse classify(Classification.ClassificationRequest request) {
        return predictionService.classify(request);
    }

    @Override
    public RegressionOuterClass.RegressionResponse regress(RegressionOuterClass.RegressionRequest request) {
        return predictionService.regress(request);
    }

    @Override
    public Predict.PredictResponse predict(Predict.PredictRequest request) {
        return predictionService.predict(request);
    }

    public static class Builder {

        private final Configuration configuration = Configuration.load();

        private Optional<String> target = configuration.getTarget();
        private Optional<ChannelCredentials> credentials = configuration.getCredentials();

        public Builder target(String target) {
            this.target = Optional.of(target);
            return this;
        }

        public Builder credentials(ChannelCredentials credentials) {
            this.credentials = Optional.of(credentials);
            return this;
        }

        public TensorFlowServingClient build() {
            return new TensorFlowServingClient(
                target.orElse(DEFAULT_TARGET),
                credentials.orElse(InsecureChannelCredentials.create()));
        }
    }
}
