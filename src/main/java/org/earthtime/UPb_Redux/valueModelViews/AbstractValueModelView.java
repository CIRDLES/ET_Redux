/*
 * AbstractValueModelView.java
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UPb_Redux.valueModelViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RatioNamePrettyPrinter;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractValueModelView extends JLayeredPane {

    /**
     *
     */
    public static int PANEL_HEIGHT = 25;
    /**
     *
     */
    public static int PANEL_WIDTH = 550;
    /**
     *
     */
    protected final JLabel valueModelNameLabel;
    /**
     *
     */
    protected final ValueModel valueModel;
    /**
     *
     */
    protected final JTextField valueTextBox;
    /**
     *
     */
    protected final JTextField uncertaintyTextBox;

    /**
     *
     */
    protected DataEntryDetectorInterface dataEntryDetector;

    /**
     *
     */
    protected boolean showOneSigmaAsPerCent;

    /**
     *
     */
    /**
     *
     *
     * @param valueModel
     */
    public AbstractValueModelView ( ValueModel valueModel ) {

        this.valueModel = valueModel;

        valueTextBox = new JTextField();
        valueTextBox.setBounds( 110, 0, 185, PANEL_HEIGHT );
        this.add( valueTextBox );

        uncertaintyTextBox = new JTextField();
        uncertaintyTextBox.setBounds( 300, 0, 185, PANEL_HEIGHT );
        this.add( uncertaintyTextBox );

        valueModelNameLabel = new JLabel( RatioNamePrettyPrinter.makePrettyHTMLString( valueModel.getName() ) );
        valueModelNameLabel.setBounds( //
                0, //
                0, //
                100, //
                PANEL_HEIGHT );
        valueModelNameLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        valueModelNameLabel.setFont(ReduxConstants.sansSerif_12_Bold );
        this.add( valueModelNameLabel );

        showOneSigmaAsPerCent = false;

        this.setBounds( 0, 0, PANEL_WIDTH, PANEL_HEIGHT );
    }

    /**
     *
     *
     * @param editable
     */
    protected void initTextBoxes ( boolean editable ) {
        valueTextBox.setDocument( new DialogEditor.BigDecimalDocument( valueTextBox, editable ) );
        valueTextBox.setText( valueModel.getValue().setScale(20, RoundingMode.HALF_UP).toPlainString() );

        uncertaintyTextBox.setDocument( new DialogEditor.BigDecimalDocument( uncertaintyTextBox, editable ) );

        displayOneSigmaPerMode();

        if ( editable ) {

            // return etc
            uncertaintyTextBox.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed ( ActionEvent e ) {
                    announceChangedUncertaintyField();
                }
            } );

            // lost focus
            uncertaintyTextBox.addFocusListener( new FocusListener() {
                @Override
                public void focusGained ( FocusEvent e ) {
                }

                @Override
                public void focusLost ( FocusEvent e ) {
                    announceChangedUncertaintyField();
                }
            } );

            // return etc
            valueTextBox.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed ( ActionEvent e ) {
                    announceChangedValueField();
                }
            } );

            // lost focus
            valueTextBox.addFocusListener( new FocusListener() {
                @Override
                public void focusGained ( FocusEvent e ) {
                }

                @Override
                public void focusLost ( FocusEvent e ) {
                    announceChangedValueField();
                }
            } );
        }

    }

    private void displayOneSigmaPerMode () {
        if ( isShowOneSigmaAsPerCent() ) {
            uncertaintyTextBox.setText( valueModel.getOneSigmaPct().setScale(20, RoundingMode.HALF_UP).toPlainString() );
        } else {
            uncertaintyTextBox.setText( valueModel.getOneSigmaAbs().setScale(20, RoundingMode.HALF_UP).toPlainString() );
        }

    }

    private void announceChangedUncertaintyField () {
        if ( uncertaintyTextBox.getText().trim().length() == 0 ) {
            uncertaintyTextBox.setText( "0" );
        }

        if ( new BigDecimal( uncertaintyTextBox.getText() ).compareTo( valueModel.getOneSigma() ) != 0 ) {
            dataEntryDetector.dataEntryDetected();
        }
    }

    private void announceChangedValueField () {
        if ( valueTextBox.getText().trim().length() == 0 ) {
            valueTextBox.setText( "0" );
        }
        
        if ( new BigDecimal( valueTextBox.getText() ).compareTo( BigDecimal.ZERO ) == 0 ) {
            uncertaintyTextBox.setText( "0" );
        }

        if ( new BigDecimal( valueTextBox.getText() ).compareTo( valueModel.getValue() ) != 0 ) {
            dataEntryDetector.dataEntryDetected();
        }
    }

    /**
     *
     */
    public abstract void saveEdits ();

    /**
     *
     * @param g2d
     */
    protected void paintInit ( Graphics2D g2d ) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setPaint( Color.BLACK );
        g2d.setStroke( new BasicStroke( 1.0f ) );
        g2d.setFont(ReduxConstants.sansSerif_12_Bold );

    }

    /**
     *
     * @param g2d
     */
    public void paint ( Graphics2D g2d ) {
        paintInit( g2d );

    }

    /**
     * @param showOneSigmaAsPerCent the showOneSigmaAsPerCent to set
     */
    public void setShowOneSigmaAsPerCent ( boolean showOneSigmaAsPerCent ) {
        this.showOneSigmaAsPerCent = showOneSigmaAsPerCent;
    }

    /**
     * @return the showOneSigmaAsPerCent
     */
    public boolean isShowOneSigmaAsPerCent () {
        return showOneSigmaAsPerCent;
    }

    /**
     *
     */
    public void toggleShowOneSigmaAsPerCent () {
        showOneSigmaAsPerCent =  ! showOneSigmaAsPerCent;
        displayOneSigmaPerMode();
    }
}
