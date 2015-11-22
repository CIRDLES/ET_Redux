/*
 * MineralStandardUPbRatioViewEditable.java
 *
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
package org.earthtime.UPb_Redux.valueModelViews;

import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MineralStandardUPbRatioViewNotEditable extends ValueModelViewNotEditable {

    /**
     *
     *
     * @param valueModel
     */
    public MineralStandardUPbRatioViewNotEditable ( ValueModel valueModel) {
        super( valueModel);

        initTextBoxes( false );

        JCheckBox measuredCheckBox = new JCheckBox(//
                "", ((MineralStandardUPbRatioModel) valueModel).isMeasured() );
        measuredCheckBox.setEnabled( false );
        measuredCheckBox.setFont(ReduxConstants.sansSerif_12_Bold );

        measuredCheckBox.setSize( 25, AbstractValueModelView.PANEL_HEIGHT );
        measuredCheckBox.setLocation( this.uncertaintyTextBox.getX() + this.uncertaintyTextBox.getWidth() + 25, 0 );

        this.add( measuredCheckBox );


        this.setSize( this.getWidth(), this.getHeight() );

    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

        JFrame testFrame = new JFrame();
        testFrame.setBounds( 400, 400, PANEL_WIDTH, PANEL_HEIGHT + 25 );

        ValueModel valueModel = new MineralStandardUPbRatioModel( //
                "test", //
                new BigDecimal( 12345.67890 ), //
                "ABS", //
                new BigDecimal( 0.987654321 ),//
                true);

        AbstractValueModelView testView = new MineralStandardUPbRatioViewNotEditable( valueModel);
        testView.setBorder( new LineBorder( Color.red ) );


        testFrame.add( testView );
        testFrame.setVisible( true );
    }
}
