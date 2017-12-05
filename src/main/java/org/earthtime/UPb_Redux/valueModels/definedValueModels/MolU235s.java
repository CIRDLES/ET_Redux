/*
 * MolU235s.java
 *
 * Created on Dec 12, 2008
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UPb_Redux.valueModels.definedValueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolU235s extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -35769518777785051L;
    private final static String NAME = "molU235s";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private boolean doubleSpike;
    private boolean tracerIsMixed233_236;
    // is not a metal
    //  private ValueModel r238_233m;
    private ValueModel molU233t;
    private ValueModel r238_235oc;
    private ValueModel molU235b;
    private ValueModel molU235t;
    private ValueModel molU238b;
    private ValueModel molU238t;
    private ValueModel r238_235s;
    private ValueModel r233_235oc;
    private ValueModel r233_235m;
    // not double spike
    private ValueModel alphaU;
    // is a metal
    private ValueModel molU238s;

    /** Creates a new instance of MolU235s */
    public MolU235s() {
        super(NAME, UNCT_TYPE);
        this.doubleSpike = false;
        this.tracerIsMixed233_236 = false;

    }

    /**
     * Used to estimate U in the absence of U data
     * @param molU238s 
     * @param r238_235s 
     */
    public void EstimateValue(
            ValueModel molU238s,
            ValueModel r238_235s) {

        try {
            setValue(molU238s.getValue().//
                    divide(r238_235s.getValue(), ReduxConstants.mathContext15));
        } catch (Exception e) {
            setValue(BigDecimal.ZERO);
        }
    }

    /**
     * 
     * @param inputValueModels
     * @param parDerivTerms
     */
    @Override
    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {

        // jan 2011 added check for tracer as a metal
        if (tracerIsMixed233_236) {
            molU238s = inputValueModels[0];
            r238_235s = inputValueModels[1];

            try {
                setValue(molU238s.getValue().//
                        divide(r238_235s.getValue(), ReduxConstants.mathContext15));
            } catch (Exception e) {
                setValue(BigDecimal.ZERO);
            }

            try {
                BigDecimal dMolU235s__dMolU238s = BigDecimal.ONE.//
                        divide(r238_235s.getValue(), ReduxConstants.mathContext15);
                parDerivTerms.put("dMolU235s__dMolU238s", dMolU235s__dMolU238s);
            } catch (Exception e) {
                parDerivTerms.put("dMolU235s__dMolU238s", BigDecimal.ZERO);
            }

            try {
                BigDecimal dMolU235s__dR238_235s = molU238s.getValue().negate().//
                        divide(r238_235s.getValue().pow(2), ReduxConstants.mathContext15);
                parDerivTerms.put("dMolU235s__dR238_235s", dMolU235s__dR238_235s);
            } catch (Exception e) {
                parDerivTerms.put("dMolU235s__dR238_235s", BigDecimal.ZERO);
            }

        } else {

            if (doubleSpike) {

                r238_235oc = inputValueModels[0];
                r233_235oc = inputValueModels[1];
                r238_235s = inputValueModels[2];
                molU235b = inputValueModels[3];
                molU238b = inputValueModels[4];
                molU235t = inputValueModels[5];
                molU238t = inputValueModels[6];
                molU233t = inputValueModels[7];
                r233_235m = inputValueModels[8];

                BigDecimal term1 = BigDecimal.ZERO;
                try {
                    term1 = new BigDecimal("3.0").//
                            multiply(r238_235oc.getValue().//
                            divide(r233_235oc.getValue(), ReduxConstants.mathContext15)).//
                            multiply(molU233t.getValue());
                } catch (Exception e) {
                }

                BigDecimal term2 = new BigDecimal("5.0").//
                        multiply(r238_235oc.getValue().
                        multiply(molU235b.getValue().
                        add(molU235t.getValue())));

                BigDecimal term3 = new BigDecimal("2.0").//
                        multiply(molU238b.getValue().
                        add(molU238t.getValue()));

                BigDecimal term4 = new BigDecimal("5.0").//
                        multiply(r238_235oc.getValue()).
                        subtract(new BigDecimal("2.0").//
                        multiply(r238_235s.getValue()));

                if (term4.compareTo(BigDecimal.ZERO) == 0) {
                    // default value of 0.0 if division by zero
                    setValue(BigDecimal.ZERO);
                } else {
                    setValue(term1.subtract(term2).add(term3).
                            divide(term4, ReduxConstants.mathContext15));
                }


                ExpTreeII bdtTerm1 = ExpTreeII.ZERO;
                try {
                    bdtTerm1 =//
                            ExpTreeII.THREE.//
                            multiply(r238_235oc.getValueTree().//
                            divide(r233_235oc.getValueTree())).//
                            multiply(molU233t.getValueTree());
                    bdtTerm1.setNodeName("Term1");
                } catch (Exception e) {
                }

                ExpTreeII bdtTerm2 =
                        ExpTreeII.FIVE.//
                        multiply(r238_235oc.getValueTree().
                        multiply(molU235b.getValueTree().
                        add(molU235t.getValueTree())));
                bdtTerm2.setNodeName("Term2");

                ExpTreeII bdtTerm3 =
                        ExpTreeII.TWO.//
                        multiply(molU238b.getValueTree().
                        add(molU238t.getValueTree()));
                bdtTerm3.setNodeName("Term3");

                ExpTreeII bdtTerm4 = //
                        ExpTreeII.FIVE.//
                        multiply(r238_235oc.getValueTree()).
                        subtract(ExpTreeII.TWO.//
                        multiply(r238_235s.getValueTree()));
                bdtTerm4.setNodeName("Term4");

                setValueTree(//
                        bdtTerm1.subtract(bdtTerm2).add(bdtTerm3).
                        divide(bdtTerm4));

                //System.out.println(getValue().toPlainString() + "\n" + getValueTree().treeToString(1));
                if (getValue().compareTo(getValueTree().getNodeValue()) != 0) {
                    //               System.out.println( differenceValueCalcs() );
                }





                BigDecimal BD2 = new BigDecimal("2.0");
                BigDecimal BD3 = new BigDecimal("3.0");
                BigDecimal BD4 = new BigDecimal("4.0");
                BigDecimal BD5 = new BigDecimal("5.0");
                BigDecimal BD6 = new BigDecimal("6.0");
                BigDecimal BD10 = new BigDecimal("10.0");

                try {
                    BigDecimal dMolU235s__dR233_235oc =BD3.negate().//
                            multiply(molU233t.getValue().//
                            multiply(r238_235oc.getValue())).
                            divide(r233_235m.getValue().pow(2).//
                            multiply(BD5.//
                            multiply(r238_235oc.getValue()).//
                            subtract(BD2.//
                            multiply(r238_235s.getValue()))), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dR233_235oc", dMolU235s__dR233_235oc);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dR233_235oc", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dMolU233t = BD3.//
                            multiply(r238_235oc.getValue()).//
                            divide(r233_235oc.getValue().//
                            multiply(BD5.//
                            multiply(r238_235oc.getValue()).//
                            subtract(BD2.//
                            multiply(r238_235s.getValue()))), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dMolU233t", dMolU235s__dMolU233t);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU233t", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dR238_235oc = BD10.//
                            multiply(r238_235s.getValue()).//
                            multiply(molU235b.getValue().//
                            add(molU235t.getValue())).//

                            subtract(BD10.//
                            multiply(molU238b.getValue().//
                            add(molU238t.getValue()))).//

                            subtract(BD6.//
                            multiply(molU233t.getValue().//
                            multiply(r238_235s.getValue())).//
                            divide(r233_235oc.getValue(), ReduxConstants.mathContext15)).//

                            divide(BD5.//
                            multiply(r238_235oc.getValue()).//

                            subtract(BD2.//
                            multiply(r238_235s.getValue())).pow(2), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dR238_235oc", dMolU235s__dR238_235oc);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dR238_235oc", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dMolU235b = BD5.//
                            multiply(r238_235oc.getValue()).//
                            divide(BD2.//
                            multiply(r238_235s.getValue()).//
                            subtract(BD5.//
                            multiply(r238_235oc.getValue())), ReduxConstants.mathContext15);

                    parDerivTerms.put("dMolU235s__dMolU235b", dMolU235s__dMolU235b);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU235b", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dMolU235t = BD5.//
                            multiply(r238_235oc.getValue()).//
                            divide(BD2.//
                            multiply(r238_235s.getValue()).//
                            subtract(BD5.//
                            multiply(r238_235oc.getValue())), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dMolU235t", dMolU235s__dMolU235t);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU235t", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dMolU238b = BD2.//
                            divide(BD5.//
                            multiply(r238_235oc.getValue()).//
                            subtract(BD2.//
                            multiply(r238_235s.getValue())), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dMolU238b", dMolU235s__dMolU238b);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU238b", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dMolU238t = BD2.//
                            divide(BD5.//
                            multiply(r238_235oc.getValue()).//
                            subtract(BD2.//
                            multiply(r238_235s.getValue())), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dMolU238t", dMolU235s__dMolU238t);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU238t", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dR238_235s = BD4.//
                            multiply(molU238b.getValue().//
                            add(molU238t.getValue())).//

                            add(BD6.//
                            multiply(molU233t.getValue()).//
                            multiply(r238_235oc.getValue().//
                            divide(r233_235oc.getValue(), ReduxConstants.mathContext15))).//

                            subtract(BD10.//
                            multiply(r238_235oc.getValue().//
                            multiply(molU235b.getValue().//
                            add(molU235t.getValue())))).//

                            divide(BD5.//
                            multiply(r238_235oc.getValue()).//
                            subtract(BD2.//
                            multiply(r238_235s.getValue())).pow(2), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dR238_235s", dMolU235s__dR238_235s);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dR238_235s", BigDecimal.ZERO);
                }

            } else {

                alphaU = inputValueModels[0];
                molU233t = inputValueModels[1];
                molU235t = inputValueModels[2];
                molU238t = inputValueModels[3];
                molU235b = inputValueModels[4];
                molU238b = inputValueModels[5];
                r238_235oc = inputValueModels[6];
                r238_235s = inputValueModels[7];

                BigDecimal term1 = molU238b.getValue();

                BigDecimal term2 = molU238t.getValue();

                BigDecimal term3 =
                        BigDecimal.ONE.//
                        add(new BigDecimal("3.0").//
                        multiply(alphaU.getValue())).
                        multiply(r238_235oc.getValue());

                BigDecimal term4 =
                        molU235b.getValue().add(molU235t.getValue());

                BigDecimal term5 = r238_235s.getValue();

                try {
                    setValue(term1.add(term2).subtract(term3.multiply(term4)).
                            divide(term3.subtract(term5), ReduxConstants.mathContext15));
                } catch (Exception e) {
                    setValue(BigDecimal.ZERO);
                }


                BigDecimal BD3 = new BigDecimal("3.0");

                try {
                    BigDecimal dMolU235s__dMolU238b = BigDecimal.ONE.//
                            divide(BigDecimal.ONE.//
                            add(BD3.//
                            multiply(alphaU.getValue())).//
                            multiply(r238_235oc.getValue().//
                            subtract(r238_235s.getValue())), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dMolU238b", dMolU235s__dMolU238b);
                    parDerivTerms.put("dMolU235s__dMolU238t", dMolU235s__dMolU238b);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU238b", BigDecimal.ZERO);
                    parDerivTerms.put("dMolU235s__dMolU238t", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dAlphaU = BD3.negate().//
                            multiply(r238_235oc.getValue().//
                            multiply(molU238b.getValue().//
                            add(molU238t.getValue().//
                            subtract(r238_235s.getValue().//
                            multiply(molU235b.getValue().//
                            add(molU235t.getValue())))))).//
                            divide(r238_235oc.getValue().//
                            add(BD3.//
                            multiply(alphaU.getValue().//
                            multiply(r238_235oc.getValue()))).//
                            subtract(r238_235s.getValue()).pow(2), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dAlphaU", dMolU235s__dAlphaU);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dAlphaU", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dR238_235m = BigDecimal.ONE.//
                            add(BD3.//
                            multiply(alphaU.getValue())).//
                            multiply(r238_235s.getValue().//
                            multiply(molU235b.getValue().//
                            add(molU235t.getValue())).//
                            subtract(molU238b.getValue()).//
                            subtract(molU238t.getValue())).//

                            divide(r238_235oc.getValue().//
                            add(BD3.//
                            multiply(alphaU.getValue().//
                            multiply(r238_235oc.getValue()))).//
                            subtract(r238_235s.getValue()).pow(2), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dR238_235m", dMolU235s__dR238_235m);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dR238_235m", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dMolU235b = r238_235oc.getValue().//
                            add(BD3.//
                            multiply(alphaU.getValue().//
                            multiply(r238_235oc.getValue()))).//
                            divide(r238_235s.getValue().//
                            subtract(r238_235oc.getValue()).//
                            subtract(BD3.//
                            multiply(alphaU.getValue().//
                            multiply(r238_235oc.getValue()))), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dMolU235b", dMolU235s__dMolU235b);
                    parDerivTerms.put("dMolU235s__dMolU235t", dMolU235s__dMolU235b);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dMolU235b", BigDecimal.ZERO);
                    parDerivTerms.put("dMolU235s__dMolU235t", BigDecimal.ZERO);
                }

                try {
                    BigDecimal dMolU235s__dR238_235s = molU238b.getValue().//
                            add(molU238t.getValue().//
                            subtract(r238_235oc.getValue().//
                            multiply(BigDecimal.ONE.//
                            add(BD3.//
                            multiply(alphaU.getValue()))).//
                            multiply(molU235b.getValue().//
                            add(molU235t.getValue())))).//
                            divide(r238_235oc.getValue().//
                            add(BD3.//
                            multiply(alphaU.getValue().//
                            multiply(r238_235oc.getValue()))).//
                            subtract(r238_235s.getValue()).pow(2), ReduxConstants.mathContext15);
                    parDerivTerms.put("dMolU235s__dR238_235s", dMolU235s__dR238_235s);
                } catch (Exception e) {
                    parDerivTerms.put("dMolU235s__dR238_235s", BigDecimal.ZERO);
                }
            }
        }

    }

    /**
     * @param zircon 
     */
    public void setDoubleSpike(boolean zircon) {
        this.doubleSpike = zircon;
    }

    /**
     * @param tracerIsMixed233_236 the tracerIsMixed233_236 to set
     */
    public void setTracerIsMixed233_236(boolean tracerIsMixed233_236) {
        this.tracerIsMixed233_236 = tracerIsMixed233_236;
    }
}
