# File Upload and Download <a href="https://www.browserstack.com/"><img src="https://www.vectorlogo.zone/logos/browserstack/browserstack-icon.svg" alt="BrowserStack" height="30"/></a> <a href="https://java.com"><img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" alt="Java" height="30" /></a> <a href="https://www.selenium.dev/"><img src="https://seeklogo.com/images/S/selenium-logo-DB9103D7CF-seeklogo.com.png" alt="Selenium" height="30" /></a>

This repo provides details on how to upload and download files from BrowserStack remote terminals.

## Setup

- Clone the repo
- Install dependencies `mvn compile`
- Update the environment variables with your [BrowserStack Username and Access Key](https://www.browserstack.com/accounts/settings)

## File Upload

### Desktop browsers
- Run `mvn -P file-upload-desktop-single test`, to run a single tests to upload a file on a desktop browser.
- Run `mvn -P file-upload-desktop-parallel test`, to run parallel tests to upload a file on desktop browsers.

### Mobile browsers
- Run `mvn -P file-upload-mobile-single test`, to run a single tests to upload a file on a mobile browser.
- Run `mvn -P file-upload-mobile-parallel test`, to run parallel tests to upload a file on mobile browsers.

## File Download
- Run `mvn -P file-download-desktop-single test`, to run a single tests to download a file on a desktop browser.
- Run `mvn -P file-download-desktop-parallel test`, to run parallel tests to download a file on desktop browsers.

## Notes
- You can view your Automate test results on the [BrowserStack Automate dashboard](https://automate.browserstack.com/).
- Export the environment variables for the Username and Access Key of your BrowserStack account.
  ```sh
  export BROWSERSTACK_USERNAME=<browserstack-username> && export BROWSERSTACK_ACCESS_KEY=<browserstack-access-key>
  ```
  
## Documentation
- Test File Upload: https://www.browserstack.com/docs/automate/selenium/test-file-upload
- Test File Download: https://www.browserstack.com/docs/automate/selenium/test-file-download