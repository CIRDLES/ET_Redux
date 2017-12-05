/*
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.UPb_Redux.dialogs.parameterManagers;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerSubscribeInterface;
import org.earthtime.projects.ProjectInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class ShrimpProjectParamaetersManager extends AbstractProjectParametersManager {

    public ShrimpProjectParamaetersManager(ProjectInterface project, ProjectManagerSubscribeInterface projectManager, ETReduxFrame uPbReduxFrame) {
        super(project, projectManager, uPbReduxFrame);
    }

    @Override
    public void initView() {

        boolean editable = !rawDataProcessed;

        removeAll();

        this.setBackground(new Color(255, 222, 173));

        this.setOpaque(true);

        massSpecSetup = rawDataFileHandler.getMassSpec();

        JLabel titleLabel = new JLabel("This form will be used to review and confirm SHRIMP parameter settings and options.");
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        titleLabel.setBounds(leftMargin, 150, parentDimension.width - 50, 25);
        this.add(titleLabel);

        // temp setting
        fullPropagationRB = new JRadioButton("Full uncertainty propagation");
        fullPropagationRB.setSelected(true);
        
        initReferenceMaterialChooser();
        initToolBar(editable);
        
        monitorButton.setEnabled(false);
        repropagateButton.setEnabled(false);
    }

    @Override
    protected void saveAndLoadData(boolean loadData) {
        readyToProcessData = loadData;
    }
}
