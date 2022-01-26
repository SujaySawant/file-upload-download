package com.test.download.desktop;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class SingleTest {

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String URL = "http://hub-cloud.browserstack.com/wd/hub";
    private WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setup(Method m) throws IOException {
        Files.deleteIfExists(Paths.get("target", "List of devices.csv"));
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("project", "File Upload and Download");
        caps.setCapability("build", "Demo");
        caps.setCapability("name", m.getName() + " - Chrome latest");

        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "latest");

        caps.setCapability("browserstack.user", USERNAME);
        caps.setCapability("browserstack.key", ACCESS_KEY);
        caps.setCapability("browserstack.debug", true);
        caps.setCapability("browserstack.networkLogs", true);

        driver = new RemoteWebDriver(new URL(URL), caps);
    }

    @Test
    public void fileDownloadTest() {
        String fileExistsScript = "browserstack_executor: {\"action\": \"fileExists\", \"arguments\": {\"fileName\": \"BrowserStack - List of devices to test on.csv\"}}";
        String filePropertiesScript = "browserstack_executor: {\"action\": \"getFileProperties\", \"arguments\": {\"fileName\": \"BrowserStack - List of devices to test on.csv\"}}";
        String fileContentScript = "browserstack_executor: {\"action\": \"getFileContent\", \"arguments\": {\"fileName\": \"BrowserStack - List of devices to test on.csv\"}}";

        WebDriverWait wait = new WebDriverWait(driver, 10);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        driver.get("https://www.browserstack.com/test-on-the-right-mobile-devices");
        driver.findElement(By.id("accept-cookie-notification")).click();
        driver.findElement(By.className("icon-csv")).click();
        wait.until(d -> ((JavascriptExecutor) d).executeScript(fileExistsScript));
        System.out.println("File exists: " + jse.executeScript(fileExistsScript));
        FileProperties properties = new FileProperties(jse.executeScript(filePropertiesScript));
        System.out.println(properties);
        String base64EncodedFile = (String) jse.executeScript(fileContentScript);
        byte[] data = Base64.getDecoder().decode(base64EncodedFile);
        try (OutputStream stream = new FileOutputStream("target/List of devices.csv")) {
            stream.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Unable to download the file.", e);
        }
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
