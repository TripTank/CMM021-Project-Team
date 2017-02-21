package techtonic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author DL
 */
public class XMLfiles {
        private final File xmlRootDir;
        // Stores paths of XML files by (type/name)
        private final HashMap<String, HashMap<String, List<String>>> xmlFiles1;
        private final HashMap<String, HashMap<String, List<File>>> xmlFiles2;
        String root;
        
        public XMLfiles(File f){
            xmlRootDir = f;
            xmlFiles1 = new HashMap<>();
            xmlFiles2 = new HashMap<>();
        }
        
        public XMLfiles(String r){
            root = r;
            xmlRootDir = new File(r);
            xmlFiles1 = new HashMap<>();
            xmlFiles2 = new HashMap<>();
        }
        
        public void add(String type, String name, String f){
            if(!xmlFiles1.containsKey(type)) 
                xmlFiles1.put(type, new HashMap<String,List<String>>());
            if(!xmlFiles1.get(type).containsKey(name))
                    xmlFiles1.get(type).put(name, new ArrayList<String>());    
            xmlFiles1.get(type).get(name).add(root + "/" + type + "/" + f);        
        }
        
        public void add(String type, String name, File f){
            if(!xmlFiles2.containsKey(type)) 
                xmlFiles2.put(type, new HashMap<String,List<File>>());
            if(!xmlFiles2.get(type).containsKey(name))
                    xmlFiles2.get(type).put(name, new ArrayList<File>());    
            xmlFiles2.get(type).get(name).add(f);       
        }
        
        public List<String> getFilesAsString(String type, String name){
            if(xmlFiles1.containsKey(type) && xmlFiles1.get(type).containsKey(name)){ 
                return xmlFiles1.get(type).get(name);
            }
            else return new ArrayList<>(); 
        }
        
        public List<File> getFilesAsFile(String type, String name){
            if(xmlFiles2.containsKey(type) && xmlFiles2.get(type).containsKey(name)){ 
                return xmlFiles2.get(type).get(name);
            }
            else return new ArrayList<>(); 
        }
        
        public List<String> getFilesAsString(String type){
            List<String> files = new ArrayList<>(); 
            if(xmlFiles1.containsKey(type)){ 
                 for(List<String> fs: xmlFiles1.get(type).values()){
                     files.addAll(fs);
                 }
            }
            return files; 
        }

        public List<File> getFilesAsFile(String type){
            List<File> files = new ArrayList<>(); 
            if(xmlFiles2.containsKey(type)){ 
                 for(List<File> fs: xmlFiles2.get(type).values()){
                     files.addAll(fs);
                 }
            }
            return files; 
        }
}
          