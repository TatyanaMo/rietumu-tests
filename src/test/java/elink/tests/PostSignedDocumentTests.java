package elink.tests;

import elink.base.BaseApiTest;
import elink.config.TestConfig;
import elink.signing.XmlDocSigner;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PostSignedDocumentTests extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    private static String refNo;
    private static String signedDoc;

    @BeforeAll
    static void registerGetAndSignDocument() {
        String initialDoc = loadDocument("docs/postDocumentRequest.xml");
        Response postDocResponse = elinkProClientRequester.postDocument(TestConfig.get("ticket.active"), "EN", initialDoc);
        refNo = postDocResponse.jsonPath().getString("refNo");

        Response getDocForSignResponse = elinkProClientRequester.getDocumentForSign(TestConfig.get("ticket.active"), "EN", refNo);
        String unsignedDoc = getDocForSignResponse.jsonPath().getString("doc");

        signedDoc = new XmlDocSigner().sign(unsignedDoc);
    }

    @Test
    void postSignedDocumentCheckValidSignature() {
        LOGGER.info("This test check successful response for 'postSignedDocument' function with all valid data");
        Response response = elinkProClientRequester.postSignedDocument(TestConfig.get("ticket.active"), "EN", refNo, signedDoc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
        assertThat(response.jsonPath().getString("error_message")).isEqualTo("Document executed successfully");
        assertThat(response.jsonPath().getString("refNo").trim()).isEqualTo(refNo);
    }

    @ParameterizedTest(name = "transactionCheckForLanguage_{0}")
    @ValueSource(strings = {"EN", "RU", "LV"})
    void postSignedDocumentCheckForLanguage(String language) {
        LOGGER.info("This test check successful response  for 'postSignedDocument' function for all allowed languages");
        Response response = elinkProClientRequester.postSignedDocument(TestConfig.get("ticket.active"), language, refNo, signedDoc);

        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
        assertThat(response.jsonPath().getString("error_message")).isEqualTo("Document executed successfully");
        assertThat(response.jsonPath().getString("refNo").trim()).isEqualTo(refNo);
    }

    @Test
    void postSignedDocumentCheckForNotSupportiveLanguage() {
        /* Sandbox behavior: language is not validated against the documented set (EN/RU/LV).
         An unsupported value like LT is accepted (code 0) and returns English text (not rejected or translated).
          */
        LOGGER.info("This test check successful response for 'postSignedDocument' function with language not supported");
        Response response = elinkProClientRequester.postSignedDocument(
                TestConfig.get("ticket.active"), "LT", refNo, signedDoc);
        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
        assertThat(response.jsonPath().getString("error_message")).isEqualTo("Document executed successfully");
        assertThat(response.jsonPath().getString("refNo").trim()).isEqualTo(refNo);
    }

    @Test
    void postSignedDocumentCheckForOmittedLanguage() {
        LOGGER.info("This test check successful response for 'postSignedDocument' function when language not added");
        Response response = elinkProClientRequester.postSignedDocument(
                TestConfig.get("ticket.active"), null, refNo, signedDoc);
        assertThat(response.jsonPath().getString("code")).isEqualTo("0");
        assertThat(response.jsonPath().getString("error_code")).isEqualTo("IERR_OK");
        assertThat(response.jsonPath().getString("error_message")).isEqualTo("Document executed successfully");
        assertThat(response.jsonPath().getString("refNo").trim()).isEqualTo(refNo);
    }

    @Test
    void postSignedDocumentCheckForMissingTicket() {
        LOGGER.info("This test check negative scenario for 'postSignedDocument' function when missed ticket");
        Response response = elinkProClientRequester.postSignedDocument(null, "EN", refNo, signedDoc);
        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("ticket");
    }

    @Test
    void postSignedDocumentCheckForInvalidTicket() {
        LOGGER.info("This test check negative scenario for 'postSignedDocument' function when invalid ticket");
        Response response = elinkProClientRequester.postSignedDocument("not-a-real-ticket-12345", "EN", refNo, signedDoc);
        assertThat(response.jsonPath().getString("code")).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void postSignedDocumentCheckForMissingRefNo() {
        LOGGER.info("This test check negative scenario for 'postSignedDocument' function when missed refNo");
        Response response = elinkProClientRequester.postSignedDocument(TestConfig.get("ticket.active"), "EN", null, signedDoc);
        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("refNo");
    }

    @Test
    void postSignedDocumentCheckForMissingDoc() {
        LOGGER.info("This test check negative scenario for 'postSignedDocument' function when missed doc");
        Response response = elinkProClientRequester.postSignedDocument(TestConfig.get("ticket.active"), "EN", refNo, null);
        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("doc");
    }

    @Test
    void postSignedDocumentCheckForInvalidLanguage() {
        LOGGER.info("This test check negative scenario for 'postSignedDocument' function when invalid language");
        Response response = elinkProClientRequester.postSignedDocument(
                TestConfig.get("ticket.active"), "XXX", refNo, signedDoc);
        assertThat(response.jsonPath().getString("code")).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("language");
    }
}

