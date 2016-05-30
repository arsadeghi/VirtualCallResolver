package edu.uci.in4matx212.model.cfa;

import java.util.HashSet;
import java.util.Set;

import soot.RefType;
import soot.SootClass;
import soot.Type;
import soot.jimple.internal.JNewExpr;

public abstract class ConstGraphNode {

	public ConstGraphNode() {
		incomingNodes = new HashSet<>();
		outgoingNodes = new HashSet<>();
		reachingType = new HashSet<>();
	}

	Set<ConstGraphNode> incomingNodes;
	Set<ConstGraphNode> outgoingNodes;
	Set<SootClass> reachingType;

	public boolean hasEdge() {
		return !incomingNodes.isEmpty() && !outgoingNodes.isEmpty();
	}

	public boolean isRoot() {
		return incomingNodes.isEmpty();
	}

	public boolean hasType() {
		return !reachingType.isEmpty();
	}

	public void propagateTypes() {
		for (ConstGraphNode node : outgoingNodes) {
			node.reachingType.addAll(reachingType);
			node.propagateTypes();
		}
	}

	public void drawNode(Set<String> edges) {
		for (ConstGraphNode node : outgoingNodes) {
			edges.add("\"" + this.toString() + "\"->\"" + node.toString() + "\";\n");
			node.drawNode(edges);
		}
	}

	public Set<SootClass> getReachingType() {
		return reachingType;
	}

	public void addSubset(ConstGraphNode subset) {
		subset.outgoingNodes.add(this);
		this.incomingNodes.add(subset);
	}

	public void addReachingType(JNewExpr expr) {
		Type type = expr.getType();
		if (!(type instanceof RefType))
			return;
		reachingType.add(((RefType) type).getSootClass());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		reachingType.forEach(sc -> sb.append(sc.getShortName()).append(","));
		return "\n Available Types {" + sb +"}";
	}
}
