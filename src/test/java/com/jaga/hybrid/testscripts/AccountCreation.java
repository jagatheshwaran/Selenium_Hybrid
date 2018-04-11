package com.jaga.hybrid.testscripts;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jaga.hybrid.commonfunctions.BaseClass;
import com.jaga.hybrid.helperfunctions.DropDownHelper;
import com.jaga.hybrid.helperfunctions.VerificationHelper;

/**
 * 
 * @author Jagatheshwaran
 * @since 21/03/2018
 * @Modified 22/03/2018
 *
 */
public class AccountCreation extends BaseClass {

	DropDownHelper dropdownhelper;
	VerificationHelper verificationhelper;
	WebDriver driver;

	@DataProvider(name = "createAccount")
	public String[][] createAccount() {
		return getData("testData.xlsx", "CreateAccount");

	}

	@Test(dataProvider = "createAccount")
	public void createAccount(String firstName, String lastName, String dob, String email, String company,
			String address, String postcode, String city, String state, String Country, String phone, String password,
			String confirmPassword) {
		try {
			
			BaseClass.getWebElement("CreateAccContinueBtn").click();
			BaseClass.getWebElement("genderOption").click();
			BaseClass.getWebElement("firstName").sendKeys(firstName);
			BaseClass.getWebElement("lastName").sendKeys(lastName);
			BaseClass.getWebElement("dateOfBirth").sendKeys(dob);
			BaseClass.getWebElement("emailAddress").sendKeys(email);
			BaseClass.getWebElement("companyName").sendKeys(company);
			BaseClass.getWebElement("address").sendKeys(address);
			BaseClass.getWebElement("postCode").sendKeys(postcode);
			BaseClass.getWebElement("cityName").sendKeys(city);
			BaseClass.getWebElement("stateName").sendKeys(state);
			BaseClass.loadProperties();
			dropdownhelper.selectByVisibleTextBy(getLocators1(properties.getProperty("countryName")), Country);
			BaseClass.getWebElement("telePhoneNumber").sendKeys(phone);
			BaseClass.getWebElement("password").sendKeys(password);
			BaseClass.getWebElement("confirmationPassword").sendKeys(confirmPassword);
			BaseClass.getWebElement("submitButton").click();
			verificationhelper.isDisplayed(BaseClass.getWebElement("successMessage"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
