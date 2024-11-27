///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17+
//REPOS mavencentral,jitpack=https://jitpack.io
//DEPS com.github.tadayosi:tensorflow-serving-client-java:main-SNAPSHOT
//DEPS org.slf4j:slf4j-simple:2.0.16

import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.FloatList;

import com.github.tadayosi.tensorflow.serving.client.TensorFlowServingClient;
import com.google.protobuf.Int64Value;

import tensorflow.serving.InputOuterClass.ExampleList;
import tensorflow.serving.InputOuterClass.Input;
import tensorflow.serving.Model.ModelSpec;
import tensorflow.serving.RegressionOuterClass.RegressionRequest;

public class regress {

    public static void main(String... args) throws Exception {
        var client = TensorFlowServingClient.newInstance();
        var request = RegressionRequest.newBuilder()
                .setModelSpec(ModelSpec.newBuilder()
                        .setName("half_plus_two")
                        .setVersion(Int64Value.of(123))
                        .setSignatureName("regress_x_to_y"))
                .setInput(Input.newBuilder()
                        .setExampleList(ExampleList.newBuilder()
                                .addExamples(Example.newBuilder()
                                        .setFeatures(Features.newBuilder()
                                                .putFeature("x", Feature.newBuilder()
                                                        .setFloatList(FloatList.newBuilder().addValue(1.0f))
                                                        .build())))))
                .build();
        var response = client.regress(request);
        System.out.println("Response>");
        System.out.println(response);
    }
}
