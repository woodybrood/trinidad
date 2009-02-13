package com.neuri.trinidad;

import java.io.File;
import java.io.IOException;

public interface TestResultRepository {
	void recordTestResult(TestResult result) throws IOException; 
	void addFile(File f, String relativeFilePath) throws IOException;
}
