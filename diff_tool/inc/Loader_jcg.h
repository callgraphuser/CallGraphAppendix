#ifndef LOADER_JCG_H
#define LOADER_JCG_H

#include "Loader.h"


/**
* Processes the output of the tool JCG
*/


class Loader_jcg : public virtual Loader {
  
  public:
    Loader_jcg(std::string filepath, std::string name);
    ~Loader_jcg();
    std::vector<Record> load();
    std::set<std::pair<int, int>> transformConnections();
};

#endif