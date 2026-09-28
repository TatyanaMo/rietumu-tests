package elink.tests;

import elink.base.BaseApiTest;
import elink.config.TestConfig;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PostDocumentTests extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    @Test
    void postDocumentCheckAllFieldsForValidDocument() {
     /*
    Example of assertions for all response params with data, empty was excluded
    */
        LOGGER.info("This test checks all documented response fields for 'PostDocument' function with valid data");
        String initialDoc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(TestConfig.get("ticket.active"), "EN", initialDoc);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.jsonPath().getString("code")).isEqualTo("0");
            softly.assertThat(response.jsonPath().getString("error")).isEqualTo("");
            softly.assertThat(response.jsonPath().getList("signatureRequired", String.class)).isNotEmpty();
            softly.assertThat(response.jsonPath().getString("refNo")).isNotBlank();
            softly.assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
            softly.assertThat(response.jsonPath().getString("error_message")).isNotBlank();
            softly.assertThat(response.jsonPath().getString("execute_message")).isNotNull();
            softly.assertThat(response.jsonPath().getString("error_field")).isNotNull();
            softly.assertThat(response.jsonPath().getString("error_level")).isEqualTo("0");
        });
    }


    @ParameterizedTest(name = "transactionCheckForLanguage_{0}")
    @ValueSource(strings = {"EN", "RU", "LV"})
    void postDocumentCheckForLanguage(String language) {
        LOGGER.info("This test check successful response  for 'PostDocument' function for all allowed languages");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(
                TestConfig.get("ticket.active"), language, doc);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
    }

    @Test
    void postDocumentCheckForOmittedLanguage() {
        LOGGER.info("This test check successful response for 'PostDocument' function when no language added");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(
                TestConfig.get("ticket.active"), null, doc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
    }

    @Test
    void transactionsCheckForNotSupportiveLanguage() {
         /* Sandbox behavior: language is not validated against the documented set (EN/RU/LV).
         An unsupported value like LT is accepted (code 0) and returns English text (not rejected or translated).
          */
        LOGGER.info("This test check negative scenario 'invalid values' for 'PostDocument' function with language not supported");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(
                TestConfig.get("ticket.active"), "LT", doc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
    }

    @Test
    void postDocumentCheckForInvalidLanguage() {
        LOGGER.info("This test check negative scenario for 'PostDocument' function with not valid language");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(TestConfig.get("ticket.active"), "XXX", doc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("language");
    }


    @Test
    void postDocumentCheckForMissingTicket() {
        LOGGER.info("This test check negative scenario for 'PostDocument' function when ticket missed");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(null, "EN", doc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("ticket");
    }

    @Test
    void postDocumentCheckForInactiveTicket() {
        LOGGER.info("This test check negative scenario for 'PostDocument' function when ticket inactive");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument(TestConfig.get("ticket.inactive"), "EN", doc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void postDocumentCheckForInvalidTicket() {
        LOGGER.info("This test check negative scenario for 'PostDocument' function when ticket invalid");
        String doc = loadDocument("docs/postDocumentRequest.xml");
        Response response = elinkProClientRequester.postDocument("not-valid-ticket-1234", "EN", doc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void postDocumentCheckForMissingDoc() {
        LOGGER.info("This test check negative scenario for 'PostDocument' function when doc missed");
        Response response = elinkProClientRequester.postDocument(
                TestConfig.get("ticket.active"), "EN",null);

        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("doc");
    }

    @Test
    void postDocumentCheckForInvalidDoc() {
        /*
        Sandbox behavior: for invalid ticker returns error code '6', but with comment 'Invalid or inactive ticket.', not 'doc'.
         */
        LOGGER.info("This test check negative scenario for 'PostDocument' function when doc invalid");
        Response response = elinkProClientRequester.postDocument(
                TestConfig.get("ticket.active"), "EN","not-doc-12345333");

        assertThat(response.jsonPath().getString("code")).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }
}
