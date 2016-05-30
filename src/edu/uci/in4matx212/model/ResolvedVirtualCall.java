package edu.uci.in4matx212.model;

import java.util.Set;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;

public class ResolvedVirtualCall {

	JVirtualInvokeExpr virtualCall;
	Set<SootClass> availableTypes;

	public ResolvedVirtualCall(JVirtualInvokeExpr virtualCall, Set<SootClass> availableTypes) {
		this.virtualCall = virtualCall;
		this.availableTypes = availableTypes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String baseName = ((JimpleLocal) virtualCall.getBase()).getName();
		String methodName = virtualCall.getMethod().getName();
		for (SootClass sootClass : availableTypes) {
			SootMethod method = sootClass.getMethod(virtualCall.getMethod().getSubSignature());
			if(sb.length() > 0)
				sb.append(", ");
			sb.append(method.getDeclaringClass().getShortName() + "." + method.getName());
		}
		return baseName + "." + methodName + "(...), the methods potentially invoked here are: " + sb.toString();
	}

}
