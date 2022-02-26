package org.zebias.lsautoconfig.service;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.zebias.lsautoconfig.main.MainController;
import org.zebias.lsautoconfig.model.TableColumnModel;

@Slf4j
public class ConfigTask extends Task<Void> {

    private TableColumnModel cm;
    private Integer id;

    public ConfigTask(TableColumnModel cm, Integer id) {
        this.cm = cm;
        this.id = id;
    }

    @Override
    protected Void call() throws Exception {
        cm.setProgress(0);
        new MainController().start(cm);
        cm.setProgress(1);
        return null;
    }
}
