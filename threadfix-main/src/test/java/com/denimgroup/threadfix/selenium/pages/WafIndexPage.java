////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class WafIndexPage extends BasePage {

	public WafIndexPage(WebDriver webdriver) {
		super(webdriver);
	}

	public WafRulesPage clickRules(String wafName){
		driver.findElementById("rulesButton" + wafName).click();
		return new WafRulesPage(driver);
	}

	public WafIndexPage clickDeleteWaf(String wafName){
		clickEditWaf(wafName);
        waitForElement(driver.findElementById("deleteWafButton"));
		driver.findElementById("deleteWafButton").click();
		handleAlert();
		sleep(1000);
		return new WafIndexPage(driver);
	}

	public WafIndexPage clickAddWafLink() {
        waitForElement(driver.findElementById("createWafModalButton"));
		driver.findElementById("createWafModalButton").click();
		waitForElement(driver.findElementById("submit"));
		return new WafIndexPage(driver);
	}

	public WafIndexPage createNewWaf(String name,String type){
		setWafName(name);
		setWafType(type);
		return this;
	}

    public WafIndexPage setWafName(String name){
        driver.findElementById("wafCreateNameInput").clear();
        driver.findElementById("wafCreateNameInput").sendKeys(name);
        return this;
    }

    public WafIndexPage setWafType(String type){
        new Select(driver.findElementById("typeSelect")).selectByVisibleText(type);
        return this;
    }

    public boolean isWafPresent(String name){
        boolean presence = driver.findElementsById("wafName" + name).size() == 1;
        return presence;
    }
	
	public WafIndexPage clickCreateWaf(){
		driver.findElementById("submit").click();
        if (!(driver.findElementByClassName("alert-success").isDisplayed())) {
            sleep(4000);
        }
		return new WafIndexPage(driver);
    }

	public boolean isTextPresentInWafTableBody(String text) {
		return driver.findElementById("wafTableBody").getText().contains(text);
	}

    //todo replace with wafName once success message is fixed.
	public boolean isSuccessPresent(String wafName){
		return driver.findElementByClassName("alert-success").getText().contains(wafName);
	}
	
	public WafIndexPage clickEditWaf(String wafName){
		driver.findElementById("editWafModalButton"+wafName).click();
		waitForElement(driver.findElementById("myModalLabel"));
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage editWaf(String wafName, String newName, String type){
        waitForElement(driver.findElementById("myModalLabel"));
		driver.findElementById("wafCreateNameInput").clear();
		driver.findElementById("wafCreateNameInput").sendKeys(newName);
        new Select(driver.findElementById("typeSelect")).selectByVisibleText(type);
		return new WafIndexPage(driver);
	}
	
	public String getNameErrorsText(){
		return driver.findElementById("nameRequiredError").getText();
	}

	public String getNameText(int row){
		return  driver.findElementById("wafName" + row).getText();
	}

	public WafIndexPage clickCloseWafModal(){
		driver.findElementByLinkText("Close").click();
		sleep(1000);
		return new WafIndexPage(driver);
	}

	public int getWafEditHeaderWidth(String wafName) {
		return 0;
	}
}