package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.SourceType;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Selenium {
    public static final String SEARCH_KEYWORD = "televizoriai";
    public static WebDriver narsykle;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Selenium+Maven");

        System.setProperty(
                "webdriver.chrome.driver",
                "drivers/driver120.exe"
        );

        narsykle = new ChromeDriver();
        narsykle.get("https://pigu.lt");

        //slapuku isjungimas
        WebElement notAgree = narsykle.findElement(By.className("c-link"));
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) narsykle;
        javascriptExecutor.executeScript("arguments[0].click()", notAgree);
        Thread.sleep(1000);
//        narsykle.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        WebElement paieskosLaukelis = narsykle.findElement(By.id("searchInput"));

        //nurodant iki kiek laiko bus laukiama uzkraunamo elemento atidarymo
//        WebDriverWait webDriverWait = new WebDriverWait(narsykle,Duration.ofSeconds(1));
//        webDriverWait.until(ExpectedConditions.visibilityOf(paieskosLaukelis));
        Thread.sleep(1000);


        if(paieskosLaukelis.isDisplayed() && paieskosLaukelis.isEnabled()){
            paieskosLaukelis.sendKeys(SEARCH_KEYWORD);
        }
        else {
            System.out.println("Paieskos laukelis nematomas, neaktyvus");
            makeScreenshot();//Screenshot
            System.exit(1);//programa baigia darba
        }

        WebElement searchIkon = narsykle.findElement(By.className("c-icon--search"));
        searchIkon.click();

        Thread.sleep(1000);

        String naujasURL = narsykle.getCurrentUrl();
        if(naujasURL.contains(SEARCH_KEYWORD)){
            System.out.println(SEARCH_KEYWORD + " paieskos kriterijus atvaizduojamas URL adrese");
        }
        else{
            System.out.println(SEARCH_KEYWORD + " paieskos kriterijus neatvaizduojamas URL adrese");
            makeScreenshot();//Screenshot
            System.exit(1);
        }

        System.out.println(naujasURL);

        String searchTitle = narsykle.getTitle().toLowerCase();
        System.out.println(searchTitle);

        if(!searchTitle.contains(SEARCH_KEYWORD)){
            System.out.println("Paieskos kriterijus '"+ SEARCH_KEYWORD +"' nera atvaizduojamas pavadinime");
            makeScreenshot();//Screenshot
            System.exit(1);
        }

//        WebElement firstItem = narsykle.findElement(By.className("img-list-container"));
//        firstItem.click();


        WebElement kortele = narsykle.findElement(By.id("_0productBlock90680899"));
        Actions action = new Actions(narsykle);//hower uzejimas ant korteles simuliuoja paspudima ant korteles
        //siam metodui paduodamas web elementas
        action.moveToElement(kortele).build().perform();//moveToElement naudojamas nuejimui iki elemento ir uzvedimui ant peles
        Thread.sleep(1000);


        WebElement iKrepseli = narsykle.findElement(By.xpath("//*[@id=\"_0productBlock90680899\"]/div/div/a[2]"));
        iKrepseli.click();

        String h1 = narsykle.findElement(By.tagName("h1")).getText().toLowerCase();
        if(!h1.contains(SEARCH_KEYWORD.substring(0,SEARCH_KEYWORD.length()-2))){//numetam galune televizoriai 2 paskutinius simbolius
            System.out.println("puslapis prasideda zodziu" + SEARCH_KEYWORD);
            makeScreenshot();//Screenshot
            System.exit(1);
        }

        Thread.sleep(1000);

        makeScreenshot();//pabandymas ar daro Screenshot

        WebElement modal = narsykle.findElement(By.cssSelector("#modal"));
        String modalTitle = narsykle.findElement(By.className("add-to-cart-modal-title")).getText();
        if(!modal.isDisplayed() || !modalTitle.contains("prekė įtraukta")){
            System.out.println("modalas nera rodomas arba preke i krepseli neitraukta");
            makeScreenshot();//Screenshot
            System.exit(1);
        }

        Thread.sleep(1000);

        narsykle.findElement(By.id("close")).click();
        if(modal.isDisplayed()){
            System.out.println("Modal neuzdarytas");
            makeScreenshot();
            System.exit(1);
        }

        narsykle.close();
    }

    public static void makeScreenshot() {
        String title = narsykle.getTitle();
        String fileName = title
                .replaceAll("[ .|]","_");

        File imgFile = ((TakesScreenshot) narsykle).getScreenshotAs(OutputType.FILE);

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            String dateTime = dtf.format (now);
            FileUtils.copyFile(imgFile, new File("src/test/screenshots/"+fileName+"_"+dateTime+".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}