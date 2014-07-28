/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import beans.MainPopsBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;

/**
 *
 * @author rad
 */
public class Gene implements Serializable { //, Comparable<Contig> {
    //wheat contig info

    private final String geneId;
    private final Contig contig;
    private final Annotation annotationMips;
    private final Annotation annotationRice;
    private final ArrayList<Double> tissuesFPKMs;

    //location on contig
    private final Integer from;
    private final Integer to;
    private final String strand;

    //for FPKM charts:
    private BarChartModel barChartModel;
    private final String[] fpkmTableHeaders;

    public Gene(String geneId, Contig contig, Annotation annotationMips, Annotation annotationRice, ArrayList<Double> tissuesFPKMs,
            String[] fpkmTableHeaders, String traes_to_CSS_entry) {
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
    }

    public String getGeneId() {
        if (isPlaceHolder()) {
            return "no gene prediction on " + geneId;
        }
        return geneId;
    }

    public boolean isPlaceHolder() {
        if (geneId.equals(contig.getContigId())) {
            return true;
        }
        return false;
    }

    public String getId() {
        return geneId;
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

    public BarChartModel getBarChartModel() {
        if (barChartModel == null) {
            barChartModel = new BarChartModel();
            for (int i = 2; i < tissuesFPKMs.size(); i++) { //0,1 are start and stop postioins
                BarChartSeries series = new BarChartSeries();
                for (int j = 2; j < tissuesFPKMs.size(); j++) {
                    if (i == j) {
                        series.set(fpkmTableHeaders[j + 1], tissuesFPKMs.get(j)); //+1 as headers are geneid, start, stop and then sample ids
                    } else {
                        series.set(fpkmTableHeaders[j + 1], 0);
                    }
                    series.setLabel(fpkmTableHeaders[i + 1]);
                }
                barChartModel.addSeries(series);
            }
            barChartModel.setStacked(true);
            barChartModel.setZoom(true);
//            barModel.setAnimate(true);
            Axis xAxis = barChartModel.getAxis(AxisType.X);
//            xAxis.setLabel("Tissue/stage");
            xAxis.setTickAngle(-60);
            xAxis.setMin(-0.5);
            Axis yAxis = barChartModel.getAxis(AxisType.Y);
            yAxis.setLabel("FPKM");
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
    
    

}
