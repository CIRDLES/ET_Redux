/*
 * Thumbnail.java
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
package org.earthtime.UPb_Redux.utilities;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * http://java.sun.com/developer/TechTips/1999/tt1021.html
 *
 * @author James F. Bowring
 */
public class Thumbnail {

    // Aug 2012 for compatibility with Java 1.7 (no sun libraries where possible)
    // from: http://stackoverflow.com/questions/1069095/how-do-you-create-a-thumbnail-image-out-of-a-jpeg-in-java
    private static BufferedImage scale ( BufferedImage source, double ratio ) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage bi = getCompatibleImage( w, h );
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance( xScale, yScale );
        g2d.drawRenderedImage( source, at );
        g2d.dispose();
        return bi;
    }

    private static BufferedImage getCompatibleImage ( int w, int h ) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage( w, h );
        return image;
    }

    /**
     *
     * @param originalImageURI
     * @param maxDimensionOfThumbnail
     * @return
     */
    public static BufferedImage createThumbnailFromImage (
            String originalImageURI, int maxDimensionOfThumbnail) {

        BufferedImage scaledImage = null;
        try {
            BufferedImage bufferedOriginal = ImageIO.read( new File( originalImageURI ) );
            double width = bufferedOriginal.getWidth();
            double height = bufferedOriginal.getHeight();

            double maxDim = Math.max( width, height );
            double scale = maxDimensionOfThumbnail / maxDim;

            scaledImage = scale( bufferedOriginal, scale );

        } catch (IOException iOException) {
        }

        return scaledImage;

    }
}
