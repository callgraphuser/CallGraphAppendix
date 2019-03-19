#include "../inc/Factory.h"
#include "../inc/Loader.h"
#include "../inc/GraphDBCommon.h"
#include "../inc/Loader_soot.h"
#include "../inc/Loader_osa.h"
#include "../inc/Loader_spoon.h"
#include "../inc/Loader_jcg.h"
#include "../inc/Loader_wala.h"
#include "../inc/Loader_jdt.h"
#include "../inc/Labels.h"

using namespace std;

bool Factory::factoryExist = false;
Factory* Factory::factory = NULL;
Factory::Factory() {
}

Loader* Factory::getLoaderPointer( string str, string filePath, unsigned counter ) const {
  
  if ( Labels::SOOT_CL == str ) {
    
    return new Loader_soot(filePath, Labels::SOOT_NAME + std::to_string(counter));
  }
  else if ( Labels::OSA_CL == str ) {
    return new Loader_osa(filePath, Labels::OSA_NAME + std::to_string(counter));
  }
  else if ( Labels::SPOON_CL == str ) {
    return new Loader_spoon(filePath, Labels::SPOON_NAME + std::to_string(counter));
  }
  else if ( Labels::JCG_CL == str ) {
    return new Loader_jcg(filePath, Labels::JCG_NAME + std::to_string(counter));
  }
  else if ( Labels::WALA_CL == str ) {
    return new Loader_wala(filePath, Labels::WALA_NAME + std::to_string(counter));
  }
  else if (Labels::JDT_CL == str) {
    return new Loader_jdt(filePath, Labels::JDT_NAME + std::to_string(counter));
  }
  else { 
    throw Labels::UNRECOGNIZED_SWITCH + str;
  }
}

Named* Factory::getUnionGraphPointer() const {
  return new UnionGraph();
}

Factory& Factory::createFactory() {
  
  if ( factoryExist )
    return *factory;
  
  Factory::factory = new Factory();
  factoryExist = true;
  
  return *factory;
  
}