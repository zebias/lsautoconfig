package org.zebias.lsautoconfig.service;

import javafx.stage.Stage;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class App {
    private static App app = null;
    private App() {
    }
    private Stage stage;

    private int compileCount;

    private int totalCount;

    private String basePath = "D:\\autoConfig\\";

    public synchronized static App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

}
