package edu.uci.in4matx212;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import edu.uci.in4matx212.cgc.ICallGraphGenerator;
import edu.uci.in4matx212.model.ResolvedVirtualCall;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.options.Options;

public class Util {

	public static Set<SootMethod> findSimilarMethods(String signature, Set<SootClass> sootClasses) {
		Set<SootMethod> similarMethods = new HashSet<>();
		for (SootClass sootClass : sootClasses) {
			for (SootMethod sootMethod : sootClass.getMethods()) {
				if (sootMethod.getSubSignature().equals(signature)) {
					similarMethods.add(sootMethod);
				}
			}
		}
		return similarMethods;
	}

	public static boolean isSubType(SootClass subClass, SootClass superClass){
		SootClass sootClass = subClass;
		while(!sootClass.getName().equals("java.lang.Object")){
			if(sootClass.equals(superClass))
				return true;
			sootClass =sootClass.getSuperclass();
		}
		return false;
	}
	
	public static Set<ResolvedVirtualCall> resolveVirtualCalls(SootClass sClass, ICallGraphGenerator callGraphGenerator) {
		Set<ResolvedVirtualCall> result = new HashSet<>();
		for (SootMethod sootMethod : sClass.getMethods()) {
			Body methodBody = sootMethod.retrieveActiveBody();
			for (Unit unit : methodBody.getUnits()) {
				for (ValueBox box : unit.getUseBoxes()) {
					if (box.getValue() instanceof JVirtualInvokeExpr) {
						JVirtualInvokeExpr virtualCall = (JVirtualInvokeExpr) box.getValue();
						ResolvedVirtualCall resolvedVirtualCall = callGraphGenerator.resolveCall(virtualCall);
						System.out.println(resolvedVirtualCall.toString());
						result.add(resolvedVirtualCall);
					}

				}

			}
		}
		return result;
	}

	public static Set<SootClass> loadClasses(String sootClassPath, String packagePrefix) {
		Set<SootClass> sClasses = new HashSet<>();
		applySootOptions(sootClassPath);
		for (String className : findClassNames(sootClassPath, packagePrefix)) {
			SootClass sClass = Scene.v().loadClassAndSupport(packagePrefix + "." + className);
			sClass.setApplicationClass();
			sClasses.add(sClass);
		}
		return sClasses;
	}

	private static Set<String> findClassNames(String sootClassPath, String packagePrefix) {
		File folder = new File(sootClassPath + "/" + packagePrefix.replace(".", "/"));
		return Arrays.asList(folder.listFiles()).stream().filter(f -> f.getName().endsWith(".java"))
				.map(f -> f.getName().substring(0, f.getName().lastIndexOf("."))).collect(Collectors.toSet());
	}

	private static void applySootOptions(String soot_class_path) {
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_dir(Collections.singletonList(soot_class_path));
	}
}
