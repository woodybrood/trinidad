package com.neuri.trinidad.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.neuri.trinidad.TestEngine;
import com.neuri.trinidad.TestRepository;
import com.neuri.trinidad.TestRunner;
import com.neuri.trinidad.fitnesserunner.FitNesseRepository;
import com.neuri.trinidad.fitnesserunner.FitTestEngine;
import com.neuri.trinidad.fitnesserunner.SlimTestEngine;
import com.neuri.trinidad.transactionalrunner.TransactionalTestEngineDecorator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Goal which runs a trinidad test execution.
 * 
 * @goal run-tests
 * 
 * @phase integration-test
 * @requiresDependencyResolution test
 */
public class TrinidadMojo extends AbstractMojo {
	/**
	 * this is an embedded resource parameter, it is set automatically using maven - ignore it 
	 * @parameter expression="${project}" */
	private org.apache.maven.project.MavenProject mavenProject;

	/**
	 * The URI for the result repository (output directory if using a file
	 * result repository).
	 * 
	 * @parameter expression="${trinidad.output}"
	 *            default-value="${project.build.directory}/trinidad"
	 * @required
	 */
	private String resultRepositoryUri;

	/**
	 * Engine to be used to run the tests. This can be either <b>fit</b>,
	 * <b>slim</b> or a fully-qualified class name of a third-party TestEngine
	 * implementation.
	 * 
	 * @parameter expression="${trinidad.engine}"
	 * @required
	 */
	private String testEngine;

	/**
	 * Test repository type. This can be <b>fitnesse</b> or a fully qualified
	 * name of a third-party TestRepository implementation
	 * 
	 * @parameter expression="${trinidad.test.type}"
	 * @required
	 */
	private String testRepositoryType;

	/**
	 * The URI for the test repository. The format depends on the test
	 * repository type. For fitnesse test repositories, this is the path to the
	 * main fitnesse
	 * 
	 * @parameter expression="${trinidad.test.location}"
	 * @required
	 */
	private String testRepositoryUri;

	/**
	 * Set this to true for Maven to break the build if any of the tests have
	 * failed. Leave it set to false to just print out a warning
	 * 
	 * @parameter default-value="false"
	 * 
	 */
	private boolean breakBuildOnFailure;

	/**
	 * Set this to true for Maven to stop running tests after the first failed test/suite. 
	 * Leave it set to false to run all tests regardless of failures.
	 * 
	 * @parameter default-value="false"
	 * 
	 */
	private boolean stopAfterFirstFailure;
	
	/**
	 * A set of suite names to execute.
	 * 
	 * @parameter
	 */
	private String[] suites = new String[0];

	/**
	 * A set of test names to execute
	 * 
	 * @parameter
	 */
	private String[] tests = new String[0];

	/**
	 * a single test to execute
	 * 
	 * @parameter alias="test" expression="${trinidad.run.test}"
	 */
	private String singleTest = null;

	/**
	 * a single test to execute
	 * 
	 * @parameter alias="suite" expression="${trinidad.run.suite}"
	 */
	private String singleSuite = null;

	
	/**
	 * An optional Spring context file for transactional test rollback. If specified, the test engine will
	 * actually be wrapped into a spring transactional test engine decorator. See transactionalrunner documentation for more info
	 * 
	 * @parameter expression="${trinidad.spring.context}"
	 */
	private String springContext;
	
	public void execute() throws MojoExecutionException {
		processDefaults();
		createOutputDirectory();
		final ClassLoader cl = initProjectTestClassLoader();
		Thread runnerThread = new Thread(new Runnable() {
			public void run() {
				try {
					Object testRunnerInstance = loadTestRunner(cl);
					runIndividualTests(testRunnerInstance);
					runSuites(testRunnerInstance);
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		});
		runnerThread.setContextClassLoader(cl);
		runnerThread.start();
		try {
			runnerThread.join();
		} catch (InterruptedException e) {
			getLog().error(e);
			throw new MojoExecutionException("exception in the mojo runner thread",e);
		}
		if (breakBuildOnFailure && totalWrongOrException> 0)
			throw new MojoExecutionException(
				"Acceptance test run failed. Total "+totalWrongOrException+ " failing tests or exceptions. See log for more information.");		
	}

	private void createOutputDirectory() {
		File output = new File(this.resultRepositoryUri);
		if (!output.exists())
			output.mkdirs();
	}

	private void runSuites(Object testRunnerInstance)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException,
			MojoExecutionException {
		Method runSuite = testRunnerInstance.getClass().getMethod("runSuite",
				String.class);
		for (String suite : suites) {
			if (stopAfterFirstFailure && totalWrongOrException>0 ) break;
			getLog().info("running suite=" + suite);
			Object counts = runSuite.invoke(testRunnerInstance, suite);
			int wrongOrException = getWrongPlusExceptions(counts);
//			if (breakBuildOnFailure && wrongOrException > 0)
//				throw new MojoExecutionException(
//						"Acceptance test suite run failed:" + suite);
			totalWrongOrException+=wrongOrException;
		}
	}

	private int totalWrongOrException=0;
	
	private void runIndividualTests(Object testRunnerInstance)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException,
			MojoExecutionException {
		Method runTest = testRunnerInstance.getClass().getMethod("runTest",
				String.class);
		for (String test : tests) {
			if (stopAfterFirstFailure && totalWrongOrException>0 ) break;
			getLog().info("running test=" + test);
			Object counts = (Integer) runTest.invoke(testRunnerInstance, test);
			int wrongOrException = getWrongPlusExceptions(counts);
			totalWrongOrException+=wrongOrException;
		}
	}

	private Object loadTestRunner(ClassLoader cl)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		Class<?> testEngineClass = cl.loadClass(this.testEngine);
		Class<?> testRepositoryClass = cl.loadClass(this.testRepositoryType);

		Object testEngine = testEngineClass.newInstance();

		getLog().debug("loaded test engine=" + testEngine);

		if (springContext!=null && springContext.trim().length()>0){
			Class<?> transactionalEngineClass=cl.loadClass(TransactionalTestEngineDecorator.class.getName());
			Constructor<?> ct=transactionalEngineClass.getConstructor(String.class, cl.loadClass(TestEngine.class.getName()));
			testEngine=ct.newInstance(springContext,testEngine);
			getLog().debug("loaded transactional decorator test engine=" + testEngine);	
		}
		Object testRepository = testRepositoryClass.newInstance();
		getLog().debug("loaded test repository=" + testRepository);

		Method setUri = testRepositoryClass.getMethod("setUri", String.class);
		setUri.invoke(testRepository, testRepositoryUri);

		Class<?> c = cl.loadClass(TestRunner.class.getName());
		Object testRunnerInstance = c.getConstructor(
				cl.loadClass(TestRepository.class.getName()),
				cl.loadClass(TestEngine.class.getName()), String.class)
				.newInstance(testRepository, testEngine, resultRepositoryUri);
		getLog().debug("loaded test runner=" + testRunnerInstance);
		return testRunnerInstance;
	}

	private int getWrongPlusExceptions(Object counts)
			throws IllegalAccessException, NoSuchFieldException {
		int wrong = counts.getClass().getField("wrong").getInt(counts);
		int exceptions = counts.getClass().getField("exceptions")
				.getInt(counts);
		int wrongOrException = wrong + exceptions;
		return wrongOrException;
	}

	private void processDefaults() {
		if (singleTest != null)
			tests = new String[] { singleTest };
		if (singleSuite != null)
			suites = new String[] { singleSuite };
		if ("FIT".equalsIgnoreCase(testEngine))
			testEngine = FitTestEngine.class.getName();
		if ("SLIM".equalsIgnoreCase(testEngine))
			testEngine = SlimTestEngine.class.getName();
		if ("FITNESSE".equalsIgnoreCase(testRepositoryType))
			testRepositoryType = FitNesseRepository.class.getName();
	}

	private ClassLoader initProjectTestClassLoader()
			throws MojoExecutionException {
		try {
			List<String> classpath = mavenProject.getTestClasspathElements();
			getLog().debug("class path=" + classpath);
			URL[] urlArray = new URL[classpath.size()];
			for (int i = 0; i < classpath.size(); i++) {
				urlArray[i] = new File(classpath.get(i)).toURI().toURL();
			}
			return new URLClassLoader(urlArray);
		} catch (Exception e) {
			throw new MojoExecutionException(
					"class loader initialisation failed", e);
		}
	}

	public TestRepository getTestRepository(String testRepositoryType,
			String testRepositoryUri) {
		try {
			if ("fitnesse".equalsIgnoreCase(testRepositoryType)) {
				return new com.neuri.trinidad.fitnesserunner.FitNesseRepository(
						testRepositoryUri);
			}
		} catch (IOException e) {
			throw new Error(
					"Cannot instantiate fitnesse test repository for uri "
							+ testRepositoryUri);
		}

		try {
			TestRepository repository = (TestRepository) Class.forName(
					testRepositoryType).newInstance();
			repository.setUri(testRepositoryUri);
			return repository;
		} catch (Exception e) {
			throw new Error("Cannot instantiate test repository by class name "
					+ testRepositoryType + " and uri " + testRepositoryUri);
		}
	}

}
