package myspoon2.processors;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

public class ASTVisitor extends CtScanner {

    class ClassSignature {

        String name;
        Integer id;
        Integer pckId;
    }

    class MethodSignature implements Comparable<MethodSignature> {

        public String name;
        public Integer pck;
        public Integer cls;
        public Boolean constructor;

        @Override
        public int compareTo(MethodSignature aThat) {
            final int EQUAL = 0;

            if (this == aThat) {
                return EQUAL;
            }

            int comparison = name.compareTo(aThat.name);
            if (comparison != EQUAL) {
                return comparison;
            }

            comparison = pck.compareTo(aThat.pck);
            if (comparison != EQUAL) {
                return comparison;
            }

            comparison = cls.compareTo(aThat.cls);
            if (comparison != EQUAL) {
                return comparison;
            }

            comparison = constructor.compareTo(aThat.constructor);
            if (comparison != EQUAL) {
                return comparison;
            }

            return EQUAL;

        }

    }

    class Pair<L extends Comparable<L>, R extends Comparable<R>> implements Comparable<Pair<L, R>> {

        private final L left;
        private final R right;

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
            return this.left.equals(pairo.getLeft())
                    && this.right.equals(pairo.getRight());
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

    TreeMap<String, Integer> packages;
    TreeMap<String, Integer> classes;
    TreeMap<MethodSignature, Integer> methods;
    TreeMap<Integer, Pair<Integer, Integer>> methodPos;
    TreeMap<Integer, Pair<Integer, Integer>> methodEndPos;
    TreeSet<Pair<Integer, Integer>> edges;
    String project;

    public ASTVisitor(String projectname) {
        super();
        packages = new TreeMap<>();
        classes = new TreeMap<>();
        methods = new TreeMap<>();
        edges = new TreeSet<>();
        project = projectname;
        methodPos = new TreeMap<>();
        methodEndPos = new TreeMap<>();
    }

    private static String getNameById(TreeMap<String, Integer> map, Integer searchedId) {
        for (Map.Entry<String, Integer> met : map.entrySet()) {
            if (met.getValue().equals(searchedId)) {
                return met.getKey();
            }
        }
        return "";
    }
    
    private String producePosStr(Integer id){
        String posStr = new String();
        if(methodPos.containsKey(id)){
            Pair<Integer, Integer> p = methodPos.get(id);
            posStr += ":"+p.getLeft().toString()+":"+p.getRight().toString();
        }
        else{
            posStr += ":-1:-1";
        }
        if(methodEndPos.containsKey(id)){
            Pair<Integer, Integer> p = methodEndPos.get(id);
            posStr += ":"+p.getLeft().toString()+":"+p.getRight().toString();
        }
        else{
            posStr += ":-1:-1";
        }
        return posStr;
    }

    private String produceNameToMethod(MethodSignature ms) {
        String pck_name = getNameById(packages, ms.pck);
        String cls_name = getNameById(classes, ms.cls);
        if (ms.constructor) {
            String test = pck_name + "." + cls_name + "(";
            if ((ms.name.length() > test.length() && ms.name.regionMatches(0, test, 0, test.length())) || (ms.name.length() > pck_name.length() && ms.name.regionMatches(0, pck_name, 0, pck_name.length()) && ms.name.contains("$" + cls_name + "("))) {
                int index = ms.name.indexOf('(');
                if (index < 0) {
                    return ms.name + producePosStr(methods.get(ms)); //error 
                }
                return ms.name.substring(0, index) + "." + cls_name + ms.name.substring(index, ms.name.length()) + producePosStr(methods.get(ms));

            }
            return pck_name + "." + cls_name + "." + ms.name + producePosStr(methods.get(ms));

        } else {
            return pck_name + "." + cls_name + "." + ms.name + producePosStr(methods.get(ms));
        }

    }

    private String encode(String input) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            switch (input.charAt(i)) {
                case '&':
                    buffer.append("&amp;");
                    break;
                case '\"':
                    buffer.append("&quot;");
                    break;
                case '\'':
                    buffer.append("&apos;");
                    break;
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                default:
                    buffer.append(input.charAt(i));
                    break;
            }
        }
        return buffer.toString();
    }

    public void generateGraphml() {
        try {
            PrintWriter writer = new PrintWriter(project + ".graphml", "UTF-8");
            writer.println("<?xml version='1.0' encoding='utf-8'?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n<key attr.name=\"label\" attr.type=\"string\" for=\"node\" id=\"d0\"/>");
            writer.println("<graph edgedefault=\"directed\">");
            for (Map.Entry<MethodSignature, Integer> met : methods.entrySet()) {

                writer.println("<node id=\"" + met.getValue() + "\">");
                writer.println("	<data key=\"d0\">" + encode(produceNameToMethod(met.getKey())) + "</data>");
                writer.println("</node>");
            }
            for (Pair<Integer, Integer> met : edges) {
                writer.println("<edge source=\"" + met.getLeft() + "\"  target=\"" + met.getRight() + "\"/>");
            }
            writer.println("</graph></graphml>");
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateDotFile() {
        try {
            PrintWriter writer = new PrintWriter(project + ".dot", "UTF-8");
            writer.println("digraph graphname {\nrankdir=\"LR\";");
            for (Map.Entry<MethodSignature, Integer> met : methods.entrySet()) {
                writer.println(met.getValue() + " [label=\"" + produceNameToMethod(met.getKey()) + "\"]");
            }
            for (Pair<Integer, Integer> met : edges) {
                writer.println(met.getLeft() + " -> " + met.getRight());
            }
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ClassSignature extractAnonymusClassData(CtClass cls) {
        ClassSignature sign = new ClassSignature();
        sign.name = cls.getSimpleName();
        CtElement parent = cls.getParent();
        CtClass parent_class = null;
        while (parent != null) {
            if (parent instanceof CtClass) {
                parent_class = (CtClass) parent;
                sign.name = parent_class.getSimpleName() + "$" + sign.name;


            }
            parent = parent.getParent();
        }
        
         if (!classes.containsKey(sign.name)) {
                        classes.put(sign.name, classes.size());
         }
         sign.id = classes.get(sign.name);
         String pck = parent_class!= null ? parent_class.getPackage().getQualifiedName() : "";
         if (pck.length() == 0 || pck.equals("unnamed package")) {
              pck = "root";
         }
         if (!packages.containsKey(pck)) {
                        packages.put(pck, packages.size());
         }
         sign.pckId = packages.get(pck);
        return sign;
    }

    private MethodSignature getMethodSignatureFromExec(CtExecutableReference<?> exec) {
        if (exec != null) {
            
            String calledClass = exec.getDeclaringType().getSimpleName();
            CtTypeReference<?> decl = exec.getDeclaringType();
            CtPackageReference called_package = decl.getPackage();
            while (called_package == null) {
                decl = decl.getDeclaringType();
                calledClass = decl.getSimpleName() + "$" + calledClass;
                called_package = decl.getPackage();
            }
            String calledPackage = called_package.getQualifiedName();
            if (calledPackage.length() == 0 || calledPackage.equals("unnamed package")) {
                calledPackage = "root";
            }
            MethodSignature called_ms = new MethodSignature();
            called_ms.name = exec.getSignature();
            called_ms.constructor = exec.isConstructor();
            
            Pair<Integer, Integer> position = exec.getPosition().isValidPosition() ? new Pair<>(exec.getPosition().getLine(), exec.getPosition().getColumn()) : new Pair<>(-1, -1);
            Pair<Integer, Integer> endPosition = exec.getPosition().isValidPosition() ? new Pair<>(exec.getPosition().getSourceEnd(), exec.getPosition().getEndColumn()) : new Pair<>(-1, -1);

            if (!packages.containsKey(calledPackage)) {
                packages.put(calledPackage, packages.size());
            }

            if (!classes.containsKey(calledClass)) {
                classes.put(calledClass, classes.size());
            }

            called_ms.pck = packages.get(calledPackage);
            called_ms.cls = classes.get(calledClass);
            if (!methods.containsKey(called_ms)) {
                methods.put(called_ms, methods.size());
            }
            Integer ms_id = methods.get(called_ms);
            if(!methodPos.containsKey(ms_id) || (position.getLeft() != -1)){
                methodPos.put(ms_id, position);
            }
            if(!methodEndPos.containsKey(ms_id) || (endPosition.getLeft() != -1)){
                methodEndPos.put(ms_id, endPosition);
            }
            return called_ms;

        }
        return null;
    }

    private <T extends CtType<?>, CtTypeInformation> void extractContainerData(T caller, MethodSignature caller_signature) {
        if (caller.getPackage() == null) {//inner valami
            if (caller_signature.cls == null) {
                String clsname = caller.getQualifiedName();
                String pck = "";
                int dotPos = clsname.lastIndexOf('.');
                if (dotPos > 0) {
                    pck = clsname.substring(0, dotPos);
                    clsname = clsname.substring(dotPos + 1);     
                }
                if (!classes.containsKey(clsname)) {
                    classes.put(clsname, classes.size());
                }
                caller_signature.cls = classes.get(clsname);

                if (pck.length() == 0 || pck.equals("unnamed package")) {
                    pck = "root";
                }
                if (!packages.containsKey(pck)) {
                    packages.put(pck, packages.size());
                }
                caller_signature.pck = packages.get(pck);
            }
            return;
        }
        String clsname = !caller.isTopLevel() ? caller.getQualifiedName() : caller.getSimpleName();
        int dotPos = clsname.lastIndexOf('.');
        if (dotPos > 0) {
            clsname = clsname.substring(dotPos + 1);
        }
        if (!classes.containsKey(clsname)) {
            classes.put(clsname, classes.size());
        }
        if (caller_signature.cls == null) { //innerben már be lett állítva
            if (!classes.containsKey(clsname)) {
                classes.put(clsname, classes.size());
            }
            caller_signature.cls = classes.get(clsname);
        }

        String pck = caller.getPackage().getQualifiedName();
        if (pck.length() == 0 || pck.equals("unnamed package")) {
            pck = "root";
        }
        if (!packages.containsKey(pck)) {
            packages.put(pck, packages.size());
        }
        caller_signature.pck = packages.get(pck);

    }

    private void handleInvocation(CtAbstractInvocation<?> invoc) {
        MethodSignature caller_signature = new MethodSignature();
        CtElement parent = invoc.getParent();
        boolean static_caller = false;
        Pair<Integer, Integer> caller_pos = null, caller_pos_end = null;
        while (parent != null) {
            if (parent instanceof CtMethod) {
                CtMethod caller = (CtMethod) parent;
                caller_signature.name = caller.getSignature();
                caller_signature.constructor = false;
                caller_pos = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getLine(), caller.getPosition().getColumn()) : new Pair<>(-1, -1);
                caller_pos_end = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn()) : new Pair<>(-1, -1);
            } else if (parent instanceof CtConstructor) {
                CtConstructor caller = (CtConstructor) parent;
                caller_signature.name = caller.getSignature();
                caller_signature.constructor = true;
                caller_pos = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getLine(), caller.getPosition().getColumn()) : new Pair<>(-1, -1);
                caller_pos_end = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn()) : new Pair<>(-1, -1);
            } else if (parent instanceof CtField) {
                CtField caller = (CtField) parent;
                static_caller = caller.isStatic();
                caller_pos = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getLine(), caller.getPosition().getColumn()) : new Pair<>(-1, -1);
                caller_pos_end = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn()) : new Pair<>(-1, -1);
            } else if (parent instanceof CtAnonymousExecutable) {
                CtAnonymousExecutable caller = (CtAnonymousExecutable) parent;
                static_caller = caller.isStatic();
                caller_pos = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getLine(), caller.getPosition().getColumn()) : new Pair<>(-1, -1);
                caller_pos_end = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn()) : new Pair<>(-1, -1);
            } else if (parent instanceof CtClass) {
                CtClass caller = (CtClass) parent;
                if (caller.isAnonymous()) {
                    ClassSignature clsSign = extractAnonymusClassData(caller);
                    caller_signature.cls = clsSign.id;
                    caller_signature.pck = clsSign.pckId;
                    break;
                } else {
                    extractContainerData(caller, caller_signature);
                    break;
                }

            } else if (parent instanceof CtInterface) {
                CtInterface caller = (CtInterface) parent;
                caller_pos = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getLine(), caller.getPosition().getColumn()) : new Pair<>(-1, -1);
                caller_pos_end = caller.getPosition().isValidPosition() ? new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn()) : new Pair<>(-1, -1);
                extractContainerData(caller, caller_signature);
                break;

            }
            parent = parent.getParent();
        }
        if (caller_signature.name == null) {
            //ez biztos?
            if (static_caller) {
                caller_signature.name = "<clinit>()";
            } else {
                caller_signature.name = "<initblock>()";
            }
            caller_signature.constructor = true;
        }

        if (!methods.containsKey(caller_signature)) {
            methods.put(caller_signature, methods.size());
        }
        
         Integer ms_id = methods.get(caller_signature);
            if(!methodPos.containsKey(ms_id) || (caller_pos.getLeft() != -1)){
                methodPos.put(ms_id, caller_pos);
            }
            if(!methodEndPos.containsKey(ms_id) || (caller_pos_end.getLeft() != -1)){
                methodEndPos.put(ms_id, caller_pos_end);
            }

        CtExecutableReference<?> exec = invoc.getExecutable();

        MethodSignature called_ms = getMethodSignatureFromExec(exec);
        if (called_ms != null) {

            Integer edgeId1, edgeId2;
            edgeId1 = methods.get(caller_signature);
            edgeId2 = methods.get(called_ms);
            edges.add(new Pair<Integer, Integer>(edgeId1, edgeId2));

        }

    }

    @Override
    public void visitCtConstructorCall(CtConstructorCall invoc) {
        super.visitCtConstructorCall(invoc);
        handleInvocation(invoc);
    }

    @Override
    public void visitCtInvocation(CtInvocation invoc) {
        super.visitCtInvocation(invoc);
        handleInvocation(invoc);
    }

    @Override
    public void visitCtNewClass(CtNewClass invoc) {
        super.visitCtNewClass(invoc);

        CtElement parent = invoc.getParent();
        MethodSignature caller_signature = new MethodSignature();
        Pair<Integer, Integer> position_caller = null, caller_pos_end = null;
        boolean static_caller = false;
        while (parent != null) {
            if (parent instanceof CtInvocation) {
                CtInvocation caller_invoc = (CtInvocation) parent;
                caller_signature = getMethodSignatureFromExec(caller_invoc.getExecutable());
                position_caller = new Pair<>(-1, -1);
                caller_pos_end = new Pair<>(-1, -1);
                break;
            }
            if (parent instanceof CtMethod) {
                CtMethod caller = (CtMethod) parent;
                caller_signature.name = caller.getSignature();
                caller_signature.constructor = false;
                position_caller = new Pair<>(caller.getPosition().getLine(), caller.getPosition().getColumn());
                caller_pos_end = new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn());
            } else if (parent instanceof CtConstructor) {
                CtConstructor caller = (CtConstructor) parent;
                caller_signature.name = caller.getSignature();
                caller_signature.constructor = true;
                position_caller = new Pair<Integer, Integer>(caller.getPosition().getLine(), caller.getPosition().getColumn());
                caller_pos_end = new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn());
            } else if (parent instanceof CtField) {
                CtField caller = (CtField) parent;
                static_caller = caller.isStatic();
                position_caller = new Pair<Integer, Integer>(caller.getPosition().getLine(), caller.getPosition().getColumn());
                caller_pos_end = new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn());
            } else if (parent instanceof CtAnonymousExecutable) {
                CtAnonymousExecutable caller = (CtAnonymousExecutable) parent;
                static_caller = caller.isStatic();
                position_caller = new Pair<Integer, Integer>(caller.getPosition().getLine(), caller.getPosition().getColumn());
                caller_pos_end = new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn());
            } else if (parent instanceof CtClass) {
                CtClass caller = (CtClass) parent;
                if (caller.isAnonymous()) {
                    ClassSignature clsSign = extractAnonymusClassData(caller);
                    caller_signature.cls = clsSign.id;
                    caller_signature.pck = clsSign.pckId;
                    break;
                } else {
                    extractContainerData(caller, caller_signature);
                    break;
                }

            } else if (parent instanceof CtInterface) {
                CtInterface caller = (CtInterface) parent;
                extractContainerData(caller, caller_signature);
                position_caller = new Pair<Integer, Integer>(caller.getPosition().getLine(), caller.getPosition().getColumn());
                caller_pos_end = new Pair<>(caller.getPosition().getEndLine(), caller.getPosition().getEndColumn());
                break;
            }
            parent = parent.getParent();
        }

        ClassSignature sign = extractAnonymusClassData(invoc.getAnonymousClass());
        MethodSignature called_ms = new MethodSignature();
        Pair<Integer, Integer> position_called = null, position_called_end = null;
        called_ms.name = invoc.getExecutable().getSignature();
        called_ms.constructor = invoc.getExecutable().isConstructor();
        called_ms.cls = sign.id;
        called_ms.pck = sign.pckId;
        position_called = invoc.getPosition().isValidPosition() ? new Pair<>(invoc.getPosition().getLine(), invoc.getPosition().getColumn()) : new Pair<>(-1, -1);
        position_called_end = invoc.getPosition().isValidPosition() ? new Pair<>(invoc.getPosition().getEndLine(), invoc.getPosition().getEndColumn()) : new Pair<>(-1, -1);
        if (!methods.containsKey(called_ms)) {
            methods.put(called_ms, methods.size());
        }
        
        Integer ms_id1 = methods.get(called_ms);
        if(!methodPos.containsKey(ms_id1) || (position_called.getLeft() != -1)){
            methodPos.put(ms_id1, position_called);
        }
        if(!methodEndPos.containsKey(ms_id1) || (position_called_end.getLeft() != -1)){
            methodEndPos.put(ms_id1, position_called_end);
        }
        

        if (caller_signature != null) {

            if (caller_signature.name == null) {
                //ez biztos?
                if (static_caller) {
                    caller_signature.name = "<clinit>()";
                } else {
                    caller_signature.name = "<initblock>()";
                }
                caller_signature.constructor = true;
            }

            if (!methods.containsKey(caller_signature)) {
                methods.put(caller_signature, methods.size());
            }
            
            Integer ms_id = methods.get(caller_signature);
            if(!methodPos.containsKey(ms_id) || (position_caller.getLeft() != -1)){
                methodPos.put(ms_id, position_caller);
            }
            if(!methodEndPos.containsKey(ms_id) || (caller_pos_end.getLeft() != -1)){
                methodEndPos.put(ms_id, caller_pos_end);
            }
            

            Integer edgeId1, edgeId2;
            edgeId1 = methods.get(caller_signature);
            edgeId2 = methods.get(called_ms);
            edges.add(new Pair<Integer, Integer>(edgeId1, edgeId2));

        }
    }

}
