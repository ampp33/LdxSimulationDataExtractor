package org.malibu.msu.ldx.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.malibu.msu.ldx.LdxConfigLoader;
import org.malibu.msu.ldx.LdxReportMapping;
import org.malibu.msu.ldx.LdxReportSpreadsheet;
import org.malibu.msu.ldx.web.LdxReportCallback;
import org.malibu.msu.ldx.web.LdxReportProcessorConfig;
import org.malibu.msu.ldx.web.LdxWebReportProcessor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LdxSimulationDataExtractorUi {

	private JFrame frmLdxSimulationData;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextField simulationFilterField;
	private JLabel statusLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					LdxSimulationDataExtractorUi window = new LdxSimulationDataExtractorUi();
					window.frmLdxSimulationData.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LdxSimulationDataExtractorUi() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLdxSimulationData = new JFrame();
		frmLdxSimulationData.setTitle("LDX Simulation Data Extractor v1.0");
		frmLdxSimulationData.setResizable(false);
		frmLdxSimulationData.setBounds(100, 100, 601, 181);
		frmLdxSimulationData.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLdxSimulationData.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(10, 66, 70, 14);
		frmLdxSimulationData.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(294, 66, 70, 14);
		frmLdxSimulationData.getContentPane().add(lblPassword);
		
		usernameField = new JTextField();
		usernameField.setBounds(90, 63, 194, 20);
		frmLdxSimulationData.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(374, 63, 211, 20);
		frmLdxSimulationData.getContentPane().add(passwordField);
		passwordField.setColumns(10);
		
		JLabel lblSimulation = new JLabel("Simulation:");
		lblSimulation.setBounds(10, 94, 70, 14);
		frmLdxSimulationData.getContentPane().add(lblSimulation);
		
		simulationFilterField = new JTextField();
		simulationFilterField.setBounds(90, 91, 194, 20);
		frmLdxSimulationData.getContentPane().add(simulationFilterField);
		simulationFilterField.setColumns(10);
		
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						// prompt for output file path
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setSelectedFile(new File("output.xlsx"));
						int choice = fileChooser.showSaveDialog(frmLdxSimulationData);
						if(choice != JFileChooser.APPROVE_OPTION) {
							// halt processing if they don't select a file
							return;
						}
						
						// process
						LdxReportProcessorConfig config
								= new LdxReportProcessorConfig(fileChooser.getSelectedFile().getAbsolutePath(),
																usernameField.getText(),
																new String(passwordField.getPassword()),
																simulationFilterField.getText());
						boolean success = runExtraction(config);
						if(success) {
							updateStatus("Done! Success!");
							JOptionPane.showMessageDialog(frmLdxSimulationData, "Success!");
						} else {
							JOptionPane.showMessageDialog(frmLdxSimulationData, "An error occurred during processing");
						}
					}
				}).start();
			}
		});
		runButton.setBounds(496, 119, 89, 23);
		frmLdxSimulationData.getContentPane().add(runButton);
		
		statusLabel = new JLabel("<status>");
		statusLabel.setBounds(10, 123, 476, 14);
		frmLdxSimulationData.getContentPane().add(statusLabel);
		
		JLabel lblNewLabel = new JLabel("LDX Simulation Data Extractor");
		lblNewLabel.setForeground(new Color(255, 140, 0));
		lblNewLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));
		lblNewLabel.setBounds(10, 11, 342, 33);
		frmLdxSimulationData.getContentPane().add(lblNewLabel);
		
		JLabel lblV = new JLabel("v1.0");
		lblV.setBounds(294, 35, 46, 14);
		frmLdxSimulationData.getContentPane().add(lblV);
	}
	
	private boolean runExtraction(LdxReportProcessorConfig config) {
		updateStatus("Loading configuration...");
		List<LdxReportMapping> ldxReportMappings = null;
		try {
			LdxConfigLoader ldxMapper = new LdxConfigLoader();
			ldxReportMappings = ldxMapper.loadMappingConfig();
		} catch (Exception ex) {
			updateStatus("Failed to load configuration: " + ex.getMessage());
			return false;
		}
		
		LdxReportSpreadsheet ss = null;
		try {
			updateStatus("Preparing spreadsheet from template...");
			ss = new LdxReportSpreadsheet();
		} catch (Exception ex) {
			updateStatus("Failed to prepare spreadsheet: " + ex.getMessage());
			return false;
		}
		
		try {
			runWebHandler(config, ss, ldxReportMappings);
		} catch (Exception ex) {
			updateStatus("Error occurred while extracting reports: " + ex.getMessage());
			return false;
		} finally {
			updateStatus("Saving spreadsheet...");
			try {
				ss.saveSpreadsheet(config.getOutputFilePath());
			} catch (Exception ex) {
				updateStatus("Failed to save spreadsheet!  Error: " + ex.getMessage());
				return false;
			}
		}
		
		return true;
	}
	
	private void runWebHandler(LdxReportProcessorConfig config, LdxReportSpreadsheet ss, List<LdxReportMapping> ldxReportMappings) throws Exception {
		updateStatus("Accessing LDX website...");
		LdxWebReportProcessor reportProcessor = new LdxWebReportProcessor();
		reportProcessor.process(config, new LdxReportCallback() {
			public void handleReport(String simulation, String team, String code, String playDate, WebDriver driver) throws Exception {
				// write values not found on report (found on simulation table page)
				ss.writeValueToColumn("Team", team);
				ss.writeValueToColumn("Code", code);
				ss.writeValueToColumn("Play Date", playDate);
				
				updateStatus(String.format("Extracting: [ team : '%s' ] [ code : '%s' ] [ playdate : '%s' ]", team, code, playDate));
				
				// write mapped entries to spreadsheet
				for (LdxReportMapping ldxReportMapping : ldxReportMappings) {
					WebElement element = driver.findElement(By.xpath(ldxReportMapping.getxPath()));
					ss.writeValueToColumn(ldxReportMapping.getColumnHeader(), element.getText());
				}
				
				// close and move onto the next row
				ss.nextRow();
			}
		});
	}
	
	private void updateStatus(String text) {
		statusLabel.setText(text);
	}
}
