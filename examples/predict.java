///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17+
//DEPS io.github.tadayosi.tensorflow:tensorflow-serving-client:0.2.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.TensorShapeProto.Dim;

import io.github.tadayosi.tensorflow.serving.client.TensorFlowServingClient;
import com.google.protobuf.Int64Value;

import tensorflow.serving.Model.ModelSpec;
import tensorflow.serving.Predict.PredictRequest;

public class predict {

    public static void main(String... args) throws Exception {
        try (var client = TensorFlowServingClient.newInstance()) {
            var request = PredictRequest.newBuilder()
                    .setModelSpec(ModelSpec.newBuilder()
                            .setName("half_plus_two")
                            .setVersion(Int64Value.of(123)))
                    .putInputs("x", TensorProto.newBuilder()
                            .setDtype(DataType.DT_FLOAT)
                            .setTensorShape(TensorShapeProto.newBuilder()
                                    .addDim(Dim.newBuilder().setSize(3)))
                            .addFloatVal(1.0f)
                            .addFloatVal(2.0f)
                            .addFloatVal(5.0f)
                            .build())
                    .build();
            var response = client.predict(request);
            System.out.println("Response>");
            System.out.println(response);
        }
    }
}
