#ifndef LABELS_H
#define LABELS_H

#include <iostream>

/**
* Constant strings used in the program.
*/

namespace Labels {
  
  extern const std::string FILE_READ_ERROR;
  extern const std::string FORMAT_NOT_FOUND_ERROR;
  extern const std::string UNINITIALIZED_RECORD;
  extern const std::string METHOD_NOT_FOUND_ERROR;
  extern const std::string COULD_NOT_WRITE_OUTPUT;
  extern const std::string UNRECOGNIZED_SWITCH;
  extern const std::string COULD_NOT_WRITE_TSV;
  extern const std::string TOOL_HAS_MORE_THAN_ONE_REP;
  extern const std::string NO_LOADER_WERE_GIVEN;
  extern std::string PROJECT_NAME;
  extern std::string PROJECT_PATH;
  extern const std::string COULD_NOT_WRITE_FILE_LIST;
  extern const std::string ANONYM_CLASS_TRANSFORMATION_OPTION_UNKNOWN;
  extern const std::string WALA_NON_INNERCLASS_EXISTENCE;
  extern const std::string COULD_NOT_WRITE_GRAPHML;
  extern const std::string PERCENTAGE_TABLE_FILE_COULD_NOT_OPEN;
  extern const std::string CMD_COULD_NOT_BE_CREATED;
  extern const std::string METHOD_PARAMETER_WRONG_FORMAT;
  extern const std::string PACKAGE_CLASS_WRONG_FORMAT;
  extern const std::string DEBUG_FILE_COULD_NOT_WRITE;
  extern const std::string ID_FILE_COULD_NOT_BE_CREATED;
  
  //******************* strings used for tool naming and options*************************
  extern const std::string SOOT_NAME;
  extern const std::string SOOT_CL; //Soot Command Line
  extern const std::string OSA_NAME;
  extern const std::string OSA_CL;
  extern const std::string SPOON_NAME;
  extern const std::string SPOON_CL;
  extern const std::string JCG_NAME;
  extern const std::string JCG_CL;
  extern const std::string WALA_NAME;
  extern const std::string WALA_CL;
  extern const std::string JDT_NAME;
  extern const std::string JDT_CL;
}

#endif