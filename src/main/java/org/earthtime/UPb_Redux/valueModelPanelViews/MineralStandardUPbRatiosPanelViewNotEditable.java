/*
 * MineralStandardUPbRatiosPanelViewNotEditable.java
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

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModelViews.AbstractValueModelView;
import org.earthtime.UPb_Redux.valueModelViews.MineralStandardUPbRatioViewNotEditable;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MineralStandardUPbRatiosPanelViewNotEditable extends AbstractValueModelsPanelView {

    /**
     *
     *
     * @param valueModels
     */
    public MineralStandardUPbRatiosPanelViewNotEditable ( ValueModel[] valueModels) {
        super(valueModels);

        for (int i = 0; i < valueModels.length; i ++) {
            valueModelViews[i] = new MineralStandardUPbRatioViewNotEditable(//
                    valueModels[i]);
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
        throw new UnsupportedOperationException( "Not saveable." );
    }

    
    /**
     *
     * @param args
     * @throws Exception
     */
//    public static void main ( String[] args ) throws Exception {
//
//        JFrame testFrame = new JFrame();
//        testFrame.setBounds( 400, 400, 600, 300 );
//        testFrame.setDefaultCloseOperation( javax.swing.WindowConstants.EXIT_ON_CLOSE );
//
//        ValueModel[] valueModels = new MineralStandardUPbRatioModel[5];
//        for (int i = 0; i < 5; i ++) {
//            valueModels[i] = new MineralStandardUPbRatioModel( "test " + i );
//        }
//        AbstractValueModelsPanelView testView = new MineralStandardUPbRatiosPanelViewNotEditable( valueModels);
//        testView.setBorder( new LineBorder( Color.red ) );
//
//
//        testFrame.add( testView );
//        testFrame.setVisible( true );
//    }
}
