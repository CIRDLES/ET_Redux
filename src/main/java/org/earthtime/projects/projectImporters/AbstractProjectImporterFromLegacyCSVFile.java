/*
 * AbstractProjectImporterFromLegacyCSVFile.java
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
package org.earthtime.projects.projectImporters;

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
import org.earthtime.projects.ProjectI;
import org.earthtime.utilities.FileHelper;

/**
 * Converts contents of a CSV file to an aliquot of fractions. The CSV file must
 * have the fields in a specific order as specified by the template found at
 * ****************
 *
 * @author James F. Bowring
 */
public abstract class AbstractProjectImporterFromLegacyCSVFile {

    // instance attributes
    private File mruFolder;
 
    /**
     *
     */
    public AbstractProjectImporterFromLegacyCSVFile () {
    }

    /**
     *
     * @param project
     * @return @throws FileNotFoundException
     * @throws BadImportedCSVLegacyFileException
     */
    public ProjectI readInProjectSamples (ProjectI project)
            throws FileNotFoundException, BadImportedCSVLegacyFileException {

        File csvFile = openCSVFile( mruFolder );
        mruFolder = csvFile.getParentFile();

        extractProjectFromCSVFile(project, csvFile );

        return project;

    }
    
    /**
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    protected abstract ProjectI extractProjectFromCSVFile (ProjectI project, File file )
            throws FileNotFoundException;

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
     * @param cellContents
     * @return
     */
    protected BigDecimal readCSVCell ( String cellContents ) {
        BigDecimal retVal;

        try {
            retVal = new BigDecimal( cellContents, ReduxConstants.mathContext15 );
        } catch (Exception e) {
            retVal = BigDecimal.ZERO;
        }

        return retVal;
    }

    /**
     *
     * @param aLine
     * @return
     */
    protected Vector<String> processLegacyCSVLine ( String aLine ) {
        Vector<String> myLine = new Vector<String>();

        //use a second Scanner to parse the content of each line

        // remove all quotes
        aLine = aLine.replaceAll( "\"", "" );
        
        // capture empty lines : leading comma : '0' is flag to ignore line
        if ((aLine == null) || (aLine.length() == 0) ||  ( aLine.startsWith( "," ) )) {
            myLine.add( "0" );
        }
        Scanner s = new Scanner( aLine );
        s.useDelimiter( "," );

        while (s.hasNext()) {
            myLine.add( s.next().trim() );
        }

        s.close();

        // add dummy fields to handle possible missing last values
        if ( !myLine.get( 0 ).equalsIgnoreCase( "0" ) ) {
            int size = myLine.size();
            for (int i = size; i < 32; i ++) {
                myLine.add( "0" );
            }
        }

        return myLine;
    }

    /**
     *
     * @param lineContents
     * @return
     */
    public static boolean lineHasOnlyFirstElement ( Vector<String> lineContents ) {
        boolean retVal = false;
        if ( (lineContents.get( 0 ).length() == 0) || (lineContents.get( 0 ).equalsIgnoreCase( "0" )) ) {
            retVal = false;
        } else {
            retVal = true;
            int cellCountToCheck = Math.min(6, lineContents.size());
            for (int i = 1; i < cellCountToCheck; i ++) {
                retVal = retVal && (lineContents.get( i ).length() == 0);
            }
        }


        return retVal;
    }

    /**
     * @return the mruFolder
     */
    public File getMruFolder () {
        return mruFolder;
    }

    /**
     * @param mruFolder the mruFolder to set
     */
    public void setMruFolder ( File mruFolder ) {
        this.mruFolder = mruFolder;
    }

}
