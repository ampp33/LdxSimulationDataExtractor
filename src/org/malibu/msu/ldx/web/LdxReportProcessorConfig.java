package org.malibu.msu.ldx.web;

public class LdxReportProcessorConfig {
	private String username;
	private String password;
	private String simulationFilter;
	private String outputFilePath;
	
	public LdxReportProcessorConfig(String outputFilePath, String username, String password, String simulationFilter) {
		this.outputFilePath = outputFilePath;
		this.username = username;
		this.password = password;
		this.simulationFilter = simulationFilter;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSimulationFilter() {
		return simulationFilter;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}
}
