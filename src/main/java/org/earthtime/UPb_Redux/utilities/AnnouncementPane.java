/*
 * AnnouncementPane.java
 *
 * Created on 30 September 2009
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

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.earthtime.ETRedux;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class AnnouncementPane extends JLayeredPane {

    private final JLabel iconPanel;
    private final JLabel CIRDLESPanel;
    private JPanel messagePanel;

    /**
     *
     */
    public AnnouncementPane() {
        super();
        setOpaque(true);
        setBackground(Color.white);

        // setup CIRDLES icon
        ClassLoader cldr = this.getClass().getClassLoader();
        java.net.URL imageCIRDLESURL = cldr.getResource("org/earthtime/images/cirdles-logo-text-512.png");//cirdles-logo.png");
        CIRDLESPanel = new JLabel();
        CIRDLESPanel.setBackground(Color.red);
        CIRDLESPanel.setBounds(ETReduxFrame.FRAME_WIDTH / 2 - 80, 0, 160, 160);//(50, 50, 128, 128);
        ImageIcon myCIRDLESIcon = new CustomIcon(imageCIRDLESURL);
        ((CustomIcon) myCIRDLESIcon).setSize(CIRDLESPanel.getHeight(), CIRDLESPanel.getWidth());
        CIRDLESPanel.setIcon(myCIRDLESIcon);

        this.add(CIRDLESPanel);

        // setup U-Pb_Redux icon
        cldr = this.getClass().getClassLoader();
        java.net.URL imageReduxURL = cldr.getResource("org/earthtime/UPb_Redux/images/U-Pb_Redux_Icon.png");
        iconPanel = new JLabel();
        iconPanel.setBackground(Color.red);
        iconPanel.setBounds(ETReduxFrame.FRAME_WIDTH / 2 - 128, 225, 256, 256);//(50, 50, 128, 128);
        ImageIcon myReduxIcon = new CustomIcon(imageReduxURL);
        ((CustomIcon) myReduxIcon).setSize(iconPanel.getHeight(), iconPanel.getWidth());
        iconPanel.setIcon(myReduxIcon);

        this.add(iconPanel);

        JLabel versionLabel = new JLabel("Version " + ETRedux.VERSION);
        versionLabel.setBounds(ETReduxFrame.FRAME_WIDTH / 2 - 128, 475, 256, 20);
        versionLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.add(versionLabel);

        JLabel releaseDateLabel = new JLabel(ETRedux.RELEASE_DATE);
        releaseDateLabel.setBounds(ETReduxFrame.FRAME_WIDTH / 2 - 128, 500, 256, 20);
        releaseDateLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        releaseDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.add(releaseDateLabel);

        initializeAnnouncementPane("");

    }

    /**
     *
     * @param message
     */
    public void initializeAnnouncementPane(String message) {

        if (messagePanel != null) {
            remove(messagePanel);
        }

        // Setup message panel
        messagePanel = new MessagePanel();
        messagePanel.setBackground(Color.white);
        messagePanel.setOpaque(false);
        messagePanel.setBounds(200, 1, 750, 200);

        ((MessagePanel) this.messagePanel).setMessage(message);

        this.add(messagePanel, DEFAULT_LAYER);

    }

}
