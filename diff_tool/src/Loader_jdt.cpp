#include "../inc/Loader_jdt.h"
#include "../inc/common.h"
#include "../inc/Labels.h"

#include <set>
#include <iostream>
#include <string>
#include <sstream>
#include <algorithm>
#include <vector>

#ifdef JDT_DEBUG
  #define JDT_PRINT(a) cout << "Loaded jdt: " << a << endl;
#else
  #define JDT_PRINT(a) ;
#endif

using namespace std;

Loader_jdt::Loader_jdt( string filepath, string name ) : Loader(filepath, name) {
}
Loader_jdt::~Loader_jdt() {
}

vector<Record> Loader_jdt::load() {

  cout << "JDT loader" << endl;
  
  vector<Record> tmpRecords;
  
  string line;
  getline(input, line); //get the "header" line
  getline(input, line); //get the "header" line
  
  while ( getline(input, line) ) {
    
    if ( line.find("label") != string::npos ) {

      string representation;
      string pckgClass;
      string method;
      string infoMine;

      stringstream input_stringstream(line);
      getline(input_stringstream, representation, ' ');
      getline(input_stringstream, infoMine, ' ');
      
      {
        string plus;
        while ( getline(input_stringstream, plus, ' ') ) {
          
          infoMine += plus;
        }
      }
      
      infoMine.erase(0, 8); //[label=" --> 8 chars
      size_t ending = infoMine.rfind("\"]");
      if (ending != string::npos)
        infoMine.erase(ending);

      string pckgClassMethod;
      string paramsReturnFileInfo;
      
      stringstream iss(infoMine);
      getline(iss, pckgClassMethod, '(');
      getline(iss, paramsReturnFileInfo, '(');


      notFilteredMethodNames.insert(infoMine);

      //it is a node
      ++methodNum;

      size_t lastDotPos = pckgClassMethod.rfind("."); //find the last dot. From that point method name comes
      if (lastDotPos != string::npos) {

        method = pckgClassMethod.substr(lastDotPos + 1);
        pckgClass = pckgClassMethod.substr(0, lastDotPos);

      }
      else {

        throw Labels::METHOD_NOT_FOUND_ERROR;
      }

      vector<string> parameterVector;

      //delete everything after '(' as it is the return value
      size_t paramEnd = paramsReturnFileInfo.rfind(")");
      string fileInfo;
      
      if (paramEnd != string::npos) {
        //copy the fileinfo into the fileInfo string and delete it from the parameters
        fileInfo = paramsReturnFileInfo.substr(paramEnd + 2); //plus 2 it is the ')' and the ':'
        paramsReturnFileInfo.erase(paramEnd);
      }
      else {

        throw Labels::METHOD_PARAMETER_WRONG_FORMAT;
      }

      //split it on ';'
      stringstream paramStream(paramsReturnFileInfo); //here, only params in the string
      string parameter;
      while ( getline(paramStream, parameter, ';') ) {
        
        if (common::options::JDT_generics == common::enums::JDTGenerics::JDT_classAndParameters || common::options::JDT_generics == common::enums::JDTGenerics::JDT_onlyParameters) {
          size_t generics;
          while ( (generics = parameter.find("<")) != string::npos) {
            unsigned char i = 0;
            unsigned char openedInner = 1;
            do {
            //count how many chars to delete
              
              ++i;
              if ( parameter[generics + i] == '<' )
                ++openedInner;
              if ( parameter[generics + i] == '>' )
                --openedInner;
            }while(openedInner != 0 || parameter[generics + i] != '>');
            parameter.erase(generics, i+1);
          }
        }

        parameterVector.push_back(parameter);
      }
      
      //get the lineinfo
      string lineStr;
      stringstream lineInfoStream(fileInfo);
      getline(lineInfoStream, lineStr, ':'); //return type
      getline(lineInfoStream, lineStr, ':'); //file name
      getline(lineInfoStream, lineStr, ':'); //start line
      int startLine = atoi(lineStr.c_str());
      getline(lineInfoStream, lineStr, ':'); //start col
      getline(lineInfoStream, lineStr, ':'); //end line
      int endLine = atoi(lineStr.c_str());
      
      if ( 0 == pckgClass.length() || 0 == method.length() )
        throw Labels::UNINITIALIZED_RECORD;
       
      if ( common::options::anonymClassNameTransform > 0 ) {
        //there is a kind of transformation
        if ( 1 == common::options::anonymClassNameTransform ) {
          //turn every anonym class into a constant anonym class
          common::unifyeAnonymClasses(pckgClass);
          common::unifyeAnonymMethods(method);
        }
        else {
          
          throw Labels::ANONYM_CLASS_TRANSFORMATION_OPTION_UNKNOWN;
        }
      }
   
      string pckgStr, classStr;
      
      common::cutPckgClass(pckgClass, pckgStr, classStr);
      
      
      if (common::options::JDT_generics == common::enums::JDTGenerics::JDT_classAndParameters || common::options::JDT_generics == common::enums::JDTGenerics::JDT_onlyClass) {
        size_t generics;
        while ( (generics = classStr.find("<")) != string::npos) {
          unsigned char i = 0;
          unsigned char openedInner = 1;
          do {
          //count how many chars to delete
            ++i;
            if ( classStr[generics + i] == '<' )
                ++openedInner;
              if ( classStr[generics + i] == '>' )
                --openedInner;
          }while(openedInner != 0 || classStr[generics + i] != '>');
          classStr.erase(generics, i+1);
        }
      }

      Record r(pair<string, string>(representation, name), pckgStr, classStr, method, parameterVector, infoMine, startLine, endLine);
      if ( find(tmpRecords.begin(), tmpRecords.end(), r) == tmpRecords.end() )  //put it only if not here
        tmpRecords.push_back( r );
      
      if ( find( common::storedIds.begin(), common::storedIds.end(), r ) == common::storedIds.end() ) {
        //so this record is not found in the vector
        common::storedIds.push_back(r);
        ++uniqueMethodNum;
      }
      else {

        auto it = find( common::storedIds.begin(), common::storedIds.end(), r );
        if ( *it == pair<string, string>(representation, name) ) {
          //contains this representation
        }
        else {
          ++uniqueMethodNum;
          *it += pair<string, string>(representation, name);  //add this representation
        }
      }
      JDT_PRINT(r)
      
    } //end of processing label
  }//end of processing line
  
  input.clear();
  input.seekg(0, ios::beg);
  
  printNotFilteredMethodNames();

  return tmpRecords;
}

set<pair<int, int>> Loader_jdt::transformConnections() {

  cout << "JDT transform" << endl;
  
  set<pair<int, int>> connections;

  string line;
  getline(input, line); //get the "header" line
  getline(input, line); //get the "header" line

  while ( getline(input, line) ) {
    
  if ( line.find("label") == string::npos && line[0] != '}' ) {
      
      
    std::string delimiter = "->";
    size_t delimiter_pos = line.find(delimiter);
    string caller = line.substr(0, delimiter_pos);  //left part
    common::trim(caller);
    string callee = line.substr(delimiter_pos + delimiter.length());  //right part
    common::trim(callee);

    //it is a connection
    ++callNum;
      
      int callerId = -1, calleeId = -1;
      
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
    else {
      //do nothing
    }
  }

  return connections;
}