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
public class ChartModelWithId {

    private final BarChartModel model;
    private final String chartId;

    public ChartModelWithId(BarChartModel model, String chartId) {
        this.model = model;
        this.chartId = chartId;
//            System.err.println(model+" "+chartId);
    }

    public BarChartModel getModel() {
        return model;
    }

    public String getChartId() {
        return chartId;
    }

    public String getTitle() {
        return model.getTitle().split(" in ")[1];
    }
 }
