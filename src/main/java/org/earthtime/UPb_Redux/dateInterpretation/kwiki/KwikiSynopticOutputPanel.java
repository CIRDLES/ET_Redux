/*
 * KwikiSynopticOutputPanel.java
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring
 */
public class KwikiSynopticOutputPanel extends JPanel {

    private ETFractionInterface fraction;

    /** Creates a new instance of KwikiDateDisplay
     * @param x
     * @param y 
     * @param fraction  
     */
    public KwikiSynopticOutputPanel ( int x, int y, ETFractionInterface fraction ) {
        super();

        this.fraction = fraction;

        setOpaque( true );

        setBackground(ReduxConstants.mySampleYellowColor );

        setBounds( x, y, 180, 275 );
    }

    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );

        paint( (Graphics2D) g );
    }

    /**
     * 
     * @param g2d
     */
    public void paint ( Graphics2D g2d ) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );
        g2d.setColor( Color.BLACK );

        // Arial prints numbers all same width, but not letters!
        g2d.setFont( new Font( "Monospaced", Font.BOLD, 12 ) );
        g2d.setPaint( Color.BLACK );
        g2d.setStroke( new BasicStroke( 1.0f ) );

        DrawBounds( g2d );

        BigDecimal picoNanoBorder = new BigDecimal( 0.000000001 );
        int row = 1;
        int verticalOffset = 14;
        int col1Loc = 5;
        int col2Loc = 100;

        g2d.drawString( "% discordance", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                getFraction().getRadiogenicIsotopeDateByName( RadDates.percentDiscordance ).getValue() ),
                col2Loc, row ++ * verticalOffset );

        //if (getFraction().getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getValue().compareTo(picoNanoBorder) > 0) {
        if ( ((UPbFraction) getFraction()).getOutputsByName( "blankPbMass" ).getValue().compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "Pb blank (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "blankPbMass" ).getValue().movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "Pb blank (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "blankPbMass" ).getValue().movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        if ( ((UPbFraction) getFraction()).getOutputsByName( "initCommonPbMass" ).getValue().compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "Pb initial (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "initCommonPbMass" ).getValue().movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "Pb initial (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "initCommonPbMass" ).getValue().movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        if ( getFraction().getCompositionalMeasureByName( "totRadiogenicPbMass" ).getValue().compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "Pb* (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    getFraction().getCompositionalMeasureByName( "totRadiogenicPbMass" ).getValue().movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "Pb* (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    getFraction().getCompositionalMeasureByName( "totRadiogenicPbMass" ).getValue().movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        BigDecimal totalPbMass =//
                getFraction().getCompositionalMeasureByName( "totCommonPbMass" ).getValue().//
                add( getFraction().getCompositionalMeasureByName( "totRadiogenicPbMass" ).getValue() );
        if ( totalPbMass.compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "Pb total (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue( totalPbMass.movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "Pb total (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue( totalPbMass.movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        g2d.drawString( "Pb*/Pbc", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                getFraction().getCompositionalMeasureByName( "radToCommonTotal" ).getValue() ),
                col2Loc, row ++ * verticalOffset );

        g2d.drawString( "mol% 206Pb*", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                ((UPbFraction) getFraction()).getOutputsByName( "radToCommonPb206" ).getValue().movePointRight( 2 ) ),
                col2Loc, row ++ * verticalOffset );

        g2d.drawString( "mol% 207Pb*", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                ((UPbFraction) getFraction()).getOutputsByName( "radToCommonPb207" ).getValue().movePointRight( 2 ) ),
                col2Loc, row ++ * verticalOffset );

        g2d.drawString( "mol% 208Pb*", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                ((UPbFraction) getFraction()).getOutputsByName( "radToCommonPb208" ).getValue().movePointRight( 2 ) ),
                col2Loc, row ++ * verticalOffset );

        row ++;

        if ( getFraction().getAnalysisMeasure( AnalysisMeasures.uBlankMassInGrams.getName() ).getValue().compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "U blank (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    getFraction().getAnalysisMeasure( AnalysisMeasures.uBlankMassInGrams.getName() ).getValue().movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "U blank (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    getFraction().getAnalysisMeasure( AnalysisMeasures.uBlankMassInGrams.getName() ).getValue().movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        if ( ((UPbFraction) getFraction()).getOutputsByName( "uTracerMassInGrams" ).getValue().compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "U tracer (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "uTracerMassInGrams" ).getValue().movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "U tracer (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "uTracerMassInGrams" ).getValue().movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        if ( ((UPbFraction) getFraction()).getOutputsByName( "uSampleMassInGrams" ).getValue().compareTo( picoNanoBorder ) > 0 ) {
            g2d.drawString( "U sample (ng)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "uSampleMassInGrams" ).getNonNegativeValue().movePointRight( 9 ) ),
                    col2Loc, row ++ * verticalOffset );
        } else {
            g2d.drawString( "U sample (pg)", col1Loc, row * verticalOffset );
            g2d.drawString( FormulateOutputValue(//
                    ((UPbFraction) getFraction()).getOutputsByName( "uSampleMassInGrams" ).getNonNegativeValue().movePointRight( 12 ) ),
                    col2Loc, row ++ * verticalOffset );
        }

        row ++;

        g2d.drawString( "206/204 (corr)", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                getFraction().getSampleIsochronRatiosByName( "r206_204tfc" ).getValue() ),
                col2Loc, row ++ * verticalOffset );

        g2d.drawString( "Th/U", col1Loc, row * verticalOffset );
        g2d.drawString( FormulateOutputValue(//
                getFraction().getCompositionalMeasureByName( "rTh_Usample" ).getValue() ),
                col2Loc, row ++ * verticalOffset );

        row ++;

        g2d.drawString( "alphaU (%/amu)", col1Loc, row * verticalOffset );
        BigDecimal assumedAlphaU = getFraction().getAnalysisMeasure( AnalysisMeasures.alphaU.getName() ).getValue();
        if ( ((UPbFraction) getFraction()).getMeanAlphaU().compareTo( BigDecimal.ZERO ) > 0 ) {
            assumedAlphaU = ((UPbFraction) getFraction()).getMeanAlphaU();
        }

        g2d.drawString( FormulateOutputValue(//
                assumedAlphaU.movePointRight( 2 ) ),
                col2Loc, row ++ * verticalOffset );

        g2d.drawString( "alphaPb (%/amu)", col1Loc, row * verticalOffset );
        BigDecimal assumedAlphaPb = getFraction().getAnalysisMeasure( AnalysisMeasures.alphaPb.getName() ).getValue();
        if ( ((UPbFraction) getFraction()).getMeanAlphaPb().compareTo( BigDecimal.ZERO ) > 0 ) {
            assumedAlphaPb = ((UPbFraction) getFraction()).getMeanAlphaPb();
        }

        g2d.drawString( FormulateOutputValue(//
                assumedAlphaPb.movePointRight( 2 ) ),
                col2Loc, row ++ * verticalOffset );


    }

    private String FormulateOutputValue ( BigDecimal value ) {
        return String.format( "%1$10s", value.setScale( 2, RoundingMode.HALF_UP ) );
    }

    private void DrawBounds ( Graphics2D g2d ) {

        g2d.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );

    }

    /**
     * @return the fraction
     */
    public ETFractionInterface getFraction () {
        return fraction;
    }

    /**
     * @param fraction the fraction to set
     */
    public void setFraction ( ETFractionInterface fraction ) {
        this.fraction = fraction;
    }
}
