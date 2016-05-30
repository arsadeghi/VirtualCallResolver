package edu.uci.in4matx212.model.cfa;

import soot.SootMethod;

public class ReturnConstGraphNode extends ConstGraphNode {

	SootMethod method;

	public ReturnConstGraphNode(SootMethod method) {
		super();
		this.method = method;
	}

	@Override
	public String toString() {
		return "Return: " + method.getDeclaringClass().getName() + "." + method.getName() + "(" + method.getReturnType() + ")"+ super.toString();
	}

	public static boolean isEqual(SootMethod method, ConstGraphNode node) {
		if (!(node instanceof ReturnConstGraphNode))
			return false;
		return ((ReturnConstGraphNode) node).method.getSubSignature().equals(method.getSubSignature());
	}

}
