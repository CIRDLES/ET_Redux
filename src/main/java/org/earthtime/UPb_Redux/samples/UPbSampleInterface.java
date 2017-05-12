/* Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.samples;

import java.io.File;
import java.io.FileNotFoundException;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.exceptions.ETException;

/**
 * Any class that implements <code>Sample</code> should contain all of the
 * scientific data related to that geological sample as well as the additional
 * methods to manipulate this data.
 *
 * @author Stan Gasque
 */
public interface UPbSampleInterface {

    /**
     * reads <code>Fractions</code> from the file specified by argument
     * <code>location</code> and adds them to the <code>Aliquot</code> specified
     * by argument <code>aliquotNumber</code> in this <code>Sample</code>.
     *
     * @pre argument <code>location</code> specifies an XML file with valid
     * <code>UPbFractions</code> and argument <code>aliquotNumber</code>
     * specifies an <code>Aliquot</code> that exists in this <code>Sample</code>
     * @post all <code>Fractions</code> found in the specified file are added to
     * the specified <code>Aliquot</code> in this <code>Sample</code>
     * @param location file to read data from
     * @param aliquotNumber number of <code>Aliquot</code> to add
     * <code>Fractions</code> from the file to
     * @return  <code>String</code> - path of the file that data was read from
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     * @throws java.io.FileNotFoundException FileNotFoundException
     */
    String importUPbFractionFolderForManualUpdate(File location, int aliquotNumber) throws ETException, BadLabDataException, FileNotFoundException;

    /**
     * reads in data from the XML file specified by argument
     * <code>fractionFile</code> and adds any <code>Fractions</code> found in
     * the file to this <code>Sample</code> under the <code>Aliquot</code>
     * specified by argument <code>aliquotNumber</code>.
     *
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * @pre     <code>fractionFile</code> is an XML file containing valid
     * <code>Fractions</code> and <code>aliquotNumber</code> specifies an
     * existing <code>Aliquot</code> in this <code>Sample</code>
     * @post all <code>Fractions</code> found in the file are added to the
     * <code>Aliquot</code> specified by <code>aliquotNumber</code> in this
     * <code>Sample</code>
     * @param fractionFile the file to read data from
     * @param aliquotNumber the number of the <code>Aliquot</code> that the
     * <code>Fractions</code> being read from the file belong to
     * @param validateSampleName
     * @param doValidate
     * @return
     */
    public String processXMLFractionFile(File fractionFile, int aliquotNumber, Boolean validateSampleName, boolean doValidate) throws ETException, BadLabDataException;

    /**
     * gets the <code>defaultFractionCounter</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>defaultFractionCounter</code> of this
     * <code>Sample</code>
     * @return  <code>int</code> - <code>defaultFractionCounter</code> of this
     * <code>Sample</code>
     */
    int getDefaultFractionCounter();

    /**
     * gets the <code>defaultFractionName</code> of this <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>defaultFractionName</code> of this
     * <code>Sample</code>
     * @return  <code>String</code> - <code>defaultFractionName</code> of this
     * <code>Sample</code>
     */
    String getDefaultFractionName();

    /**
     * gets the <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>fractionDataOverriddenOnImport</code> field of
     * this <code>Sample</code>
     * @return  <code>boolean</code> -
     * <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code>
     */
    boolean isFractionDataOverriddenOnImport();

    /**
     * sets the <code>defaultFractionCounter</code> of this <code>Sample</code>
     * to the argument <code>defaultFractionCounter</code>
     *
     * @pre argument <code>defaultFractionCounters</code> is a valid
     * <code>defaultFractionCounter</code>
     * @post this <code>Sample</code>'s <code>defaultFractionCounter</code> is
     * set to argument <code>defaultFractionCounter</code>
     * @param defaultFractionCounter value to which
     * <code>defaultFractionCounter</code> of this <code>Sample</code> will be
     * set
     */
    void setDefaultFractionCounter(int defaultFractionCounter);

    /**
     * sets the <code>defaultFractionName</code> of this <code>Sample</code> to
     * the argument <code>defaultFractionName</code>
     *
     * @pre argument <code>defaultFractionName</code> is a valid
     * <code>defaultFractionName</code>
     * @post this <code>Sample</code>'s <code>defaultFractionName</code> is set
     * to argument <code>defaultFractionName</code>
     * @param defaultFractionName value to which
     * <code>defaultFractionName</code> of this <code>Sample</code> will be set
     */
    void setDefaultFractionName(String defaultFractionName);

    /**
     *
     * @param aliquotNumber
     * @throws BadLabDataException
     */
    public abstract void addDefaultUPbFractionToAliquot(int aliquotNumber)
            throws BadLabDataException;

    /**
     *
     * @param aliquotNumber
     * @throws BadLabDataException
     */
    public abstract void addDefaultUPbLegacyFractionToAliquot(int aliquotNumber)
            throws BadLabDataException;
}
