/*
 * ReportPainterI.java
 *
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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

import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public interface ReportPainterI {

    /**
     * 
     * @param fractionIdToFocus the value of fractionIdToFocus
     */
    public void loadAndShowReportTableData (String fractionIdToFocus);
    
    /**
     * 
     * @param performReduction
     * @param inLiveMode the value of inLiveMode
     */
    public void rebuildFractionDisplays ( boolean performReduction, boolean inLiveMode);
    
    /**
     *
     * @param theSample
     */
    public void setTheSample ( SampleInterface theSample );
    
    /**
     *
     * @param performReduction
     * @throws BadLabDataException
     */
    public void setUpTheSample ( boolean performReduction )
            throws BadLabDataException;

}
