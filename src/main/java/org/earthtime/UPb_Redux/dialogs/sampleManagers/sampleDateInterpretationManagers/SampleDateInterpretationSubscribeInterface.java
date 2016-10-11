/*
 * SampleDateInterpretationSubscribeInterface.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers;

import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring
 */
public interface SampleDateInterpretationSubscribeInterface {

    /**
     *
     */
    public void publishClosingOfSampleDateInterpretation();

    /**
     *
     */
    public void updateReportTable();

    /**
     *
     */
    public void refreshReportTableData();

    /**
     * opens a modal editor for the <code>Fraction</code> indicated by argument
     * <code>fraction</code> and opened to the editing tab indicated by argument
     * <code>selectedTab</code>. <code>selectedTab</code> is valid only if it
     * contains a number between zero and seven inclusive.
     *
     * @pre the <code>Fraction</code> corresponding to <code>fraction</code>
     * exists in this <code>Sample</code> and <code>selectedTab</code> is a
     * valid tab number
     * @post an editor for the specified <code>Fraction</code> is opened to the
     * specified tab
     * @param fraction the <code>Fraction</code> to be edited
     * @param selectedTab the tab to open the editor to
     */
    void editFraction(ETFractionInterface fraction, int selectedTab);

}
