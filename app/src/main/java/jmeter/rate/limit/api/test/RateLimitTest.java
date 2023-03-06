package jmeter.rate.limit.api.test;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.timers.ConstantThroughputTimer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ViewResultsFullVisualizer;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import static org.apache.jmeter.JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY;

import java.io.FileOutputStream;

import java.io.File;

public class RateLimitTest {

    public static void main(String[] args) throws Exception {
        // Initialize JMeter
        JMeterUtils.loadJMeterProperties("/home/duyet-mai/repos/apache-jmeter-5.5/bin/jmeter.properties");
        JMeterUtils.setJMeterHome("/home/duyet-mai/repos/apache-jmeter-5.5/");

        JMeterUtils.initLocale();

        // JMeter Test Plan
        HashTree testPlanTree = new HashTree();

        // Add a listener to the test plan
        ResultCollector resultCollector = new ResultCollector(new Summariser());
        resultCollector.setFilename("results.jtl");

        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("example.com");
        httpSampler.setPort(80);
        httpSampler.setPath("/api/items");
        httpSampler.setMethod(HTTPConstants.GET);

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.initialize();

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopController);

        // Constant Throughput Timer
        ConstantThroughputTimer throughputTimer = new ConstantThroughputTimer();
        throughputTimer.setProperty(new StringProperty("calcMode", "All active threads in current thread group"));
        throughputTimer.setThroughput(10); // Change this to your desired rate limit

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

        // Save the results to a file
        // JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.classname",
        //         "org.apache.jmeter.report.dashboard.HtmlTemplateExporter");
        JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, "report-output/dashboard");

        ReportGenerator generator = new ReportGenerator("results.jtl", null);
        generator.generate();
        // Save the results to a file
        // File testPlanFile = new File("results.jmx");
        // FileOutputStream output = new FileOutputStream(testPlanFile);
        // SaveService.saveTree(testPlanTree, output);
    }
}