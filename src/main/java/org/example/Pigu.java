package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class Pigu {

    public static WebDriver browser; // Declare Webdriver as a class-level variable
//    Universalus kintamasis
    public static final String SEARCH_KEYWORD = "Televizoriai";
    public static final int SECONDS_TO_WAIT_FOR_A_ELEMENT = 5;
    public static void setUp() {
        //        Instructing what kind of browser system is going to use & navigating for drivers
        System.out.println("Pigu.lt Selenium + Maven testing + JUnit");
        System.setProperty(
                "webdriver.chrome.driver",
                "drivers/chromedriver124.exe"
        );

        //        Browser settings
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--ignore-certificate-errors");

//        New object is being created & given URL to open
        browser = new ChromeDriver(chromeOptions);
        browser.get("https://pigu.lt");
    }
    public static void closeCookies() {
        //        Locating an element by given class
        WebElement declineCookies = browser.findElement(By.className("c-link"));

//        Gives an input to a located by class name element to make a click function
        declineCookies.click();
    }
    public static boolean isSearchFieldEnabledAndDisplayed(WebElement element) {
        //        Locating an element by given ID
//        WebElement searchInput = browser.findElement(By.id("searchInput"));

        if(element.isEnabled() && element.isDisplayed()) {
            //        Gives an input to a located by ID element to receive keys
            return true;
        }
        else {
            return false;
        }
    }
    public static void searchByKeywordClick(String keyword, By locator) {
        //        Locating an element by given class
        WebElement searchInput = browser.findElement(By.className("sn-suggest-input"));
        searchInput.sendKeys(keyword);
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(SECONDS_TO_WAIT_FOR_A_ELEMENT));
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        //        Gives an input to a located by class name element to make a click function
        browser.findElement(locator).click();
    }
    public static String searchKeywordUrl(String keyword){
        String searchURL = browser.getCurrentUrl();

        if (searchURL.contains(keyword)) {
            return keyword + " search request is displayed in URL";
        }
        return keyword + " search request isn't displayed in URL";
    }
    public static void waitElement(By locator) {
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(SECONDS_TO_WAIT_FOR_A_ELEMENT));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    public static void hoverElement(WebElement element){
        Actions act = new Actions(browser);
        act.moveToElement(element).build().perform();
    }
    public static void clickOnElement(By locator){
        browser.findElement(locator).click();
    }
    public static String checkTitle(String keyword){
        String title = browser.getTitle().toLowerCase();
        if (title.contains(keyword)) {
            return "Puslapio pavadinimas nėra žodis '" + keyword + "'.";
        }
        else {
            return "Puslapio pavadinime nėra žodis '" + keyword + "'.";
        }
    }
    public static void checkItemName(WebElement element, String keyword){
        String h1 = element.getText().toLowerCase();
        if (!h1.contains(keyword.substring(0, keyword.length() - 2))){
            System.out.println("Puslapis neprasideda žodžiu " + keyword);
        }
    }
    public static boolean isModalDisplayed(WebElement modal, WebElement modalTitle, String keyword){
        String title = modalTitle.getText();
        if (isSearchFieldEnabledAndDisplayed(modal) || title.contains(keyword)) {
            return true;
        }
        else{
            System.out.println("Modalo klaida");
            return false;
        }
    }

    public static void ScreenShots() {
        String name = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss").format(new Date());
        File imgFile = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(imgFile, new File("src/test/screenshots" + name + "_screenshot.png"));
        }
        catch (IOException error) {
            System.out.println("Nepavyko padaryti screenshot. Plačiau: " + error.getMessage());
        }
    }
    public static void closeBrowser() {
        browser.quit();
    }

    public static void main(String[] args) {

        setUp();
        closeCookies();
        System.out.println(isSearchFieldEnabledAndDisplayed(browser.findElement(By.className("sn-suggest-input"))));
        searchByKeywordClick(SEARCH_KEYWORD, By.className("c-search__submit"));
        System.out.println(searchKeywordUrl(SEARCH_KEYWORD));
        waitElement(By.cssSelector("div[id^='_0productBlock']"));
        hoverElement(browser.findElement(By.cssSelector("div[id^='_0productBlock']")));
        clickOnElement(By.cssSelector(".product-name a"));
        System.out.println(checkTitle(SEARCH_KEYWORD));
        checkItemName(browser.findElement(By.tagName("h1")), SEARCH_KEYWORD);
        clickOnElement(By.cssSelector(".c-product__controls .c-btn--primary"));
        waitElement(By.id("modal"));
        isModalDisplayed(browser.findElement(By.id("modal")), browser.findElement(By.className("add-to-cart-modal-title")), "prekė įtraukta");
        clickOnElement(By.id("close"));
        isSearchFieldEnabledAndDisplayed(browser.findElement(By.id("modal")));
        closeBrowser();
    }
}