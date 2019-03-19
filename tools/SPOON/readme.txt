Used SPOON version: https://github.com/INRIA/spoon/releases/tag/spoon-core-6.1.0
Used Apache CLI version: https://github.com/apache/commons-cli/releases/tag/cli-1.4

command line parameters:
-i directory where the source files can be found
-o directory where spoon puts working files
-p name of the project
--classp additional dependencies of the analysed project that spoon needs durin the compilation

example paramterization:
java -jar myspoon2.jar -i=C:\Users\username\Documents\myspoon2\spoontest\input -o=C:\Users\username\Documents\myspoon2\spoontest\spooned -p=test --classp=C:\Users\username\Documents\junit\junit\3.8.2\junit-3.8.2.jar,C:\Users\username\Documents\joda-convert-2.0.jar 