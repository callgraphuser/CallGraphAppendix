#ifndef LOADER_OSA_H
#define LOADER_OSA_H

#include "Loader.h"

/**
* Processes the output of the tool OSA
*/


class Loader_osa : public virtual Loader {
  
  public:
    Loader_osa(std::string filepath, std::string name);
    ~Loader_osa();
    std::vector<Record> load();
    std::set<std::pair<int, int>> transformConnections();
};

#endif