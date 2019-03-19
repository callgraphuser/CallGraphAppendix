#include "../inc/Loader_jcg.h"
#include "../inc/common.h"
#include "../inc/Labels.h"

#include <set>
#include <iostream>
#include <string>
#include <sstream>
#include <algorithm>


#ifdef JCG_DEBUG
  #define JCG_PRINT(a) cout << "Loaded jcg: " << a << endl;
#else
  #define JCG_PRINT(a) ;
#endif

using namespace std;

Loader_jcg::Loader_jcg( string filepath, string name ) : Loader(filepath, name) {
}
Loader_jcg::~Loader_jcg() {
}

vector<Record> Loader_jcg::load() {

  
cout << "JCG loader" << endl;
  
  vector<Record> tmpRecords; 
  string member1, member2;

  while( input >> member1 >> member2 ) {
    
    methodNum += 2; //as it has 2 methods in a row
    
    string pckgClassMethod = member1;
    member1.erase(0,2);
    pckgClassMethod.erase(0, 2);  //erase "M:"
    
    int lineinfo = -1;
    string num;
    char i = 1;
    while ( pckgClassMethod[i] != ')' ) {

      num += pckgClassMethod[i];
      ++i;
    }
    ++i;
    pckgClassMethod.erase(0, i);
    
    lineinfo = atoi(num.c_str());
    
    string f_classWithPckg;
    string method;
    stringstream input_stringstream(pckgClassMethod);
    getline(input_stringstream, f_classWithPckg , ':');
    getline(input_stringstream, method, ':');


    notFilteredMethodNames.insert(member1);

    string parameters = method.substr(method.find("("));
    method.erase(method.find("("), method.length() - method.find("("));
    parameters.erase(parameters.length() - 1, 1);
    parameters.erase(0, 1);

    //cut apart parameters
    vector<string> parameterVector;
    stringstream iss(parameters);
    string parameter;
    while (getline(iss, parameter, ',')) {

      parameterVector.push_back(parameter);
    }


    if (common::options::anonymClassNameTransform > 0) {
      //there is a kind of transformation
      if (1 == common::options::anonymClassNameTransform) {
        //turn every anonym class into a constant anonym class
        common::unifyeAnonymClasses(f_classWithPckg);
        common::unifyeAnonymMethods(method);
      }
      else {

        throw Labels::ANONYM_CLASS_TRANSFORMATION_OPTION_UNKNOWN;
      }
    }
    
    string classStr;
    string packageStr;
    common::cutPckgClass(f_classWithPckg, packageStr, classStr);
    
    Record r(pair<string, string>(member1, name), packageStr, classStr, method, parameterVector, lineinfo, lineinfo);

    if (find(tmpRecords.begin(), tmpRecords.end(), r) == tmpRecords.end()) { //put it only if not here
      tmpRecords.push_back(r);

    }
    if (find(common::storedIds.begin(), common::storedIds.end(), r) == common::storedIds.end()) {
      //so this record is not found in the vector
      ++uniqueMethodNum;
      common::storedIds.push_back(r);
    }
    else {

      auto it = find(common::storedIds.begin(), common::storedIds.end(), r);
      if (*it == pair<string, string>(member1, name)) {
        //contains this representation
      }
      else {

        *it += pair<string, string>(member1, name);  //add this representation
        ++uniqueMethodNum;
      }
    }
    JCG_PRINT(r)
    
    //here comes the next node in the line
    string pckgClassMethod2 = member2;
    member2.erase(0,3);
    pckgClassMethod2.erase(0, 3);  //erase "(.)"
    
    string f_classWithPckg2;
    string method2;
    stringstream input_stringstream2(pckgClassMethod2);
    getline(input_stringstream2, f_classWithPckg2, ':');
    getline(input_stringstream2, method2, ':');

    notFilteredMethodNames.insert(member2);

    
    string parameters2 = method2.substr(method2.find("("));
    method2.erase(method2.find("("), method2.length()-method2.find("("));
    parameters2.erase(parameters2.length()-1, 1);
    parameters2.erase(0,1);

    //cut apart parameters
    vector<string> parameterVector2;
    stringstream iss2(parameters2);
    string parameter2;
    while( getline(iss2, parameter2, ',') ) {

      parameterVector2.push_back(parameter2);
    }
    
    if ( common::options::anonymClassNameTransform > 0 ) {
      //there is a kind of transformation
      if ( 1 == common::options::anonymClassNameTransform ) {
        //turn every anonym class into a constant anonym class
        common::unifyeAnonymClasses(f_classWithPckg2);
        common::unifyeAnonymMethods(method2);
      }
      else {
        
        throw Labels::ANONYM_CLASS_TRANSFORMATION_OPTION_UNKNOWN;
      }
    }
      
    string pckg2;
    string classStr2;
    common::cutPckgClass(f_classWithPckg2, pckg2, classStr2);

    Record r2(pair<string, string>(member2, name), pckg2, classStr2, method2, parameterVector2);

    if ( find(tmpRecords.begin(), tmpRecords.end(), r2) == tmpRecords.end() )  {//put it only if not here
      tmpRecords.push_back( r2 );

    }

    if ( find( common::storedIds.begin(), common::storedIds.end(), r2 ) == common::storedIds.end() ) {
      //so this record is not found in the vector
      ++uniqueMethodNum;
      common::storedIds.push_back(r2);
    }
    else {

      auto it = find( common::storedIds.begin(), common::storedIds.end(), r2 );
      if ( *it == pair<string, string>(member2, name) ) {
        //contains this representation
      }
      else {
        
        *it += pair<string, string>(member2, name);  //add this representation
        ++uniqueMethodNum;
      }
    }
    
    JCG_PRINT(r2)
  }
  
  
  input.clear();
  input.seekg(0, ios::beg);

  printNotFilteredMethodNames();

  return tmpRecords;
}

set<pair<int, int>> Loader_jcg::transformConnections() {
  
cout << "jcg transform" << endl;
  
  
  
  set<pair<int, int>> connections;  //TODO
  
  string member1, member2;
  
  while ( input >> member1 >> member2 ) {
    
    member1.erase(0,2);
    member2.erase(0,3);
    
    ++callNum;
    string caller = member1;
    string callee = member2;
    int callerId = -1, calleeId = -1;
    bool check = false;

  
    
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
  
  return connections;
}
