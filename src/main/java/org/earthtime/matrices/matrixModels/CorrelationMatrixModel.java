/*
 * CorrelationMatrixModel.java
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.matrices.matrixModels;

import Jama.Matrix;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author James F. Bowring
 */
public class CorrelationMatrixModel extends AbstractMatrixModel {

    /**
     *
     */
    public CorrelationMatrixModel () {
        super( "Correlations" );
    }

    @Override
    public AbstractMatrixModel copy () {
        AbstractMatrixModel retval = new CorrelationMatrixModel();

        retval.setRows( rows );
        retval.copyCols( cols );
        retval.setMatrix( matrix.copy() );

        return retval;
    }

    /**
     *
     */
    @Override
    public void initializeMatrix () {
        setMatrix( Matrix.identity( getRows().size(), getCols().size() ) );
    }

    /**
     *
     * @param correlations
     * @return
     */
    public boolean initializeCorrelations (
            Map<String, BigDecimal> correlations ) {

        boolean retVal =  ! (getRows().isEmpty() || getCols().isEmpty());
        if ( retVal ) {
            Iterator<String> covKeys = correlations.keySet().iterator();
            while (covKeys.hasNext()) {
                String covName = covKeys.next();
                BigDecimal rho = correlations.get( covName );
                setCorrelationCells( covName, rho.doubleValue() );
            }
        }
        return retVal;
    }

    /**
     *
     * @param correlationName
     * @param rho
     */
    protected void setCorrelationCells ( String correlationName, double rho ) {

        // name is of form rhoXXX__YYY
        String both = correlationName.substring( 3 );
        String[] each = both.split( "__" );
        // left side needs lowercase
        String leftSide = each[0].substring( 0, 1 ).toLowerCase() + each[0].substring( 1 );

        setCorrelationCell( leftSide, each[1], rho );

    }

    /**
     *
     * @param leftSide
     * @param rightSide
     * @param rho
     * @return
     */
    protected boolean setCorrelationCell ( String leftSide, String rightSide, double rho ) {
        boolean retval = false;

        Integer left = getCols().get( leftSide );
        Integer right = getCols().get( rightSide );
        if ( (left != null) && (right != null) ) {
            matrix.set( (int) left, (int) right, rho );
            matrix.set( (int) right, (int) left, rho );
            retval = true;
        }
        return retval;
    }

    /**
     * 
     * @param correlationName
     * @return
     */
    public double getCorrelationCell ( String correlationName ) {

        // name is of form rhoXXX__YYY
        String both = correlationName.substring( 3 );
        String[] each = both.split( "__" );
        // left side needs lowercase
        String leftSide = each[0].substring( 0, 1 ).toLowerCase() + each[0].substring( 1 );

        return getCorrelationCell( leftSide, each[1] );

    }

    /**
     *
     * @param leftSide
     * @param rightSide
     * @return
     */
    public double getCorrelationCell ( String leftSide, String rightSide ) {
        double retval = 0.0;

        Integer left = getCols().get( leftSide );
        Integer right = getCols().get( rightSide );

        if ( (left != null) && (right != null) ) {
            return matrix.get( (int) left, (int) right );
        }
        return retval;
    }

    /**
     *
     * @param leftSide
     * @param rightSide
     * @param rho
     */
    public void setCorrelationCells ( String leftSide, String rightSide, double rho ) {
        Integer left = getCols().get( leftSide );
        Integer right = getCols().get( rightSide );

        if ( (left != null) && (right != null) ) {
            matrix.set( left, right, rho );
            matrix.set( right, left, rho );
        }
    }

    /**
     *
     * @param args
     */
    public static void main ( String[] args ) {
        AbstractMatrixModel myMatrix = new CorrelationMatrixModel();

        String[] rowNames = new String[]{"first", "second", "third", "fourth", "fifth"};
        myMatrix.setRows( rowNames );
        myMatrix.setCols( myMatrix.getRows() );

        ConcurrentMap<String, BigDecimal> varianceTerms = new ConcurrentHashMap<String, BigDecimal>();
        varianceTerms.put( "third", new BigDecimal( 1 ) );
        varianceTerms.put( "fourth", new BigDecimal( 2 ) );
        varianceTerms.put( "fifth", new BigDecimal( 3 ) );

        Map<String, BigDecimal> coVariances = new HashMap<String, BigDecimal>();
        coVariances.put( "covThird__fourth", new BigDecimal( 9 ) );

//        if (myMatrix.initializeMatrixModelWithVariances(varianceTerms)) {
//            ((CorrelationMatrixModel)myMatrix).initializeCoVariances(coVariances);
//            System.out.println(myMatrix.ToStringWithLabels());
//        }


    }

    /**
     * 
     * @param row
     * @param col
     * @param value
     */
    @Override
    public void setValueAt ( int row, int col, double value ) {
        // only if value between -1 and 1
        if ( (value >=  - 1.0) && (value <= 1.0) ) {
            matrix.set( row, col, value );
            if ( row != col ) {
                matrix.set( col, row, value );
            }
        }
    }
}
