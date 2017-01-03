
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
package org.earthtime.UPb_Redux.pbBlanks;

import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 * Any class that implements <code>PbBlank</code> should 
 * 
 * @author Stan Gasque
 */
public interface PbBlankI {

    /**
     * returns a deep copy of this <code>PbBlank</code>.
     *
     * @pre     this <code>PbBlank</code> exists
     * @post    returns a new <code>PbBlank</code> with data identical to that
     * of this <code>PbBlank</code>
     * @return  <code>PbBlank</code> - a new <code>PbBlank</code> whose fields
     * match those of this <code>PbBlank</code>
     */
    PbBlank Copy();

    /**
     * compares this <code>PbBlank</code> with argument <code>pbBlank</code>
     * lexicographically by <code>name</code>.
     *
     * @pre     argument <code>pbBlank</code> is a valid <code>PbBlank</code>
     * @post    returns <code>true</code> if this <code>PbBlank</code> is
     * argument <code>pbBlank</code> or if their <code>name</code>
     * fields are lexicographically equivalent, else <code>false</code>
     * @param   pbBlank     <code>PbBlank</code> to compare this<code>PbBlank</code> against
     * @return  <code>boolean</code> - <code>true</code> if this <code>PbBlank</code>
     * is argument <code>pbBlank</code> or if their <code>name</code>
     * fields are lexicographically equivalent, else <code>false</code>
     */
    @Override
    boolean equals(Object pbBlank);

    /**
     * gets the <code>name</code> of this <code>PbBlank</code>.
     *
     * @pre     this <code>PbBlank</code> exists
     * @post    returns the <code>name</code> of this <code>PbBlank</code>;
     * returns <code>null</code> if the <code>name</code> was never
     * initialized.
     * @return  <code>String</code> - <code>name</code> of this <code>PbBlank</code>
     */
    String getName();

    /**
     * finds and returns the <code>ValueModel</code> from <code>ratios</code>
     * whose <code>name</code> field matches the argument <code>ratioName</code>.
     *
     * @pre     argument <code>ratioName</code> is a valid <code>String</code>
     * @post    returns the <code>ValueModel</code> from <code>ratios</code>
     * whose name is <code>ratioName</code> or <code>null</code> if
     * none is found
     * @param   ratioName   name of the <code>ValueModel</code> to search for
     * @return  <code>ValueModel</code> - the member of <code>ratios</code>
     * whose <code>name</code> field is equivalent to argument
     * <code>ratioName</code>; <code>null</code> if no matching
     * <code>ValueModel</code> is found
     */
    ValueModel getRatioByName(String ratioName);

    /**
     * gets the <code>ratios</code> of this <code>PbBlank</code>.
     *
     * @pre     this <code>PbBlank</code> exists
     * @post    returns the <code>ratios</code> of this <code>PbBlank</code>
     * @return  <code>ValueModel[]</code> - the <code>ratios</code> of this
     * <code>PbBlank</code>
     */
    ValueModel[] getRatios();

    /**
     * gets the <code>name</code> of this <code>PbBlank</code>.
     *
     * @pre     this <code>PbBlank</code> exists
     * @post    returns the <code>name</code> of this <code>PbBlank</code> via
     * {@link #getName() getName}
     * @return  <code>String</code> - <code>name</code> of this <code>PbBlank</code>
     */
    String getReduxLabDataElementName();

    /**
     * finds and returns the <code>ValueModel</code> from <code>rhoCorrelations</code>
     * whose <code>name</code> field matches the argument <code>rhoCorr</code>.
     *
     * @pre     argument <code>rhoCorr</code> is a valid <code>String</code>
     * @post    returns the <code>ValueModel</code> from <code>rhoCorrelations</code>
     * whose name is <code>rhoCorr</code> or <code>null</code> if
     * none is found
     * @param   rhoCorr     name of the <code>ValueModel</code> to search for
     * @return  <code>ValueModel</code> - the member of <code>rhoCorrelations</code>
     * whose <code>name</code> field is equivalent to argument
     * <code>rhoCorr</code>; <code>null</code> if no matching
     * <code>ValueModel</code> is found
     */
    ValueModel getRhoCorrelationByName(String rhoCorr);

    /**
     * gets the <code>rhoCorrelations</code> of this <code>PbBlank</code>.
     *
     * @pre     this <code>PbBlank</code> exists
     * @post    returns the <code>rhoCorrelations</code> of this <code>PbBlank</code>
     * @return  <code>ValueModel[]</code> - this <code>PbBlank</code>'s <code>rhoCorrelations</code>
     */
    ValueModel[] getRhoCorrelations();

    /**
     * returns 0 as the hashcode for this <code>PbBlank</code>. Implemented to meet
     * equivalency requirements as documented by <code>java.lang.Object</code>
     *
     * @pre     this <code>PbBlank</code> exists
     * @post    hashcode of 0 is returned for this <code>PbBlank</code>
     * @return  <code>int</code> - 0
     */
    @Override
    int hashCode();

    /**
     * sets the <code>name</code> of this <code>PbBlank</code>.
     *
     * @pre     argument <code>name</code> is a valid <code>String</code>
     * @post    <code>name</code> of this <code>PbBlank</code> is set to
     * argument <code>name</code>
     * @param   name    value to set this <code>PbBlank</code>'s <code>name</code> to
     */
    void setName(String name);

    /**
     * sets the <code>ratios</code> of this <code>PbBlank</code>.
     *
     * @pre     argument <code>ratios</code> is a valid collection of <code>ValueModel</code>
     * @post    this <code>PbBlanks</code>'s <code>ratio</code> field is set to
     * argument <code>ratios</code>
     * @param   ratios  value to set this <code>PbBlank</code>'s <code>ratios</code> to
     */
    void setRatios(ValueModel[] ratios);

    /**
     * sets the <code>rhoCorrelations</code> of this <code>PbBlank</code> to
     * argument <code>rhoCorrelations</code>.
     *
     * @pre     argument <code>rhoCorrelations</code> is a valid collection of
     * <code>ValueModel</code>
     * @post    this <code>PbBlank</code>'s <code>rhoCorrelations</code> is set
     * to argument <code>rhoCorrelations</code>
     * @param   rhoCorrelations     value to set this <code>PbBlanks</code>'s
     * <code>rhoCorrelations</code> to
     */
    void setRhoCorrelations(ValueModel[] rhoCorrelations);
}
