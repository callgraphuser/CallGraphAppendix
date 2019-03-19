#ifndef SWITCH_H
#define SWITCH_H

#include "Loader.h"
#include "Factory.h"
#include <iostream>

/**
* A class for representing a switch. A switch can decide what kind of loader is set, it processes the command line arguments.
*/

class Switch {
    std::string definer; //the switch string that activates this one

    std::vector<std::string> filenames; //a tool can be compared to itself, but uses the same switch. it contains the files for the same tool.
    Factory& factory; //switch passes the creation of a loader to the factory.
  public:
    /**
    * Creates a switch
    * \param activationString what we have to type if we want to use a loader
    * \param f the factory, where the switch should pass the creation duty.
    */
    Switch( std::string activationString, Factory& f );
    /**
    * Adds the filepath to the vactor of files.
    */
    void init(std::string filePath );
    bool operator==( const std::string str ) const; //returns true if the given string is the activation string for the object.
    bool operator==( const char* str ) const;
    
    /**
    * Get the loader pointer
    * \param counter which number of this (defined by the switch) loader should be returned
    * \return A pointer to the loader.
    */
    Loader* getLoaderPointer(unsigned counter) const;
    
    //Make easier to print switches, it might useful when debugging.
    friend std::ostream& operator<<( std::ostream& o, Switch s );
  
};

#endif