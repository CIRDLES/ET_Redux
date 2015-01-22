/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.Tripoli.fitFunctions;

/**
 *
 * @author samuelbowring
 */
public interface AbstractOverDispersionLMAlgorithmInterface {

    /**
     *
     * @return
     */
    public AbstractFunctionOfX getInitialFofX();

    /**
     *
     * @return
     */
    public AbstractFunctionOfX getFinalFofX ();
}
