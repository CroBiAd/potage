/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import beans.AppDataBean;
import beans.Location_cMFilter;
import beans.MainPopsBean;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import javax.faces.bean.ManagedProperty;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author rad
 */
public class PerLocationContigs implements Serializable {

    private final ArrayList<Contig> contigs;
//    private ArrayList<Contig> filteredContigs;
    private ArrayList<Contig> selectedContigs;
    private final String chromosome;
    private final Location_cMFilter cM_filterForNonGeneContigs;
    private StreamedContent exportFileWholeIWGSC;
    private String toExport = "page";

    private ArrayList<Double> cM_Selected;
    
    public PerLocationContigs(String chromosome, Location_cMFilter cM_filterForNonGeneContigs) {
        this.chromosome = chromosome;
        this.contigs = new ArrayList<>();
//        this.filteredContigs = null;

        this.cM_filterForNonGeneContigs = cM_filterForNonGeneContigs;
    }

    public PerLocationContigs(ArrayList<Contig> contigs, String chromosome, Location_cMFilter cM_filterForNonGeneContigs) {
        this.contigs = contigs;
        this.chromosome = chromosome;
        this.cM_filterForNonGeneContigs = cM_filterForNonGeneContigs;
    }
    
    
    @ManagedProperty(value = "#{appDataBean}")
    private AppDataBean appData;

    public AppDataBean getAppData() {
        return appData;
    }

    public void setAppData(AppDataBean appData) {
        this.appData = appData;
    }
    
    

//    public PerLocationContigs(String chromosome, double cM_corrected_min, double cM_corrected_max, double cM_original_min, double cM_original_max) {
//        this.chromosome = chromosome;
//        this.cM_corrected_min = cM_corrected_min;
//        this.cM_corrected_max = cM_corrected_max;
//        this.cM_original_min = cM_original_min;
//        this.cM_original_max = cM_original_max;
//        this.contigs = new ArrayList<>();
//        this.filteredContigs = new ArrayList<>();
//        this.cM_correctedRange = new ArrayList<>();
//        this.cM_originalRange = new ArrayList<>();
//        cM_filterForNonGeneContigs = new Location_cMFilter();
//    }
    public void addContig(Contig c) {
//        if (contigs.isEmpty()) {
//            getcM_correctedRange().add(c.getcM_corrected());
//            getcM_originalRange().add(c.getcM_original());
//        } else { //risky way of collecting all cM positions (assumes input is sorted)
//            Contig previous = contigs.get(contigs.size() - 1);
//            if (previous.getcM_corrected() != c.getcM_corrected().doubleValue()) {
//                getcM_correctedRange().add(c.getcM_corrected());
//            }
//            if (previous.getcM_original() != c.getcM_original().doubleValue()) {
//                getcM_originalRange().add(c.getcM_original());
//            }
//        }
        contigs.add(c);
    }

    public ArrayList<Contig> getContigs() {
        return contigs;
    }

    public Location_cMFilter getcM_filterForNonGeneContigs() {
        return cM_filterForNonGeneContigs;
    }

//    public ArrayList<Contig> getFilteredContigs() {
//        return filteredContigs;
//    }
//
//    public void setFilteredContigs(ArrayList<Contig> filteredContigs) {
//        this.filteredContigs = filteredContigs;
//    }

    public ArrayList<Double> rangePrefix(String prefix) {
        return cM_filterForNonGeneContigs.rangePrefix(prefix);
    }

//    public ArrayList<Double> originalRangePrefix(String prefix) {
//        return cM_filterForNonGeneContigs.originalRangePrefix(prefix);
//    }

    public ArrayList<Double> getcM_range() {
        return cM_filterForNonGeneContigs.getcM_Range();
    }
    
     public ArrayList<Double> getcM_Selected() {
        return cM_Selected;
    }

    public void setcM_Selected(ArrayList<Double> cM_Selected) {
        this.cM_Selected = cM_Selected;
    }
//    public ArrayList<Double> getcM_originalRange() {
//        return cM_filterForNonGeneContigs.getcM_originalRange();
//    }

    public String getChromosome() {
        return chromosome;
    }

    public void resetFilter() {
        cM_filterForNonGeneContigs.resetFilter();
//        setFilteredContigs(null);
    }

    public boolean isFiltered() {
        return !Objects.equals(cM_filterForNonGeneContigs.getcM_min(), cM_filterForNonGeneContigs.getcM_min_user()) || !Objects.equals(cM_filterForNonGeneContigs.getcM_max(), cM_filterForNonGeneContigs.getcM_max_user());
//        return filteredContigs != null;
    }

    public boolean filterByCm(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }         
        if(value == null) {
            return false;
        }
        String[] split = filterText.split("-");
        return ((Comparable) value).compareTo(Double.valueOf(split[0])) >= 0 && ((Comparable) value).compareTo(Double.valueOf(split[1])) <= 0;
//        return ((Comparable) value).compareTo(Double.valueOf(filterText)) >= 0;
    }
        
//    public void filterByCm() {
//        filteredContigs = new ArrayList<>();
//        if (contigs != null) {
//            System.err.println(contigs.size()+" contigs ");
//            for (Contig contig : contigs) {
////                double cM_corrected = contig.getcM_corrected();
////                double cM_original = contig.getcM_original();
//                if (cM_filterForNonGeneContigs.isWithinUserCoordinates(contig.getcM())) {
//                    filteredContigs.add(contig);
//                }
//            }
//            System.err.println(filteredContigs.size()+" filtered contigs ");
//
//            //reset selection to prevent erratic behaviour (e.g. first elem in the table remains selected even though it is a different elem due to cM restriction
//            setSelectedContigs(null);
//        }
////        RequestContext.getCurrentInstance().update(":formSearch3:contigList");
//    }

    public ArrayList<Contig> getSelectedContigs() {
        return selectedContigs;
    }

    public void setSelectedContigs(ArrayList<Contig> selectedContigs) {
        this.selectedContigs = selectedContigs;
    }

    public void clearSelectedContigs() {
        setSelectedContigs(null);
    }

    public boolean somethingSelected() {
        return selectedContigs != null && !selectedContigs.isEmpty();
    }

    private void exportWholeIWGSCFile() {
        if (selectedContigs == null || selectedContigs.isEmpty()) {
        } else {
            String newline = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            for (Contig c : selectedContigs) {
//                System.err.println("Selected: " + c.getId() + " " + c.getId());
                sb.append(">");
                sb.append(c.getContigId());
//                sb.append(" ").append(p.getHit().getHitDef());
//                sb.append(" predicted POPSEQ location ");
//                sb.append(barleyChromosome);
//                sb.append("H:");
//                sb.append(c.getFrom());
//                sb.append("-").append(c.getTo());
//                sb.append(" ").append(c.getFrame());
                sb.append(newline);
                sb.append(reusable.BlastOps.getCompleteSubjectSequence(c.getContigId(), appData.getBLAST_DB()).get(0).getSequenceString());
                sb.append(newline);
            }
            InputStream stream = new ByteArrayInputStream(sb.toString().getBytes());
            this.exportFileWholeIWGSC = new DefaultStreamedContent(stream, "application/txt", "Selected_IWGSC_contigs_" + reusable.CommonMaths.getRandomString() + ".fasta");

//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Saving...",  selectedContigs.size() + " selected contig(s)"));
//            RequestContext requestContext = RequestContext.getCurrentInstance();
//            requestContext.update("form:dataTable:saveFastaWhole");
//            context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));
//            System.err.println(sb.toString());
        }
    }

    public StreamedContent getExportFileWholeIWGSC() {
        exportWholeIWGSCFile();
        return exportFileWholeIWGSC;
    }

    public Integer getIndexOfContig(String contigId) {
        Integer idx = null;
        for (int i = 0; i < contigs.size(); i++) {
            String id = contigs.get(i).getId();
            if (id.equalsIgnoreCase(contigId)) {
                idx = i;
                break;
            }
        }
        return idx;
    }
    
    public ArrayList<Integer> getIndicesOfContigs(ArrayList<String> contigIds) {
        ArrayList<Integer> indices = new ArrayList<>(contigIds.size());
        for (int i = 0; i < contigs.size(); i++) {
            String id = contigs.get(i).getId();
            for (String contigId : contigIds) {
                if (id.equalsIgnoreCase(contigId)) {
                    indices.add(i);
                    break;
                }
            }
        }
        return indices;
    }

    public String getToExport() {
        return toExport;
    }

    public void setToExport(String toExport) {
        this.toExport = toExport;
    }

    public boolean exportPageOnly() {
        if (toExport.equals("page")) {
            return true;
        }
        return false;
    }

    public boolean exportSelectedOnly() {
        if (toExport.equals("selected")) {
            return true;
        }
        return false;
    }

    public boolean exportDsiabled() {
        if (isDataLoaded() && (!exportSelectedOnly() || (exportSelectedOnly() && somethingSelected()))) {
            return false;
        }
        return true;
    }
    
    public boolean isDataLoaded() {
        return contigs != null && !contigs.isEmpty();
    }
}
