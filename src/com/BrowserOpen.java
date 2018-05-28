package com;


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



public class BrowserOpen {
	 public static void main(String[] args) throws InterruptedException {
		 //Add download preference
		 String path = BrowserOpen.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		 System.out.println(path);
		 try {
			String pathName = URLDecoder.decode(path, "UTF-8");
			pathName = pathName.substring(1,pathName.lastIndexOf("/") );
			
			System.out.println(pathName);
			String downloadFilepath = pathName;
			//String downloadFilepath = "C:\\Users\\srueda\\Documents\\workspace\\workspace_Selenium\\RunableJarFile";
			
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("prefs", chromePrefs);
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			
			// declaration and instantiation of objects/variables
			System.setProperty("webdriver.chrome.driver",downloadFilepath + "\\chromedriver.exe");
			WebDriver driver = new ChromeDriver(cap);
		    String baseUrl = "https://amadeus.cytric.net/ibe/?system=Demo_SRUEDA";
		    
		    // launch Fire fox and direct it to the Base URL
		    driver.get(baseUrl);
		    waitforlogin(driver, baseUrl);
			
			GoToPath(driver,"//a[text()='Management']");
			GoToPath(driver,"//html/body/div/div[2]/header/div/nav/ul/li[2]/ul/li[2]/a");
			GoToPath(driver,"//a[text()='User Administration']");
			GoToPath(driver,"//a[text()='User Data Import and Export']");
			GoToPath(driver,"//a[text()='Export User Data']");
		
			ChangeSetting(driver);
			GoToPath(driver,"//button[@name='btnContinue']");
			GoToPath(driver,"//button[@name='btnCheck']");
			WaitOnCSVFile(driver);
			
		    //close Chrome
			Thread.sleep(10000);
		    driver.close();	
		    
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 public static void ChangeSetting(WebDriver driver) throws InterruptedException {
		 //new Select(driver.findElement(By.xpath("//select[@id='location']"))).selectByVisibleText("Mitsubishi Fuso Truck and Bus Corporation, Kawasaki-shi");
		 Uncheck(driver, "//input[@name='column_internal_id']");
		 Uncheck(driver, "//input[@name='column_external_reference']");
		 Check(driver, "//input[@name='column_last_change']");
		 Check(driver, "//input[@name='column_last_login']");
		 new Select(driver.findElement(By.xpath("//select[@name='column_apis_id']"))).selectByVisibleText("5");
	 }
	 
	 public static void WaitOnCSVFile(WebDriver driver) throws InterruptedException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		Date date = new Date();

		String Xpath= "//span[contains(text(), '"+dateFormat.format(date)+"')]";
		
		List<WebElement> elems = driver.findElements(By.xpath(Xpath));
		while (elems.size() == 0) {
			TimeUnit.SECONDS.sleep(10);
			GoToPath(driver,"//input[@name='btnRefresh']");
			elems = driver.findElements(By.xpath(Xpath));
		}
		driver.findElement(By.xpath(Xpath)).click();
	 }
	 
	 
	 public static void waitforlogin(WebDriver driver, String baseUrl) {
		    WebDriverWait wait = new WebDriverWait(driver, 30);
		    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(baseUrl)));
		    String url =  driver.getCurrentUrl();
		    System.out.println(url);
	 }
	 
	 public static void GoToPath(WebDriver driver, String Xpath) throws InterruptedException {
		 WaitFor(driver, Xpath);
		 driver.findElement(By.xpath(Xpath)).click();		  
	 }
	 
	 public static void WaitFor(WebDriver driver, String Xpath) throws InterruptedException {
		 WebDriverWait wait = new WebDriverWait(driver, 60);
		 wait.until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath))); 
	 }
	 
	 public static void Uncheck(WebDriver driver, String Xpath) throws InterruptedException {
		 System.out.println(Xpath);
		 if (driver.findElement(By.xpath(Xpath)).isSelected() )
		 {
		     driver.findElement(By.xpath(Xpath + "/following-sibling::i")).click();
		 }
	 }
	 
	 public static void Check(WebDriver driver, String Xpath) throws InterruptedException {
		 System.out.println(Xpath);
		 if ( !driver.findElement(By.xpath(Xpath)).isSelected() )
		 {
		      driver.findElement(By.xpath(Xpath + "/following-sibling::i")).click();
		 }
	 }
}
