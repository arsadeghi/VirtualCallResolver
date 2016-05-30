package edu.uci.in4matx212.model.cfa;

import soot.Local;
import soot.Value;

public class LocalConstGraphNode extends ConstGraphNode {

	Local local;

	public LocalConstGraphNode(Local local) {
		super();
		this.local = local;
	}

	@Override
	public String toString() {
		return "Local: " + local.toString() + "(" + local.getType() + ")" + super.toString();
	}

	public static boolean isEqual(Value val, ConstGraphNode node) {
		if (!(node instanceof LocalConstGraphNode))
			return false;
		return ((LocalConstGraphNode) node).local.equals(val);
	}
}
