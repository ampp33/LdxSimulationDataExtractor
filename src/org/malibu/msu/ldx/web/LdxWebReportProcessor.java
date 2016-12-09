package org.malibu.msu.ldx.web;

public class LdxWebReportProcessor {
	
	public void process(LdxReportProcessorConfig config, LdxReportCallback callback) throws Exception {
		LdxWebHandler webHandler = new LdxWebHandler();
		webHandler.login(config.getUsername(), config.getPassword());
		webHandler.gotoReports();
		try {
			webHandler.extractSimulationReports(config.getSimulationFilter(), callback);
		} finally {
			webHandler.close();
		}
	}
	
}
