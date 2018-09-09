/*
 * SamplesOrganizerPane.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.samples.TripoliUnknownSample;
import org.earthtime.beans.ET_JButton;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.DragAndDropListItemInterface;

/**
 *
 * @author James F. Bowring
 */
public class SamplesOrganizerPane extends JLayeredPane implements ReduxDragAndDropClipboardInterface {

    private final int WIDTH_OF_SAMPLE_DISPLAY_LIST = 225;
    private final int BOTTOM_MARGIN = 25;
    private final int LEFT_MARGIN = 30;
    private final int TOP_MARGIN = 20;
    private final JLabel titleLabel;
    private int myWidth;
    private final int myHeight;
    private final ArrayList<AbstractTripoliSample> tripoliSamples;
    private final ArrayList<JLayeredPane> sampleDisplayPanes;
    private final JLayeredPane sampleFractionLists_pane;
    private final JScrollPane sampleFractionLists_scroll;
    private final JButton addSampleButton;
    private DragAndDropListItemInterface[] dndClipboardListItems;
    private final ProjectManagerSubscribeInterface projectManager;

    /**
     *
     *
     * @param title
     * @param x
     * @param y
     * @param myWidth
     * @param myHeight
     * @param tripoliSamples
     * @param projectManager the value of projectManager
     */
    public SamplesOrganizerPane(//
            String title, int x, int y, int myWidth, int myHeight, ArrayList<AbstractTripoliSample> tripoliSamples, ProjectManagerSubscribeInterface projectManager) {

        this.titleLabel = new JLabel(title);
        this.titleLabel.setBounds(2, 2, 150, 15);
        this.add(this.titleLabel, DEFAULT_LAYER);

        this.myWidth = myWidth;
        this.myHeight = myHeight;

        this.setBounds(x, y, myWidth, myHeight);
        this.setOpaque(true);

        this.tripoliSamples = tripoliSamples;

        sampleFractionLists_pane = new JLayeredPane();

        // populate list boxes
        sampleDisplayPanes = new ArrayList<>();

        // walk the samples
        for (int i = 0; i < tripoliSamples.size(); i++) {
            ActionListener closeButtonActionListener = new CloseSampleButtonActionListener();
            SampleFractionListDisplayPane sampleDisplayPane
                    =//
                    new SampleFractionListDisplayPane( //
                            tripoliSamples.get(i), //
                            closeButtonActionListener, //
                            this, //
                            projectManager);
            ((CloseSampleButtonActionListener) closeButtonActionListener).setSampleDisplayPane(sampleDisplayPane);
            sampleDisplayPanes.add(sampleDisplayPane);
            sampleFractionLists_pane.add(sampleDisplayPane);
        }

        sampleFractionLists_scroll = new javax.swing.JScrollPane();
        sampleFractionLists_scroll.setAutoscrolls(true);
        sampleFractionLists_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(sampleFractionLists_scroll, DEFAULT_LAYER);
        sampleFractionLists_scroll.setViewportView(sampleFractionLists_pane);

        // button to create additional SampleFractionListDisplayPanels
        addSampleButton = new ET_JButton("+");
        addSampleButton.setBounds(5, myHeight - 50, 15, 15);
        addSampleButton.addActionListener(new AddSampleButtonActionListener(this));
        this.add(addSampleButton, DEFAULT_LAYER);

        dndClipboardListItems = new DragAndDropListItemInterface[0];

        this.projectManager = projectManager;

        refreshSampleFractionListsPane();

    }

    /**
     *
     */
    public void saveChanges() {
        for (JLayeredPane sampleDisplayPane : sampleDisplayPanes) {
            ((SampleFractionListDisplayPane) sampleDisplayPane).saveChanges();
        }

        projectManager.updateDataChangeStatus(false);
    }

    /**
     * @return the dndClipboardListItems
     */
    @Override
    public DragAndDropListItemInterface[] getDndClipboardListItems() {
        return dndClipboardListItems;
    }

    /**
     * @param dndClipboardListItems the dndClipboardListItems to set
     */
    @Override
    public void setDndClipboardListItems(DragAndDropListItemInterface[] dndClipboardListItems) {
        this.dndClipboardListItems = dndClipboardListItems;
    }

    /**
     * @param myWidth the myWidth to set
     */
    public void setMyWidth(int myWidth) {
        this.myWidth = myWidth;
    }

    private class AddSampleButtonActionListener implements ActionListener {

        private final ReduxDragAndDropClipboardInterface samplesOrgPane;

        public AddSampleButtonActionListener(ReduxDragAndDropClipboardInterface samplesOrgPane) {
            this.samplesOrgPane = samplesOrgPane;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AbstractTripoliSample addedSample = new TripoliUnknownSample("unknown" + (tripoliSamples.size() + 1));
            tripoliSamples.add(addedSample);
            projectManager.updateDataChangeStatus(true);

            ActionListener closeButtonActionListener = new CloseSampleButtonActionListener();
            SampleFractionListDisplayPane sampleDisplayPane
                    =//
                    new SampleFractionListDisplayPane( //
                            addedSample,//
                            closeButtonActionListener,//
                            samplesOrgPane, //
                            projectManager);
            ((CloseSampleButtonActionListener) closeButtonActionListener).setSampleDisplayPane(sampleDisplayPane);
            sampleDisplayPanes.add(sampleDisplayPane);
            sampleFractionLists_pane.add(sampleDisplayPane);

            refreshSampleFractionListsPane();

        }
    }

    private class CloseSampleButtonActionListener implements ActionListener {

        private JLayeredPane sampleDisplayPane;

        public CloseSampleButtonActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (sampleDisplayPane != null) {
                projectManager.updateDataChangeStatus(true);

                tripoliSamples.remove(((SampleFractionListDisplayPane) sampleDisplayPane).getTripoliSample());
                tripoliSamples.trimToSize();

                // sept 2016 discovered that project sample was not removed
                ArrayList< SampleInterface> projectSamples = projectManager.getProject().getProjectSamples();
                SampleInterface removedSample = null;
                for (SampleInterface sample : projectSamples) {
                    if (sample.getSampleName().compareToIgnoreCase(((SampleFractionListDisplayPane) sampleDisplayPane).getTripoliSample().getSampleName()) == 0) {
                        removedSample = sample;
                        break;
                    }
                }
                if (removedSample != null) {
                    projectSamples.remove(removedSample);
                }

                sampleDisplayPanes.remove(sampleDisplayPane);
                sampleDisplayPanes.trimToSize();

                sampleFractionLists_pane.remove(sampleDisplayPane);

                sampleDisplayPane.removeAll();
                sampleDisplayPane.validate();

                sampleFractionLists_pane.validate();
                refreshSampleFractionListsPane();

                sampleFractionLists_pane.repaint();
            }
        }

        /**
         * @param sampleDisplayPane the sampleDisplayPane to set
         */
        public void setSampleDisplayPane(JLayeredPane sampleDisplayPane) {
            this.sampleDisplayPane = sampleDisplayPane;
        }
    }

    /**
     *
     */
    public final void refreshSampleFractionListsPane() {
        this.setSize(myWidth, myHeight);
        sampleFractionLists_scroll.setBounds( //
                LEFT_MARGIN, TOP_MARGIN, myWidth - LEFT_MARGIN, myHeight - TOP_MARGIN - BOTTOM_MARGIN + 25 /*height of scrollbar*/);
        sampleFractionLists_pane.setPreferredSize(//
                new Dimension( //
                        Math.max(myWidth - 25 - LEFT_MARGIN, tripoliSamples.size() * (WIDTH_OF_SAMPLE_DISPLAY_LIST + 25)), //
                        myHeight - BOTTOM_MARGIN - TOP_MARGIN - 10));

        layoutSampleFractionLists();

        validate();
    }

    private void layoutSampleFractionLists() {

        for (int i = 0; i < sampleDisplayPanes.size(); i++) {
            sampleDisplayPanes.get(i).setBounds(//
                    10 + (WIDTH_OF_SAMPLE_DISPLAY_LIST + 20) * i, //
                    10, WIDTH_OF_SAMPLE_DISPLAY_LIST, myHeight - BOTTOM_MARGIN - TOP_MARGIN - 10);//50);
        }

        sampleFractionLists_pane.validate();
    }

    /**
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
