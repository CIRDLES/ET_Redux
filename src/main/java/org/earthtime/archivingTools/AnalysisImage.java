/*
 * AnalysisImage.java
 *
 * Created Nov 10, 2010 Copied here 2015
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
package org.earthtime.archivingTools;

import java.io.Serializable;
import org.earthtime.dataDictionaries.AnalysisImageTypes;

/**
 *
 * @author James F. Bowring
 */
public class AnalysisImage implements AnalysisImageInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = -6082771900853607193L;
    private AnalysisImageTypes imageType;
    private String imageURL;

    /**
     * 
     * @param imageType
     * @param imageURL
     */
    public AnalysisImage ( AnalysisImageTypes imageType, String imageURL ) {
        this.imageType = imageType;
        this.imageURL = imageURL;
    }

    /**
     * @return the imageType
     */
    @Override
    public AnalysisImageTypes getImageType () {
        return imageType;
    }

    /**
     * @param imageType the imageType to set
     */
    @Override
    public void setImageType ( AnalysisImageTypes imageType ) {
        this.imageType = imageType;
    }

    /**
     * @return the imageURL
     */
    @Override
    public String getImageURL () {
        return imageURL;
    }

    /**
     * @param imageURL the imageURL to set
     */
    @Override
    public void setImageURL ( String imageURL ) {
        this.imageURL = imageURL;
        System.out.println("Image URL = " + imageURL);
    }

}
