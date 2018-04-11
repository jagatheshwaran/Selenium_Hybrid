package com.jaga.hybrid.commonfunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.jaga.hybrid.excelreader.ExcelReader;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * 
 * @author Jagatheshwaran
 * @since /3/2018
 * @Modified - 21/03/2018
 *
 */
public class BaseClass {

	public static WebDriver driver;
	public static File file;
	public static FileInputStream fileinputstream;
	public static Properties properties;
	public static WebDriverWait wait;
	public static ExtentReports extentreports;
	public static ExtentTest extenttest;
	public static ITestResult itestresult;
	public ExcelReader excelreader;

	static {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		extentreports = new ExtentReports(
				System.getProperty("user.dir") + "src//test//resources//AutomtionTestReports//Test"
						+ simpledateformat.format(calendar.getTime()) + ".html");
	}

	public void startBrowser(String browser) {
		if (System.getProperty("os.name").contains("Windows")) {
			if (properties.getProperty("browser").equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "//src//main//resources//Drivers//chromedriver.exe");
				driver = new ChromeDriver();
			} else if (properties.getProperty("browser").equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + "//src//main//resources//Drivers//firefox.exe");
				driver = new FirefoxDriver();
			} else if (properties.getProperty("browser").equalsIgnoreCase("ie")) {
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + "//src//main//resources//Drivers//ie.exe");
				driver = new InternetExplorerDriver();
			}

		} else if (System.getProperty("os.name").contains("Mac")) {
			if (properties.getProperty("browser").equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "//src//main//resources//Drivers//chromedriverupdated");
				driver = new ChromeDriver();
			} else if (properties.getProperty("browser").equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.firefox.marionette",
						System.getProperty("user.dir") + "//src//main//resources//Drivers//firefox");
				driver = new FirefoxDriver();
			} else if (properties.getProperty("browser").equalsIgnoreCase("ie")) {
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + "//src//main//resources//Drivers//ie");
				driver = new InternetExplorerDriver();
			}
		}
	}

	public static void loadProperties() throws IOException {

		properties = new Properties();

		/*
		 * file = new File(System.getProperty("user.dir") +
		 * "//src//main//java//com//jaga//hybrid//properties//config.properties");
		 * fileinputstream = new FileInputStream(file);
		 * properties.load(fileinputstream);
		 */

		file = new File(
				System.getProperty("user.dir") + "//src//main//java//com//jaga//hybrid//properties//object.properties");
		fileinputstream = new FileInputStream(file);
		properties.load(fileinputstream);

	}

	@BeforeClass
	public void init() throws IOException {

		loadProperties();
		System.out.println(System.getProperty("os.name"));
		System.out.println(properties.getProperty("browser"));
		startBrowser("chrome");
		System.out.println(properties.getProperty("url"));
		driver.get(properties.getProperty("url"));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

	}

	public String getSnapShot(String imageName) throws IOException {
		if (imageName.equals("")) {
			imageName = "blank";
		}
		File sourceImage = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String imageLoction = System.getProperty("user.dir") + "src//test//resources//Screenshots//";
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		String actulImage = imageLoction + imageName + "_" + simpledateformat.format(calendar.getTime()) + ".png";
		File destinationImage = new File(actulImage);
		FileUtils.copyFile(sourceImage, destinationImage);
		return actulImage;
	}

	public WebElement waitForElement(WebDriver driver, long time, WebElement element) {
		wait = new WebDriverWait(driver, time);
		return wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public WebElement waitForElementWithPollingInterval(WebDriver driver, long time, WebElement element) {
		wait = new WebDriverWait(driver, time);
		wait.pollingEvery(time, TimeUnit.SECONDS);
		wait.ignoring(NoSuchElementException.class);
		return wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public void implicitWait(long time) {
		driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
	}

	public void getResult(ITestResult itestresult) throws IOException {
		if (itestresult.getStatus() == ITestResult.SUCCESS) {
			extenttest.log(LogStatus.PASS, itestresult.getName() + "Test is Passed");
		} else if (itestresult.getStatus() == ITestResult.SKIP) {
			extenttest.log(LogStatus.SKIP, itestresult.getName() + "Test is Skipped :-" + itestresult.getThrowable());
		} else if (itestresult.getStatus() == ITestResult.FAILURE) {
			extenttest.log(LogStatus.FAIL, itestresult.getName(), "Test is Failed" + itestresult.getThrowable());
			String screen = getSnapShot("");
			extenttest.log(LogStatus.FAIL, extenttest.addScreenCapture(screen));

		} else if (itestresult.getStatus() == ITestResult.STARTED) {
			extenttest.log(LogStatus.INFO, itestresult.getName() + "Test is Started");
		}
	}

	@AfterMethod
	public void afterMethod(ITestResult itestresult) throws IOException {
		getResult(itestresult);
	}

	@BeforeMethod
	public void beforeMethod(Method itestresult) {
		extenttest = extentreports.startTest(itestresult.getName());
		extenttest.log(LogStatus.INFO, itestresult.getName() + "Test Started");
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() {
		driver.quit();
		extentreports.endTest(extenttest);
		extentreports.flush();
	}

	public static WebElement getLocator(String locator) throws Exception {
		// System.out.println("locator > " + locator);
		String split[] = locator.split(":");
		String locatorType = split[0];
		// System.out.println(locatorType);
		String locatorValue = split[1];
		// System.out.println(locatorValue);

		if (locatorType.equalsIgnoreCase("id")) {
			return driver.findElement(By.id(locatorValue));
		} else if (locatorType.equalsIgnoreCase("name")) {
			return driver.findElement(By.name(locatorValue));
		} else if (locatorType.equalsIgnoreCase("className")) {
			return driver.findElement(By.className(locatorValue));
		} else if (locatorType.equalsIgnoreCase("tagName")) {
			return driver.findElement(By.tagName(locatorValue));
		} else if (locatorType.equalsIgnoreCase("linkText")) {
			return driver.findElement(By.linkText(locatorValue));
		} else if (locatorType.equalsIgnoreCase("partialLinkText")) {
			return driver.findElement(By.partialLinkText(locatorValue));
		} else if (locatorType.equalsIgnoreCase("cssSelector")) {
			return driver.findElement(By.cssSelector(locatorValue));
		} else if (locatorType.equalsIgnoreCase("xpath")) {
			return driver.findElement(By.xpath(locatorValue));
		} else
			throw new Exception("Unknown Locator Type :" + locatorType);

	}

	public List<WebElement> getLocators(String locator) throws Exception {
		String split[] = locator.split(":");
		String locatorType = split[0];
		String locatorVlue = split[1];

		if (locatorType.equalsIgnoreCase("id"))
			return driver.findElements(By.id(locatorVlue));
		else if (locatorType.equalsIgnoreCase("name"))
			return driver.findElements(By.name(locatorVlue));
		else if (locatorType.equalsIgnoreCase("className"))
			return driver.findElements(By.className(locatorVlue));
		else if (locatorType.equalsIgnoreCase("tagName"))
			return driver.findElements(By.tagName(locatorVlue));
		else if (locatorType.equalsIgnoreCase("linkText"))
			return driver.findElements(By.linkText(locatorVlue));
		else if (locatorType.equalsIgnoreCase("partialLinkText"))
			return driver.findElements(By.partialLinkText(locatorVlue));
		else if (locatorType.equalsIgnoreCase("cssSelector"))
			return driver.findElements(By.cssSelector(locatorVlue));
		else if (locatorType.equalsIgnoreCase("xpath"))
			return driver.findElements(By.xpath(locatorVlue));
		else
			throw new Exception("Unknown Locator Type :" + locatorType);

	}

	public static WebElement getWebElement(String object) throws Exception {
		System.out.println("object > " + object);
		// System.out.println(getLocator(BaseClass.properties.getProperty(object)));
		return getLocator(BaseClass.properties.getProperty(object));
	}

	public List<WebElement> getWebElements(String object) throws Exception {
		System.out.println("object > " + object);
		return getLocators(BaseClass.properties.getProperty(object));

	}

	public static WebElement getWebElement1(String object) throws Exception {
		System.out.println("object > " + object);
		// System.out.println(getLocator(BaseClass.properties.getProperty(object)));
		return getLocators1(BaseClass.properties.getProperty(object));
	}

	public String[][] getData(String excelName, String sheetName) {
		String excelLocation = System.getProperty("user.dir") + "//src//test//resources//TestData//" + excelName;
		excelreader = new ExcelReader();
		System.out.println("Data in Base" + excelreader.getExcelData(excelLocation, sheetName));

		return excelreader.getExcelData(excelLocation, sheetName);

	}

	public static WebElement getLocators1(String locator) throws Exception {
		System.out.println("locator > " + locator);
		//String split[] = locator.split(":");
		String locatorType =  locator.split(":")[0];
		System.out.println(locatorType);
		String locatorValue =  locator.split(":")[1];
		WebElement loc = null;
		System.out.println(locatorValue);

		if (locatorType.equalsIgnoreCase("id")) {
			//return By.id(locatorValue);
		} else if (locatorType.equalsIgnoreCase("name")) {
			loc = driver.findElement(By.name(locatorValue));
			System.out.println("locator >>>"+loc);
			return  loc;
			
		} else if (locatorType.equalsIgnoreCase("className")) {
			//return By.className(locatorValue);
		} else if (locatorType.equalsIgnoreCase("tagName")) {
			//return By.tagName(locatorValue);
		} else if (locatorType.equalsIgnoreCase("linkText")) {
			//return By.linkText(locatorValue);
		} else if (locatorType.equalsIgnoreCase("partialLinkText")) {
		//	return By.partialLinkText(locatorValue);
		} else if (locatorType.equalsIgnoreCase("cssSelector")) {
			//return By.cssSelector(locatorValue);
		} else if (locatorType.equalsIgnoreCase("xpath")) {
			//return By.xpath(locatorValue);
		} else
			throw new Exception("Unknown Locator Type :" + locatorType);
		//return loc;
		return loc;
			

	}

	public static void main(String ar[]) throws Exception {
		BaseClass bc = new BaseClass();

		loadProperties();
		//getWebElement1("countryName");
		getLocators1(BaseClass.properties.getProperty("countryName"));

	}

}