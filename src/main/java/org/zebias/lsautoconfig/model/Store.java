package org.zebias.lsautoconfig.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Store {

    private int id;
    private String name;
    private int result;
    private double progress;
    private List<String> pass;
    private String operator;
    private String type;
    private String address;
    private String iccid;
    private String mac;

    public Store(int id, String name, List<String> pass) {
        this.id = id;
        this.name = name;
        this.pass = pass;
    }


    public Store(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Store(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }else {
            if(this.getClass() == obj.getClass()){
                if(this.getId() == ((Store) obj).getId()){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, result, progress);
    }
}
