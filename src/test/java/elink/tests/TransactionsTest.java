package elink.tests;

import elink.base.BaseApiTest;
import elink.config.TestConfig;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

public class TransactionsTest extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    private static final String CCY = "EUR";
    private static final String DATE_FROM = "2026-09-01";
    private static final String DATE_TILL = "2026-09-26";

    @Test
    void transactionsCheckAllFieldsForValidRequest() {
    /*
    Example of assertions for all response params with data (for one transaction), empty was excluded
    */
        LOGGER.info("This test checks all documented response fields for 'Transactions' function with valid data");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"), CCY, DATE_FROM, DATE_TILL, "EN", null);

        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getList("transactions")).isNotEmpty();

        String path = "transactions[0].";

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.jsonPath().getString(path + "uniqueID")).isNotBlank();
            softly.assertThat(response.jsonPath().getString(path + "trnID")).isNotBlank();
            softly.assertThatCode(() -> {
                        LocalDate parsedDate = LocalDate.parse(response.jsonPath().getString(path + "date"));
                    })
                    .as("date should be a valid calendar date in YYYY-MM-DD format") .doesNotThrowAnyException();
            softly.assertThat(response.jsonPath().getString(path + "refno")).isNotBlank();
            softly.assertThat(response.jsonPath().getString(path + "narrative")).isNotNull();
            softly.assertThat(response.jsonPath().getString(path + "amount")).matches("-?\\d+(\\.\\d+)?");
            softly.assertThat(response.jsonPath().getString(path + "currency")).matches("[A-Z]{3}");
            softly.assertThat(response.jsonPath().getString(path + "saldo")).matches("-?\\d+(\\.\\d+)?");
            softly.assertThat(response.jsonPath().getString(path + "trndesc")).isNotBlank();
            softly.assertThat(response.jsonPath().getString(path + "tcf")).matches("[YN]");
        });
    }

    @Test
    void transactionsCheckForAllValidData() {
        LOGGER.info("This test check successful response for 'Transactions' function with all valid data");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"), CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getList("transactions")).isNotEmpty();
        assertThat(response.jsonPath().getString("transactions[0].currency")).hasSize(3);
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }

    @Test
    void transactionsCheckForOmittedCurrency() {
        LOGGER.info("This test check successful response for 'Transactions' function when currency null");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"), null, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }


    @ParameterizedTest(name = "transactionCheckForLanguage_{0}")
    @ValueSource(strings = {"EN", "RU", "LV"})
    void transactionCheckForLanguage(String language) {
        LOGGER.info("This test check successful response  for 'Transactions' function for all allowed languages");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"), CCY, DATE_FROM, DATE_TILL, language, null);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }

    @Test
    void transactionsCheckForMissingTicket() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when ticket missed");
        Response response = elinkClientRequester.transactions(null, CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("ticket");
    }

    @Test
    void transactionsCheckForMissingDateFrom() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when 'date from' missed");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, "", DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("dateFrom");
    }

    @Test
    void transactionsCheckForMissingDateTill() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when 'date till' missed");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, DATE_FROM, "", "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("dateTill");
    }

    @Test
    void transactionsCheckForInactiveTicket() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when ticket inactive");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.inactive"), CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void transactionsCheckForInvalidTicket() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when ticket invalid");
        Response response = elinkClientRequester.transactions(
                "not-a-real-ticket-12345", CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("6");
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid or inactive ticket.");
    }

    @Test
    void transactionsCheckForInvalidDateFormat() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when date format invalid");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, "01-01-2024", DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("dateFrom");
    }

    @Test
    void transactionsCheckForInvalidCcyFormat() {
        LOGGER.info("This test check negative scenario for 'Transactions' function when currency invalid");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), "EURO", DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
        assertThat(response.jsonPath().getString("error")).isEqualTo("ccy");
    }

    @Test
    void transactionsCheckForDateRangeValidationNotApplied() {
         /* Sandbox behavior: dateFrom/dateTill ordering is not validated.
         An inverted range (dateFrom after dateTill) still returns code 0 and the fixed dataset rather (not an error).
          */
        LOGGER.info("This test check negative scenario for 'Transactions' function when order for date period incorrect");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, DATE_TILL, DATE_FROM, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }

    @Test
    void transactionsCheckForLanguageValidationNotApplied() {
         /* Sandbox behavior: language is not validated against the documented set (EN/RU/LV).
         An unsupported value like LT is accepted (code 0) and returns English text (not rejected or translated).
          */
        LOGGER.info("This test check negative scenario for 'Transactions' function when language unsupported");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, DATE_FROM, DATE_TILL, "LT", null);
        assertThat(jsonCode(response)).isEqualTo("0");
        assertThat(response.jsonPath().getString("error")).isEqualTo("");
    }
}

