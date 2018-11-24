package nextstep.helper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HtmlFormDataBuilder {
    private HttpHeaders httpHeaders;
    private MultiValueMap<String, Object> params;

    private HtmlFormDataBuilder(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
        this.params = new LinkedMultiValueMap<>();
    }

    public HttpHeaders invoke() {
        return new HttpHeaders();
    }

    public HtmlFormDataBuilder addParam(String key, String value) {
        params.add(key, value);
        return this;
    }

    public HttpEntity<MultiValueMap<String, Object>> build() {
        return new HttpEntity<MultiValueMap<String, Object>>(params, httpHeaders);
    }

    public static HtmlFormDataBuilder urlEncodedForm() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HtmlFormDataBuilder(headers);
    }
}
