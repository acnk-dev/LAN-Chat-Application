package json.elements;

public class JSONNumber implements JSONElement {
    private Number value;

    public JSONNumber(Number value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
