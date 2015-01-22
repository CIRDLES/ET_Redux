/*
 * TracerUPbRatiosAbstractDataView.java
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.dataDictionaries.TracerUPbTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class TracerUPbRatiosAbstractDataView extends AbstractRatiosDataView {

    private final JComboBox <TracerUPbTypesEnum>tracerTypeChooser;

    /**
     *
     *
     * @param ratiosDataModel
     * @param parentDimension the value of parentDimension
     */
    public TracerUPbRatiosAbstractDataView ( AbstractRatiosDataModel ratiosDataModel, Dimension parentDimension ) {
        super( ratiosDataModel, parentDimension );

        tracerTypeChooser = new JComboBox<>();
    }

    /**
     *
     * @param editable the value of editable
     */
    @Override
    protected void initView ( final boolean editable){
        super.initView( editable );

        JLabel tracerTypesLabel = new JLabel( "Choose Tracer Type (currently supported):" );

        tracerTypesLabel.setBounds( 25, 10, 350, 25 );
        setOpaqueWithLightGray( tracerTypesLabel );

        JLayeredPane tracerTypePane = new JLayeredPane();

        tracerTypePane.add( tracerTypesLabel, DEFAULT_LAYER );

        // set up Tracer Type chooser
        tracerTypeChooser.setBounds( 100, 40, 275, 25 );
        tracerTypeChooser.setFont(ReduxConstants.sansSerif_12_Bold );
        tracerTypeChooser.removeAllItems();
        TracerUPbTypesEnum[] supportedTypes = TracerUPbTypesEnum.getSupportedTypes();
        for (int i = 0; i < supportedTypes.length; i ++) {
            tracerTypeChooser.addItem( supportedTypes[i] );
        }
        tracerTypeChooser.setSelectedItem( TracerUPbTypesEnum.valueFromName( ((TracerUPbModel) dataModel).getTracerType() ) );
        tracerTypeChooser.setEnabled( editable );
        tracerTypeChooser.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed ( ActionEvent e ) {
                ((TracerUPbModel) dataModel)//
                        .setTracerType( (String) tracerTypeChooser.getSelectedItem().toString() );
                dataModel.initializeNewRatiosAndRhos( true );
                dataModel.initializeModel();

                // update valueModelPane to show current tracer type ratios and concentrations
                dataTabs.remove( valueModelsPanelView );
                matrixTabs.remove( correlationVarUnctMatrixView );
                matrixTabs.remove( covarianceVarUnctMatrixView );

                setupViews();
                initDataTabsValueModelPanel(0, 0 );
                initializeMatrixTabs();

                try {
                    saveAndUpdateModelView( false );
                } catch (ETException eTException) {
                }
            }
        } );

        setOpaqueWithLightGray( tracerTypeChooser );
        tracerTypePane.add( tracerTypeChooser, DEFAULT_LAYER );

        tracerTypeChooser.setEnabled( editable );

        setOpaqueWithLightGray( tracerTypePane );
        insertModelTab( "Tracer Type", tracerTypePane, 1 );
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

        ((TracerUPbModel) dataModel)//
                .setTracerType( (String) tracerTypeChooser.getSelectedItem().toString() );

        super.saveEdits( checkCovarianceValidity );
    }
}
