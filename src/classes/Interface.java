package classes;

import java.util.ArrayList;
import java.util.List;

public class Interface {
    private String name;
    private String package_;
    private List<String> values = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
