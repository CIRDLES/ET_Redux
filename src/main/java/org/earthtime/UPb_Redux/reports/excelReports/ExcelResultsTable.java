/*
 * ExcelResultsTable.java
 *
 * Created 19 October 2009
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.reports.excelReports;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.earthtime.UPb_Redux.filters.XLSFileFilter;
import static org.earthtime.UPb_Redux.reports.excelReports.ExcelReportConstants.EXCEL_FONT_FOR_NUMERIC_NUMBERS;
import static org.earthtime.UPb_Redux.reports.excelReports.ExcelReportConstants.EXCEL_FONT_FOR_STRING_NUMBERS;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public final class ExcelResultsTable {

    private static String sampleName;
    private static String[][] reportFractions;

    /**
     * 
     * @param mySampleName
     * @param myReportFractions
     * @param isNumeric
     */
    public static void produceExcelResultsTable ( String mySampleName, String[][] myReportFractions, boolean isNumeric ) {

        sampleName = mySampleName;
        reportFractions = myReportFractions;

        File excelFile = FileHelper.AllPlatformSaveAs(
                new Frame(),
                "Save Report Table as Excel File: *" + ExcelReportConstants.FILE_EXTENSION,
                null,
                ExcelReportConstants.FILE_EXTENSION,
                sampleName + "_ReportTable" + ExcelReportConstants.FILE_EXTENSION,
                new XLSFileFilter() );

        if ( excelFile != null ) {
            writeExcelReport( excelFile, isNumeric );
            try {
                loadExcelFileAfterCreation( excelFile );
            } catch (IOException iOException) {
            }
        }
    }


    private static void writeExcelReport ( File excelFile, boolean isNumeric ) {
        WorkbookSettings ws = new WorkbookSettings();
        ws.setSuppressWarnings( ExcelReportConstants.SUPPRESS_WARNINGS );

        int firstDataRow = Integer.parseInt( reportFractions[0][0] );

        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook( excelFile, ws );
            WritableSheet sheet = workbook.createSheet( ExcelReportConstants.SHEET_NAME,
                    ExcelReportConstants.SHEET_INDEX );
            setExcelPrintPageFormat( sheet );

            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setBorder( Border.BOTTOM, BorderLineStyle.THIN );
            cellFormat.setWrap( false );

            // category titles with bottom border
            String catName = "Fraction";
            String savedCatName = catName;
            // starts after Fraction column = 2
            for (int c = 3; c < reportFractions[0].length; c ++) {
                catName = reportFractions[0][c].trim();
                if (  ! catName.equalsIgnoreCase( savedCatName ) ) {
                    sheet.addCell( new Label( c - 2, 1, catName, cellFormat ) );
                    sheet.mergeCells( c - 2, 1, c - 1, 1 );
                    savedCatName = catName;
                } else {
                    sheet.addCell( new Label( c - 2, 1, "", cellFormat ) );
                }
            }

            // column titles
            for (int c = 2; c < reportFractions[0].length; c ++) {
                sheet.addCell( new Label( c - 2, 2, reportFractions[1][c] ) );
                sheet.addCell( new Label( c - 2, 3, reportFractions[2][c] ) );
                // write third header row
                sheet.addCell( new Label( c - 2, 4, reportFractions[3][c] + " " + /*footnote*/reportFractions[5][c], cellFormat ) );

                // write sample name row = 1 and underlined
                if ( c == 2 ) {
                    sheet.addCell( new Label( c - 2, 0, sampleName, cellFormat ) );
                } else {
                    sheet.addCell( new Label( c - 2, 0, "", cellFormat ) );
                }
            }

            String saveAliquotName = "";

            // reportRowAdvance accounts for 6 rows of header info
            int reportRowAdvance = 6 - firstDataRow;
            // footnotes start with no data and advance as rows are added
            int footNoteStartRow = firstDataRow;

            for (int row = firstDataRow - 1; row < reportFractions.length; row ++) {

                // check whether fraction is included
                if ( reportFractions[row][0].equalsIgnoreCase( "TRUE" ) ) {
                    footNoteStartRow ++;
                    // for each aliquot
                    if (  ! reportFractions[row][1].equalsIgnoreCase( saveAliquotName ) ) {
                        saveAliquotName = reportFractions[row][1];
                        footNoteStartRow ++;
                        WritableCell writableCell = new Label( 0, row + reportRowAdvance ++, reportFractions[row][1] );
                        sheet.addCell( writableCell );
                    }

                    // fraction data
                    WritableFont cellFont =
                            new WritableFont( WritableFont.createFont(isNumeric ? EXCEL_FONT_FOR_NUMERIC_NUMBERS  : EXCEL_FONT_FOR_STRING_NUMBERS ),
                            ExcelReportConstants.EXCEL_FONT_SIZE );
                    WritableCellFormat cellFormat2 = new WritableCellFormat( cellFont );
                    cellFormat2.setAlignment( Alignment.RIGHT );

                    WritableCell writableCell = null;
                    for (int c = 2; c < reportFractions[0].length; c ++) {

                        // decide on number or string contents if c > 2 = data column (2 = fractionID)
                        // nov 2009 also detect "-" for not appropriate data item
                        if ( isNumeric &&//
                                // (c > 2) && //
                                ( ! reportFractions[row][c].trim().equalsIgnoreCase( "-" )) &&//
                                ( ! reportFractions[3][c].trim().equalsIgnoreCase( "Fraction" )) ) {
                            try {
                                writableCell = //
                                        new jxl.write.Number(c - 2, row + reportRowAdvance, Double.valueOf(reportFractions[row][c]), cellFormat2);
                            } catch (NumberFormatException numberFormatException) {
                                // case of non-number where there should be one
                                System.out.println("CELL = " + reportFractions[row][c]);
                                writableCell = //
                                        new jxl.write.Number(c - 2, row + reportRowAdvance, 0.0);
                            }
                        } else {
                            writableCell = //
                                    new Label( c - 2, row + reportRowAdvance, reportFractions[row][c], cellFormat2 );
                        }

                        sheet.addCell( writableCell );

                    }

                } else {
                    reportRowAdvance --;
                }
            }

            // set column widths based on isNumeric and/or first row of data
            // detect column width and then iterate through cells to set as this API has no useful range object
            for (int col = 2; col < reportFractions[0].length; col ++) {
                int colWidth = //
                        Math.max( reportFractions[2][col].trim().length(),
                        reportFractions[3][col].trim().length() );
                if ( isNumeric ) {
                    colWidth = //
                            Math.max( ExcelReportConstants.STANDARD_COLUMN_WIDTH,
                            colWidth );
                } else {
                    colWidth = //
                            Math.max( reportFractions[firstDataRow][col].trim().length(),
                            colWidth );
                }

                sheet.setColumnView( col - 2, colWidth + 3 );
            }


            // write out footnotes
            for (int i = 0; i < reportFractions[6].length; i ++) {
                if (  ! reportFractions[6][i].equals( "" ) ) {
                    // strip out footnote letter
                    String[] footNote = reportFractions[6][i].split( "&" );
                    String footNoteLine = //
                            " " //
                            + footNote[0] //
                            + "  " //
                            + footNote[1];
                    sheet.addCell( new Label( 0, footNoteStartRow ++, footNoteLine ) );
                }
            }
            workbook.write();



            workbook.close();


        } catch (IOException iOException) {
        } catch (WriteException writeException) {
        }

    }

    private static void setExcelPrintPageFormat ( WritableSheet sheet ) {
        SheetSettings settings = sheet.getSettings();
        settings.setPaperSize( ExcelReportConstants.PAPER_SIZE );
        settings.setOrientation( ExcelReportConstants.PAGE_ORIENTATION );
        settings.setScaleFactor( ExcelReportConstants.SCALE_FACTOR );
        settings.setLeftMargin( ExcelReportConstants.LEFT_MARGIN );
        settings.setRightMargin( ExcelReportConstants.RIGHT_MARGIN );
        settings.setTopMargin( ExcelReportConstants.TOP_MARGIN );
        settings.setBottomMargin( ExcelReportConstants.BOTTOM_MARGIN );
    }

    private static void loadExcelFileAfterCreation ( File file ) throws IOException {
        JOptionPane jopt = new JOptionPane();
        int userChoice = jopt.showConfirmDialog(//
                null,//
                "Excel file successfully generated."//
                + " Do you want to open the file?",//
                "Open an Excel file?",//
                JOptionPane.YES_NO_OPTION );
        if ( userChoice == JOptionPane.YES_OPTION ) {
            BrowserControl.displayURL( file.getCanonicalPath() );
        }
    }
}
