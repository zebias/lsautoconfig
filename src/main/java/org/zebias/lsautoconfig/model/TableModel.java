package org.zebias.lsautoconfig.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TableModel {

    // 表格组合TableColumnModel模型
    private ObservableList<TableColumnModel> tableList = FXCollections.observableArrayList();

    public ObservableList<TableColumnModel> getTableList() {
        return tableList;
    }

    public void setTableList(ObservableList<TableColumnModel> tableList) {
        this.tableList = tableList;
    }
}
