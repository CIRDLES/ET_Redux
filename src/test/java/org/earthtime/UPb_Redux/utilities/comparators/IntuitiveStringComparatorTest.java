/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.UPb_Redux.utilities.comparators;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author parizotclement
 */
public class IntuitiveStringComparatorTest {
    
    /**
     * Integration Test of class IntuitiveStringComparator
     * Testing the sorting of an Array
     */
    @Test
    public void testSort () {
        String[] list = {
            "1z1",
            "1z2",
            "1z14",
            "1d",
            "1c",
            "1b",
            "foo 03",
            "foo 00003",
            "foo 5",
            "foo 003",
            "foo~03",
            "foo 10far",
            "foo 10boo",
            "foo 10bar",
            "foo 10",
            "foo!03"
        };


        Arrays.sort( list, new IntuitiveStringComparator<String>() );

        String[] results = {
            "1b",
            "1c",
            "1d",
            "1z1",
            "1z2",
            "1z14",
            "foo 03",
            "foo 003",
            "foo 00003",
            "foo 5", 
            "foo 10",
            "foo 10bar",
            "foo 10boo",
            "foo 10far",
            "foo!03",
            "foo~03",
        };
        
        
        Assert.assertArrayEquals(results, list);
        
    }
    
}
