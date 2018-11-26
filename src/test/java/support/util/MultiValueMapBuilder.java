package support.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MultiValueMapBuilder {
    private MultiValueMap<String, Object> map;

    public MultiValueMapBuilder() {
        this.map = new LinkedMultiValueMap<>();
    }

    public MultiValueMapBuilder add(String key, Object value) {
        map.add(key, value);
        return this;
    }

    public MultiValueMap<String, Object> build(){
        return map;
    }

    public static MultiValueMapBuilder builder() {
        return new MultiValueMapBuilder();
    }
}
