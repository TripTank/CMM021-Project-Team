/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package techtonic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jwitsml.Value;
import org.jwitsml.WitsmlTrajectory;
import org.jwitsml.WitsmlTrajectoryStation;
import org.jwitsml.WitsmlWellbore;

/**
 *
 * @author 1412625
 */
public class WellBoreListener implements ActionListener {

    WitsmlXMLReader xmlreader = new WitsmlXMLReader();
    private int id;
    private WitsmlWellbore wellbore;
    private JPanel chartPanel;
    int talker = 0;
    JFreeChart charts;
    XYSeries seriesMagger;

    public WellBoreListener(int id, WitsmlWellbore wellbore, JPanel pan) {
        this.id = id;
        this.wellbore = wellbore;
        this.seriesMagger = seriesMagger;
        chartPanel = pan;
        talker++;
        System.out.print("talker created   ==>>>  "+talker);
   
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WitsmlWellbore getWellbore() {
        return wellbore;
    }

    public void setWellbore(WitsmlWellbore wellbore) {
        this.wellbore = wellbore;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WitsmlTrajectory> trajectorys = xmlreader.getTrajectorys(wellbore);
        for (WitsmlTrajectory trajectory : trajectorys) {
            System.out.println(
                    "name:  " + trajectory.getName() + " "
                    + "tvdunit:  " + trajectory.getTvdUnit() + " "
            );
            System.out.println("");
            System.out.println("Trajectory Stations");
            System.out.println("***************************************************************");
            System.out.println("*/ " + trajectory.getName() + " has   " + trajectory.getStations().size() + "  stations");
            //Set<WitsmlTrajectoryStation> stations = trajectory.getStations();
            //System.out.println(stations);
            List<WitsmlTrajectoryStation> stationsAsList = Arrays.asList(new WitsmlTrajectoryStation[trajectory.getStations().size()]);
            for (WitsmlTrajectoryStation s : trajectory.getStations()) {
                stationsAsList.set(s.getStationNo(), s);
            }

            for (WitsmlTrajectoryStation station : stationsAsList) {
                System.out.println(
                        "tvd: " + station.getTvd() + "  "
                        + "md:  " + station.getMd() + "  "
                        + "north:  " + station.getNorth() + "  "
                        + "east:  " + station.getEast() + "  "
                        + "gravity:  " + station.getInclination() + "  "
                );
            }
            // create a DataSet object for storing xy data 
            XYSeries series = new XYSeries("series name");
// add data to Dataset (here assume data is in ArrayLists x and y
            int count = 1;
            for (WitsmlTrajectoryStation station : stationsAsList) {
                
                Value tvd = station.getTvd();
                if(tvd == null){
                continue;
                }
                Value md = station.getTvd();
                if(md == null){
                    continue;
                }
           //     System.out.println(count + " : ===>> tvd : "+tvd.getValue()+ "; md "+md.getValue());
                series.add(tvd.getValue(), md.getValue());
                count++;
            }
            
            XYSeriesCollection data = new XYSeriesCollection();
            data.addSeries(series);
            
// create a chart using the createYLineChart method...
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Line Chart", // chart title
                    "x", "y", // x and y axis labels
                    data); // data
            chart.setBackgroundPaint(Color.BLACK);
// display the chart on a ChartFrame...
//            ChartFrame frame = new ChartFrame(
//                    "XY graph using JFreeChart", chart);
//            frame.pack();
//            frame.setVisible(true);
            Techtonic.setChart(chart);
          
            ChartPanel cp = new ChartPanel(chart);
            cp.setMouseZoomable(true, true);
            chartPanel.setLayout(new java.awt.BorderLayout());
            chartPanel.add(cp,BorderLayout.CENTER);
            chartPanel.validate();        
            System.out.println("");
            System.out.println("");
        }
    }

}
