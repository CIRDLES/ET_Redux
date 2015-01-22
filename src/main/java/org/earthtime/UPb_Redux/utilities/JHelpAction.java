/* Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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

import javax.help.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import java.io.*;

/**
 * Copyright (c) 2003 Markus Kritzig. All Rights Reserved.
 *
 * Markus Kritzig makes no representations or warranties about the fitness of
 * this software for any particular purpose, including the implied warranty of
 * merchantability.
 */
/**
 * This class provides an
 * <code>AbstractAction</code> that is geared towards supporting the creation
 * and display of
 * <code>JavaHelp</code> sets. It should be constructed via the
 * <code>get*Instance</code> static factory methods that create instances of
 * this class with slightly different behaviour. <p> <b>Usage note:</b><br>
 * Because usually there is only one helpset loaded in an application, the
 * <code>HelpBroker</code> is
 * <code>static</code> and needs to be constructed first. Therefore the
 * <code>startHelpWorker</code> method must be invoked before any help action
 * can be carried out. It takes the name of the helpset as an argument and
 * constructs the
 * <code>HelpBroker</code> in a worker thread. This should be done at program
 * start.
 *
 *
 * @author <a href="mailto:mk@mk-home.de">Markus Kraetzig</a>
 */
public class JHelpAction extends AbstractAction {

    private static String helpSetName = null;
    private static final ActionEvent cshAction =
            new ActionEvent( new JLabel(), ActionEvent.ACTION_PERFORMED, null );
    private static HelpBroker helpBroker = null;
    private static WorkerHelpset worker = null;

    private static class WorkerHelpset extends SwingWorker {

        private static Point loc = new Point( 90, 50 );
        private static Dimension dimension = new Dimension( 1100, 600 );
        private final String helpSetName;
        private HelpBroker mainHelpBroker = null;

        public WorkerHelpset ( String helpSetName ) {
            this.helpSetName = helpSetName;
        }
        // Not to be invoked.

        private WorkerHelpset () {
            this( null );
        }

        public synchronized Object construct () {
            if ( mainHelpBroker == null ) {
                try {
                    // <sven@killig.de>: modified for Web Start
                    URL hsURL = HelpSet.findHelpSet( this.getClass().getClassLoader(), helpSetName );
                    HelpSet mainHelpSet = new HelpSet( null, hsURL );
                    if ( mainHelpSet != null ) {
                        mainHelpBroker = mainHelpSet.createHelpBroker();
                    }
                } catch (Throwable ee) {
                    System.out.println(
                            "\nHelpSet " + helpSetName + " not created.\n" + ee.getMessage() );
                    return null;
                }
                mainHelpBroker.setLocation( loc );
                mainHelpBroker.setSize( dimension );
                //((DefaultHelpBroker)mainHelpBroker).setActivationWindow(modal_dialog); 

            }
            return mainHelpBroker;
        }

        @Override
        public Object get () {
            return construct();
        }
    }

    /**
     * <code>JHelpViewerAction</code> constructor not to be invoked.
     */
    private JHelpAction () {
        super();
    }

    /**
     * <code>JHelpViewerAction</code> constructor, use static factories instead.
     *
     * @param name the name of the action
     */
    public JHelpAction ( String name ) {
        super( name );
    }

    /**
     * <code>JHelpViewerAction</code> constructor, use static factories instead.
     *
     * @param name the name of the action
     * @param icon the icon to display in a component
     */
    protected JHelpAction ( String name, Icon icon ) {
        super( name, icon );
    }

    /**
     * Action method that is invoked, when an action event is catched.
     *
     * @param evt
     * @evt the action event
     * @throws IllegalStateException if <code>HelpBroker</code> was not
     * initialized via <code>startHelpWorker</code> before
     */
    public void actionPerformed ( java.awt.event.ActionEvent evt ) {
        showHelp();
    }

    /**
     * Gets an instance of this class that invokes
     * <code>showHelpFromFocus</code> in its
     * <code>actionPerformed</code> method. When this action is invoked, the
     * helptopic for the component that currently has focus is displayed, given
     * that it has a valid
     * <code>helpID</code>.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getFocusInstance ( String name ) {
        return new JHelpAction( name ) {
            public void actionPerformed ( ActionEvent evt ) {
                showHelpFromFocus();
            }
        };

    }

    /**
     * Same usage as {@link #getFocusInstance(String name)} but with an icon.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     * @param icon the icon to be displayed by components accepting this action
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getFocusInstance ( String name, Icon icon ) {
        return new JHelpAction( name, icon ) {
            public void actionPerformed ( ActionEvent evt ) {
                showHelpFromFocus();
            }
        };
    }

    /**
     * Gets instance of broker object and ensures that it is initialized.
     *
     * @return help broker object or <code>null</code> if it could not be
     * initialized
     * @throws IllegalStateException if <code>HelpBroker</code> was not
     * initialized via <code>startHelpWorker</code> before
     */
    public static HelpBroker getHelpBroker () {
        if ( helpSetName == null ) {
            throw new IllegalStateException( "Name of helpset has not yet been set." );
        }

        if ( helpBroker == null ) {
            Object o = worker.get();
            if ( o instanceof HelpBroker ) {
                helpBroker = (HelpBroker) o;
            }
        }

        // HelpBroker still null, creation failed.
        if ( helpBroker == null ) {
            throw new IllegalStateException( "helpBroker has not been created." );
        }

        return helpBroker;
    }

    /**
     *
     * @param hb
     */
    public static void setHelpBroker ( HelpBroker hb ) {
        helpBroker = hb;
    }

    /**
     * Gets an instance of this class that invokes
     * <code>showHelp</code> in its
     * <code>actionPerformed</code> method.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getShowHelpInstance ( String name ) {
        return new JHelpAction( name );
    }

    /**
     * Same usage as {@link #getShowHelpInstance(String name)} but with an icon.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     * @param icon the icon to be displayed by components accepting this action
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getShowHelpInstance ( String name, Icon icon ) {
        return new JHelpAction( name, icon );
    }

    /**
     * Gets an instance of this class that invokes
     * <code>showHelp(helpID)</code> in its
     * <code>actionPerformed</code> method. When this action is invoked, the
     * helptopic associated with
     * <code>helpID</code> is displayed.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     * @param helpID the identifier for the helptopic to display, must be a
     * valid target, for example <code>html.var_modeling</code>
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getShowIDInstance ( String name, final String helpID ) {
        return new JHelpAction( name ) {
            public void actionPerformed ( ActionEvent evt ) {
                showHelp( helpID );
            }
        };

    }

    /**
     * Same usage as {@link #getShowIDInstance(String name, String helpID)} but
     * with an icon.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     * @param icon the icon to be displayed by components accepting this action
     * @param helpID the identifier for the helptopic to display, must be a
     * valid target, for example <code>html.var_modeling</code>
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getShowIDInstance ( String name, Icon icon, final String helpID ) {
        return new JHelpAction( name, icon ) {
            public void actionPerformed ( ActionEvent evt ) {
                showHelp( helpID );
            }
        };

    }

    /**
     * Gets an instance of this class that invokes
     * <code>trackFieldHelp</code> in its
     * <code>actionPerformed</code> method. When this action is invoked, the
     * helptopic for the component that is clicked on next is displayed, given
     * that it has a valid
     * <code>helpID</code>. The mouse pointer changes during that operation.
     * This is useful for displaying context sensitive help.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getTrackInstance ( String name ) {
        return new JHelpAction( name ) {
            public void actionPerformed ( ActionEvent evt ) {
                trackFieldHelp();
            }
        };

    }

    /**
     * Same usage as {@link #getTrackInstance(String name)} but with an icon.
     *
     * @param name the name of the action, usually displayed as text in
     * components accepting <code>AbstractAction</code> objects
     * @param icon the icon to be displayed by components accepting this action
     *
     * @return initialized action object that displays the helpset set by
     * <code>startHelpWorker</code>
     */
    public static JHelpAction getTrackInstance ( String name, Icon icon ) {
        return new JHelpAction( name, icon ) {
            public void actionPerformed ( ActionEvent evt ) {
                trackFieldHelp();
            }
        };
    }

    /**
     * Shows the helpset that has been initialized via
     * <code>startHelpWorker</code>.
     *
     * @throws IllegalStateException if <code>HelpBroker</code> was not
     * initialized via <code>startHelpWorker</code> before
     */
    public static void showHelp () {
        if ( helpBroker == null ) {
            if ( getHelpBroker() != null ) {
                new CSH.DisplayHelpFromSource( helpBroker ).actionPerformed( cshAction );
            }

        } else {
            helpBroker.setDisplayed( true );
        }
        return;

    }

    /**
     * Shows the topic associated with
     * <code>target</code> in the helpset that has been initialized via
     * <code>startHelpWorker</code>.
     *
     * @param target the help ID to display
     *
     * @throws IllegalStateException if <code>HelpBroker</code> was not
     * initialized via <code>startHelpWorker</code> before
     */
    public static void showHelp ( String target ) {
        showHelp();
        if ( target != null ) {
            try {
                if ( getHelpBroker() != null ) {
                    getHelpBroker().setCurrentID( Map.ID.create( target, helpBroker.getHelpSet() ) );
                }
            } catch (InvalidHelpSetContextException ee) {
                System.out.println( ee.getMessage() );
            }
        }

    }

    /**
     * Shows the topic associated with the component that currently has focus in
     * the helpset that has been initialized via
     * <code>startHelpWorker</code>. If the component does not have a valid help
     * ID, the default topic is displayed.
     *
     * @throws IllegalStateException if <code>HelpBroker</code> was not
     * initialized via <code>startHelpWorker</code> before
     */
    public static void showHelpFromFocus () {
        if ( getHelpBroker() != null ) {
            new CSH.DisplayHelpFromFocus( helpBroker ).actionPerformed( cshAction );
        }

    }

    /**
     * Creates a worker thread to initialize a
     * <code>HelpBroker</code> object for a helpset specified by
     * <code>hSetName</code>. This method should be called at program start to
     * prepare the helpset for display. Usually it should only be called once.
     * All other methods rely on the availability of the
     * <code>HelpBroker</code> object created by this method.
     *
     * @param hSetName the name of the helpset to statically initialize
     * <code>JHelpAction</code> with, must be a valid filename that can be
     * transformed to a <code>URL</code>, usually stated relative to the
     * resource directory of this class
     */
    public static void startHelpWorker ( String hSetName ) {
        helpSetName = hSetName;
        worker = new WorkerHelpset( helpSetName );
        worker.start();

    }

    /**
     * Shows the topic associated with the component that is clicked on with the
     * mouse in the helpset that has been initialized via
     * <code>startHelpWorker</code>. If the component does not have a valid help
     * ID, the default topic is displayed. This operation is useful for
     * implementing context sensitive help.
     *
     * @throws IllegalStateException if <code>HelpBroker</code> was not
     * initialized via <code>startHelpWorker</code> before
     */
    public static void trackFieldHelp () {
        if ( getHelpBroker() != null ) {
            new CSH.DisplayHelpAfterTracking( helpBroker ).actionPerformed( cshAction );
        }

    }
}
