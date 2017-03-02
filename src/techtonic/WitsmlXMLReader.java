/*
Copyright 2010 Statoil ASA
Licensed under the Apache License, Version 2.0 (the "License"); you may not use 
this file except in compliance with the License. You may obtain a copy of the 
License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed 
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
CONDITIONS OF ANY KIND, either express or implied. See the License for the 
specific language governing permissions and limitations under the License.
*/

/* Version 4 - revised operation from Version 3 
Assumes structured directory of xml files, parameter in WitsmlReader 
is base path of directory, subsequent queries request type
and uses pre-existing list.txt */

package techtonic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jwitsml.*;

/**
 * Class representing the WITSML XML reader in the client program.
 */
public final class WitsmlXMLReader {
    
    static Capabilities clientCapabilities = new Capabilities(
                    WitsmlVersion.VERSION_1_3_1,
                    "David Lonie","d.p.lonie@rgu.ac.uk",
                    "+44 1224 262705","Simple test","Simple test","RGU","1.0");
    /** The XML file containing Witsml Query response that we will read */
   
    private static String xmlroot; 
    private static String xmlResources;
    private static String xmlWells;
    /** The version we will communicate. Non-null. */
    private static WitsmlVersion version = WitsmlVersion.VERSION_1_3_1; // default
    private String versionstr;

    /** Constructors from JWitsml class require a server object to associate to 
     * with each Witsml object, so create a dummy server for this purpose */
    private WitsmlServer dummyserver;
    
    List <String> wellUID;
    static XMLfiles xmls;
    
    static FileFilter getDirs =  (File f) -> f.isDirectory();
    
    static FileFilter getXML = (File f) -> f.getName().endsWith(".xml");
   
    private void setVersion(Element e){
        versionstr = e.getAttributeValue("version");
        switch(versionstr.substring(0,3)){
            case "1.2": version = WitsmlVersion.VERSION_1_2_0; break;
            case "1.3": version = WitsmlVersion.VERSION_1_3_1; break;
            case "1.4": version = WitsmlVersion.VERSION_1_4_0; break;
            default: version = null;
        }
    }

    // Constructs an XMLReader that points to a local copy of xml files
    public WitsmlXMLReader() {
        xmlroot = "xml";
        ClassLoader classLoader = getClass().getClassLoader(); 
        xmlWells = xmlroot + "/wells.xml";
        try{ //determine WITSML type from wells.xml
            SAXBuilder builder = new SAXBuilder();           
            Document document = builder.build(classLoader.getResourceAsStream(xmlWells));
            Element rootElement = document.getRootElement();
            setVersion(rootElement);
            xmls = new XMLfiles(xmlroot);
            loadList();
        }
        catch(JDOMException | IOException e){
            System.out.println("Issue when creating document from xml " + e);
        }
                      
        try{ // contents of this server are irrelevant, dummyserver used when creating Witsml object
            dummyserver = new WitsmlServer(new URL("http://rgu.ac.uk"), 
                    "username", "password", version, clientCapabilities);
        }
        catch(MalformedURLException e){
            System.out.println("Issue with url in dummyserver construction " + e);
        }
    }
    
   
    /**
     * Get WITSML version of this WITSML XML reader object
     * @return  WITSML version of this WITSML server.
     */
    public WitsmlVersion getVersion() {
        return version;
    }

    /**
     * Return the WITSML type for the specified class.
     * @param c  Class to check WITSML type for.
     * @return       WITSML type of the specified class.
     */
    private static <T extends WitsmlObject> String getWitsmlType(Class<T> c) {
        try {
            Field field = c.getDeclaredField("WITSML_TYPE");
            field.setAccessible(true);
            return (String) field.get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            assert false : "Invalid WitsmlObject class: " + c;
            return null;
        }
    }

    /**
     * Return the actual (i.e. version specific) class of the given WITSML type.
     * @param c  Class to check WITSML type for.
     * @return         The actual class. Never null.
     */
    @SuppressWarnings("unchecked")
    private static <T extends WitsmlObject> Class<? extends WitsmlObject> getActualClass(Class<T> c) {

        String type = getWitsmlType(c);
        String className = getWitsmlType(c); 
        try {
            className = WitsmlObject.class.getPackage().getName() +
                        "." +
                        "v" + version.getVersion().replace(".", "") +
                        "." +
                        "Witsml" +
                        type.substring(0, 1).toUpperCase() +
                        type.substring(1);

            return (Class<? extends WitsmlObject>) Class.forName(className);
        }
        catch (ClassNotFoundException exception) {
            assert false : "Invalid type: " + className;
            return null;
        }
    }    

    /**
     * Get ancestor IDs from the specified instance and up the hierarchy.
     * @param instance  Instance to start with. Non-null.
     * @return          IDs starting with ID of given instance and upwards.
     */
    private static String[] getIds(WitsmlObject instance) {
        assert instance != null : "instance cannot be null";

        List<String> ids = new ArrayList<>();
        WitsmlObject p = instance;
        while (p != null) {
            ids.add(p.getId());
            p = p.getParent();
        }
        return ids.toArray(new String[0]);
    }    

    /**
     * Return all instances of the specified class from this WITSML XML file.
     * @param <T>
     * @param baseClass    Class of instances to return. Non-null.
     * @return             Requested instances. Never null.
     * @throws IllegalArgumentException  If any of the arguments are null.
     */
    public <T extends WitsmlObject> List<T> get(Class<T> baseClass)
    {
        if (baseClass == null) throw new IllegalArgumentException("baseClass cannot be null");
        return get(baseClass, null, new String[0]);
    }

    /**
     * Return all children of the given class from the specified parent instance.
     * @param <T>
     * @param baseClass    Class of children to return. Non-null.
     * @param parent       Parent instance. May be null, to indicate root-level.
     * @return             Requested instances. Never null.
     * @throws IllegalArgumentException  If baseClass or witsmlQuery is null.
     */
    public <T extends WitsmlObject> List<T> get(Class<T> baseClass,  WitsmlObject parent)
    {
        if (baseClass == null) throw new IllegalArgumentException("baseClass cannot be null");
        return get(baseClass, parent, new String[0]);
    }

    /**
     * Return all children of the given class from the specified parent instance.
   * @param <T>
     * @param baseClass    Class of children to return. Non-null.
     * @param parentIds    Id of parent(s). Closest parent first. Null to indicate root-level.
     * @return             Requested instances. Never null.
     * @throws IllegalArgumentException  If baseClass or witsmlQuery is null.
     */
    public <T extends WitsmlObject> List<T> get(Class<T> baseClass, String ... parentIds)
    {
        if (baseClass == null) throw new IllegalArgumentException("baseClass cannot be null");
        return get(baseClass, null, parentIds);
    }


    /**
     * Return instances from this WITSML xml file.
     *
     * @param baseClass  Class of instances to get. Non-null.
     * @param parent       Parent object. Null to indicate root-level or if
     *                     parent IDs are specified instead.
     * @param parentIds    ID of parent(s). Closest parent first. Null to indicate
     *                     root-level or if parent instance is specified instead.
     * @throws IllegalArgumentException  If baseClass or witsmlQuery is null, or
     *                     If both parent and parentIds are non-null.
     * @throws WitsmlServerException  If the server access failed for some reason.
     */
    private <T extends WitsmlObject> List<T> get(Class<T> baseClass,
                                                 WitsmlObject parent,
                                                 String ... parentIds){
        String type = getWitsmlType(baseClass);
        Class<? extends WitsmlObject> actualClass = getActualClass(baseClass);
        
        String xmlpath = xmlroot;
        String[] parentId;
        if (parent != null) parentId = getIds(parent);
        else if (parentIds != null) parentId = parentIds;
        else parentId = new String[] {""};
  
        List<String> files = new ArrayList<>();
        String source;
        if(baseClass == WitsmlWell.class){
            files.add(xmlWells);
        }
        else if(parent == null){           
            files = xmls.getFilesAsString(type);
        }
        else{
            files = xmls.getFilesAsString(type, parentId[0]);
        }       
        // Prepare the return structure
        List<T> instances = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        try {  /*  DPL replaced query code by code that reads text from xml file */    
            for(String f: files){
                SAXBuilder builder = new SAXBuilder();          // create xml builder
                InputStream xml = classLoader.getResourceAsStream(f);
                Document xmldoc = builder.build(xml);     // create xml document object
                Element rootElement = xmldoc.getRootElement();  // identify root element in xml
                setVersion(rootElement);
                actualClass = getActualClass(baseClass);
                // Create a newInstance method for generic Witsml object
                Method newInstanceMethod = actualClass.getDeclaredMethod("newInstance",
                                                                         WitsmlServer.class,
                                                                         WitsmlObject.class,
                                                                         Element.class);
                newInstanceMethod.setAccessible(true);
                
                // Loop over the elements and create instances accordingly   
                List<?> elements = rootElement.getChildren(type, rootElement.getNamespace());
                for (Object element : elements) {
                    WitsmlObject instance = (WitsmlObject) newInstanceMethod.invoke(
                            null, dummyserver, parent, element);
                    instances.add(baseClass.cast(instance));
                }       
            }
            return instances;
        }
        catch (NoSuchMethodException exception) {// Programmer error
            assert false : "Method not found: " + actualClass; System.out.println(exception); return null;
        }
        catch (IllegalAccessException exception) {// Programmer error
            assert false : "Unable to invoke: " + actualClass; System.out.println(exception); return null;
        }
        catch (InvocationTargetException exception) {// Wrapped exception from the invocation such as WitsmlParseException
            String message = "Exception thrown by " + actualClass + ".newInstance()"; System.out.println(message + " " + exception); return null;
        }
        catch (IOException exception) {// Unable to read response XML
            String message = "Unable to read XML file: "; System.out.println(exception); return null;
        }
        catch (JDOMException exception) {// Unable to parse response XML
            String message = "Unable to parse XML: "; System.out.println(exception); return null;
        }
    }

    public void loadList() throws JDOMException, IOException{
        ClassLoader classLoader = getClass().getClassLoader(); 
        File list = new File(classLoader.getResource(xmlroot + "/list.txt").getFile());
        
        List<String> lines = Files.readAllLines(list.toPath());
//        System.out.println("Read all lines");
//        System.out.println(lines);
        for(String line: lines){
            String[] bits = line.split(",");
            xmls.add(bits[0],bits[1],bits[2]);
        }
    }

    public List<WitsmlWell> getWells(){
        return get(WitsmlWell.class);
    }
    
    public List<WitsmlWellbore> getWellbores(WitsmlWell well){
        return get(WitsmlWellbore.class, well);
    }
    
    public List<WitsmlTrajectory> getTrajectorys(WitsmlWellbore wellbore){
        return get(WitsmlTrajectory.class, wellbore);
    }
    
    public List<WitsmlLog> getLogs(WitsmlWellbore wellbore){
        return get(WitsmlLog.class, wellbore);
    }
}