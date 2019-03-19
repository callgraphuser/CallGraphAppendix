#include "../inc/GraphDBCommon.h"
#include "../inc/common.h"
#include "../inc/Labels.h"
#include <fstream>
#include <stdio.h>

#ifndef _WIN32
#define _popen popen
#define _pclose pclose
#endif

using namespace std;

void GraphDBCommon::writeUnifiedGraphToGraphml(string filename, const vector<Record>& records, const set<pair<int, int>>& connections) {
  
  ofstream graphml(filename);
  if (!graphml.is_open())
    throw Labels::COULD_NOT_WRITE_GRAPHML;


  auto encode = [](const std::string& data) {
    std::string buffer;
    buffer.reserve(data.size());
    for (size_t pos = 0; pos != data.size(); ++pos) {
      switch (data[pos]) {
      case '&':  buffer.append("&amp;");       break;
      case '\"': buffer.append("&quot;");      break;
      case '\'': buffer.append("&apos;");      break;
      case '<':  buffer.append("&lt;");        break;
      case '>':  buffer.append("&gt;");        break;
      default:   buffer.append(&data[pos], 1); break;
      }
    }
    return buffer;
  };

  auto findUnifiedId = [](const Record& r) -> int{
    for (unsigned i = 0; i < common::storedIds.size(); ++i) {
      if (common::storedIds[i] == r) {
        return i;
      }
    }
    return -1;
  };

  graphml << "<?xml version='1.0' encoding='utf-8'?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n<key attr.name=\"label\" attr.type=\"string\" for=\"node\" id=\"d0\"/>\n";
  graphml << "<graph edgedefault=\"directed\">\n";


  for (unsigned i = 0; i < records.size(); ++i) {
    int id = findUnifiedId(records[i]);
    if (id < 0) {
      continue;
    }
    std::string label = common::getMethodById(static_cast<unsigned>(id));
    graphml << "<node id=\"" << id << "\">" << std::endl;
    graphml << "  <data key=\"d0\">" << encode(label) << "</data>" << std::endl;
    graphml << "</node>" << std::endl;
  }

  for (const auto edge : connections) {
    graphml << "<edge source=\"" << edge.first << "\"  target=\"" << edge.second << "\"/>" << std::endl;
  }


  graphml << "</graph></graphml>";
  graphml.close();
}

bool GraphDBCommon::executeCommand(const std::string& cmd, std::string& result) {
  FILE* pipe = _popen(cmd.c_str(), "r");
  if (!pipe) {
    std::cerr << "Failed to popen";
    return false;
  }
  char buffer[128];
  while (fgets(buffer, 128, pipe) != NULL) {
    std::cout << "Reading..." << std::endl;
    result.append(buffer);
  }
  auto returnCode = _pclose(pipe);

  std::cout << result << std::endl;
  std::cout << returnCode << std::endl;

  return returnCode == 0;
}


bool GraphDBCommon::convertGraphmlToJson(const std::string& in_filename, const std::string& o_filename) {
  std::string command("python GraphDBScripst/convert-graph-formats.py -i graphml -o json "+ in_filename + " " + o_filename), result;
  return executeCommand(command, result);
}

bool GraphDBCommon::uploadToDraphDB(const std::string& filename, bool clearGraph, const std::string& node_id) {
  
  std::string command("python GraphDBScripst/gremlin-uploader.py -os \"ws://localhost:8182/gremlin\" -ots \"g\" -i \"" + filename + "\" -nid \""+node_id + "\""), result;
  if (!clearGraph) {
    command += " -k";
  }
  return executeCommand(command, result);
}


void GraphDBCommon::saveDBLabelsToFile(const std::vector<std::string>& labels, const std::string& filename) {
  std::ofstream ofs(filename +  "_labels.txt");
  if (!ofs.is_open()) {
    return;
  }
  for (unsigned i = 0; i < labels.size(); ++i) {
    ofs << labels[i] << std::endl;;
  }
  ofs.close();
}

void GraphDBCommon::loadDBLabelsFromFile(std::vector<std::string>& labels, const std::string& filename) {
  std::ifstream ifs(filename + "_labels.txt");
  if (!ifs.is_open())
    return;
  std::string line;
  while (std::getline(ifs, line)) {
    common::trim(line);
    labels.push_back(line);
  }
  ifs.close();
}



bool GraphDBCommon::compareInGraphDB(const std::string& graph_id1, const std::string& graph_id2) {
  std::string command("python GraphDBScripst/gremlinstat.py -os \"ws://localhost:8182/gremlin\" -ots \"g\" -pr1 \""+graph_id1+"\" -pr2 \""+graph_id2+"\""), result;
  bool retVal = executeCommand(command, result);
  if (!retVal)
    return false;
  std::ofstream ofs(graph_id1+"_"+graph_id2+"_compare.txt");
  if (ofs.is_open()) {
    ofs.write(result.c_str(), result.size());
    ofs.close();
    return true;
  }
  return false;
}