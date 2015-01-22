/*
 * SamplesCommonLeadPane.java
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.UPb_Redux.ReduxConstants;
import static org.earthtime.UPb_Redux.ReduxConstants.biMapOfIndexesToCommonLeadCorrectionSchemaNames;
import static org.earthtime.UPb_Redux.ReduxConstants.sansSerif_11_Plain;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.beans.ET_JButton;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA1;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA2;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeB1;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeB2;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeC;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeNONE;

/**
 *
 * @author James F. Bowring
 */
public class SamplesCommonLeadPane extends JLayeredPane {

    private final int WIDTH_OF_SAMPLE_DISPLAY_LIST = 200;
    private final int BOTTOM_MARGIN = 5;
    private final int LEFT_MARGIN = 5;
    private final int TOP_MARGIN = 55;
    private final int GRID_WIDTH = 975;
    private final JLabel titleLabel;
    private int myWidth;
    private final int myHeight;
    private final ArrayList<AbstractTripoliSample> tripoliSamples;
    private final JLayeredPane samplesCommonLead_pane;
    private final JList listOfSamplesForCommonLead;
    private final JScrollPane listView;
    private SamplesCommonLeadAssignmentGrid samplesCommonLeadAssignmentGrid;
    private final JScrollPane samplesCommonLeadScroll;
    private final JLabel sampleNameLabel;
    private final int workingHeight;
    private final JComboBox<String> initialPbModelChooser;
    // jan 2014

    /**
     *
     */
        protected String commonLeadCorrectionHighestLevel;
    private boolean expandedView;
    private JButton expanderButton;
    private ProjectManagerSubscribeInterface projectManager;

    /**
     *
     *
     * @param x
     * @param y
     * @param myWidth
     * @param myHeight
     * @param tripoliSamples
     * @param commonLeadCorrectionHighestLevel the value of
     * commonLeadCorrectionHighestLevel
     * @param projectManager
     */
    public SamplesCommonLeadPane(//
            int x, int y, int myWidth, int myHeight, //
            ArrayList<AbstractTripoliSample> tripoliSamples, //
            String commonLeadCorrectionHighestLevel, //
            ProjectManagerSubscribeInterface projectManager) {

        this.titleLabel = new JLabel("Select Sample:");
        this.titleLabel.setBounds(LEFT_MARGIN, TOP_MARGIN - 20, 250, 15);
        this.add(this.titleLabel, DEFAULT_LAYER);

        this.myWidth = myWidth;
        this.myHeight = myHeight;

        this.setBounds(x, y, myWidth, myHeight);
        this.setOpaque(true);

        this.tripoliSamples = tripoliSamples;

        this.commonLeadCorrectionHighestLevel = commonLeadCorrectionHighestLevel;
        this.projectManager = projectManager;

        this.expandedView = false;

        samplesCommonLead_pane = new JLayeredPane();
        workingHeight = myHeight - BOTTOM_MARGIN - TOP_MARGIN;

        // create listOfSamplesForCommonLead of samples that user will choose from to populate common lead assignemnt grid with fractions
        DefaultListModel<AbstractTripoliSample>  listOfSamplesForPbCorrectionModel = new DefaultListModel<>();
        Iterator<AbstractTripoliSample> tripoliSamplesIterator = tripoliSamples.iterator();
        while (tripoliSamplesIterator.hasNext()) {
            // april 2014 - leave out primary standards
            AbstractTripoliSample ts = tripoliSamplesIterator.next();
            if (!ts.isPrimaryStandard()) {
                listOfSamplesForPbCorrectionModel.addElement(ts);
            }
        }

        listOfSamplesForCommonLead = new JList<>(listOfSamplesForPbCorrectionModel);
        listOfSamplesForCommonLead.setFont(sansSerif_11_Plain);
        listOfSamplesForCommonLead.setVisibleRowCount(-1);
        listOfSamplesForCommonLead.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // scehma controls for grid; N, A1, A2, B1, B2, C, D
        JLabel schemesLabel = new JLabel("<html><u>Schemas:</u></html>");
        schemesLabel.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 140, 0, 125, 15);
        this.add(schemesLabel, DEFAULT_LAYER);

        JLabel schemesNoneLabel = new JLabel("<html>N</html>");
        schemesNoneLabel.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesNoneLabel.setHorizontalAlignment(JLabel.CENTER);
        schemesNoneLabel.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 142, TOP_MARGIN - 30, 15, 15);
        this.add(schemesNoneLabel, DEFAULT_LAYER);

        JLabel schemesA1Label = new JLabel("<html>A<sup>1</sup></html>");
        schemesA1Label.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesA1Label.setHorizontalAlignment(JLabel.CENTER);
        schemesA1Label.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 157, TOP_MARGIN - 31, 15, 15);
        this.add(schemesA1Label, DEFAULT_LAYER);

        JLabel schemesA2Label = new JLabel("<html>A<sup>2</sup></html>");
        schemesA2Label.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesA2Label.setHorizontalAlignment(JLabel.CENTER);
        schemesA2Label.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 172, TOP_MARGIN - 31, 15, 15);
        this.add(schemesA2Label, DEFAULT_LAYER);

        JLabel schemesB1Label = new JLabel("<html>B<sup>1</sup></html>");
        schemesB1Label.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesB1Label.setHorizontalAlignment(JLabel.CENTER);
        schemesB1Label.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 187, TOP_MARGIN - 31, 15, 15);
        this.add(schemesB1Label, DEFAULT_LAYER);

        JLabel schemesB2Label = new JLabel("<html>B<sup>2</sup></html>");
        schemesB2Label.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesB2Label.setHorizontalAlignment(JLabel.CENTER);
        schemesB2Label.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 202, TOP_MARGIN - 31, 15, 15);
        this.add(schemesB2Label, DEFAULT_LAYER);

        JLabel schemesCLabel = new JLabel("<html>C</html>");
        schemesCLabel.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesCLabel.setHorizontalAlignment(JLabel.CENTER);
        schemesCLabel.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 217, TOP_MARGIN - 30, 15, 15);
        this.add(schemesCLabel, DEFAULT_LAYER);

        JLabel schemesDLabel = new JLabel("<html>D</html>");
        schemesDLabel.setFont(ReduxConstants.sansSerif_10_Bold);
        schemesDLabel.setHorizontalAlignment(JLabel.CENTER);
        schemesDLabel.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 232, TOP_MARGIN - 30, 15, 15);
        this.add(schemesDLabel, DEFAULT_LAYER);

        // initial Pb Models
        JLabel initialPbModelsLabel = new JLabel("<html><u>Initial Pb Models:</u></html>");
        initialPbModelsLabel.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 250, 0, 125, 15);
        this.add(initialPbModelsLabel, DEFAULT_LAYER);

        // master initial pb models chooser
        initialPbModelChooser = new JComboBox<>();
        ArrayList<AbstractRatiosDataModel> initialPbModels = ReduxLabData.getInstance().getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
            // StaceyKramers at top and = default
            if (initialPbModels.get(i) instanceof StaceyKramersInitialPbModelET) {
                initialPbModelChooser.insertItemAt(initialPbModels.get(i).getReduxLabDataElementName(), 0);
            } else {
                // backward compatible hide old placeholder name
                if (!initialPbModels.get(i).getReduxLabDataElementName().startsWith("Placeholder")) {
                    initialPbModelChooser.addItem(initialPbModels.get(i).getReduxLabDataElementName());
                }
            }
        }

        initialPbModelChooser.setSelectedIndex(0);
        initialPbModelChooser.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 250, TOP_MARGIN - 40, 165, 25);
        initialPbModelChooser.setOpaque(false);
        initialPbModelChooser.setFont(sansSerif_11_Plain);
        initialPbModelChooser.setBackground(Color.white);
        this.add(initialPbModelChooser);

        // expands/contracts all fractions
        expanderButton = new ET_JButton(expandedView ? "-" : "+");
        expanderButton.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 925, TOP_MARGIN - 25, 20, 20);
        expanderButton.setFont(ReduxConstants.sansSerif_12_Bold);
        expanderButton.addActionListener((ActionEvent e) -> {
            expandedView = !expandedView;
            ((AbstractButton) e.getSource()).setText(expandedView ? "-" : "+");
            samplesCommonLeadAssignmentGrid.toggleAllFractionViewsExpanded(expandedView);
            samplesCommonLeadAssignmentGrid.refreshLayoutOfGrid();
        });
        this.add(expanderButton);

        fillButtonsFactory();

        // scroll pane for common lead grid
        samplesCommonLeadScroll = new JScrollPane();
        samplesCommonLeadScroll.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10, TOP_MARGIN, GRID_WIDTH, workingHeight);
        samplesCommonLeadScroll.setAutoscrolls(true);
        samplesCommonLeadScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(samplesCommonLeadScroll);

        sampleNameLabel = new JLabel("Fractions:");
        sampleNameLabel.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10, TOP_MARGIN - 20, 250, 15);
        this.add(sampleNameLabel, DEFAULT_LAYER);

        // add listener for sample choice
        listOfSamplesForCommonLead.addListSelectionListener((ListSelectionEvent evt) -> {
            JList list = (JList) evt.getSource();
            AbstractTripoliSample tripoliSample = (AbstractTripoliSample) list.getSelectedValue();
            setUpGrid(tripoliSample);
        });

        listView = new JScrollPane(listOfSamplesForCommonLead);
        listView.setBounds(LEFT_MARGIN, TOP_MARGIN, WIDTH_OF_SAMPLE_DISPLAY_LIST - 20, workingHeight);
        listOfSamplesForCommonLead.setSelectedIndex(0);

        add(listView);

        refreshSampleFractionListsPane();

    }

    private void setUpGrid(AbstractTripoliSample tripoliSample) {

        boolean needNewGrid = true;
        if (samplesCommonLeadAssignmentGrid != null) {
            if (samplesCommonLeadAssignmentGrid.getTripoliSample().equals(tripoliSample)) {
                samplesCommonLeadAssignmentGrid.refreshGrid();
                expandedView = samplesCommonLeadAssignmentGrid.determineExpandedViewState();
                expanderButton.setText(expandedView ? "-" : "+");
                needNewGrid = false;
            }
        }

        if (needNewGrid) {
            sampleNameLabel.setText("Fractions: ");
            samplesCommonLeadAssignmentGrid = new SamplesCommonLeadAssignmentGrid(tripoliSample, GRID_WIDTH - 2);
            samplesCommonLeadAssignmentGrid.setBounds(0, 0, GRID_WIDTH, workingHeight);

            //samplesCommonLeadAssignmentGrid.refreshLayoutOfGrid();
            samplesCommonLeadScroll.setViewportView(samplesCommonLeadAssignmentGrid);
        }
        samplesCommonLeadScroll.validate();
    }

    /**
     * @param myWidth the myWidth to set
     */
    public void setMyWidth(int myWidth) {
        this.myWidth = myWidth;
    }

    /**
     *
     */
    public final void refreshSampleFractionListsPane() {
        this.setSize(myWidth, myHeight);
        samplesCommonLead_pane.setPreferredSize(//
                new Dimension( //
                        Math.max(myWidth - 25 - LEFT_MARGIN, tripoliSamples.size() * (WIDTH_OF_SAMPLE_DISPLAY_LIST + 25)), //
                        myHeight - BOTTOM_MARGIN - TOP_MARGIN - 10));

        validate();
    }

    /**
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    private void fillButtonsFactory() {

        // creates fill buttons for schema choices and initil pb model chooser
        ActionListener fillButtonActionListener = new FillButtonActionListener();

        int commonLeadCorrectionHighestLevelIndex = biMapOfIndexesToCommonLeadCorrectionSchemaNames.inverse()//
                .get(commonLeadCorrectionHighestLevel);

        ET_JButton[] schemaFIllButtons = new ET_JButton[commonLeadCorrectionHighestLevelIndex + 1];
        for (int i = 0; i < commonLeadCorrectionHighestLevelIndex + 1; i++) {
            schemaFIllButtons[i] = new ET_JButton("\u2193");
            schemaFIllButtons[i].setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 142 + (15 * i), TOP_MARGIN - 17, 15, 15);
            schemaFIllButtons[i].setActionCommand(biMapOfIndexesToCommonLeadCorrectionSchemaNames.get(i));
            schemaFIllButtons[i].addActionListener(fillButtonActionListener);
            this.add(schemaFIllButtons[i], DEFAULT_LAYER);
        }
        
        // initial pb model
        ET_JButton initialPbModelFill = new ET_JButton("\u2193  \u2193      F I L L     \u2193  \u2193");
        initialPbModelFill.setBounds(LEFT_MARGIN + WIDTH_OF_SAMPLE_DISPLAY_LIST - 10 + 255, TOP_MARGIN - 17, 152, 15);
        initialPbModelFill.setActionCommand("InitialPb");
        initialPbModelFill.addActionListener(fillButtonActionListener);
        this.add(initialPbModelFill, DEFAULT_LAYER);

    }

    class FillButtonActionListener implements ActionListener {

        private AbstractTripoliSample tripoliSample;

        public FillButtonActionListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tripoliSample = (AbstractTripoliSample) listOfSamplesForCommonLead.getSelectedValue();

            switch (((AbstractButton) e.getSource()).getActionCommand()) {
                case "A1":
                    tripoliSample.setCommonLeadLossCorrectionSchemaForAllFractions(CommonLeadLossCorrectionSchemeA1.getInstance());
                    setDefaultInitialPbModel();
                    break;
                case "A2":
                    tripoliSample.setCommonLeadLossCorrectionSchemaForAllFractions(CommonLeadLossCorrectionSchemeA2.getInstance());
                    tripoliSample.setInitialPbModelForAllFractions(StaceyKramersInitialPbModelET.getStaceyKramersInstance());
                    break;
                case "B1":
                    tripoliSample.setCommonLeadLossCorrectionSchemaForAllFractions(CommonLeadLossCorrectionSchemeB1.getInstance());
                    setDefaultInitialPbModel();
                    break;
                case "B2":
                    tripoliSample.setCommonLeadLossCorrectionSchemaForAllFractions(CommonLeadLossCorrectionSchemeB2.getInstance());
                    setDefaultInitialPbModel();
                    break;
                case "C":
                    tripoliSample.setCommonLeadLossCorrectionSchemaForAllFractions(CommonLeadLossCorrectionSchemeC.getInstance());
                    setDefaultInitialPbModel();
                    break;
                case "NONE":
                    tripoliSample.setCommonLeadLossCorrectionSchemaForAllFractions(CommonLeadLossCorrectionSchemeNONE.getInstance());
                    break;

                case "InitialPb":
                    try {
                        AbstractRatiosDataModel initialPbModelET = ReduxLabData.getInstance().getAnInitialPbModel((String) initialPbModelChooser.getSelectedItem());
                        tripoliSample.setInitialPbModelForAllFractions(initialPbModelET);
                    } catch (BadLabDataException badLabDataException) {
                    }
                    break;

            }

            // refresh view
            setUpGrid(tripoliSample);
            samplesCommonLeadAssignmentGrid.refreshLayoutOfGrid();
            
        }

        private void setDefaultInitialPbModel() {
            tripoliSample.setDefaultInitialPbModelForAllFractions();
        }

    }
}
