/*
 * IsotopesEnum.java
 *
 * Created Sep 29, 2012
 *
 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.isotopes;

/**
 *
 * @author James F. Bowring
 */
public enum IsotopesEnum {

    /**
     *
     */
    Hf176( "Hf", 176, "176Hf", "Hf176" ),

    /**
     *
     */
    Hg202( "Hg", 202, "202Hg", "Hg202" ),

    /**
     *
     */
    Pb204( "Pb", 204, "204Pb", "Pb204" ),

    /**
     *
     */
    Pb206( "Pb", 206, "206Pb", "Pb206" ),

    /**
     *
     */
    Pb207( "Pb", 207, "207Pb", "Pb207" ),

    /**
     *
     */
    Pb208( "Pb", 208, "208Pb", "Pb208" ),

    /**
     *
     */
    Th232( "Th", 232, "232Th", "Th232" ),

    /**
     *
     */
    U235( "U", 235, "235U", "U235"),

    /**
     *
     */
    U238( "U", 238, "238U", "U238" );
    private final String symbol;
    private final int atomicMass;
    private final String prettyName;
    private final String name;

    private IsotopesEnum ( //
            final String symbol, //
            final int atomicMass, //
            final String prettyName,//
            final String name ) {
        this.symbol = symbol;
        this.atomicMass = atomicMass;
        this.prettyName = prettyName;
        this.name = name;
    }

    /**
     *
     * @return the prettyName
     */
    public String getPrettyName () {
        return prettyName;
    }

    /**
     * @return the symbol
     */
    public String getSymbol () {
        return symbol;
    }

    /**
     * @return the atomicMass
     */
    public int getAtomicMass () {
        return atomicMass;
    }

    /**
     * @return the name
     */
    public String getName () {
        return name;
    }
}
