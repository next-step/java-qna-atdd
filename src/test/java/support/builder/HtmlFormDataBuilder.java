package support.builder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HtmlFormDataBuilder {
    private static final String _METHOD = "_method";

    private static final String PUT = "put";
    private static final String DELETE = "delete";

    private HttpHeaders headers;
    private MultiValueMap<String, Object> params;

    private HtmlFormDataBuilder(HttpHeaders headers) {
        this.headers = headers;
        this.params = new LinkedMultiValueMap<>();
    }

    public HtmlFormDataBuilder addParameter(String key, Object value) {
        this.params.add(key, value);
        return this;
    }

    public HtmlFormDataBuilder put() {
        return addParameter(_METHOD, PUT);
    }

    public HtmlFormDataBuilder delete() {
        return addParameter(_METHOD, DELETE);
    }

    public HttpEntity<MultiValueMap<String, Object>> build() {
        return new HttpEntity<>(params, headers);
    }

    public static HtmlFormDataBuilder urlEncodedForm() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HtmlFormDataBuilder(headers);
    }

}