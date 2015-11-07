/*
 * SampleFractionListDisplayPane.java
 *
 * Created Dec 30, 2011
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
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.beans.ET_JButton;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.MineralStandardUPbRatiosDataViewNotEditable;
import org.earthtime.utilities.DragAndDropListItemInterface;

/**
 * JLayeredPane containing JList customized to support drag and drip of
 * TripoliFractions to other instances
 *
 * @author James F. Bowring
 */
public class SampleFractionListDisplayPane extends JLayeredPane {

    private final JList<TripoliFraction> list;
    private final JScrollPane listView;
    private final Font listFont = new Font("SansSerif", Font.PLAIN, 10);
    private final AbstractTripoliSample tripoliSample;
    private final JButton closeButton;
    private final ReduxDragAndDropClipboardInterface reduxDragAndDropClipboardInterface;
    private JComboBox roleChooser;
    private final JTextField sampleNameText;
    private final ProjectManagerSubscribeInterface projectManager;
    // nov 2014
    private final JLabel r238_235s_textField;

    /**
     *
     *
     * @param tripoliSample
     * @param closeButtonActionListener
     * @param reduxDragAndDropClipboardInterface
     * @param projectManager the value of projectManager
     */
    public SampleFractionListDisplayPane( //
            AbstractTripoliSample tripoliSample, ActionListener closeButtonActionListener, ReduxDragAndDropClipboardInterface reduxDragAndDropClipboardInterface, final ProjectManagerSubscribeInterface projectManager) {

        setOpaque(true);
        setBackground(Color.white);
        setBorder(new LineBorder(Color.black));

        this.tripoliSample = tripoliSample;

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(ReduxConstants.sansSerif_12_Plain);
        nameLabel.setBounds(2, 5, 40, 15);
        this.add(nameLabel, DEFAULT_LAYER);

        sampleNameText = new JTextField(tripoliSample.getSampleName());
        sampleNameText.setBounds(45, 2, 150, 20);
        sampleNameText.getDocument().addDocumentListener(new TextDocChangeListener());

        this.add(sampleNameText, DEFAULT_LAYER);

        this.projectManager = projectManager;

        if (tripoliSample.isPrimaryStandard()) {

            roleChooser = new JComboBox();
            ArrayList<AbstractRatiosDataModel> mineralStandardModels = ReduxLabData.getInstance().getMineralStandardModels();
            for (int i = (mineralStandardModels.size() > 1 ? 1 : 0); i < mineralStandardModels.size(); i++) {
                roleChooser.addItem(mineralStandardModels.get(i));
            }
            roleChooser.setSelectedIndex(-1);
            if (tripoliSample.getMineralStandardModel() == null) {
                tripoliSample.setMineralStandardModel(mineralStandardModels.get(1));
            }
            roleChooser.setSelectedItem(tripoliSample.getMineralStandardModel());
            roleChooser.addActionListener(new MineralStandardChooserActionListener());

            roleChooser.setBounds(0, 27, 195, 25);
            roleChooser.setFont(ReduxConstants.sansSerif_10_Bold);

            // view standard model
            JButton viewStandardModelButton = new ET_JButton("View");
            viewStandardModelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AbstractRatiosDataModel selectedModel = //
                            ((AbstractRatiosDataModel) roleChooser.getSelectedItem());
                    AbstractRatiosDataView modelView = //
                            new MineralStandardUPbRatiosDataViewNotEditable(selectedModel, null, false);
                    modelView.displayModelInFrame();
                }
            });
            viewStandardModelButton.setFont(ReduxConstants.sansSerif_10_Bold);
            viewStandardModelButton.setBounds(193, 27, 30, 23);
            this.add(viewStandardModelButton);

        } else {
            JLabel roleLabel = new JLabel("Role:");
            roleLabel.setBounds(2, 32, 40, 15);
            this.add(roleLabel, DEFAULT_LAYER);

            roleChooser = new JComboBox(
                    new String[]{"Unknown", "Secondary STD"});

            if (tripoliSample.isSecondaryStandard()) {
                roleChooser.setSelectedItem("Secondary STD");
            }

            roleChooser.addActionListener(new ChangeActionListener());
            roleChooser.setBounds(45, 27, 150, 25);
        }

        this.add(roleChooser);

        ArrayList<DragAndDropListItemInterface> sampleFractionsDNDList = new ArrayList<>();
        Iterator tripoliFractionsIterator = tripoliSample.getSampleFractions().iterator();
        while (tripoliFractionsIterator.hasNext()) {
            sampleFractionsDNDList.add((DragAndDropListItemInterface) tripoliFractionsIterator.next());
        }

        this.reduxDragAndDropClipboardInterface = reduxDragAndDropClipboardInterface;

        DefaultListModel<TripoliFraction> listModel = new DefaultListModel<>();
        Iterator dndListIterator = sampleFractionsDNDList.iterator();
        while (dndListIterator.hasNext()) {
            TripoliFraction element = (TripoliFraction) dndListIterator.next();
            listModel.addElement(element);
        }

        list = new JList<>(listModel);
        list.setFont(listFont);
        list.setVisibleRowCount(-1);
        list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        list.setTransferHandler(new FractionListTransferHandler(sampleFractionsDNDList));

        list.setDropMode(DropMode.INSERT);

        listView = new JScrollPane(list);
        list.setDragEnabled(true);

        add(listView);

        // close button
        closeButton = new ET_JButton("X");
        closeButton.setEnabled(list.getModel().getSize() == 0);
        closeButton.addActionListener(closeButtonActionListener);
        if (!tripoliSample.isPrimaryStandard()) {
            this.add(closeButton, DEFAULT_LAYER);
        }

//        // minimize/maximize button
//        maxMinButton = new JButton("-");
//        maxMinButton.addActionListener( closeButtonActionListener );
//        if (  ! tripoliSample.isPrimaryStandard() ) {
//            this.add( maxMinButton, DEFAULT_LAYER );
//        }
        r238_235s_textField = new JLabel();
        try {
            r238_235s_textField.setText(tripoliSample.getSampleR238_235s().formatValueAndOneSigmaQuickLook());
        } catch (Exception e) {
            r238_235s_textField.setText("not found");
        }
        r238_235s_textField.setFont(ReduxConstants.sansSerif_10_Plain);
        add(r238_235s_textField);

        validate();

    }

//    /**
//     * @return the amChanged
//     */
//    public boolean isAmChanged () {
//        return amChanged;
//    }
//
//    /**
//     * @param amChanged the amChanged to set
//     */
//    public void setAmChanged ( boolean amChanged ) {
//        this.amChanged = amChanged;
//    }
    private class MineralStandardChooserActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tripoliSample.setMineralStandardModel((AbstractRatiosDataModel) ((JComboBox) e.getSource()).getSelectedItem());
            projectManager.updateDataChangeStatus(true);
        }
    }

    private class ChangeActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            projectManager.updateDataChangeStatus(true);
        }
    }

    private class TextDocChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            projectManager.updateDataChangeStatus(true);
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            projectManager.updateDataChangeStatus(true);
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            projectManager.updateDataChangeStatus(true);
        }

    }

    /**
     *
     */
    public void saveChanges() {
        String sampleName = sampleNameText.getText();
        if (sampleName.trim().length() == 0) {
            sampleName = "NONE";
        }

        tripoliSample.setSampleName(sampleName);

        if (!tripoliSample.isPrimaryStandard()) {
            if (((String) roleChooser.getSelectedItem()).compareToIgnoreCase("Secondary STD") == 0) {
                tripoliSample.setSecondaryStandard(true);
            } else {
                tripoliSample.setSecondaryStandard(false);
            }
        }

        projectManager.updateDataChangeStatus(false);
    }

    /**
     *
     * @param x
     * @param y
     * @param w
     * @param h
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);

        listView.setBounds(10, 55, w - 20, h - 80);//60);

        closeButton.setBounds(w - 15, 0, 15, 15);

        r238_235s_textField.setBounds(10, 59 + h - 80, w - 20, 15);

//        maxMinButton.setBounds( w - 30, 0, 15, 15 );
    }

    /**
     * @return the tripoliSample
     */
    public AbstractTripoliSample getTripoliSample() {
        return tripoliSample;
    }

    class FractionListTransferHandler extends TransferHandler {

        private ArrayList<DragAndDropListItemInterface> dndListTripoliFractions;

        public FractionListTransferHandler(ArrayList<DragAndDropListItemInterface> dndListTripoliFractions) {
            this.dndListTripoliFractions = dndListTripoliFractions;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport info) {

            return true;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info) {
            // faking out the clipboard with our own clipboard to prevent copying of data objects
            if (!info.isDrop()) {
                return false;
            }

            DefaultListModel<TripoliFraction> listModel = (DefaultListModel<TripoliFraction>) list.getModel();

//            // Get the DragAndDropListItemInterface that is being dropped.
//            Transferable t = info.getTransferable();
//            DragAndDropListItemInterface[] data;
//            try {
//                data = (DragAndDropListItemInterface[])t.getTransferData( DataFlavor.stringFlavor );;
//            } catch (Exception e) {
//                return false;
//            }
            /**
             * Perform the actual import. Keep items in sort order
             *
             */
            DragAndDropListItemInterface[] data = reduxDragAndDropClipboardInterface.getDndClipboardListItems();
            for (int i = 0; i < data.length; i++) {
                // find where to insert element in backing list
                TripoliFraction tf = (TripoliFraction) data[i];
                int index = Collections.binarySearch(dndListTripoliFractions, tf);
                dndListTripoliFractions.add(Math.abs(index + 1), tf);

                // insert into list as well
                listModel.add(Math.abs(index + 1), tf);

                tripoliSample.addTripoliFraction(tf);
            }

            closeButton.setEnabled(dndListTripoliFractions.isEmpty());

            return true;

        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {

            if (action == MOVE) {
//                System.out.println( "DONE EXPORTING " + (TripoliFraction) list.getSelectedValues()[0] );

                int origLength = list.getSelectedValuesList().size();

                DragAndDropListItemInterface[] copyOfSelected = new DragAndDropListItemInterface[origLength];
                for (int i = 0; i < origLength; i++) {
                    copyOfSelected[i] = (DragAndDropListItemInterface) list.getSelectedValuesList().get(i);
                }
                for (int i = 0; i < origLength; i++) {
                    ((DefaultListModel) ((JList) source).getModel()).removeElement(copyOfSelected[i]);
                    dndListTripoliFractions.remove(copyOfSelected[i]);

                    tripoliSample.removeTripoliFraction((TripoliFraction) copyOfSelected[i]);
                }

                ((DefaultListModel) ((JList) source).getModel()).trimToSize();

                closeButton.setEnabled(list.getModel().getSize() == 0);

                projectManager.updateDataChangeStatus(true);
            }
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList) c;
            List values = list.getSelectedValuesList();

            DragAndDropListItemInterface[] t = new DragAndDropListItemInterface[values.size()];
            for (int i = 0; i < t.length; i++) {
                t[i] = (DragAndDropListItemInterface) values.get(i);
            }

            reduxDragAndDropClipboardInterface.setDndClipboardListItems(t);

            return new SelectedFractionListItems(t);
        }
    }
}

class SelectedFractionListItems implements Transferable, ClipboardOwner {

    private DragAndDropListItemInterface[] data;
    private final DataFlavor[] flavors = new DataFlavor[0];

    public SelectedFractionListItems(DragAndDropListItemInterface[] data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
