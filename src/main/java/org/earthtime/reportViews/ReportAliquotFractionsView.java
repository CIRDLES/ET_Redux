/*
 * ReportAliquotFractionsView.java
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
package org.earthtime.reportViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MouseInputListener;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.fop.svg.PDFTranscoder;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.FractionNotesDialog;
import org.earthtime.UPb_Redux.filters.SVGFileFilter;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.evolution.seaWater.SeawaterModelAssignmentManager;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author James F. Bowring
 */
public class ReportAliquotFractionsView extends JLayeredPane implements ReportUpdaterInterface {

    private ETReduxFrame parentFrame;
    private SampleInterface sample;
    private boolean activeTrueRejectsFalse;
    private String[][] reportFractions;
    private int aliquotCount;
    private JLayeredPane upperLeftCorner;
    private JLayeredPane reportHeader;
    private JLayeredPane reportFractionIDs;
    private JScrollPane reportFractionIDsScrollPane;
    private JLayeredPane reportBody;
    private JScrollPane reportBodyScrollPane;
    private int reportWidth;
    private int fractionColumnWidth;
    private int DATATABLE_TOP_HEIGHT = 80;//62;
    private float COLUMN_WIDTH_ADJUST_FACTOR = 8.05f;//8.f;//7.3f;//6.9f;
    private float dividerWidth = 10f;
    private int lineHeight = 17;
    private String displayMessage = "";
    private JButton sortFractionsButton;
    private JButton toggleMeasuredRatiosButton;
    private JButton toggleAliquotBarsButton;
    private JButton showSeawaterAssignmentsButton;
    public static boolean showAliquotBars = false;
    private ArrayList<JButton> sortButtons;
    private static TableRowObject lastAquiredTableRowObject;

    /**
     *
     */
    protected int sortedColumnNumber = 2;

    /**
     *
     */
    protected int sortedColumnDirection = 1; // 1 = asc, -1 = desc
    /**
     *
     */
    private int leftMargin = 5;
    private int fractionButtonMargin = 20;

    /**
     *
     * @param parentFrame
     * @param activeTrueRejectsFalse
     */
    public ReportAliquotFractionsView(ETReduxFrame parentFrame, boolean activeTrueRejectsFalse) {
        super();

        this.parentFrame = parentFrame;
        this.activeTrueRejectsFalse = activeTrueRejectsFalse;
        this.reportFractions = new String[0][0];
        this.aliquotCount = 0;

        setOpaque(true);

        setBackground(Color.white);

        addComponentListener(new ViewListener());
    }

    /**
     *
     *
     * @param parentFrame the value of parentFrame
     * @param sample
     * @param activeTrueRejectsFalse
     */
    public ReportAliquotFractionsView(ETReduxFrame parentFrame, SampleInterface sample, boolean activeTrueRejectsFalse) {
        this(parentFrame, activeTrueRejectsFalse);

        this.sample = sample;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;
    }

    private synchronized void prepareReportFractionsArrayForDisplay() {
        if (activeTrueRejectsFalse) {
            reportFractions = SampleInterface.reportActiveFractionsByNumberStyle(sample, false);
        } else {
            reportFractions = SampleInterface.reportRejectedFractionsByNumberStyle(sample, false);
        }

        sortReportColumn(reportFractions, sortedColumnNumber, sortedColumnDirection);
    }

    /**
     *
     * @param performReduction
     * @param inLiveMode the value of inLiveMode
     * @param fractionIdToFocus the value of fractionIdToFocus
     */
    @Override
    public void updateReportTable(boolean performReduction, boolean inLiveMode, String fractionIdToFocus) {
        parentFrame.updateReportTable(performReduction, inLiveMode, fractionIdToFocus);
        prepareReportFractionsArrayForDisplay();
        reSizeSortButtons();
        repaint();
    }

    public void forceVerticalScrollToShowSpecificRow(String lastAcquiredFractionID) {
        ((ReportPainter) reportBody).setLastAcquiredFractionID(lastAcquiredFractionID);
        repaint();

        if (lastAquiredTableRowObject != null) {
            int bottomPixelCount = lastAquiredTableRowObject.getBottomPixelCount();
        }
    }

    /**
     * @param reportFractions the reportFractions to set
     */
    public void setReportFractions(String[][] reportFractions) {
        this.reportFractions = reportFractions;
    }

    /**
     * @return the reportFractions
     */
    public String[][] getReportFractions() {
        return reportFractions;
    }

    @Override
    public void updateEvolutionPlot() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class TableRowObject implements Comparable<TableRowObject> {

        private final int bottomPixelCount;
        private final Object rowObject;

        TableRowObject(int bottomPixelCount, Object rowObject) {
            this.bottomPixelCount = bottomPixelCount;
            this.rowObject = rowObject;
        }

        @Override
        public int compareTo(TableRowObject o) {
            return (new Integer(this.bottomPixelCount).compareTo(o.bottomPixelCount));
        }

        /**
         * @return the bottomPixelCount
         */
        public int getBottomPixelCount() {
            return bottomPixelCount;
        }

        /**
         * @return the rowObject
         */
        public Object getRowObject() {
            return rowObject;
        }
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
     * @param g
     */
    public void paint(Graphics2D g) {
        try {
            upperLeftCorner.repaint();
            reportHeader.repaint();
            reportFractionIDs.repaint();
            reportBody.repaint();
        } catch (Exception e) {
        }
    }

    private float calculateColumnWidth(int col) {
        float colWidth;
        if (reportFractions.length > Integer.parseInt(reportFractions[0][0])) {

            int myIndex = 0;
            try {
                myIndex = Integer.valueOf(reportFractions[0][0]);
            } catch (NumberFormatException numberFormatException) {
            }
            try {
                colWidth = reportFractions[myIndex][col].length();
            } catch (Exception e) {
                colWidth = 0.0f;
            }
        } else {
            try {
                colWidth = Math.max(reportFractions[1][col].trim().length(),//
                        Math.max(reportFractions[2][col].trim().length(),//
                                reportFractions[3][col].trim().length() + reportFractions[5][col].trim().length() / 2));// row 5 = footnote letters
            } catch (Exception e) {
                colWidth = 0.0f;
            }
        }

        return colWidth;
    }

    private void reSizeScrollPanes() {
        //July 2016 adapted to handle narrow tables per LiveData 
        reportBodyScrollPane.setSize(Math.min(reportWidth - fractionColumnWidth + fractionButtonMargin + 2 * (int) dividerWidth, getWidth() - fractionColumnWidth), getHeight() - DATATABLE_TOP_HEIGHT);
        reportBodyScrollPane.revalidate();

        // -15 compensates for hidden scrollbar when coordinating scrolls
        int offsetForScrollbar = 15;

        reportFractionIDsScrollPane.setSize(fractionColumnWidth, getHeight() - DATATABLE_TOP_HEIGHT - offsetForScrollbar);
        reportFractionIDsScrollPane.revalidate();

        repaint();
    }

    private void reSizeSortButtons() {
        int fractionDataStartRow = Integer.parseInt(reportFractions[0][0]);
        if (reportFractions.length >= fractionDataStartRow) {
            sortFractionsButton.setBounds(1, DATATABLE_TOP_HEIGHT - lineHeight - 1, fractionColumnWidth - 3, lineHeight - 3);
            toggleMeasuredRatiosButton.setBounds(1, 2, fractionColumnWidth - 3, lineHeight - 3);
            toggleAliquotBarsButton.setBounds(1, 2, fractionColumnWidth - 3, lineHeight - 3);
            showSeawaterAssignmentsButton.setBounds(1, 2 + lineHeight - 3, fractionColumnWidth - 3, lineHeight - 3);

            int drawnWidth = 3;
            // oct 2016 added -1 for filtering cell added
            for (int c = 3; c < reportFractions[0].length - 1; c++) {
                float colWidth = calculateColumnWidth(c) * COLUMN_WIDTH_ADJUST_FACTOR;
                sortButtons.get(c - 3).setBounds( //
                        drawnWidth + 2, DATATABLE_TOP_HEIGHT - lineHeight - 1, (int) colWidth + 4, lineHeight - 3);
                //drawnWidth += colWidth + dividerWidth * 1.02 + c * 0.0075;// + 0.495;

                try {
                    drawnWidth += (reportFractions[fractionDataStartRow][c].length()) * COLUMN_WIDTH_ADJUST_FACTOR + dividerWidth * 1.05 + (c - 3) * 0.008;
                } catch (Exception e) {
                }
            }
        }
    }

    private class ViewListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            try {
                reSizeScrollPanes();
            } catch (Exception resizeException) {
            }
        }

        @Override
        public void componentMoved(ComponentEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void componentShown(ComponentEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void componentHidden(ComponentEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    /**
     *
     */
    public void refreshPanel() {
        preparePanel();
        reSizeScrollPanes();
    }

    /**
     *
     */
    public void preparePanel() {

        int saveVerticalScrollPosition = 0;
        int saveHorizontalScrollPosition = 0;
        try {
            saveVerticalScrollPosition = reportBodyScrollPane.getVerticalScrollBar().getValue();
            saveHorizontalScrollPosition = reportBodyScrollPane.getHorizontalScrollBar().getValue();
        } catch (Exception e) {
            saveVerticalScrollPosition = 0;
            saveHorizontalScrollPosition = 0;
        }

        // reset panel
        removeAll();

        // also reset fractions selected re: red box
        sample.deSelectAllFractionsInDataTable();

        // first get reportFractions from sample
        try {
            prepareReportFractionsArrayForDisplay();

        } catch (Exception e) {
        }
        // restore leftmargin
        leftMargin = 5;

        // then pre-process to determine sizes of panels             
        float width = 0;
        // oct 2016 added -1 for filtering cell added
        for (int i = 2; i < reportFractions[0].length - 1; i++) {
            width += calculateColumnWidth(i) * COLUMN_WIDTH_ADJUST_FACTOR + dividerWidth;
        }

        reportWidth = (int) width + (int) dividerWidth;

        // column #2 is the fractionID column
        fractionColumnWidth
                = leftMargin + (int) (calculateColumnWidth(2) * COLUMN_WIDTH_ADJUST_FACTOR) + (int) dividerWidth;

        fractionColumnWidth += fractionButtonMargin;

        aliquotCount = sample.getActiveAliquots().size();

        // set up panels
        upperLeftCorner = new ReportPainter(this, "FRACTION_HEADER", false);
        upperLeftCorner.setBackground(Color.white);
        upperLeftCorner.setOpaque(true);
        upperLeftCorner.setBounds(0, 0, fractionColumnWidth, DATATABLE_TOP_HEIGHT);
        add(upperLeftCorner, JLayeredPane.PALETTE_LAYER);

        reportHeader = new ReportPainter(this, "HEADER", false);
        reportHeader.setBackground(Color.white);
        reportHeader.setOpaque(true);
        reportHeader.setBounds(fractionColumnWidth, 0, Math.min(reportWidth - fractionColumnWidth + fractionButtonMargin + (int) dividerWidth, getWidth() - fractionColumnWidth), DATATABLE_TOP_HEIGHT);
        reportHeader.setPreferredSize(new Dimension(Math.min(reportWidth - fractionColumnWidth + fractionButtonMargin + (int) dividerWidth, getWidth() - fractionColumnWidth), DATATABLE_TOP_HEIGHT));
        add(reportHeader);

        // july 2017 no buttons if empty report
        if (reportFractions.length > Integer.parseInt(reportFractions[0][0])) {
            // start with singleton
            sortFractionsButton = new ET_JButton("Sort");
            sortFractionsButton.setFont(ReduxConstants.sansSerif_10_Bold);
            sortFractionsButton.addActionListener(new SortButtonActionListener(2));
            upperLeftCorner.add(sortFractionsButton, JLayeredPane.PALETTE_LAYER);

            // build sort buttons
            sortButtons = new ArrayList<>();
            // oct 2016 added -1 for filtering cell added
            for (int c = 3; c < reportFractions[0].length - 1; c++) {

                JButton sortButton = new ET_JButton("\u25B2 \u25BC");
                sortButton.setFont(ReduxConstants.sansSerif_10_Bold);
                sortButton.addActionListener(new SortButtonActionListener(c));
                sortButtons.add(sortButton);

                reportHeader.add(sortButton, DEFAULT_LAYER);
            }

            // June 2016 add button upper left to toggle measured ratios inside compostion for Dan Condon et al issue
            toggleMeasuredRatiosButton = new ET_JButton("Toggle Measured");
            toggleMeasuredRatiosButton.setFont(ReduxConstants.sansSerif_10_Bold);
            toggleMeasuredRatiosButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    sample.getReportSettingsModel().toggleMeasuredRatiosInCompositionCategory();
                    parentFrame.loadAndShowReportTableData("");
                }
            });
            if (sample.isAnalysisTypeIDTIMS()) {
                upperLeftCorner.add(toggleMeasuredRatiosButton, JLayeredPane.PALETTE_LAYER);
            }

            // July 2017 add button upper left to toggle Aliquot Bars in support of USeries
            toggleAliquotBarsButton = new ET_JButton((showAliquotBars ? "Hide " : "Show ") + "Aliquot Bars");
            toggleAliquotBarsButton.setFont(ReduxConstants.sansSerif_10_Bold);
            toggleAliquotBarsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAliquotBars = !showAliquotBars;
                    toggleAliquotBarsButton.setText((showAliquotBars ? "Hide " : "Show ") + "Aliquot Bars");
                    parentFrame.loadAndShowReportTableData("");
                }
            });
            if (reportFractions[1][0].contains("UTh")) {
                upperLeftCorner.add(toggleAliquotBarsButton, JLayeredPane.PALETTE_LAYER);
            } else {
                showAliquotBars = true;
            }

            // July 2019 add button upper left to access seawater assignment in support of USeries
            showSeawaterAssignmentsButton = new ET_JButton("Assign SeawaterModels");
            showSeawaterAssignmentsButton.setFont(ReduxConstants.sansSerif_10_Bold);
            showSeawaterAssignmentsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SeawaterModelAssignmentManager seawaterModelAssignmentManager 
                            = new SeawaterModelAssignmentManager(parentFrame, true, sample);
                    seawaterModelAssignmentManager.setVisible(true);
                }
            });
            if (reportFractions[1][0].contains("UTh")) {
                upperLeftCorner.add(showSeawaterAssignmentsButton, JLayeredPane.PALETTE_LAYER);
            } else {
                showAliquotBars = true;
            }

            reSizeSortButtons();

        }

        // report fractionIDs in scroll pane
        reportFractionIDs = new ReportPainter(this, "FRACTION", true);
        reportFractionIDs.setBackground(Color.white);
        reportFractionIDs.setOpaque(true);
        reportFractionIDs.setPreferredSize(new Dimension(//
                fractionColumnWidth, (reportFractions.length + aliquotCount * (int) (showAliquotBars ? 1 : 0)) * lineHeight + 175));

        reportFractionIDsScrollPane = new JScrollPane(reportFractionIDs);
        reportFractionIDsScrollPane.setBorder(null);
        reportFractionIDsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        reportFractionIDsScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        reportFractionIDsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        reportFractionIDsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        // eliminate default "crawl"
        reportFractionIDsScrollPane.getVerticalScrollBar().setUnitIncrement(64);
        reportFractionIDsScrollPane.getVerticalScrollBar().setBlockIncrement(2);
        reportFractionIDsScrollPane.getHorizontalScrollBar().setUnitIncrement(64);

        reportFractionIDsScrollPane.setLocation(0, DATATABLE_TOP_HEIGHT);

        reportFractionIDsScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent evt) {
                reportBodyScrollPane.getVerticalScrollBar().setValue( //
                        reportFractionIDsScrollPane.getVerticalScrollBar().getValue());
                reportBodyScrollPane.revalidate();
            }
        });

        reportFractionIDsScrollPane.validate();

        // report body inside scrollpane
        reportBody = new ReportPainter(this, "BODY", true);
        reportBody.setBackground(Color.white);
        reportBody.setOpaque(true);
        reportBody.setPreferredSize(new Dimension(//
                reportWidth - fractionColumnWidth + fractionButtonMargin, //
                (reportFractions.length + aliquotCount * (int) (showAliquotBars ? 1 : 0)) * lineHeight + 175));

        reportBodyScrollPane = new JScrollPane(reportBody);
        reportBodyScrollPane.setBorder(null);
        reportBodyScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        reportBodyScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        // eliminate default "crawl"
        reportBodyScrollPane.getVerticalScrollBar().setUnitIncrement(64);
        reportBodyScrollPane.getVerticalScrollBar().setBlockIncrement(2);
        reportBodyScrollPane.getHorizontalScrollBar().setUnitIncrement(64);

        reportBodyScrollPane.setLocation(fractionColumnWidth, DATATABLE_TOP_HEIGHT);

        reportBodyScrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent evt) {
                // causes header panel to coordinate with data panel
                reportHeader.setBounds(//
                        fractionColumnWidth - evt.getValue() - 2, 0, reportWidth, DATATABLE_TOP_HEIGHT);
            }
        });

        reportBodyScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent evt) {
                reportFractionIDsScrollPane.getVerticalScrollBar().setValue( //
                        reportBodyScrollPane.getVerticalScrollBar().getValue());
                reportFractionIDsScrollPane.revalidate();

            }
        });

        reportBodyScrollPane.validate();

        // add after all objects created
        add(reportFractionIDsScrollPane, DEFAULT_LAYER);
        add(reportBodyScrollPane, DEFAULT_LAYER);

        validate();

        reportBodyScrollPane.getVerticalScrollBar().setValue(saveVerticalScrollPosition);
        reportBodyScrollPane.getHorizontalScrollBar().setValue(saveHorizontalScrollPosition);

    } // preparePanel

    private class SortButtonActionListener implements ActionListener {

        int columnNumber;

        public SortButtonActionListener(int columnNumber) {
            this.columnNumber = columnNumber;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (sortedColumnNumber == columnNumber) {
                // toggle direction of sort
                sortedColumnDirection = -sortedColumnDirection;
            } else {
                // sort small to large
                sortedColumnDirection = 1;
            }

            sortedColumnNumber = columnNumber;
            sortReportColumn(getReportFractions(), columnNumber, sortedColumnDirection);
            repaint();
        }
    }

    /**
     *
     * @param reportFractions
     * @param columnNumber
     * @param sortedColumnDirection
     */
    public static void sortReportColumn( //
            final String[][] reportFractions, //
            final int columnNumber, //
            final int sortedColumnDirection) {
        int fractionDataStartRow = Integer.parseInt(reportFractions[0][0]);

        String[][] reportFractionsSorted = new String[reportFractions.length - fractionDataStartRow][reportFractions[0].length];
        for (int i = 0; i < reportFractionsSorted.length; i++) {
            for (int j = 0; j < reportFractionsSorted[i].length; j++) {
                reportFractionsSorted[i][j] = reportFractions[i + fractionDataStartRow][j];
            }
        }

        Arrays.sort(reportFractionsSorted, (final String[] entry1, final String[] entry2) -> {
            int retVal = 0;

            // aliquots have been ordered in manager by number regardless of name
            // so these sorts are within each aliquot
            // compare aliquot name
            // entry1[1] is aliquot name and entry1[2] is fraction name or columnNumber is for secondary Fraction column
            if (entry1[1].trim().equalsIgnoreCase(entry2[1].trim())) {
                if ((columnNumber == 2) || reportFractions[0][columnNumber].trim().equalsIgnoreCase("Fraction")) {

                    String field1;
                    try {
                        field1 = entry1[columnNumber].trim();
                    } catch (Exception e) {
                        field1 = "";
                    }
                    String field2;
                    try {
                        field2 = entry2[columnNumber].trim();
                    } catch (Exception e) {
                        field2 = "";
                    }
                    Comparator<String> forNoah = new IntuitiveStringComparator<>();
                    if (sortedColumnDirection == 1) {
                        retVal = forNoah.compare(field1, field2);
                    } else {
                        retVal = forNoah.compare(field2, field1);
                    }

                } else {

                    BigDecimal field1;
                    try {
                        field1 = new BigDecimal(entry1[columnNumber].trim());
                    } catch (Exception e) {
                        field1 = BigDecimal.ZERO;
                    }

                    BigDecimal field2;
                    try {
                        field2 = new BigDecimal(entry2[columnNumber].trim());
                    } catch (Exception e) {
                        field2 = BigDecimal.ZERO;
                    }

                    if (sortedColumnDirection == 1) {
                        retVal = field1.compareTo(field2);
                    } else {
                        retVal = field2.compareTo(field1);
                    }
                }
            } //            }
            else {
                retVal = 0;//Aliquots are already sorted by number elsewhere so no sort here
//                    retVal = entry1[1].trim().compareToIgnoreCase( entry2[1].trim() );
            }
            return retVal;
        });

        System.arraycopy(reportFractionsSorted, 0, reportFractions, fractionDataStartRow, reportFractionsSorted.length);
    }

    /**
     * @return the displayMessage
     */
    public String getDisplayMessage() {
        return displayMessage;
    }

    /**
     * @param displayMessage the displayMessage to set
     */
    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    /**
     *
     *
     * @param fileNameForSVGPDF the value of fileNameForSVGPDF
     * @param pdfOutput
     * @return
     */
    public File createReportSVGandPDF(String fileNameForSVGPDF, boolean pdfOutput)
            throws ETException {

        File svg_pdf_File = FileHelper.AllPlatformSaveAs(
                new Frame(),
                "Save Report Table as SVG and PDF File: *" + ".svg",
                null,
                ".SVG",
                fileNameForSVGPDF + "_ReportTable" + ".svg",
                new SVGFileFilter());

        if (svg_pdf_File != null) {

            ReportPainter svg_pdfReport = new ReportPainter(this, "BOTH", true);

            svg_pdfReport.outputToSVG(svg_pdf_File);

            // lets garbage collect
            System.gc();

            if (pdfOutput) {
                try {
                    String fileName = svg_pdfReport.outputToPDFviaMajas(svg_pdf_File);

                    svg_pdfReport.viewPDF(fileName);
                } catch (Exception e) {
                    throw new ETException(null, "Unable to create PDF, please report details to development team.");
                }
            }
        }

        return svg_pdf_File;

    }

    private class ReportPainter extends JLayeredPane implements MouseInputListener {

        private final ReportUpdaterInterface parent;
        private final boolean showFractions;
        private final String paintType; // "BOTH", "HEADER", "BODY", "FRACTION", "FRACTION_HEADER"
        private ArrayList<TableRowObject> verticalPixelFractionMap;
        private TableRowObject lastSelectedTableRowObject;
        private String lastAcquiredFractionID;

        public ReportPainter(ReportUpdaterInterface parent, String paintType, boolean showFractions) {
            super();
            this.parent = parent;
            this.paintType = paintType;
            this.showFractions = showFractions;

            verticalPixelFractionMap = new ArrayList<>();
            this.lastSelectedTableRowObject = new TableRowObject(0, new UPbFraction());

            this.lastAcquiredFractionID = "";

            setOpaque(true);

            setBackground(Color.white);

            addListeners();
        }

        private void addListeners() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            paint((Graphics2D) g);
        }

        /**
         *
         * @param g2D
         */
        public void paint(Graphics2D g2D) {

            verticalPixelFractionMap = new ArrayList<>();

            RenderingHints rh = g2D.getRenderingHints();
            rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2D.setRenderingHints(rh);

            g2D.setColor(Color.white);

            // set up headers
            leftMargin = 5;
            float drawnWidth = leftMargin;
            float overallWidth = 0;

            int topMargin = -2;
            int drawnHeight = 0;

            int fractionDataStartRow = Integer.parseInt(getReportFractions()[0][0]);

            // http://webdesign.about.com/od/fonts/qt/web-safe-fonts.htm
            Font numberFont = new Font("Monospaced", Font.BOLD, 13);//Lucida Sans Typewriter  Droid Sans Mono   Calibri 14  "Monospaced"
            Font footnoteFont = new Font("Monospaced", Font.BOLD, 9);

            g2D.setFont(numberFont);
            g2D.setColor(Color.BLACK);

            AliquotInterface aliquot = null;

            // message to display when sample is missing, etc.
            if (displayMessage.length() > 0) {

                g2D.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2D.drawString(displayMessage, 100, 100);

            } else {

                if (paintType.equalsIgnoreCase("FRACTION_HEADER")) {
                    g2D.drawString(
                            "Fraction",
                            drawnWidth,
                            drawnHeight + topMargin + lineHeight * 2 + 22);

                    // start with a vertical line
                    g2D.setStroke(new BasicStroke(0.5f));
                    g2D.setColor(Color.gray);

                    g2D.drawLine(//
                            fractionColumnWidth - 1, //
                            drawnHeight + lineHeight + topMargin + 3,
                            fractionColumnWidth - 1, //
                            drawnHeight + topMargin + lineHeight * 3 + 22 + 5);

                    // draw horizontal line under column titles
                    g2D.setColor(Color.black);
                    g2D.drawLine(0, //
                            drawnHeight + topMargin + lineHeight * 3 + 11,//
                            fractionColumnWidth,//
                            drawnHeight + topMargin + lineHeight * 3 + 11);
                    g2D.drawLine(0, //
                            drawnHeight + topMargin + lineHeight * 4 + 11,//
                            fractionColumnWidth,//
                            drawnHeight + topMargin + lineHeight * 4 + 11);
                }

                if (paintType.equalsIgnoreCase("BOTH") || paintType.equalsIgnoreCase("HEADER")) {
                    // category titles
                    String catName = "Fraction";
                    String savedCatName = catName;
                    int colStart = 2;
                    if (paintType.equalsIgnoreCase("HEADER")) {
                        colStart = 3;
                    }
                    // oct 2016 added -1 for filtering cell added
                    for (int c = colStart; c < reportFractions[0].length - 1; c++) {
                        float colWidth = calculateColumnWidth(c) * COLUMN_WIDTH_ADJUST_FACTOR;

                        catName = getReportFractions()[0][c].trim();
                        if ((!catName.equalsIgnoreCase(savedCatName))
                                && (!catName.equalsIgnoreCase("Fraction"))) {// added nov 2009 for second fraction column

                            // May 2010 fix to erase any part of previous category that would be overwritten due to too narrow category condition
                            Color savedColor = g2D.getColor();
                            g2D.setColor(Color.white);
                            g2D.fillRect((int) drawnWidth - 5, topMargin + lineHeight / 4, (int) Math.ceil(colWidth + dividerWidth), lineHeight);
                            g2D.setColor(savedColor);

                            // June 2017 special condition to hide category per Noah
                            g2D.drawString((String) (reportFractions[1][0].contains("UTh") ? "" : catName),
                                    drawnWidth,
                                    drawnHeight + topMargin + lineHeight);

                            savedCatName = catName;
                        }
                        drawnWidth += colWidth + dividerWidth;
                    }

                    // column titles
                    drawnWidth = leftMargin;
                    drawnHeight += lineHeight;
                    int accumulateDrawnHeight = 0;

                    // oct 2016 added -1 for filtering cell added
                    for (int c = colStart; c < reportFractions[0].length - 1; c++) {
                        float colWidth = calculateColumnWidth(c) * COLUMN_WIDTH_ADJUST_FACTOR;

                        accumulateDrawnHeight = 0;

                        g2D.drawString(reportFractions[1][c],
                                drawnWidth,
                                drawnHeight + topMargin + lineHeight + accumulateDrawnHeight);
                        g2D.drawString(reportFractions[2][c],
                                drawnWidth,
                                drawnHeight + topMargin + lineHeight + accumulateDrawnHeight + 11);
                        accumulateDrawnHeight += 11;
                        g2D.drawString(reportFractions[3][c],
                                drawnWidth,
                                drawnHeight + topMargin + lineHeight + accumulateDrawnHeight + 11);
                        accumulateDrawnHeight += 11;

                        // handle footnote letters, which are stored in reportFractions[5]
                        if (!reportFractions[5][c].equalsIgnoreCase("")) {
                            g2D.setFont(footnoteFont);
                            g2D.drawString(reportFractions[5][c],//superScript for footnote
                                    //drawnWidth + reportFractions[3][c].trim().length() * COLUMN_WIDTH_ADJUST_FACTOR,//6.8f,
                                    drawnWidth + reportFractions[3][c].length() * COLUMN_WIDTH_ADJUST_FACTOR,//6.8f,
                                    drawnHeight + topMargin + lineHeight + accumulateDrawnHeight - 4);//7);
                            g2D.setFont(numberFont);
                        }

                        drawnWidth += colWidth;

                        g2D.setStroke(new BasicStroke(0.5f));
                        g2D.setColor(Color.gray);

                        g2D.drawLine(//
                                (int) (leftMargin + drawnWidth + 2), //
                                drawnHeight + topMargin + 3,// + lineHeight, //
                                (int) (leftMargin + drawnWidth + 2), //
                                drawnHeight + topMargin + lineHeight * 2 + accumulateDrawnHeight + 5);//13);
                        g2D.setColor(Color.black);

                        drawnWidth += dividerWidth;
                    }

                    drawnHeight += lineHeight + accumulateDrawnHeight + 4;
                    accumulateDrawnHeight = 0;

                    overallWidth = drawnWidth - 3;

                    // draw horizontal line under column titles
                    g2D.setColor(Color.black);
                    g2D.drawLine(0, drawnHeight, reportWidth - fractionColumnWidth + fractionButtonMargin + (int) dividerWidth, drawnHeight);
                    g2D.setColor(Color.black);

                    // section for sort arrows
                    g2D.drawLine(0, drawnHeight + lineHeight, reportWidth - fractionColumnWidth + fractionButtonMargin + (int) dividerWidth, drawnHeight + lineHeight);
                    g2D.setColor(Color.black);
                    drawnHeight -= 2;//5;

                }

                // paint fractions and set up interactivity
                if (paintType.equalsIgnoreCase("BOTH") || paintType.equalsIgnoreCase("BODY") || paintType.equalsIgnoreCase("FRACTION")) {

                    if ((reportFractions.length < fractionDataStartRow + 1)//
                            &&//
                            !paintType.equalsIgnoreCase("FRACTION")) {
                        g2D.drawString(
                                "There are no Fractions Selected.",
                                12,
                                (drawnHeight + topMargin + lineHeight));
                        g2D.drawString(
                                "The displayed Report Headers are used for style " + reportFractions[1][0] + ".",
                                12,
                                (drawnHeight + topMargin + 2 * lineHeight));
                        g2D.drawString(
                                "Use Reports menu to customize the table.",
                                12,
                                (drawnHeight + topMargin + 3 * lineHeight));
                    } else {
                        String saveAliquotName = "";

                        int grayBarRowCount = 0;

                        for (int row = fractionDataStartRow; row < reportFractions.length; row++) {
                            drawnWidth = leftMargin;

                            // april 2012 reportFractions will contain only accepted OR rejected, thus here check for printing fractions
                            if (showFractions) {
                                grayBarRowCount++;
                                // for each aliquot                                
                                if (!reportFractions[row][1].equalsIgnoreCase(saveAliquotName)) {
                                    saveAliquotName = reportFractions[row][1];

                                    if (showAliquotBars) {
                                        g2D.setColor(ReduxConstants.myAliquotGrayColor);

                                        g2D.fillRect(0, drawnHeight + 2 + topMargin + 0, reportWidth - 1, lineHeight + 2);

                                        g2D.setColor(Color.BLACK);

                                        if (!paintType.equalsIgnoreCase("FRACTION")) {
                                            // aliquot  name
                                            g2D.drawString(reportFractions[row][1],
                                                    leftMargin,
                                                    drawnHeight + topMargin + lineHeight);
                                        }
                                    }

                                    // build map of row to fraction objects and aliquot objects
                                    if ((sample != null) && paintType.equalsIgnoreCase("FRACTION")) {
                                        aliquot = sample.getAliquotByName(reportFractions[row][1].trim());
                                        if (showAliquotBars) {
                                            verticalPixelFractionMap.add( //
                                                    new TableRowObject( //
                                                            drawnHeight + topMargin + lineHeight + 1,//
                                                            aliquot));

                                            if (((ReportRowGUIInterface) aliquot).isSelectedInDataTable()) {
                                                // dec 2011 give some button characteristics for selected aliquot 
                                                g2D.setColor(Color.red);
                                                g2D.drawRoundRect( //
                                                        leftMargin - 1,//
                                                        drawnHeight + topMargin + 3, //
                                                        fractionColumnWidth - 5,
                                                        lineHeight - 2, 5, 5);
                                                g2D.setColor(Color.BLACK);
                                            }
                                        }
                                    }

                                    if (paintType.equalsIgnoreCase("BODY") || paintType.equalsIgnoreCase("BOTH")) {
                                        leftMargin = 3;
                                    }
                                    drawnWidth = leftMargin;

                                    if (showAliquotBars) {
                                        drawnHeight += lineHeight + 5;
                                    }
                                }

                                // try gray bar style
                                if (grayBarRowCount % 2 == 0) {
                                    g2D.setColor(new Color(240, 240, 240));
                                    g2D.fillRect(leftMargin, drawnHeight + topMargin + 2, reportWidth - 5, lineHeight + 0);
                                }

                                // fraction data
                                // oct 2016 -1 added in to compensate for additional cell that records true/false filtering
                                int columnCount = reportFractions[0].length - 1;
                                boolean showAsFilteredOut = (reportFractions[row][columnCount].compareToIgnoreCase("false") == 0);
                                int columnStart = 2;
                                if (paintType.equalsIgnoreCase("FRACTION")) {
                                    columnCount = 3;
                                }
                                if (paintType.equalsIgnoreCase("BODY")) {
                                    columnStart = 3;
                                }

                                // build map of row to fraction objects and aliquot objects
                                if ((sample != null) && paintType.equalsIgnoreCase("FRACTION") && (aliquot != null)) {
                                    // april 2012
                                    //right shift text in fraction column to allow for fractionButtonMargin
                                    drawnWidth += fractionButtonMargin;

                                    ETFractionInterface fraction = null;
                                    try {
                                        fraction = sample.getFractionByIDAndAliquotNumber(reportFractions[row][2].trim(),
                                                ((ReduxAliquotInterface) aliquot).getAliquotNumber());
                                        TableRowObject currentFractionTableRowObject
                                                = new TableRowObject( //
                                                        drawnHeight + topMargin + lineHeight + 1,//
                                                        fraction);
                                        verticalPixelFractionMap.add(currentFractionTableRowObject);
                                        if (lastAcquiredFractionID.compareToIgnoreCase(fraction.getFractionID()) == 0) {
                                            lastAquiredTableRowObject = currentFractionTableRowObject;
                                        }

                                        if (((ReportRowGUIInterface) fraction).isSelectedInDataTable()) {
                                            // dec 2011 give some button characteristics for selected fraction 
                                            g2D.setColor(Color.red);
                                            g2D.drawRoundRect( //
                                                    (int) drawnWidth,//leftMargin + 2,//
                                                    drawnHeight + topMargin + 3, //
                                                    (int) (calculateColumnWidth(2) * COLUMN_WIDTH_ADJUST_FACTOR) + 0,//
                                                    lineHeight - 2, 5, 5);

                                            // notes box
                                            g2D.drawRoundRect( //
                                                    leftMargin - 1,
                                                    drawnHeight + topMargin + 4, //
                                                    lineHeight - 3, //
                                                    lineHeight - 3, 5, 5);
                                            g2D.setColor(Color.BLACK);
                                        }

                                        // add in Notes box
                                        if (fraction.getFractionNotes().length() > 0) {
                                            g2D.setColor(Color.blue);
                                        } else {
                                            g2D.setColor(Color.black);
                                        }
                                    } catch (Exception e) {
                                    }

                                    g2D.draw3DRect( //
                                            leftMargin, //
                                            drawnHeight + topMargin + 5, //
                                            lineHeight - 5, //
                                            lineHeight - 5, //
                                            true);

                                    g2D.setFont(ReduxConstants.sansSerif_10_Bold);
                                    g2D.drawString(
                                            "N",
                                            leftMargin + 3,
                                            drawnHeight + topMargin + lineHeight - 2);

                                }

                                g2D.setFont(numberFont);
//                                g2D.setColor(Color.BLACK);

                                for (int c = columnStart; c < columnCount; c++) {
                                    // oct 2016 filtering
                                    if (showAsFilteredOut) {
                                        g2D.setColor(Color.LIGHT_GRAY);
                                    } else {
                                        g2D.setColor(Color.BLACK);
                                    }

                                    try {
                                        g2D.drawString(reportFractions[row][c],
                                                leftMargin + drawnWidth,
                                                drawnHeight + topMargin + lineHeight);

                                    } catch (Exception ex) {
                                        g2D.drawString("",
                                                leftMargin + drawnWidth,
                                                drawnHeight + topMargin + lineHeight);
                                    }

                                    drawnWidth += (reportFractions[fractionDataStartRow][c].length()) * COLUMN_WIDTH_ADJUST_FACTOR;

                                    // vertical line
                                    g2D.setColor(Color.gray);

                                    g2D.drawLine(//
                                            (int) (leftMargin + drawnWidth + 4), //
                                            drawnHeight + topMargin,// + lineHeight, //
                                            (int) (leftMargin + drawnWidth + 4), //
                                            drawnHeight + topMargin + lineHeight + 10);

                                    drawnWidth += dividerWidth;
                                }

                                drawnHeight += lineHeight;
                            }
                        }
                    }

                    // list out footnotes
                    g2D.setColor(Color.BLACK);
                    if (!paintType.equalsIgnoreCase("FRACTION")) {
                        drawnHeight += lineHeight * 3;
                        drawnWidth = leftMargin;

                        for (String item : reportFractions[6]) {
                            if (!item.equals("")) {
                                // strip out footnote letter
                                String[] footNote = item.split("&");
                                // footnote may contain one or more new lines 
                                if (footNote[1].contains("\n")) {
                                    String[] footNoteBody = footNote[1].split("\n");
                                    String footNoteLine
                                            = " " //
                                            + footNote[0] //
                                            + "  " //
                                            + footNoteBody[0];
                                    g2D.drawString(//
                                            footNoteLine, drawnWidth, (drawnHeight + topMargin + lineHeight));
                                    drawnWidth = leftMargin;
                                    drawnHeight += lineHeight;

                                    for (int l = 1; l < footNoteBody.length; l++) {
                                        footNoteLine
                                                = "    " //
                                                + footNoteBody[l];
                                        g2D.drawString(//
                                                footNoteLine, drawnWidth, (drawnHeight + topMargin + lineHeight));
                                        drawnWidth = leftMargin;
                                        drawnHeight += lineHeight;
                                    }
                                } else {
                                    String footNoteLine
                                            = " " //
                                            + footNote[0] //
                                            + "  " //
                                            + footNote[1];
                                    g2D.drawString(//
                                            footNoteLine, drawnWidth, (drawnHeight + topMargin + lineHeight));
                                    drawnWidth = leftMargin;
                                    drawnHeight += lineHeight;
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (paintType.equalsIgnoreCase("FRACTION") && (verticalPixelFractionMap.size() > 0)) {

                int mouseY = e.getY();
                int mouseX = e.getX();

                // determine index of row just previous
                int row = Collections.binarySearch(verticalPixelFractionMap, new TableRowObject(mouseY, null));
                row = Math.abs(row + 1);

                if (mouseY > verticalPixelFractionMap.get(verticalPixelFractionMap.size() - 1).getBottomPixelCount()) {
                    row = -1;
                }

                if (row >= 0) {

                    try {
                        Object fractionOrAliquot = verticalPixelFractionMap.get(row).getRowObject();

                        if (fractionOrAliquot instanceof ETFractionInterface) {

                            if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                                // determine if note box or fraction name
                                if (mouseX < lineHeight) {
                                    // show notes
                                    JDialog notesDialog = new FractionNotesDialog(parentFrame, true, (ETFractionInterface) fractionOrAliquot);
                                    notesDialog.setLocation(parentFrame.getX() + 300, parentFrame.getY() + 300);
                                    notesDialog.setVisible(true);
                                } else {
                                    parentFrame.editFraction(((ETFractionInterface) verticalPixelFractionMap.get(row).getRowObject()), 8);// kwikitab
                                    updateReportTable(false, false, "");
                                }
                            } else {
                                boolean isRejected = !((ETFractionInterface) verticalPixelFractionMap.get(row).rowObject).isRejected();
                                ((ETFractionInterface) verticalPixelFractionMap.get(row).getRowObject()).setRejected(isRejected);
                                // dec 2015 for tripoli fractions
                                try {
                                    if (verticalPixelFractionMap.get(row).getRowObject() instanceof UPbLAICPMSFraction) {
                                        ((UPbFractionI) verticalPixelFractionMap.get(row).getRowObject()).getTripoliFraction().setIncluded(!isRejected);
                                    }
                                } catch (Exception noTF) {
                                }
                                updateReportTable(false, false, "");
                            }
                        }

                        if (fractionOrAliquot instanceof AliquotInterface) {

                            if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                                parentFrame.editAliquotByProjectType(((AliquotInterface) verticalPixelFractionMap.get(row).getRowObject()));
                            } else {
                                AliquotInterface.toggleAliquotFractionsRejectedStatus(((ReduxAliquotInterface) verticalPixelFractionMap.get(row).getRowObject()));
                                updateReportTable(false, false, "");
                            }

                        }
                    } catch (Exception e2) {
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void mouseEntered(MouseEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (paintType.equalsIgnoreCase("FRACTION")) {
                try {
                    ((ReportRowGUIInterface) lastSelectedTableRowObject.getRowObject()).setSelectedInDataTable(false);
                    repaintTableRowElementButtonArea(lastSelectedTableRowObject);
                } catch (Exception ex) {
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
//            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (paintType.equalsIgnoreCase("FRACTION") && (verticalPixelFractionMap.size() > 0)) {

                int mouseY = e.getY();
                int mouseX = e.getX();

                // determine index of row just previous using pixel count of bottom of row
                int row = Collections.binarySearch(verticalPixelFractionMap, new TableRowObject(mouseY, null));
                row = Math.abs(row + 1);
                if (row >= verticalPixelFractionMap.size()) {
                    row--;
                }

                if (mouseY > verticalPixelFractionMap.get(verticalPixelFractionMap.size() - 1).getBottomPixelCount()) {
                    row = -1;
                }

                if (row >= 0) {

                    TableRowObject tableRowObject = verticalPixelFractionMap.get(row);

                    Object fractionOrAliquot = tableRowObject.getRowObject();

                    if (fractionOrAliquot instanceof ETFractionInterface) {
                        if (mouseX < lineHeight) {
                            setToolTipText(//
                                    "<html>"
                                    + "Notes:<br>"
                                    + ((ETFractionInterface) fractionOrAliquot).getFractionNotes()
                                    + "<br><br>"
                                    + "Left-Click here to edit fraction notes.<br>"
                                    + "Right-Click to toggle fraction inclusion.</html>");
                        } else {
                            setToolTipText("<html>Left-Click here to open fraction manager.<br>"
                                    + "Right-Click to toggle active / rejected.</html>");
                        }
                    } else {
                        setToolTipText("<html>Left-Click here to open aliquot manager.<br>"
                                + "Right-Click to toggle active / rejected for all fractions in this aliquot.</html>");
                    }

                    if (tableRowObject.getBottomPixelCount() != lastSelectedTableRowObject.getBottomPixelCount()) {
                        try {
                            ((ReportRowGUIInterface) lastSelectedTableRowObject.getRowObject()).setSelectedInDataTable(false);

                            repaintTableRowElementButtonArea(lastSelectedTableRowObject);
                            lastSelectedTableRowObject = tableRowObject;

                            ((ReportRowGUIInterface) fractionOrAliquot).setSelectedInDataTable(true);
                        } catch (Exception ex) {
                        }
                        repaintTableRowElementButtonArea(tableRowObject);
                    }

                }
            }
        }

        private void repaintTableRowElementButtonArea(TableRowObject tableRowObject) {
            repaint(0, tableRowObject.getBottomPixelCount() - lineHeight + 2, getWidth(), lineHeight + 3);
        }

        /**
         *
         * @param file
         */
        public String outputToPDFviaMajas(File file) {

            // Create a transcoder
            PDFTranscoder t = new PDFTranscoder();

            Map<TranscodingHints.Key, Float> hints = new HashMap<>();
            hints.put(PDFTranscoder.KEY_WIDTH, new Float(reportWidth));
            hints.put(PDFTranscoder.KEY_HEIGHT, reportFractions.length * 20f + 150f);
            t.setTranscodingHints(hints);

            TranscoderInput input = null;
            try {
                input = new TranscoderInput(new FileInputStream(file));

            } catch (FileNotFoundException fileNotFoundException) {
            }

            // Create the transcoder output.
            String transcoderOutputFileName = file.getAbsolutePath().replace(".svg", ".pdf");
            try {
                OutputStream ostream = new FileOutputStream(transcoderOutputFileName);
                TranscoderOutput output = new TranscoderOutput(ostream);
                // Save the image.

                t.transcode(input, output);

                // Flush and close the stream.
                try {
                    ostream.flush();
                    ostream.close();

                } catch (IOException iOException) {
                }
            } catch (FileNotFoundException | TranscoderException fileNotFoundException) {
            }

            return transcoderOutputFileName;
        }

        /**
         *
         * @param file
         */
        public void outputToSVG(File file) {

            // Get a DOMImplementation.
            DOMImplementation domImpl
                    = GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document.
            String svgNS = "http://www.w3.org/2000/svg";
            Document document = domImpl.createDocument(svgNS, "svg", null);

            // Create an instance of the SVG Generator.
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

            // Ask the test to render into the SVG Graphics2D implementation.
            paint(svgGenerator);

            // Finally, stream out SVG to the standard output using
            // UTF-8 encoding.
            boolean useCSS = true; // we want to use CSS style attributes

            Writer out = null;
            try {
                out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException fileNotFoundException) {
            }
            try {
                svgGenerator.stream(out, useCSS);
            } catch (SVGGraphics2DIOException sVGGraphics2DIOException) {
            }

            svgGenerator.dispose();
        }

        /**
         *
         * @param file
         */
        public void outputToPDF(File file) {
            SVGConverter myConv = new SVGConverter();
            myConv.setDestinationType(org.apache.batik.apps.rasterizer.DestinationType.PDF);
            try {
                myConv.setSources(new String[]{file.getCanonicalPath()});

            } catch (IOException iOException) {
            }
            myConv.setWidth((float) getWidth() + 2);
            myConv.setHeight((float) getHeight() + 2);

            // lets garbage collect
            System.gc();

            try {
                myConv.execute();

            } catch (SVGConverterException sVGConverterException) {
                System.out.println("Error in pdf conversion: " + sVGConverterException.getMessage());
            }
        }

        /**
         *
         * @param fileURL
         */
        public void viewPDF(String fileURL) {
            BrowserControl.displayURL(fileURL);
        }

        /**
         * @param lastAcquiredFractionID the lastAcquiredFractionID to set
         */
        public void setLastAcquiredFractionID(String lastAcquiredFractionID) {
            this.lastAcquiredFractionID = lastAcquiredFractionID;
        }
    }
}
