package com.test.download.desktop;

import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ParallelTest {

    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String URL = "http://hub-cloud.browserstack.com/wd/hub";

    @BeforeMethod(alwaysRun = true)
    @Parameters({"config", "capability"})
    public void setup(String configFile, String capability, Method m) throws IOException {
        Files.deleteIfExists(Paths.get("target", "List of devices.csv"));
        JsonPath jsonPath = JsonPath.from(new File("src/test/resources/config/" + configFile + ".json"));
        Map<String, String> capabilitiesMap = new HashMap<>();
        capabilitiesMap.putAll(jsonPath.getMap("commonCapabilities"));
        capabilitiesMap.putAll(jsonPath.getMap("capabilities[" + capability + "]"));
        if (capabilitiesMap.get("device") == null) {
            capabilitiesMap.put("name", m.getName() + " - " + capabilitiesMap.get("browser") + " " + capabilitiesMap.get("browser_version"));
        } else {
            capabilitiesMap.put("name", m.getName() + " - " + capabilitiesMap.get("device"));
        }
        capabilitiesMap.put("browserstack.user", USERNAME);
        capabilitiesMap.put("browserstack.key", ACCESS_KEY);
        DesiredCapabilities caps = new DesiredCapabilities(capabilitiesMap);
        if (capabilitiesMap.get("browser").equals("Firefox")) {
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.folderList", 1);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.manager.focusWhenStarting", false);
            profile.setPreference("browser.download.useDownloadDir", true);
            profile.setPreference("browser.helperApps.alwaysAsk.force", false);
            profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
            profile.setPreference("browser.download.manager.closeWhenDone", true);
            profile.setPreference("browser.download.manager.showAlertOnComplete", false);
            profile.setPreference("browser.download.manager.useWindow", false);
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
            caps.setCapability(FirefoxDriver.PROFILE, profile);
        }
        driverThread.set(new RemoteWebDriver(new URL(URL), caps));
    }

    @Test
    public void fileDownloadTest() {
        String fileExistsScript = "browserstack_executor: {\"action\": \"fileExists\", \"arguments\": {\"fileName\": \"BrowserStack - List of devices to test on.csv\"}}";
        String filePropertiesScript = "browserstack_executor: {\"action\": \"getFileProperties\", \"arguments\": {\"fileName\": \"BrowserStack - List of devices to test on.csv\"}}";
        String fileContentScript = "browserstack_executor: {\"action\": \"getFileContent\", \"arguments\": {\"fileName\": \"BrowserStack - List of devices to test on.csv\"}}";

        WebDriver driver = driverThread.get();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        driver.get("https://www.browserstack.com/test-on-the-right-mobile-devices");
        driver.findElement(By.id("accept-cookie-notification")).click();
        driver.findElement(By.className("icon-csv")).click();
        wait.until(d -> ((JavascriptExecutor) d).executeScript(fileExistsScript));
        System.out.println("File exists: " + jse.executeScript(fileExistsScript));
        System.out.println("File properties: " + jse.executeScript(filePropertiesScript));
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
        JavascriptExecutor jse = (JavascriptExecutor) driverThread.get();
        if (tr.isSuccess()) {
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\"}}");
        } else {
            String reason = tr.getThrowable().getMessage().split("\\n")[0].replaceAll("[\\\\{}\"]", "");
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + reason + "\"}}");
        }
        driverThread.get().quit();
        driverThread.remove();
    }

}
