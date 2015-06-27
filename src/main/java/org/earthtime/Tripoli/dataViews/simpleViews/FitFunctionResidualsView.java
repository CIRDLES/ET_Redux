/*
 * FitFunctionResidualsView.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.beans.MaskingShade;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.DownholeFractionationDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.MaskingShadeTargetInterface;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;

/**
 *
 * @author James F. Bowring
 */
public class FitFunctionResidualsView extends AbstractRawDataView implements MaskingShadeTargetInterface {

    private DownholeFractionationDataModel downholeFractionationDataModel;
    private int countOfMaskedTimeSlotsOnLeft;
    private int countOfMaskedTimeSlotsOnRight;

    /**
     * 
     *
     * @param downholeFractionationDataModel
     * @param dataPresentationMode
     * @param bounds
     */   
    public FitFunctionResidualsView ( //
            DownholeFractionationDataModel downholeFractionationDataModel, //
            DataPresentationModeEnum dataPresentationMode,//
            Rectangle bounds) {

        super( bounds );

        setCursor( Cursor.getDefaultCursor() );

        this.downholeFractionationDataModel = downholeFractionationDataModel;
        this.dataPresentationMode = dataPresentationMode;
    }

    /**
     * 
     * @param g2d
     */
    @Override
    public void paint ( Graphics2D g2d ) {
        paintInit( g2d );


        g2d.setStroke( new BasicStroke( 1.0f ) );
        g2d.drawLine( 0, 0, getWidth() - 1, 0 );

        // draw residuals as little rectangles
        g2d.setPaint( Color.RED );
        for (int i = 0; i < myOnPeakData.length; i ++) {
            Path2D box = new Path2D.Double();
            box.moveTo( mapX( myOnPeakNormalizedAquireTimes[i] ) - 2, mapY( myOnPeakData[i] ) );
            box.lineTo( mapX( myOnPeakNormalizedAquireTimes[i] ) + 2, mapY( myOnPeakData[i] ) );
            box.lineTo( mapX( myOnPeakNormalizedAquireTimes[i] ) + 2, mapY( 0.0 ) );
            box.lineTo( mapX( myOnPeakNormalizedAquireTimes[i] ) - 2, mapY( 0.0 ) );
            box.closePath();

            g2d.fill( box );

        }


        //draw zero
        g2d.setColor( Color.black );
        g2d.setStroke( new BasicStroke( 0.5f ) );
        Shape zeroLine = new Line2D.Double(//
                mapX( myOnPeakNormalizedAquireTimes[0] ) - 4.0f,//
                mapY( 0 ), //
                mapX( myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1] ) + 4.0f,//
                mapY( 0 ) );
        g2d.draw( zeroLine );
    }

    /**
     * 
     */
    @Override
    public void preparePanel () {

        this.removeAll();

        setDisplayOffsetY( 0.0 );
        setDisplayOffsetX( 0.0 );


        // walk standards and get min and max for axes
        myOnPeakData = downholeFractionationDataModel.getFittedStandardsResiduals();

        // normalize aquireTimes
        myOnPeakNormalizedAquireTimes = downholeFractionationDataModel.getNormalizedAquireTimes();

        // Y-axis is ratios
        minY = Double.MAX_VALUE;
        maxY =  - Double.MAX_VALUE;

        // find min and max y
        for (int i = 0; i < myOnPeakData.length; i ++) {
            minY = Math.min( minY, myOnPeakData[i] );
        }
        for (int i = 0; i < myOnPeakData.length; i ++) {
            maxY = Math.max( maxY, myOnPeakData[i] );
        }

        // let's run zero through the center
        if ( maxY >  - minY ) {
            minY =  - maxY;

        } else {
            maxY =  - minY;
        }


        // masking shade only for standards now *******************************************
        // first determine width of mask
        countOfMaskedTimeSlotsOnLeft = -1;
        for (int i = 0; i < DownholeFractionationDataModel.MAX_AQUISITIONS_SHADABLE; i ++) {
            if (  ! downholeFractionationDataModel.getMaskingSingleton().getMaskingArray()[i] ) {
                countOfMaskedTimeSlotsOnLeft ++;
            }
        }

        JLayeredPane myMaskingShadeLeft = new MaskingShade( //
                this, //
                false,//
                MaskingShade.PULL_FROM_LEFT,//
                countOfMaskedTimeSlotsOnLeft );

        add( myMaskingShadeLeft, javax.swing.JLayeredPane.DEFAULT_LAYER );

        countOfMaskedTimeSlotsOnRight = -1;
        int lowestAquisitionIndex = //
                downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length - DownholeFractionationDataModel.MAX_AQUISITIONS_SHADABLE;
        for (int i = lowestAquisitionIndex; i < downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length; i ++) {
            if (  ! downholeFractionationDataModel.getMaskingSingleton().getMaskingArray()[i] ) {
                countOfMaskedTimeSlotsOnRight ++;
            }
        }

        JLayeredPane myMaskingShadeRight = new MaskingShade( //
                this, //
                false,//
                MaskingShade.PULL_FROM_RIGHT,//
                countOfMaskedTimeSlotsOnRight );

        add( myMaskingShadeRight, javax.swing.JLayeredPane.DEFAULT_LAYER );


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
        return 0;
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
