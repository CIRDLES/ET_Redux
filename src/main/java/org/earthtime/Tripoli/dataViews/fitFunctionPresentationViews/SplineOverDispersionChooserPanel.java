/*
 * SplineOverDispersionChooserPanel.java
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
package org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.SortedMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.AbstractFitFunctionView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.SmoothingSplineFitFunctionView;
import org.earthtime.Tripoli.dataViews.overlayViews.DataViewsOverlay;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.beans.ET_JButton;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class SplineOverDispersionChooserPanel extends AbstractRawDataView {

    /**
     *
     */
    protected ButtonGroup fitFunctionButtonGroup;
    private AbstractFitFunctionView fitFunctionView;
    private SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD;
    private JButton[] splineWithOdChoiceButtons;

    /**
     *
     * @param sampleSessionDataView
     * @param fitFunctionView
     * @param sessionOfStandardsSplinesWithOD
     * @param bounds
     */
    public SplineOverDispersionChooserPanel (//
            JLayeredPane sampleSessionDataView,//
            AbstractFitFunctionView fitFunctionView, //
            SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD,//
            Rectangle bounds ) {
        super( bounds );

        this.sampleSessionDataView = sampleSessionDataView;
        this.fitFunctionView = fitFunctionView;
        this.sessionOfStandardsSplinesWithOD = sessionOfStandardsSplinesWithOD;

        this.splineWithOdChoiceButtons = new JButton[sessionOfStandardsSplinesWithOD.size()];

        setOpaque( true );
        setCursor( Cursor.getDefaultCursor() );

        fitFunctionButtonGroup = new ButtonGroup();
    }

    @Override
    public void paint ( Graphics2D g2d ) {
        paintInit( g2d );

        g2d.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );

        g2d.setFont( new Font(
                "SansSerif",
                Font.BOLD,
                9 ) );
        
        if ( tics != null ) {
            g2d.setStroke( new BasicStroke( 0.3f ) );
            g2d.setPaint( Color.BLACK );
            for (int i = 0; i < tics.length; i ++) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            15, mapY( getTics()[i].doubleValue() ), getWidth(), mapY( getTics()[i].doubleValue() ) );
                    g2d.draw( ticMark );

                    TextLayout mLayout = //
                            new TextLayout(
                            getTics()[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext() );

                    Rectangle2D bounds = mLayout.getBounds();

                    g2d.drawString( getTics()[i].toPlainString(),//
                            (float) 13 - (float) bounds.getWidth(),//
                            (float) mapY( getTics()[i].doubleValue() ) + (float) bounds.getHeight() / 2.0f );
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void preparePanel (boolean doReScale, boolean inLiveMode) {
        this.removeAll();

        minY = Double.MAX_VALUE;
        maxY =  - Double.MAX_VALUE;
        minX = Double.MAX_VALUE;
        maxX =  - Double.MAX_VALUE;

        Iterator<Double> splineFitsIterator = sessionOfStandardsSplinesWithOD.keySet().iterator();
        while (splineFitsIterator.hasNext()) {
            double xiValue = splineFitsIterator.next();
            double logRoughness = sessionOfStandardsSplinesWithOD.get( xiValue ).getLogRoughness();

            minY = Math.min( minY, logRoughness );
            maxY = Math.max( maxY, logRoughness );
            maxX = Math.max( maxX, xiValue );
            minX = Math.min( minX, xiValue);
        }

        tics = TicGeneratorForAxes.generateTics( minY, maxY, (int) (getHeight() / 15.0) );

        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment( minY, maxY, 0.3 );
        minY -= yMarginStretch;
        maxY += yMarginStretch;

        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment( minX, maxX, 0.2 );
        minX -= xMarginStretch;
        maxX += xMarginStretch;


        Iterator<Double> splinesWithODiterator = sessionOfStandardsSplinesWithOD.keySet().iterator();
        int index = 0;
        while (splinesWithODiterator.hasNext()) {
            double xi = splinesWithODiterator.next();
            final AbstractFunctionOfX splineWithOD = sessionOfStandardsSplinesWithOD.get( xi );
            double logRoughness = splineWithOD.getLogRoughness();

            splineWithOdChoiceButtons[index] = new ET_JButton( "" );
            splineWithOdChoiceButtons[index].setBounds( //
                    new Rectangle( (int) mapX( xi ), (int) mapY( logRoughness ), 7, 7 ) );

            DecimalFormat f = new DecimalFormat( "\u03BE = 0.00000" );
            splineWithOdChoiceButtons[index].setToolTipText( f.format( xi ) );
            splineWithOdChoiceButtons[index].addActionListener( new ActionListener() {
                @Override
                public void actionPerformed ( ActionEvent ae ) {
                    AbstractFunctionOfX currentSplineWithOD = null;

                    if ( sampleSessionDataView instanceof SessionOfStandardView ) {
                        currentSplineWithOD = ((SessionOfStandardView) sampleSessionDataView).getSessionForStandardDataModel().getSelectedFitFunction();
                        currentSplineWithOD.copyValuesFrom( splineWithOD );
                        ((SessionOfStandardView) sampleSessionDataView).refreshPanel(true, false);
                        ((SmoothingSplineFitFunctionView) fitFunctionView).refreshXiLabel();
                        ((SmoothingSplineFitFunctionView) fitFunctionView).getPresentationView().repaint();
                    } else if ( sampleSessionDataView instanceof DataViewsOverlay ) {
                        currentSplineWithOD = ((DataViewsOverlay) sampleSessionDataView).getDownholeFractionationDataModel().getSelectedFitFunction();
                        currentSplineWithOD.copyValuesFrom( splineWithOD );
                        ((DataViewsOverlay) sampleSessionDataView).refreshPanel(true, false);
                        ((SmoothingSplineFitFunctionView) fitFunctionView).refreshXiLabel();
                        ((SmoothingSplineFitFunctionView) fitFunctionView).getPresentationView().repaint();
                    }



                }
            } );

            add( splineWithOdChoiceButtons[index] );

            index ++;
        }


    }

    @Override
    public DataModelInterface getDataModel () {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @param sessionOfStandardsSplinesWithOD the
     * sessionOfStandardsSplinesWithOD to set
     */
    public void setSessionOfStandardsSplinesWithOD ( SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD ) {
        this.sessionOfStandardsSplinesWithOD = sessionOfStandardsSplinesWithOD;
    }
}
