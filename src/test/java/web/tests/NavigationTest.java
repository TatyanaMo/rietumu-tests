package web.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import web.pages.BaseFunc;
import web.pages.FundingLatviaPage;
import web.pages.HomePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigationTest {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private final BaseFunc baseFunc = new BaseFunc();

    @Test
    public void navigateFromHomepageToLoanCalculatorCheck() {
        LOGGER.info("Navigate Homepage -> Private -> Lending -> Mortgage in Latvia");
        FundingLatviaPage fundingLatviaPage = new HomePage(baseFunc)
                .open()
                .acceptCookie()
                .clickPrivate()
                .clickLending()
                .clickMortgageInLatvia();

        LOGGER.info("Verify the Loan calculator page was reached");
        assertTrue(baseFunc.getCurrentUrl().endsWith("/en/person/funding/funding-latvia"), "Did not land on the Loan calculator page");
    }

    @AfterEach
    public void closeBrowser() {
        baseFunc.closeBrowser();
    }
}
