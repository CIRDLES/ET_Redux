/*
 * CsvResultsTable.java
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.filters.CSVFileFilter;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class CsvResultsTable {

    private static String sampleName;
    private static String[][] reportFractions;

    /**
     * 
     * @param mySampleName
     * @param myReportFractions
     */
    public static void produceCSVResultsTable ( String mySampleName, String[][] myReportFractions ) {
        sampleName = mySampleName;
        reportFractions = myReportFractions;

        File excelFile = FileHelper.AllPlatformSaveAs(
                new Frame(),
                "Save Report Table as '.csv' File: *.csv",
                null,
                ".csv",
                sampleName + "_ReportTable" + ".csv",
                new CSVFileFilter() );

        if ( excelFile != null ) {
            writeCSVReport( excelFile );
            try {
                loadCSVFileAfterCreation( excelFile );
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * 
     * @param csvFile
     */
    public static void writeCSVReport ( File csvFile ) {

        int firstDataRow = Integer.parseInt( reportFractions[0][0] );

        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter( new FileWriter( csvFile ) );

            // Sample
            outputWriter.write( sampleName + "\n" );

            // category titles 
            String catName = "Fraction";
            String savedCatName = catName;
            // starts after Fraction column = 2
            for (int c = 2; c < reportFractions[0].length; c ++) {
                catName = reportFractions[0][c].trim();
                if (  ! catName.equalsIgnoreCase( savedCatName ) ) {
                    outputWriter.write( catName + "," );
                    savedCatName = catName;
                } else {
                    outputWriter.write( "," );
                }
            }

            outputWriter.write( "\n" );
            // column titles
            for (int row = 1; row < 4; row ++) {
                for (int c = 2; c < reportFractions[0].length; c ++) {

                    // footnote
                    if ( row == 3 ) {
                        outputWriter.write( replaceUnicodes(reportFractions[row][c]) + " " + reportFractions[5][c] + "," );
                    } else {
                        outputWriter.write( replaceUnicodes(reportFractions[row][c]) + "," );
                    }
                }
                outputWriter.write( "\n" );

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
                        outputWriter.write( reportFractions[row][1] + "\n" );
                        reportRowAdvance ++;
                    }

                    // fraction data
                    for (int c = 2; c < reportFractions[0].length; c ++) {
                        outputWriter.write( reportFractions[row][c] + "," );
                    }

                    outputWriter.write( "\n" );

                } else {
                    reportRowAdvance --;
                }

            }


            // write out footnotes
            outputWriter.write( "\n" );
            outputWriter.write( "\n" );

            for (int i = 0; i < reportFractions[6].length; i ++) {
                if (  ! reportFractions[6][i].equals( "" ) ) {
                    // strip out footnote letter
                    String[] footNote = reportFractions[6][i].split( "&" );
                    String footNoteLine = //
                            " " //
                            + footNote[0] //
                            + "  " //
                            + footNote[1] + "\n";
                    outputWriter.write( replaceUnicodes(footNoteLine) );
                    footNoteStartRow ++;
                }
            }

            outputWriter.write( "\n" );
            outputWriter.write( "\n" );


            outputWriter.flush();
            outputWriter.close();

        } catch (IOException iOException) {
        }

//        try {
//            BrowserControl.displayURL( csvFile.getCanonicalPath() );
//        } catch (IOException ex) {
//        }
    }
    
    private static String replaceUnicodes(String text){
        String retVal = text;
        
        retVal = retVal.replace( "\u00B1", "+/-" );
        retVal = retVal.replace( "\u03C3", "sigma" );
        retVal = retVal.replace( "\u03c1", "rho" );
        retVal = retVal.replace( "\u03BB", "lambda" );
        retVal = retVal.replace( ",", " and " );
        
        return retVal;
    }

    private static void loadCSVFileAfterCreation ( File csvFile ) throws IOException {
        JOptionPane jopt = new JOptionPane();
        int userChoice = jopt.showConfirmDialog(//
                null,//
                "'.csv' file successfully generated."//
                + " Do you want to open the file?",//
                "Open a '.csv' file?",//
                JOptionPane.YES_NO_OPTION );
        if ( userChoice == JOptionPane.YES_OPTION ) {
            BrowserControl.displayURL( csvFile.getCanonicalPath() );
        }
    }

    /**
     * @param aSampleName the sampleName to set
     */
    public static void setSampleName ( String aSampleName ) {
        sampleName = aSampleName;
    }

    /**
     * @param aReportFractions the reportFractions to set
     */
    public static void setReportFractions ( String[][] aReportFractions ) {
        reportFractions = aReportFractions;
    }
}
