/*
 * TimeToString.java
 *
 * Created Sep 10, 2011
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility for printing timeStamps.
 * see http://javatechniques.com/blog/dateformat-and-simpledateformat-examples/
 * 
 * @author James F. Bowring
 */
public class TimeToString {

    /**
     *
     * @param time
     * @return
     */
    public static String secondsAsLongToTimeString ( long time ) {
        int seconds = (int) (time % 60);
        int minutes = (int) ((time / 60) % 60);
        int hours = (int) ((time / 3600) % 24);
        String secondsStr = (seconds < 10 ? "0" : "") + seconds;
        String minutesStr = (minutes < 10 ? "0" : "") + minutes;
        String hoursStr = (hours < 10 ? "0" : "") + hours;

        return hoursStr + ":" + minutesStr + ":" + secondsStr;
    }

    /**
     * 
     * @param time
     * @return
     */
    public static String timeStampString ( long time ) {
        
        Date timeStamp = new Date( time );
        
        SimpleDateFormat format =
            new SimpleDateFormat("HH:mm:ss dd.MMM");
        
        //In case it is needed to change the time zone
        //format.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        return format.format(timeStamp);
    }
    
    /**
     * 
     * @param time
     * @return
     */
    public static String timeStampStringNoDate ( long time ) {
        Date timeStamp = new Date( time );
        
        SimpleDateFormat format =
            new SimpleDateFormat("HH:mm:ss:S");
        
        return format.format(timeStamp);
    }

}
