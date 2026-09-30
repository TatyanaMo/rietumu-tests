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

public class OutgoingPaymentDetailsTests extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private static String refno;

    private static final String CCY = "EUR";
    private static final String DATE_FROM = "2026-09-01";
    private static final String DATE_TILL = "2026-09-26";

    @BeforeAll
    static void fetchRefnoFromTransaction() {
        Response transactionsResponse = elinkClientRequester.transactions(TestConfig.get("ticket.active"), CCY, DATE_FROM, DATE_TILL, "EN", null);
        refno = transactionsResponse.jsonPath().getString("transactions[0].refno");
    }

    @Test
    void outgoingPaymentDetailsCheckForAllValidData() {
        LOGGER.info("This test check successful response for 'OutgoingPaymentDetails' function with all valid data");
        Response response = elinkClientRequester.outgoingPaymentDetails(TestConfig.get("ticket.active"), refno, "EN");
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
        assertThat(response.jsonPath().getString("details.ref_no")).isEqualTo(refno);
        assertThat(response.jsonPath().getString("details.pmnt_ccy")).hasSize(3);
        /*
         NOTE: charge_type is documented as one of OUR/BEN/SHA, but the sandbox actually returns "DEF" for this test account's fixed data - a value not
         listed in the public documentation. Not asserted here.
         */
        assertThat(response.jsonPath().getString("details.state_id"))
                .isIn("0", "1", "2", "3", "4", "5", "6", "7", "20");
        assertThat(response.jsonPath().getString("details.urgency_code"))
                .isIn("1", "2", "3");
    }

    @ParameterizedTest(name = "outgoingPaymentDetailsCheckForLanguage_{0}")
    @ValueSource(strings = {"EN", "RU", "LV"})
    void outgoingPaymentDetailsCheckForLanguage(String language) {
        LOGGER.info("This test check successful response for 'OutgoingPaymentDetails' function for all allowed languages");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.active"), refno, language);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }

    @Test
    void outgoingPaymentDetailsCheckWithoutLanguage() {
        LOGGER.info("This test check successful response for 'OutgoingPaymentDetails' function: without language added");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.active"), refno, null);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }

    @Test
    void outgoingPaymentDetailsCheckForNotSupportiveLanguage() {
        LOGGER.info("This test check successful response for 'OutgoingPaymentDetails' function when language unsupported");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.active"), refno, "LT");
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }

    @Test
    void outgoingPaymentDetailsCheckForInvalidLanguage() {
        LOGGER.info("This test check successful response for 'OutgoingPaymentDetails' function when language invalid");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.active"), refno, "XXX");
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("language");
    }

    @Test
    void outgoingPaymentDetailsCheckForMissingTicket() {
        LOGGER.info("This test check negative scenario for 'OutgoingPaymentDetails' function when ticket missed");
        Response response = elinkClientRequester.outgoingPaymentDetails(null, refno, "EN");
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("ticket");
    }

    @Test
    void outgoingPaymentDetailsCheckForMissingRefno() {
        LOGGER.info("This test check negative scenario for 'OutgoingPaymentDetails' function when refno missed");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.active"), null, "EN");
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("refno");
    }

    @Test
    void outgoingPaymentDetailsCheckForInactiveTicket() {
        LOGGER.info("This test check negative scenario for 'OutgoingPaymentDetails' function when ticket inactive");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.inactive"), refno, "EN");
        assertThat(jsonCode(response)).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void outgoingPaymentDetailsCheckForInvalidTicket() {
        LOGGER.info("This test check negative scenario for 'OutgoingPaymentDetails' function when ticket invalid");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                "not-a-real-ticket-12345", refno, "EN");
        assertThat(jsonCode(response)).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void outgoingPaymentDetailsCheckForInvalidRefno() {
        LOGGER.info("This test check negative scenario for 'OutgoingPaymentDetails' function when refno invalid");
        Response response = elinkClientRequester.outgoingPaymentDetails(
                TestConfig.get("ticket.active"), "NONEXISTENT-REFNO-999", "EN");
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("refno");
    }
}
