package org.zebias.lsautoconfig.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.zebias.lsautoconfig.ui.log.LayoutConsoleLog;
import org.zebias.lsautoconfig.ui.log.TextAreaPrint;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
@Slf4j
@Data
public class LogController implements Initializable {

    @FXML
    private TextArea logText;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logText.setEditable(false);
        log.info("LogController initialize");
        TextAreaPrint textAreaPrint = new TextAreaPrint(logText);
        new LayoutConsoleLog(textAreaPrint);
    }
}
