#ifndef UNION_GRAPH_H
#define UNION_GRAPH_H

#include "Named.h"

/**
* A class similar to the loaders, but works on the loaded methods and connections.
*/

class UnionGraph : public Named {
  public:
    std::string getName() const {
      return "UnionGraph";
    }

    std::string getFilePath() {
      return "UnionGraph";
    }
  
    std::string getKind() const {
      
      return "U_";
    }
};

#endif
