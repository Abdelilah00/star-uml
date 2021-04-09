package classes;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private String name;
    private List<Attribute> attributes = new ArrayList<>();
    private String package_;

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
