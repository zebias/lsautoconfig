package org.zebias.lsautoconfig.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@FXMLView(value = "/views/CustomDialog.fxml")
public class CustomDialogView extends AbstractFxmlView {

    public void start(Stage primaryStage) {
        Scene scene;
        if (getView().getScene() == null) {
            scene = new Scene(getView());
        }else{
            scene = getView().getScene();
        }
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT); // 隐藏头标题
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }

    public void close(){
        getView().getScene().getWindow().hide();
    }
}
