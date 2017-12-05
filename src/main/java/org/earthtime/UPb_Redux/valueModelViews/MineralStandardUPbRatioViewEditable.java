/*
 * MineralStandardUPbRatioViewEditable.java
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

import javax.swing.JCheckBox;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public class MineralStandardUPbRatioViewEditable extends ValueModelViewEditable {

    private final JCheckBox measuredCheckBox;
    
    /**
     *
     *
     * @param valueModel
     * @param dataEntryDetector the value of dataEntryDetector
     */
    public MineralStandardUPbRatioViewEditable ( ValueModel valueModel, DataEntryDetectorInterface dataEntryDetector) {
        super( valueModel, dataEntryDetector );

        measuredCheckBox = new JCheckBox(//
                "", ((MineralStandardUPbRatioModel) valueModel).isMeasured() );
               
        measuredCheckBox.setEnabled( true );
        measuredCheckBox.setFont(ReduxConstants.sansSerif_12_Bold );

        measuredCheckBox.setSize( 25, AbstractValueModelView.PANEL_HEIGHT );
        measuredCheckBox.setLocation( this.uncertaintyTextBox.getX() + this.uncertaintyTextBox.getWidth() + 25, 0 );

        this.add( measuredCheckBox );

        this.setSize( this.getWidth(), this.getHeight() );

    }

    /**
     * 
     */
    @Override
    public void saveEdits () {
        super.saveEdits();
        
        
        if (!valueModel.hasPositiveValue()){
            uncertaintyTextBox.setText( "0.0");
            valueModel.setOneSigma( 0.0);
            measuredCheckBox.setSelected( false);
        }
        ((MineralStandardUPbRatioModel) valueModel).setMeasured( measuredCheckBox.isSelected() );
        
    }
   
}
