package web.pages;

import elink.config.TestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class HomePage {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private BaseFunc baseFunc;

    private static final By ACCEPT_COOKIE_BTN = By.xpath(".//a[contains(@class, 'avia-cookie-consent-button') and normalize-space() = 'Accept all']");
    private static final By PRIVATE_NAV_LINK = By.xpath(".//a[@href = '/en/person']");
    private static final By MORTGAGE_IN_LATVIA_CARD = By.xpath(".//a[contains(@class, 'card-item') and @href = '/en/person/funding/funding-latvia']");


    public HomePage(BaseFunc baseFunc) {
        this.baseFunc = baseFunc;
    }

    public HomePage open() {
        baseFunc.goToUrl(TestConfig.get("url"));
        return this;
    }

    public HomePage acceptCookie() {
        LOGGER.info("Accepting cookies");
        baseFunc.click(ACCEPT_COOKIE_BTN);
        return this;
    }

    public PrivatePage clickPrivate() {
        LOGGER.info("Clicking Private navigation link");
        baseFunc.click(PRIVATE_NAV_LINK);
        return new PrivatePage(baseFunc);
    }
}
