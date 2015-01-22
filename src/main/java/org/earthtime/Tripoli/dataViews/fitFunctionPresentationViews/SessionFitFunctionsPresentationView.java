/*
 * SessionFitFunctionsPresentationView.java
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
package org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.sessionModels.AbstractSessionForStandardDataModel;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;

/**
 *
 * @author James F. Bowring
 */
public class SessionFitFunctionsPresentationView extends AbstractFitFunctionPresentationView {

    private final DataModelFitFunctionInterface sessionForStandardDataModel;

    /**
     * 
     *
     * @param sampleSessionDataView
     * @param sessionForStandardDataModel
     * @param targetDataModelView
     * @param dataPresentationMode
     * @param bounds
     */
    
    public SessionFitFunctionsPresentationView( //
            JLayeredPane sampleSessionDataView, //
            DataModelFitFunctionInterface sessionForStandardDataModel,//
            FitFunctionDataInterface targetDataModelView, //
            DataPresentationModeEnum dataPresentationMode, //
            Rectangle bounds) {

        super(targetDataModelView, bounds);

        setCursor(Cursor.getDefaultCursor());

        this.sampleSessionDataView = sampleSessionDataView;
        this.sessionForStandardDataModel = sessionForStandardDataModel;
        
        this.dataPresentationMode = dataPresentationMode;

        this.standardValue = ((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getStandardValue();

    }


    /**
     * 
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

    }

    /**
     * 
     */
    @Override
    public void preparePanel() {
        removeAll();

        createFitFunctionPanes(sessionForStandardDataModel, true);      
    }

}
