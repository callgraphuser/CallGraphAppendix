Used WALA version: https://github.com/wala/WALA d901b13425b7052bab55cb900a24ce8689528f0b
Used Apache CLI version: https://github.com/apache/commons-cli/releases/tag/cli-1.4

example execution:
-i: the jar of the analysed project
-o: the directory where the output files are saved
-p: project name
-ex: exclusion file required by Wala
java -jar WalaTest.jar -i=C:\Users\username\WalaTest\testinput\osa_java.jar -o=C:\Users\username\WalaTest\testoutput -p=osa_java -ex=C:\Users\username\WalaTest\exclusions.txt