package edu.uci.in4matx212.cgc;

import java.util.Set;

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.ParameterRef;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import edu.uci.in4matx212.Util;
import edu.uci.in4matx212.model.ResolvedVirtualCall;
import edu.uci.in4matx212.model.cfa.ConstGraph;
import edu.uci.in4matx212.model.cfa.ConstGraphNode;
import edu.uci.in4matx212.model.cfa.LocalConstGraphNode;
import edu.uci.in4matx212.model.cfa.ParamConstGraphNode;
import edu.uci.in4matx212.model.cfa.ReturnConstGraphNode;
import edu.uci.in4matx212.model.cfa.ThisConstGraphNode;

public class CFA  extends CallGraphGenerator  {

	static final String GRAPH_OUT = "ConstGraph.gv";

	@Override
	public String getAlgorithmName() {
		return "Control Flow Analysis (Context-insensitive)";
	}

	@Override
	public String getAlgorithmShortName() {
		return "0-CFA";
	}

	@Override
	public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall) {
		Value base = virtualCall.getBase();
		ConstGraphNode baseNode = ConstGraph.findLocalNode(base);
		return new ResolvedVirtualCall(virtualCall, baseNode.getReachingType());
	}

	@Override
	public void init(Set<SootClass> loadedClasses, Boolean showProcessResults, String outputFolder) {
		super.init(loadedClasses, showProcessResults, outputFolder);
		for (SootClass sClass : loadedClasses)
			createNodes(sClass);
		for (SootClass sClass : loadedClasses)
			connectNodes(sClass);
		ConstGraph.propagateTypes();
		if (showProcessResults)
			ConstGraph.drawConstGarph(outputFolder + "/" + GRAPH_OUT);
	}

	private static void createNodes(SootClass sClass) {
		for (SootMethod sootMethod : sClass.getMethods()) {
			if (sootMethod.isAbstract())
				continue;
			Body methodBody = sootMethod.retrieveActiveBody();
			createNodes(methodBody);
			createNodes(sootMethod);
		}
	}

	private void connectNodes(SootClass sClass) {
		for (SootMethod sootMethod : sClass.getMethods()) {
			if (sootMethod.isAbstract())
				continue;
			Body methodBody = sootMethod.retrieveActiveBody();
			for (Unit unit : methodBody.getUnits()) {
				if (unit instanceof JAssignStmt)
					processAssignment((JAssignStmt) unit);
				if (unit instanceof JReturnStmt)
					processReturn((JReturnStmt) unit, sootMethod);
				if (unit instanceof JIdentityStmt)
					processParam((JIdentityStmt) unit, sootMethod);
				for (ValueBox box : unit.getUseBoxes()) {
					if (box.getValue() instanceof AbstractInvokeExpr) {
						Value lhs = unit.getDefBoxes().stream().map(b -> b.getValue()).findFirst().orElse(null);
						processMethodCall((AbstractInvokeExpr) box.getValue(), lhs);
					}
				}
			}
		}
	}

	private static void createNodes(Body b) {
		for (Local local : b.getLocals()) {
			ConstGraphNode node = new LocalConstGraphNode(local);
			ConstGraph.addNode(node);
		}
	}

	private static void createNodes(SootMethod sMethod) {
		for (int i = 0; i < sMethod.getParameterCount(); i++) {
			Type parameterType = sMethod.getParameterType(i);
			if (parameterType instanceof RefType) {
				if (((RefType) parameterType).getSootClass().isApplicationClass()) {
					ConstGraphNode node = new ParamConstGraphNode(sMethod, i);
					ConstGraph.addNode(node);
				}
			}
		}
		Type returnType = sMethod.getReturnType();
		if (!(returnType instanceof VoidType)) {
			ConstGraphNode node = new ReturnConstGraphNode(sMethod);
			ConstGraph.addNode(node);
		}
		ConstGraphNode node = new ThisConstGraphNode(sMethod);
		ConstGraph.addNode(node);
	}

	private static void processReturn(JReturnStmt stmt, SootMethod method) {
		Value local = stmt.getOp();
		if (local instanceof JimpleLocal)
			ConstGraph.findReturnNode(method).addSubset(ConstGraph.findLocalNode(local));
	}

	private static void processParam(JIdentityStmt stmt, SootMethod method) {
		if (!(stmt.getRightOp() instanceof ParameterRef))
			return;
		Value local = stmt.getLeftOp();
		if (!(local instanceof JimpleLocal))
			return;
		ParameterRef param = (ParameterRef) stmt.getRightOp();
		if (param.getType() instanceof RefType && ((RefType) param.getType()).getSootClass().isApplicationClass())
			ConstGraph.findLocalNode(local).addSubset(ConstGraph.findParamNode(method, param.getIndex()));
	}

	private static void processAssignment(JAssignStmt stmt) {
		Value lhs = stmt.getLeftOp();
		Value rhs = stmt.getRightOp();
		if (lhs instanceof JimpleLocal && rhs instanceof JimpleLocal)
			ConstGraph.findLocalNode(lhs).addSubset(ConstGraph.findLocalNode(rhs));
		if (lhs instanceof JimpleLocal && rhs instanceof JNewExpr)
			ConstGraph.findLocalNode(lhs).addReachingType((JNewExpr) rhs);
	}

	public void processMethodCall(AbstractInvokeExpr invokeExpr, Value lhs) {
		Set<SootMethod> similarMethods = Util.findSimilarMethods(invokeExpr.getMethod().getSubSignature(), loadedClasses);
		Value base = null;
		if (invokeExpr instanceof JVirtualInvokeExpr)
			base = ((JVirtualInvokeExpr) invokeExpr).getBase();
		for (SootMethod sootMethod : similarMethods) {
			if (base != null)
				ConstGraph.findThisNode(sootMethod).addSubset(ConstGraph.findLocalNode(base));
			if (lhs != null && !(sootMethod.getReturnType() instanceof VoidType))
				ConstGraph.findLocalNode(lhs).addSubset(ConstGraph.findReturnNode(sootMethod));
			for (int i = 0; i < sootMethod.getParameterCount(); i++) {
				Type parameterType = sootMethod.getParameterType(i);
				if (parameterType instanceof RefType) {
					if (((RefType) parameterType).getSootClass().isApplicationClass()) {
						ConstGraph.findParamNode(sootMethod, i).addSubset(ConstGraph.findLocalNode(invokeExpr.getArg(i)));
					}
				}
			}
		}
	}

}
