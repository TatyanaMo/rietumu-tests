package web.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class PrivatePage {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private BaseFunc baseFunc;

    private static final By LENDING_NAV_LINK = By.xpath(".//a[contains(@class, 'header-nav__item') and @href = '/en/person/funding']");

    public PrivatePage(BaseFunc baseFunc) {
        this.baseFunc = baseFunc;
    }

    public LendingPage clickLending() {
        LOGGER.info("Clicking Lending navigation link");
        baseFunc.click(LENDING_NAV_LINK);
        return new LendingPage(baseFunc);
    }
}
