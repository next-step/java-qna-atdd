package nextstep.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Collections.singletonList;

public class HtmlFormDataBuilder {

    private final HttpHeaders headers;
    private final MultiValueMap<String, Object> params;

    private HtmlFormDataBuilder(final HttpHeaders headers) {
        this.headers = headers;
        this.params = new LinkedMultiValueMap<>();
    }

    public HtmlFormDataBuilder addParameter(final String key, final String value) {
        this.params.add(key, value);
        return this;
    }

    public HttpEntity<MultiValueMap<String, Object>> build() {
        return new HttpEntity<>(params, headers);
    }

    public static HtmlFormDataBuilder urlEncodedForm() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HtmlFormDataBuilder(headers);
    }

}
