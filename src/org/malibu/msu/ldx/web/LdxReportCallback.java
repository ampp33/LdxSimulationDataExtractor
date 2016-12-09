package org.malibu.msu.ldx.web;

import org.openqa.selenium.WebDriver;

public interface LdxReportCallback {
	public void handleReport(String simulation, String team, String code, String playDate, WebDriver driver) throws Exception;
}
