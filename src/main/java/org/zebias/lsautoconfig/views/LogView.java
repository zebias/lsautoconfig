package org.zebias.lsautoconfig.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.Scene;
import javafx.stage.Stage;

@FXMLView(value = "/views/Log.fxml")
public class LogView extends AbstractFxmlView {

    public void start(Stage primaryStage) {
        Scene scene;
        if (getView().getScene() == null) {
            scene = new Scene(getView());
        }else{
            scene = getView().getScene();
        }
        primaryStage.setTitle("日志");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
