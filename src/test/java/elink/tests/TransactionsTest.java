package elink.tests;

import elink.base.BaseApiTest;
import elink.config.TestConfig;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TransactionsTest extends BaseApiTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    private static final String CCY = "EUR";
    private static final String DATE_FROM = "2026-08-01";
    private static final String DATE_TILL = "2026-08-31";

    @Test
    void transactionsCheckForActiveTicketAndValidDates() {
        LOGGER.info("This test check successfull response for 'Transactions' function with valid data");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"),CCY,DATE_FROM,DATE_TILL,"EN",null);
        assertThat(jsonCode(response)).isEqualTo("0");
    }

    @Test
    void transactionsCheckForOmittedCurrency() {
        LOGGER.info("This test check successfull response for 'Transactions' function when no currency added to request");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"),null,DATE_FROM,DATE_TILL,"EN", null);
        assertThat(jsonCode(response)).isEqualTo("0");
    }


    @ParameterizedTest(name = "transactionCheckForLanguage_{0}")
    @ValueSource(strings = {"EN", "RU", "LV"})
    void transactionCheckForLanguage(String language) {
        LOGGER.info("This test check successful response  for 'Transactions' function for all allowed languages");
        Response response = elinkClientRequester.transactions(TestConfig.get("ticket.active"),CCY,DATE_FROM,DATE_TILL,language, null);
        assertThat(jsonCode(response)).isEqualTo("0");
    }

    @Test
    void transactionsCheckForMissingTicket() {
        LOGGER.info("This test check negative scenario 'missing required fields' for 'Transactions' function: no ticket added");
        Response response = elinkClientRequester.transactions(null, CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
    }

    @Test
    void transactionsCheckForMissingDateFrom() {
        LOGGER.info("This test check negative scenario 'missing required fields' for 'Transactions' function: no 'date from' added");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, null, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
    }

    @Test
    void transactionsCheckForMissingDateTill() {
        LOGGER.info("This test check negative scenario 'missing required fields' for 'Transactions' function: no 'date till' added");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, DATE_FROM, null, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
    }

    @Test
    void transactionsCheckForInactiveTicket() {
        LOGGER.info("This test check negative scenario 'invalid values' for 'Transactions' function: inactive ticket");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.inactive"), CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("6");
    }

    @Test
    void transactionsCheckForInvalidTicket() {
        LOGGER.info("This test check negative scenario 'invalid values' for 'Transactions' function: invalid ticket");
        Response response = elinkClientRequester.transactions(
                "not-a-real-ticket-12345", CCY, DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("6");
    }

    @Test
    void transactionsCheckForInvalidDateFormat() {
        LOGGER.info("This test check negative scenario 'invalid values' for 'Transactions' function: invalid date format");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, "01-01-2024", DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
    }

    @Test
    void transactionsCheckForInvalidCcyFormat() {
        LOGGER.info("This test check negative scenario 'invalid values' for 'Transactions' function: invalid currency format");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), "EURO", DATE_FROM, DATE_TILL, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("4");
    }

    @Test
    void transactionsCheckForDateRangeValidationNotApplied() {
         /* Sandbox behavior: dateFrom/dateTill ordering is not validated.
         An inverted range (dateFrom after dateTill) still returns code 0 and the fixed dataset rather (not an error).
          */
        LOGGER.info("This test check negative scenario 'invalid values' for 'Transactions' function: incorrect order for date period");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, DATE_TILL, DATE_FROM, "EN", null);
        assertThat(jsonCode(response)).isEqualTo("0");
    }

    @Test
    void transactionsCheckForLanguageValidationNotApplied() {
         /* Sandbox behavior: language is not validated against the documented set (EN/RU/LV).
         An unsupported value like LT is accepted (code 0) and returns English text (not rejected or translated).
          */
        LOGGER.info("This test check negative scenario 'invalid values' for 'Transactions' function: unsupported language");
        Response response = elinkClientRequester.transactions(
                TestConfig.get("ticket.active"), CCY, DATE_FROM, DATE_TILL, "LT", null);
        assertThat(jsonCode(response)).isEqualTo("0");
    }


}

