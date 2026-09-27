package web.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class FundingLatviaPage {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private BaseFunc baseFunc;

    private static final By AMOUNT_INPUT = By.id("summa");
    private static final By YEARS_INPUT = By.id("period1");
    private static final By MONTHS_INPUT = By.id("period");
    private static final By RATE_INPUT = By.id("rate");
    private static final By VARIABLE_SCHEDULE_RADIO = By.xpath(".//input[@name = 'type' and @value = '1']");
    private static final By EQUAL_SCHEDULE_RADIO = By.xpath(".//input[@name = 'type' and @value = '2']");
    private static final By MONTHLY_REPAYMENT_RESULT = By.id("ikmenesi");

    public FundingLatviaPage(BaseFunc baseFunc) {
        this.baseFunc = baseFunc;
    }

    public FundingLatviaPage setAmount(String amount) {
        LOGGER.info("Setting loan amount to " + amount);
        baseFunc.type(AMOUNT_INPUT, amount);
        return this;
    }

    public FundingLatviaPage setYears(String years) {
        LOGGER.info("Setting years to " + years);
        baseFunc.type(YEARS_INPUT, years);
        return this;
    }

    public FundingLatviaPage setMonths(String months) {
        LOGGER.info("Setting months to " + months);
        baseFunc.type(MONTHS_INPUT, months);
        return this;
    }

    public FundingLatviaPage setRate(String rate) {
        LOGGER.info("Setting annual interest rate to " + rate);
        baseFunc.type(RATE_INPUT, rate);
        return this;
    }

    public FundingLatviaPage selectVariableSchedule() {
        LOGGER.info("Selecting Variable repayment schedule");
        baseFunc.click(VARIABLE_SCHEDULE_RADIO);
        return this;
    }

    public FundingLatviaPage selectEqualSchedule() {
        LOGGER.info("Selecting Equal repayment schedule");
        baseFunc.click(EQUAL_SCHEDULE_RADIO);
        return this;
    }

    public String getMonthlyRepaymentText() {
        return baseFunc.getText(MONTHLY_REPAYMENT_RESULT);
    }

    public String getAmountFieldValue() {
        return baseFunc.getAttribute(AMOUNT_INPUT, "value");
    }

    public String getYearsFieldValue() {
        return baseFunc.getAttribute(YEARS_INPUT, "value");
    }

    public String getMonthsFieldValue() {
        return baseFunc.getAttribute(MONTHS_INPUT, "value");
    }

    public boolean isVariableScheduleSelected() {
        return baseFunc.getAttribute(VARIABLE_SCHEDULE_RADIO, "checked") != null;
    }

    public FundingLatviaPage clearAmount() {
        LOGGER.info("Clearing the amount field via keyboard input");

        WebElement field = baseFunc.findElement(AMOUNT_INPUT);
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

        return this;
    }
}
