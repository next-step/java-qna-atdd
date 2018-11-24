package support.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

/**
 * Created by hspark on 24/11/2018.
 */
public class HtmlFormDataBuilder {
	private static final String METHOD_PARAM = "_method";
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

	public HtmlFormDataBuilder setMethod(HttpMethod value) {
		this.params.add(METHOD_PARAM, value.name());
		return this;
	}

	public HttpEntity<MultiValueMap<String, Object>> build() {
		return new HttpEntity<MultiValueMap<String, Object>>(params, headers);
	}

	public static HtmlFormDataBuilder urlEncodedForm() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HtmlFormDataBuilder(headers);
	}
}
