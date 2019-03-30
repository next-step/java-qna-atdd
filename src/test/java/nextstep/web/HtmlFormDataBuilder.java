package nextstep.web;

import java.util.Collections;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HtmlFormDataBuilder {

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

  public HttpEntity<MultiValueMap<String, Object>> build() {
    return new HttpEntity<>(params, headers);
  }

  public static HtmlFormDataBuilder urlEncodedForm() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return new HtmlFormDataBuilder(headers);
  }

  public HtmlFormDataBuilder put() {
    this.params.add("_method", "put");
    return this;
  }

  public HtmlFormDataBuilder delete() {
    this.params.add("_method", "delete");
    return this;
  }
}
