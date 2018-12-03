package support.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HtmlFormBuilder {
    private static final String METHOD = "_method";
    private MultiValueMap<String, Object> map;

    public HtmlFormBuilder() {
        this.map = new LinkedMultiValueMap<>();
    }

    public HtmlFormBuilder add(String key, Object value) {
        map.add(key, value);
        return this;
    }

    public MultiValueMap<String, Object> build() {
        return map;
    }

    public static HtmlFormBuilder builder() {
        return new HtmlFormBuilder();
    }

    public static HtmlFormBuilder put() {
        return new HtmlFormBuilder().add(METHOD, "put");
    }

    public static HtmlFormBuilder delete() {
        return new HtmlFormBuilder().add(METHOD, "delete");
    }
}
