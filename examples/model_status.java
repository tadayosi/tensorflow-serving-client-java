///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17+
//DEPS io.github.tadayosi.tensorflow:tensorflow-serving-client:0.2.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import io.github.tadayosi.tensorflow.serving.client.TensorFlowServingClient;
import com.google.protobuf.Int64Value;

import tensorflow.serving.GetModelStatus.GetModelStatusRequest;
import tensorflow.serving.Model.ModelSpec;

public class model_status {

    public static void main(String... args) throws Exception {
        try (var client = TensorFlowServingClient.newInstance()) {
            var request = GetModelStatusRequest.newBuilder()
                    .setModelSpec(ModelSpec.newBuilder()
                            .setName("half_plus_two")
                            .setVersion(Int64Value.of(123)))
                    .build();
            var response = client.getModelStatus(request);
            System.out.println("Response>");
            System.out.println(response);
        }
    }
}
