Used version: https://github.com/sed-inf-u-szeged/OpenStaticAnalyzer c6ae9e3b4ba006acf906ced39e7eb62ae0337065
Apply callgraph.patch to this version

example execution: 
Lim2CallGraph.exe -projectName:commons-math -useFilter:0 -rul:D:\Users\username\Documents\CallGraph.rul D:\Users\username\Documents\results\commons-math-MATH_3_6_1.lim

lim files are produced by the OSA analyser toolchain: https://github.com/sed-inf-u-szeged/OpenStaticAnalyzer
This contains an ast like representation of the source code.

-useFilter:0 turns of the filtering mechanism for java library methods

-rul : must be given