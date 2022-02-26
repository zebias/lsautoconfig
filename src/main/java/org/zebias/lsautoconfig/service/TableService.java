package org.zebias.lsautoconfig.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.zebias.lsautoconfig.model.Store;
import org.zebias.lsautoconfig.model.TableColumnModel;
import org.zebias.lsautoconfig.model.TableModel;
import org.zebias.lsautoconfig.ui.ProgressStage;

import java.util.List;
import java.util.Optional;

@Slf4j
public class TableService {

    private TableModel model;

    public TableService(TableModel model) {
        this.model = model;
    }

    public void loadTableList(String operatorName) {
        log.info("operatorName: " + operatorName);
        List<Store> stores = ExcelUtil.getReader(App.getInstance().getBasePath()+"tables\\342.xlsx").readAll(Store.class);
        List<Store> stores2 = ExcelUtil.getReader(App.getInstance().getBasePath()+"tables\\02.xlsx").readAll(Store.class);
//        List<Store> storeList = ListUtils.retainAll(stores, stores2);//找出不通元素 相同元素是removeAll
//        model.setTableList(FXCollections.emptyObservableList());
        for (Store store : stores) {
            if(store.getOperator() != null && store.getOperator().equals(operatorName)) {
                // 判断是否存在相同的字段
                if (CollUtil.isNotEmpty(stores2)) {
                    if(stores2.contains(store)){
                        store.setProgress(1);
                        model.getTableList().add(TableColumnModel.fromWork(store));
                    }else{
                        model.getTableList().add(TableColumnModel.fromWork(store));
                    }
                }else{
                    model.getTableList().add(TableColumnModel.fromWork(store));
                }
            }else{
                log.info("不是自己的表，不加载 {}", store);
            }
        }
    }

    public void loadTableList() {
        List<Store> stores = ExcelUtil.getReader(App.getInstance().getBasePath()+"tables\\342.xlsx").readAll(Store.class);
        List<Store> stores2 = ExcelUtil.getReader(App.getInstance().getBasePath()+"tables\\02.xlsx").readAll(Store.class);
//        List<Store> storeList = ListUtils.retainAll(stores, stores2);//找出不通元素 相同元素是removeAll
        for (Store store : stores) {
                // 判断是否存在相同的字段
                if (CollUtil.isNotEmpty(stores2)) {
                    if(stores2.contains(store)){
                        store.setProgress(1);
                        model.getTableList().add(TableColumnModel.fromWork(store));
                    }else{
                        model.getTableList().add(TableColumnModel.fromWork(store));
                    }
                }else{
                    model.getTableList().add(TableColumnModel.fromWork(store));
                }
        }
    }

    public void executeStartWork(Integer id) {
        System.out.println("executeStartWork: " + id);
        if (id == null) {
            return;
        }
        Optional<TableColumnModel> opt = model.getTableList().stream().filter(i -> i.getId() == id).findFirst();
        if (opt.isPresent()) {
            TableColumnModel cm = opt.get();
            ProgressStage.of(App.getInstance().getStage(),new ConfigTask(cm, id),"执行中...").show();
        }
    }

    public void executeStopWork(Integer id) {
        System.out.println("executeStopWork: " + id);
        if (id == null) {
            return;
        }
        model.getTableList().removeIf(i -> i.getId() == id);
    }
}
