/*
 * JacobianMatrixModel.java
 *
 * Created on December 11, 2008
 *
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
package org.earthtime.matrices.matrixModels;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author James F. Bowring
 */
public class JacobianMatrixModel extends AbstractMatrixModel {

    /**
     *
     * @param levelName
     */
    public JacobianMatrixModel ( String levelName ) {
        super( levelName );
    }

    @Override
    public AbstractMatrixModel copy () {
        AbstractMatrixModel retval = new JacobianMatrixModel(levelName);

        retval.setRows( rows );
        retval.copyCols( cols );
        retval.setMatrix( matrix.copy() );

        return retval;
    }

    /**
     *
     * @param derivedTerms
     * @return
     */
    public boolean initializeMatrixModelWithDerivedTerms (
            Map<String, BigDecimal> derivedTerms ) {

        boolean retVal =  ! (getRows().isEmpty() || getCols().isEmpty());
        if ( retVal ) {
            initializeMatrix();
            Iterator<Integer> rowKeys = getRows().keySet().iterator();
            while (rowKeys.hasNext()) {
                Integer rowKey = rowKeys.next();
                int row = (int) rowKey;
                String rowName = getRows().get( rowKey );

                // if there is a match in the columns, set cell = 1
                Integer col = getCols().get( rowName );
                if (  ! (col == null) ) {
                    matrix.set( row, (int) col, 1.0 );
                } else {
                    // this is an added row and we search for partial derivatives
                    String parDerivNameA = createPartialDerivName( rowName );
                    Iterator<String> colKeys = getCols().keySet().iterator();
                    while (colKeys.hasNext()) {
                        String colKey = colKeys.next();
                        String parDerivNameB = createPartialDerivName( colKey );
                        BigDecimal parDeriv = //
                                derivedTerms.get(//
                                parDerivNameA //
                                + "__"//
                                + parDerivNameB );
                        if (  ! (parDeriv == null) ) {
                            matrix.set(//
                                    row, //
                                    (int) getCols().get( colKey ), //
                                    parDeriv.doubleValue() );
                        }
                    }
                }
            }
        }

        return retVal;
    }

    /**
     *
     * @param args
     */
    public static void main ( String[] args ) {
        JacobianMatrixModel myMatrix = new JacobianMatrixModel( "0" );

        String[] rowNames = new String[]{"first", "second", "fourth"};
        myMatrix.setRows( rowNames );
        myMatrix.setCols( myMatrix.getRows() );

        rowNames = new String[]{"first", "second", "third", "fourth", "fifth"};
        myMatrix.setRows( rowNames );

        ConcurrentMap<String, BigDecimal> parDerivTerms = new ConcurrentHashMap<String, BigDecimal>();
        parDerivTerms.put( "dThird__dSecond", new BigDecimal( 99.9 ) );
        parDerivTerms.put( "dThird__dFourth", new BigDecimal( 11.1 ) );
        parDerivTerms.put( "dFifth__dFirst", new BigDecimal( 22.2 ) );

        if ( myMatrix.initializeMatrixModelWithDerivedTerms( parDerivTerms ) ) {
            System.out.println( myMatrix.ToStringWithLabels() );
            //  myMatrix.getMatrix().print(new DecimalFormat("0.000000E00"), 15);
        }


    }

    /**
     * 
     * @param row
     * @param col
     * @param value
     */
    @Override
    public void setValueAt ( int row, int col, double value ) {
        getMatrix().set( row, col, value );
    }
}
