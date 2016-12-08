/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import org.primefaces.model.chart.BarChartModel;

/**
 *
 * @author rad
 */
public class ChartModelWithId extends BarChartModel{

    
    private final String chartId;
    private final String shortTitle;
    

    public ChartModelWithId( String chartId, String shortTitle) {
        this.shortTitle = shortTitle;
        this.chartId = chartId;
//            System.err.println(model+" "+chartId);
    }


    public String getChartId() {
        return chartId;
    }

//    public String getTitle() {
//        return getTitle().split(" in ")[1];
//    }

    public String getShortTitle() {
        return shortTitle;
    }
    
    
 }
