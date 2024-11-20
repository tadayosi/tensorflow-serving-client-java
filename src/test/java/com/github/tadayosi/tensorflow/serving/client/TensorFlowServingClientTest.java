package com.github.tadayosi.tensorflow.serving.client;

import com.google.protobuf.Int64Value;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import tensorflow.serving.GetModelStatus;
import tensorflow.serving.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tensorflow.serving.GetModelStatus.ModelVersionStatus.State.AVAILABLE;

@Testcontainers
class TensorFlowServingClientTest extends TensorFlowServingTestSupport {

    private static final String DEFAULT_MODEL = "half_plus_two";
    private static final long DEFAULT_MODEL_VERSION = 123;

    @Test
    void testGetModelStatus() {
        var request = GetModelStatus.GetModelStatusRequest.newBuilder()
            .setModelSpec(Model.ModelSpec.newBuilder()
                .setName(DEFAULT_MODEL)
                .setVersion(Int64Value.of(DEFAULT_MODEL_VERSION)))
            .build();
        var response = client.getModelStatus(request);

        assertEquals(1, response.getModelVersionStatusCount());
        var modelVersionStatus = response.getModelVersionStatus(0);
        assertEquals(DEFAULT_MODEL_VERSION, modelVersionStatus.getVersion());
        assertEquals(AVAILABLE, modelVersionStatus.getState());
    }

}
