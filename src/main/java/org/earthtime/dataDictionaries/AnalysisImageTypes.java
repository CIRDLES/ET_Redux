/*
 * AnalysisImageTypes.java
 *
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.dataDictionaries;

import java.io.Serializable;

/**
 *
 * @author James F. Bowring
 */
public final class AnalysisImageTypes implements Serializable {

    // Class variables
    private static final long serialVersionUID = -2496017337387907201L;
    String name;

    private AnalysisImageTypes ( String name ) {
        this.name = name;
    }
    /**
     * 
     */
    public static AnalysisImageTypes PHOTO = new AnalysisImageTypes( "photo" );
    /**
     * 
     */
    public static AnalysisImageTypes CONCORDIA = new AnalysisImageTypes( "concordia" );
    /**
     * 
     */
    public static AnalysisImageTypes WEIGHTED_MEAN = new AnalysisImageTypes( "weighted_mean" );
    /**
     * 
     */
    public static AnalysisImageTypes PROBABILITY_DENSITY = new AnalysisImageTypes( "probability_density" );
    /**
     * 
     */
    public static AnalysisImageTypes HISTOGRAM = new AnalysisImageTypes( "histogram" );
    /**
     * 
     */
    public static AnalysisImageTypes REPORT = new AnalysisImageTypes( "report" );
    /**
     * 
     */
    public static AnalysisImageTypes REPORT_CSV = new AnalysisImageTypes( "report_csv" );

    /**
     * 
     * @return
     */
    public String getName () {
        return name;
    }

//    private void readObject (
//            ObjectInputStream stream )
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName( AnalysisImageTypes.class.getCanonicalName() ) );
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println( "Customized De-serialization of AnalysisImageTypes " + theSUID );
//    }
}
