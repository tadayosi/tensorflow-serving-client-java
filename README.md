# TensorFlow Serving Client for Java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.tadayosi.tensorflow/tensorflow-serving-client/badge.svg?style=flat)](https://repo1.maven.org/maven2/io/github/tadayosi/tensorflow/tensorflow-serving-client/)
[![Test](https://github.com/tadayosi/tensorflow-serving-client-java/actions/workflows/test.yml/badge.svg)](https://github.com/tadayosi/tensorflow-serving-client-java/actions/workflows/test.yml)

TensorFlow Serving Client for Java (TFSC4J) is a Java client library for [TensorFlow Serving](https://github.com/tensorflow/serving). It supports the following [TensorFlow Serving Client API (gRPC)](https://github.com/tensorflow/serving/tree/master/tensorflow_serving/apis):

- [Model status API](https://www.tensorflow.org/tfx/serving/api_rest#model_status_api)
- [Model Metadata API](https://www.tensorflow.org/tfx/serving/api_rest#model_metadata_api)
- [Classify and Regress API](https://www.tensorflow.org/tfx/serving/api_rest#classify_and_regress_api)
- [Predict API](https://www.tensorflow.org/tfx/serving/api_rest#predict_api)

## Requirements

- Java 17+

## Install


Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.tadayosi.tensorflow</groupId>
    <artifactId>tensorflow-serving-client</artifactId>
    <version>0.2.1</version>
</dependency>
```

## Usage

> [!IMPORTANT]
> TFSC4J uses the gRPC port (default: `8500`) to communicate with the TensorFlow model server.

To creat a client:

```java
TensorFlowServingClient client = TensorFlowServingClient.newInstance();
```

By default, the client connects to `localhost:8500`, but if you want to connect to a different target URI (e.g. `example.com:8080`), instantiate a client as follows:

```java
TensorFlowServingClient client = TensorFlowServingClient.builder()
    .target("example.com:8080")
    .build();
```

### Model status API

To get the status of a model:

```java
try (TensorFlowServingClient client = TensorFlowServingClient.newInstance()) {
    GetModelStatusRequest request = GetModelStatusRequest.newBuilder()
        .setModelSpec(ModelSpec.newBuilder()
            .setName("half_plus_two")
            .setVersion(Int64Value.of(123)))
        .build();
    GetModelStatusResponse response = client.getModelStatus(request);
    System.out.println(response);
}
```

Output:

```console
model_version_status {
  version: 123
  state: AVAILABLE
  status {
  }
}
```

### Model Metadata API

To get the metadata of a model:

```java
try (TensorFlowServingClient client = TensorFlowServingClient.newInstance()) {
    GetModelMetadataRequest request = GetModelMetadataRequest.newBuilder()
        .setModelSpec(ModelSpec.newBuilder()
            .setName("half_plus_two")
            .setVersion(Int64Value.of(123)))
        .addMetadataField("signature_def")) // metadata_field is mandatory
        .build();
    GetModelMetadataResponse response = client.getModelMetadata(request);
    System.out.println(response);
}
```

Output:

```console
model_spec {
  name: "half_plus_two"
  version {
    value: 123
  }
}
metadata {
  key: "signature_def"
  value {
    type_url: "type.googleapis.com/tensorflow.serving.SignatureDefMap"
    value: "..."
  }
}
```

### Classify API

To classify:

```java
try (TensorFlowServingClient client = TensorFlowServingClient.newInstance()) {
    ClassificationRequest request = ClassificationRequest.newBuilder()
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
    ClassificationResponse response = client.classify(request);
    System.out.println(response);
}
```

Output:

```console
result {
  classifications {
    classes {
      score: 2.5
    }
  }
}
model_spec {
  name: "half_plus_two"
  version {
    value: 123
  }
  signature_name: "classify_x_to_y"
}
```

### Regress API

To regress:

```java
try (TensorFlowServingClient client = TensorFlowServingClient.newInstance()) {
    RegressionRequest request = RegressionRequest.newBuilder()
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
    RegressionResponse response = client.regress(request);
    System.out.println(response);
}
```

Output:

```console
result {
  regressions {
    value: 2.5
  }
}
model_spec {
  name: "half_plus_two"
  version {
    value: 123
  }
  signature_name: "regress_x_to_y"
}
```

### Predict API

To predict:

```java
try (TensorFlowServingClient client = TensorFlowServingClient.newInstance()) {
    PredictRequest request = PredictRequest.newBuilder()
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
    PredictResponse response = client.predict(request);
    System.out.println(response);
}
```

Output:

```console
outputs {
  key: "y"
  value {
    dtype: DT_FLOAT
    tensor_shape {
      dim {
        size: 3
      }
    }
    float_val: 2.5
    float_val: 3.0
    float_val: 4.5
  }
}
model_spec {
  name: "half_plus_two"
  version {
    value: 123
  }
  signature_name: "serving_default"
}
```

## Configuration

### tfsc4j.properties

```properties
target = <target>
credentials = <credentials>
```

### System properties

You can configure the TFSC4J properties via system properties with prefix `tfsc4j.`.

For instance, you can configure `target` with the `tfsc4j.target` system property.

### Environment variables

You can also configure the TFSC4J properties via environment variables with prefix `TFSC4J_`.

For instance, you can configure `target` with the `TFSC4J_TARGET` environment variable.

## Examples

See [examples](./examples/).

## Build

```console
mvn clean install
```
