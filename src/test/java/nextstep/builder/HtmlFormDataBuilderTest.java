package nextstep.builder;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;


public class HtmlFormDataBuilderTest {

    @Test
    public void urlEncodedForm() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("test",111).build();

        assertThat(request.getBody().getFirst("test")).isEqualTo(111);
    }
}