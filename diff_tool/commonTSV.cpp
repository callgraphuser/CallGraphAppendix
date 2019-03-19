#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <algorithm>
#include <sstream>

using namespace std;

#ifdef DEBUG
  #define DDD cout << line << endl;
#else
  #define DDD ;
#endif

#define SOOT_NUM 1
#define OSA_NUM 2
#define SPOON_NUM 4
#define JCG_NUM 8
#define WALA_NUM 16
#define JDT_NUM 32


class Record {
  public:
    string transformed;
    string soot;
    string osa;
    string spoon;
    string jcg;
    string wala;
    string jdt;
#ifdef TRACE
  string trace;
#endif
  
    unsigned long int metszet;
    
    Record(string t) : transformed(t) {
      
      soot = "x";
      osa = "x";
      spoon = "x";
      jcg = "x";
      wala = "x";
      jdt = "x";
#ifdef TRACE
    trace = "x";
#endif
      
      metszet = 0;
    }
  
    int commonNum() {
      
      int num = 0;
      if ( "x" != soot ) {
        ++num;
      }
      if ( "x" != osa ) {
        ++num;
      }
      if ( "x" != spoon ) {
        ++num;
      }
      if ( "x" != jcg ) {
        ++num;
      }
      if ( "x" != wala ) {
        ++num;
      }
      if ( "x" != jdt ) {
        ++num;
      }
#ifdef TRACE
    if ( "x" != trace ) {
      ++num;
    }
#endif
      
      return num;
    }
  
    int getMetszet() {
      
      if ( "x" != soot ) {
        metszet |=  SOOT_NUM;
      }
      if ( "x" != osa ) {
        metszet |=  OSA_NUM;
      }
      if ( "x" != spoon ) {
        metszet |=  SPOON_NUM;
      }
      if ( "x" != jcg ) {
        metszet |=  JCG_NUM;
      }
      if ( "x" != wala ) {
        metszet |=  WALA_NUM;
      }
      if ( "x" != jdt ) {
        metszet |=  JDT_NUM;
      }
#ifdef TRACE
    if ( "x" != trace ) {
      metszet |=  TRACE_NUM;
    }
#endif
      return metszet;
    }

  std::string getMetszetAsString() {
    
    std::string metszet = "";
    if ("x" != soot) {
      metszet += transformed;
    }
    metszet += ";";
    if ("x" != osa) {
      metszet += transformed;
    }
    metszet += ";";
    if ("x" != spoon) {
      metszet += transformed;
    }
    metszet += ";";
    if ("x" != jcg) {
      metszet += transformed;
    }
    metszet += ";";
    if ("x" != wala) {
      metszet += transformed;
    }
    metszet += ";";
    if ("x" != jdt) {
      metszet += transformed;
    }
#ifdef TRACE
  metszet += ";";
  if ("x" != trace) {
    metszet += transformed;
  }
#endif
    
    return metszet;
  }
    
  bool operator==(const string& str ) const {

    if ( str == transformed )
      return true;

    return false;
  }
};

class RecordConn {
  
  public:
    string connection;
    string soot;
    string spoon;
    string osa;
    string jcg;
    string wala;
    string jdt;
#ifdef TRACE
  string trace;
#endif
  
    int metszet;
    
    RecordConn( string str ) : connection(str) {
      
      soot = "";
      spoon = "";
      osa = "";
      jcg = "";
      wala = "";
      jdt = "";
#ifdef TRACE
    trace = "";
#endif
      
      metszet = 0;
    }
    
    int commonNum() {
      
      int num = 0;
      if ( "" != soot ) {
        ++num;
      }
      if ( "" != osa ) {
        ++num;
      }
      if ( "" != spoon ) {
        ++num;
      }
      if ( "" != jcg ) {
        ++num;
      }
      if ( "" != wala ) {
        ++num;
      }
      if ( "" != jdt ) {
        ++num;
      }
#ifdef TRACE
    if ( "" != trace ) {
      ++num;
    }
#endif
      return num;
    }
  
    int getMetszet() {
      
      if ( "" != soot ) {
        metszet |=  SOOT_NUM;
      }
      if ( "" != osa ) {
        metszet |=  OSA_NUM;
      }
      if ( "" != spoon ) {
        metszet |=  SPOON_NUM;
      }
      if ( "" != jcg ) {
        metszet |=  JCG_NUM;
      }
      if ( "" != wala ) {
        metszet |=  WALA_NUM;
      }
      if ( "" != jdt ) {
        metszet |=  JDT_NUM;
      }
#ifdef TRACE
   if ( "" != trace ) {
      metszet |=  TRACE_NUM;
    }
#endif
      return metszet;
    }

  std::string getMetszetAsString(const std::string& id) {
    std::string metszet = "";
    if ("" != soot) {
      metszet += id;
    }
    metszet += ";";
      if ("" != osa) {
        metszet += id;
      }
    metszet += ";";
      if ("" != spoon) {
        metszet += id;
      }
    metszet += ";";
      if ("" != jcg) {
        metszet += id;
      }
    metszet += ";";
      if ("" != wala) {
        metszet += id;
      }
    metszet += ";";
      if ("" != jdt) {
        metszet += id;
      }
#ifdef TRACE
    metszet += ";";
    if ("" != trace) {
      metszet += id;
    }
#endif
    return metszet;
  }
    
    bool operator==(const string& str ) const {
      
      if ( str == connection )
        return true;
      return false;
    }
};

int main(int argc, char** argv) {
  
  ifstream soot ;
  ifstream osa;
  ifstream spoon;
  ifstream jcg;
  ifstream wala;
  ifstream jdt;
#ifdef TRACE
  ifstream trace;
#endif
  
  ifstream sootConn;
  ifstream osaConn;
  ifstream spoonConn;
  ifstream chpConn;
  ifstream jcgConn;
  ifstream walaConn;
  ifstream jdtConn;
#ifdef TRACE
  ifstream traceConn;
#endif
  
  if ( 3 == argc ) {
    ifstream input(argv[1]);
    string file2open;
    
    while ( input >> file2open ) {
    
      if ( file2open.find("L_JCG") != string::npos )
        jcg.open(file2open.substr(6));
      else if ( file2open.find("L_Soot") != string::npos )
        soot.open(file2open.substr(7));
      else if ( file2open.find("L_OSA") != string::npos )
        osa.open(file2open.substr(6));
      else if ( file2open.find("L_SPOON") != string::npos )
        spoon.open(file2open.substr(8));
      else if ( file2open.find("L_WALA") != string::npos )
        wala.open(file2open.substr(7));
      else if ( file2open.find("L_JDT") != string::npos )
        jdt.open(file2open.substr(6));
      
#ifdef TRACE
    else if ( file2open.find("L_TRACE") != string::npos )
      trace.open(file2open.substr(8));
#endif
    }
    //--------------------------------------
    input.close();
    
    if ( !jcg.is_open() ) {
      cerr << "jcg could not be opened." << endl;
      return 1;
    }
    if ( !soot.is_open() ) {
      cerr << "soot could not be opened." << endl;
      return 1;
    }
    if ( !osa.is_open() ) {
      cerr << "osa could not be opened." << endl;
      return 1;
    }
    if ( !spoon.is_open() ) {
      cerr << "spoon could not be opened." << endl;
      return 1;
    }
    if ( !wala.is_open() ) {
      cerr << "wala could not be opened." << endl;
      return 1;
    }
    if ( !jdt.is_open() ) {
      cerr << "jdt could not be opened." << endl;
      return 1;
    }
    
#ifdef TRACE
  if ( !trace.is_open() ) {
    cerr << "trace could not be opened." << endl;
    return 1;
  }
#endif
    
    input.open(argv[2]);
    //--------------------------------------
    while( input >> file2open ) {
      if ( file2open.find("L_JCG") != string::npos )
        jcgConn.open(file2open.substr(6)); //L_JCG is 5 but the main program puts an extra number there, so it must be 6
      else if ( file2open.find("L_Soot") != string::npos )
        sootConn.open(file2open.substr(7)); //similar
      else if ( file2open.find("L_OSA") != string::npos )
        osaConn.open(file2open.substr(6));
      else if ( file2open.find("L_SPOON") != string::npos )
        spoonConn.open(file2open.substr(8));
      else if ( file2open.find("L_WALA") != string::npos )
        walaConn.open(file2open.substr(7));
      else if ( file2open.find("L_JDT") != string::npos )
        jdtConn.open(file2open.substr(6));
#ifdef TRACE
    else if ( file2open.find("L_TRACE") != string::npos )
      traceConn.open(file2open.substr(8));
#endif
    }
    input.close();
    
    if ( !jcgConn.is_open() ) {
      cerr << "jcgConn could not be opened." << endl;
      return 1;
    }
    if ( !sootConn.is_open() ) {
      cerr << "sootConn could not be opened." << endl;
      return 1;
    }
    if ( !osaConn.is_open() ) {
      cerr << "osaConn could not be opened." << endl;
      return 1;
    }
    if ( !spoonConn.is_open() ) {
      cerr << "spoonConn could not be opened." << endl;
      return 1;
    }
    if ( !walaConn.is_open() ) {
      cerr << "walaConn could not be opened." << endl;
      return 1;
    }
    if ( !jdtConn.is_open() ) {
      cerr << "jdtConn could not be opened." << endl;
      return 1;
    }
#ifdef TRACE
  if ( !traceConn.is_open() ) {
    cerr << "traceConn could not be opened." << endl;
    return 1;
  }
#endif
    
  }
  else {
    
    if ( 9 != argc ) {
#ifdef TRACE
      cerr << "Must give the 14 inputs (1 method 1 connection) (outputs of the tools) in the following order: JCG Soot OSA SPOON WALA JDT TRACE JCGConn SootConn OSAConn SPOONConn WALAConn JDTConn TRACEConn" << endl;
#else
      cerr << "Must give the 12 inputs (1 method 1 connection) (outputs of the tools) in the following order: JCG Soot OSA SPOON WALA JDT JCGConn SootConn OSAConn SPOONConn WALAConn JDTConn" << endl;
#endif
      return 1;
    }
    int index = 1;
    
    jcg.open(argv[index++]);
    soot.open(argv[index++]);
    osa.open(argv[index++]);
    spoon.open(argv[index++]);
    wala.open(argv[index++]);
    jdt.open(argv[index++]);
#ifdef TRACE
  trace.open(argv[index++]);
#endif
    
    jcgConn.open(argv[index++]);
    sootConn.open(argv[index++]);
    osaConn.open(argv[index++]);
    spoonConn.open(argv[index++]);
    walaConn.open(argv[index++]);
    jdtConn.open(argv[index++]);
#ifdef TRACE
  traceConn.open(argv[index++]);
#endif
  }
  
//------load connections-------
  vector<RecordConn> connections;
  string linec, connStr, toolc;
  
  ofstream connout("commonConnections.tsv");

  ofstream vennC("commonConnectionsVenn.csv");
  //vennC << "Soot;OSA;SPOON;JCG;WALA;TRACE"<<std::endl;
  //vennC << "Soot;OSA;SPOON;JCG;WALA;JDT"<<std::endl;
  
#ifdef TRACE
  vennC << "Soot;OSA;SPOON;JCG;WALA;JDT;TRACE"<<std::endl;
#else
  vennC << "Soot;OSA;SPOON;JCG;WALA;JDT"<<std::endl;
#endif
  
  //-------------------------jcg---------------
  jcgConn >> connStr >> toolc; 
  cout << "JCG connections" << endl;
  
  getline(jcgConn, linec);
  
  while ( getline(jcgConn, linec) ) {

    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');

    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->jcg = "X";
    }
    else {
      RecordConn r(connStr);
      r.jcg = "X";
      connections.push_back(r);
    }
  }
  //-----------------Soot----------
  sootConn >> connStr >> toolc;
  cout << "Soot connections" << endl;
  
  getline(sootConn, linec);
  
  while ( getline(sootConn, linec) ) {
    
    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');
    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->soot = "X";
    }
    else {
      RecordConn r(connStr);
      r.soot = "X";
      connections.push_back(r);
    }
  }
  //-------------OSA-------------
  osaConn >> connStr >> toolc; 
  cout << "OSA connections" << endl;
  
  getline(osaConn, linec);
  
  while ( getline(osaConn, linec) ) {
    
    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');
    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->osa = "X";
    }
    else {
      RecordConn r(connStr);
      r.osa = "X";
      connections.push_back(r);
    }
  }
  //----------SPOON-----------
  spoonConn >> connStr >> toolc; 
  cout << "SPOON connections" << endl;
  
  getline(spoonConn, linec);
  
  while ( getline(spoonConn, linec) ) {
    
    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');
    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->spoon = "X";
    }
    else {
      RecordConn r(connStr);
      r.spoon = "X";
      connections.push_back(r);
    }
  }
  //-----WALA------
  walaConn >> connStr >> toolc; 
  cout << "WALA connections" << endl;
  
  getline(walaConn, linec);
  
  while ( getline(walaConn, linec) ) {
    
    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');
    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->wala = "X";
    }
    else {
      RecordConn r(connStr);
      r.wala = "X";
      connections.push_back(r);
    }
  }
  //--------JDT-------------
  jdtConn >> connStr >> toolc; 
  cout << "JDT connections" << endl;
  
  getline(jdtConn, linec);
  
  while ( getline(jdtConn, linec) ) {
    
    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');
    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->jdt = "X";
    }
    else {
      RecordConn r(connStr);
      r.jdt = "X";
      connections.push_back(r);
    }
  }
  
#ifdef TRACE
  //-------TRACE-------
  traceConn >> connStr >> toolc; 
  cout << "TRACE connections" << endl;
  
  getline(traceConn, linec);
  
  while ( getline(traceConn, linec) ) {
    
    stringstream input_stringstream(linec);

    getline(input_stringstream, connStr , '\t');
    getline(input_stringstream, toolc , '\t');
    
    auto it = find(connections.begin(), connections.end(), connStr);
    if ( it != connections.end() ) {
      //found it
      it->trace = "X";
    }
    else {
      RecordConn r(connStr);
      r.trace = "X";
      connections.push_back(r);
    }
  }
#endif
  
  
  //-----kiiras
#ifdef TRACE
  connout << "Connection" << "\t" << "matching" << "\t" << "Soot(" << SOOT_NUM << ")" << "\t" << "OSA(" << OSA_NUM << ")" << "\t" << "SPOON(" << SPOON_NUM << ")" << "\t" << "JCG(" << JCG_NUM << ")" << "\t" << "WALA(" << WALA_NUM << ")" << "\t" << "JDT(" << JDT_NUM << ")" << "\t" << "TRACE(" << TRACE_NUM << ")" << "\t" << "IntersectionID" << endl;
#else
  connout << "Connection" << "\t" << "matching" << "\t" << "Soot(" << SOOT_NUM << ")" << "\t" << "OSA(" << OSA_NUM << ")" << "\t" << "SPOON(" << SPOON_NUM << ")" << "\t" << "JCG(" << JCG_NUM << ")" << "\t" << "WALA(" << WALA_NUM << ")" << "\t" << "JDT(" << JDT_NUM << ")" << "\t" << "IntersectionID" << endl;
#endif
  cout << connections.size() << endl;
  for ( unsigned i = 0; i < connections.size(); i++ ) {
#ifdef TRACE
    connout << connections[i].connection << "\t" << connections[i].commonNum() << "\t" << connections[i].soot << "\t" << connections[i].osa << "\t" << connections[i].spoon << "\t" << connections[i].jcg << "\t" << connections[i].wala << "\t" << connections[i].jdt << "\t" << connections[i].trace << "\t" << connections[i].getMetszet() << endl;
  
#else
    connout << connections[i].connection << "\t" << connections[i].commonNum() << "\t" << connections[i].soot << "\t" << connections[i].osa << "\t" << connections[i].spoon << "\t" << connections[i].jcg << "\t" << connections[i].wala << "\t" << connections[i].jdt << "\t" << connections[i].getMetszet() << endl;
#endif
    
  vennC << connections[i].getMetszetAsString(std::to_string(i)) << std::endl;
  }
  
  connout.close();
  vennC.close();
  
//-----load methods----------  
  vector<Record> methods;
  
  ofstream out("commonMethods.tsv");

  ofstream venn("commonMethodsVenn.csv");
  //venn << "Soot;OSA;SPOON;JCG;WALA;TRACE"<<std::endl;
  //venn << "Soot;OSA;SPOON;JCG;WALA;JDT"<<std::endl;
  
#ifdef TRACE
  venn << "Soot;OSA;SPOON;JCG;WALA;JDT;TRACE"<<std::endl;
  
#else
  venn << "Soot;OSA;SPOON;JCG;WALA;JDT"<<std::endl;
#endif
  string tool, trans, rep;
  string line;
  
  //---------------------------------
  
  cout << "JCG" << endl;

  jcg >> tool >> trans >> rep; //get the header
  getline(jcg, line);
DDD
  while( getline(jcg, line) ) {
DDD    
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');
    
    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->jcg = rep;
    }
    else {
      Record r(trans);
      r.jcg = rep;
      methods.push_back(r);
    }
  }
  //------------------------------------------
  
  soot >> tool >> trans >> rep; //get the header
  cout << "Soot" << endl;
 
  getline(soot, line);
DDD
  while( getline(soot, line) ) {
    
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');

    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->soot = rep;
    }
    else {
      Record r(trans);
      r.soot = rep;
      methods.push_back(r);
    }
  }
  //----
  
  cout << "OSA" << endl;

  osa >> tool >> trans >> rep; //get the header
  getline(osa, line);
DDD
  while( getline(osa, line) ) {
    
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');
    
    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->osa = rep;
    }
    else {
      Record r(trans);
      r.osa = rep;
      methods.push_back(r);
    }
  }
  //-------
  
  cout << "SPOON" <<endl;

  spoon >> tool >> trans >> rep; //get the header
  getline(spoon, line);
DDD
  while( getline(spoon, line) ) {
DDD
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');
    
    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->spoon = rep;
    }
    else {
      Record r(trans);
      r.spoon = rep;
      methods.push_back(r);
    }
  } 
  //-------

  cout << "WALA" << endl;

  wala >> tool >> trans >> rep; //get the header
  getline(wala, line);
DDD
  while( getline(wala, line) ) {
DDD    
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');
    
    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->wala = rep;
    }
    else {
      Record r(trans);
      r.wala = rep;
      methods.push_back(r);
    }
  }
  //--------------
  
  cout << "JDT" << endl;

  jdt >> tool >> trans >> rep; //get the header
  getline(jdt, line);
DDD
  while( getline(jdt, line) ) {
DDD    
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');
    
    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->jdt = rep;
    }
    else {
      Record r(trans);
      r.jdt = rep;
      methods.push_back(r);
    }
  }
  //--------------
  
  
  
#ifdef TRACE
  cout << "TRACE" << endl;

  trace >> tool >> trans >> rep; //get the header
  getline(trace, line);
DDD
  while( getline(trace, line) ) {
DDD    
    stringstream input_stringstream(line);

    getline(input_stringstream, tool , '\t');
    getline(input_stringstream, trans , '\t');
    getline(input_stringstream, rep , '\t');
    
    auto it = find(methods.begin(), methods.end(), trans);
    if ( it != methods.end() ) {
      //found it
      it->trace = rep;
    }
    else {
      Record r(trans);
      r.trace = rep;
      methods.push_back(r);
    }
  }
  
#endif
  //---write output
  
#ifdef TRACE
  out << "Transformed" << "\t" << "matching" << "\t" << "Soot.rep(" << SOOT_NUM << ")" << "\t" << "OSA.rep(" << OSA_NUM << ")" << "\t" << "SPOON.rep(" << SPOON_NUM << ")" << "\t" << "JCG.rep(" << JCG_NUM << ")" << "\t" << "WALA.rep(" << WALA_NUM << ")" << "\t" << "JDT(" << JDT_NUM << ")" << "\t" << "TRACE(" << TRACE_NUM << ")" << "\t" << "IntersectionID" << endl;
  
#else
  out << "Transformed" << "\t" << "matching" << "\t" << "Soot.rep(" << SOOT_NUM << ")" << "\t" << "OSA.rep(" << OSA_NUM << ")" << "\t" << "SPOON.rep(" << SPOON_NUM << ")" << "\t" << "JCG.rep(" << JCG_NUM << ")" << "\t" << "WALA.rep(" << WALA_NUM << ")" << "\t" << "JDT(" << JDT_NUM << ")" << "\t" << "IntersectionID" << endl;
  
#endif
  for ( unsigned i = 0; i < methods.size(); i++ ) {
    
#ifdef TRACE
    out << methods[i].transformed << "\t" << methods[i].commonNum() << "\t" << methods[i].soot << "\t" << methods[i].osa << "\t" << methods[i].spoon << "\t" << methods[i].jcg << "\t" << methods[i].wala << "\t" << methods[i].jdt << "\t" << methods[i].trace << "\t" << methods[i].getMetszet() << endl;
  
#else
    out << methods[i].transformed << "\t" << methods[i].commonNum() << "\t" << methods[i].soot << "\t" << methods[i].osa << "\t" << methods[i].spoon << "\t" << methods[i].jcg << "\t" << methods[i].wala << "\t" << methods[i].jdt << "\t" << methods[i].getMetszet() << endl;
    
#endif
  venn << methods[i].getMetszetAsString() << std::endl;
  }
  
  out.close();
  venn.close();
  
  return 0;
}
