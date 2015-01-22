/*
 * MaskingSingleton.java
 *
 * Created Jul 29, 2011
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
package org.earthtime.Tripoli.dataModels;

import java.io.Serializable;

/**
 *
 * @author James F. Bowring
 */
public class MaskingSingleton implements Serializable {

    // Class variables
    private static final long serialVersionUID = -2086025499717049075L;
    private static MaskingSingleton instance = null;
    // instance variables 
    private boolean[] maskingArray;
    private int leftShadeCount;
    private int rightShadeCount;

    private MaskingSingleton() {

        leftShadeCount = -1;
        rightShadeCount = -1;
    }

    /**
     *
     * @return
     */
    public static MaskingSingleton getInstance() {
        if (instance == null) {
            instance = new MaskingSingleton();
        }
        return instance;
    }

    /**
     *
     * @param savedInstance
     */
    public static void setInstance(MaskingSingleton savedInstance) {
        instance = savedInstance;
    }

    /**
     * @return the maskingArray
     */
    public boolean[] getMaskingArray() {
        return maskingArray;
    }

    /**
     *
     * @param activeDataMap
     * @return
     */
    public boolean[] applyMask(boolean[] activeDataMap) {
        // from left
        for (int i = 0; i < activeDataMap.length; i++) {
            activeDataMap[i] = true;
        }

        for (int i = 0; i < leftShadeCount; i++) {
            activeDataMap[i] = false;
        }

        for (int i = activeDataMap.length - rightShadeCount - 1; i < activeDataMap.length; i++) {
            activeDataMap[i] = false;
        }

        return activeDataMap;
    }

    /**
     *
     * @return
     */
    public int getCountOfActiveData() {
        int countOfActiveData = 0;
        for (int i = 0; i < maskingArray.length; i++) {
            if (maskingArray[i]) {
                countOfActiveData++;
            }
        }

        return countOfActiveData;
    }

    /**
     * @param aMaskingArray
     * @param maskingArray the maskingArray to set
     */
    public void setMaskingArray(boolean[] aMaskingArray) {
        this.maskingArray = aMaskingArray;
        
        for (int i = 0; i < maskingArray.length; i++) {
            maskingArray[i] = true;
        }
        
        for (int i = 0; i < leftShadeCount; i++) {
            maskingArray[i] = false;
        }

        for (int i = maskingArray.length - rightShadeCount - 1; i < maskingArray.length; i++) {
            maskingArray[i] = false;
        }
    }

    /**
     * @return the leftShadeCount
     */
    public int getLeftShadeCount() {
        return leftShadeCount;
    }

    /**
     * @param myLeftShadeCount
     * @param leftShadeCount the leftShadeCount to set
     */
    public void setLeftShadeCount(int myLeftShadeCount) {
        this.leftShadeCount = myLeftShadeCount;
    }

    /**
     * @return the rightShadeCount
     */
    public int getRightShadeCount() {
        return rightShadeCount;
    }

    /**
     * @param rightShadeCount the rightShadeCount to set
     */
    public void setRightShadeCount(int rightShadeCount) {
        this.rightShadeCount = rightShadeCount;
    }
}
