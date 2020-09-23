package com.deque;

import com.deque.attest.AttestConfiguration;
import com.deque.attest.AttestDriver;
import com.deque.attest.reporter.AttestReportingOptions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static com.deque.attest.matchers.IsAuditedForAccessibility.isAuditedForAccessibility;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.IOException;

/*
* ~ AttestCustomConfigTest~
* ~~~~~ IN THIS TEST CASE ~~~~~~~~~~
* << Shows local testing of HTML page in project >> 
* << Uses specific identifier and rule set in single test case >>
* << Uses specific Axe script and rule set for all test cases (shown in configuration) >>
* << Use of different 
*/
public class AttestCustomConfigTest {
    //In this case we point to specific LOCAL page within our project
    String absolutePath = new File("src/main/webapp/index.html").getAbsolutePath();
    static String macOSReporter = new File("src/test/resources/attest-reporter-macos").getAbsolutePath();
    AttestDriver attestDriver;
    private WebDriver webDriver;
    private AttestReportingOptions _reportOptions = new AttestReportingOptions();


    //Setup: Specific configuration used below: 
    //withAxeScript() allows users to use custom axe script of their choosing
    @Before
    public void setUp() throws Exception {
        AttestConfiguration.configure()
            .withAxeScript(getClass().getResource("/axe.js"))
            .testSuiteName("Homepage") //  Default reporting suite name, results will be associated 
            .outputDirectory("target/attest-reports/attest-customization");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("headless");
        webDriver = new ChromeDriver(options);
        attestDriver = new AttestDriver(webDriver);
        webDriver.get("file:///"+absolutePath);
    }

    @After
    public void tearDown() throws Exception {
        webDriver.quit();
    }
 
    //Reporting function that calls the executable after all the tests have been run and creates HTML and XML report off of it.
    //Requires the attest reporter executable to be within the project.  
    @AfterClass
    public static void createHtmlXmlReports() throws IOException{
        Runtime rt = Runtime.getRuntime();
        String command = macOSReporter +" target/attest-reports/attest-customization --dest target/attest-reports/attest-customization/a11y-html-report --format html+junit";
        rt.exec(command);
    }

    
    //TestCase: Using local page in the project and running Attest against it
    @Test
    public void auditHomePageForAccessibility() throws Exception {
        assertThat(attestDriver, isAuditedForAccessibility().logResults(_reportOptions.uiState("Homepage All")));
    }

    //TestCase: Using local page, but running it on one single section of the page using wcag AA rules. 
    //Notice the ability to chain functionality, such as within and according to to specify what you want to run. 
    @Test
    public void auditHomePageWithOptions() throws Exception {
        assertThat(attestDriver, isAuditedForAccessibility().within(".jumbotron").skipping("color-contrast").accordingTo("wcag2a").logResults(_reportOptions.uiState("Homepage section")));
    }

    //TestCase: Using local page, but running it against content we want excluded from the page.  
    @Test
    public void auditHomePageWithExclusions() throws Exception {
        assertThat(attestDriver, isAuditedForAccessibility().excluding(".jumbotron").logResults(_reportOptions.uiState("Homepage excluding section")));
    }


     //TestCase: Using local page, but checking for 3 accessibility rules anyways.  
     @Test
     public void auditHomePageWithCertainChecks() throws Exception {
         assertThat(attestDriver, isAuditedForAccessibility().checkingOnly("label", "aria-roles", "html-has-lang").logResults(_reportOptions.uiState("Homepage with certain checks")));
     }

}
