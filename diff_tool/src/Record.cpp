#ifdef DEBUG
#define DDD cout << "records are compared...: " << endl; \
  cout << "records compared " << r << "and:" << *this << endl;

#else
#define DDD ;  
#endif

#ifdef DEBUGINFO
  #define DEBUG_EQUALITY cout << *this << " equals to " << r << endl;
#else
  #define DEBUG_EQUALITY ;
#endif

#ifdef DEBUGINFO
  #define DEBUG_COMPARE cout << *this << " is equals? " << r << endl;
#else
  #define DEBUG_COMPARE ;
#endif


#include "../inc/Record.h"
#include "../inc/common.h"


#include<stdio.h>


#include <algorithm>
#include <iostream>
#include <sstream>

using namespace std;


string genericParameterTypeNameArray[] = {"E", "K", "N", "T", "V"};


bool basic_type( const string& str ) {
  
  return str == "byte" ||
         str == "char" ||
         str == "short" ||
         str == "int" ||
         str == "long" ||
         str == "float" ||
         str == "double" ||
         str == "boolean";
}


bool anonym( const string& str ) {

  //an anonym is a number.
  for ( unsigned int i = 0; i < str.length(); i++ ) {

    if ( str[i] > '9' || str[i] < '0' )
    return false;
  }

  return true;
}


bool inner(string str) {

  stringstream input_stringstream(str);
  string part;

  while ( getline(input_stringstream, part , '$') ) {

    if ( anonym(part) ) { //if there is a total number part(an anonym) it is an anonym

      return true;
    }
  }

  return false;
}

bool anonymEqual( string str, string str2 ) {


  vector<string> parts1;
  parts1.clear();
  vector<string> parts2;
  parts2.clear();
  string part;

  stringstream input_stringstream(str);

  while ( getline(input_stringstream, part , '$') ) {

    parts1.push_back(part);

  }

  stringstream input_stringstream2(str2);

  while ( getline(input_stringstream2, part , '$') ) {

    parts2.push_back(part);

  }

  if ( parts1.size() != parts2.size() )
    return false;




  for ( unsigned int i = 0; i < parts1.size(); i++ ) {

    if ( ( anonym(parts1[i]) && anonym(parts2[i]) ) || parts1[i] == parts2[i] ) {

    }
    else {

      return false;
    }
  }

  return true;
}



string Record::createUnifiedMethodName() {

  string params = "";
  auto parameters = getParameters();
  for (unsigned i = 0; i < parameters.size(); i++) {

    params += parameters[i] + ",";
  }

  if (params.length() > 0 && ',' == params[params.length() - 1])
    params[params.length() - 1] = ')';
  else
    params += ")";
  return getPackage() + "." + getClass() + "." + getMethodName() + "(" + params;// + ")";
}



//----------------------------------------------public methods----------------------------------------------------

Record::Record( pair<string, string> rep, string package, string methodClass, string methodName, vector<string> parameters, int startLine, int endLine ) : package(package), methodClass(methodClass), methodName(methodName), parameters(parameters), startLine(startLine), endLine(endLine) {
  sameMethods.push_back(rep);
  unifiedRep = createUnifiedMethodName();

  if ( -1 != startLine && -1 == endLine ) {

    this->endLine = startLine;
  }
}

//-----------------------------For those where rep is not the line but an ID e.g. OSA---------------------------------------
Record::Record( pair<string, string> rep, string package, string methodClass, string methodName, vector<string> parameters, string secondaryRep, int startLine, int endLine ) : package(package), methodClass(methodClass), methodName(methodName), parameters(parameters), secondaryRep(secondaryRep), startLine(startLine), endLine(endLine) {
  sameMethods.push_back(rep);
  unifiedRep = createUnifiedMethodName();
  
  if ( -1 != startLine && -1 == endLine ) {
    
    this->endLine = startLine;
  }
}
//----------------------------------------------------------------------------------------------------------------
bool Record::operator==(const Record& r ) const {

DDD
  
  if ( r.unifiedRep == unifiedRep )
    return true;
  
  
  if ( common::options::initblock2init && package == r.package &&
       methodClass == r.methodClass &&
      (
        (methodName == "<init>" && r.methodName == "<initblock>") ||
        (r.methodName == "<init>" && methodName == "<initblock>")
      )
     ) {
    return true;
  }
      
  bool isEquals = false;
  
  
  if ( common::options::lineInfoPairing ) {
    
    if ( r.package == package && //the same package. It must not differ!
        ( anonymEqual(methodClass, r.methodClass) ) &&  //class names are the same or both of them is anonym
        ( anonymEqual(methodName, r.methodName) ) ) {  //the same method or both of them is anonym.
      
      if ( r.parameters.size() == parameters.size() ) { //must have the same number of parameters.
        
        if ( startLine != -1 && //this is a real information
            r.startLine != -1 && 
          ( (startLine <= r.startLine && endLine >= r.endLine) || (startLine >= r.startLine && endLine <= r.endLine) ) //a total interception, a in b or b in a
          ) {
  DEBUG_EQUALITY
    
          //isEquals = true; lehet, hogy rossz, mert ha van egy sorba több ilyen paraméterszámú, de mással..?
          bool match = true;
          for (unsigned int i = 0; i < parameters.size(); i++) {

            if ( parameters[i] == r.parameters[i] || //parameters are equal or
                 (!common::options::onlyAnonymWithLineinfo && //if variable is true: "Generic" search not allowed. !var. is false, so nothing is going to be excuted after the &&, is it should
                  ( //this is to identify what belongs together
                   !basic_type(parameters[i]) && !basic_type(r.parameters[i]) &&  //if not equal must not be basic type, no way of polimorphism.
                   (parameters[i].find("java.lang.Object") != string::npos || r.parameters[i].find("java.lang.Object") != string::npos ||
                   parameters[i].find("java/lang/Object") != string::npos || r.parameters[i].find("java/lang/Object") != string::npos ||//it might equal if one of them is Object.
                    (
                      common::options::genericParameterTypesNames &&  (
                        (parameters[i] == genericParameterTypeNameArray[0] || r.parameters[i] == genericParameterTypeNameArray[0]) ||
                        (parameters[i] == genericParameterTypeNameArray[1] || r.parameters[i] == genericParameterTypeNameArray[1]) ||
                        (parameters[i] == genericParameterTypeNameArray[2] || r.parameters[i] == genericParameterTypeNameArray[2]) ||
                        (parameters[i] == genericParameterTypeNameArray[3] || r.parameters[i] == genericParameterTypeNameArray[3]) ||
                        (parameters[i] == genericParameterTypeNameArray[4] || r.parameters[i] == genericParameterTypeNameArray[4]) 
                        
                      )
                    )
                  )
                 )
                )
              ) {

            }
            else {

              match = false;
              break;
            }
          }

          isEquals = match;
        }
        else if ( ( -1 == startLine && -1 == endLine ) || ( -1 == r.startLine && -1 == r.endLine ) ) {
          
          if ( r.package == package && //the same package. It must not differ!
        ( methodClass == r.methodClass) &&  //class names are the same
        ( methodName == r.methodName) )  {
          
            bool match = true;
            for (unsigned int i = 0; i < parameters.size(); i++) {

              if ( parameters[i] == r.parameters[i] || //parameters are equal or
                   (!common::options::onlyAnonymWithLineinfo && //if variable is true: "Generic" search not allowed. !var. is false, so nothing is going to be excuted after the &&, is it should
                    ( //this is to identify what belongs together
                     !basic_type(parameters[i]) && !basic_type(r.parameters[i]) &&  //if not equal must not be basic type, no way of polimorphism.
                     (parameters[i].find("java.lang.Object") != string::npos || r.parameters[i].find("java.lang.Object") != string::npos ||//it might equal if one of them is Object.
                      parameters[i].find("java/lang/Object") != string::npos || r.parameters[i].find("java/lang/Object") != string::npos ||
                      (
                        common::options::genericParameterTypesNames && (
                          (parameters[i] == genericParameterTypeNameArray[0] || r.parameters[i] == genericParameterTypeNameArray[0]) ||
                          (parameters[i] == genericParameterTypeNameArray[1] || r.parameters[i] == genericParameterTypeNameArray[1]) ||
                          (parameters[i] == genericParameterTypeNameArray[2] || r.parameters[i] == genericParameterTypeNameArray[2]) ||
                          (parameters[i] == genericParameterTypeNameArray[3] || r.parameters[i] == genericParameterTypeNameArray[3]) ||
                          (parameters[i] == genericParameterTypeNameArray[4] || r.parameters[i] == genericParameterTypeNameArray[4])
                        )
                      )
                     
                    )
                   )
                  )
                ) {

              }
              else {
                match = false;
                break;
              }
            }
            isEquals = match;
          }
        }
      }
    }
  }

  return isEquals;
}

bool Record::operator==( const pair<string, string> method ) const {
  
  if ( find( sameMethods.begin(), sameMethods.end(), method ) != sameMethods.end() ) {

    return true;
  }
  
  return false;
}

bool Record::operator==(const string& unifiedNode) const {

  bool found = false;
  if ( unifiedNode == unifiedRep )
    found = true;
  
  return found;
}


Record& Record::operator+=( const pair<string, string> nWOR ) {
  
  sameMethods.push_back(nWOR);
  
  return *this;
}

Record& Record::operator+=( const Record& r ) {
  
  
  if ( *this == r ) {
    
    for ( auto it = r.getSameMethods().begin(); it != r.getSameMethods().end(); ++it ) {
      
      sameMethods.push_back(*it);
    }
  }
  
  return *this;
}

bool Record::operator<( const Record& r ) const {
  
  if ( r.getClass().length() < methodClass.length() )
    return true;
  
  return false;
}

string Record::getPackage() const {
  
  return package;
}
string Record::getClass() const {
  
  return methodClass;
}
string Record::getMethodName() const {
  
  return methodName;
}
vector<string> Record::getParameters() const {
  
  return parameters;
}
vector<pair<string, string>> Record::getSameMethods() const {
  
  return sameMethods;
}

string Record::getSecondaryRepresentation() const {
  
  return secondaryRep;
}

string Record::getUnifiedRepresentation() const {

  return unifiedRep;
}


//friend
ostream& operator<<(ostream& o, const Record& r) {
  
  o << r.unifiedRep;
#ifdef DEBUG_LINEINFO
  o << " lineinfo: " << r.startLine << " " << r.endLine;
#endif
  
  return o;
}