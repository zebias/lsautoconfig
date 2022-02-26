package org.zebias.lsautoconfig.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogModel {
    private StringProperty log = new SimpleStringProperty();

    public String getLog() {
        return log.get();
    }

    public StringProperty logProperty() {
        return log;
    }

    public void setLog(String log) {
        this.log.set(log);
    }
}
