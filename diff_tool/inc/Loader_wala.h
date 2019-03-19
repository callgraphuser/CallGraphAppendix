#ifndef LOADER_WALA_H
#define LOADER_WALA_H

#include "Loader.h"

/**
* Processes the output of the tool WALA
*/

class Loader_wala : public virtual Loader {
  
  public:
    Loader_wala(std::string filepath, std::string name);
    ~Loader_wala();
    std::vector<Record> load();
    std::set<std::pair<int, int>> transformConnections();
};

#endif