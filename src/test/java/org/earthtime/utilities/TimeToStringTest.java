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
package org.earthtime.utilities;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author parizotclement
 */
public class TimeToStringTest {
    
    
    /**
     * Integration Test of class TimeToString
     * 
     * 
     * 
     */
    @Test
    public void testOutput (){
        long time = 1191343282;
        TimeToString ts = new TimeToString();        
        assertEquals("16:41:22",ts.secondsAsLongToTimeString( time ) );
        
        //Different hour depending on the default time zone
        //assertEquals("10:55:43 14.Jan",ts.timeStampString( time ) );
    }
    
}
