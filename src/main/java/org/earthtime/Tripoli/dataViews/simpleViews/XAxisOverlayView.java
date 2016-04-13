/*
 * XAxisOverlayView.java
 *
 * Created Jul 27, 2011
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.RawIntensitiesDataView;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.MaskingShadeTargetInterface;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class XAxisOverlayView extends AbstractRawDataView implements MaskingShadeTargetInterface {

    private AbstractRawDataView tripoliFractionRawDataModelView;
    private int shiftOnPeakAquireTimes;

    /**
     * 
     * @param tripoliFractionRawDataModelView
     * @param bounds
     */
    public XAxisOverlayView ( //
            AbstractRawDataView tripoliFractionRawDataModelView,
            Rectangle bounds ) {

        super( bounds );

        setCursor( Cursor.getDefaultCursor() );

        this.tripoliFractionRawDataModelView = tripoliFractionRawDataModelView;
        this.shiftOnPeakAquireTimes = 0;
    }

    /**
     * 
     * @param g2d
     */
    @Override
    public void paint ( Graphics2D g2d ) {
        paintInit( g2d );

        NumberFormat formatter = new DecimalFormat( "##0" );


        // draw x tics and labels
        // need to use tic generator class
        //int ticVal = -1;
//        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i ++) {
//            //if ( (int) Math.floor( myOnPeakNormalizedAquireTimes[i] ) > ticVal ) {
//            //ticVal = (int) Math.floor( myOnPeakNormalizedAquireTimes[i] );
//            Line2D tic = new Line2D.Double( //
//                    mapX( myOnPeakNormalizedAquireTimes[i] + shiftOnPeakAquireTimes ),//
//                    mapY( 7 ),//
//                    mapX( myOnPeakNormalizedAquireTimes[i] + shiftOnPeakAquireTimes ),//
//                    mapY( maxY ) );
//            g2d.draw( tic );


        for (int i = 0; i < getTics().length; i ++) {
            try {
                Shape ticMark = new Line2D.Double( //
                        mapX( shiftOnPeakAquireTimes + getTics()[i].doubleValue() ),//
                        mapY( 7 ),//
                        mapX( shiftOnPeakAquireTimes + getTics()[i].doubleValue() ), //
                        mapY( maxY ) );
                g2d.draw( ticMark );

                TextLayout mLayout = //
                        new TextLayout(
                        getTics()[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext() );

                Rectangle2D bounds = mLayout.getBounds();

                g2d.drawString( getTics()[i].toPlainString(),//
                        (float) mapX( shiftOnPeakAquireTimes + getTics()[i].doubleValue() ) - (float) (bounds.getWidth() / 2.0f),//
                        (float) mapY( 1 ) );
            } catch (Exception e) {
            }
            //}



//            // every other label for narrow graphs
//            int narrowWidthThreshold = (tripoliFractionRawDataModelView instanceof RawIntensitiesDataView) ? 512 : 256;
//            if ( ((getWidth() < narrowWidthThreshold) && (i % 2 == 0)) || (getWidth() >= narrowWidthThreshold) ) {
//                String ticLabel = formatter.format( myOnPeakNormalizedAquireTimes[i] );
//                TextLayout mLayout = //
//                        new TextLayout(
//                        ticLabel, g2d.getFont(), g2d.getFontRenderContext() );
//
//                Rectangle2D bounds = mLayout.getBounds();
//
//                g2d.drawString( ticLabel,//
//                        (float) mapX( myOnPeakNormalizedAquireTimes[i] + shiftOnPeakAquireTimes ) - (float) (bounds.getWidth() / 2.0f),//
//                        (float) mapY( 1 ) );
//            }
            // }
        }

    }

    /**
     * 
     * @param doReScale the value of doReScale
     */
    @Override
    public void preparePanel (boolean doReScale) {

        this.removeAll();

        setDisplayOffsetY( 0.0 );
        setDisplayOffsetX( 0.0 );

        // X axis needs normalized aquireTimes
        myOnPeakNormalizedAquireTimes = tripoliFractionRawDataModelView.getDataModel().getOnPeakAquireTimesInSeconds();//.getNormalizedOnPeakAquireTimes();//.getOnPeakNormalizedAquireTimes();

        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];

        setTics( TicGeneratorForAxes.generateTics( minX, Math.floor( maxX ), 15));//(int) Math.floor( maxX ) ) );

        // test for special case of rawisotope data which is twice as wide
        if ( tripoliFractionRawDataModelView instanceof RawIntensitiesDataView ) {
            shiftOnPeakAquireTimes = (int) Math.ceil( maxX );
            maxX += shiftOnPeakAquireTimes;
        }

        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment( minX, maxX, 0.05 );
        minX -= xMarginStretch;
        maxX += xMarginStretch;


        // arbitrary
        minY = 0.0;
        maxY = 10.0;

    }

    /**
     * 
     * @return
     */
    @Override
    public DataModelInterface getDataModel () {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param currentShadeX
     * @return
     */
    @Override
    public int provideShadeXFromLeft ( int currentShadeX ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param currentShadeX
     * @return
     */
    @Override
    public int provideShadeXFromRight ( int currentShadeX ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
