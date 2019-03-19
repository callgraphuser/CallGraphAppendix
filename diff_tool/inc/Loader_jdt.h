#ifndef LOADER_JDT_H
#define LOADER_JDT_H

#include "Loader.h"

/**
* Processes the output of the tool JDT
*/


class Loader_jdt : public virtual Loader {
  
  public:
    Loader_jdt(std::string filepath, std::string name);
    ~Loader_jdt();
    std::vector<Record> load();
    std::set<std::pair<int, int>> transformConnections();
};

#endif