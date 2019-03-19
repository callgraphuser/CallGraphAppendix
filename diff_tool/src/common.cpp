#include "../inc/common.h"
#include "../inc/Loader.h"
#include "../inc/Labels.h"
#include <numeric>
#include<iostream>

namespace common {
  
  std::vector<Record> storedIds;
  std::vector<std::string> tsvFiles;
  std::vector<std::string> connTSVFiles;
  bool TRACE_RUN = false;

  void cutPckgClass(const std::string pckgClass, std::string& pckg, std::string& classStr) {
    size_t genericSign = pckgClass.find("<");
    size_t lastDot = 0;
    
    if (genericSign != std::string::npos) {

      lastDot = genericSign;
      while (lastDot != 0 && pckgClass[lastDot] != '.') {

        --lastDot;
      }

      if (lastDot != 0) {

        pckg = pckgClass.substr(0, lastDot);
        classStr = pckgClass.substr(lastDot + 1);
      }
    }
    else {

      lastDot = pckgClass.rfind(".");
      if (lastDot == std::string::npos) {

      }
      else {
        pckg = pckgClass.substr(0, lastDot);
        classStr = pckgClass.substr(lastDot + 1);
      }
    }    
  }
  
  bool unifyeAnonymClasses( std::string& str ) {
    
    bool replaced = false;
    
    for ( unsigned i = 0; i < str.length() - 1; i++ ) {
      
      if ( '$' == str[i] && str[i + 1] >= '0' && str[i + 1] <= '9' ) {
        
        str.erase(i+1);
        str += "UNIFIEDANONYMCLASSNAME";
        replaced = true;
        break;
      }
    }
    
    return replaced;
  }
  
  bool unifyeAnonymMethods( std::string& str ) {
    
    bool replaced = false;
    
    for ( unsigned i = 0; i < str.length() - 1; i++ ) {
      
      if ( '$' == str[i] && str[i + 1] >= '0' && str[i + 1] <= '9' ) {
        
        str.erase(i+1);
        str += "UNIFIEDANONYMMETHODNAME";
        replaced = true;
        break;
      }
    }
    
    return replaced;
  }
  
  void printFilteredMethod(const std::string& loadername, std::vector<Record> records) {
    std::ofstream of("non_filtered_methods_" + loadername + "_unified");
    if (!of.is_open()) {
      return;
    }
    of << records.size() << std::endl;
    std::sort(records.begin(), records.end(), [](const Record& lhs, const Record& rhs) {
      return lhs.getSecondaryRepresentation().compare(rhs.getSecondaryRepresentation()) < 0;
    });
    for (const auto& meth : records) {
      of << meth.getSecondaryRepresentation() << std::endl;
    }
    of.close();
  }

  std::vector<Record> filterNodes(const std::vector<Record>& unfiltered, std::set<int>& filteredIds) {
    std::vector<Record> filteredNodes;
    auto findId = [](const Record& c)->unsigned {
      for (unsigned i = 0; i < common::storedIds.size(); ++i) {
        if (common::storedIds[i] == c) {
          return i;
      }
    }
      return 0;
    };
    for (unsigned i = 0; i < unfiltered.size(); ++i) {
      std::string method = unfiltered[i].getPackage() + "." + unfiltered[i].getClass() + "." + unfiltered[i].getMethodName();
      if (!isExclude(method)) {
        filteredNodes.push_back(unfiltered[i]);
      }
      else {
        filteredIds.insert(findId(unfiltered[i]));
      }
    }
    return filteredNodes;
  }
  std::set<std::pair<int, int>> filterConnections(const std::set<std::pair<int, int>>& unfiltered, const std::set<int>& filteredIds) {
    std::set<std::pair<int, int>> filtered;
    for (const auto& edge : unfiltered) {
      if (filteredIds.find(edge.first) != filteredIds.end() || filteredIds.find(edge.second) != filteredIds.end()) {
        continue;
      }
      filtered.insert(edge);
    }
    return filtered;
  }

  bool isJavaLib(const std::string& method) {
    const static std::set<std::string> excludes = { "java.", "sun.", "javax." };
    for (const std::string& anExclude : excludes) {
      if (method.find(anExclude) == 0) {
        return true;
      }
    }
    if (Labels::PROJECT_PATH.length() > 0 && method.find(Labels::PROJECT_PATH) != 0) {
      return true;
    }

    return false;
  }

  bool isExcludableInit(const std::string& name) {
    static const std::set<std::string> excludedInits = { "<init>", "<clinit>", "<initblock>" };
    for (const std::string& anExclude : excludedInits) {
      if (name.find(anExclude) != std::string::npos) {
        return true;
      }
    }
    return false;
  }

  bool isExclude(const std::string& method) {
    switch (common::options::filterLevel) {
    case 0:
      return false;
    case 1:
      return isJavaLib(method);
    case 2:
      return isJavaLib(method) || isExcludableInit(method);
    default:
      return false;
    }
  }


  std::string produceMethodPairingName() {
	  switch (common::options::resolve) {
	  case common::enums::methodRes::nothing: return "nothing";
	  case common::enums::methodRes::unio: return "unio";
	  case common::enums::methodRes::section: return "section";
	  default: return "dafaq";
	  }
  }

  std::string produceFileNamePrefix() {
	  return Labels::PROJECT_NAME + "_" + produceMethodPairingName() + "_";
  }

  
}

namespace common {
  
  namespace options {
    unsigned char loadToGraph = 0;
    unsigned char filterLevel = 0;
    unsigned char calculateUnionGraph = 0;
    unsigned char anonymClassNameTransform = 0;
    bool lineInfoPairing = false;
    unsigned char resolve = 0;
    unsigned char JDT_generics = common::enums::JDTGenerics::JDT_classAndParameters;
    bool onlyAnonymWithLineinfo = false;
    bool genericParameterTypesNames = false;
    bool initblock2init = true;
  }
}