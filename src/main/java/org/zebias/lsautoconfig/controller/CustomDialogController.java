package org.zebias.lsautoconfig.controller;

import cn.hutool.core.io.FileUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.zebias.lsautoconfig.model.PathModel;
import org.zebias.lsautoconfig.service.App;
import org.zebias.lsautoconfig.ui.Toast;
import org.zebias.lsautoconfig.views.CustomDialogView;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
@Slf4j
@Data
public class CustomDialogController implements Initializable {

    @Autowired
    private CustomDialogView customDialogView;

    private PathModel pathModel = new PathModel();
    @FXML
    private TextField pathField;

    @FXML
    private Label path;

    @Autowired
    private IndexController indexController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("CustomDialogController initialize");
        path.textProperty().bindBidirectional(pathModel.pathProperty()); // 双向绑定
        pathField.textProperty().bindBidirectional(pathModel.pathFieldProperty());
        path.setText(App.getInstance().getBasePath());
        path.setTextFill(Color.RED);
        path.setOnMouseClicked(event -> {
            pathField.setText(path.getText());
        }) ;
    }

    @FXML
    public void cancel(ActionEvent event) {
        log.info("CustomDialogController cancel");
        customDialogView.close();
    }

    @FXML
    public void submit(ActionEvent event) {
        log.info("用户输入的值是：{}", pathModel.getPathField());
        /// 判断路径是否符合要求
        if(pathModel.getPathField()==null){
            showAlert("路径不能为空");
            return;
        }
        String pathField = pathModel.getPathField();
        if(!pathField.endsWith("\\")){
            pathField += "\\";
        }
        boolean exist = FileUtil.exist(pathField+"edgedriver_win64\\msedgedriver.exe");
        if(!exist){
            showAlert("路径不存在，请重新输入");
            return;
        }
        pathModel.setPath(pathField);
        App.getInstance().setBasePath(pathField);
        customDialogView.close();
        indexController.init();
        Toast.toast("路径修改成功");
        log.info("CustomDialogController submit");
    }

    private void showAlert(String msg){
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
