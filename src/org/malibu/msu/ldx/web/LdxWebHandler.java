package org.malibu.msu.ldx.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class LdxWebHandler {
	
	private WebDriver driver;
	
	public LdxWebHandler() {
		driver = new PhantomJSDriver();
	}
	
	public void login(String username, String password) {
		driver.get("xxx");
		
		WebElement usernameField = driver.findElement(By.xpath("//input[contains(@id,'UserName')]"));
		usernameField.clear();
		usernameField.sendKeys(username);
		
		WebElement passwordField = driver.findElement(By.xpath("//input[contains(@id,'Password')]"));
		passwordField.clear();
		passwordField.sendKeys(password);
		
		WebElement loginButton = driver.findElement(By.xpath("//input[contains(@id,'Login')]"));
		loginButton.click();
	}
	
	public void gotoReports() {
		WebElement reportsLink = driver.findElement(By.xpath("//a[text()='Reports']"));
		reportsLink.click();
	}
	
	public void extractSimulationReports(String simulationFilter, LdxReportCallback callback) throws Exception {
		String relevantRowXpathStub = "//table[contains(@id,'CompleteGames')]//tr/td[1][text()='%s']/..";
		String relevantRowXpath = String.format(relevantRowXpathStub, simulationFilter);
		int numRelevantRows = driver.findElements(By.xpath(relevantRowXpath)).size();
		for(int relevantRowIndex = 0; relevantRowIndex < numRelevantRows; relevantRowIndex++) {
			// LAME: we have to do findElements each time to avoid stale elements... need to figure
			// out how to fix this, if possible...
			WebElement reportRow = driver.findElements(By.xpath(relevantRowXpath)).get(relevantRowIndex);
			
			String simulation = reportRow.findElement(By.xpath("td[1]")).getText();
			String team = reportRow.findElement(By.xpath("td[2]")).getText();
			String code = reportRow.findElement(By.xpath("td[3]")).getText();
			String playDate = reportRow.findElement(By.xpath("td[4]")).getText();
			
			// open the report by clicking the "Report" button (opens in a new window)
			reportRow.findElement(By.xpath("td[5]/input")).click();
			
			// switch driver to look at the newly opened window
			String mainWindowHandle = driver.getWindowHandle();
			for (String handle : driver.getWindowHandles()) {
				if(!handle.equals(mainWindowHandle)) {
					driver.switchTo().window(handle);
				}
			}
			
			// page is open, execute the callback!
			try {
				callback.handleReport(simulation, team, code, playDate, driver);
			} finally {
				// close popped up window, we're done with it
				driver.close();
				
				// switch driver to look at original window
				driver.switchTo().window(mainWindowHandle);
			}
		}
		
	}
	
	public void close() {
		driver.close();
	}
}
