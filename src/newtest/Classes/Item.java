package newtest.Classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private int idParent;
    private int idOwn;
    private StringProperty name = new SimpleStringProperty();

    public Item (int idParent, int idOwn, String name){
        this.idParent = idParent;
        this.idOwn = idOwn;
        this.name.set(name);
    }
    //---------Setters-----------------

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }

    public void setIdOwn(int idOwn) {
        this.idOwn = idOwn;
    }

    public void setName(String name) {
        this.name.set(name);
    }
    //--------Getters------------------

    public int getIdParent() {
        return idParent;
    }

    public int getIdOwn() {
        return idOwn;
    }

    public String getName() {
        return name.get();
    }
    //---------ToString---------------

    @Override
    public String toString() {
        return getName();
    }
}
