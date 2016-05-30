package edu.uci.in4matx212.model.cfa;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import soot.SootMethod;
import soot.Value;

public class ConstGraph {

	private static Set<ConstGraphNode> nodes = new HashSet<>();

	public static void propagateTypes() {
		Set<ConstGraphNode> roots = getRoots();
		roots.forEach(ConstGraphNode::propagateTypes);
	}

	private static Set<ConstGraphNode> getRoots() {
		return nodes.stream().filter(ConstGraphNode::isRoot).collect(Collectors.toSet());
	}

	public static ConstGraphNode findLocalNode(Value opr) {
		return nodes.stream().filter(n -> n instanceof LocalConstGraphNode).filter(n -> LocalConstGraphNode.isEqual(opr, n)).findAny().get();
	}

	public static ConstGraphNode findThisNode(SootMethod method) {
		return nodes.stream().filter(n -> n instanceof ThisConstGraphNode).filter(n -> ThisConstGraphNode.isEqual(method, n)).findAny().get();
	}

	public static ConstGraphNode findReturnNode(SootMethod method) {
		return nodes.stream().filter(n -> n instanceof ReturnConstGraphNode).filter(n -> ReturnConstGraphNode.isEqual(method, n)).findAny().get();
	}

	public static ConstGraphNode findParamNode(SootMethod method, int i) {
		return nodes.stream().filter(n -> n instanceof ParamConstGraphNode).filter(n -> ParamConstGraphNode.isEqual(method, i, n)).findAny().get();
	}

	public static void addNode(ConstGraphNode node) {
		nodes.add(node);
	}

	public static Set<ConstGraphNode> getNodes() {
		return nodes;
	}

	public static void drawConstGarph(String fileName) {
		Set<ConstGraphNode> roots = getRoots();
		Set<String> edges = new HashSet<String>();
		roots.forEach(n -> n.drawNode(edges));
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
			writer.write("digraph ComparisionGraph {\n");
			edges.forEach(s -> {
				try {
					writer.write(s);
				} catch (IOException e) {
				}
			});
			writer.write("}");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
