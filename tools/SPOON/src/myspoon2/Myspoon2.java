/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myspoon2;

import myspoon2.processors.ASTVisitor;
import spoon.Launcher;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;

class Params {

    public String inputPath;
    public String outputPath;
    public String projectName;
    public String classpath;

    public String toString() {
        return "Input path:" + inputPath + "\nOutput path:" + outputPath + "\nProjectname:" + projectName + "\n";
    }
}

/**
 *
 * @author szte
 */
public class Myspoon2 {

    private static Params processCommandLineArguments(String[] args) {
        Options options = new Options();

        String outpOption = "output", inpOption = "input", projectName = "project", classp = "classp";

        Option input = new Option("i", inpOption, true, "location of sources");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("o", outpOption, true, "where to put output files");
        output.setRequired(false);
        options.addOption(output);

        Option proj = new Option("p", projectName, true, "project name");
        output.setRequired(false);
        options.addOption(proj);

        Option cp = new Option("c", classp, true, "Extra classpath elements");
        output.setRequired(false);
        options.addOption(cp);

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
        para.classpath = cmd.getOptionValue(classp);

        return para;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Params params = processCommandLineArguments(args);

        System.out.println(params);

        final String[] inner_args = {
            "-i", params.inputPath,
            "-o", params.outputPath
        };

        String[] both;
        String[] paths = {params.outputPath, params.inputPath};
        if (params.classpath.length() > 0) {
            String[] cps = params.classpath.split(",");
            both = (String[]) ArrayUtils.addAll(cps, paths);
        }else{
            both = paths;
        }

        Launcher launcher = null;

        launcher = new Launcher();

        launcher.setArgs(inner_args);

        launcher.getEnvironment().setSourceClasspath(both);

        launcher.buildModel();

        ASTVisitor viz = new ASTVisitor(params.projectName);

        launcher.getModel().getRootPackage().accept(viz);

        viz.generateDotFile();

        viz.generateGraphml();

    }

}
