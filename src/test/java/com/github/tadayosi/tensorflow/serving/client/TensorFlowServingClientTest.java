package com.github.tadayosi.tensorflow.serving.client;

import com.google.protobuf.Int64Value;
import org.junit.jupiter.api.Test;
import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.FloatList;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.testcontainers.junit.jupiter.Testcontainers;
import tensorflow.serving.Classification;
import tensorflow.serving.GetModelMetadata;
import tensorflow.serving.GetModelStatus;
import tensorflow.serving.InputOuterClass;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;
import tensorflow.serving.RegressionOuterClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void testModelMetadata() {
        var request = GetModelMetadata.GetModelMetadataRequest.newBuilder()
            .setModelSpec(Model.ModelSpec.newBuilder()
                .setName(DEFAULT_MODEL)
                .setVersion(Int64Value.of(DEFAULT_MODEL_VERSION)))
            .addMetadataField("signature_def")
            .build();
        var response = client.getModelMetadata(request);

        assertEquals(1, response.getMetadataCount());
        var modelSpec = response.getModelSpec();
        assertEquals(DEFAULT_MODEL, modelSpec.getName());
        assertEquals(DEFAULT_MODEL_VERSION, modelSpec.getVersion().getValue());
        var metadata = response.getMetadataOrThrow("signature_def");
        assertNotNull(metadata);
    }

    @Test
    void testClassify() {
        var request = Classification.ClassificationRequest.newBuilder()
            .setModelSpec(Model.ModelSpec.newBuilder()
                .setName(DEFAULT_MODEL)
                .setVersion(Int64Value.of(DEFAULT_MODEL_VERSION))
                .setSignatureName("classify_x_to_y"))
            .setInput(InputOuterClass.Input.newBuilder()
                .setExampleList(InputOuterClass.ExampleList.newBuilder()
                    .addExamples(Example.newBuilder()
                        .setFeatures(Features.newBuilder()
                            .putFeature("x", Feature.newBuilder()
                                .setFloatList(FloatList.newBuilder().addValue(1.0f))
                                .build())))))
            .build();
        var response = client.classify(request);

        assertTrue(response.hasResult());
        var result = response.getResult();
        assertEquals(1, result.getClassificationsCount());
        assertEquals(1, result.getClassifications(0).getClassesCount());
        assertEquals(2.5f, result.getClassifications(0).getClasses(0).getScore(), 0.0001);
    }

    @Test
    void testRegress() {
        var request = RegressionOuterClass.RegressionRequest.newBuilder()
            .setModelSpec(Model.ModelSpec.newBuilder()
                .setName(DEFAULT_MODEL)
                .setVersion(Int64Value.of(DEFAULT_MODEL_VERSION))
                .setSignatureName("regress_x_to_y"))
            .setInput(InputOuterClass.Input.newBuilder()
                .setExampleList(InputOuterClass.ExampleList.newBuilder()
                    .addExamples(Example.newBuilder()
                        .setFeatures(Features.newBuilder()
                            .putFeature("x", Feature.newBuilder()
                                .setFloatList(FloatList.newBuilder().addValue(1.0f))
                                .build())))))
            .build();
        var response = client.regress(request);

        assertTrue(response.hasResult());
        var result = response.getResult();
        assertEquals(1, result.getRegressionsCount());
        assertEquals(2.5f, result.getRegressions(0).getValue(), 0.0001);
    }

    @Test
    void testPredict() {
        var request = Predict.PredictRequest.newBuilder()
            .setModelSpec(Model.ModelSpec.newBuilder()
                .setName(DEFAULT_MODEL)
                .setVersion(Int64Value.of(DEFAULT_MODEL_VERSION)))
            .putInputs("x", TensorProto.newBuilder()
                .setDtype(DataType.DT_FLOAT)
                .setTensorShape(TensorShapeProto.newBuilder()
                    .addDim(TensorShapeProto.Dim.newBuilder().setSize(3)))
                .addFloatVal(1.0f)
                .addFloatVal(2.0f)
                .addFloatVal(5.0f)
                .build())
            .build();
        var response = client.predict(request);

        assertEquals(1, response.getOutputsCount());
        var output = response.getOutputsOrThrow("y");
        assertEquals(3, output.getTensorShape().getDim(0).getSize());
        assertEquals(2.5f, output.getFloatVal(0), 0.0001);
        assertEquals(3.0f, output.getFloatVal(1), 0.0001);
        assertEquals(4.5f, output.getFloatVal(2), 0.0001);
    }

}
