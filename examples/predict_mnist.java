///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17+
//DEPS io.github.tadayosi.tensorflow:tensorflow-serving-client:0.2.0
//DEPS org.tensorflow:tensorflow-core-api:1.0.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import java.nio.file.Files;
import java.nio.file.Path;

import org.tensorflow.Graph;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.TensorShapeProto.Dim;
import org.tensorflow.op.OpScope;
import org.tensorflow.op.Ops;
import org.tensorflow.op.Scope;
import org.tensorflow.op.image.DecodeImage;

import io.github.tadayosi.tensorflow.serving.client.TensorFlowServingClient;

import com.google.protobuf.ByteString;
import com.google.protobuf.Int64Value;

import tensorflow.serving.Model.ModelSpec;
import tensorflow.serving.Predict.PredictRequest;

public class predict_mnist {

    public static void main(String... args) throws Exception {
        var input = Files.readAllBytes(Path.of("data/mnist/0/3.png"));
        try (var graph = new Graph()) {
            var tf = Ops.create(graph);
            tf.image.decodeImage(graph.opBuilder(null, null, null), null);
        }

        try (var client = TensorFlowServingClient.newInstance()) {
            var request = PredictRequest.newBuilder()
                    .setModelSpec(ModelSpec.newBuilder()
                            .setName("mnist")
                            .setVersion(Int64Value.of(1)))
                    .putInputs("keras_tensor", TensorProto.newBuilder()
                            .setDtype(DataType.DT_FLOAT)
                            .setTensorShape(TensorShapeProto.newBuilder()
                                    .addDim(Dim.newBuilder().setSize(28))
                                    .addDim(Dim.newBuilder().setSize(28))
                                    .addDim(Dim.newBuilder().setSize(1)))
                            .setTensorContent(ByteString.copyFrom(input))
                            .build())
                    .build();
            var response = client.predict(request);
            System.out.println("Response>");
            System.out.println(response);
        }
    }
}
