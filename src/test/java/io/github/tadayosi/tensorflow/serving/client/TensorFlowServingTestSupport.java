package io.github.tadayosi.tensorflow.serving.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

class TensorFlowServingTestSupport {

    private static final String IMAGE_NAME = "bitnamilegacy/tensorflow-serving";

    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> tensorflowServing = new GenericContainer<>(DockerImageName.parse(IMAGE_NAME))
        .withExposedPorts(8500, 8501)
        .withCopyFileToContainer(
            MountableFile.forClasspathResource("testdata/saved_model_half_plus_two_cpu"), "/bitnami/model-data")
        .withEnv("TENSORFLOW_SERVING_MODEL_NAME", "half_plus_two")
        .withEnv("TENSORFLOW_SERVING_ENABLE_MONITORING", "yes")
        .waitingFor(Wait.forListeningPorts(8500, 8501));

    protected TensorFlowServingClient client;

    @BeforeEach
    void setUp() {
        client = TensorFlowServingClient.builder()
            .target("localhost:" + tensorflowServing.getMappedPort(8500))
            .build();
    }

    @AfterEach
    void tearDown() {
        client.close();
    }
}
