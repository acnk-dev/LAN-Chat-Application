package json.elements;

public class JSONBoolean implements JSONElement {
    private Boolean value;

    public JSONBoolean(Boolean value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }
}
