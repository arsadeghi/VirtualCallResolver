package edu.uci.in4matx212.cgc;

import java.util.Set;

import soot.SootClass;
import soot.jimple.internal.JVirtualInvokeExpr;
import edu.uci.in4matx212.model.ResolvedVirtualCall;

public class RA extends CallGraphGenerator{

	@Override
	public String getAlgorithmName() {
		return "Reachability Analysis";
	}

	@Override
	public String getAlgorithmShortName() {
		return "RA";
	}

	@Override
	public void init(Set<SootClass> loadedClasses, Boolean showProcessResults, String outputFolder) {
		super.init(loadedClasses, showProcessResults, outputFolder);
	}

	@Override
	public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall) {
		return super.resolveCall(virtualCall);
	}

}
