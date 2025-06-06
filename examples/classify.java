///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17+
//DEPS io.github.tadayosi.tensorflow:tensorflow-serving-client:0.2.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.FloatList;

import io.github.tadayosi.tensorflow.serving.client.TensorFlowServingClient;
import com.google.protobuf.Int64Value;

import tensorflow.serving.Classification.ClassificationRequest;
import tensorflow.serving.InputOuterClass.ExampleList;
import tensorflow.serving.InputOuterClass.Input;
import tensorflow.serving.Model.ModelSpec;

public class classify {

    public static void main(String... args) throws Exception {
        try (var client = TensorFlowServingClient.newInstance()) {
            var request = ClassificationRequest.newBuilder()
                    .setModelSpec(ModelSpec.newBuilder()
                            .setName("half_plus_two")
                            .setVersion(Int64Value.of(123))
                            .setSignatureName("classify_x_to_y"))
                    .setInput(Input.newBuilder()
                            .setExampleList(ExampleList.newBuilder()
                                    .addExamples(Example.newBuilder()
                                            .setFeatures(Features.newBuilder()
                                                    .putFeature("x", Feature.newBuilder()
                                                            .setFloatList(FloatList.newBuilder().addValue(1.0f))
                                                            .build())))))
                    .build();
            var response = client.classify(request);
            System.out.println("Response>");
            System.out.println(response);
        }
    }
}
