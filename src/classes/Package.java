package classes;

import java.util.ArrayList;
import java.util.List;

public class Package {
    private String name;
    private List<Entity> entities = new ArrayList<>();
    private List<Interface> interfaces = new ArrayList<>();

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
