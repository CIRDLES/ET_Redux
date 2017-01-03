/*
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction;

import java.awt.geom.Path2D;
import java.util.Vector;
import org.earthtime.dataDictionaries.DataDictionary;

/**
 *
 * @author jackom @date June 2008
 */
public class InputUnctContrib implements Comparable<InputUnctContrib> {

    private String name;
    private double unct;
    private double heightRatio; //~~~ Temp: height = only total uncertainty
    private Vector<InputUnctContrib> covariants;
    private double covariance;
    private double totalUnct; //+ covariants' unct
    private double trueUnct; //totalUnct + covariance
    private Path2D contribBar;
    private Path2D selectionBar;
    private boolean selected;
    private int countOfCovarianceConnections;
    // added april 2009 to remove contributors with 0 partial derivatives
    private double partialDerivative;

    /**
     *
     * @param name
     * @param name
     */
    public InputUnctContrib ( String name ) {
        this.name = name;
        this.unct = 0.0;
        this.covariance = 0.0;
        this.covariants = new Vector<InputUnctContrib>();
        this.countOfCovarianceConnections = 0;
        this.partialDerivative = 0.0;
    }

    /**
     *
     * @param nameP
     * @param unctP
     * @param partialDerivative
     */
    public InputUnctContrib ( String nameP, double unctP, double partialDerivative ) {
        this( nameP );
        this.unct = unctP;
        this.partialDerivative = partialDerivative;;

    }

    /**
     *
     * @return
     */
    public String outputToString () {
        return inputUnctContribToString( this, "", 0 );
    }

    private String inputUnctContribToString ( InputUnctContrib currentInputUnctContrib, String retVal, int depth ) {

        retVal += depth + "> ";
        // indent
        for (int i = 0; i < depth; i ++) {
            retVal += "    ";
        }
        // top level
        retVal += currentInputUnctContrib.getName()//
                + " unct = " + currentInputUnctContrib.getUnct() //
                + " partDeriv = " + currentInputUnctContrib.getPartialDerivative() //
                + " trueunct = " + currentInputUnctContrib.getTrueUnct() + "\n";

        //System.out.println( retVal );
        //recursive
        for (int i = 0; i < currentInputUnctContrib.getCovariants().size(); i ++) {
            retVal = inputUnctContribToString( currentInputUnctContrib.getCovariants().get( i ), retVal, depth + 1 );
        }


        return retVal;
    }

    /**
     *
     * @return
     */
    public double getUnct () {
        return unct;
    }

    /**
     *
     */
    public void calcUncts () {
        totalUnct = unct;
        if (  ! (covariants.isEmpty()) ) {
            for (InputUnctContrib IUC : covariants) {
                totalUnct += IUC.getUnct();
            }
        }
    }

    /**
     *
     * @return
     */
    public double calculateDeepCovariance () {
        if (  ! (covariants.isEmpty()) ) {
            for (InputUnctContrib IUC : covariants) {
                covariance += IUC.calculateDeepCovariance();
            }
        }
        setTrueUnct( totalUnct + covariance );
        return covariance;
    }

    /**
     *
     * @return
     */
    public double getTotalUnct () {
        return totalUnct;
    }

    /**
     *
     * @param inputUnctContrib
     * @return
     * @throws ClassCastException
     */
    public int compareTo ( InputUnctContrib inputUnctContrib ) throws ClassCastException {
        InputUnctContrib other = (InputUnctContrib) inputUnctContrib;
        return -1 * ((Double) getTrueUnct()).compareTo( ((Double) other.getTrueUnct()) );
    }

    /**
     *
     * @param inputUnctContrib
     * @return
     */
    @Override
    public boolean equals ( Object inputUnctContrib ) {
        //check for self-comparison
        if ( this == inputUnctContrib ) {
            return true;
        }
        if (  ! (inputUnctContrib instanceof InputUnctContrib) ) {
            return false;
        }

        InputUnctContrib myInputUnctContrib = (InputUnctContrib) inputUnctContrib;
        return (((Double) getTrueUnct()).compareTo( myInputUnctContrib.getTrueUnct() ) == 0);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode () {

        return 0;
    }

    /**
     *
     * @return
     */
    public String toStringCovNames () {
        String retStr;
       // if ( partialDerivative != 0.0 ) {
            retStr = DataDictionary.inputsNameTranslator.get( name ) + ", ";
       // }
        for (InputUnctContrib IUC : covariants) {
         //   if ( IUC.getPartialDerivative() != 0.0 ) {
                retStr += DataDictionary.inputsNameTranslator.get( IUC.getName() ) + ", ";
         //   }
        }

        return retStr.substring( 0, retStr.length() - 2 );
    }

    /**
     *
     * @param unct
     */
    public void setUnct ( double unct ) {
        this.unct = unct;
    }

    /**
     *
     * @return
     */
    public String getName () {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName ( String name ) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public double getCovariance () {
        return covariance;
    }

    /**
     *
     * @return
     */
    public double getCovarianceIfPos () {
        if ( covariance > 0.0 ) {
            return covariance;
        } else {
            return 0.0;
        }
    }

    /**
     *
     * @param covariance
     */
    public void setCovariance ( double covariance ) {
        this.covariance = covariance;
    }

    /**
     *
     * @return
     */
    public Vector<InputUnctContrib> getCovariants () {
        return covariants;
    }

    /**
     *
     * @param name
     * @return
     */
    public InputUnctContrib getCovariantByName ( String name ) {
        InputUnctContrib retVal = new InputUnctContrib( name );
        for (InputUnctContrib IUC : covariants) {
            if ( name.equalsIgnoreCase( IUC.getName() ) ) {
                retVal = IUC;
            }
        }
        return retVal;
    }

    /**
     *
     * @param covariant
     */
    public void addCovariant ( InputUnctContrib covariant ) {
        covariants.add( covariant );
    }

    /**
     *
     * @return
     */
    public double getHeightRatio () {
        return heightRatio;
    }

    /**
     *
     * @param height
     */
    public void setHeightRatio ( double height ) {
        this.heightRatio = height;
    }

    /**
     *
     * @return
     */
    public double getTrueUnct () {
        return trueUnct;
    }

    /**
     *
     * @return
     */
    public Path2D getContribBar () {
        return contribBar;
    }

    /**
     *
     * @param contribBar
     */
    public void setContribBar ( Path2D contribBar ) {
        this.contribBar = contribBar;
    }

    /**
     *
     * @return
     */
    public boolean isSelected () {
        return selected;
    }

    /**
     *
     * @param selected
     */
    public void setSelected ( boolean selected ) {
        this.selected = selected;
    }

    /**
     *
     * @return
     */
    public Path2D getSelectionBar () {
        return selectionBar;
    }

    /**
     *
     * @param selectionBar
     */
    public void setSelectionBar ( Path2D selectionBar ) {
        this.selectionBar = selectionBar;
    }

    /**
     * @return the countOfCovarianceConnections
     */
    public int getCountOfCovarianceConnections () {
        return countOfCovarianceConnections;
    }

    /**
     * @param countOfCovarianceConnections the countOfCovarianceConnections to
     * set
     */
    public void setCountOfCovarianceConnections ( int countOfCovarianceConnections ) {
        this.countOfCovarianceConnections = countOfCovarianceConnections;
    }

    /**
     *
     */
    public void incrementCountOfCovarianceConnections () {
        countOfCovarianceConnections ++;
    }

    /**
     * @return the partialDerivative
     */
    public double getPartialDerivative () {
        return partialDerivative;
    }

    /**
     * @param partialDerivative the partialDerivative to set
     */
    public void setPartialDerivative ( double partialDerivative ) {
        this.partialDerivative = partialDerivative;
    }

    /**
     * @param trueUnct the trueUnct to set
     */
    public void setTrueUnct ( double trueUnct ) {
        this.trueUnct = trueUnct;
    }
}
