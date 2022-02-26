package org.zebias.lsautoconfig.model;

import javafx.beans.property.*;

public class TableColumnModel {

    private Store store;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty operator = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final IntegerProperty result = new SimpleIntegerProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty iccid = new SimpleStringProperty();
    private final StringProperty mac = new SimpleStringProperty();

    public static TableColumnModel fromWork(Store store) {
        TableColumnModel model = new TableColumnModel();
        model.store = store;
        model.setId(store.getId());
        model.setName(store.getName());
        model.setProgress(store.getProgress());
        model.setResult(store.getResult());
        model.setOperator(store.getOperator());
        model.setType(store.getType());
        model.setAddress(store.getAddress());
        model.setIccid(store.getIccid());
        model.setMac(store.getMac());
        return model;
    }

    public String getMac() {
        return mac.get();
    }

    public StringProperty macProperty() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac.set(mac);
    }

    public String getAddress() {
        return address.get();
    }

    public String getIccid() {
        return iccid.get();
    }

    public StringProperty iccidProperty() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid.set(iccid);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getOperator() {
        return operator.get();
    }

    public StringProperty operatorProperty() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator.set(operator);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public int getResult() {
        return result.get();
    }

    public IntegerProperty resultProperty() {
        return result;
    }

    public void setResult(int result) {
        this.result.set(result);
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }
}
