/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import beans.MainPopsBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.primefaces.component.chart.Chart;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;

/**
 *
 * @author rad
 */
public class Gene implements Serializable, Comparable<Gene> {
    //wheat contig info

    private final String geneId;
    private Contig contig;
    private Annotation annotationMips;
    private Annotation annotationRice;
    private ArrayList<Double> tissuesFPKMs;

    //location on contig
    private Integer from;
    private Integer to;
    private String strand;

    //for FPKM charts:
    private ArrayList<BarChartModel> barChartModel;
//    private BarChartModel barChartModel;
    private String[] fpkmTableHeaders;
    private ArrayList<String> fpkmSettings;

    //for charts tabview
    private int currentTabIndex = 0;
    private ArrayList<ChartModelWithId> chartModels;
    private TabView chartsTabView;
    
//    //place in full chromosome (pops and unanchored)
//    private int index;

    public Gene(String geneId) {
        this.geneId = geneId;
    }

    
    public Gene(String geneId, Contig contig, Annotation annotationMips, Annotation annotationRice, ArrayList<Double> tissuesFPKMs,
        String[] fpkmTableHeaders, String traes_to_CSS_entry, ArrayList<String> fpkmSettings) {
        this.geneId = geneId;
        this.contig = contig;
        this.annotationMips = annotationMips;
        this.annotationRice = annotationRice;
        this.tissuesFPKMs = tissuesFPKMs;
        if (traes_to_CSS_entry == null) {
            this.from = null;
            this.to = null;
            this.strand = null;
        } else {
            String toks[] = traes_to_CSS_entry.split(",");
            this.from = Integer.parseInt(toks[2]);
            this.to = Integer.parseInt(toks[3]);
            this.strand = toks[4].substring(0, 1);
        }
        this.fpkmTableHeaders = fpkmTableHeaders;
        this.fpkmSettings = fpkmSettings;
    }

    public Gene(String geneId, Contig contig, Annotation annotationMips, Annotation annotationRice, ArrayList<Double> tissuesFPKMs,
        String[] fpkmTableHeaders, int from, int to, String strand, ArrayList<String> fpkmSettings) {
        this.geneId = geneId;
        this.contig = contig;
        this.annotationMips = annotationMips;
        this.annotationRice = annotationRice;
        this.tissuesFPKMs = tissuesFPKMs;
        this.from = from;
        this.to = to;
        this.strand = strand;
        this.fpkmTableHeaders = fpkmTableHeaders;
        this.fpkmSettings = fpkmSettings;
    }

//    public Gene(String geneId, ArrayList<String> record, ArrayList<Double> tissuesFPKMs, ArrayList<String> 
//        String[] fpkmTableHeaders, ArrayList<String> fpkmSettings) {
//        this.geneId = geneId;
////        this.contig = new Contigrecord.get(0);
////        this.annotationMips = annotationMips;
////        this.annotationRice = annotationRice;
//        this.tissuesFPKMs = tissuesFPKMs;
//        this.fpkmTableHeaders = fpkmTableHeaders;
//        this.fpkmSettings = fpkmSettings;
//    }
    public boolean isPlaceHolder() {
        if (geneId.equals(contig.getContigId())) {
            return true;
        }
        return false;
    }

    public String getGeneId() {
        return geneId;
    }

    public String getGeneIdHTML(String fontColour) {
        StringBuilder html = new StringBuilder();
        if (getGeneId().startsWith("Traes")) {
            html.append("<a href=\"http://plants.ensembl.org/Triticum_aestivum/Gene/Summary?db=core;g=");
            html.append(getGeneId());
            html.append("\" target=\"_blank\" style=\"color: ").append(fontColour).append("\">");
            html.append(getGeneId()).append("</a> ");
            return html.toString();
        } else {
            return getGeneId();
        }

    }

    public Contig getContig() {
        return contig;
    }

    public Annotation getAnnotationMips() {
        return annotationMips;
    }

    public Annotation getAnnotationRice() {
        return annotationRice;
    }

    public ArrayList<Double> getTissuesFPKMs() {
        return tissuesFPKMs;
    }

    public BarChartModel getBarChartModel(int i) {
        return getBarChartModels().get(i);
    }
    
    

    public ArrayList<BarChartModel> getBarChartModels() {
        ArrayList<BarChartModel> models = new ArrayList<>();
        int first = 2; //0,1 are start and stop postioins   
        Iterator<String> it = fpkmSettings.iterator();
        while (it.hasNext()) {
//            if(i<num) {
//            String numSamples = fpkmSettings.get(k);
//            first += Integer.parseInt(numSamples);
//            }

            String line = it.next();
            if (!line.startsWith("#")) {
                String[] split = line.split("\t");

                BarChartModel model = new BarChartModel();
                model.setTitle(getGeneId() + " in " + split[0]);                
                int last = first + Integer.parseInt(split[1]);
//        System.err.println("Get me the values: "+first+"-"+last);
                for (int i = first; i < last; i++) {
                    BarChartSeries series = new BarChartSeries();
                    for (int j = first; j < last; j++) {
                        if (i == j) {
                            String seriesName = fpkmTableHeaders[j + 1];
//                    if(seriesName.length() >= 10) {
//                        seriesName = seriesName.replaceFirst("_", "\n");
//                    }
                            if (tissuesFPKMs == null) {
                                System.err.println("tissuesFPKMs == null");
//                            } else {
//                                System.err.println("tissuesFPKMs.size() = "+tissuesFPKMs.size()+", j="+j);
                            }
                            series.set(seriesName, tissuesFPKMs.get(j)); //+1 as headers are geneid, start, stop and then sample ids
                        } else {
                            series.set(fpkmTableHeaders[j + 1], 0);
                        }
//                series.setLabel(fpkmTableHeaders[j + 1]);
                    }
                    model.addSeries(series);
                }
                model.setStacked(true);
                model.setZoom(true);
                //            barModel.setAnimate(true);
                Axis xAxis = model.getAxis(AxisType.X);
                //            xAxis.setLabel("Tissue/stage");
                xAxis.setTickAngle(-40);
                xAxis.setMin(-0.5);
                Axis yAxis = model.getAxis(AxisType.Y);
                yAxis.setLabel("FPKM");
                models.add(model);
                first = last;
            }
        }
        return models;
    }

//    public TabView getChartsTabView() {
//        if(chartsTabView == null ){
//            chartsTabView = new TabView();
//            ArrayList<BarChartModel> barChartModels = getBarChartModels();
//            for (int i = 0; i < barChartModels.size(); i++) {
//                BarChartModel barChartModel = barChartModels.get(i);
//                ChartModelWithId modelWithId = new ChartModelWithId(barChartModel, "chart_" + i + "_" + getGeneId());
//                Tab tab = new Tab();
//                Chart chart = new Chart();
//                chart.setModel(barChartModel);
//                chart.setWidgetVar("chart_"+i+"_"+geneId);
//                tab.getChildren().add(chart);
//                chartsTabView.getChildren().add(tab);
//            }
//        }
//        return chartsTabView;
//    }
    
    public ArrayList<ChartModelWithId> getChartModels() {
//        ArrayList<ModelWithId> chartModels = new ArrayList<>();
        if (chartModels == null) {
            chartModels = new ArrayList<>();
            ArrayList<BarChartModel> barChartModels = getBarChartModels();
            for (int i = 0; i < barChartModels.size(); i++) {
                BarChartModel barChartModel = barChartModels.get(i);
                ChartModelWithId modelWithId = new ChartModelWithId(barChartModel, "chart_" + i + "_" + getGeneId());
                chartModels.add(modelWithId);
            }
        }
        return chartModels;
    }
    public ArrayList<BarChartModel> getBarChartModel() {
        if (barChartModel == null) {
            barChartModel = new ArrayList<>();
            
            int first = 2; //0,1 are start and stop postioins
            for(String line: fpkmSettings) {
                BarChartModel model = new BarChartModel();
                model.setTitle(getGeneId());
                int last = first + Integer.parseInt(line);            
                for (int i = first; i < last; i++) { 
                    BarChartSeries series = new BarChartSeries();
                    for (int j = 2; j < last; j++) {
                        if (i == j) {
                            series.set(fpkmTableHeaders[j + 1], tissuesFPKMs.get(j)); //+1 as headers are geneid, start, stop and then sample ids
                        } else {
                            series.set(fpkmTableHeaders[j + 1], 0);
                        }
                        series.setLabel(fpkmTableHeaders[i + 1]);
                    }
                    model.addSeries(series);
                }
                model.setStacked(true);
                model.setZoom(true);
    //            barModel.setAnimate(true);
                Axis xAxis = model.getAxis(AxisType.X);
    //            xAxis.setLabel("Tissue/stage");
                xAxis.setTickAngle(-60);
                xAxis.setMin(-0.5);
                Axis yAxis = model.getAxis(AxisType.Y);
                yAxis.setLabel("FPKM");
                first = last;
                barChartModel.add(model);
            }
        }
        return barChartModel;
}
    public String[] getFpkmTableHeaders() {
        return fpkmTableHeaders;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    public String getStrand() {
        return strand;
    }

    public boolean isContainedInList(List<Gene> list) {
        for (Gene gene : list) {
            if (gene.getGeneId().equalsIgnoreCase(this.geneId)) {
                return true;
            }
        }
        return false;
    }

    public void onTabChange(TabChangeEvent event) {
        TabView tabView = (TabView) event.getComponent();
        currentTabIndex = tabView.getActiveIndex();
    }

    public void exportChart(Gene gene) {
        RequestContext.getCurrentInstance().getCallbackParams().put("exportChart", "chart_" + currentTabIndex + "_" + gene.getGeneId());
//        RequestContext.getCurrentInstance().getCallbackParams().put("exportChart", );
//        System.err.println(RequestContext.getCurrentInstance().getCallbackParams().get("exportChart"));
    }

    @Override
    public int compareTo(Gene o) {
        return getGeneId().compareTo(o.getGeneId());
    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }
    
    

}
