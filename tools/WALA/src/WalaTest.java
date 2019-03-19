import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarFile;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.ProgressMaster;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.graph.traverse.DFS;
import com.ibm.wala.util.intset.OrdinalSetMapping;
import com.ibm.wala.util.io.FileProvider;

import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceFieldPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.callgraph.propagation.ReturnValueKey;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.NonNullSingletonIterator;

import org.apache.commons.cli.*;

class Params {

    public String inputPath;
    public String outputPath;
    public String projectName;
    public String exclusonLoc;

    public String toString() {
        return "Input path:" + inputPath + "\nOutput path:" + outputPath + "\nProjectname:" + projectName + "\n";
    }
}

public class WalaTest {
	
	
	private static Params processCommandLineArguments(String[] args) {
        Options options = new Options();

        String outpOption = "output", inpOption = "input", projectName = "project", excludeLoc = "excludeLoc";

        Option input = new Option("i", inpOption, true, "location of sources");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("o", outpOption, true, "where to put output files");
        output.setRequired(false);
        options.addOption(output);

        Option proj = new Option("p", projectName, true, "project name");
        output.setRequired(false);
        options.addOption(proj);
        
        Option ex = new Option("ex", excludeLoc, true, "exclusion file location");
        output.setRequired(false);
        options.addOption(ex);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return null;
        }
        Params para = new Params();
        para.inputPath = cmd.getOptionValue(inpOption);
        para.outputPath = cmd.getOptionValue(outpOption);
        para.projectName = cmd.getOptionValue(projectName);
        para.exclusonLoc = cmd.getOptionValue(excludeLoc);

        return para;

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
	
	 enum MethodTypeMapper{ INIT, CLINIT, NORMAL, SYNTHETIC}

	private TreeMap<String, Integer> idMapper = new TreeMap<>();

	private TreeSet<Pair<Integer, Integer>> cg_map = new TreeSet<>();
	
	TreeMap<MethodTypeMapper, TreeSet<String>> type_mapper = new TreeMap<>();

	private static boolean isExclude(String name) {
		String excludes[] = { "java.", "com.sun.", "sun.", "com.ibm.wala" };
		for (int i = 0; i < excludes.length; ++i) {
			if (name.startsWith(excludes[i])) {
				return true;
			}
		}
		return false;
	}

	private void produceDotFile(Params params) {
		try {
			PrintWriter writer = new PrintWriter(params.outputPath + File.separator + params.projectName + ".dot", "UTF-8");
			writer.println("digraph graphname {\nrankdir=\"LR\";");
			for (Map.Entry<String, Integer> met : idMapper.entrySet()) {
				writer.println(met.getValue() + " [label=\"" + met.getKey() + "\"]");
			}
			for (Pair<Integer, Integer> met : cg_map) {
				writer.println(met.getLeft() + " -> " + met.getRight());
			}
			writer.println("}");
			writer.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
	}
	
	
	  private static Iterable<Entrypoint> makeMainEntrypoints(ClassLoaderReference clr, IClassHierarchy cha) {
		    if (cha == null) {
		      throw new IllegalArgumentException("cha is null");
		    }
		    final HashSet<Entrypoint> result = HashSetFactory.make();
		    PrintWriter writer;
			try {
				writer = new PrintWriter("entry_points.txt", "UTF-8");
			
		    
		   
		    for (IClass klass : cha) {
		      if (klass.getClassLoader().getReference().equals(clr)) {
		        for(IMethod m : klass.getAllMethods()) {
		        	if(!m.isAbstract() && !m.isPrivate() && !isExclude(m.getSignature())) {
		        		writer.println(m.getSignature());
		        		result.add(new DefaultEntrypoint(m, cha));
		        	}
		        		
		        }
		      }
		    }
	
		    writer.close();
		    return result::iterator;
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
   
    private void initTypeMapper() {
    	type_mapper.put(MethodTypeMapper.INIT, new TreeSet<String>());
		type_mapper.put(MethodTypeMapper.CLINIT, new TreeSet<String>());
		type_mapper.put(MethodTypeMapper.SYNTHETIC, new TreeSet<String>());
		type_mapper.put(MethodTypeMapper.NORMAL, new TreeSet<String>());
    }
    
    private void putToTypeMapper(IMethod m) {
    	if(m.isInit()) {
    		type_mapper.get(MethodTypeMapper.INIT).add(m.getSignature());
    	}
    	else if(m.isClinit()) {
    		type_mapper.get(MethodTypeMapper.CLINIT).add(m.getSignature());
    	}
    	else if(m.isSynthetic()) {
    		type_mapper.get(MethodTypeMapper.SYNTHETIC).add(m.getSignature());
    	}
    	else {
    		type_mapper.get(MethodTypeMapper.NORMAL).add(m.getSignature());
    	}
    }
    
    private void printType(PrintWriter writer, String typeName, MethodTypeMapper typeCode) {
    	writer.println(typeName);
		writer.println(type_mapper.get(typeCode).size());
		for(String method: type_mapper.get(typeCode)) {
			writer.println(method);
		}
    }
    
    private void printTypeMapper(Params params) {
    	try {
			PrintWriter writer = new PrintWriter(params.outputPath + File.separator + params.projectName + ".methods", "UTF-8");
			printType(writer, "INIT", MethodTypeMapper.INIT);
			printType(writer, "CLINIT", MethodTypeMapper.CLINIT);
			printType(writer, "SYNTHETIC", MethodTypeMapper.SYNTHETIC);
			printType(writer, "NORMAL", MethodTypeMapper.NORMAL);
			writer.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
    }
    
    private String getSourceString(IMethod meth) {
    	if(meth.isSynthetic() || meth.isInit() || meth.isClinit()) {
    		return "";
    	}
    	String called_name = "::";
    	try {
			called_name += Integer.toString(meth.getSourcePosition(0).getFirstLine());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			called_name+="-1";
		}
    	called_name+="::";
    	try {
			called_name += Integer.toString(meth.getSourcePosition(0).getLastLine());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			called_name+="-1";
		}
    	called_name+="::";
    	called_name+=meth.getDeclaringClass().getSourceFileName();
    	return called_name;
    }
    
	private void anal(Params params)
			throws IOException, ClassHierarchyException, IllegalArgumentException, CallGraphBuilderCancelException {
		
		
		File exFile = new FileProvider().getFile(params.exclusonLoc);
		System.out.println(exFile.getAbsolutePath());
		
		AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(params.inputPath, exFile);

		ClassHierarchy cha = ClassHierarchyFactory.make(scope);
		Iterable<Entrypoint> entryPoints = makeMainEntrypoints(scope.getApplicationLoader(), cha);//Util.makeMainEntrypoints(scope, cha);//makeMainEntrypoints(scope.getApplicationLoader(), cha);////makeMainEntrypoints(scope.getApplicationLoader(), cha);//Util.makeMainEntrypoints(scope, cha);//makeMainEntrypoints(scope.getApplicationLoader(), cha);//Util.makeMainEntrypoints(scope, cha);
		Iterator<Entrypoint> it = entryPoints.iterator();
		//while(it.hasNext()) {
		
		//Entrypoint ep = it.next();
		//HashSet<Entrypoint> entries = new HashSet<Entrypoint>();
		//entries.add(ep);
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		//System.out.println(timeStamp + "::" + ep.toString());
		AnalysisOptions opts = new AnalysisOptions(scope, entryPoints);
		AnalysisCache cache = new AnalysisCacheImpl();
		CallGraphBuilder cgBuilder = Util.makeZeroOneContainerCFABuilder(opts, cache, cha, scope);//Util.makeRTABuilder(opts, cache, cha, scope);//Util.makeZeroOneContainerCFABuilder(opts, cache, cha, scope); //Util.makeRTABuilder(opts, cache, cha, scope);//Util.makeZeroOneContainerCFABuilder(opts, cache, cha, scope); 
		CallGraph cg = cgBuilder.makeCallGraph(opts, null);
		System.out.println("Entry nodes size:"+cg.getEntrypointNodes().size());

	
		for (Iterator<CGNode> i = DFS.iterateDiscoverTime(cg, new NonNullSingletonIterator<CGNode>(cg.getFakeRootNode())); i.hasNext();) {
			CGNode node = i.next();
			if (node.getMethod() != null) {
				
				String caller_name = node.getMethod().getSignature();
				if(isExclude(caller_name)) {
					continue;
				}
				caller_name += ":" + node.getMethod().getLineNumber(0);
				putToTypeMapper(node.getMethod());
				if (!idMapper.containsKey(caller_name)) {
					idMapper.put(caller_name, idMapper.size());
				}
				Integer caller_id = idMapper.get(caller_name);
				Iterator<CallSiteReference> callSiteIt = node.iterateCallSites();
				while (callSiteIt.hasNext()) {
					CallSiteReference callSite = callSiteIt.next();
					Iterator<CGNode>  targets = cg.getPossibleTargets(node, callSite).iterator();
					
					 for (; targets.hasNext();) {
				            CGNode target =  targets.next();
				            
				            if (target.getMethod() != null) {
				            	putToTypeMapper(target.getMethod());
				            	String called_name = target.getMethod().getSignature();
				            	called_name += ":" + target.getMethod().getLineNumber(0);
								if (!idMapper.containsKey(called_name)) {
									idMapper.put(called_name, idMapper.size());
								}
								Integer called_id = idMapper.get(called_name);
								cg_map.add(new Pair<Integer, Integer>(caller_id, called_id));
							}
				          }
					
					
				}
			}
		}
		//}
	}

	public static void main(String args[]) {
		Params params = processCommandLineArguments(args);
		WalaTest wt = new WalaTest();
		try {
			wt.initTypeMapper();
			wt.anal(params);
			wt.produceDotFile(params);
			wt.printTypeMapper(params);;
		} catch (ClassHierarchyException | IllegalArgumentException | CallGraphBuilderCancelException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
