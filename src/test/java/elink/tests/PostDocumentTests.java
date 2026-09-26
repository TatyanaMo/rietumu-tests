package elink.tests;

import elink.base.BaseApiTest;
import elink.config.TestConfig;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostDocumentTests extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    @Test
    void postDocumentCheckForValidDocument() {
        LOGGER.info("This test check successfull response for 'PostDocument' function with all valid data");
        String initialDoc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(TestConfig.get("ticket.active"),"EN", initialDoc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("refNo")).isNotEmpty();
        assertThat(response.jsonPath().getList("signatureRequired", String.class)).contains("CER");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
    }

}
