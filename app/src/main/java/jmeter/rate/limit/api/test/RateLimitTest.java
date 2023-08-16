package jmeter.rate.limit.api.test;

import org.apache.jmeter.control.LoopController;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.timers.ConstantThroughputTimer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import static org.apache.jmeter.JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class RateLimitTest {

    public static void main(String[] args) throws Exception {

        final String JMETER_PROPERTIES_PATH = "/home/ubuntu/software/apache-jmeter-5.5/bin/jmeter.properties";
        final String JMETER_HOME_PATH = "/home/ubuntu/software/apache-jmeter-5.5";
        final String RESULT_FOLDER_PATH = "/home/ubuntu/jmeter_report";

        
        final String DOMAIN_NAME = "example.com"; 
        final String API_PATH = "example-client/trading/api/providers/404656/trades/open/all"; 


        final int NUMBER_OF_USERS = 20;
        final int RAMP_UP_TIME = 10;
        final float NUMBER_OF_REQUEST_PER_SECOND = 2;
        final int DURATION_SECONDS = 30*60; 


        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String nameByTimestamp = now.format(formatter);
        

        final String resultJltFilePath = String.format("%s/results%s.jtl",RESULT_FOLDER_PATH, nameByTimestamp);
        final String htmlReportDashboardFilePath=  String.format("%s/dashboard/%s",RESULT_FOLDER_PATH,nameByTimestamp);

        // Initialize JMeter

        JMeterUtils.loadJMeterProperties(JMETER_PROPERTIES_PATH);
        JMeterUtils.setJMeterHome(JMETER_HOME_PATH);

        JMeterUtils.initLocale();

        // JMeter Test Plan
        HashTree testPlanTree = new HashTree();

        // Add a listener to the test plan
        ResultCollector resultCollector = new ResultCollector(new Summariser());
        resultCollector.setFilename(resultJltFilePath);

        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setProtocol("https");
        httpSampler.setDomain(DOMAIN_NAME);
        httpSampler.setPort(443);
        httpSampler.setPath(API_PATH);
        httpSampler.setMethod(HTTPConstants.GET);


        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(-1);
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.initialize();

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setNumThreads(NUMBER_OF_USERS);
        threadGroup.setScheduler(true);
        threadGroup.setRampUp(RAMP_UP_TIME);
        threadGroup.setSamplerController(loopController);
        threadGroup.setDuration(DURATION_SECONDS); 


        // Constant Throughput Timer
        ConstantThroughputTimer throughputTimer = new ConstantThroughputTimer();
        throughputTimer.setProperty(new StringProperty("calcMode", "All active threads in current thread group"));
        throughputTimer.setThroughput(NUMBER_OF_REQUEST_PER_SECOND*NUMBER_OF_USERS*60); // Change this to your desired rate limit

        // Test Plan
        TestPlan testPlan = new TestPlan("Rate Limit Test Plan");

        // Construct Test Plan from previously initialized elements
        testPlanTree.add("testPlan", testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(loopController);
        threadGroupHashTree.add(httpSampler);
        threadGroupHashTree.add(throughputTimer);

        // Run Test Plan
        testPlanTree.add(testPlanTree.getArray()[0], resultCollector);
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(testPlanTree);
        jmeter.run();

        
        JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, String.format(htmlReportDashboardFilePath));

        
        ReportGenerator generator = new ReportGenerator(resultJltFilePath, null);
        generator.generate();

    }
}
