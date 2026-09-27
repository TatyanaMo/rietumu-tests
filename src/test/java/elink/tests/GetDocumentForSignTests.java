package elink.tests;

import elink.base.BaseApiTest;
import elink.config.TestConfig;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class GetDocumentForSignTests extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    private static String refNo;

    @BeforeAll
    static void registerDocumentAndFetchRefNo() {
        String initialDoc =loadDocument("docs/postDocumentRequest.xml");
        Response postDocResponse = elinkProClientRequester.postDocument(TestConfig.get("ticket.active"), "EN", initialDoc);
        refNo = postDocResponse.jsonPath().getString("refNo");
    }

    @Test
    void getDocumentForSignCheckForValidData() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function with all valid data");
        Response response = elinkProClientRequester.getDocumentForSign(TestConfig.get("ticket.active"), "EN", refNo);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getList("signatureRequired", String.class)).contains("CER");
        assertThat(response.jsonPath().getString("doc")).contains("<RBdocument");
        assertThat(response.jsonPath().getString("doc")).contains(refNo);
        assertThat(response.jsonPath().getString("status")).isEqualTo("20");
        assertThat(response.jsonPath().getString("state")).isEqualTo("Waiting for signature");
    }

    @ParameterizedTest(name = "transactionCheckForLanguage_{0}")
    @ValueSource(strings = {"EN", "RU", "LV"})
    void getDocumentForSignCheckForLanguage(String language) {
        LOGGER.info("This test check successful response  for 'GetDocumentForSign' function for all allowed languages");
        Response response = elinkProClientRequester.getDocumentForSign(TestConfig.get("ticket.active"), language, refNo);
        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getList("signatureRequired", String.class)).contains("CER");
        assertThat(response.jsonPath().getString("doc")).contains("<RBdocument");
        assertThat(response.jsonPath().getString("doc")).contains(refNo);
        assertThat(response.jsonPath().getString("status")).isEqualTo("20");
        assertThat(response.jsonPath().getString("state")).isEqualTo("Waiting for signature");
    }

    @Test
    void getDocumentForSignCheckForOmittedLanguage() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when language not added");
        Response response = elinkProClientRequester.getDocumentForSign(TestConfig.get("ticket.active"), "XXX", refNo);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getList("signatureRequired", String.class)).contains("CER");
        assertThat(response.jsonPath().getString("doc")).contains("<RBdocument");
        assertThat(response.jsonPath().getString("doc")).contains(refNo);
        assertThat(response.jsonPath().getString("status")).isEqualTo("20");
        assertThat(response.jsonPath().getString("state")).isEqualTo("Waiting for signature");
    }


    @Test
    void getDocumentForSignCheckForInvalidLanguage() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when invalid language");
        Response response = elinkProClientRequester.getDocumentForSign(
                TestConfig.get("ticket.active"), "XXX", refNo);

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("language");
    }

    @Test
    void getDocumentForSignCheckForInvalidRefNo() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when invalid refNo");
        Response response = elinkProClientRequester.getDocumentForSign(
                TestConfig.get("ticket.active"), "EN", "HVII270");

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("refNo");
    }

    @Test
    void getDocumentForSignCheckForMissingTicket() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when missed ticket");
        Response response = elinkProClientRequester.getDocumentForSign(null, "EN", refNo);

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("ticket");
    }

    @Test
    void getDocumentForSignCheckForInvalidTicket() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when invalid ticket");
        Response response = elinkProClientRequester.getDocumentForSign(
                "not-a-real-ticket-12345", "EN", refNo);

        assertThat(response.jsonPath().getString("code")).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void getDocumentForSignCheckForInactiveTicket() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when inactive ticket");
        Response response = elinkProClientRequester.getDocumentForSign(
                "not-a-real-ticket-12345", "EN", refNo);

        assertThat(response.jsonPath().getString("code")).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void getDocumentForSignCheckForMissingRefNo() {
        LOGGER.info("This test check successful response for 'GetDocumentForSign' function when missed refNo");
        Response response = elinkProClientRequester.getDocumentForSign(TestConfig.get("ticket.active"), "EN", null);

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("refNo");
    }
}

