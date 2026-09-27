package web.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web.pages.BaseFunc;
import web.pages.FundingLatviaPage;
import web.pages.HomePage;

import static org.junit.jupiter.api.Assertions.*;

public class LoanCalculatorTests {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private final BaseFunc baseFunc = new BaseFunc();
    private FundingLatviaPage fundingLatviaPage;

    private final String AMOUNT = "100000";
    private final String RATE = "5";
    private final String YEARS = "10";
    private final String MONTHS = "0";
    private final String CHANGED_RATE = "7";
    private final String DECIMAL_RATE = "5.5";
    private final String MONTHS_ONLY_TERM_YEARS = "0";
    private final String MONTHS_ONLY_TERM_MONTHS = "6";
    private final String AMOUNT_ABOVE_MINIMUM = "301";
    private final String MINIMUM_AMOUNT_BOUNDARY = "300";
    private final String ZERO_RATE = "0";
    private final String ZERO_TERM_YEARS = "0";
    private final String ZERO_TERM_MONTHS = "0";
    private final String NON_NUMERIC_INPUT = "abc123abc";
    private final String EXPECTED_ACCEPTED_AMOUNT = "123";
    private final String AMOUNT_MORE_THAN_MAX_LENGTH = "1234567890123456";
    private final int EXPECTED_AMOUNT_MAX_LENGTH = 15;
    private final String YEARS_MORE_THAN_MAX_LENGTH = "123";
    private final String MONTHS_MORE_THAN_MAX_LENGTH = "111";
    private final int EXPECTED_YEARS_MAX_LENGTH = 2;
    private final int EXPECTED_MONTH_MAX_LENGTH = 2;
    private final String DEFAULT_AMOUNT = "100 000";
    private final String DEFAULT_YEARS = "10";
    private final String DEFAULT_MONTHS = "0";
    private final String VARIABLE_SCHEDULE_EXPECTED_FROM = "1 250.00";
    private final String VARIABLE_SCHEDULE_EXPECTED_TO = "836.81";
    private final String EQUAL_SCHEDULE_EXPECTED_RESULT = "1 060.66";


    @BeforeEach
    void openLoanCalculator() {
        fundingLatviaPage = new HomePage(baseFunc)
                .open()
                .acceptCookie()
                .clickPrivate()
                .clickLending()
                .clickMortgageInLatvia();
    }

    @Test
    void loanCalculatorCheckForDefaultValues() {
        LOGGER.info("Verify default fields values on fresh page load");

        assertEquals(DEFAULT_AMOUNT, fundingLatviaPage.getAmountFieldValue());
        assertEquals(DEFAULT_YEARS, fundingLatviaPage.getYearsFieldValue());
        assertEquals(DEFAULT_MONTHS, fundingLatviaPage.getMonthsFieldValue());
        assertTrue(fundingLatviaPage.isVariableScheduleSelected());
        assertTrue(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test
    void loanCalculatorCheckForVariableScheduleCalculation() {
        LOGGER.info("Verify Variable schedule calculation for amount " + AMOUNT + " rate " + RATE + " term " + MONTHS + " months");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectVariableSchedule();
        String result = fundingLatviaPage.getMonthlyRepaymentText();

        assertTrue(result.contains(VARIABLE_SCHEDULE_EXPECTED_FROM));
        assertTrue(result.contains(VARIABLE_SCHEDULE_EXPECTED_TO));
    }

    @Test
    void loanCalculatorCheckForEqualScheduleCalculation() {
        LOGGER.info("Verify Equal schedule calculation for amount " + AMOUNT + " rate " + RATE + " term " + MONTHS + " months");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectEqualSchedule();

        assertEquals(EQUAL_SCHEDULE_EXPECTED_RESULT, fundingLatviaPage.getMonthlyRepaymentText());
    }

    @Test
    void loanCalculatorCheckForRecalculationOnInputChange() {
        LOGGER.info("Verify the result recalculates when the rate changes");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectEqualSchedule();
        String firstResult = fundingLatviaPage.getMonthlyRepaymentText();

        fundingLatviaPage.setRate(CHANGED_RATE);
        String updatedResult = fundingLatviaPage.getMonthlyRepaymentText();

        assertNotEquals(firstResult, updatedResult);
    }

    @Test
    void loanCalculatorCheckForDecimalRate() {
        LOGGER.info("Verify a decimal rate is accepted and produces a result");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(DECIMAL_RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectEqualSchedule();

        assertFalse(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test
    void loanCalculatorCheckForMonthsOnlyTerm() {
        LOGGER.info("Verify if only month entered result is produces");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(RATE)
                .setYears(MONTHS_ONLY_TERM_YEARS)
                .setMonths(MONTHS_ONLY_TERM_MONTHS)
                .selectEqualSchedule();

        assertFalse(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test
    void loanCalculatorCheckForBoundaryAmountJustAboveMinimum() {
        LOGGER.info("Verify amount " + AMOUNT_ABOVE_MINIMUM + " (just above the minimum) produces a result");

        fundingLatviaPage.setAmount(AMOUNT_ABOVE_MINIMUM)
                .setRate(RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectEqualSchedule();

        assertFalse(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test
    void loanCalculatorCheckForMinimumAmountBoundary() {
        LOGGER.info("Verify amount " + MINIMUM_AMOUNT_BOUNDARY + " (the minimum boundary) produces a blank result");

        fundingLatviaPage.setAmount(MINIMUM_AMOUNT_BOUNDARY)
                .setRate(RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectEqualSchedule();

        assertTrue(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test
    void loanCalculatorCheckForZeroRate() {
        LOGGER.info("Verify a zero rate produces a blank result");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(ZERO_RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .selectEqualSchedule();

        assertTrue(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test
    void loanCalculatorCheckForZeroTerm() {
        LOGGER.info("Verify a total zero terms (0 years, 0 months) produces a blank result");

        fundingLatviaPage.setAmount(AMOUNT)
                .setRate(RATE)
                .setYears(ZERO_TERM_YEARS)
                .setMonths(ZERO_TERM_MONTHS)
                .selectEqualSchedule();

        assertTrue(fundingLatviaPage.getMonthlyRepaymentText().isEmpty());
    }

    @Test void loanCalculatorCheckForEmptyAmount() {
        LOGGER.info("Verify an empty amount field produces a blank result");
        fundingLatviaPage.setRate(RATE)
                .setYears(YEARS)
                .setMonths(MONTHS)
                .clearAmount();
        assertTrue(fundingLatviaPage.getMonthlyRepaymentText().isEmpty()); }

    @Test
    void loanCalculatorCheckForNonNumericKeystrokesRejected() {
        LOGGER.info("Verify non-numeric values are rejected in the amount field (only numbers accepted)");

        fundingLatviaPage.setAmount(NON_NUMERIC_INPUT);

        assertEquals(EXPECTED_ACCEPTED_AMOUNT, fundingLatviaPage.getAmountFieldValue());
    }

    @Test
    void loanCalculatorCheckForAmountMaxLength() {
        LOGGER.info("Verify the amount field does not accept more than 15 characters");

        fundingLatviaPage.setAmount(AMOUNT_MORE_THAN_MAX_LENGTH);

        assertEquals(EXPECTED_AMOUNT_MAX_LENGTH, fundingLatviaPage.getAmountFieldValue().length());
    }

    @Test
    void loanCalculatorCheckForYearsMaxLength() {
        LOGGER.info("Verify the years field does not accept more than 2 characters");

        fundingLatviaPage.setYears(YEARS_MORE_THAN_MAX_LENGTH);

        assertEquals(EXPECTED_YEARS_MAX_LENGTH, fundingLatviaPage.getYearsFieldValue().length());
    }

    @Test
    void loanCalculatorCheckForMonthMaxLength() {
        LOGGER.info("Verify the month field does not accept more than 2 characters");

        fundingLatviaPage.setYears(MONTHS_MORE_THAN_MAX_LENGTH);

        assertEquals(EXPECTED_MONTH_MAX_LENGTH, fundingLatviaPage.getYearsFieldValue().length());
    }

    @AfterEach
    public void closeBrowser() {
        baseFunc.closeBrowser();
    }
}
