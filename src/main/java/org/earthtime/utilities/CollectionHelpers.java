/*
 * CollectionHelpers.java
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

package org.earthtime.utilities;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author James F. Bowring
 */
public final class CollectionHelpers {

    /**
     * 
     * @param sourceListing
     * @return
     */
    public static Vector<String> vectorSortedUniqueMembers(String[][] sourceListing){
        SortedSet<String> treeSet = new TreeSet<String>();

        for (int i = 0; i < sourceListing.length; i ++){
            for (int j = 0; j < sourceListing[i].length; j ++){
                treeSet.add( sourceListing[i][j]);
            }
        }

        Vector<String> retval = new Vector<String>();
        for (String s : treeSet){
            retval.add( s );
        }

        return retval;
    }

}
