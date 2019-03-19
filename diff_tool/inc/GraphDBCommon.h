#ifndef GRAPHDB_COMMON_H
#define GRAPHDB_COMMON_H

#include <vector>
#include <set>
#include <utility>
#include "Record.h"

class GraphDBCommon {
  static bool executeCommand(const std::string& cmd, std::string& result);
public:
  static void writeUnifiedGraphToGraphml(std::string filename, const std::vector<Record>& records, const std::set<std::pair<int, int>>& connections);
  static bool convertGraphmlToJson(const std::string& in_filename, const std::string& o_filename);
  static bool uploadToDraphDB(const std::string& filename, bool clearGraph, const std::string& node_id);
  static bool compareInGraphDB(const std::string& graph_id1, const std::string& graph_id2);
  static void saveDBLabelsToFile(const std::vector<std::string>& labels, const std::string& filename);
  static void loadDBLabelsFromFile(std::vector<std::string>& labels, const std::string& filename);

};

#endif
