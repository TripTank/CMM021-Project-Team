
package techtonic;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jwitsml.WitsmlLog;
import org.jwitsml.WitsmlLogCurve;
import org.jwitsml.WitsmlTrajectory;
import org.jwitsml.WitsmlTrajectoryStation;
import org.jwitsml.WitsmlWell;
import org.jwitsml.WitsmlWellbore;


public class witsmlSample {

    public static void main(String[] args) {
        XYSeries series = new XYSeries("series name");
        
        System.out.println("****  Wells  ****");
        /* this line creates a "virtual" database connection that can be queried */
        WitsmlXMLReader xmlreader = new WitsmlXMLReader();
        
        
        /* We can query for all the WitsmlWell objects in the data set 
         * This is stored here in List of WitsmlWell objects */
        List<WitsmlWell> wells = xmlreader.getWells();
        
        /* here we loop through the list of WitsmWell's displaying a few of 
         * the attributes (there are may more e.g. see well.toString() ) */
        for (WitsmlWell well : wells) {
            System.out.println(
                    "name: " + well.getName() + "  "
                    + "status:  " + well.getStatus() + "  "
                    + "operator:  " + well.getOperator() + "  "
            );
        }
        System.out.println("\n");
        System.out.println("****  Wellbores  ****");
        
        /* Here we are picking ONE a particular WitsmlWell from the list */
        WitsmlWell well = wells.get(3);

        /* for a particular WitsmlLog we can query for all the WitsmlWellbore 
         * objects associated with that WitsmlWell */
        List<WitsmlWellbore> wellbores = xmlreader.getWellbores(well);
        
        /* here we loop through the list of WitsmWellbore's displaying a few of 
         * the attributes (there are may more e.g. see wellbore.toString() ) */
        for (WitsmlWellbore wellbore : wellbores) {
            System.out.println(
                    "name: " + wellbore.getName() + "  "
                    + "status:  " + wellbore.getStatus() + "  "
                    + "type:  " + wellbore.getType() + "  "
            );
        }
        
        
        /* Here we are picking ONE a particular WitsmlWellbore from the list */
        WitsmlWellbore wellbore = wellbores.get(0);
        
        System.out.println("\n\n");
        System.out.println("****  Trajectorys  ****");
        
        /* for a particular WitsmlWellbore we can query for all the WitsmlTrajectory 
         * objects associated with that WitsmlWellbore */
        List<WitsmlTrajectory> trajectorys = xmlreader.getTrajectorys(wellbore);
        
        /* here we loop through the list of WitsmTrajectory's displaying a few of 
         * the attributes (there are may more e.g. see trajectory.toString() ) */
        for (WitsmlTrajectory trajectory : trajectorys) {
            System.out.println(
                    "name:  " + trajectory.getName() + " " +
                "tvdunit:  " + trajectory.getTvdUnit() + " "            
            );
        }
        System.out.println("\n\n");
        System.out.println("****  Trajectory Stations  ****");
        
        /* Here we are picking ONE a particular WitsmlWellbore from the list */
        WitsmlTrajectory trajectory = trajectorys.get(0);
        System.out.println("*/ " + trajectory.getName() + " has   " + trajectory.getStations().size() + "  stations");

        /* for a particular trajectory we can use the getStations method to 
         * return the set of WitsmlTrajectoryStation objects associated with that trajectory */
        /* its'a bit awkward because the method returns a Set not a List */
        Set<WitsmlTrajectoryStation> stations = trajectory.getStations();
        /* If you need a List, e.g. to plot the data then can convert to a List*/
        List<WitsmlTrajectoryStation> stationsAsList = Arrays.asList(new WitsmlTrajectoryStation[trajectory.getStations().size()]);        
        for (WitsmlTrajectoryStation s: trajectory.getStations()){
                stationsAsList.set(s.getStationNo(),s);
        }
        
        /* here we loop through the list of WitsmLog's displaying a few of 
         * the attributes  */
        //for (WitsmlTrajectoryStation station : stations) { // this would work too
        for (WitsmlTrajectoryStation station : stationsAsList) {
            System.out.println(
                    "tvd: " + station.getTvd() + "  "
                    + "md:  " + station.getMd() + "  "
                    + "north:  " + station.getNorth() + "  "
                    + "east:  " + station.getEast() + "  "
            );
        }
        
        System.out.println("\n\n");
        System.out.println("****  Logs  ****");
        
          
        /* for a particular wellbore we can query for all the WitsmlLog 
         * objects associated with that wellbore */
        System.out.println("Wellbore : " + wellbore.toString());
        List<WitsmlLog> logs = xmlreader.getLogs(wellbore);
        
        System.out.println("Logs : " + logs.toString());
        
        /* here we loop through the list of WitsmLog's displaying a few of 
         * the attributes (there are may more e.g. see log.toString() ) */
        for (WitsmlLog log : logs) {
            System.out.println(
                    "name: " + log.getName() + "  "
                    + "description:  " + log.getDescription() + "  "
                    + "indexType:  " + log.getIndexType() + "  "
            );
        }
        System.out.println("\n\n");
        
        /* Now pick ONE a particular WitsmlLog from the list */
        WitsmlLog log = logs.get(0);
        
        /* for a particular WitsmlLog we can query for all the WitsmlLogCurve 
         * objects associated with that log */
        List<WitsmlLogCurve> curves = log.getCurves();

        /* the Curve at index 0 in that list will always be
         * depth for a depth-based log, or time for a time-based log */
        WitsmlLogCurve depth = curves.get(0);
        /* the other Curves at the other indices are the measurements taken at that depth or time */
        WitsmlLogCurve measurement = curves.get(1);
        
        System.out.println("\n\n****  Log Curves  ****");
        /* here we loop through the list of WitsmLogCurves's displaying a few of 
         * the attributes (there are may more e.g. see curve.toString() ) */
        for(WitsmlLogCurve c : curves){
            System.out.println(
                "curveNo: " + c.getCurveNo() + "\n" +
                "name: " + c.getName() + "\n" +
                "description:  " + c.getDescription() + "\n" +
                "unit:  " + c.getUnit()+ "\n" +
                "toString:  " + c.getUnit()+ "\n" 
            );
            /* for each WitsmlLogCurve we can also access the values it stores */
//            List<Object> values = c.getValues();
//            for(Object v: values){
//                System.out.print("v.toString() :"+
//                v.toString() + " ");
//            }
 List<Object> values = c.getValues();
                                for (int k = 0; k < values.size(); k++) {
                                    System.out.print(
                                            values.get(k) + " ");
                                    series.add((Integer) values.get(k), (Integer) values.get(k + 1));
                                }
                                XYSeriesCollection data = new XYSeriesCollection();
                                data.addSeries(series);
                                JFreeChart chart = ChartFactory.createXYLineChart(
                                        "Line Chart", // chart title
                                        "x", "y", // x and y axis labels
                                        data);
                                ChartFrame frameChat = new ChartFrame(
                                        "XY graph using JFreeChart", chart);
                                frameChat.pack();
                                frameChat.setVisible(true);
        }
        
       
        
    }
    
}
