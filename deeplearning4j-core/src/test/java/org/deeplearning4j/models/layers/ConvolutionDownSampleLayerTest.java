/*
 * Copyright 2015 Skymind,Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.deeplearning4j.models.layers;

import org.deeplearning4j.datasets.fetchers.MnistDataFetcher;
import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.featuredetectors.rbm.RBM;
import org.deeplearning4j.nn.api.LayerFactory;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.override.ClassifierOverride;
import org.deeplearning4j.nn.conf.override.ComposableOverride;
import org.deeplearning4j.nn.conf.override.ConfOverride;
import org.deeplearning4j.nn.layers.OutputLayer;
import org.deeplearning4j.nn.layers.convolution.ConvolutionDownSampleLayer;
import org.deeplearning4j.nn.layers.convolution.preprocessor.ConvolutionInputPreProcessor;
import org.deeplearning4j.nn.layers.convolution.preprocessor.ConvolutionPostProcessor;
import org.deeplearning4j.nn.layers.factory.LayerFactories;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.optimize.stepfunctions.GradientStepFunction;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agibsonccc on 9/7/14.
 */
public class ConvolutionDownSampleLayerTest {
    private static final Logger log = LoggerFactory.getLogger(ConvolutionDownSampleLayerTest.class);

    @Test
    public void testConvolution() throws Exception {
        boolean switched = false;
        if(Nd4j.dtype == DataBuffer.FLOAT) {
            Nd4j.dtype = DataBuffer.DOUBLE;
            switched = true;
        }
        MnistDataFetcher data = new MnistDataFetcher(true);
        data.fetch(2);
        DataSet d = data.next();

        d.setFeatures(d.getFeatureMatrix().reshape(2, 1, 28, 28));
        LayerFactory layerFactory = LayerFactories.getFactory(ConvolutionDownSampleLayer.class);
        NeuralNetConfiguration n = new NeuralNetConfiguration.Builder()
                .filterSize(2, 1, 2, 2).layerFactory(layerFactory).build();

        ConvolutionDownSampleLayer c = layerFactory.create(n);

        if(switched) {
            Nd4j.dtype = DataBuffer.FLOAT;
        }

    }

    @Test
    public void testMultiLayer() {


        LayerFactory layerFactory = LayerFactories.getFactory(ConvolutionDownSampleLayer.class);
        int batchSize = 110;
        /**
         *
         */
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.LBFGS)
                .dist(Nd4j.getDistributions().createNormal(0, 1))
                .iterations(100).iterationListener(new ScoreIterationListener(1))
                .activationFunction("tanh").filterSize(5, 1, 2, 2).constrainGradientToUnitNorm(true)
                .nIn(4).nOut(3).batchSize(batchSize)
                .layerFactory(layerFactory).dropOut(0.5)
                .list(3)
                .preProcessor(0, new ConvolutionPostProcessor()).inputPreProcessor(0, new ConvolutionInputPreProcessor(2, 2))
                .preProcessor(1, new ConvolutionPostProcessor()).inputPreProcessor(1, new ConvolutionInputPreProcessor(3, 3))
                .hiddenLayerSizes(new int[]{4, 25})
                .override(0, new ConfOverride() {

                    @Override
                    public void overrideLayer(int i, NeuralNetConfiguration.Builder builder) {
                        if (i == 0)
                            builder.filterSize(5, 1, 2, 2);

                    }
                })    .override(1, new ConfOverride() {

                    @Override
                    public void overrideLayer(int i, NeuralNetConfiguration.Builder builder) {

                        if (i == 1)
                            builder.filterSize(5, 1, 3, 3);

                    }
                })
                .override(2, new ConfOverride() {

                    @Override
                    public void overrideLayer(int i, NeuralNetConfiguration.Builder builder) {
                        if (i == 2) {
                            builder.activationFunction("softmax");
                            builder.weightInit(WeightInit.ZERO);
                            builder.layerFactory(LayerFactories.getFactory(OutputLayer.class));
                            builder.lossFunction(LossFunctions.LossFunction.MCXENT);
                        }
                    }
                }).build();

        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        DataSetIterator iter = new IrisDataSetIterator(150, 150);


        org.nd4j.linalg.dataset.DataSet next = iter.next();
        next.normalizeZeroMeanZeroUnitVariance();
        SplitTestAndTrain trainTest = next.splitTestAndTrain(110);
        /**
         * Likely cause: shape[0] mis match on the filter size and the input batch size.
         * Likely need to make a little more general.
         */
        network.fit(trainTest.getTrain());


        //org.nd4j.linalg.dataset.DataSet test = trainTest.getTest();
        Evaluation eval = new Evaluation();
        INDArray output = network.output(trainTest.getTest().getFeatureMatrix());
        eval.eval(trainTest.getTest().getLabels(),output);
        log.info("Score " +eval.stats());

    }


}
