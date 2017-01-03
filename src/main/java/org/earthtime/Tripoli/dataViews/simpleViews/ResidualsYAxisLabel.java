/*
 * ResidualsYAxisLabel.java
 *
 * Created Jul 27, 2011
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.MaskingShadeTargetInterface;

/**
 *
 * @author James F. Bowring
 */
public class ResidualsYAxisLabel extends AbstractRawDataView implements MaskingShadeTargetInterface {

    private AbstractRawDataView tripoliFractionRawDataModelView;

    /**
     * 
     * @param tripoliFractionRawDataModelView
     * @param bounds
     */
    public ResidualsYAxisLabel ( //
            AbstractRawDataView tripoliFractionRawDataModelView,
            Rectangle bounds ) {

        super( bounds );

        setCursor( Cursor.getDefaultCursor() );

        this.tripoliFractionRawDataModelView = tripoliFractionRawDataModelView;
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

        String label = "Residuals:";
        TextLayout mLayout = //
                new TextLayout(
                label, g2d.getFont(), g2d.getFontRenderContext() );

        Rectangle2D bounds = mLayout.getBounds();

        g2d.drawString( label,//
                getWidth() - (float) (bounds.getWidth()) - 2f,//
                (float) mapY( getRangeY_Display() / 2.0 ) + (float) (bounds.getHeight() / 2f) );

    }

    /**
     * 
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel (boolean doReScale, boolean inLiveMode) {

        this.removeAll();

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
