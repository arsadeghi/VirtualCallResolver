package edu.uci.in4matx212.cgc;

import java.util.Set;
import java.util.stream.Collectors;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.internal.JVirtualInvokeExpr;
import edu.uci.in4matx212.Util;
import edu.uci.in4matx212.model.ResolvedVirtualCall;

public abstract class CallGraphGenerator implements ICallGraphGenerator {

	Set<SootClass> loadedClasses;

	@Override
	public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall) {
		Set<SootMethod> similarMethods = Util.findSimilarMethods(virtualCall.getMethod().getSubSignature(), loadedClasses);
		Set<SootClass> availableTypes = similarMethods.stream().map(c -> c.getDeclaringClass()).filter(c -> isTypeAvailable(virtualCall, c))
				.collect(Collectors.toSet());
		return new ResolvedVirtualCall(virtualCall, availableTypes);
	}

	/**
	 * @param virtualCall  
	 * @param sootClass 
	 */
	protected boolean isTypeAvailable(JVirtualInvokeExpr virtualCall, SootClass sootClass) {
		return true;
	}

	@Override
	public void init(Set<SootClass> loadedClasses, Boolean showProcessResults, String outputFolder) {
		this.loadedClasses = loadedClasses;
	}

	
	
}
