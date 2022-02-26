package org.zebias.lsautoconfig;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zebias.lsautoconfig.service.App;
import org.zebias.lsautoconfig.views.IndexView;
import org.zebias.lsautoconfig.views.SplashView;

@SpringBootApplication
public class LsAutoconfigApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(LsAutoconfigApplication.class, IndexView.class, new SplashView() ,args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.setTitle("蒲公英自动配置工具 - V0.6");
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            App.getInstance().getStage().close();
            System.exit(0);
        }) ;
        App.getInstance().setStage(stage);
//        stage.show();
    }
}
