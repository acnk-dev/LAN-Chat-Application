package json.elements;

import java.util.HashMap;
import java.util.Map;

public class JSONObject implements JSONElement {
    private Map<String, JSONElement> properties;

    public JSONObject(Map<String, JSONElement> properties) {
        this.properties = properties;
    }

    @Override
    public Object getValue() {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, JSONElement> entry : properties.entrySet()) {
            JSONElement value = entry.getValue();
            result.put(entry.getKey(), value == null ? null : value.getValue());
        }
        return result;
    }
}
