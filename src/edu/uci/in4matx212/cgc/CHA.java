package edu.uci.in4matx212.cgc;

import java.util.Set;

import soot.RefType;
import soot.SootClass;
import soot.jimple.internal.JVirtualInvokeExpr;
import edu.uci.in4matx212.Util;
import edu.uci.in4matx212.model.ResolvedVirtualCall;

public class CHA extends CallGraphGenerator{

	@Override
	public String getAlgorithmName() {
		return "Class Hierarchy Analysis";
	}

	@Override
	public String getAlgorithmShortName() {
		return "CHA";
	}

	@Override
	public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall) {
		return super.resolveCall(virtualCall);
	}

	@Override
	public void init(Set<SootClass> loadedClasses, Boolean showProcessResults, String outputFolder) {
		super.init(loadedClasses, showProcessResults, outputFolder);
	}

	@Override
	protected boolean isTypeAvailable(JVirtualInvokeExpr virtualCall, SootClass sootClass) {
		SootClass superClass = ((RefType) virtualCall.getBase().getType()).getSootClass();
		return Util.isSubType(sootClass, superClass);
	}

}
