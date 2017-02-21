 
package techtonic;

import java.util.ArrayList;
import java.util.List;
import org.jwitsml.WitsmlLog;
import org.jwitsml.WitsmlTrajectory;
import org.jwitsml.WitsmlWell;
import org.jwitsml.WitsmlWellbore;


public class Cmm021witsml {

    public static void main(String[] args) {
        WitsmlXMLReader xmlreader = new WitsmlXMLReader();
        
        List<WitsmlWell> wells = xmlreader.getWells();
        WitsmlWell well = wells.get(0);
        System.out.println(
                "name:  " + well.getName() + "\n" +
                "operator:  " + well.getOperator() + "\n"
        );
               
        List<WitsmlWellbore> wellbores = xmlreader.getWellbores(well);
        WitsmlWellbore wellbore = wellbores.get(0);
        System.out.println(
                "name:  " + wellbore.getName() + "\n" +
                "status:  " + wellbore.getStatus() + "\n"
        );
        
        List<WitsmlTrajectory> trajectorys = xmlreader.getTrajectorys(wellbore);
        WitsmlTrajectory trajectory = trajectorys.get(0);
        System.out.println(
                "name:  " + trajectory.getName() + "\n" +
                "tvdunit:  " + trajectory.getTvdUnit() + "\n"
        );
 
        List<WitsmlLog> logs = xmlreader.getLogs(wellbore);
        WitsmlLog log = logs.get(0);
        System.out.println(
                "name: " + log.getName() + "\n" +
                "tvdunit:  " + log.getDescription() + "\n" +
                "tvdunit:  " + log.getIndexType() + "\n" 
            //    "tvdunit:  " + log. + "\n" 
        );    
    }
}
