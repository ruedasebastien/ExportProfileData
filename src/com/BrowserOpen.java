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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.ConfigFileEncryption;

public class BrowserOpen {
	private static WebDriver driver;

	public static void main(String[] args) throws Exception {

		ConfigFileEncryption ConfigFile = new ConfigFileEncryption();
		String cytricSystem = ConfigFile.GetKeyValue("cytricSystem");

		WebDriver driver = BrowserOption();

		System.out.println("Starting cytric");
		driver.get(cytricSystem);// launch Fire fox and direct it to the Base URL
		// waitforlogin(cytricSystem);
		LogInCytric(cytricSystem, ConfigFile);
		System.out.println("Accessing system");

		System.out.println("Starting csv Export");
		GoToPath("//a[text()='Management']");
		GoToPath("//html/body/div/div[2]/header/div/nav/ul/li[2]/ul/li[2]/a");
		GoToPath("//a[text()='User Administration']");
		GoToPath("//a[text()='User Data Import and Export']");
		GoToPath("//a[text()='Export User Data']");

		System.out.println("Change settings");
		ChangeSetting();

		GoToPath("//button[@name='btnContinue']");
		GoToPath("//button[@name='btnCheck']");

		System.out.println("Waiting on the export file");
		WaitOnCSVFile();

		// close Chrome
		Thread.sleep(10000);
		tearDown();
	}

	public static void LogInCytric(String baseUrl, ConfigFileEncryption ConfigFile) throws IOException {
		driver.findElement(By.xpath("//input[@name='u']")).sendKeys(ConfigFile.GetKeyValue("cytricUsername"));
		driver.findElement(By.xpath("//input[@name='p']")).sendKeys(ConfigFile.GetPassword());
		driver.findElement(By.xpath("//button[@type='submit']")).click();
	}

	public static void ChangeSetting() throws InterruptedException {
		// new Select(driver.findElement(By.xpath("//select[@id='location']"))).selectByVisibleText("MitsubishiFuso Truck and Bus Corporation, Kawasaki-shi");
		Uncheck("//input[@name='column_internal_id']");
		Uncheck("//input[@name='column_external_reference']");
		Check("//input[@name='column_last_change']");
		Check("//input[@name='column_last_login']");
		new Select(driver.findElement(By.xpath("//select[@name='column_apis_id']"))).selectByVisibleText("5");
	}

	public static void WaitOnCSVFile() throws InterruptedException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		Date date = new Date();

		String Xpath = "//span[contains(text(), '" + dateFormat.format(date) + "')]";

		List<WebElement> elems = driver.findElements(By.xpath(Xpath));
		while (elems.size() == 0) {
			TimeUnit.SECONDS.sleep(10);
			GoToPath("//input[@name='btnRefresh']");
			elems = driver.findElements(By.xpath(Xpath));
		}
		driver.findElement(By.xpath(Xpath)).click();
	}

	public static WebDriver BrowserOption() {

		String downloadFilepath = getJarPath();
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		ChromeOptions options = new ChromeOptions();

		options.setExperimentalOption("prefs", chromePrefs);
		// options.setHeadless(true);
		//options.addArguments("--headless");

		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, options);

		// declaration and instantiation of objects/variables
		System.setProperty("webdriver.chrome.driver", downloadFilepath + "\\chromedriver.exe");
		WebDriver driver = new ChromeDriver(cap);

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
}
