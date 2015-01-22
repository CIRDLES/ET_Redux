/*
 * MineralStandardUPbRatiosPanelViewEditable.java
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
package org.earthtime.UPb_Redux.valueModelPanelViews;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModelViews.AbstractValueModelView;
import org.earthtime.UPb_Redux.valueModelViews.MineralStandardUPbRatioViewEditable;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public class MineralStandardUPbRatiosPanelViewEditable extends AbstractValueModelsPanelView {

    /**
     *
     *
     * @param valueModels
     * @param dataEntryDetector the value of dataEntryDetector
     */
    public MineralStandardUPbRatiosPanelViewEditable ( ValueModel[] valueModels, DataEntryDetectorInterface dataEntryDetector) {
        super( valueModels);

        this.dataEntryDetector = dataEntryDetector;

        for (int i = 0; i < valueModelViews.length; i ++) {
            valueModelViews[i] = new MineralStandardUPbRatioViewEditable (//
                    valueModels[i], dataEntryDetector );
        }

        JLabel measuredLabel = new JLabel( "<html><u>measured</u></html>" );
        measuredLabel.setFont(ReduxConstants.sansSerif_10_Bold );
        measuredLabel.setHorizontalAlignment( SwingConstants.CENTER );
        measuredLabel.setBounds( 420, 2, 85, AbstractValueModelView.PANEL_HEIGHT );
        this.add( measuredLabel );

        initValueModelViews();
    }

    /**
     *
     */
    @Override
    public void saveEdits () {
        for (int i = 0; i < valueModelViews.length; i ++) {
            valueModelViews[i].saveEdits();
        }
    }
}
