/* 
 * Copyright 2016 University of Adelaide.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package beans;

import java.io.Serializable;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

@SessionScoped
@ManagedBean(name = "barChartBean")
public class BarChartBean implements Serializable {
 
    private BarChartModel barModel;
    private HorizontalBarChartModel horizontalBarModel;
 
    
    @PostConstruct
    public void init() {
        createBarModels();
    }
 
    public BarChartModel getBarModel() {
        return barModel;
    }
     
    public HorizontalBarChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }
 
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            BarChartSeries series = new BarChartSeries();
            for (int j = 0; j < 10; j++) {
                if(i==j) {
                    series.set("Sample"+j, random.nextInt(200));
                } else {
                    series.set("Sample"+j, 0);                    
                }
            }
            series.setLabel("Sample "+i);                    
            model.addSeries(series);
        }
        
//        
//        ChartSeries boys = new ChartSeries();
////        boys.setLabel("Root_Z??");
//        boys.set("2004", 120);
//        model.
////        boys.set("2005", 100);
////        boys.set("2006", 44);
////        boys.set("2007", 150);
////        boys.set("2008", 25);
// 
//        ChartSeries girls = new ChartSeries();
//        girls.setLabel("Stem_Z??");
//        girls.set("2004", 52);
////        girls.set("2005", 60);
////        girls.set("2006", 110);
////        girls.set("2007", 135);
////        girls.set("2008", 120);
// 
//        model.addSeries(boys);
//        model.addSeries(girls);
//         
        return model;
    }
     
    private void createBarModels() {
        createBarModel();
//        createHorizontalBarModel();
    }
     
    private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("FPKMs for a gene");
        barModel.setStacked(true);
//        barModel.setLegendPosition("ne");
//        barModel.setLegendCols(5);
        barModel.setAnimate(true);
        
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Tissue/stage");
        xAxis.setTickAngle(-60);
        xAxis.setMin(-0.5);
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("FPKM");
//        yAxis.setMin(0);
//        yAxis.setMax(200);
    }
     
    private void createHorizontalBarModel() {
        horizontalBarModel = new HorizontalBarChartModel();
 
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 50);
        boys.set("2005", 96);
        boys.set("2006", 44);
        boys.set("2007", 55);
        boys.set("2008", 25);
 
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 82);
        girls.set("2007", 35);
        girls.set("2008", 120);
 
        horizontalBarModel.addSeries(boys);
        horizontalBarModel.addSeries(girls);
         
        horizontalBarModel.setTitle("Horizontal and Stacked");
        horizontalBarModel.setLegendPosition("e");
        horizontalBarModel.setStacked(true);
         
        Axis xAxis = horizontalBarModel.getAxis(AxisType.X);
        xAxis.setLabel("xlabel");
        xAxis.setMin(0);
        xAxis.setMax(200);
         
        Axis yAxis = horizontalBarModel.getAxis(AxisType.Y);
        yAxis.setLabel("ylabel");        
    }
 
}
