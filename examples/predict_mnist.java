///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17+
//DEPS io.github.tadayosi.tensorflow:tensorflow-serving-client:0.2.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.TensorShapeProto.Dim;

import com.google.protobuf.Int64Value;

import io.github.tadayosi.tensorflow.serving.client.TensorFlowServingClient;
import tensorflow.serving.Model.ModelSpec;
import tensorflow.serving.Predict.PredictRequest;

public class predict_mnist {

    static final String DATA_DIR = "data/mnist";

    public static void main(String... args) throws Exception {
        try (var client = TensorFlowServingClient.newInstance()) {
            Files.walk(Path.of(DATA_DIR)).forEach(file -> {
                if (Files.isDirectory(file)) {
                    System.out.println("Directory: " + file.getFileName());
                    return;
                }
                try {
                    var data = preprocess(file);
                    var inputs = TensorProto.newBuilder()
                            .setDtype(DataType.DT_FLOAT)
                            .setTensorShape(TensorShapeProto.newBuilder()
                                    .addDim(Dim.newBuilder().setSize(28))
                                    .addDim(Dim.newBuilder().setSize(28)))
                            .addAllFloatVal(data)
                            .build();
                    var request = PredictRequest.newBuilder()
                            .setModelSpec(ModelSpec.newBuilder()
                                    .setName("mnist")
                                    .setVersion(Int64Value.of(1)))
                            .putInputs("keras_tensor", inputs)
                            .build();
                    var response = client.predict(request);
                    var output = response.getOutputsOrThrow("output_0");
                    var answer = argmax(output);
                    System.out.println("  %s => %s".formatted(file.getFileName(), answer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    static List<Float> preprocess(Path file) throws IOException {
        var image = ImageIO.read(file.toFile());
        var width = image.getWidth();
        var height = image.getHeight();
        if (width != 28 || height != 28) {
            throw new IllegalArgumentException("Image size must be 28x28");
        }
        var normalised = new ArrayList<Float>(width * height);
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var rgb = image.getRGB(x, y);
                normalised.add( (rgb & 0xFF) / 255.0f);
            }
        }
        return normalised;
    }

    static int argmax(TensorProto tensor) {
        return IntStream.range(0, tensor.getFloatValCount())
                .reduce((max, i) -> tensor.getFloatVal(max) > tensor.getFloatVal(i) ? max : i)
                .orElseThrow();
    }

}
