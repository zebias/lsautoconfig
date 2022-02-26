package org.zebias.lsautoconfig.service;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.zebias.lsautoconfig.constact.Event;
import org.zebias.lsautoconfig.model.Store;

import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class TableColumnBuilder {

    public static TableColumn textColumn(String text, String field, int width) {
        TableColumn column = new TableColumn();
        column.setText(text);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory(field));
        return column;
    }

    public static TableColumn textColumnByIccid(String text, String field, int width) {
        TableColumn column = new TableColumn();
        column.setText(text);
        column.setPrefWidth(width);
        column.setEditable(true);
        log.info("获取到iccid值是：{}， field: {}", text, field);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(new PropertyValueFactory(field));

        return column;
    }

    public static TableColumn textColumnByMac(String text, String field, int width) {
        TableColumn column = new TableColumn();
        column.setText(text);
        column.setPrefWidth(width);
        column.setEditable(true);
        log.info("获取到mac值是：{}， field: {}", text, field);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(new PropertyValueFactory(field));

        return column;
    }

    public static TableColumn textColumnByType(String text, String field, int width) {
        TableColumn column = new TableColumn();
        column.setText(text);
        //TODO 未生效
//        if(text.contains("BOE")){
//            column.setStyle("-fx-fill: red;");
//        }
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory(field));
        return column;
    }

    public static TableColumn progressColumn(String text, String field, int width) {
        TableColumn column = new TableColumn();
        column.setText(text);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory(field));
        column.setCellFactory(v -> new TableCell<Object, Double>() {
            private HBox hBox = new HBox();
            private Label progressLabel = new Label("0% ");
            private ProgressBar progressBar = new ProgressBar();
            {
                progressLabel.setPrefWidth(50);
                progressLabel.setAlignment(Pos.CENTER_RIGHT);
                hBox.getChildren().addAll(progressBar, progressLabel);
            }
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(item);
                    progressLabel.setText((int) ((item * 100)) + "% ");
                    setGraphic(hBox);
                }
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        });
        return column;
    }

    public static TableColumn operatorColumn(String text, String field, int width, BiConsumer<Event, Integer> consumer, List<Store> list) {
        TableColumn column = new TableColumn();
        column.setText(text);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory(field));
        column.setCellFactory(v -> new TableCell<Object, Integer>() {
            private HBox hBox = new HBox();
            private Button button1 = new Button("启动");
            private Integer value;
            {
                button1.styleProperty().set("-fx-padding: 2px 10px");
                // 获取行数据
                button1.setOnAction(e->consumer.accept(Event.START, value));
                hBox.setAlignment(Pos.CENTER);
                hBox.getChildren().addAll(button1);
            }
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
//                    log.info("empty item: {}", item);
                    button1.setId(String.valueOf(value));
                } else {
//                    log.info("执行触发了： item: {}", item);
                    if (list.contains(new Store(item))) {
                        button1.setText("已完成");
                        button1.setDisable(true);
                    }else{
                        button1.setDisable(false);
                        button1.setText("启动");
                    }
                    this.value = item;
                    setGraphic(hBox);
                }
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        });
        return column;
    }
}
