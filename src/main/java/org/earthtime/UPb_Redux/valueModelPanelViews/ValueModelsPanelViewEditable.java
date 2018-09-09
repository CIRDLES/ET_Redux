/*
 * ValueModelsPanelViewEditable.java
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
package org.earthtime.UPb_Redux.valueModelPanelViews;

import org.earthtime.UPb_Redux.valueModelViews.ValueModelViewEditable;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelsPanelViewEditable extends AbstractValueModelsPanelView {

    
    /**
     *
     *
     * @param valueModels
     * @param dataEntryDetector
     */
    public ValueModelsPanelViewEditable ( ValueModel[] valueModels, DataEntryDetectorInterface dataEntryDetector) {
        super(valueModels);
        
        this.dataEntryDetector = dataEntryDetector;

        for (int i = 0; i < valueModelViews.length; i ++) {
            valueModelViews[i] = new ValueModelViewEditable(//
                    valueModels[i], dataEntryDetector );
        }

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

    
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

//        JFrame testFrame = new JFrame();
//        testFrame.setBounds( 400, 400, 500, 300 );
//
//        ValueModel[] valueModels = new ValueModel[5];
//        for (int i = 0; i < 5; i ++) {
//            valueModels[i] = new ValueModel( "test " + i );
//        }
//        AbstractValueModelsPanelView testView = new ValueModelsPanelViewEditable( valueModels, 15, this );
//        testView.setBorder( new LineBorder( Color.red ) );
//
//
//        testFrame.add( testView );
//        testFrame.setVisible( true );
    }
}
