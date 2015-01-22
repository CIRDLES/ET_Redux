/*
 * ValueModelViewEditable.java
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

import java.math.BigDecimal;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelViewEditable extends AbstractValueModelView {

    /**
     *
     *
     * @param valueModel
     * @param dataEntryDetector the value of dataEntryDetector
     */
    public ValueModelViewEditable ( ValueModel valueModel, DataEntryDetectorInterface dataEntryDetector) {
        super( valueModel );

        this.dataEntryDetector = dataEntryDetector;

        initTextBoxes( true );
       
    }

    /**
     *
     */
    @Override
    public void saveEdits () {
        valueModel.setValue( new BigDecimal( valueTextBox.getText() ) );
        valueModel.setOneSigma( new BigDecimal( uncertaintyTextBox.getText() ) );
        if (isShowOneSigmaAsPerCent()){
            valueModel.setUncertaintyTypePCT();
        } else {
            valueModel.setUncertaintyTypeABS();
        }
    }

}
