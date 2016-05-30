package edu.uci.in4matx212;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import soot.SootClass;
import edu.uci.in4matx212.cgc.CFA;
import edu.uci.in4matx212.cgc.CHA;
import edu.uci.in4matx212.cgc.ICallGraphGenerator;
import edu.uci.in4matx212.cgc.RA;
import edu.uci.in4matx212.cgc.RTA;

public class Main {

	static final String GRAPH_OUT = "/Users/alireza/Desktop/ConstGraph.gv";
	final static String SOOT_CLASS_PATH = "/Users/alireza/Documents/workspaces/workspace/HW5_Test1/src";
	final static String PKG_PREFIX = "edu.uci.in4matx212.test";
	final static String TARGET_CLASS = "Main";

	final static ICallGraphGenerator[] CGG_ALGORITHMS = new ICallGraphGenerator[] { new RA(), new CFA(), new CHA(), new RTA() };
	final static ICallGraphGenerator DEFAULT_CGG_ALGORITHMS = new RA();

	public static void main(String[] args) {

		Options options = createOptions();
		try {
			CommandLine line = new DefaultParser().parse(options, args);
			processOptions(line);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cgg", options);
			System.exit(1);
		}
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(Option.builder("alg").longOpt("algorithm").numberOfArgs(1).required(true).desc("the name of call graph generator algorithm").build());
		options.addOption(Option.builder("cp").longOpt("classPath").numberOfArgs(1).required(true).desc("directory containing the analysis classes").build());
		options.addOption(Option.builder("pkg").longOpt("package").numberOfArgs(1).required(true).desc("package prefix of the classes under analysis").build());
		options.addOption(Option.builder("cls").longOpt("class").numberOfArgs(1).required(true).desc("target class to calculagte call graph").build());
		options.addOption(Option.builder("v").longOpt("verbose").numberOfArgs(1).desc("show process results (e.g. constraint graph)").build());
		options.addOption(Option.builder("t").longOpt("time").desc("calculate performance").build());
		return options;
	}

	private static void processOptions(CommandLine line) {
		String classPath = line.getOptionValue("cp", SOOT_CLASS_PATH);
		String packagePrefix = line.getOptionValue("pkg", PKG_PREFIX);
		String targetClass = line.getOptionValue("cls", TARGET_CLASS);
		String algName = line.getOptionValue("alg");
		Boolean verbose = line.hasOption("v");
		String outputFolder = null;
		if (verbose)
			outputFolder = line.getOptionValue("v", GRAPH_OUT);
		Boolean measureTime = line.hasOption("t");
		ICallGraphGenerator callGraphGenerator = Arrays.asList(CGG_ALGORITHMS).stream().filter(a -> a.getAlgorithmShortName().equals(algName)).findFirst()
				.orElse(DEFAULT_CGG_ALGORITHMS);
		Set<SootClass> loadedClasses = Util.loadClasses(classPath, packagePrefix);
		SootClass sootTargetClass = loadedClasses.stream().filter(c -> c.getName().equals(packagePrefix + "." + targetClass)).findFirst().get();
		run(callGraphGenerator, verbose, measureTime, sootTargetClass, loadedClasses, outputFolder);
	}

	private static void run(ICallGraphGenerator callGraphGenerator, Boolean verbose, Boolean measureTime, SootClass sootTargetClass,
			Set<SootClass> loadedClasses, String outputFolder) {
		long startTime = System.currentTimeMillis();
		callGraphGenerator.init(loadedClasses, verbose, outputFolder);
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Resolving virtual calls using " + callGraphGenerator.getAlgorithmName() + "(" + callGraphGenerator.getAlgorithmShortName() + "): ");
		System.out.println("===============================================================================");
		Util.resolveVirtualCalls(sootTargetClass, callGraphGenerator);
		System.out.println("===============================================================================");
		if (measureTime)
			System.out.println("Processing time " + duration + "(ms)");
	}
}
