#include "../inc/Labels.h"

namespace Labels {
  
  const std::string FILE_READ_ERROR = "Couldn't read file: ";
  const std::string FORMAT_NOT_FOUND_ERROR = "Format couldn't match the pre defined format.";
  const std::string UNINITIALIZED_RECORD = "Record could not be initialized. className packageName or methodName is missing.";
  const std::string METHOD_NOT_FOUND_ERROR = "Could not find method in the format.";
  const std::string COULD_NOT_WRITE_OUTPUT = "Could not write output file whilst making statistics.";
  const std::string UNRECOGNIZED_SWITCH = "The given switch could not be found: ";
  const std::string COULD_NOT_WRITE_TSV = "Could not write tsv (tab separated file).";
  const std::string TOOL_HAS_MORE_THAN_ONE_REP = "Tool has more than one representation for method.";
  const std::string NO_LOADER_WERE_GIVEN = "No loader was given. Please give at least 1 loader.";
  const std::string COULD_NOT_WRITE_FILE_LIST = "Could not write list of tsv files.";
  const std::string ANONYM_CLASS_TRANSFORMATION_OPTION_UNKNOWN = "Transformation option for anonym classes is invalid.";
  const std::string WALA_NON_INNERCLASS_EXISTENCE = "Wala is among the nodes, but could not remove it as there is no '$'.";
  const std::string COULD_NOT_WRITE_GRAPHML = "Could not write graphml file";
  std::string PROJECT_NAME = "Default";
  std::string PROJECT_PATH = "";
  const std::string PERCENTAGE_TABLE_FILE_COULD_NOT_OPEN = "Could not open csv file for percentages: ";
  const std::string CMD_COULD_NOT_BE_CREATED = "Could not write commandLine file.";
  const std::string METHOD_PARAMETER_WRONG_FORMAT = "Method's parameter had wrong format or for unknown reason could not be parsed.";
  const std::string PACKAGE_CLASS_WRONG_FORMAT = "The format of the package and the class name was not correct: ";
  const std::string DEBUG_FILE_COULD_NOT_WRITE = "The file that contains matching information (debug file) could not be written: ";
  const std::string ID_FILE_COULD_NOT_BE_CREATED = "ID and method mapping csv could not be created";
  
  //*********************************** strings used for tool naming and options *************************
  const std::string SOOT_NAME = "Soot";
  const std::string SOOT_CL = "-s"; //Soot Command Line
  const std::string OSA_NAME = "OSA";
  const std::string OSA_CL = "-osa";
  const std::string SPOON_NAME = "SPOON";
  const std::string SPOON_CL = "-sp";
  const std::string JCG_NAME = "JCG";
  const std::string JCG_CL = "-j";
  const std::string WALA_NAME = "WALA";
  const std::string WALA_CL = "-w";
  const std::string JDT_NAME = "JDT";
  const std::string JDT_CL = "-jdt";
}