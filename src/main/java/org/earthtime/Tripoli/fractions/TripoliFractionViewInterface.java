/*
 * TripoliFractionViewInterface.java
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
package org.earthtime.Tripoli.fractions;

import java.util.ArrayList;

/**
 *
 * @author James F. Bowring
 */
public interface TripoliFractionViewInterface {

    /**
     * 
     * @param colorMeExcluded 
     */
    public void setColorMeExcluded ( boolean colorMeExcluded );

    /**
     * 
     * @return
     */
    public boolean isColorMeExcluded ();

    /**
     * 
     * @return
     */
    public int getShowVerticalLineAtThisIndex ();

    /**
     * 
     * @param showVerticalLineAtThisIndex 
     */
    public void setShowVerticalLineAtThisIndex ( int showVerticalLineAtThisIndex );

    /**
     * 
     * @return
     */
    public int getShowSecondVerticalLineAtThisIndex ();

    /**
     * 
     * @param showSecondVerticalLineAtThisIndex
     */
    public void setShowSecondVerticalLineAtThisIndex ( int showSecondVerticalLineAtThisIndex );

    /**
     * 
     * @return
     */
    public double getSelBoxFirstY ();

    /**
     * 
     * @param selBoxFirstY
     */
    public void setSelBoxFirstY ( double selBoxFirstY );

    /**
     * 
     * @return
     */
    public double getSelBoxSecondY ();

    /**
     * 
     * @param selBoxSecondY
     */
    public void setSelBoxSecondY ( double selBoxSecondY );

    /**
     * 
     * @return
     */
    public ArrayList<Integer> getSelectedForToggleIndexes ();

    /**
     * 
     * @param selectedForToggleIndexes
     */
    public void setSelectedForToggleIndexes ( ArrayList<Integer> selectedForToggleIndexes );
}
