package org.deeplearning4j.example.convnet.mnist;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.deeplearning4j.datasets.DataSet;
import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.datasets.mnist.draw.DrawMnistGreyScale;
import org.deeplearning4j.distributions.Distributions;
import org.deeplearning4j.nn.NeuralNetwork;
import org.deeplearning4j.nn.Tensor;
import org.deeplearning4j.plot.FilterRenderer;
import org.deeplearning4j.plot.NeuralNetPlotter;
import org.deeplearning4j.rbm.ConvolutionalRBM;
import org.deeplearning4j.util.MatrixUtil;
import org.jblas.DoubleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MnistConvNet {

    private static Logger log = LoggerFactory.getLogger(MnistConvNet.class);

    public static void main(String[] args) throws Exception {
        RandomGenerator gen = new MersenneTwister(123);

        ConvolutionalRBM r = new ConvolutionalRBM
                .Builder().withFilterSize(new int[]{28,28})
                .withNumFilters(9).withStride(new int[]{2, 2})
                .withVisibleSize(new int[]{28, 28})
                .withLossFunction(NeuralNetwork.LossFunction.RECONSTRUCTION_CROSSENTROPY)
                .numberOfVisible(28).numHidden(28)
                .withMomentum(0.3)
                .build();


        //batches of 10, 60000 examples total
        DataSetIterator iter = new MnistDataSetIterator(1,10);
        for(int i = 0; i < 10 ;i++) {
            while(iter.hasNext()) {
                DataSet next = iter.next();
                DoubleMatrix reshape = next.getFirst().reshape(28,28);
                r.train(reshape, 1e-1, new Object[]{1, 1e-1, 5000});
                Tensor W = (Tensor) r.getW().dup();

                DoubleMatrix draw =  W.reshape(W.rows() * W.columns(),W.slices());
                FilterRenderer render = new FilterRenderer();
                render.renderFilters(draw,"tmpfile.png",28,28);


            }







            iter.reset();
        }






    }

}