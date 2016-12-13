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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.component.chart.Chart;
//import org.primefaces.component.chart.bar.BarChart;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.resizable.Resizable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartModel;

@RequestScoped
@ManagedBean(name = "dfView")
public class DFView implements Serializable {

    public void viewCars() {

        //works
//        Map<String, Object> options = new HashMap<>();
////        options.put("modal", true);
//        options.put("draggable", false);
//        options.put("resizable", false);
//        options.put("contentHeight", 520);
//        options.put("widgetVar", Math.random());
////        RequestContext.getCurrentInstance().openDialog("newxhtml");
//        RequestContext.getCurrentInstance().openDialog("newxhtml", options, null);
        BarChartModel barModel2 = initBarModel();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent panelGroup = facesContext.getViewRoot().findComponent("formCentre:panel_id1");

        Map<String, Object> attributes = panelGroup.getAttributes();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String string = entry.getKey();
            String string1 = entry.getValue().toString();
            System.out.println(string + "  " + string1);
        }
//        int width = Integer.parseInt(facesContext.getExternalContext().getRequestParameterMap().get("width"));
//        int height = Integer.parseInt(facesContext.getExternalContext().getRequestParameterMap().get("height"));
//        System.out.println("Width = "+width);
//        System.out.println("Height = "+height);

        int dialogWidth = 400;
        int dialogHeight = 200;
        int offsetWidth = dialogWidth/20;
        int offsetHeight = dialogHeight/20;
        
        int chartWidth = dialogWidth-offsetWidth;
        int chartHeight = dialogHeight-offsetHeight;
        System.out.println("chartHeight = " + chartHeight);
        System.out.println("chartWidth = " + chartWidth);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                BarChartModel barModel = initBarModel();
                String coordinates = (i*dialogWidth)+","+(j*dialogHeight);                
                Dialog dialog = new Dialog();
                dialog.setVisible(true);
                dialog.setMinimizable(true);
//                dialog.setResizable(false);
                dialog.setWidth(dialogWidth+"px");
                dialog.setHeight(dialogHeight+"px");
//                dialog.setPosition(coordinates); 
//                dialog.setPosition("left+"+(i*dialogWidth)+",top+"+(j*dialogHeight)); 
//                dialog.setStyle("overflow-y:auto; top:5px; bottom:5px; overflow-x:auto; left:5px; right:5px;");
                Map<Object, Object> map = requestContext.getAttributes();
                for (Object key: map.keySet()) {
                    System.err.println(key.toString()+"-----"+map.get(key));
                }
                
                String leftOffset = random.nextInt(75)+"%";
                String topOffset = random.nextInt(75)+"%";
                dialog.setPosition("left+"+leftOffset+",top+"+topOffset);
                dialog.setHeader(leftOffset+","+topOffset);
                dialog.setFitViewport(true);
//                System.err.println("getPosition="+dialog.getPosition());
                
//                System.err.println("Setting coordinates : "+coordinates);
//                dialog.setFitViewport(true);
                
                Chart chartComponent = new Chart();
                chartComponent.setType("bar");
                chartComponent.setModel(barModel);
                chartComponent.setStyle("height:"+chartHeight+"px; width: "+chartWidth+"px");
                
                
                panelGroup.getChildren().add(dialog);
                dialog.getChildren().add(chartComponent);
            }
        }
        requestContext.update("formCentre:panel_id1");

//        Dialog dialog2 = new Dialog();
//        dialog2.setHeader("Dialog2");
//        dialog2.setVisible(true);
//        dialog2.setMinimizable(true);
//        dialog2.setHeight("425px");
//        dialog2.setWidth("850px");
//        dialog2.setPosition("0,600");
//
//        Dialog dialog1 = new Dialog();
//        dialog1.setHeader("Dialog1");
//        dialog1.setVisible(true);
//        dialog1.setMinimizable(true);
//        dialog1.setHeight("425px");
//        dialog1.setWidth("850px");
//        dialog1.setPosition("250,800");
//
//        Chart chartComponent1 = new Chart();
//        chartComponent1.setType("bar");
//        chartComponent1.setModel(barModel);
//        chartComponent1.setStyle("height:400px; width: 800px");
//
//        Chart chartComponent2 = new Chart();
//        chartComponent2.setType("bar");
//        chartComponent2.setModel(barModel2);
//        chartComponent2.setStyle("height:400px; width: 800px");

//        panelGroup.getChildren().add(dialog1);
//        dialog1.getChildren().add(chartComponent1);
//        panelGroup.getChildren().add(dialog2);
//        dialog2.getChildren().add(chartComponent2);
//        RequestContext requestContext = RequestContext.getCurrentInstance();
//        requestContext.update("formCentre:panel_id1");

//        ExpressionFactory exFactory = ExpressionFactory.newInstance();
//        Chart barChart = (Chart) FacesContext.getCurrentInstance().getApplication().createComponent(Chart.COMPONENT_TYPE);
//        barChart.setId("barChart");
//        barChart.setModel(barChartBean.getBarModel());
////        barChart.setVar("chartData");
////        ValueExpression categoryFieldVE = exFactory.createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{chartData.category}", String.class);
////        pieChart.setCategoryField(categoryFieldVE);
////        ValueExpression dataFieldVE = exFactory.createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{chartData.data}", int.class);
////        pieChart.setDataField(dataFieldVE);
//
//        panelGroup.getChildren().add(barChart);
    }

    private BarChartModel initBarModel() {
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
