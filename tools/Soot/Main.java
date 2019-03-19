import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.LineNumberTag;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

public class Main {



  private static String outputFile = "default-Soot-Directory";

  public static File serializeCallGraph(CallGraph graph, String fileName) {
    
    if (fileName == null) {
      
      fileName = soot.SourceLocator.v().getOutputDir();
      if (fileName.length() > 0) {

        fileName = fileName + java.io.File.separator;
      }
      fileName = fileName + "call-graph" + DotGraph.DOT_EXTENSION;
    }
    
    DotGraph canvas = new DotGraph("call-graph");
    QueueReader<Edge> listener = graph.listener();
    
    while (listener.hasNext()) {
      Edge next = listener.next();
        
      MethodOrMethodContext src = next.getSrc();
      MethodOrMethodContext tgt = next.getTgt();

      String srcString = src.toString();
      String tgtString = tgt.toString();


      if ( !srcString.startsWith("<java.") && !srcString.startsWith("<sun.") && !srcString.startsWith("<jdk.") && !srcString.startsWith("<javax.") && !srcString.startsWith("<com.sun.") && !srcString.startsWith("<org.xml.")) {  

        if ( src.method().hasTag("LineNumberTag") ) {

          LineNumberTag tag = (LineNumberTag)(src.method().getTag("LineNumberTag"));



          srcString = "(" + (tag.getLineNumber() + 1) + ")" + srcString;  //get the first stmnt.

        }
        else {
          //System.err.println("No lineinfo was provided");
        }


          if ( tgt.method().hasTag("LineNumberTag") ) {
            LineNumberTag tag = (LineNumberTag)(tgt.method().getTag("LineNumberTag"));


            tgtString = "(" + (tag.getLineNumber() + 1) + ")" + tgtString;  //get the first stmnt
          }
          else {
            //System.err.println("No lineinfo was provided");
          }

          canvas.drawEdge(srcString, tgtString);
      }
    }
    
    canvas.plot(fileName);
    return new File(fileName);
  }
  
  public static void main(String[] args) {
    
    
    System.out.println("asd");
    final String basicPath;
    
    final String classDir;
    
    
    final String classPath;
                
    if( 3 > args.length ) {
      System.err.println("At least 3 parameters needed.");
      return;
    }
    
    if ( args.length == 3)
      basicPath = "";
    else if ( 4 == args.length )
      basicPath = args[3];
    else {
      System.err.println("Can not have more than 4 parameters.");
      return;
    }
    classDir = args[0];
    classPath = args[1];
    
    outputFile = args[2];
    
    System.out.println("Directory " + classDir + "classPath");
    
    List<String> argsList = new ArrayList<String>();
    argsList.addAll(Arrays.asList(new String[] {"-allow-phantom-refs",  "-W", "-cp", classPath, "-keep-line-number", "-process-dir", classDir})); //parameters of Soot.
        String[] args2 = new String[argsList.size()];
    args2 = argsList.toArray(args2);

    soot.options.Options.v().setPhaseOption("cg","all-reachable:true");

    soot.Main.main(args2);

    CallGraph cg = Scene.v().getCallGraph();
    serializeCallGraph(cg, outputFile);
    
  }

}
