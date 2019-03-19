#ifndef NAMED_H
#define NAMED_H

/**
* This class is used to use different types of loaders generally.
*/

class Named {
  public:
    virtual std::string getName() const = 0;
    virtual std::string getFilePath() = 0;
    virtual std::string getKind() const = 0;
};

#endif