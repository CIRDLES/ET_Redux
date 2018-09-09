package org.earthtime.plots.topsoil;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

public class PlaceHolderForJavaFxThread extends JLayeredPane {

    public PlaceHolderForJavaFxThread() {
    }

    public void runme() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI();
            }
        });
    }

    private void initAndShowGUI() {
        JFXPanel fxPanel = new JFXPanel();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(null);
            }
        });
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Stage topsoilPlotWindow = new Stage(StageStyle.DECORATED);
        topsoilPlotWindow.setMaxWidth(0);
        topsoilPlotWindow.setMaxHeight(0);
        topsoilPlotWindow.setResizable(false);
        topsoilPlotWindow.show();

    }
}
