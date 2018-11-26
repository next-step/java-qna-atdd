package nextstep.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public HtmlFormDataBuilder get() {
        this.params.add("_method", HttpMethod.GET.name());
        return this;
    }

    public HtmlFormDataBuilder post() {
        this.params.add("_method", HttpMethod.POST.name());
        return this;
    }

    public HtmlFormDataBuilder put() {
        this.params.add("_method", HttpMethod.PUT.name());
        return this;
    }

    public HtmlFormDataBuilder delete() {
        this.params.add("_method", HttpMethod.DELETE.name());
        return this;
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
