package org.malibu.msu.ldx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LdxConfigLoader {
	
	public static final String MAPPING_FILE_PATH = "\\resources\\mapping.csv";
	
	public List<LdxReportMapping> loadMappingConfig() throws IOException {
		
		List<LdxReportMapping> ldxMappings = new LinkedList<>();
		
		try ( BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("workingDir") + MAPPING_FILE_PATH)))) {
			String line = null;
			reader.readLine(); // skip first row, which contains the headers
			while((line = reader.readLine()) != null) {
				String[] csvValues = line.split(",");
				if(csvValues.length != 2) {
					throw new IOException("Bad mapping file entry found (incorrect number of params): " + line);
				}
				LdxReportMapping value = new LdxReportMapping();
				value.setColumnHeader(csvValues[0]);
				value.setxPath(csvValues[1]);
				ldxMappings.add(value);
			}
		}
		
		return ldxMappings;
	}
	
}
