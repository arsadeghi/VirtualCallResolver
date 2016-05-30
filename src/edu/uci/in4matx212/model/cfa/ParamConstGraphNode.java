package edu.uci.in4matx212.model.cfa;

import soot.SootMethod;

public class ParamConstGraphNode extends ConstGraphNode {

	SootMethod method;
	int paramNumber;

	public ParamConstGraphNode(SootMethod method, int paramNumber) {
		super();
		this.method = method;
		this.paramNumber = paramNumber;
	}

	@Override
	public String toString() {
		return "Param: " + method.getDeclaringClass().getName() + "." + method.getName() + "(" + paramNumber + "," + method.getParameterType(paramNumber) + ")"+ super.toString();
	}

	public static boolean isEqual(SootMethod method, int i, ConstGraphNode node) {
		if (!(node instanceof ParamConstGraphNode))
			return false;
		ParamConstGraphNode paramNode = (ParamConstGraphNode) node;
		return paramNode.method.getSubSignature().equals(method.getSubSignature()) && paramNode.paramNumber == i;
	}
}
