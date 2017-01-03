/*
 * URatioCellRenderer.java
 *
 * Created on March 25, 2006, 8:54 PM
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Formatter;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class URatioCellRenderer extends TableCellRendererToLabel {

    /**
     * 
     */
    public final static int URatioWidthInTable = 70;

    /**
     * 
     * @param g
     */
    @Override
    public void paint ( Graphics g ) {
        if ( mePaint ) {
            g.setColor(ReduxConstants.ColorOfUranium );
            // } else {
            //     g.setColor(selection);
        }
        // g.fillRect(0, 0, getWidth()-1, getHeight()-1);

        super.paint( g );

        g.setColor( Color.black );
        g.setFont( new Font( "Monospaced", Font.BOLD, 11 ) );

        String data[] = this.getText().split( ":" );
        StringBuilder ratio = new StringBuilder();
        StringBuilder stdErr = new StringBuilder();

        Formatter ratioFormatter = new Formatter( ratio );
        Formatter stdErrFormatter = new Formatter( stdErr );

        try {
            ratioFormatter.format( "%9.4f", Double.valueOf( data[0].trim() ) );
            g.drawString( ratio.substring( 0 ),
                    (int) g.getClipBounds().getWidth() - 70, 12 ); // hard-coded for now 2,12);
            stdErrFormatter.format( "%9.4f", Double.valueOf( data[1].trim() ) );
            g.drawString( stdErr.substring( 0 ),
                    (int) g.getClipBounds().getWidth() - 70, 27 );//, 2,27);
        } catch (Exception e) {
            // april 2010 draw whatever is there, for LAGACY = "n/a"
            g.drawString( data[0].trim(),
                    (int) g.getClipBounds().getWidth() - 35 - data[0].trim().length() / 2, 20 ); // hard-coded for now 2,12);
        }
    }
    // public void setToolTipText(String text) {
    //     String ratio[] = text.split(":");
    //     super.setToolTipText("ratio with %StdError");
    // }
}
