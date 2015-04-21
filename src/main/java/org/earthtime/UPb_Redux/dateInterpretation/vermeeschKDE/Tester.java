/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

/**
 *
 * @author James F. Bowring
 */
public class Tester {

    //Main moved to tests : TesterTest
    
    /*Pieter says:  here's a simple function that reads in some
     data from a file and spits out an array of density estimates at the
     points specified by the timescale array. Plotting those values against
     time gives the KDE:
     */

    /**
     *
     * @param filename
     * @param timescale
     * @return
     * @throws Exception
     */
    
    public double[] getKDE ( String filename, double[] timescale )
            throws Exception {

        Preferences prefs = new Preferences( true );
        OtherData otherData = new OtherData( filename, prefs );

        double[][] ae = otherData.getDataErrArray( otherData.preferences.logarithmic() );
        System.out.println( "ae[0] contents" );
        for (int i = 0; i < ae[0].length; i ++) {
            System.out.print( ae[0][i] + ", " );
        }
        System.out.println();

        KDE kde = new KDE();

        double[] pdf = kde.pdf( ae[0], timescale, true );
        System.out.println( "pdf contents" );
        for (int i = 0; i < pdf.length; i ++) {
            System.out.print( pdf[i] + ", " );
        }
        System.out.println();

        return pdf;

    }
}
