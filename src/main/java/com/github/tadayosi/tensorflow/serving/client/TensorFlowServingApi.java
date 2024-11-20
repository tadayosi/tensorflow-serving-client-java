package com.github.tadayosi.tensorflow.serving.client;

import tensorflow.serving.Classification;
import tensorflow.serving.GetModelMetadata;
import tensorflow.serving.GetModelStatus;
import tensorflow.serving.Predict;
import tensorflow.serving.RegressionOuterClass;

public interface TensorFlowServingApi {

    GetModelStatus.GetModelStatusResponse getModelStatus(GetModelStatus.GetModelStatusRequest request);

    GetModelMetadata.GetModelMetadataResponse getModelMetadata(GetModelMetadata.GetModelMetadataRequest request);

    Classification.ClassificationResponse classify(Classification.ClassificationRequest request);

    RegressionOuterClass.RegressionResponse regress(RegressionOuterClass.RegressionRequest request);

    Predict.PredictResponse predict(Predict.PredictRequest request);
}
