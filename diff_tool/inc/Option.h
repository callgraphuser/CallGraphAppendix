#ifndef OPTION_H
#define OPTION_H

/**
* Command line options are handled in the same way, this class represents a general option
*/

#include <iostream>
#include <string>

class Option {
  
    std::string definer;  //string that is the option itself
    void (*fooP)(char**,int); //pointer to a function that should be executed when this option is set.
  public:
    /**
    * Creates an option
    * \param def what is the string to use this option
    * \param fooA a pointer to a method that should be executed
    */
    Option( std::string def, void (*fooA)(char**,int) );
    ~Option();
    
    /**
    * A method that is called and it calls the method that should be executed
    * \param argVector the argument list
    * \param argIndex which argument it should use.
    */
    void foo(char** argVector, int argIndex);
  
    bool operator==(const char* str) const;
};

#endif