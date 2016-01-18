/*
 * WeightedMeanOptionsDialog.java
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
package org.earthtime.UPb_Redux.dialogs.graphManagers;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.samples.SampleInterface;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author James F. Bowring
 */
public class WeightedMeanOptionsDialog extends DialogEditor {

    // instance variables
    private final ArrayList<JLabel> aliquotNameLabels;
    private final ArrayList<JCheckBox> wm207_235CheckBox;
    private final ArrayList<JCheckBox> wm206_238CheckBox;
    private final ArrayList<JCheckBox> wm207_206CheckBox;
    private final ArrayList<JCheckBox> wm208_232CheckBox;
    private ArrayList<JCheckBox> wm206_238r_ThCheckBox = new ArrayList<>();
    private ArrayList<JCheckBox> wm207_235r_PaCheckBox = new ArrayList<>();
    private ArrayList<JCheckBox> wm207_206r_ThCheckBox = new ArrayList<>();
    private ArrayList<JCheckBox> wm207_206r_PaCheckBox = new ArrayList<>();
    private ArrayList<JCheckBox> wm207_206r_ThPaCheckBox = new ArrayList<>();
    private final SampleInterface sample;
    private Object[][] selectedModels;
    private Map<String, String> weightedMeanOptions;
    private String wm207_235;
    private String wm206_238;
    private String wm207_206;
    private String wm208_232;
    private String wm206_238r_Th;
    private String wm207_235r_Pa;
    private String wm207_206r_Th;
    private String wm207_206r_Pa;
    private String wm207_206r_ThPa;

    /**
     * Creates new form WeightedMeanOptionsDialog
     *
     * @param parent
     * @param modal
     * @param sample
     */
    public WeightedMeanOptionsDialog(java.awt.Frame parent,
            boolean modal,
            SampleInterface sample) {

        super(parent, modal);
        this.sample = sample;

        selectedModels = new Object[0][0];

        setLocationRelativeTo(parent);
        setAlwaysOnTop(modal);

        setWeightedMeanOptions(sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions());

        initComponents();

        // october 2009
        // auto-adjust height to accommodate aliquots up to maxium of 750
        int maxHeight = Math.min(750, sample.getAliquots().size() * 30 + 180);
        setSize(1100, maxHeight);

        aliquotNameLabels = new ArrayList<>();
        wm206_238CheckBox = new ArrayList<>();
        wm207_206CheckBox = new ArrayList<>();
        wm207_235CheckBox = new ArrayList<>();
        wm208_232CheckBox = new ArrayList<>();
        wm206_238r_ThCheckBox = new ArrayList<JCheckBox>();
        wm207_235r_PaCheckBox = new ArrayList<JCheckBox>();
        wm207_206r_ThCheckBox = new ArrayList<JCheckBox>();
        wm207_206r_PaCheckBox = new ArrayList<JCheckBox>();
        wm207_206r_ThPaCheckBox = new ArrayList<JCheckBox>();

        // build display
        org.jdesktop.layout.GroupLayout wmAliquotArrayLayout = new org.jdesktop.layout.GroupLayout(wmAliquotArray_panel);
        wmAliquotArray_panel.setLayout(wmAliquotArrayLayout);

        ParallelGroup myHorizAliquot = wmAliquotArrayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false);
        SequentialGroup myVerticalAliquot = wmAliquotArrayLayout.createSequentialGroup();

        // vertical offset
        myVerticalAliquot.add(60, 60, 60);

        JLabel aliquotLabelH = new JLabel("Aliquot");
        JLabel wm206_238H = new JLabel("206Pb / 238U");
        JLabel wm207_235H = new JLabel("207Pb / 235U");
        JLabel wm207_206H = new JLabel("207Pb / 206Pb");
        JLabel wm208_232H = new JLabel("208Pb / 232Th");

        JLabel wm206_238r_ThH = new JLabel("<html>206Pb/238U <br>(Th-corrected)</html>");
        JLabel wm207_235r_PaH = new JLabel("<html>207Pb/235U <br>(Pa-corrected)</html>");
        JLabel wm207_206r_ThH = new JLabel("<html>207Pb/206Pb <br>(Th-corrected)</html>");
        JLabel wm207_206r_PaH = new JLabel("<html>207Pb/206Pb <br>(Pa-corrected)</html>");
        JLabel wm207_206r_ThPaH = new JLabel("<html>207Pb/206Pb <br>(Th-, Pa-corrected)</html>");

        Font headerFont = new Font("SansSerif", Font.BOLD, 12);
        aliquotLabelH.setFont(headerFont);
        wm207_235H.setFont(headerFont);
        wm206_238H.setFont(headerFont);
        wm207_206H.setFont(headerFont);
        wm208_232H.setFont(headerFont);
        wm206_238r_ThH.setFont(headerFont);
        wm207_235r_PaH.setFont(headerFont);
        wm207_206r_ThH.setFont(headerFont);
        wm207_206r_PaH.setFont(headerFont);
        wm207_206r_ThPaH.setFont(headerFont);

        myHorizAliquot.add(wmAliquotArrayLayout.createSequentialGroup()//
                .add(10, 10, 10) // left-hand margin
                .add(aliquotLabelH, 130, 130, 130) //
                .add(wm206_238H, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm207_235H, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm207_206H, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm208_232H, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm206_238r_ThH, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm207_235r_PaH, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm207_206r_ThH, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm207_206r_PaH, 100, 100, 100)//
                .add(13, 13, 13)//
                .add(wm207_206r_ThPaH, 130, 130, 130) //
        );
        myVerticalAliquot.add(wmAliquotArrayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//
                .add(aliquotLabelH, 22, 22, 22)//
                .add(wm206_238H, 22, 22, 22)//
                .add(wm207_235H, 22, 22, 22)//
                .add(wm207_206H, 22, 22, 22)//
                .add(wm208_232H, 22, 22, 22)//
                .add(wm206_238r_ThH, 35, 35, 35)//
                .add(wm207_235r_PaH, 35, 35, 35)//
                .add(wm207_206r_ThH, 35, 35, 35)//
                .add(wm207_206r_PaH, 35, 35, 35)//
                .add(wm207_206r_ThPaH, 35, 35, 35)//
        );

        restoreSavedValues();

        for (AliquotInterface aliquot : sample.getActiveAliquots()) {
            JLabel aliquotLabel = new JLabel(aliquot.getAliquotName());
            aliquotNameLabels.add(aliquotLabel);

            JCheckBox wm207_235CB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 207Pb/235U")) {
                wm207_235CB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 207Pb/235U")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm207_235CB.setEnabled(false);
            }
            wm207_235CheckBox.add(wm207_235CB);

            JCheckBox wm206_238CB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 206Pb/238U")) {
                wm206_238CB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 206Pb/238U")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm206_238CB.setEnabled(false);
            }
            wm206_238CheckBox.add(wm206_238CB);

            JCheckBox wm207_206CB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 207Pb/206Pb")) {
                wm207_206CB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 207Pb/206Pb")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm207_206CB.setEnabled(false);
            }
            wm207_206CheckBox.add(wm207_206CB);

            JCheckBox wm208_232CB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 208Pb/232Th")) {
                wm208_232CB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 208Pb/232Th")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm208_232CB.setEnabled(false);
            }
            wm208_232CheckBox.add(wm208_232CB);

            JCheckBox wm206_238r_ThCB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 206Pb/238U (Th-corrected)")) {
                wm206_238r_ThCB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 206Pb/238U (Th-corrected)")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm206_238r_ThCB.setEnabled(false);
            }
            wm206_238r_ThCheckBox.add(wm206_238r_ThCB);

            JCheckBox wm207_235r_PaCB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 207Pb/235U (Pa-corrected)")) {
                wm207_235r_PaCB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 207Pb/235U (Pa-corrected)")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm207_235r_PaCB.setEnabled(false);
            }
            wm207_235r_PaCheckBox.add(wm207_235r_PaCB);

            JCheckBox wm207_206r_ThCB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 207Pb/206Pb (Th-corrected)")) {
                wm207_206r_ThCB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 207Pb/206Pb (Th-corrected)")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm207_206r_ThCB.setEnabled(false);
            }
            wm207_206r_ThCheckBox.add(wm207_206r_ThCB);

            JCheckBox wm207_206r_PaCB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 207Pb/206Pb (Pa-corrected)")) {
                wm207_206r_PaCB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 207Pb/206Pb (Pa-corrected)")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm207_206r_PaCB.setEnabled(false);
            }
            wm207_206r_PaCheckBox.add(wm207_206r_PaCB);

            JCheckBox wm207_206r_ThPaCB = new JCheckBox();
            if (aliquot.containsSampleDateModelByName("weighted mean 207Pb/206Pb (Th- and Pa-corrected)")) {
                wm207_206r_ThPaCB.setEnabled(((SampleDateModel) aliquot.//
                        getASampleDateModelByName("weighted mean 207Pb/206Pb (Th- and Pa-corrected)")).getIncludedFractionIDsVector().size() > 0);
            } else {
                wm207_206r_ThPaCB.setEnabled(false);
            }
            wm207_206r_ThPaCheckBox.add(wm207_206r_ThPaCB);

            myHorizAliquot.add(wmAliquotArrayLayout.createSequentialGroup()//
                    .add(10, 10, 10) // left-hand margin
                    .add(aliquotLabel, 165, 165, 165) //
                    .add(wm206_238CB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm207_235CB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm207_206CB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm208_232CB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm206_238r_ThCB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm207_235r_PaCB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm207_206r_ThCB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm207_206r_PaCB, 22, 22, 22)//
                    .add(90, 90, 90)//
                    .add(wm207_206r_ThPaCB, 22, 22, 22)//
            );
            myVerticalAliquot.add(wmAliquotArrayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//
                    .add(aliquotLabel, 22, 22, 22)//
                    .add(wm206_238CB, 22, 22, 22)//
                    .add(wm207_235CB, 22, 22, 22)//
                    .add(wm207_206CB, 22, 22, 22)//
                    .add(wm208_232CB, 22, 22, 22)//
                    .add(wm206_238r_ThCB, 22, 22, 22)//
                    .add(wm207_235r_PaCB, 22, 22, 22)//
                    .add(wm207_206r_ThCB, 22, 22, 22)//
                    .add(wm207_206r_PaCB, 22, 22, 22)//
                    .add(wm207_206r_ThPaCB, 22, 22, 22)//
            );

        }

        selectCheckBoxes();

        wmAliquotArrayLayout.setHorizontalGroup(
                wmAliquotArrayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(wmAliquotArrayLayout.createSequentialGroup().add(myHorizAliquot)));

        wmAliquotArrayLayout.setVerticalGroup(
                wmAliquotArrayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(myVerticalAliquot));

    }

    private void restoreSavedValues() {
        wm206_238 = getWeightedMeanOptions().get("weighted mean 206Pb/238U");
        wm207_235 = getWeightedMeanOptions().get("weighted mean 207Pb/235U");
        wm207_206 = getWeightedMeanOptions().get("weighted mean 207Pb/206Pb");
        wm208_232 = getWeightedMeanOptions().get("weighted mean 208Pb/232Th");
        wm206_238r_Th = getWeightedMeanOptions().get("weighted mean 206Pb/238U (Th-corrected)");
        wm207_235r_Pa = getWeightedMeanOptions().get("weighted mean 207Pb/235U (Pa-corrected)");
        wm207_206r_Th = getWeightedMeanOptions().get("weighted mean 207Pb/206Pb (Th-corrected)");
        wm207_206r_Pa = getWeightedMeanOptions().get("weighted mean 207Pb/206Pb (Pa-corrected)");
        wm207_206r_ThPa = getWeightedMeanOptions().get("weighted mean 207Pb/206Pb (Th- and Pa-corrected)");
    }

    private void replaceSavedValues() {
        getWeightedMeanOptions().put("weighted mean 206Pb/238U", wm206_238);
        getWeightedMeanOptions().put("weighted mean 207Pb/235U", wm207_235);
        getWeightedMeanOptions().put("weighted mean 207Pb/206Pb", wm207_206);
        getWeightedMeanOptions().put("weighted mean 208Pb/232Th", wm208_232);
        getWeightedMeanOptions().put("weighted mean 206Pb/238U (Th-corrected)", wm206_238r_Th);
        getWeightedMeanOptions().put("weighted mean 207Pb/235U (Pa-corrected)", wm207_235r_Pa);
        getWeightedMeanOptions().put("weighted mean 207Pb/206Pb (Th-corrected)", wm207_206r_Th);
        getWeightedMeanOptions().put("weighted mean 207Pb/206Pb (Pa-corrected)", wm207_206r_Pa);
        getWeightedMeanOptions().put("weighted mean 207Pb/206Pb (Th- and Pa-corrected)", wm207_206r_ThPa);
    }

    private void selectCheckBoxes() {
        for (int a = 0; a < wm207_235CheckBox.size(); a++) {
            try {
                wm206_238CheckBox.get(a).setSelected(
                        (wm206_238CheckBox.get(a).isEnabled()
                        && wm206_238.substring(a, a + 1).equalsIgnoreCase("1")));
                wm207_235CheckBox.get(a).setSelected(
                        (wm207_235CheckBox.get(a).isEnabled()
                        && wm207_235.substring(a, a + 1).equalsIgnoreCase("1")));
                wm207_206CheckBox.get(a).setSelected(
                        (wm207_206CheckBox.get(a).isEnabled()
                        && wm207_206.substring(a, a + 1).equalsIgnoreCase("1")));
                wm208_232CheckBox.get(a).setSelected(
                        wm208_232CheckBox.get(a).isEnabled()
                        && wm207_206.substring(a, a + 1).equalsIgnoreCase("1") ? true : false);
                wm206_238r_ThCheckBox.get(a).setSelected(
                        wm206_238r_ThCheckBox.get(a).isEnabled()
                        && wm206_238r_Th.substring(a, a + 1).equalsIgnoreCase("1") ? true : false);
                wm207_235r_PaCheckBox.get(a).setSelected(
                        wm207_235r_PaCheckBox.get(a).isEnabled()
                        && wm207_235r_Pa.substring(a, a + 1).equalsIgnoreCase("1") ? true : false);
                wm207_206r_ThCheckBox.get(a).setSelected(
                        wm207_206r_ThCheckBox.get(a).isEnabled()
                        && wm207_206r_Th.substring(a, a + 1).equalsIgnoreCase("1") ? true : false);
                wm207_206r_PaCheckBox.get(a).setSelected(
                        wm207_206r_PaCheckBox.get(a).isEnabled()
                        && wm207_206r_Pa.substring(a, a + 1).equalsIgnoreCase("1") ? true : false);
                wm207_206r_ThPaCheckBox.get(a).setSelected(
                        wm207_206r_ThPaCheckBox.get(a).isEnabled()
                        && wm207_206r_ThPa.substring(a, a + 1).equalsIgnoreCase("1") ? true : false);
            } catch (Exception e) {
            }
        }

    }

    private void OK() {
        Vector<AliquotInterface> activeAliquots = sample.getActiveAliquots();
        selectedModels = new Object[activeAliquots.size()][10];

        // populate array of aliquots with selected wm date interpretations
        for (int i = 0; i < activeAliquots.size(); i++) {

            getSelectedModels()[i][0] = activeAliquots.get(i);
            if (wm206_238CheckBox.get(i).isSelected()) {
                getSelectedModels()[i][2] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 206Pb/238U");
                wm206_238 = setAliquotFlag(wm206_238, i, "1");
            } else {
                wm206_238 = setAliquotFlag(wm206_238, i, "0");
            }
            if (wm207_235CheckBox.get(i).isSelected()) {
                getSelectedModels()[i][1] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 207Pb/235U");
                wm207_235 = setAliquotFlag(wm207_235, i, "1");
            } else {
                wm207_235 = setAliquotFlag(wm207_235, i, "0");
            }

            if (wm207_206CheckBox.get(i).isSelected()) {
                getSelectedModels()[i][3] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 207Pb/206Pb");
                wm207_206 = setAliquotFlag(wm207_206, i, "1");
            } else {
                wm207_206 = setAliquotFlag(wm207_206, i, "0");
            }

            if (wm208_232CheckBox.get(i).isSelected()) {
                getSelectedModels()[i][3] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 208Pb/232Th");
                wm208_232 = setAliquotFlag(wm208_232, i, "1");
            } else {
                wm208_232 = setAliquotFlag(wm208_232, i, "0");
            }

            if (wm206_238r_ThCheckBox.get(i).isSelected()) {
                getSelectedModels()[i][4] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 206Pb/238U (Th-corrected)");
                wm206_238r_Th = setAliquotFlag(wm206_238r_Th, i, "1");
            } else {
                wm206_238r_Th = setAliquotFlag(wm206_238r_Th, i, "0");
            }

            if (wm207_235r_PaCheckBox.get(i).isSelected()) {
                getSelectedModels()[i][5] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 207Pb/235U (Pa-corrected)");
                wm207_235r_Pa = setAliquotFlag(wm207_235r_Pa, i, "1");
            } else {
                wm207_235r_Pa = setAliquotFlag(wm207_235r_Pa, i, "0");
            }
            if (wm207_206r_ThCheckBox.get(i).isSelected()) {
                getSelectedModels()[i][6] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 207Pb/206Pb (Th-corrected)");
                wm207_206r_Th = setAliquotFlag(wm207_206r_Th, i, "1");
            } else {
                wm207_206r_Th = setAliquotFlag(wm207_206r_Th, i, "0");
            }
            if (wm207_206r_PaCheckBox.get(i).isSelected()) {
                getSelectedModels()[i][7] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 207Pb/206Pb (Pa-corrected)");
                wm207_206r_Pa = setAliquotFlag(wm207_206r_Pa, i, "1");
            } else {
                wm207_206r_Pa = setAliquotFlag(wm207_206r_Pa, i, "0");
            }
            if (wm207_206r_ThPaCheckBox.get(i).isSelected()) {
                getSelectedModels()[i][8] = activeAliquots.get(i).getASampleDateModelByName("weighted mean 207Pb/206Pb (Th- and Pa-corrected)");
                wm207_206r_ThPa = setAliquotFlag(wm207_206r_ThPa, i, "1");
            } else {
                wm207_206r_ThPa = setAliquotFlag(wm207_206r_ThPa, i, "0");
            }
        }

        // save off the choices
        replaceSavedValues();

    }

    private String setAliquotFlag(String flags, int position, String value) {
        // set position to value or add to end
        if (position == 0) {
            return value;
        } else if (position >= (flags.length() - 1)) {
            return flags.substring(0, position) + value;
        } else {
            return flags.substring(0, position) + value + flags.substring(position + 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonsPanel = new javax.swing.JPanel();
        save_button = new javax.swing.JButton();
        close_button = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        wmAliquotArray_panel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select weighted means to view as graphs:");
        setBackground(new java.awt.Color(230, 255, 230));

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("OK");
        save_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Cancel");
        close_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonsPanelLayout = new org.jdesktop.layout.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createSequentialGroup()
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 269, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 247, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(415, Short.MAX_VALUE))
        );

        buttonsPanelLayout.linkSize(new java.awt.Component[] {close_button, save_button}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        wmAliquotArray_panel.setBackground(new java.awt.Color(244, 255, 244));

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 18));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Weighted Mean Sample Date Interpretation");

        org.jdesktop.layout.GroupLayout wmAliquotArray_panelLayout = new org.jdesktop.layout.GroupLayout(wmAliquotArray_panel);
        wmAliquotArray_panel.setLayout(wmAliquotArray_panelLayout);
        wmAliquotArray_panelLayout.setHorizontalGroup(
            wmAliquotArray_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wmAliquotArray_panelLayout.createSequentialGroup()
                .add(238, 238, 238)
                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 410, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(315, Short.MAX_VALUE))
        );
        wmAliquotArray_panelLayout.setVerticalGroup(
            wmAliquotArray_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wmAliquotArray_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8)
                .addContainerGap(334, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(wmAliquotArray_panel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 963, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(363, Short.MAX_VALUE)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .add(31, 31, 31)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        restoreSavedValues();
        selectCheckBoxes();
        OK();
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        OK();
        close();
    }//GEN-LAST:event_save_buttonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton save_button;
    private javax.swing.JPanel wmAliquotArray_panel;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @return
     */
    public Object[][] getSelectedModels() {
        return selectedModels;
    }

    /**
     *
     * @param selectedModels
     */
    public void setSelectedModels(Object[][] selectedModels) {
        this.selectedModels = selectedModels;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getWeightedMeanOptions() {
        return weightedMeanOptions;
    }

    /**
     *
     * @param weightedMeanOptions
     */
    public void setWeightedMeanOptions(Map<String, String> weightedMeanOptions) {
        this.weightedMeanOptions = weightedMeanOptions;
    }
}
