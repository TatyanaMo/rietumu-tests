package web.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class BaseFunc {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final WebDriverWait shortWait;

    public BaseFunc() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    public void goToUrl(String url) {
        LOGGER.info("Opening " + url);
        driver.get(url);
    }

    public void click(By locator) {
        wait.until(elementToBeClickable(locator)).click();
    }

    public void type(By locator, String text) {
        wait.until(presenceOfElementLocated(locator)).clear();
        driver.findElement(locator).sendKeys(text);
    }

    public void waitForElement(By locator) {
        wait.until(presenceOfElementLocated(locator));
    }

    public String getText(By locator) {
        return driver.findElement(locator).getText();
    }

    public String getAttribute(By locator, String attributeName) {
        return driver.findElement(locator).getAttribute(attributeName);
    }

    public boolean isElementVisible(By locator) {
        try {
            shortWait.until(visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public WebElement findElement(By locator) {
        return wait.until(presenceOfElementLocated(locator));
    }

    public void closeBrowser() {
        LOGGER.info("Closing browser window");
        if (driver != null) driver.quit();
    }
}
