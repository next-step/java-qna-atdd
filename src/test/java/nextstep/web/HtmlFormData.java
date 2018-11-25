package nextstep.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HtmlFormData {
    private HttpHeaders headers;
    private MultiValueMap<String, Object> params;

    private HtmlFormData(HttpHeaders headers, MultiValueMap<String, Object> params) {
        this.headers = headers;
        this.params = params;
    }

    public HttpHeaders getHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(headers);
    }

    public MultiValueMap<String, Object> getParams() {
        return params;
    }

    public HttpEntity<MultiValueMap<String, Object>> newHttpEntity() {
        return new HttpEntity<>(params, headers);
    }

    public static HtmlFormDataBuilder urlEncodedFormBuilder() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HtmlFormDataBuilder(headers);
    }

    static class HtmlFormDataBuilder {
        private HttpHeaders headers;
        private MultiValueMap<String, Object> params;

        HtmlFormDataBuilder(HttpHeaders headers) {
            this.headers = headers;
            this.params = new LinkedMultiValueMap<>();
        }

        HtmlFormDataBuilder addParameter(String key, Object value) {
            params.add(key, value);
            return this;
        }

        HtmlFormData build() {
            return new HtmlFormData(headers, params);
        }

    }
}
