/*
 * ConcordiaLine.java
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

import java.awt.geom.Path2D;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;

/**
 *
 * @author James F. Bowring
 */
public class ConcordiaLine {

    private ParametricCurveSegmentI startSeg;
    private ParametricCurveSegmentI endSeg;
    /**
     * 
     */
    protected GraphAxesSetup graphAxesSetup;
    /**
     * 
     */
    protected ParametricCurveSegmentI startUpperUncertSeg;
    /**
     * 
     */
    protected ParametricCurveSegmentI endUpperUncertSeg;
    /**
     * 
     */
    protected ParametricCurveSegmentI startLowerUncertSeg;
    /**
     * 
     */
    protected ParametricCurveSegmentI endLowerUncertSeg;
    private Path2D upperUnctEnvelope;
    private Path2D lowerUnctEnvelope;

    /**
     * 
     * @param startSeg
     * @param graphAxesSetup
     */
    public ConcordiaLine(ParametricCurveSegmentI startSeg, GraphAxesSetup graphAxesSetup) {
        this.startSeg = startSeg;
        this.endSeg = startSeg;
        this.graphAxesSetup = graphAxesSetup;
        this.startUpperUncertSeg = null;
        this.endUpperUncertSeg = null;
        this.startLowerUncertSeg = null;
        this.endLowerUncertSeg = null;
        this.upperUnctEnvelope = null;
        this.lowerUnctEnvelope = null;
    }

    /**
     * 
     * @param n
     */
    public void RefineLineByRecursiveHalving(int n) {

        for (int i = 0; i < n; i++) {
            ParametricCurveSegmentI mySeg = startSeg;

            while (mySeg != null) {
                mySeg.SplitLeft();

                mySeg = mySeg.getRightSeg();
            }
            startSeg = startSeg.getLeftSeg();
        }
    }

    // June 2010
    /**
     * 
     * @param aspectRatio
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public void calcUpperUnctEnvelopeC(double aspectRatio, double minX, double minY, double maxX, double maxY) {

        // parameters represent viewport 
        constructUpperEnvelopeAtLeftEnd(startSeg, aspectRatio, minX, minY, maxX, maxY);
        constructUpperEnvelopeAtRightEnd(endSeg, aspectRatio, minX, minY, maxX, maxY);

        upperUnctEnvelope = new Path2D.Double (Path2D.WIND_NON_ZERO);

        // start at bottom left of concordia
        upperUnctEnvelope.moveTo(
                (float) mapX(startSeg.minX()),
                (float) mapY(startSeg.minY()));

        ParametricCurveSegmentI myWorkingSeg = startUpperUncertSeg;

        // decide whether to include origin
        if (myWorkingSeg != null) {
            if (myWorkingSeg.minLessSigmaY(aspectRatio) > startSeg.minY()) {
                upperUnctEnvelope.lineTo(
                        mapX(minX), mapY(minY));
            }
        } else {
            // paint the upper left corner
            upperUnctEnvelope.lineTo(
                    (float) mapX(minX),
                    (float) mapY(maxY));
        }


        while (myWorkingSeg != null) {
            // check for out of bounds
            double tempX = myWorkingSeg.minLessSigmaX(aspectRatio);
            double tempY = myWorkingSeg.minLessSigmaY(aspectRatio);
            double tempXc = myWorkingSeg.controlUpperX(aspectRatio);
            double tempYc = myWorkingSeg.controlUpperY(aspectRatio);
            double tempXr = myWorkingSeg.maxLessSigmaX(aspectRatio);
            double tempYr = myWorkingSeg.maxLessSigmaY(aspectRatio);

            upperUnctEnvelope.lineTo(
                    (float) mapX(tempX),
                    (float) mapY(tempY));

            if ((pointInViewPort(tempX, tempY, minX, minY, maxX, maxY)) //
                    ||//
                    ((determineAxisIntersectedByUpperEnvelopeLeft(minX, minY, tempX, tempY, tempXr, tempYr) == 1)
                    && pointInViewPort(tempXr, tempYr, minX, minY, maxX, maxY))) {
                upperUnctEnvelope.curveTo(//
                        (float) mapX(tempX),
                        (float) mapY(tempY),
                        (float) mapX(tempXc),
                        (float) mapY(tempYc),
                        (float) mapX(tempXr),
                        (float) mapY(tempYr));

            } else if ((tempX > minX) && (tempY > minY) && !pointInViewPort(tempX, tempY, minX, minY, maxX, maxY) //
                    && (!pointInViewPort(tempXr, tempYr, minX, minY, maxX, maxY))) {//
                // handle upper right corner
                if (determineAxisIntersectedByConcordiaRight(maxX, maxY) == -1) {
                    upperUnctEnvelope.lineTo(
                            (float) mapX(tempXr),
                            (float) mapY(tempYr));
                }
                upperUnctEnvelope.lineTo(
                        (float) mapX(maxX),
                        (float) mapY(maxY));
                // get rid of the rest of out-of-view segments
                while (myWorkingSeg.getRightSeg() != null) {
                    myWorkingSeg = myWorkingSeg.getRightSeg();
                }
            }

            myWorkingSeg = myWorkingSeg.getRightSeg();
        }
    }

    /**
     * 
     * @param aspectRatio
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public void calcUpperUnctEnvelopeTW(double aspectRatio, double minX, double minY, double maxX, double maxY) {

        // TW plotting is upside down because concordia lines grow from low to high age
        // parameters represent viewport
        constructUpperEnvelopeAtLeftEnd(endSeg, aspectRatio, minX, minY, maxX, maxY);
        constructUpperEnvelopeAtRightEnd(startSeg, aspectRatio, minX, minY, maxX, maxY);

        upperUnctEnvelope = new Path2D.Double (Path2D.WIND_NON_ZERO);

        // start at top left of tw
        upperUnctEnvelope.moveTo(
                (float) mapX(endSeg.maxX()),
                (float) mapY(endSeg.maxY()));

        ParametricCurveSegmentI myWorkingSeg = startUpperUncertSeg;

        // decide whether to upper left
        if (myWorkingSeg != null) {
            if (myWorkingSeg.maxLessSigmaY(aspectRatio) < endSeg.maxY()) {
                upperUnctEnvelope.lineTo(
                        (float) mapX(minX),
                        (float) mapY(maxY));
            }
        }
        while (myWorkingSeg != null) {
            // check for out of bounds
            double tempX = myWorkingSeg.maxLessSigmaX(aspectRatio);
            double tempY = myWorkingSeg.maxLessSigmaY(aspectRatio);
            double tempXc = myWorkingSeg.controlUpperX(aspectRatio);
            double tempYc = myWorkingSeg.controlUpperY(aspectRatio);
            double tempXr = myWorkingSeg.minLessSigmaX(aspectRatio);
            double tempYr = myWorkingSeg.minLessSigmaY(aspectRatio);

            upperUnctEnvelope.lineTo(
                    (float) mapX(tempX),
                    (float) mapY(tempY));

            if ((pointInViewPort(tempX, tempY, minX, minY, maxX, maxY)) //
                    ||//
                    ((determineAxisIntersectedByUpperEnvelopeLeft(minX, minY, tempX, tempY, tempXr, tempYr) == 1)
                    && pointInViewPort(tempXr, tempYr, minX, minY, maxX, maxY))) {
                upperUnctEnvelope.curveTo(//
                        (float) mapX(tempX),
                        (float) mapY(tempY),
                        (float) mapX(tempXc),
                        (float) mapY(tempYc),
                        (float) mapX(tempXr),
                        (float) mapY(tempYr));

            } else if ((tempX > minX) && (tempY < maxY) && (!pointInViewPort(tempX, tempY, minX, minY, maxX, maxY) //
                    && (!pointInViewPort(tempXr, tempYr, minX, minY, maxX, maxY)))) { //
                if (determineAxisIntersectedByConcordiaRight(maxX, maxY) == -1) {
                    // handle lower right corner
                    upperUnctEnvelope.lineTo(
                            (float) mapX(tempXr),
                            (float) mapY(tempYr));
                }
                upperUnctEnvelope.lineTo(
                        (float) mapX(maxX),
                        (float) mapY(minY));
                // get rid of the rest
                while (myWorkingSeg.getLeftSeg() != null) {
                    myWorkingSeg = myWorkingSeg.getLeftSeg();
                }
            }
            myWorkingSeg = myWorkingSeg.getLeftSeg();
        }
    }

    /**
     * 
     * @param aspectRatio
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public void calcLowerUnctEnvelopeC(double aspectRatio, double minX, double minY, double maxX, double maxY) {

        // parameters represent viewport
        constructLowerEnvelopeAtRightEnd(endSeg, aspectRatio, minX, minY, maxX, maxY);
        constructLowerEnvelopeAtLeftEnd(startSeg, aspectRatio, minX, minY, maxX, maxY);

        lowerUnctEnvelope = new Path2D.Double (Path2D.WIND_NON_ZERO);

        // start at top right of concordia
        lowerUnctEnvelope.moveTo(
                (float) mapX(endSeg.maxX()),
                (float) mapY(endSeg.maxY()));

        ParametricCurveSegmentI myWorkingSeg = startLowerUncertSeg;

        // decide whether to include upper right corner of viewport
        if (myWorkingSeg != null) {
            if (myWorkingSeg.maxPlusSigmaY(aspectRatio) < endSeg.maxY()) {
                lowerUnctEnvelope.lineTo(
                        (float) mapX(maxX),
                        (float) mapY(maxY));
            }
        } else {
            // paint the lower right corner
            lowerUnctEnvelope.lineTo(
                    (float) mapX(maxX),
                    (float) mapY(minY));
        }

        while (myWorkingSeg != null) {
            // check for out of bounds
            double tempX = myWorkingSeg.maxPlusSigmaX(aspectRatio);
            double tempY = myWorkingSeg.maxPlusSigmaY(aspectRatio);
            double tempXc = myWorkingSeg.controlLowerX(aspectRatio);
            double tempYc = myWorkingSeg.controlLowerY(aspectRatio);
            double tempXl = myWorkingSeg.minPlusSigmaX(aspectRatio);
            double tempYl = myWorkingSeg.minPlusSigmaY(aspectRatio);

            lowerUnctEnvelope.lineTo(
                    (float) mapX(tempX),
                    (float) mapY(tempY));

            if (pointInViewPort(tempX, tempY, minX, minY, maxX, maxY) //
                    ||//
                    ((determineAxisIntersectedByLowerEnvelopeRight(maxX, maxY, tempXl, tempYl, tempX, tempY) == 1) //
                    && pointInViewPort(tempXl, tempYl, minX, minY, maxX, maxY))) {

                lowerUnctEnvelope.curveTo(//
                        (float) mapX(tempX),
                        (float) mapY(tempY),
                        (float) mapX(tempXc),
                        (float) mapY(tempYc),
                        (float) mapX(tempXl),
                        (float) mapY(tempYl));
            } else if ((tempX < maxX) && (tempY < maxY) && !pointInViewPort(tempX, tempY, minX, minY, maxX, maxY)//
                    && (!pointInViewPort(tempXl, tempYl, minX, minY, maxX, maxY))) { //
                if (determineAxisIntersectedByConcordiaLeft(minX, minY) == -1) {
                    lowerUnctEnvelope.lineTo(
                            (float) mapX(tempXl),
                            (float) mapY(tempYl));
                }
                lowerUnctEnvelope.lineTo(
                        (float) mapX(minX),
                        (float) mapY(minY));
                // get rid of the rest
                while (myWorkingSeg.getLeftSeg() != null) {
                    myWorkingSeg = myWorkingSeg.getLeftSeg();
                }
            }
            myWorkingSeg = myWorkingSeg.getLeftSeg();
        }
    }

    /**
     * 
     * @param aspectRatio
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public void calcLowerUnctEnvelopeTW(double aspectRatio, double minX, double minY, double maxX, double maxY) {

        // parameters represent viewport
        constructLowerEnvelopeAtRightEnd(startSeg, aspectRatio, minX, minY, maxX, maxY);
        constructLowerEnvelopeAtLeftEnd(endSeg, aspectRatio, minX, minY, maxX, maxY);

        lowerUnctEnvelope = new Path2D.Double (Path2D.WIND_NON_ZERO);

        // start at top right of concordia
        lowerUnctEnvelope.moveTo(
                (float) mapX(startSeg.minX()),
                (float) mapY(startSeg.minY()));

        ParametricCurveSegmentI myWorkingSeg = startLowerUncertSeg;

        // decide whether to include lower right corner of viewport
        if (myWorkingSeg != null) {
            if (myWorkingSeg.minPlusSigmaY(aspectRatio) > startSeg.minY()) {
                lowerUnctEnvelope.lineTo(
                        (float) mapX(maxX),
                        (float) mapY(minY));
            }
        }

        while (myWorkingSeg != null) {
            // check for out of bounds
            double tempX = myWorkingSeg.minPlusSigmaX(aspectRatio);
            double tempY = myWorkingSeg.minPlusSigmaY(aspectRatio);
            double tempXc = myWorkingSeg.controlLowerX(aspectRatio);
            double tempYc = myWorkingSeg.controlLowerY(aspectRatio);
            double tempXl = myWorkingSeg.maxPlusSigmaX(aspectRatio);
            double tempYl = myWorkingSeg.maxPlusSigmaY(aspectRatio);

            lowerUnctEnvelope.lineTo(
                    (float) mapX(tempX),
                    (float) mapY(tempY));

            if (pointInViewPort(tempX, tempY, minX, minY, maxX, maxY) //
                    ||//
                    ((determineAxisIntersectedByLowerEnvelopeRight(maxX, maxY, tempXl, tempYl, tempX, tempY) == 1) //
                    && pointInViewPort(tempXl, tempYl, minX, minY, maxX, maxY))) {

                lowerUnctEnvelope.curveTo(//
                        (float) mapX(tempX),
                        (float) mapY(tempY),
                        (float) mapX(tempXc),
                        (float) mapY(tempYc),
                        (float) mapX(tempXl),
                        (float) mapY(tempYl));
            } else if ((tempX < maxX) && (tempY > minY) && !pointInViewPort(tempX, tempY, minX, minY, maxX, maxY)//
                    && (!pointInViewPort(tempXl, tempYl, minX, minY, maxX, maxY))) { //
                if (determineAxisIntersectedByConcordiaLeft(minX, minY) == -1) {
                    lowerUnctEnvelope.lineTo(
                            (float) mapX(tempXl),
                            (float) mapY(tempYl));
                }
                lowerUnctEnvelope.lineTo(
                        (float) mapX(minX),
                        (float) mapY(maxY));
                // get rid of the rest
                while (myWorkingSeg.getRightSeg() != null) {
                    myWorkingSeg = myWorkingSeg.getRightSeg();
                }

            }
            myWorkingSeg = myWorkingSeg.getRightSeg();
        }
    }

    private double mapX(double x) {
        return graphAxesSetup.mapX(x);
    }

    private double mapY(double y) {
        return graphAxesSetup.mapY(y);
    }

    private void constructUpperEnvelopeAtLeftEnd(//
            ParametricCurveSegmentI myStartUpperUncertSeg, //
            double aspectRatio, //
            double minX, double minY,
            double maxX, double maxY) {

        if (myStartUpperUncertSeg != null) {
            // determine location of bottom end: inside or outside and crossing left or bottom

            //cases: x and y inside box ==> extend line until outside
            //       x is outside, y is inside = crosses left
            //       x is inside, y is outside = crosses bottom
            //       x and y outside ==> shorten line and test again

            // additional case: concordia and upper cross two diff axes

            // goal is to set myEndLowerUncertSeg so that it crosses an axis

            if (myStartUpperUncertSeg instanceof ConcordiaLineSegment) {

                double xLeft = myStartUpperUncertSeg.minLessSigmaX(aspectRatio);
                double yLeft = myStartUpperUncertSeg.minLessSigmaY(aspectRatio);

                if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                    // we create a new segment to the left and try again
                    ParametricCurveSegmentI leftExtensionSegment = //
                            new ConcordiaLineSegment(//
                            myStartUpperUncertSeg.getMinT() - (myStartUpperUncertSeg.getMaxT() - myStartUpperUncertSeg.getMinT()),//
                            myStartUpperUncertSeg.getMinT(), //
                            null, //
                            myStartUpperUncertSeg);

                    constructUpperEnvelopeAtLeftEnd(
                            leftExtensionSegment, aspectRatio, minX, minY, maxX, maxY);
                } else {
                    // we determine if the right end of the segment is inside
                    double xRight = myStartUpperUncertSeg.maxLessSigmaX(aspectRatio);
                    double yRight = myStartUpperUncertSeg.maxLessSigmaY(aspectRatio);

                    int concordiaLeftAxis = //
                            determineAxisIntersectedByConcordiaLeft(minX, minY);
                    int uncertEnvelopeLeftAxis =//
                            determineAxisIntersectedByUpperEnvelopeLeft(minX, minY, xLeft, yLeft, xRight, yRight);

                    if (concordiaLeftAxis == uncertEnvelopeLeftAxis) {
                        if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                            // we are done moving
                            startUpperUncertSeg = myStartUpperUncertSeg;
                        } else {
                            // we move one segment to the right
                            constructUpperEnvelopeAtLeftEnd(myStartUpperUncertSeg.getRightSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use origin of viewport
                        // create a special leftseg
                        ParametricCurveSegmentI leftExtensionSegment = //
                                new ConcordiaLineSegment(//
                                myStartUpperUncertSeg.getMinT() - (myStartUpperUncertSeg.getMaxT() - myStartUpperUncertSeg.getMinT()),//
                                myStartUpperUncertSeg.getMinT(), //
                                null, //
                                myStartUpperUncertSeg);

                        startUpperUncertSeg = leftExtensionSegment;
                    }
                }
            } else {
                // Tera Waserburg 
                double xLeft = myStartUpperUncertSeg.maxLessSigmaX(aspectRatio);
                double yLeft = myStartUpperUncertSeg.maxLessSigmaY(aspectRatio);

                if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                    // we create a new segment to the left and try again
                    ParametricCurveSegmentI leftExtensionSegment = //
                            new TeraWasserburgLineSegment(//
                            myStartUpperUncertSeg.getMaxT(),//
                            myStartUpperUncertSeg.getMaxT() + (myStartUpperUncertSeg.getMaxT() - myStartUpperUncertSeg.getMinT()), //
                            myStartUpperUncertSeg, //
                            null);

                    constructUpperEnvelopeAtLeftEnd(
                            leftExtensionSegment, aspectRatio, minX, minY, maxX, maxY);

                } else {
                    // we determine if the right end of the segment is inside
                    double xRight = myStartUpperUncertSeg.minLessSigmaX(aspectRatio);
                    double yRight = myStartUpperUncertSeg.minLessSigmaY(aspectRatio);

                    int concordiaLeftAxis = //
                            determineAxisIntersectedByConcordiaLeft(minX, minY);
                    int uncertEnvelopeLeftAxis =//
                            determineAxisIntersectedByUpperEnvelopeLeft(minX, minY, xLeft, yLeft, xRight, yRight);

                    if (concordiaLeftAxis == uncertEnvelopeLeftAxis) {
                        if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                            // we are done moving
                            startUpperUncertSeg = myStartUpperUncertSeg;
                        } else {
                            // we move one segment to the right
                            constructUpperEnvelopeAtLeftEnd(myStartUpperUncertSeg.getLeftSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use origin of viewport
                        // create a special leftseg
                        ParametricCurveSegmentI leftExtensionSegment = //
                                new TeraWasserburgLineSegment(//
                                myStartUpperUncertSeg.getMaxT(),//
                                myStartUpperUncertSeg.getMaxT() + (myStartUpperUncertSeg.getMaxT() - myStartUpperUncertSeg.getMinT()), //
                                myStartUpperUncertSeg, //
                                null);

                        startUpperUncertSeg = leftExtensionSegment;
                    }
                }
            }
        }
    }

    private void constructUpperEnvelopeAtRightEnd(//
            ParametricCurveSegmentI myEndUpperUncertSeg, //
            double aspectRatio, //
            double minX, double minY,
            double maxX, double maxY) {

        if (myEndUpperUncertSeg != null) {
            // determine location of top end: inside or outside and crossing top or right

            //cases: x and y inside box ==> extend line until outside
            //       x is outside, y is inside = crosses right
            //       x is inside, y is outside = crosses top
            //       x and y outside ==> shorten line and test again

            // additional case: concordia and upper cross two diff axes

            // goal is to set myEndLowerUncertSeg so that it crosses an axis
            if (myEndUpperUncertSeg instanceof ConcordiaLineSegment) {
                double xRight = myEndUpperUncertSeg.maxLessSigmaX(aspectRatio);
                double yRight = myEndUpperUncertSeg.maxLessSigmaY(aspectRatio);

                if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                    // we create a new segment to the right and try again

                    ParametricCurveSegmentI rightExtensionSegment = //
                            new ConcordiaLineSegment(//
                            myEndUpperUncertSeg.getMaxT(),//
                            myEndUpperUncertSeg.getMaxT() + (myEndUpperUncertSeg.getMaxT() - myEndUpperUncertSeg.getMinT()), //
                            null, //
                            null);

                    //myEndUpperUncertSeg.setRightSeg( rightExtensionSegment );
                    constructUpperEnvelopeAtRightEnd(
                            rightExtensionSegment, aspectRatio, minX, minY, maxX, maxY);


                } else {
                    // we determine if the right end of the segment is inside
                    double xLeft = myEndUpperUncertSeg.minLessSigmaX(aspectRatio);
                    double yLeft = myEndUpperUncertSeg.minLessSigmaY(aspectRatio);
                    int concordiaRightAxis = //
                            determineAxisIntersectedByConcordiaRight(maxX, maxY);
                    int uncertEnvelopeRightAxis =//
                            determineAxisIntersectedByUpperEnvelopeRight(maxX, maxY, xLeft, yLeft, xRight, yRight);

                    if (concordiaRightAxis == uncertEnvelopeRightAxis) {
                        if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                            // we are done moving
                            endUpperUncertSeg = myEndUpperUncertSeg;
                        } else {
                            // we move one segment to the left
                            constructUpperEnvelopeAtRightEnd(myEndUpperUncertSeg.getLeftSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use upper right of viewport
                        // create a special rightSeg

                        ParametricCurveSegmentI rightExtensionSegment =//
                                new ConcordiaLineSegment(//
                                myEndUpperUncertSeg.getMaxT(),//
                                myEndUpperUncertSeg.getMaxT() + (myEndUpperUncertSeg.getMaxT() - myEndUpperUncertSeg.getMinT()), //
                                null, //
                                null);

                        //myEndUpperUncertSeg.setRightSeg( rightExtensionSegment );


//                        if ( pointInViewPort( rightExtensionSegment.maxLessSigmaX( aspectRatio ), //
//                                rightExtensionSegment.maxLessSigmaY( aspectRatio ), //
//                                minX, minY, maxX, maxY * 2 ) ) {
//                            constructUpperEnvelopeAtRightEnd( rightExtensionSegment, aspectRatio, minX, minY, maxX, maxY );
//                        } else {
                        endUpperUncertSeg = rightExtensionSegment;
//                        }
                    }

                }

            } else {
                // T-W
                double xRight = myEndUpperUncertSeg.minLessSigmaX(aspectRatio);
                double yRight = myEndUpperUncertSeg.minLessSigmaY(aspectRatio);

                if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                    // we create a new segment to the right and try again

                    ParametricCurveSegmentI rightExtensionSegment = //
                            new TeraWasserburgLineSegment(//
                            myEndUpperUncertSeg.getMinT() - (myEndUpperUncertSeg.getMaxT() - myEndUpperUncertSeg.getMinT()),//
                            myEndUpperUncertSeg.getMinT(), //
                            null, //
                            null);

                    myEndUpperUncertSeg.setLeftSeg(rightExtensionSegment);
                    constructUpperEnvelopeAtRightEnd(
                            rightExtensionSegment, aspectRatio, minX, minY, maxX, maxY);


                } else {
                    // we determine if the right end of the segment is inside
                    double xLeft = myEndUpperUncertSeg.maxLessSigmaX(aspectRatio);
                    double yLeft = myEndUpperUncertSeg.maxLessSigmaY(aspectRatio);
                    int concordiaRightAxis = //
                            determineAxisIntersectedByConcordiaRight(maxX, maxY);
                    int uncertEnvelopeRightAxis =//
                            determineAxisIntersectedByUpperEnvelopeRight(maxX, maxY, xLeft, yLeft, xRight, yRight);

                    if (concordiaRightAxis == uncertEnvelopeRightAxis) {
                        if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                            // we are done moving
                            endUpperUncertSeg = myEndUpperUncertSeg;
                        } else {
                            // we move one segment to the left
                            constructUpperEnvelopeAtRightEnd(myEndUpperUncertSeg.getRightSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use upper right of viewport
                        // create a special rightSeg

                        ParametricCurveSegmentI rightExtensionSegment = //
                                new TeraWasserburgLineSegment(//
                                myEndUpperUncertSeg.getMinT() - (myEndUpperUncertSeg.getMaxT() - myEndUpperUncertSeg.getMinT()),//
                                myEndUpperUncertSeg.getMinT(), //
                                null, //
                                null);

                        myEndUpperUncertSeg.setLeftSeg(rightExtensionSegment);


                        if (pointInViewPort(rightExtensionSegment.maxLessSigmaX(aspectRatio), //
                                rightExtensionSegment.maxLessSigmaY(aspectRatio), //
                                minX, minY, maxX, maxY * 2)) {
                            constructUpperEnvelopeAtRightEnd(rightExtensionSegment, aspectRatio, minX, minY, maxX, maxY);
                        } else {
                            endUpperUncertSeg = rightExtensionSegment;
                        }
                    }

                }

            }
        }
    }

    private void constructLowerEnvelopeAtRightEnd(//
            ParametricCurveSegmentI myStartLowerUncertSeg, //
            double aspectRatio, //
            double minX, double minY,
            double maxX, double maxY) {

        if (myStartLowerUncertSeg != null) {
            // determine location of bottom end: inside or outside and crossing left or bottom

            //cases: x and y inside box ==> extend line until outside
            //       x is outside, y is inside = crosses left
            //       x is inside, y is outside = crosses bottom
            //       x and y outside ==> shorten line and test again

            // additional case: concordia and upper cross two diff axes

            // goal is to set myEndLowerUncertSeg so that it crosses an axis
            if (myStartLowerUncertSeg instanceof ConcordiaLineSegment) {
                double xRight = myStartLowerUncertSeg.maxPlusSigmaX(aspectRatio);
                double yRight = myStartLowerUncertSeg.maxPlusSigmaY(aspectRatio);

                if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                    // we create a new segment to the right and try again
                    ParametricCurveSegmentI rightExtensionSegment = //
                            new ConcordiaLineSegment(//
                            myStartLowerUncertSeg.getMaxT(),//
                            myStartLowerUncertSeg.getMaxT() + (myStartLowerUncertSeg.getMaxT() - myStartLowerUncertSeg.getMinT()), //
                            myStartLowerUncertSeg, //
                            null);

                    constructLowerEnvelopeAtRightEnd(
                            rightExtensionSegment, aspectRatio, minX, minY, maxX, maxY);
                } else {
                    // we determine if the left end of the segment is inside
                    double xLeft = myStartLowerUncertSeg.minPlusSigmaX(aspectRatio);
                    double yLeft = myStartLowerUncertSeg.minPlusSigmaY(aspectRatio);

                    int concordiaRightAxis = //
                            determineAxisIntersectedByConcordiaRight(maxX, maxY);
                    int uncertEnvelopeRightAxis =//
                            determineAxisIntersectedByLowerEnvelopeRight(maxX, maxY, xLeft, yLeft, xRight, yRight);
                    if (concordiaRightAxis == uncertEnvelopeRightAxis) {
                        if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                            // we are done moving
                            startLowerUncertSeg = myStartLowerUncertSeg;
                        } else {
                            // we move one segment to the left
                            constructLowerEnvelopeAtRightEnd(myStartLowerUncertSeg.getLeftSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use upper right of viewport
                        // create a special rightseg

                        ParametricCurveSegmentI rightExtensionSegment = //
                                new ConcordiaLineSegment(//
                                myStartLowerUncertSeg.getMaxT(),//
                                myStartLowerUncertSeg.getMaxT() + (myStartLowerUncertSeg.getMaxT() - myStartLowerUncertSeg.getMinT()), //
                                myStartLowerUncertSeg, //
                                null);

                        startLowerUncertSeg = rightExtensionSegment;
                    }
                }
            } else {
                // T-W
                double xRight = myStartLowerUncertSeg.minPlusSigmaX(aspectRatio);
                double yRight = myStartLowerUncertSeg.minPlusSigmaY(aspectRatio);
                if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                    // we create a new segment to the right and try again
                    ParametricCurveSegmentI rightExtensionSegment = //
                            new TeraWasserburgLineSegment(//
                            myStartLowerUncertSeg.getMinT() - (myStartLowerUncertSeg.getMaxT() - myStartLowerUncertSeg.getMinT()),//
                            myStartLowerUncertSeg.getMinT(), //
                            null, //
                            myStartLowerUncertSeg);

                    constructLowerEnvelopeAtRightEnd(
                            rightExtensionSegment, aspectRatio, minX, minY, maxX, maxY);
                } else {
                    // we determine if the left end of the segment is inside
                    double xLeft = myStartLowerUncertSeg.maxPlusSigmaX(aspectRatio);
                    double yLeft = myStartLowerUncertSeg.maxPlusSigmaY(aspectRatio);

                    int concordiaRightAxis = //
                            determineAxisIntersectedByConcordiaRight(maxX, maxY);
                    int uncertEnvelopeRightAxis =//
                            determineAxisIntersectedByLowerEnvelopeRight(maxX, maxY, xLeft, yLeft, xRight, yRight);
                    if (concordiaRightAxis == uncertEnvelopeRightAxis) {
                        if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                            // we are done moving
                            startLowerUncertSeg = myStartLowerUncertSeg;
                        } else {
                            // we move one segment to the left
                            constructLowerEnvelopeAtRightEnd(myStartLowerUncertSeg.getRightSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use upper right of viewport
                        // create a special rightseg

                        ParametricCurveSegmentI rightExtensionSegment = //
                                new TeraWasserburgLineSegment(//
                                myStartLowerUncertSeg.getMinT() - (myStartLowerUncertSeg.getMaxT() - myStartLowerUncertSeg.getMinT()),//
                                myStartLowerUncertSeg.getMinT(), //
                                myStartLowerUncertSeg, //
                                null);

                        startLowerUncertSeg = rightExtensionSegment;
                    }
                }
            }
        }
    }

    private void constructLowerEnvelopeAtLeftEnd(//
            ParametricCurveSegmentI myEndLowerUncertSeg, //
            double aspectRatio, //
            double minX, double minY,
            double maxX, double maxY) {

        if (myEndLowerUncertSeg != null) {
            // determine location of bottom end: inside or outside and crossing bottom or left

            //cases: x and y inside box ==> extend line until outside
            //       x is outside, y is inside = crosses left
            //       x is inside, y is outside = crosses bottom
            //       x and y outside ==> shorten line and test again

            // additional case: concordia and upper cross two diff axes

            // goal is to set myEndLowerUncertSeg so that it crosses an axis

            if (myEndLowerUncertSeg instanceof ConcordiaLineSegment) {
                double xLeft = myEndLowerUncertSeg.minPlusSigmaX(aspectRatio);
                double yLeft = myEndLowerUncertSeg.minPlusSigmaY(aspectRatio);

                if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                    // we create a new segment to the left and try again
                    ParametricCurveSegmentI leftExtensionSegment = //
                            new ConcordiaLineSegment(//
                            myEndLowerUncertSeg.getMinT() - (myEndLowerUncertSeg.getMaxT() - myEndLowerUncertSeg.getMinT()),//
                            myEndLowerUncertSeg.getMinT(), //
                            null, //
                            null);

                    myEndLowerUncertSeg.setLeftSeg(leftExtensionSegment);
                    constructLowerEnvelopeAtLeftEnd(
                            leftExtensionSegment, aspectRatio, minX, minY, maxX, maxY);

                } else {
                    // we determine if the right end of the segment is inside
                    double xRight = myEndLowerUncertSeg.maxPlusSigmaX(aspectRatio);
                    double yRight = myEndLowerUncertSeg.maxPlusSigmaY(aspectRatio);
                    int concordiaLeftAxis = //
                            determineAxisIntersectedByConcordiaLeft(minX, minY);
                    int uncertEnvelopeLeftAxis =//
                            determineAxisIntersectedByLowerEnvelopeLeft(minX, minY, xLeft, yLeft, xRight, yRight);

                    if (concordiaLeftAxis == uncertEnvelopeLeftAxis) {
                        if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                            // we are done moving
                            endUpperUncertSeg = myEndLowerUncertSeg;
                        } else {
                            // we move one segment to the right
                            constructLowerEnvelopeAtLeftEnd(myEndLowerUncertSeg.getRightSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use origin of viewport
                        // create a special leftSeg

                        ParametricCurveSegmentI leftExtensionSegment = //
                                new ConcordiaLineSegment(//
                                myEndLowerUncertSeg.getMinT() - (myEndLowerUncertSeg.getMaxT() - myEndLowerUncertSeg.getMinT()),//
                                myEndLowerUncertSeg.getMinT(), //
                                null, //
                                null);

                        myEndLowerUncertSeg.setLeftSeg(leftExtensionSegment);

                        if (pointInViewPort(leftExtensionSegment.minPlusSigmaX(aspectRatio), //
                                leftExtensionSegment.minPlusSigmaY(aspectRatio), //
                                minX, 0 - minY, maxX, maxY)) {
                            constructLowerEnvelopeAtLeftEnd(leftExtensionSegment, aspectRatio, minX, minY, maxX, maxY);

                        } else {
                            endUpperUncertSeg = leftExtensionSegment;
                        }
                    }

                }
            } else {
                // T-W
                double xLeft = myEndLowerUncertSeg.maxPlusSigmaX(aspectRatio);

                double yLeft = myEndLowerUncertSeg.maxPlusSigmaY(aspectRatio);

                if (pointInViewPort(xLeft, yLeft, minX, minY, maxX, maxY)) {
                    // we create a new segment to the left and try again
                    ParametricCurveSegmentI leftExtensionSegment = //
                            new TeraWasserburgLineSegment(//
                            myEndLowerUncertSeg.getMaxT(),//
                            myEndLowerUncertSeg.getMaxT() + (myEndLowerUncertSeg.getMaxT() - myEndLowerUncertSeg.getMinT()), //
                            null, //
                            null);

                    myEndLowerUncertSeg.setRightSeg(leftExtensionSegment);
                    constructLowerEnvelopeAtLeftEnd(
                            leftExtensionSegment, aspectRatio, minX, minY, maxX, maxY);

                } else {
                    // we determine if the right end of the segment is inside
                    double xRight = myEndLowerUncertSeg.minPlusSigmaX(aspectRatio);
                    double yRight = myEndLowerUncertSeg.minPlusSigmaY(aspectRatio);
                    int concordiaLeftAxis = //
                            determineAxisIntersectedByConcordiaLeft(minX, minY);
                    int uncertEnvelopeLeftAxis =//
                            determineAxisIntersectedByLowerEnvelopeLeft(minX, minY, xLeft, yLeft, xRight, yRight);

                    if (concordiaLeftAxis == uncertEnvelopeLeftAxis) {
                        if (pointInViewPort(xRight, yRight, minX, minY, maxX, maxY)) {
                            // we are done moving
                            endUpperUncertSeg = myEndLowerUncertSeg;
                        } else {
                            // we move one segment to the right
                            constructLowerEnvelopeAtLeftEnd(myEndLowerUncertSeg.getRightSeg(), aspectRatio, minX, minY, maxX, maxY);
                        }
                    } else {

                        // we have a split decision and use origin of viewport
                        // create a special leftSeg

                        ParametricCurveSegmentI leftExtensionSegment = //
                                new TeraWasserburgLineSegment(//
                                myEndLowerUncertSeg.getMaxT(),//
                                myEndLowerUncertSeg.getMaxT() + (myEndLowerUncertSeg.getMaxT() - myEndLowerUncertSeg.getMinT()), //
                                null, //
                                null);

                        myEndLowerUncertSeg.setRightSeg(leftExtensionSegment);

                        if (pointInViewPort(leftExtensionSegment.minPlusSigmaX(aspectRatio), //
                                leftExtensionSegment.minPlusSigmaY(aspectRatio), //
                                minX, 0 - minY, maxX, maxY)) {
                            constructLowerEnvelopeAtLeftEnd(leftExtensionSegment, aspectRatio, minX, minY, maxX, maxY);

                        } else {
                            endUpperUncertSeg = leftExtensionSegment;
                        }
                    }

                }

            }
        }
    }

    private int determineAxisIntersectedByConcordiaLeft(double minX, double minY) {

        double slope = startSeg.theSlope(startSeg.getMinT());
        double b = (startSeg.minY() - slope * startSeg.minX());

        int retval = (int) Math.signum(minY - (slope * minX + b));

        return retval;
    }

    private int determineAxisIntersectedByConcordiaRight(double maxX, double maxY) {

        double slope = startSeg.theSlope(endSeg.getMaxT());
        double b = (endSeg.maxY() - slope * endSeg.maxX());

        int retval = (int) Math.signum((slope * maxX + b) - maxY);

        return retval;
    }

    private int determineAxisIntersectedByUpperEnvelopeLeft(//
            double minX, double minY, double xLeft, double yLeft, double xRight, double yRight) {

        // let's determine axis by getting the intercept
        double slope = ((yRight - yLeft) / (xRight - xLeft));
        double b = (yRight - slope * xRight);

        int retval = (int) Math.signum(minY - (slope * minX + b));

        return retval;
    }

    private int determineAxisIntersectedByUpperEnvelopeRight(//
            double maxX, double maxY, double xLeft, double yLeft, double xRight, double yRight) {

        // let's determine axis by getting the intercept
        double slope = ((yRight - yLeft) / (xRight - xLeft));
        double b = (yRight - slope * xRight);

        int retval = (int) Math.signum((slope * maxX + b) - maxY);

        return retval;
    }

    private int determineAxisIntersectedByLowerEnvelopeRight(//
            double maxX, double maxY, double xLeft, double yLeft, double xRight, double yRight) {

        // let's determine axis by getting the intercept
        double slope = ((yRight - yLeft) / (xRight - xLeft));
        double b = (yRight - slope * xRight);

        int retval = (int) Math.signum((slope * maxX + b) - maxY);

        return retval;
    }

    private int determineAxisIntersectedByLowerEnvelopeLeft(//
            double minX, double minY, double xLeft, double yLeft, double xRight, double yRight) {

        // let's determine axis by getting the intercept
        double slope = ((yRight - yLeft) / (xRight - xLeft));
        double b = (yRight - slope * xRight);

        int retval = (int) Math.signum(minY - (slope * minX + b));

        return retval;
    }

    private boolean pointInViewPort(double x, double y, double minX, double minY, double maxX, double maxY) {
        return ((x > minX) && (y > minY) && (x < maxX) && (y < maxY));
    }

    /**
     * 
     * @return
     */
    public ParametricCurveSegmentI getStartSeg() {
        return startSeg;
    }

    /**
     * 
     * @param startSeg
     */
    public void setStartSeg(ParametricCurveSegmentI startSeg) {
        this.startSeg = startSeg;
    }

    /**
     * 
     * @return
     */
    public ParametricCurveSegmentI getEndSeg() {
        return endSeg;
    }

    /**
     * 
     * @param endSeg
     */
    public void setEndSeg(ParametricCurveSegmentI endSeg) {
        this.endSeg = endSeg;
    }

    /**
     * @return the upperUnctEnvelope
     */
    public Path2D getUpperUnctEnvelope() {
        return upperUnctEnvelope;
    }

    /**
     * @param upperUnctEnvelope the upperUnctEnvelope to set
     */
    public void setUpperUnctEnvelope(Path2D upperUnctEnvelope) {
        this.upperUnctEnvelope = upperUnctEnvelope;
    }

    /**
     * @return the lowerUnctEnvelope
     */
    public Path2D getLowerUnctEnvelope() {
        return lowerUnctEnvelope;
    }

    /**
     * @param lowerUnctEnvelope the lowerUnctEnvelope to set
     */
    public void setLowerUnctEnvelope(Path2D lowerUnctEnvelope) {
        this.lowerUnctEnvelope = lowerUnctEnvelope;
    }
}
