package org.Abhinandan_Basu_Project.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance(){

        if(extent == null){

            ExtentSparkReporter reporter =
                    new ExtentSparkReporter("reports/StarlaJewelsReport.html");

            reporter.config().setReportName("Starla Jewels API Automation Report");
            reporter.config().setDocumentTitle("API Test Execution");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }

        return extent;
    }
}
