package edu.uci.in4matx212.cgc;

import java.util.Set;

import soot.SootClass;
import soot.jimple.internal.JVirtualInvokeExpr;
import edu.uci.in4matx212.model.ResolvedVirtualCall;

public interface ICallGraphGenerator {

	public String getAlgorithmName();

	public String getAlgorithmShortName();

	public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall);
	
	public void init(Set<SootClass> loadedClasses, Boolean showProcessResults, String outputFolder);
}
