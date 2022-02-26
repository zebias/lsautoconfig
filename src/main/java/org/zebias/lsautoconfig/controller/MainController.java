package org.zebias.lsautoconfig.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@FXMLController
public class MainController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("页面初始化");
    }


    @FXML
    private Label storeId;
    @FXML
    private Label storeName;
    @FXML
    private Label number;
    @FXML
    private Button btn;

    /**
     * fxml add action
     * @param event
     */
    @FXML
    public void startEvent(ActionEvent event) {
        storeId.setText("storeId");
        storeName.setText("storeName");
        number.setText("number");
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("sb字符串后面跟着###");
        Consumer<StringBuilder> consumer = (str) -> str.append("###后面跟着的字符串");
        consumer.accept(sb);
        System.out.println(sb.toString());
    }
}
