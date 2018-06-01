package com;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.ConfigFileEncryption;
import com.ReadConfig;

public class BrowserOpen {
	private static WebDriver driver;

	public static void main(String[] args) throws Exception {

		ConfigFileEncryption ConfigFile = new ConfigFileEncryption();
		String cytricSystem = ConfigFile.GetKeyValue("cytricSystem");

		WebDriver driver = BrowserOption("Firefox");

		ReadConfig.UpdateJarStatus("Starting cytric");
		driver.get(cytricSystem);// launch Fire fox and direct it to the Base URL
		// waitforlogin(cytricSystem);
		LogInCytric(cytricSystem, ConfigFile);
		ReadConfig.UpdateJarStatus("Accessing system");

		ReadConfig.UpdateJarStatus("Starting csv Export");
		GoToPath("//a[text()='Management']");
		GoToPath("//html/body/div/div[2]/header/div/nav/ul/li[2]/ul/li[2]/a");
		GoToPath("//a[text()='User Administration']");
		GoToPath("//a[text()='User Data Import and Export']");
		GoToPath("//a[text()='Export User Data']");

		ReadConfig.UpdateJarStatus("Change settings");
		ChangeSetting();

		GoToPath("//button[@name='btnContinue']");
		GoToPath("//button[@name='btnCheck']");

		ReadConfig.UpdateJarStatus("Waiting on the export file");
		FindExportFileInfo();

		// close Chrome
		Thread.sleep(10000);
		ReadConfig.UpdateJarStatus("Closing Browser");
		tearDown();
	}

	public static void LogInCytric(String baseUrl, ConfigFileEncryption ConfigFile) throws IOException {
		driver.findElement(By.xpath("//input[@name='u']")).sendKeys(ConfigFile.GetKeyValue("cytricUsername"));
		driver.findElement(By.xpath("//input[@name='p']")).sendKeys(ConfigFile.GetPassword());
		driver.findElement(By.xpath("//button[@type='submit']")).click();
	}

	public static void ChangeSetting() throws InterruptedException {
		// new
		// Select(driver.findElement(By.xpath("//select[@id='location']"))).selectByVisibleText("MitsubishiFuso
		// Truck and Bus Corporation, Kawasaki-shi");
		Uncheck("//input[@name='column_internal_id']");
		Uncheck("//input[@name='column_external_reference']");
		Check("//input[@name='column_last_change']");
		Check("//input[@name='column_last_login']");
		new Select(driver.findElement(By.xpath("//select[@name='column_apis_id']"))).selectByVisibleText("5");
	}

	public static void FindExportFileInfo() throws InterruptedException, IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH");
		Date date = new Date();

		String SpanXpath = "//span[contains(text(), '" + dateFormat.format(date) + "')]";
		String pXpath = "//p[contains(text(), '" + dateFormat.format(date) + "')]/preceding-sibling::p[@class='title']";

		WaitOnCSVFile(SpanXpath, pXpath);
		RemoveExportFile(SpanXpath);

	}

	public static void WaitOnCSVFile(String Xpath, String pXpath) throws InterruptedException, IOException {

		List<WebElement> elems = driver.findElements(By.xpath(Xpath));

		while (elems.size() == 0) {
			GetTitleExport(pXpath);
			TimeUnit.SECONDS.sleep(10);
			GoToPath("//input[@name='btnRefresh']");
			elems = driver.findElements(By.xpath(Xpath));
		}
		driver.findElement(By.xpath(Xpath)).click();
		ReadConfig.UpdateJarStatus("Downloading export file");
	}

	public static void GetTitleExport(String Xpath) throws InterruptedException, IOException {
		String Text = "";

		List<WebElement> elems = driver.findElements(By.xpath(Xpath));
		for (WebElement elem : elems) {
			Text = elem.getText();
		}

		// p[contains(text(), '20180601')]/preceding-sibling::p[@class="title"]
		// System.out.println(Text);
		if (Text.contains("(")) {
			String[] parts = Text.split("(");
			parts = parts[1].split(")");
			ReadConfig.UpdateJarStatus("Running Import/Export Jobs: " + parts[0]);
		} else {
			ReadConfig.UpdateJarStatus("Pending Import/Export Jobs");
		}

	}
	public static WebDriver BrowserOption(String Browser) {
		String downloadFilepath = getJarPath();

		if (Browser == "Chrome") {
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			ChromeOptions options = new ChromeOptions();

			options.setExperimentalOption("prefs", chromePrefs);
			// options.setHeadless(true);
			// options.addArguments("--headless");

			DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			cap.setCapability(ChromeOptions.CAPABILITY, options);

			// declaration and instantiation of objects/variables
			System.setProperty("webdriver.chrome.driver", downloadFilepath + "\\chromedriver.exe");
			// System.setProperty("webdriver.chrome.driver",
			// Thread.currentThread().getContextClassLoader().getResource(downloadFilepath +
			// "\\chromedriver.exe").getFile());
			driver = new ChromeDriver(options);

		} else if (Browser == "Firefox") {
			// Create FireFox Profile object
			System.setProperty("webdriver.gecko.driver", downloadFilepath + "\\geckodriver.exe");
			FirefoxProfile profile = new FirefoxProfile();
			FirefoxBinary firefoxBinary = new FirefoxBinary();
			// firefoxBinary.addCommandLineOptions("--headless");

			profile.setPreference("browser.download.dir", downloadFilepath);// Set Location to store files after
																			// downloading.
			profile.setPreference("browser.download.folderList", 2);// Set Location to store files after downloading.
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;"); // Set Preference to not show
																							// file download
																							// confirmation dialogue
																							// using MIME types Of
																							// different file extension
																							// types.
			profile.setPreference("browser.download.manager.showWhenStarting", false);
			profile.setPreference("pdfjs.disabled", true);

			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.setBinary(firefoxBinary).setProfile(profile);
			driver = new FirefoxDriver(firefoxOptions);
		}

		return driver;
	}

	public static void tearDown() throws Exception {
		driver.close();
		driver.quit();
	}

	public static String getJarPath() {
		String path = BrowserOpen.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String pathName = "";

		try {
			pathName = URLDecoder.decode(path, "UTF-8");
			pathName = pathName.substring(1, pathName.lastIndexOf("/"));
			pathName = "C:\\Users\\srueda\\Desktop\\Daimler Export Tool\\bin";

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathName;
	}

	public static void waitforlogin(String baseUrl) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(baseUrl)));
	}

	public static void GoToPath(String Xpath) throws InterruptedException {
		WaitFor(Xpath);
		driver.findElement(By.xpath(Xpath)).click();
	}

	public static void WaitFor(String Xpath) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath)));
	}

	public static void Uncheck(String Xpath) throws InterruptedException {
		if (driver.findElement(By.xpath(Xpath)).isSelected()) {
			driver.findElement(By.xpath(Xpath + "/following-sibling::i")).click();
		}
	}

	public static void Check(String Xpath) throws InterruptedException {
		if (!driver.findElement(By.xpath(Xpath)).isSelected()) {
			driver.findElement(By.xpath(Xpath + "/following-sibling::i")).click();
		}
	}

	public static void RemoveExportFile(String Xpath) {
		driver.findElement(By.xpath(
				Xpath + "/parent::a/parent::p/parent::td[@class='setting']/following-sibling::td[@class='remove']/a"))
				.click();
	}

}
