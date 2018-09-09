/*
 * ValueModelsPanelViewNotEditable.java
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

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;
import org.earthtime.UPb_Redux.valueModelViews.AbstractValueModelView;
import org.earthtime.UPb_Redux.valueModelViews.ValueModelViewNotEditable;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelsPanelViewNotEditable extends AbstractValueModelsPanelView {

    /**
     *
     *
     * @param valueModels
     */
    public ValueModelsPanelViewNotEditable ( ValueModel[] valueModels) {
        super(valueModels);

        for (int i = 0; i < valueModelViews.length; i ++) {
            valueModelViews[i] = new ValueModelViewNotEditable(//
                    valueModels[i]);
        }

        initValueModelViews( );
    }

    /**
     * 
     */
    @Override
    public void saveEdits () {
        throw new UnsupportedOperationException( "Not saveable." );
    }
    
    

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

        JFrame testFrame = new JFrame();
        testFrame.setBounds( 400, 400, 500, 300 );

        ValueModel[] valueModels = new ValueModel[5];
        for (int i = 0; i < 5; i ++) {
            valueModels[i] = new ValueModel( "test " + i );
        }
        AbstractValueModelsPanelView testView = new ValueModelsPanelViewNotEditable( valueModels);
        testView.setBorder( new LineBorder( Color.red ) );


        testFrame.add( testView );
        testFrame.setVisible( true );
    }
}
