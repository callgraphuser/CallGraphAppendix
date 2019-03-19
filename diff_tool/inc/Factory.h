#ifndef FACTORY_H
#define FACTORY_H

#include "Loader.h"
#include "Named.h"
#include "UnionGraph.h"

/**
* Factory creates the loaders based on which command line argument is given.
*/

class Factory {
  
    static bool factoryExist; //there can be only one factory, it used for checking that.
    static Factory* factory;  //pointer to the factory, in creation it is set
    Factory();
    
  public:
    
    /**
    * Creates a new factory
    * \return a factory. If there is no factory it creates one otherwise returns the same that is already created.
    */
    static Factory& createFactory();
    
    /**
    * Gives back a loader based on the parameters.
    * \param definer defines which loader should be created.
    * \param filepath the path to the input file.
    * \param counter loaders are numbered thus one loader can be compared to the same and they can be distinguished.
    * \return Loader.
    */
    Loader* getLoaderPointer(std::string definer, std::string filePath, unsigned counter) const;
    
    /**
    * Gives back the pointer to the union grap creator.
    * \return Pointer to the union graph creator.
    */
    Named* getUnionGraphPointer() const;
};

#endif