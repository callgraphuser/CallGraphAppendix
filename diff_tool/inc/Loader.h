#ifndef LOADER_H
#define LOADER_H

#include "Record.h"
#include "Named.h"
#include<fstream>
#include <iostream>
#include<set>

/**
* A general class for handling and processing outputs from call graph creator tools
*/


class Loader : public Named {
    
    std::string filepath; //the path where the output is located.
  
  protected:
    std::ifstream input;  //stores the opened file
    unsigned long long methodNum; //number of all the methods in the output file
    unsigned long long callNum; //number of all the calls in the output file
    unsigned long long uniqueMethodNum; //some tools contains methods more than once, so it is incremented if such a method is found that has not been yet.
    std::string name; //name of the tool

    std::set<std::string> notFilteredMethodNames; //methods found in the output, these are not filtered

    void printNotFilteredMethodNames(); //prints the non filtered methods

  public:

  
    /**
    * Creates a new loader
    * \param filepath where the inputfile (output of the tool) is located
    * \param name name of the tool
    */
    Loader( std::string filepath, std::string name );
    
    /**
    * Loads the nodes to the method store, if no matching method is found creates a new one with a new id
    */
    virtual std::vector<Record> load() = 0;
  
    /**
    * Uses the ids (ones that is used by every other tool) instead of it's own method names
    * @return Returns a set of connections a connection is represented as a pair<caller, calle>
    */
    virtual std::set<std::pair<int, int>> transformConnections() = 0;
  
    virtual ~Loader();
    
  //*****************************getter methods*******************************************
    unsigned long long getMethodNum() const;
    unsigned long long getCallNum() const;
    std::string getFilePath();
    unsigned long long getUniqueMethodNum() const;
    std::string getName() const;
    std::string getKind() const;
    
  //in order to print information about loaders easier  
  friend std::ostream& operator<<(std::ostream& o, Loader* l );
  
};

#endif