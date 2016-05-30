package edu.uci.in4matx212.model.cfa;

import soot.SootMethod;

public class ThisConstGraphNode extends ConstGraphNode {

	SootMethod method;

	public ThisConstGraphNode(SootMethod method) {
		super();
		this.method = method;
	}

	@Override
	public String toString() {
		return "This: " + method.getDeclaringClass().getName() + "." + method.getName() + super.toString();
	}

	public static boolean isEqual(SootMethod method, ConstGraphNode node) {
		if (!(node instanceof ThisConstGraphNode))
			return false;
		return ((ThisConstGraphNode) node).method.getSubSignature().equals(method.getSubSignature());
	}

}
