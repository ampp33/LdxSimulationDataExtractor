start javaw -DworkingDir="%~dp0\" ^
		-Dphantomjs.binary.path="%~dp0resources\phantomjs.exe" ^
		-cp "%~dp0lib\*;%~dp0LdxSimulationDataExtractor.jar" ^
		org.malibu.msu.ldx.ui.LdxSimulationDataExtractorUi