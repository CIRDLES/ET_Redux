/*
 * TripoliFractionCommonLeadManager.java
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.SortedMap;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import static org.earthtime.UPb_Redux.ReduxConstants.biMapOfIndexesToCommonLeadCorrectionSchemaNames;
import static org.earthtime.UPb_Redux.ReduxConstants.sansSerif_10_Plain;
import static org.earthtime.UPb_Redux.ReduxConstants.sansSerif_11_Plain;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.RadDatesForPbCorrSynchEnum;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.PlaceholderInitialPb76Model;
import org.earthtime.ratioDataModels.initialPbModelsET.PlaceholderInitialPbModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA1;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA2;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeB1;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeB2;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeC;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeD;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeNONE;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.InitialPbTripoliDataView;

/**
 *
 * @author James F. Bowring
 */
public class TripoliFractionCommonLeadManager extends JPanel implements InitialPbModelSynchInterface {

    private final TripoliFraction tripoliFraction;
    private final int height;
    private final int width;
    private final static int ROW_HEIGHT = 25;
    private final String openCircle = "\u25CB";
    private final String filledCircle = "\u25CF";
    private final String notAvailable = "\u25CB";
    private JLabel fractionIDLabel;
    private JComboBox<String> initialPbModelChooser;
    private JLabel[] schemeRadioChoosers;
    private static final boolean isLinuxOS = (System.getProperty("os.name").toLowerCase().startsWith("linux"));
    private static final int COMPONENT_HEIGHT = 18;
    private JLayeredPane initialPbModelPanel;
    private AbstractRatiosDataView initialPbTripoliDataView;
    private final boolean showAbsUnct;

    /**
     *
     */
    protected boolean expandedView;
    private final SamplesCommonLeadAssignmentGridLayoutInterface parentGrid;
    private JButton expanderButton;
    private JLabel schemeResultsLabel;
    private JLabel synchronizeDateLabel;
    private JComboBox<RadDatesForPbCorrSynchEnum> synchronizedDateChooser;

    /**
     *
     * @param parentGrid the value of parentGrid
     * @param tripoliFraction the value of tripoliFraction
     * @param x the value of x
     * @param y the value of y
     * @param width the value of width
     * @param height the value of height
     */
    public TripoliFractionCommonLeadManager(//
            SamplesCommonLeadAssignmentGridLayoutInterface parentGrid, TripoliFraction tripoliFraction, int x, int y, int width, int height) {
        super();

        this.parentGrid = parentGrid;
        this.tripoliFraction = tripoliFraction;
        this.height = height;
        this.width = width;

        this.initialPbModelPanel = null;
        showAbsUnct = false;
        this.expandedView = false;

        setOpaque(true);
        setBackground(Color.white);
        setBorder(null);
        setBounds(x, y, width, height);

        setLayout(null);

        schemeLayoutFactory();
    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // bottom border
        g2d.setColor(Color.black);
        g2d.drawLine(0, getBounds().height - 1, getWidth() - 1, getBounds().height - 1);
    }

    /**
     *
     * @return
     */
    public int getHeightIntialPbModelDataView() {
        return expandedView ? (tripoliFraction.hasSingleRatioInitialPbModelET() ? 100 : 110) : ROW_HEIGHT;
    }

    private void schemeLayoutFactory() {

        this.removeAll();
        this.validate();

        fractionIDLabel = new JLabel(tripoliFraction.getFractionID());
        fractionIDLabel.setBounds(2, 0, 175, COMPONENT_HEIGHT);
        fractionIDLabel.setFont(sansSerif_11_Plain);
        this.add(fractionIDLabel);

        // custom radio buttons for schemes 
        int commonLeadCorrectionHighestLevel = biMapOfIndexesToCommonLeadCorrectionSchemaNames.inverse()//
                .get(tripoliFraction.getCommonLeadCorrectionHighestLevel());
        SchemeRadioChoosersMouseListener schemeRadioChoosersMouseListener = new SchemeRadioChoosersMouseListener();
        schemeRadioChoosers = new JLabel[7];
        for (int i = 0; i < schemeRadioChoosers.length; i++) {
            schemeRadioChoosers[i] = new JLabel();
            schemeRadioChoosers[i].setVerticalAlignment(JLabel.CENTER);

            if (isLinuxOS) {
                schemeRadioChoosers[i].setFont(new Font("Monospaced", Font.BOLD, COMPONENT_HEIGHT));
            } else {
                schemeRadioChoosers[i].setFont(new Font("Arial", Font.BOLD, COMPONENT_HEIGHT));
            }

            // determine which schema are available
            if (i <= commonLeadCorrectionHighestLevel) {
                schemeRadioChoosers[i].setText(openCircle);
                schemeRadioChoosers[i].setForeground(Color.blue);
                schemeRadioChoosers[i].addMouseListener(schemeRadioChoosersMouseListener);
            } else {
                schemeRadioChoosers[i].setText(notAvailable);
                schemeRadioChoosers[i].setForeground(Color.red);
            }

            schemeRadioChoosers[i].setBounds(140 + i * 15, 0, 15, COMPONENT_HEIGHT);
            schemeRadioChoosers[i].setName(biMapOfIndexesToCommonLeadCorrectionSchemaNames.get(i));
            this.add(schemeRadioChoosers[i]);
        }

        // before initial pb model chooser
        schemeResultsLabel = new JLabel("No scheme chosen.");
        schemeResultsLabel.setFont(sansSerif_10_Plain);
        schemeResultsLabel.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        schemeResultsLabel.setBounds(408, 0, 510, COMPONENT_HEIGHT);
        this.add(schemeResultsLabel);

        // for scheme B2
        synchronizeDateLabel = new JLabel("Synchronize this date:");
        synchronizeDateLabel.setFont(sansSerif_10_Plain);
        synchronizeDateLabel.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        synchronizeDateLabel.setBounds(650, 0, 125, COMPONENT_HEIGHT);
        this.add(synchronizeDateLabel);

        // need dropdown of dates to choose for synchronization
        synchronizedDateChooser = new JComboBox<>();
        synchronizedDateChooser.setModel(new DefaultComboBoxModel<>(RadDatesForPbCorrSynchEnum.values()));
        synchronizedDateChooser.setFont(sansSerif_10_Plain);
        synchronizedDateChooser.setBounds(775, 1, 125, 23);
        synchronizedDateChooser.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    tripoliFraction.setRadDateForSKSynch((RadDatesForPbCorrSynchEnum)evt.getItem());
                }
            }
        });
        this.add(synchronizedDateChooser);

//        parametersLayoutFactory();
        // set up InitialPbModel chooser
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

        initialPbModelChooser.setSelectedIndex(-1);
        initialPbModelChooser.setBounds(245, 1, 165, 23);
        initialPbModelChooser.setOpaque(false);
        initialPbModelChooser.setFont(sansSerif_11_Plain);
        initialPbModelChooser.setBackground(Color.white);

        initialPbModelChooser.addItemListener(new InitialPbModelItemListener());
        this.add(initialPbModelChooser);

        if (tripoliFraction.hasCommonLeadLossCorrectionScheme()) {
            initialPbModelChooser.setSelectedItem(tripoliFraction.getInitialPbModelET().getReduxLabDataElementName());
        }

        expanderButton = new ET_JButton(expandedView ? "-" : "+");
        expanderButton.setBounds(width - 20, 4, 15, 15);
        expanderButton.addActionListener((ActionEvent e) -> {
            expandedView = !expandedView;
            ((AbstractButton) e.getSource()).setText(expandedView ? "-" : "+");
            initialPbModelPanelLayoutFactory();
            parentGrid.refreshLayoutOfGrid();
        });
        this.add(expanderButton);
        expanderButton.setVisible(tripoliFraction.hasCommonLeadLossCorrectionScheme());

        customizeParameterFields();

    }

    /**
     *
     */
    public void initialPbModelPanelLayoutFactory() {

        if (initialPbModelPanel != null) {
            remove(initialPbModelPanel);
            initialPbModelPanel = null;
        }

        // update expandedview
        expandedView = (tripoliFraction.hasCommonLeadLossCorrectionScheme() ? expandedView : false);

        // provides container for valuemodels and correlation matrices
        // if fraction is assigned SK, we create a temp model for viewing here without adding a new model to the fraction for each SK
        AbstractRatiosDataModel currentModel = tripoliFraction.getInitialPbModelET();
        if (currentModel instanceof StaceyKramersInitialPbModelET) {
            currentModel = new StaceyKramersInitialPbModelET(tripoliFraction);
            ((StaceyKramersInitialPbModelET) currentModel).resetModelFromTripoliFraction();
        }

        if (currentModel instanceof PlaceholderInitialPbModel) {
            currentModel = new PlaceholderInitialPbModel(tripoliFraction);
            ((PlaceholderInitialPbModel) currentModel).resetModelFromTripoliFraction();
        }

        if (currentModel instanceof PlaceholderInitialPb76Model) {
            currentModel = new PlaceholderInitialPb76Model(tripoliFraction);
            ((PlaceholderInitialPb76Model) currentModel).resetModelFromTripoliFraction();
        }

        if (expandedView) {
            boolean editable = (currentModel instanceof StaceyKramersInitialPbModelET) || (currentModel instanceof PlaceholderInitialPbModel || (currentModel instanceof PlaceholderInitialPb76Model));
            initialPbTripoliDataView = //
                    new InitialPbTripoliDataView(//
                            this, currentModel, editable, null, true, showAbsUnct, !tripoliFraction.getCommonLeadLossCorrectionScheme().doesCalculateSKEstimatedDate());
            initialPbTripoliDataView.setBounds(0, 3, width, height - ROW_HEIGHT);

            initialPbModelPanel = new JLayeredPane();
            initialPbModelPanel.setBounds(0, 22, width, initialPbTripoliDataView.getHeight() + ROW_HEIGHT);
            initialPbModelPanel.add(initialPbTripoliDataView);
            initialPbModelPanel.validate();

            this.add(initialPbModelPanel);
        }

        customizeParameterFields();

//        try {
//            parentGrid.refreshLayoutOfGrid();
//        } catch (Exception e) {
//        }
        validate();
        repaint();

    }

    private void updateModels() {
        AbstractRatiosDataModel currentModel = tripoliFraction.getInitialPbModelET();

        // note use interface here
        if (currentModel instanceof StaceyKramersInitialPbModelET) {
            ((StaceyKramersInitialPbModelET) currentModel).setTripoliFraction(tripoliFraction);
            ((StaceyKramersInitialPbModelET) currentModel).resetModelFromTripoliFraction();
        }
        if (currentModel instanceof PlaceholderInitialPbModel) {
            ((PlaceholderInitialPbModel) currentModel).setTripoliFraction(tripoliFraction);
            ((PlaceholderInitialPbModel) currentModel).resetModelFromTripoliFraction();
        }

        if (currentModel instanceof PlaceholderInitialPb76Model) {
            ((PlaceholderInitialPb76Model) currentModel).setTripoliFraction(tripoliFraction);
            ((PlaceholderInitialPb76Model) currentModel).resetModelFromTripoliFraction();
        }

        updateCalculatedParameters(currentModel);

        try {
            initialPbTripoliDataView.updateModelView(currentModel);
        } catch (Exception e) {
        }
        validate();
    }

    /**
     *
     * @param parameterModel
     */
    @Override
    public void updateCalculatedParameters(AbstractRatiosDataModel parameterModel) {
        tripoliFraction.setInitialPbSchemeA_r207_206c(((InitialPbModelET) parameterModel).calculateR207_206c());

        tripoliFraction.setInitialPbSchemeB_R206_204c(parameterModel.getDatumByName("r206_204c"));
        tripoliFraction.setInitialPbSchemeB_R207_204c(parameterModel.getDatumByName("r207_204c"));
        tripoliFraction.setInitialPbSchemeB_R208_204c(parameterModel.getDatumByName("r208_204c"));

        fillParameterFields();
    }

//    private void parametersLayoutFactory() {
//
//        TextFieldFocusListener parameterTextFieldFocusListener = new TextFieldFocusListener();
//        TextFieldEnterKeyListener parameterTextEnterKeyListener = new TextFieldEnterKeyListener();
//        try {
//
//        } catch (Exception e) {
//        }
//    }
//    private void parameterTextFieldSetupFactory(//
//            JTextField jTextField, FocusListener textFieldFocusListener, TextFieldEnterKeyListener parameterTextEnterKeyListener, String name, int xLayout) {
//
//        jTextField.setFont(new java.awt.Font("Monospaced", 1, 10));
//        jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
//        jTextField.setDocument(new DialogEditor.DoubleDocument(jTextField, true));
//        jTextField.setName(name);
//        jTextField.addFocusListener(textFieldFocusListener);
//        jTextField.addKeyListener(parameterTextEnterKeyListener);
//        jTextField.setVisible(true);
//
//        jTextField.setBounds(xLayout, 0, 60, COMPONENT_HEIGHT);
//        this.add(jTextField);
//    }
    private void parametersHidingFactory() {

        this.revalidate();
        this.repaint();
    }

    /**
     * @return the expandedView
     */
    public boolean isExpandedView() {
        return expandedView;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void synchToThisInitialPbModelSK(SortedMap<String, BigDecimal> parameters) {
        parentGrid.synchAllSKModelsToThis(parameters);
    }

    @Override
    public void synchToThisSynchronizedSKDate(RadDatesForPbCorrSynchEnum radDateForSKSynch) {
        parentGrid.synchAllSynchronizedSKDateToThis(radDateForSKSynch);
    }

    /**
     *
     * @param tripoliFraction
     */
    @Override
    public void synchToThisInitialPbModelPlaceHolderFromFraction(TripoliFraction tripoliFraction) {
        parentGrid.synchAllPlaceHolderModelsToThisFraction(tripoliFraction);
    }

    /**
     * @param expandedView the expandedView to set
     */
    public void setExpandedView(boolean expandedView) {
        if (tripoliFraction.hasCommonLeadLossCorrectionScheme()) {
            this.expandedView = expandedView;
            this.expanderButton.setText(expandedView ? "-" : "+");
            if (expandedView) {
                initialPbModelPanelLayoutFactory();
            }
        }
    }

    /**
     * @return the initialPbTripoliDataView
     */
    public AbstractRatiosDataView getInitialPbTripoliDataView() {
        return initialPbTripoliDataView;
    }

    class InitialPbModelItemListener implements ItemListener {

        public InitialPbModelItemListener() {
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {

            // Get the chosen initialPbModelET
            AbstractRatiosDataModel initialPbModelET = null;
            try {
                initialPbModelET = ReduxLabData.getInstance().getAnInitialPbModel((String) evt.getItem());

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                tripoliFraction.setInitialPbModelET(initialPbModelET);
                updateModels();
                initialPbModelPanelLayoutFactory();

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    class TextFieldFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent evt) {

            updateModels();
        }

    }

    class TextFieldEnterKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            // System.out.println("keyT " + e.getKeyCode() + "  " + KeyEvent.VK_ENTER);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // System.out.println("keyP " + e.getKeyCode() + "  " + KeyEvent.VK_ENTER);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                ((Component) e.getSource()).dispatchEvent(new FocusEvent(((Component) e.getSource()), FocusEvent.FOCUS_LOST));
            }
        }
    }

    private void clearAllSchemaRadioButtons() {
        for (int i = 0; i < schemeRadioChoosers.length; i++) {
            if (!schemeRadioChoosers[i].getText().equalsIgnoreCase(notAvailable)) {
                schemeRadioChoosers[i].setText(openCircle);
            }
        }
    }

    class SchemeRadioChoosersMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            // if label is green then toggle between selected and not selected = open and filled
            JLabel myLabel = (JLabel) e.getSource();

            if (myLabel.getText().equalsIgnoreCase(openCircle)) {
                // be sure all are open first
                clearAllSchemaRadioButtons();
                myLabel.setText(filledCircle);
                switch (myLabel.getName()) {
                    case "A1":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeA1.getInstance());
                        break;
                    case "A2":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeA2.getInstance());
                        tripoliFraction.setInitialPbModelET(StaceyKramersInitialPbModelET.getStaceyKramersInstance());
                        break;
                    case "B1":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeB1.getInstance());
                        break;
                    case "B2":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeB2.getInstance());
                        tripoliFraction.setInitialPbModelET(StaceyKramersInitialPbModelET.getStaceyKramersInstance());
                        break;
                    case "C":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeC.getInstance());
                        break;
                    case "D":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeD.getInstance());
                        break;
                    case "NONE":
                        tripoliFraction.setCommonLeadLossCorrectionScheme(CommonLeadLossCorrectionSchemeNONE.getInstance());
                        expandedView = false;
                        expanderButton.setText("+");
                        parentGrid.refreshLayoutOfGrid();
                        break;
                }

                updateModels();
                initialPbModelPanelLayoutFactory();
                initialPbModelChooser.setSelectedItem(tripoliFraction.getInitialPbModelET().getReduxLabDataElementName());
                initialPbModelChooser.repaint();

            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private void customizeParameterFields() {

        clearAllSchemaRadioButtons();
        // set selected radiochooser
        schemeRadioChoosers[biMapOfIndexesToCommonLeadCorrectionSchemaNames.inverse()//
                .get(tripoliFraction.getCommonLeadLossCorrectionScheme().getName())].setText(filledCircle);

        try {
            expanderButton.setVisible(tripoliFraction.hasCommonLeadLossCorrectionScheme());
            expanderButton.setText(expandedView ? "-" : "+");
        } catch (Exception e) {
        }

        // determine which fields show and which are editable
        initialPbModelChooser.setVisible(tripoliFraction.hasCommonLeadLossCorrectionScheme());
        // update chooser
        if (tripoliFraction.hasCommonLeadLossCorrectionScheme()) {//.getInitialPbModelET() != null) {
            initialPbModelChooser.setSelectedItem(tripoliFraction.getInitialPbModelET().getReduxLabDataElementName());
        }

        initialPbModelChooser.setEnabled(!tripoliFraction.getCommonLeadLossCorrectionScheme().getName().startsWith("A2")//
                && !tripoliFraction.getCommonLeadLossCorrectionScheme().getName().startsWith("B2"));

        parametersHidingFactory();
//        if (!tripoliFraction.hasCommonLeadLossCorrectionScheme()) {
//            // everything disappears
////            parametersHidingFactory();
//
//        } else {
//            //parametersLayoutFactory();
//            //fillParameterFields();
//        }

        fillParameterFields();

    }

    /**
     *
     */
    public void fillParameterFields() {

        // if SK chosen then only SK fields show and in this case the estimated date shows only if 
        //  common lead correction scheme does not doesCalculateSKEstimatedDate
//        AbstractRatiosDataModel initialPbModel = tripoliFraction.getInitialPbModelET();
//        boolean staceyKramerChosen = (initialPbModel instanceof StaceyKramersInitialPbModelET);
        boolean doesCalculateSKEstimatedDate = tripoliFraction.getCommonLeadLossCorrectionScheme().doesCalculateSKEstimatedDate();
        boolean commonLeadSchemeisFlavorNone = !tripoliFraction.hasCommonLeadLossCorrectionScheme();
        boolean commonLeadSchemeIsFlavorA = tripoliFraction.getCommonLeadLossCorrectionScheme().getName().startsWith("A");

        if (commonLeadSchemeisFlavorNone) {
            schemeResultsLabel.setText("No scheme chosen.");
        } else if (doesCalculateSKEstimatedDate) {
            schemeResultsLabel.setText("Calculates SK est date after reduction.");
        } else if (commonLeadSchemeIsFlavorA) {
            schemeResultsLabel.setText(//
                    "<html>"//
                    + tripoliFraction.getInitialPbSchemeA_r207_206c().formatNameValuePCTVarSysTightReadyForHTML()//
                    + "</html>");
        } else {
            schemeResultsLabel.setText(//
                    "<html>"//
                    + tripoliFraction.getInitialPbSchemeB_R206_204c().formatNameValuePCTVarSysTightReadyForHTML()//
                    + "&nbsp;&nbsp;&nbsp;" + tripoliFraction.getInitialPbSchemeB_R207_204c().formatNameValuePCTVarSysTightReadyForHTML()//
                    + "&nbsp;&nbsp;&nbsp;" + tripoliFraction.getInitialPbSchemeB_R208_204c().formatNameValuePCTVarSysTightReadyForHTML()//
                    + "</html>");

        }

        schemeResultsLabel.repaint();

        // dec 2014
        if (tripoliFraction.getCommonLeadLossCorrectionScheme().getName().startsWith("B2")) {
            synchronizedDateChooser.setSelectedItem(tripoliFraction.getRadDateForSKSynch());
            synchronizedDateChooser.setVisible(true);
            synchronizeDateLabel.setVisible(true);
        } else {
            synchronizedDateChooser.setVisible(false);
            synchronizeDateLabel.setVisible(false);
        }
    }

}
