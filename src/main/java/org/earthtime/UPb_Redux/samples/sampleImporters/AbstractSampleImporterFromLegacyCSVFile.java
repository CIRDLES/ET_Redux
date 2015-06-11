/*
 * AbstractSampleImporterFromLegacyCSVFile.java
 *
 * Created on 5 September 2009
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
package org.earthtime.UPb_Redux.samples.sampleImporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.filters.LegacyCSVFileFilter;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.utilities.FileHelper;

/**
 * Converts contents of a CSV file to an aliquot of fractions. The CSV file must
 * have the fields in a specific order as specified by the template found at
 * ****************
 *
 * @author James F. Bowring
 */
public abstract class AbstractSampleImporterFromLegacyCSVFile {

    // instance attributes
    private File mruFolder;
    /**
     *
     */
    protected String aliquotName = "aliquot";

    /**
     *
     */
    public AbstractSampleImporterFromLegacyCSVFile () {
    }

    /**
     *
     * @return @throws FileNotFoundException
     * @throws BadImportedCSVLegacyFileException
     */
    public Vector<ETFractionInterface> readInFractions ()
            throws FileNotFoundException, BadImportedCSVLegacyFileException {

        Vector<ETFractionInterface> retval = new Vector<>();

        File csvFile = openCSVFile( mruFolder );
        mruFolder = csvFile.getParentFile();

        retval = extractFractionsFromFile( csvFile );

        return retval;

    }

    private File openCSVFile ( File location )
            throws FileNotFoundException {
        String dialogTitle = "Select a LEGACY CSV file to OPEN: *.csv";
        final String fileExtension = ".csv";
        FileFilter nonMacFileFilter = new LegacyCSVFileFilter();

        File returnFile =
                FileHelper.AllPlatformGetFile( dialogTitle, location, fileExtension, nonMacFileFilter, false, new JFrame() )[0];

        if ( returnFile != null ) {
            return returnFile;
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws BadImportedCSVLegacyFileException
     */
    protected abstract Vector<ETFractionInterface> extractFractionsFromFile ( File file )
            throws FileNotFoundException, BadImportedCSVLegacyFileException;

    /**
     *
     * @param cellContents
     * @return
     */
    protected BigDecimal readCSVCell ( String cellContents ) {
        BigDecimal retVal = BigDecimal.ZERO;

        try {
            retVal = new BigDecimal( cellContents, ReduxConstants.mathContext15 );
        } catch (Exception e) {
        }

        return retVal;
    }

    /**
     *
     * @param aLine
     * @return
     */
    protected Vector<String> processLegacyCSVLine ( String aLine ) {
        Vector<String> myLine = new Vector<>();

        //use a second Scanner to parse the content of each line

        // capture leading comma : '0' is flag to ignore line
        if (( aLine.startsWith( "," ) || aLine.trim().length() == 0) ) {
            myLine.add( "0" );
        }
        Scanner s = new Scanner( aLine );
        s.useDelimiter( "," );

        while (s.hasNext()) {
            myLine.add( s.next().trim() );
        }

        s.close();

        // add dummy fields to handle possible missing last values

        int size = myLine.size();
        if ( (size > 0) &&  ! myLine.get( 0 ).equalsIgnoreCase( "0" ) ) {
            for (int i = size; i < 32; i ++) {
                myLine.add( "0" );
            }
        }
        return myLine;
    }

    
    /**
     * @return the mruFolder
     */
    public // instance attributes
            File getMruFolder () {
        return mruFolder;
    }

    /**
     * @param mruFolder the mruFolder to set
     */
    public void setMruFolder ( File mruFolder ) {
        this.mruFolder = mruFolder;
    }

    /**
     *
     */
    public static void writeAndOpenCSVFileOfLegacyDataSampleFieldNames () {
    }

    /**
     * @return the aliquotName
     */
    public String getAliquotName () {
        return aliquotName;
    }
}
