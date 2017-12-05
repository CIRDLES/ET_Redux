/*
 * AbstractRatiosDataView.java
 *
 * Created Mar 2, 2012
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
package org.earthtime.ratioDataViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModelPanelViews.AbstractValueModelsPanelView;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.matrices.matrixViews.AbstractMatrixGridView;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractRatiosDataView extends JLayeredPane implements DataEntryDetectorInterface {

    /**
     *
     */
    protected AbstractRatiosDataModel dataModel;

    /**
     *
     */
    protected Dimension parentDimension;
    /**
     *
     */
    protected AbstractValueModelsPanelView valueModelsPanelView;
    /**
     *
     */
    protected AbstractMatrixGridView covarianceVarUnctMatrixView;

    /**
     *
     */
    protected AbstractMatrixGridView covarianceSysUnctMatrixView;
    /**
     *
     */
    protected AbstractMatrixGridView correlationVarUnctMatrixView;

    /**
     *
     */
    protected AbstractMatrixGridView correlationSysUnctMatrixView;
    /**
     *
     */
    protected JTabbedPane dataTabs;

    /**
     *
     */
    protected JTabbedPane matrixTabs;

    /**
     *
     */
    protected JScrollPane covarianceMatrixViewScrollPane;

    /**
     *
     */
    protected JScrollPane correlationMatrixViewScrollPane;
    private JTextField modelNameTextBox;
    private JTextField labNameTextBox;
    private JTextField versionTextBox;
    private JTextField minorVersionTextBox;
    private JTextField dateCertifiedBox;
    private JTextArea referenceTextArea;
    private JTextArea commentTextArea;

    /**
     *
     *
     * @param dataModel
     * @param parentDimension the value of parentDimension
     */
    public AbstractRatiosDataView(AbstractRatiosDataModel dataModel, Dimension parentDimension) {
        this.dataModel = dataModel;

        if (parentDimension == null) {
            this.parentDimension = new Dimension(600, 620);
        } else {
            this.parentDimension = parentDimension;
        }

        setOpaqueWithLightGray(this);
    }

    /**
     *
     *
     * @param editable the value of editable
     */
    protected void initView(boolean editable) {

        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) { //Nimbus (original), Motif, Metal
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        removeAll();

        Font DataFont = ReduxConstants.sansSerif_11_Bold;

        // removed March 2016 because our public face needs to show "reference material" instead of "mineral standard"
//        JLabel modelTypeLabel = new JLabel("Model Type:   " + dataModel.getClassNameAliasForXML());
//        modelTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        modelTypeLabel.setFont(DataFont);
//        modelTypeLabel.setBounds(5, 5, 250, 25);
//        this.add(modelTypeLabel);
        JLabel canEditLabel = new JLabel(dataModel.isImmutable() ? "not editable" : "editable");
        canEditLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        canEditLabel.setFont(DataFont);
        canEditLabel.setBounds(485, 5, 75, 25);
        canEditLabel.setForeground(Color.red);
        this.add(canEditLabel);

        JLabel modelNameLabel = new JLabel("Model Name: ");
        modelNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        modelNameLabel.setFont(DataFont);
        modelNameLabel.setBounds(5, 35, 100, 25);
        this.add(modelNameLabel);

        modelNameTextBox = new JTextField();
        modelNameTextBox.setDocument(new DialogEditor.UnDoAbleDocument(modelNameTextBox, editable));
        modelNameTextBox.setText(dataModel.getModelName());
        modelNameTextBox.setFont(DataFont);
        modelNameTextBox.setBounds(105, 35, 350, 25);
        this.add(modelNameTextBox);

        JLabel labNameLabel = new JLabel("Lab Name: ");
        labNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        labNameLabel.setFont(DataFont);
        labNameLabel.setBounds(5, 60, 100, 25);
        this.add(labNameLabel);

        labNameTextBox = new JTextField();
        labNameTextBox.setDocument(new DialogEditor.UnDoAbleDocument(labNameTextBox, editable));
        labNameTextBox.setText(dataModel.getLabName());
        labNameTextBox.setFont(DataFont);
        labNameTextBox.setBounds(105, 60, 225, 25);
        this.add(labNameTextBox);

        JLabel versionLabel = new JLabel("Version: ");
        versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        versionLabel.setFont(DataFont);
        versionLabel.setBounds(420, 35, 90, 25);
        this.add(versionLabel);

        versionTextBox = new JTextField();
        versionTextBox.setDocument(new DialogEditor.IntegerDocument(versionTextBox, editable));
        versionTextBox.setText(Integer.toString(dataModel.getVersionNumber()));
        versionTextBox.setFont(DataFont);
        versionTextBox.setHorizontalAlignment(JTextField.CENTER);
        versionTextBox.setBounds(505, 35, 25, 25);
        this.add(versionTextBox);

        JLabel versionDotLabel = new JLabel(".");
        versionDotLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        versionDotLabel.setFont(DataFont);
        versionDotLabel.setHorizontalAlignment(JTextField.CENTER);
        versionDotLabel.setBounds(525, 35, 15, 25);
        this.add(versionDotLabel);

        minorVersionTextBox = new JTextField();
        minorVersionTextBox.setDocument(new DialogEditor.IntegerDocument(minorVersionTextBox, editable));
        minorVersionTextBox.setText(Integer.toString(dataModel.getMinorVersionNumber()));
        minorVersionTextBox.setFont(DataFont);
        minorVersionTextBox.setHorizontalAlignment(JTextField.CENTER);
        minorVersionTextBox.setBounds(535, 35, 25, 25);
        this.add(minorVersionTextBox);

        JLabel dateCertifiedLabel = new JLabel("Date Certified: ");
        dateCertifiedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateCertifiedLabel.setFont(DataFont);
        dateCertifiedLabel.setBounds(325, 60, 150, 25);
        this.add(dateCertifiedLabel);

        dateCertifiedBox = new JTextField();
        dateCertifiedBox.setDocument(new DialogEditor.UnDoAbleDocument(dateCertifiedBox, editable));
        dateCertifiedBox.setText(dataModel.getDateCertified());
        dateCertifiedBox.setFont(DataFont);
        dateCertifiedBox.setBounds(475, 60, 85, 25);
        this.add(dateCertifiedBox);

        dataTabs = new JTabbedPane(JTabbedPane.BOTTOM);
        dataTabs.setLocation(10, 90);
        initDataTabsValueModelPanel(0, 0);

        dataTabs.addChangeListener(new ChangeListener() {
            // This method is called whenever the selected tab changes
            // data tab is always tab 0
            @Override
            public void stateChanged(ChangeEvent evt) {
                int tab = ((JTabbedPane) evt.getSource()).getSelectedIndex();
                if (tab == 0) {
                    matrixTabs.setVisible(true);
                } else {
                    matrixTabs.setVisible(false);
                }

                revalidate();
            }
        });

        JLayeredPane refAndCommentsPane = new JLayeredPane();

        JLabel referenceLabel = new JLabel("Reference: ");
        referenceLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        referenceLabel.setBounds(5, 0, 100, 25);
        refAndCommentsPane.add(referenceLabel);

        referenceTextArea = new JTextArea();
        referenceTextArea.setDocument(new DialogEditor.UnDoAbleDocument(referenceTextArea, editable));
        referenceTextArea.setText(dataModel.getReference());
        referenceTextArea.setLineWrap(true);
        referenceTextArea.setFont(ReduxConstants.sansSerif_12_Plain);

        JScrollPane referenceTextScroll = new javax.swing.JScrollPane();
        referenceTextScroll.setBounds(5, 20, valueModelsPanelView.getWidth() - 25, (valueModelsPanelView.getHeight() / 2) - 20);

        referenceTextScroll.setViewportView(referenceTextArea);

        refAndCommentsPane.add(referenceTextScroll);

        JLabel commentLabel = new JLabel("Comment: ");
        commentLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        commentLabel.setBounds(5, referenceTextScroll.getHeight() + 20, 100, 25);
        refAndCommentsPane.add(commentLabel);

        commentTextArea = new JTextArea();
        commentTextArea.setDocument(new DialogEditor.UnDoAbleDocument(commentTextArea, editable));
        commentTextArea.setText(dataModel.getComment());
        commentTextArea.setLineWrap(true);
        commentTextArea.setFont(ReduxConstants.sansSerif_12_Plain);

        JScrollPane commentTextScroll = new javax.swing.JScrollPane();
        commentTextScroll.setBounds(5, referenceTextScroll.getHeight() + 40, valueModelsPanelView.getWidth() - 25, (valueModelsPanelView.getHeight() / 2) - 30);

        commentTextScroll.setViewportView(commentTextArea);

        refAndCommentsPane.add(commentTextScroll);
        setOpaqueWithLightGray(refAndCommentsPane);

        dataTabs.add("Ref and Comment", refAndCommentsPane);
        setOpaqueWithLightGray(dataTabs);
        this.add(dataTabs, DEFAULT_LAYER);

        matrixTabs = new JTabbedPane(JTabbedPane.BOTTOM);
        this.add(matrixTabs, DEFAULT_LAYER);
        initializeMatrixTabs();

//        addComponentListener( new viewListener() );
    }

    /**
     *
     * @param index
     * @param customAdditionalTabsWidth the value of customAdditionalTabsWidth
     */
    protected void initDataTabsValueModelPanel(int index, int customAdditionalTabsWidth) {
        dataTabs.setSize(valueModelsPanelView.getWidth() + 15 + customAdditionalTabsWidth, valueModelsPanelView.getHeight() + 35);
        setOpaqueWithLightGray(valueModelsPanelView);
        insertModelTab("Data", valueModelsPanelView, index);
    }

    /**
     *
     */
    protected void initializeMatrixTabs() {
        matrixTabs.removeAll();

        correlationMatrixViewScrollPane = new JScrollPane(correlationVarUnctMatrixView);
        correlationMatrixViewScrollPane.setBorder(null);
        correlationMatrixViewScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        correlationMatrixViewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        matrixTabs.add("Correlations", correlationMatrixViewScrollPane);

        covarianceMatrixViewScrollPane = new JScrollPane(covarianceVarUnctMatrixView);
        covarianceMatrixViewScrollPane.setBorder(null);
        covarianceMatrixViewScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        covarianceMatrixViewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        matrixTabs.add("Covariances", covarianceMatrixViewScrollPane);

        updateModelView();
    }

    /**
     *
     * @param c
     */
    protected final void setOpaqueWithLightGray(JComponent c) {
        c.setOpaque(true);
        c.setBackground(ReduxConstants.dataModelGray);
    }

    private void sizeMatrixTabs() {
        matrixTabs.setLocation(10, this.dataTabs.getHeight() + 100);

        correlationVarUnctMatrixView.setPreferredSize(correlationVarUnctMatrixView.getSize());
        covarianceVarUnctMatrixView.setPreferredSize(covarianceVarUnctMatrixView.getSize());

        matrixTabs.setSize(//
                (int) parentDimension.getWidth() - 15, //
                (int) Math.min(covarianceVarUnctMatrixView.getHeight() + 50, parentDimension.getHeight() - matrixTabs.getY()));

        validate();
    }

    /**
     *
     *
     * @param checkCovarianceValidity the value of checkCovarianceValidity
     * @throws org.earthtime.exceptions.ETException
     */
    public void saveAndUpdateModelView(boolean checkCovarianceValidity)
            throws ETException {
        saveEdits(checkCovarianceValidity);
        updateModelView();

    }

    /**
     *
     * @param currentModel
     */
    public void updateModelView(AbstractRatiosDataModel currentModel) {
        dataModel = currentModel;
        updateModelView();
    }

    /**
     *
     */
    public void updateModelView() {
        dataModel.refreshModel();

        covarianceVarUnctMatrixView.setMatrixModel(dataModel.getDataCovariancesVarUnct());
        covarianceVarUnctMatrixView.rebuildTableModel();

        correlationVarUnctMatrixView.setMatrixModel(dataModel.getDataCorrelationsVarUnct());
        correlationVarUnctMatrixView.rebuildTableModel();

        sizeMatrixTabs();

    }

    /**
     *
     *
     * @param checkCovarianceValidity the value of checkCovarianceValidity
     * @throws org.earthtime.exceptions.ETException
     */
    protected void saveEdits(boolean checkCovarianceValidity)
            throws ETException {

        dataModel.setModelName(modelNameTextBox.getText());
        dataModel.setLabName(labNameTextBox.getText());
        dataModel.setVersionNumber(Integer.parseInt(versionTextBox.getText()));
        dataModel.setMinorVersionNumber(Integer.parseInt(minorVersionTextBox.getText()));
        dataModel.setDateCertified(dateCertifiedBox.getText());
        dataModel.setReference(referenceTextArea.getText());
        dataModel.setComment(commentTextArea.getText());

        updateMatrixModelsAndSaveRhos(checkCovarianceValidity);
    }

    /**
     *
     * @param checkCovarianceValidity
     * @throws ETException
     */
    protected void updateMatrixModelsAndSaveRhos(boolean checkCovarianceValidity)
            throws ETException {
        dataModel.saveEdits(checkCovarianceValidity);

    }

    /**
     *
     * @param tabName
     * @param tab
     * @param index
     */
    protected void insertModelTab(String tabName, JLayeredPane tab, int index) {
        dataTabs.insertTab(tabName, null, tab, "", index);
        dataTabs.setSelectedIndex(0);
    }

    /**
     *
     * @return
     */
    public JDialog displayModelInFrame() {
        class RatioDataDialog extends javax.swing.JDialog {

            public RatioDataDialog(Dialog owner, boolean modal) {
                super(owner, modal);
            }
        }

        RatioDataDialog modelDialog = new RatioDataDialog(null, true);
        modelDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        modelDialog.setBounds( //
                400, 400, //
                (int) parentDimension.getWidth(),
                (int) parentDimension.getHeight());

        modelDialog.add(this);
        modelDialog.setVisible(true);

        return modelDialog;
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
    protected void paintInit(Graphics2D g2d) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(ReduxConstants.sansSerif_12_Bold);
    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

    }

    /**
     *
     */
    @Override
    public void dataEntryDetected() {
        try {
            saveAndUpdateModelView(false);
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }
}
