package hu.sed.cg.instrumenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.ui.internal.registry.ViewParameterValues;

public class CGVisitor extends ASTVisitor {

	public class Pair<L extends Comparable<L>, R extends Comparable<R>> implements Comparable<Pair<L, R>> {

		private L left;
		private R right;

		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}

		public L getLeft() {
			return left;
		}

		public R getRight() {
			return right;
		}
		
		public void setLeft(L l) {
			left = l;
		}

		public void setRight(R r) {
			right = r;
		}

		@Override
		public int hashCode() {
			return left.hashCode() ^ right.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pair)) {
				return false;
			}
			Pair pairo = (Pair) o;
			return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
		}

		@Override
		public int compareTo(Pair<L, R> o) {
			final int EQUAL = 0;
			int comparison = left.compareTo(o.left);
			if (comparison != EQUAL) {
				return comparison;
			}
			comparison = right.compareTo(o.right);
			if (comparison != EQUAL) {
				return comparison;
			}
			return EQUAL;
		}

	}
	
	class Signature implements Comparable<Signature>{
		String classKey = new String();
		String methodName = new String();

		Integer params = new Integer(-1);
		public void setParams(Integer params) {
			this.params = params;
		}

		Pair<Pair<Integer,Integer>, Pair<Integer,Integer>> lineinfo = new Pair<>(new Pair<Integer,Integer>(-1,-1), new Pair<Integer,Integer>(-1,-1));
		String cuName;
		
		public Signature(String _classKey, String _methodName, Integer id, Pair<Pair<Integer,Integer>, Pair<Integer,Integer>> lines, String cuName) {
			this.classKey = _classKey;
			this.methodName = _methodName;
			this.params = id;
			this.lineinfo = lines;
			this.cuName = cuName;
		}
		
		public Signature(Signature right) {
			this.classKey = right.classKey;
			this.methodName = right.methodName;
			this.params = right.params;
			this.lineinfo = right.lineinfo;
			this.cuName = right.cuName;
		}

		@Override
		public int compareTo(Signature o) {
			// TODO Auto-generated method stub
			if(o.classKey.compareTo(classKey) != 0) {
				return classKey.compareTo(o.classKey);
			}
			
			if(o.methodName.compareTo(methodName) != 0) {
				return methodName.compareTo(o.methodName);
			}
			
			return params.compareTo(o.params);
		}
		
	}
	Map<Integer, ArrayList<ITypeBinding>> paramMapper = new TreeMap<Integer, ArrayList<ITypeBinding>>();
	Map<String, String> classMapper = new TreeMap<String, String>();
	Map<String, Pair<Integer, Signature>> nodes = new TreeMap<String, Pair<Integer, Signature>>();
	Set<Pair<Integer, Integer>> edges = new TreeSet<Pair<Integer, Integer>>();
	Integer idCounter = new Integer(0);
	Integer anonymCounter = new Integer(0);
	Integer paramIdCounter = new Integer(0);

	Stack<IMethodBinding> contexts = new Stack<IMethodBinding>();
	Stack<CompilationUnit> compilationUnits = new Stack<CompilationUnit>();
	Stack<String> classes = new Stack<String>();
	Stack<String> initBlocks = new Stack<>();
	
	public CompilationUnit getCurrentCU() {
		return compilationUnits.peek();
	}
	
	Pair<Pair<Integer,Integer>, Pair<Integer, Integer>> produceLineInfo(ASTNode n){
		if(n == null) {
			return new Pair<>(new Pair<>(-1,-1), new Pair<>(-1,-1));
		}
		int pos = n.getStartPosition();
		Pair<Integer, Integer> p = new Pair<>(getCurrentCU().getLineNumber(pos),getCurrentCU().getColumnNumber(pos));
		pos += n.getLength();
		Pair<Integer, Integer> p_end = new Pair<>(getCurrentCU().getLineNumber(pos),getCurrentCU().getColumnNumber(pos));
		return new Pair<>(p, p_end);
	}

	private String getClassName(ITypeBinding declaringClass) {
		if (declaringClass.isNested()) {
			if (declaringClass.getName().length() == 0 || declaringClass.getName().startsWith("new ")) {
				anonymCounter++;
				return getClassName(declaringClass.getDeclaringClass()) + "$" + anonymCounter.toString();
			}
			return getClassName(declaringClass.getDeclaringClass()) + "$" + declaringClass.getName();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(declaringClass.getQualifiedName());
			if(declaringClass.isGenericType()) {
				sb.append("<");
				ITypeBinding[] b = declaringClass.getTypeParameters();
				for(int i = 0;i<b.length;i++) {
					ITypeBinding eras = b[i].getErasure();
					if(eras != null) {
						sb.append(eras.getQualifiedName());
					}
					else {
						sb.append("java.lang.Object");
					}
					if(i != b.length -1) {
						sb.append(",");
					}
				}
				sb.append(">");
			}
			return sb.toString();
		}
	}
	
	public String getClassSignature(String classKey) {
		return classMapper.get(classKey);
	}
	
	public String getParametrizedName(ParameterizedType type) {
		return "";
	}
	
	public String getParamListAndReturnVal(Integer id) {
		StringBuilder signature = new StringBuilder();
		ArrayList<ITypeBinding> types = paramMapper.get(id);
		if(types == null) {
			signature.append("():void");
			return signature.toString();
		}
		for (int i = 0; i < types.size(); ++i) {
			if(types.size() == 1) {
				signature.append("):");
			}
			if(types.get(i).isNested()) {
				String withDollars = types.get(i).getBinaryName();
				String result ="";
				int inBracket = 0;
				String withDots = types.get(i).getQualifiedName();
				int withDotIndex = 0;
				for(int j=0;j<withDollars.length() && withDotIndex < withDots.length();++j, ++withDotIndex) {
					if(withDots.charAt(withDotIndex) == '<') {
						for(;withDotIndex < withDots.length();++withDotIndex) {
							result+=withDots.charAt(withDotIndex);
							if(withDots.charAt(withDotIndex) == '>') {
								inBracket--;
							}
							if(withDots.charAt(withDotIndex) == '<') {
								inBracket++;
							}
							if(inBracket == 0) {
								withDotIndex++;
								break;
							}
						}
					}
					
					result+=withDollars.charAt(j);
				}
				if(withDotIndex < withDots.length()) {
					for(;withDotIndex < withDots.length();++withDotIndex) {
						result+=withDots.charAt(withDotIndex);
					}
				}
				signature.append(result);
			}
			else if(types.get(i).isParameterizedType()) {
				ITypeBinding eras = types.get(i).getErasure();
				if(classMapper.containsKey(eras.getKey())) {
					signature.append(classMapper.get(eras.getKey()));
				}else {
					signature.append(types.get(i).getQualifiedName());
				}
			}
			
			else {
				signature.append(types.get(i).getQualifiedName());
			}
			if (i < types.size() - 2) {
				signature.append(";");
			}
			if(i == types.size() - 2) {
				signature.append("):");
			}
		}

		
		return signature.toString();
	}

	public Signature getSignature(IMethodBinding method, ASTNode n) {
		if(method == null) {
			return null;
		}
		if(nodes.containsKey(method.getKey())) {
			return nodes.get(method.getKey()).getRight();
		}
		StringBuilder signature = new StringBuilder();
		//String className = null;
		//if(classMapper.containsKey(method.getDeclaringClass().getKey())) {
		//	className = classMapper.get(method.getDeclaringClass().getKey());
		//}
		String clskey = null;
		if(method.getDeclaringClass().isParameterizedType()) {
			ITypeBinding erasure = method.getDeclaringClass().getErasure();
			clskey = erasure.getKey();
		}else {
			clskey = method.getDeclaringClass().getKey();
		}
		if(!classMapper.containsKey(clskey)) {
			String className = getClassName(method.getDeclaringClass());
	        classMapper.put(clskey, className);
		}
		//signature.append(className);
		if(method.isConstructor()) {
			System.out.println("asd");
		}
		signature.append('.').append(method.isConstructor() ? "<init>" : method.getName()).append("(");
		ITypeBinding[] params = method.getParameterTypes();
		ArrayList<ITypeBinding> pars = new ArrayList<>(Arrays.asList(params));
		
		pars.add(method.getReturnType());
		paramMapper.put(paramIdCounter, pars);
		
		return new Signature(clskey, signature.toString(), paramIdCounter++, produceLineInfo(n), n != null ? getCurrentCU().getJavaElement().getElementName() : "");//new Pair<String, String>(method.getDeclaringClass().getKey(), signature.toString());
	}

	public Map<String, Pair<Integer, String>> getNodes() {
		Map<String, Pair<Integer, String>> returnMap = new TreeMap<String, Pair<Integer, String>>();
		for(Map.Entry<String, CGVisitor.Pair<Integer, Signature>> met : nodes.entrySet()) {
			String clsSignature = getClassSignature(met.getValue().getRight().classKey);
			returnMap.put(met.getKey(), new Pair<Integer, String>(met.getValue().getLeft(), clsSignature + met.getValue().getRight().methodName + getParamListAndReturnVal(met.getValue().getRight().params) + ":" + met.getValue().getRight().cuName + ":" + met.getValue().getRight().lineinfo.getLeft().getLeft() + ":" +  + met.getValue().getRight().lineinfo.getLeft().getRight()+ ":" + met.getValue().getRight().lineinfo.getRight().getLeft() + ":" +  + met.getValue().getRight().lineinfo.getRight().getRight()));
		}
		return returnMap;
	}

	public Set<Pair<Integer, Integer>> getEdges() {
		return edges;
	}
	
	private String getMethodKey(IMethodBinding method) {
		String key = null;
		try {
			if(method.isParameterizedMethod() || method.isRawMethod() || method.getDeclaringClass().isGenericType() || method.getDeclaringClass().isParameterizedType() || method.getDeclaringClass().isRawType()) {
				key = method.getMethodDeclaration().getKey();
				ITypeBinding[] types = method.getParameterTypes();
				if(types.length > 0) {
					Pair<Integer, Signature> decl = nodes.get(key);
					if(decl != null) {
					
					key = method.getKey();
					if(!nodes.containsKey(key)) {
						Signature def = new Signature(decl.getRight());
						ArrayList<ITypeBinding> pars = new ArrayList<>(Arrays.asList(types));
					
						pars.add(method.getReturnType());
						paramMapper.put(paramIdCounter, pars);
						def.setParams(paramIdCounter++);
					
					
						nodes.put(key, new Pair<Integer, Signature>(idCounter++, def));
					}
					
				}
				}
	
			}
			else {
				key = method.getKey();
			}
		} catch (NullPointerException ex) {
			System.out.println("OH NO! Something nasty happened!");
			return "";
		}
		return key;
	}

	private boolean addNode(IMethodBinding method, ASTNode n) {
		Signature testName = null;
		String key = getMethodKey(method);
		testName = getSignature(method, n);
		if (!nodes.containsKey(key)) {
			nodes.put(key, new Pair<Integer, Signature>(idCounter++, testName));
		}else if(nodes.get(key).getRight().lineinfo.getLeft().getLeft() == -1) {
			Pair<Integer, Signature> val = nodes.get(key);
			Signature s = val.getRight();
			s.lineinfo = produceLineInfo(n);
			s.cuName = getCurrentCU().getJavaElement().getElementName();
			val.setRight(s);
			nodes.put(key, val);
			
		}

		return true;
	}
	
	private String addInitNode(Initializer node) {
		String clskey = classes.peek();
		int modifiers = node.getModifiers();
		String name = (Modifier.isStatic(modifiers) ? ".<clinit>" : ".<initblock>");
		String key = clskey + name;
		if(!nodes.containsKey(key)) {
			Signature sg = new Signature(clskey, name, paramIdCounter++, produceLineInfo(null), "");
			nodes.put(key, new Pair<Integer, Signature>(idCounter++, sg));
		}
		return key;
	}

	private Integer getMethodId(String s) {
		if (nodes.containsKey(s)) {
			return nodes.get(s).getLeft();
		}
		System.out.println("Node missing from map");
		return null;
	}

	private boolean addCall(IMethodBinding method) {
		addNode(method, null);

		if (!contexts.isEmpty()) {
			String key1 = getMethodKey(contexts.peek());
			String key2 = getMethodKey(method);
			Integer caller = getMethodId(key1);
			Integer callee = getMethodId(key2);

			if (caller == null || callee == null) {
				System.out.println("Error resolving ids");
				return false;
			}

			edges.add(new Pair<Integer, Integer>(caller, callee));

			return true;
		} else {
			if(!initBlocks.isEmpty()) {
				String key1 = initBlocks.peek();
				Integer caller =  getMethodId(key1);
				String key2 = getMethodKey(method);
				Integer callee = getMethodId(key2);
				
				if (caller == null || callee == null) {
					System.out.println("Error resolving ids");
					return false;
				}

				edges.add(new Pair<Integer, Integer>(caller, callee));
				return true;
			}
			else {
				System.out.println("Invocation without a context");
				return false;
			}
			
		}
	}

	public CGVisitor() {
		super();
	}

	@Override
	public boolean visit(MethodDeclaration node) {

		contexts.push(node.resolveBinding());
		addNode(node.resolveBinding(), node);

		return true;
	}
	
	@Override
	public boolean visit(Initializer node) {
		String key = addInitNode(node);
		initBlocks.push(key);
		return true;
	}
	
	@Override
	public void endVisit(Initializer node) {
		initBlocks.pop();
	}


	@Override
	public void endVisit(MethodDeclaration node) {
		contexts.pop();
	}

	@Override
	public boolean visit(MethodInvocation node) {
		//System.out.println("----------------------------\n" + node);
		//Expression expression = node.getExpression();
		//if (expression != null) {
			//System.out.println("Expr: " + expression.toString());
			//ITypeBinding typeBinding = expression.resolveTypeBinding();
			//if (typeBinding != null) {
				//System.out.println("Type: " + typeBinding.getQualifiedName());
			//}
		//}
		/*IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {
			ITypeBinding type = binding.getDeclaringClass();
			if (type != null) {
				System.out.println("Decl: " + type.getQualifiedName());
			}
		}*/

		if (!addCall(node.resolveMethodBinding())) {
			System.out.println(node);
		}
		//System.out.println("----------------------------\n");
		return true;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		addCall(node.resolveMethodBinding());
		return true;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		//addNode(node.resolveConstructorBinding().getMethodDeclaration(), null);
		addCall(node.resolveConstructorBinding());

		return true;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		addCall(node.resolveConstructorBinding());

		return true;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		//addNode(node.resolveConstructorBinding().getMethodDeclaration(), null);
		addCall(node.resolveConstructorBinding());

		return true;
	}

	public boolean visit(LambdaExpression node) {
		addNode(node.resolveMethodBinding(), node);
		return true;

	}

	public boolean visit(final TypeDeclaration node) {
		String className = getClassName(node.resolveBinding());
		classMapper.put(node.resolveBinding().getKey(), className);
		classes.push(node.resolveBinding().getKey());
		return super.visit(node);
	}
	
	public void endVisit(final TypeDeclaration node) {
		classes.pop();
	}
	
	@Override
	public boolean visit(final EnumDeclaration node) {
		String className = getClassName(node.resolveBinding());
		classMapper.put(node.resolveBinding().getKey(), className);
		classes.push(node.resolveBinding().getKey());
		return super.visit(node);
	}
	
	@Override
	public void endVisit(final EnumDeclaration node) {
		classes.pop();
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		compilationUnits.push(node);
		return super.visit(node);
	}
	
	@Override
	public void endVisit(CompilationUnit node) {
		super.endVisit(node);
		compilationUnits.pop();
	}

}