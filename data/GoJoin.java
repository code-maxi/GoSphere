package data;

import java.io.Serializable;

public class GoJoin implements Serializable {
    public final String name;
    public final int id;
    public GoJoin(String name, int id) {
        this.name = name;
        this.id = id;
    }
    public String toString() {
        return "GoJoin[name=" + name + ", id=" + id + "]";
    }
}
