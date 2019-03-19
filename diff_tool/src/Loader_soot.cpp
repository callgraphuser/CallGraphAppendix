#ifdef DEBUG
#define DDD cout << "New record is added...: " << f_classWithPckg << " " << method << " " << endl;
#else 
#define DDD ;
#endif

#include "../inc/Loader_soot.h"
#include "../inc/common.h"
#include "../inc/Labels.h"

#include <set>
#include <iostream>
#include <string>
#include <sstream>
#include <algorithm>


#ifdef SOOT_DEBUG
  #define SOOT_PRINT(a) cout << "Loaded soot: " << a << endl;
#else
  #define SOOT_PRINT(a) ;
#endif


using namespace std;

Loader_soot::Loader_soot( string filepath, string name ) : Loader(filepath, name) {
}
Loader_soot::~Loader_soot() {
}

vector<Record> Loader_soot::load() {
  
  
cout << "soot loader" << endl;
  
  
  
  vector<Record> tmpRecords; 
  
  string line;
  getline(input, line); //get the "header" line
  
  while ( getline(input, line) ) {
    
    int lineinfo = -1;
    
    if ( line.find("->") == string::npos && line != "}" ) {
     
      
      string methodRepresentation = line; //save the line for representation
      methodRepresentation.erase(0, 4); //the leading spaces
      
      line.erase(0, 5); //remove the '<4 spaces>"' beginning
      if ( line[0] == '<' ) {
        
        //no line info
        line.erase(0, 1);
      }
      else {
        
        string num;
        char i = 1;
        while ( line[i] != ')' ) {
          
          num += line[i];
          ++i;
        }
        
        line.erase(0, i + 2);
        lineinfo = atoi(num.c_str());
      }
      //remove the '">' ending
      size_t ending = line.rfind(">\"");
      if (ending != string::npos)
        line.erase(ending);

      //get the needed info. for a Record then algorithm::find(.., r) can be used...
     //----------------------------------------------------------------------------------------
     
      string f_classWithPckg, tmp, method;
      stringstream input_stringstream(line);

      getline(input_stringstream, f_classWithPckg , ' ');
      getline(input_stringstream, tmp , ' ');
      getline(input_stringstream, method , ' ');

      f_classWithPckg.erase(f_classWithPckg.length()-1, 1); //removing the ":"


    notFilteredMethodNames.insert(methodRepresentation);

    //the line doesn't contains the "->" sub-string so it is not a caller-calle connection but a node
      ++methodNum;
      
      string parameters = method.substr(method.find("("));
      method.erase(method.find("("), method.length()-method.find("("));
      parameters.erase(parameters.length()-1, 1);
      parameters.erase(0,1);
      
      //cut apart parameters
      vector<string> parameterVector;
      stringstream iss(parameters);
      string parameter;
      while( getline(iss, parameter, ',') ) {
        
        parameterVector.push_back(parameter);
      }
      
      
      if ( common::options::anonymClassNameTransform > 0 ) {
        //there is a kind of transformation
        if ( 1 == common::options::anonymClassNameTransform ) {
          //turn every anonym class into a constant anonym class
          common::unifyeAnonymClasses(f_classWithPckg);
          common::unifyeAnonymMethods(method);
        }
        else {
          
          throw Labels::ANONYM_CLASS_TRANSFORMATION_OPTION_UNKNOWN;
        }
      }
     
      string pckgStr, classStr;
      common::cutPckgClass(f_classWithPckg, pckgStr, classStr);
      
      Record r(pair<string, string>(methodRepresentation, name), pckgStr, classStr, method, parameterVector, methodRepresentation, lineinfo);
      if ( find(tmpRecords.begin(), tmpRecords.end(), r) == tmpRecords.end() )  //put it only if not here
        tmpRecords.push_back( r );
      
      if ( find( common::storedIds.begin(), common::storedIds.end(), r ) == common::storedIds.end() ) {
        //so this record is not found in the vector
DDD
        ++uniqueMethodNum;
        common::storedIds.push_back(r);
      }
      else {

        auto it = find( common::storedIds.begin(), common::storedIds.end(), r );
        if ( *it == pair<string, string>(methodRepresentation, name) ) {
          //contains this representation
        }
        else {
          
          *it += pair<string, string>(methodRepresentation, name);  //add this representation
          ++uniqueMethodNum;
        }
      }
      
      SOOT_PRINT(r)
      
    }

  }
  input.clear();
  input.seekg(0, ios::beg);

  printNotFilteredMethodNames();
  
  return tmpRecords;
}

set<pair<int, int>> Loader_soot::transformConnections() {
  
  
cout << "Soot transform" << endl;
  
  
  set<pair<int, int>> connections;
  
  string line;
  getline(input, line); //get the "header" line
  
  while ( getline(input, line) ) {
    
    if ( line.find("->") != string::npos && line != "}" ) {
      
      
      line.erase(line.length()-1, 1); //as connections are closed by ";"
      
      string caller = line.substr(0, line.find("->"));  //left part
      caller.erase(0, 4); //removal of leading spaces
      string callee = line.substr(line.find("->")+2);  //right part
      int callerId = -1, calleeId = -1;

    //it is a connection
    ++callNum;
      
      bool check = false; //to check if the method do be found.
      
      for (unsigned i = 0; i < common::storedIds.size(); i++ ) {
        
        if ( common::storedIds[i] == pair<string, string>(caller, name) ) {
          
          check = true;
          callerId = i;
          break;
        }
      }
      if ( !check ) {
        
        cerr << "Method couldn't be resolved: " << caller << endl;
      }
      
      check = false;
      for (unsigned i = 0; i < common::storedIds.size(); i++ ) {
        
        if ( common::storedIds[i] == pair<string, string>(callee, name) ) {
          
          check = true;
          calleeId = i;
          break;
        }
      }
      if ( !check ) {
        
        cerr << "Method couldn't be resolved: " << callee << endl;
      }

      connections.insert(pair<int, int>(callerId, calleeId));
    }
  
  }

  return connections;
}