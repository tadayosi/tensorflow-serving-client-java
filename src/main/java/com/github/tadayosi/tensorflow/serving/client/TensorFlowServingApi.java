package com.github.tadayosi.tensorflow.serving.client;

import tensorflow.serving.Classification;
import tensorflow.serving.GetModelMetadata;
import tensorflow.serving.GetModelStatus;
import tensorflow.serving.Predict;
import tensorflow.serving.RegressionOuterClass;

public interface TensorFlowServingApi {

    /**
     * Gets status of model. If the ModelSpec in the request does not specify
     * version, information about all versions of the model will be returned. If
     * the ModelSpec in the request does specify a version, the status of only
     * that version will be returned.
     */
    GetModelStatus.GetModelStatusResponse getModelStatus(GetModelStatus.GetModelStatusRequest request);

    /**
     * GetModelMetadata - provides access to metadata for loaded models.
     */
    GetModelMetadata.GetModelMetadataResponse getModelMetadata(GetModelMetadata.GetModelMetadataRequest request);

    /**
     * Classify.
     */
    Classification.ClassificationResponse classify(Classification.ClassificationRequest request);

    /**
     * Regress.
     */
    RegressionOuterClass.RegressionResponse regress(RegressionOuterClass.RegressionRequest request);

    /**
     * Predict -- provides access to loaded TensorFlow model.
     */
    Predict.PredictResponse predict(Predict.PredictRequest request);
}
