package edu.uci.in4matx212.cgc;

import java.util.HashSet;
import java.util.Set;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import edu.uci.in4matx212.model.ResolvedVirtualCall;

public class RTA  extends CallGraphGenerator {

	Set<SootClass> instantiatedClasses = new HashSet<>(); 
	
	@Override
	public String getAlgorithmName() {
		return "Rapid Type Analysis";
	}

	@Override
	public String getAlgorithmShortName() {
		return "RTA";
	}

	@Override
	public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall) {
		return super.resolveCall(virtualCall);
	}

	@Override
	public void init(Set<SootClass> loadedClasses, Boolean showProcessResults, String outputFolder) {
		super.init(loadedClasses, showProcessResults, outputFolder);
		findInstantiatedClasses(loadedClasses);
	}
	
	
	@Override
	protected boolean isTypeAvailable(JVirtualInvokeExpr virtualCall, SootClass sootClass) {
		return instantiatedClasses.contains(sootClass);
	}

	private void findInstantiatedClasses(Set<SootClass> loadedClasses){
		for (SootClass sootClass : loadedClasses) {
			for (SootMethod sootMethod : sootClass.getMethods()) {
				if (sootMethod.isAbstract())
					continue;
				Body methodBody = sootMethod.retrieveActiveBody();
				for (Unit unit : methodBody.getUnits()) {
					for (ValueBox box : unit.getUseBoxes()) {
						if (box.getValue() instanceof JNewExpr){
							JNewExpr newExpr = (JNewExpr) box.getValue();
							instantiatedClasses.add(newExpr.getBaseType().getSootClass());
						}
					}
				}
			}
		}
	}

}
