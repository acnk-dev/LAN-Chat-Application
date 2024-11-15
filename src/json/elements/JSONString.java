package json.elements;

public class JSONString implements JSONElement {
    private String value;

    public JSONString(String value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }
}
