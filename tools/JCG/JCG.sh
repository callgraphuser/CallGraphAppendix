#!/bin/bash


clear
echo '*******************************'
echo '*******************************'
echo '       JCG java cg             '
echo '*******************************'
echo '*******************************'
echo '				     '
echo '1.  If u have ur project in type *.jar -> " ./JCG.sh example.jar " to analyze'
echo '                                                                                    '
echo '2.  Otherwise u have your classes .class format ->                                  '
echo '	  "./JCG.sh toCreate_jarfile_name.jar  path/from/this/dir/to/bin"          '
echo '-------------------------------'
echo ''

if [ "$#" -eq 1 ]; then
	echo '>[ U SELECTED THE 1-PARAMETER-CASE ] '
	echo '>[ JCG CG BUILDER HAS STARTED TO '$1 ']'
	java -jar javacg-0.1-SNAPSHOT-static.jar $1 | grep "^M:" > output.txt
	echo '>[ OUTPUT IS PRINTED TO THE output.txt ]'
elif [ "$#" -eq 2 ]; then
	echo '>[ U SELECTED THE 2-PARAMETER-CASE ] '
	echo '>[ CREATE JAR FILE WITH NAME:' $1 'IN DIRECTORY:' $2 ' ] '
	jar cfv $1 $2
	echo '>[ JCG CG BUILDER HAS STARTED TO '$1 ']'
	java -jar javacg-0.1-SNAPSHOT-static.jar $1 | grep "^M:" > output.txt
	echo '>[ OUTPUT IS PRINTED TO THE output.txt ]'
else
	echo '[ INVALID PARAMETERS ]'
fi
