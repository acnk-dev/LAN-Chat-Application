package json.elements;

import java.util.ArrayList;
import java.util.List;

public class JSONArray implements JSONElement {
    private List<JSONElement> elements;

    public JSONArray(List<JSONElement> elements) {
        this.elements = elements;
    }

    @Override
    public Object getValue() {
        List<Object> result = new ArrayList<Object>();
        for (JSONElement element : elements) {
            result.add(element.getValue());
        }
        return result;
    }
}
