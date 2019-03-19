#ifndef LOADER_SOOT_H
#define LOADER_SOOT_H

#include "Loader.h"

/**
* Processes the output of the tool Soot
*/


class Loader_soot : public virtual Loader {
  
  public:
    Loader_soot(std::string filepath, std::string name);
    ~Loader_soot();
    std::vector<Record> load();
    std::set<std::pair<int, int>> transformConnections();
};

#endif