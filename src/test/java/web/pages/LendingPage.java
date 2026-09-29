package web.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class LendingPage {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private BaseFunc baseFunc;

    private static final By MORTGAGE_IN_LATVIA_CARD = By.xpath(".//a[contains(@class, 'card-item') and @href = '/en/person/funding/funding-latvia']");

    public LendingPage(BaseFunc baseFunc) {
        this.baseFunc = baseFunc;
    }

    public FundingLatviaPage clickMortgageInLatvia() {
        LOGGER.info("Clicking Mortgage in Latvia page");
        baseFunc.click(MORTGAGE_IN_LATVIA_CARD);
        return new FundingLatviaPage(baseFunc);
    }
}
