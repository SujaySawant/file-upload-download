package com.test.upload.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.PushesFiles;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SingleTest {

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String URL = "http://hub-cloud.browserstack.com/wd/hub";
    private WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setup(Method m) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("project", "BrowserStack Java TestNG");
        caps.setCapability("build", "Upload-Download");
        caps.setCapability("name", m.getName() + " - Samsung Galaxy S9");

        caps.setCapability("device", "Samsung Galaxy S20");

        caps.setCapability("browserstack.user", USERNAME);
        caps.setCapability("browserstack.key", ACCESS_KEY);
        caps.setCapability("browserstack.debug", true);
        caps.setCapability("browserstack.networkLogs", true);

        driver = new AndroidDriver<>(new URL(URL), caps);
    }

    @Test
    public void fileUploadTest() throws IOException {
        String filePath = "src/test/resources/config/";
        String fileName = "android.json";
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get("http://www.fileconvoy.com/");
        ((PushesFiles) driver).pushFile("/data/local/tmp/" + fileName, new File(filePath + fileName));
        driver.findElement(By.id("upfile_0")).sendKeys("/data/local/tmp/" + fileName);
        driver.findElement(By.id("readTermsOfUse")).click();
        driver.findElement(By.name("upload_button")).submit();
        assertEquals(wait.until(presenceOfElementLocated(By.id("TopMessage"))).getText(),
                "Your file(s) have been successfully uploaded.",
                "File was not uploaded successfully");
        String filesUploaded = driver.findElement(By.cssSelector("div#MainSection > p:nth-of-type(5)")).getText();
        assertTrue(filesUploaded.contains(fileName), "Incorrect file uploaded");
    }

    @AfterMethod(alwaysRun = true)
    public void closeDriver(ITestResult tr) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        if (tr.isSuccess()) {
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\"}}");
        } else {
            String reason = tr.getThrowable().getMessage().split("\\n")[0].replaceAll("[\\\\{}\"]", "");
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + reason + "\"}}");
        }
        driver.quit();
    }

}
