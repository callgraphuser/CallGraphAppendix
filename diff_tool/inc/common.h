#ifndef COMMON_H
#define COMMON_H

#include <vector>
#include <algorithm> 
#include <cctype>
#include "Record.h"
#include <string>
#include <set>

class Loader;

/**
* Methods and variables used in various places of the program.
*/

namespace common {
  
  extern std::vector<Record> storedIds; //stores the loaded methods, the Records created from the methods.
  extern std::vector<std::string> tsvFiles; //tsv (tab separated files) are created in order to output different informations, these files are stored.
  extern std::vector<std::string> connTSVFiles; //the same as tsvFiles it just for the connection information.
  bool unifyeAnonymClasses( std::string& str ); //transforms anonym classes
  bool unifyeAnonymMethods( std::string& str ); //methods may be anonym, transforms anonym methods.
  bool isExclude(const std::string& method);  //Decides if a method should not be in the graph
  bool isJavaLib(const std::string& name);  //decides if a method is part of a Java library.
  bool isExcludableInit(const std::string& name); //Inits are filtered too.
  std::vector<Record> filterNodes(const std::vector<Record>& unfiltered, std::set<int>& filteredIds); //does the filtering for methods.
  std::set<std::pair<int, int>> filterConnections(const std::set<std::pair<int, int>>& unfiltered, const std::set<int>& filteredIds); //does the filtering for connections
  void cutPckgClass(const std::string pckgClass, std::string& pckg, std::string& classStr); //cuts apart pckg and class name.It assumes the followong format:pckg.hierarchy.ClassName[$Nesteds]
  void printFilteredMethod(const std::string& loadername, std::vector<Record> records); 

  std::string produceMethodPairingName();
  std::string produceFileNamePrefix();
  
  extern bool TRACE_RUN;
  
  
  /**
  * Connections are stored as a pair of ids. This method gets the method name for an id.
  */
  static inline std::string getMethodById( const unsigned int& id ) {
    
    if ( id < storedIds.size() ) {
      
      std::string params = "";
      for ( unsigned i = 0; i < storedIds[id].getParameters().size(); i++ ) {
        
        params += (storedIds[id].getParameters())[i] + ",";
      }
      //params.erase(params.length() - 1, 1); //the closing ','
      if (params.length() > 0 && ',' == params[params.length()-1] )
        params[params.length()-1] = ')';
      else
        params += ")";
      return storedIds[id].getPackage() + "." + storedIds[id].getClass() + "." + storedIds[id].getMethodName() + "(" + params;// + ")";
    }
    
    return "";
  }
  
  static inline void ltrim(std::string &s) {
    s.erase(s.begin(), std::find_if(s.begin(), s.end(), [](int ch) {
      return !std::isspace(ch);
    }));
  }

  static inline void rtrim(std::string &s) {
    s.erase(std::find_if(s.rbegin(), s.rend(), [](int ch) {
      return !std::isspace(ch);
    }).base(), s.end());
  }

  static inline void trim(std::string &s) {
    ltrim(s);
    rtrim(s);
  }

  static inline bool checkAndSetId(const std::string& id_str, int& id_num, const std::string& tool_str) {
    for (unsigned i = 0; i < common::storedIds.size(); i++) {
      if (common::storedIds[i] == std::pair<std::string, std::string>(id_str, tool_str) ) {
        id_num = i;
        return true;
      }
    }
    return false;
  };
  
}

namespace common {
  
  namespace options {
    
    /**
    * Options are common.
    * Based on these variables loaders can decide what to do or not to do.
    * Option Methods set these variables.
    */
    
    extern unsigned char loadToGraph;
    extern unsigned char filterLevel;
    extern unsigned char calculateUnionGraph;
    extern unsigned char anonymClassNameTransform;
    extern bool lineInfoPairing;
    extern unsigned char resolve;
    extern unsigned char JDT_generics;
    extern bool onlyAnonymWithLineinfo;
    extern bool genericParameterTypesNames;
    extern bool initblock2init;
    
  }

}

namespace common {
  
  namespace enums {
    
    /*
    * The way of solving the mismatch of common methods
    */
    enum methodRes {
                    unio,
                    section,
                    nothing
    };

    /*
    * The way JDT resolves generic information
    */
    enum JDTGenerics {
                      JDT_nothing, 
                      JDT_onlyClass,
                      JDT_onlyParameters,
                      JDT_classAndParameters
    };
  }
}

#endif