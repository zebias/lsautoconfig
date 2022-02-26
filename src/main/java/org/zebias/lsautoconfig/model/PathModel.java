package org.zebias.lsautoconfig.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PathModel {
    private StringProperty path = new SimpleStringProperty();
    private StringProperty pathField = new SimpleStringProperty();

    public String getPathField() {
        return pathField.get();
    }

    public StringProperty pathFieldProperty() {
        return pathField;
    }

    public void setPathField(String pathField) {
        this.pathField.set(pathField);
    }

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }
}
