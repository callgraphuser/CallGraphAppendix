#ifndef LOADER_SPOON_H
#define LOADER_SPOON_H

#include "Loader.h"

/**
* Processes the output of the tool SPOON
*/

class Loader_spoon : public virtual Loader {
  public:
    Loader_spoon(std::string filepath, std::string name);
    ~Loader_spoon();
    std::vector<Record> load();
    std::set<std::pair<int, int>> transformConnections();
};

#endif