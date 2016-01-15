/*
 * PhysicalConstantsAbstractDataView.java
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
package org.earthtime.ratioDataViews;

import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class PhysicalConstantsAbstractDataView extends AbstractRatiosDataView {

    protected JTextField[] lambdaReferences;

    /**
     *
     *
     * @param dataModel
     * @param parentDimension the value of parentDimension
     */
    public PhysicalConstantsAbstractDataView ( AbstractRatiosDataModel dataModel, Dimension parentDimension ) {
        super( dataModel, parentDimension );
    }

    /**
     *
     * @param editable the value of editable
     */
    @Override
    protected void initView ( final boolean editable) {
        super.initView( editable );

        // create lambda refs panel
        JLayeredPane lambdaRefsPane = new JLayeredPane();
        lambdaRefsPane.setBackground( Color.white );

        lambdaReferences = new JTextField[dataModel.getData().length];
        int countLines = 0;
        for (int i = 0; i < dataModel.getData().length; i ++) {
            JLabel lambdaLabel = new JLabel( dataModel.getData()[i].getName().trim() + ":" );
            lambdaLabel.setBounds( 15, countLines * 25, 75, 25 );
            lambdaLabel.setFont(ReduxConstants.sansSerif_12_Bold );
            lambdaRefsPane.add( lambdaLabel );

            lambdaReferences[i] = new JTextField( ((ValueModelReferenced) dataModel.getData()[i]).getReference().trim() );
            lambdaReferences[i].setBounds( 95, countLines * 25, 400, 25 );
            lambdaReferences[i].setFont(ReduxConstants.sansSerif_10_Plain );
            lambdaReferences[i].setEnabled( editable );  //setEditable( editable );
            lambdaRefsPane.add( lambdaReferences[i] );

            countLines ++;
        }


        setOpaqueWithLightGray( lambdaRefsPane );
        insertModelTab( "\u03BB References", lambdaRefsPane, 1 );


        JLayeredPane tracerTypePane = new JLayeredPane();
        tracerTypePane.setBackground( Color.white );

        Map<String, BigDecimal> atomicMolarMasses = ((PhysicalConstantsModel) dataModel).getAtomicMolarMasses();
        Iterator<String> atomicMolarMassesIterator = atomicMolarMasses.keySet().iterator();
        countLines = 0;
        while (atomicMolarMassesIterator.hasNext()) {
            String name = atomicMolarMassesIterator.next();
            JLabel massLabel = new JLabel( name + " = " + atomicMolarMasses.get( name ).toString() );
            massLabel.setBounds( 100, countLines * 20, 400, 20 );
            massLabel.setFont(ReduxConstants.sansSerif_12_Bold );
            tracerTypePane.add( massLabel );

            countLines ++;
        }

        setOpaqueWithLightGray( tracerTypePane );
        insertModelTab( "Atomic Molar Masses", tracerTypePane, 2 );
    }

    /**
     *
     */
    protected abstract void setupViews ();

    /**
     *
     *
     * @param checkCovarianceValidity the value of checkCovarianceValidity
     * @throws org.earthtime.exceptions.ETException
     */
    @Override
    protected void saveEdits ( boolean checkCovarianceValidity )
            throws ETException {

        super.saveEdits( checkCovarianceValidity );
    }
}
