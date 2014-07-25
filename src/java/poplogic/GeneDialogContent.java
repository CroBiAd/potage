/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import java.util.Random;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;

/**
 * Given a contig object plus the gene id  or index pointing to a gene, generate the content of the dialog to be displayed:
 * chart, annotation
 * @author rad
 */
public class GeneDialogContent {

    public GeneDialogContent(Contig contig, String geneToDisplayDataFor) {
    }
         
    
    public BarChartModel getBarChartModel() {
        BarChartModel barModel = new BarChartModel();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            BarChartSeries series = new BarChartSeries();
            for (int j = 0; j < 10; j++) {
                if (i == j) {
                    series.set("Sample" + j, random.nextInt(200));
                } else {
                    series.set("Sample" + j, 0);
                }
            }
            series.setLabel("Sample " + i);
            barModel.addSeries(series);
        }

        barModel.setTitle("FPKMs for a gene");
        barModel.setStacked(true);
        barModel.setAnimate(true);
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Tissue/stage");
        xAxis.setTickAngle(-60);
        xAxis.setMin(-0.5);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("FPKM");
        return barModel;
    }
}
