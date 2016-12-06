/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.component.chart.Chart;
import org.primefaces.component.datagrid.DataGrid;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.spacer.Spacer;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.menu.DefaultMenuModel;
import poplogic.Contig;
import poplogic.Gene;
import poplogic.PerLocationContigs;
import reusable.Hit;
import reusable.HitDataModel;
import reusable.HitsForQuery;
import reusable.HitsForQueryDataModel;
import reusable.InReader;
import reusable.QesHits;
import reusable.Sequence;
//import org.primefaces.model.DefaultMenuModel;
//import org.primefaces.model.chart.DonutChartModel;

/**
 *
 * @author rad
 */
@ManagedBean(name = "mainBean")
@ViewScoped
public class MainPopsBean implements Serializable {

    //MapDB & keys //Keys are used to retrieve a given datastructure from the DB
//    private final String MAIN_MAP_KEY = "mainMap";
//    private final String CONTIG_2_GENES_MAP_KEY = "contig2Genes";
//    private final String EXPRESSION_MAP_KEY = "expressionMap";
//    private final String EXPRESSION_HEADER_KEY = "expressionHeader";
//    private final String SETTINGS_MAP_NAME = "settingsMap";
//     @ManagedProperty(value="#{mapDbFrontBean}")
//    private MapDbFrontBean mapDbFrontBean;
//    public final static String BLAST_DB_FOR_FETCHING = "//resources//pops_all_rad.nal";
//    public final static String BLAST_DB = "//resources//pops_all_rad.nal";
//    public final static String BLAST_DB = "/var/tomcat/persist/potage_data/blast_db/POPSeq_all_blastdb";
    public final static String BLAST_DB = "/var/tomcat/persist/potage_data/blast_db/IWGSC_SS";
    private final String PATH = "/var/tomcat/persist/potage_data";
//    private final String ANNOTATION_RICE = "/var/tomcat/persist/potage_data/HCS_2013_annotations_rice.txt";
//    private final String ANNOTATION = "/var/tomcat/persist/potage_data/ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header_no_brackets.txt"; //tr -d '()' < ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header.txt > ta_IWGSC_MIPSv2.0_HCS_HUMAN_READABLE_DESCS_2013Nov28_no_header_no_brackets.txt
//    private final String TRAES_CSS_MAP = "/var/tomcat/persist/potage_data/Traes_to_CSS.map";
//    private final String FPKMS = "/var/tomcat/persist/potage_data/FPKMs/reordered/popseqed_genes_on_with_header2016.fpkms";
//    private final String FPKMS_UNORDERED_GENES = "/var/tomcat/persist/potage_data/FPKMs/reordered/unordered_genes_with_header2016.fpkms";
//    private final String FPKM_SETTINGS = "/var/tomcat/persist/potage_data/FPKMs/reordered/fpkm_data_settings2016.txt";
    public final String TABLE_HEADERS = "Gene ID,From,To,Strand,Contig ID,cM,MIPS annotation Hit ID,MIPS annotation Description,MIPS annotation Interpro ID,Rice annotation Hit ID,Rice annotation Description";

    private boolean autoDisplayCharts = true;

    private String currentChromosome;
//    private LazyGeneDataModel selectedDataModel;
//    private LazyDataModel<Gene> loadedDataModel;
//    private LazyGeneDataModel filteredDataModel;
    private ArrayList<Gene> loadedGenes;
    private ArrayList<Gene> selectedGenes;
//    private ArrayList<Gene> filteredGenes;
    private ArrayList<Gene> selectedGenesForChartDisplay;
    HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap;
    private Location_cMFilter cM_filter;

    private String userQuery;
    private String userQuerySeq;
    private String globalFilter;

    //chart-dialog related
    private Gene geneSelectedForDialogDisplay;
    private String[] fpkmTableHeaders;
    private final static String DIALOG_CONTAINERS_PARENT = "formCentre";
    private final static int DIALOGS_MAX_NUMBER = 15;
    private final static int DIALOG_WIDTH = 480; //450;
    private final static int DIALOG_HEIGHT = 280; //250;
//    protected HashMap<String, Dialog> dialogIdToDialogMap = new HashMap<>();
    protected HashMap<String, Dialog> geneIdToDialogMap = new HashMap<>();
    protected ArrayList<UIComponent> availableDialogContainers = new ArrayList<>();

    //menu model for selecting chromosome to display 
    private DefaultMenuModel menuModel; //multi level e.g. chromosome 1 -> submenu 1{A,B,D}
    private DefaultMenuModel menuModelSimple; //Single level menu e.g. 1A,1B,1D
//    private DefaultMenuModel menuModelForOtherContigs; //multi level chromosome 1 -> submenu 1{A,B,D} -> range

    private static final int BUFFER_SIZE = 6124;
    private String fileContentString;
    private ArrayList<Sequence> sequences;

    private StreamedContent exportFileWholeIWGSC;
    private String toExport = "page";

    //display of non-gene contigs
    private PerLocationContigs perLocationContigs;
    private String chromosomeForNonGeneContigs;

    //display unordered genes
    private boolean appendUnordered;

    @ManagedProperty(value = "#{appDataBean}")
    private AppDataBean appData;

    public MainPopsBean() {
        perLocationContigs = new PerLocationContigs(null, new Location_cMFilter());
        cM_filter = new Location_cMFilter();
    }

    public void setAppData(AppDataBean appData) {
        this.appData = appData;
    }

    @PostConstruct
    public void init() {
//        if (!FacesContext.getCurrentInstance().isPostback()) {
//            RequestContext.getCurrentInstance().execute("alert('This onload script is added from backing bean.')");
//        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Map<String, String> parameterMap = (Map<String, String>) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (!parameterMap.isEmpty()) {
            if (parameterMap.containsKey("query")) {
                String id = parameterMap.get("query").trim().replaceFirst("\\.\\d+$", "");
                setUserQuery(id);
                searchAll(id, ":formSearch:searchMessages");
            }
            RequestContext.getCurrentInstance().update(":formSearch:idInput,:formSearch:searchMessages,:formCentre:dataTable,:formCentre:chartsGrid,:formSearch3:contigList");
        }

//        System.err.println("AppData size="+appData.getContigs("1A").size());
    }

    public PerLocationContigs getPerLocationContigs() {
        return perLocationContigs;
    }

    public String getChromosomeForNonGeneContigs() {
        return chromosomeForNonGeneContigs;
    }

    public void setChromosomeForNonGeneContigs(String chromosomeForNonGeneContigs) {
        this.chromosomeForNonGeneContigs = chromosomeForNonGeneContigs.toUpperCase();
    }

    public void handleFileUpload(FileUploadEvent event) {
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
//        System.out.println(event.getFile().getFileName()+" in "+System.getProperty("java.io.tmpdir"));
        
//        File result = new File(extContext.getRealPath("//WEB-INF//uploaded//" + event.getFile().getFileName()));
//        System.out.println(extContext.getRealPath("//WEB-INF//uploaded//" + event.getFile().getFileName()));
        try {
            File tempFile = File.createTempFile(event.getFile().getFileName(), ".fa");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int contentsI;
            InputStream inputStream = event.getFile().getInputstream();
            while (true) {
                contentsI = inputStream.read(buffer);
                if (contentsI < 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, contentsI);
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            inputStream.close();

            String fasta = InReader.readInputToString(tempFile.toString());
          
            if (setFileContentStringValidate(fasta)) {
                growl(FacesMessage.SEVERITY_INFO, "File:", event.getFile().getFileName() + " successfully uploaded.", "searchMessages2");
                growl(FacesMessage.SEVERITY_INFO, "Size:", reusable.CommonMaths.round((double) event.getFile().getSize() / 1024, 2) + " Kb", "searchMessages2");
                growl(FacesMessage.SEVERITY_INFO, "Number of sequences:", "" + sequences.size(), "searchMessages2");
            }
        } catch (IOException e) {
            e.printStackTrace();
            growl(FacesMessage.SEVERITY_FATAL, "The file was not uploaded!", "", "searchMessages2");
        } finally {
//            result.delete();
        }

    }

    public boolean setFileContentStringValidate(String fileContentString) {
        if (isValidFasta(fileContentString)) {
            this.fileContentString = fileContentString;
            if ((fileContentString == null || fileContentString.isEmpty()) && getGlobalFilter().equals(fileContentString)) {
                setGlobalFilter("");
            }
            return true;
        } else {
            this.fileContentString = null;
            if ((fileContentString == null || fileContentString.isEmpty()) && getGlobalFilter().equals(fileContentString)) {
                setGlobalFilter("");
            }
            growl(FacesMessage.SEVERITY_ERROR, "Error!", "Not a valid FASTA input!", "searchMessages2");
            return false;
        }

    }

    public void clearFileContentString() {
        this.fileContentString = null;
        growl(FacesMessage.SEVERITY_INFO, "Cleared!", "", "searchMessages2");
        setSeqSearchTabActive("0");
    }

    public void setFileContentString(String fileContentString) {
        setFileContentStringValidate(fileContentString);
    }

    public String getFileContentString() {
        return fileContentString;
    }

    private boolean isValidFasta(String fileContents) {
        boolean valid = false;
        try {
            sequences = reusable.FastaOps.sequencesFromFastaString(fileContents, true);
            if (sequences != null && !sequences.isEmpty()) {
                for (Sequence s : sequences) {
                    if (s.getIdentifierString().length() == 0) {
                        return false;
                    }
                    if (s.getLength() == 0) {
                        return false;
                    } else if (s.containsNonIUPAC()) {
                        return false;
                    }
                }
                valid = true;
            }
        } catch (Exception exception) {
        }
        return valid;
    }

    public String getCurrentChromosome() {
        if (currentChromosome == null) {
            return "";
        }
        return "Displaying POPSEQ-anchored genes/contigs on chromosome " + currentChromosome;
    }

    public void setCurrentChromosome(String currentChromosome) {
        this.currentChromosome = currentChromosome;
    }

    public void searchAll(ActionEvent actionEvent) {
        searchAll(userQuery, ":formSearch:searchMessages");
    }

    private void searchAll(String userQuery, String messageComponent) {
        appendUnordered = true;
        RequestContext.getCurrentInstance().getCallbackParams().put("showContigList", false);

        if (userQuery == null || userQuery.isEmpty()) {
            growl(FacesMessage.SEVERITY_FATAL, "Searching for nothing?!", "Consider inputting an identifier before clicking 'Search'", messageComponent);
        } else {
//
            String[] queries = userQuery.split(" |,|\n|\t|;");
//
            if (queries.length < 2) {
                SearchResult result = appData.quickFind(userQuery.trim());
                if (result == null || (result.getGene() == null && result.getContig() == null)) {
                    growl(FacesMessage.SEVERITY_FATAL, "Search failed.", "Query not found among POPSeq ordered and/or gene containing contigs", messageComponent);
                } else if (!result.getContig().hasGenes()) {
                    chromosomeForNonGeneContigs = result.getChromosome();
                    loadAllContigs();
                    growl(FacesMessage.SEVERITY_INFO, "\"Query found", userQuery + " found on chromosome " + chromosomeForNonGeneContigs + ", unfortunatelly no annotation or expression data is available for this contig.", messageComponent);
                    RequestContext.getCurrentInstance().getCallbackParams().put("showContigList", true);
                    final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":formSearch3:contigList");
                    Integer rowIndex = perLocationContigs.getIndexOfContig(userQuery);

                    //if setFirst is called with an index other than the first row of a page it obscures some of the preceeding rows
                    int rows = d.getRows();
                    int page = rowIndex / rows;
                    d.setFirst(rows * page);
                } else {
                    onSelect(result.getChromosome());
                    final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":formCentre:dataTable");
                    Integer rowIndex = result.getIndex();

                    //if setFirst is called with an index other than the first row of a page it obscures some of the preceeding rows
                    int rows = d.getRows();
                    int page = rowIndex / rows;
                    d.setFirst(rows * page);

                    growl(FacesMessage.SEVERITY_INFO, "Query found: ", userQuery + " found on chromosome " + currentChromosome, messageComponent);
                    RequestContext.getCurrentInstance().update(":formCentre:dataTable");
                }
            } else {
                growl(FacesMessage.SEVERITY_WARN, "Unfortunatelly", "Not able to process multiple queries", messageComponent);
                RequestContext.getCurrentInstance().update(":formCentre:dataTable");
            }
        }
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        if ((userQuery == null || userQuery.isEmpty()) && getGlobalFilter().equals(userQuery)) {
            setGlobalFilter("");
        }
        this.userQuery = userQuery;
    }

    public String getGlobalFilter() {
        if (globalFilter != null) {
            return globalFilter;
        } else {
            return "";
        }
    }

    public void setGlobalFilter(String globalFilter) {
        this.globalFilter = globalFilter;
    }

    public Gene getGeneSelectedForDialogDisplay() {
        return geneSelectedForDialogDisplay;
    }

    public void setGeneSelectedForDialogDisplay(Gene geneSelectedForDialogDisplay) {
        this.geneSelectedForDialogDisplay = geneSelectedForDialogDisplay;
//        currentContainerId = generateChartDialog();
        generateChartDialog();
    }
    private String currentContainerId;

    public String getCurrentContainerId() {
        return currentContainerId;
    }

    public void generateDialogContainers() {
        if (availableDialogContainers == null || availableDialogContainers.isEmpty()) {
            ComponentGenerator componentGenerator = new ComponentGenerator();
            availableDialogContainers = componentGenerator.generateDialogContainers(0, DIALOGS_MAX_NUMBER, DIALOG_CONTAINERS_PARENT);
        }

    }

    public void growl(FacesMessage.Severity severity, String header, String content, String messageOrGrowlComponent) {
        if (severity == null) {
            severity = FacesMessage.SEVERITY_INFO;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(messageOrGrowlComponent, new FacesMessage(severity, header, content));
        RequestContext.getCurrentInstance().update(messageOrGrowlComponent);
    }

    private void generateChartDialog() {
        Dialog dlg = geneIdToDialogMap.get(geneSelectedForDialogDisplay.getGeneId());
        if (dlg != null) {
            growl(FacesMessage.SEVERITY_WARN, "Note!", "Expression chart already opened for " + geneSelectedForDialogDisplay.getGeneId(), "growl");
            //for now re-draw
            RequestContext.getCurrentInstance().update(dlg.getParent().getId());
//            return null;
        } else if (availableDialogContainers.isEmpty()) {
            growl(FacesMessage.SEVERITY_WARN, "Getting crowded", "You have reached the maximum number of " + DIALOGS_MAX_NUMBER + " charts displayed simultaneously. Close some before openning more.", "growl");
//            return null;
        } else {
            UIComponent container = availableDialogContainers.remove(availableDialogContainers.size() - 1);
            String currentDisplay = DIALOG_CONTAINERS_PARENT + ":" + container.getId();

            ComponentGenerator componentGenerator = new ComponentGenerator();
            Dialog dialog = componentGenerator.generateDialog(geneSelectedForDialogDisplay, geneIdToDialogMap, DIALOG_WIDTH, DIALOG_HEIGHT, availableDialogContainers);
            container.getChildren().add(dialog);
//            containerId = container.getClientId().split("_")[1];
            String suffix = container.getClientId().split("_")[1]; //for no good reason using the same suffix for component identifiers

            ArrayList<BarChartModel> models = geneSelectedForDialogDisplay.getBarChartModels();
            for (int i = 0; i < models.size(); i++) {
                BarChartModel barChartModel = models.get(i);
                Chart chart = componentGenerator.generateChart(suffix + i, DIALOG_WIDTH, DIALOG_HEIGHT, barChartModel);
                dialog.getChildren().add(chart);
                Spacer s = new Spacer();
                s.setId(chart.getId() + "_spacer");
                dialog.getChildren().add(s);
            }

            RequestContext.getCurrentInstance().update(currentDisplay);
        }
    }

    public DefaultMenuModel getMenuModelSimple() {
        if (menuModelSimple != null) {
            return menuModelSimple;
        }

        menuModelSimple = new ComponentGenerator().generateDynamicMenuSingleLevel();
        return menuModelSimple;
    }

    public DefaultMenuModel getMenuModel() {
        if (menuModel != null) {
            return menuModel;
        }
        menuModel = new ComponentGenerator().generateDynamicMenuMultilevel();
        return menuModel;
    }

    public void updateDisplayedContigs() {
        onSelect(currentChromosome);
    }

//    
    public void onSelect(String chromosome) {
//        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();//        String path = extContext.getRealPath(PATH);
        if (chromosome != null) {
            currentChromosome = chromosome;
            fpkmTableHeaders = appData.getFpkmTableHeaders();
            if (appendUnordered) {
                loadedGenes = appData.getGenesAll(chromosome);
//                System.out.println("Loading all " + loadedGenes.size() + " genes");
            } else {
                loadedGenes = appData.getGenesBinned(chromosome);
//                System.out.println("Loading binned " + loadedGenes.size() + " genes");
            }
            genesTissuesFPKMsMap = appData.getGenesToExpressionMap(); //SUPERFLOUS?
            cM_filter = appData.getLocationFilterGenes(chromosome);
            selectedGenes = null;

//            System.out.println(loadedGenes.size() + " genes loaded");
        } else {
            loadedGenes = null;
        }
        final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":formCentre:dataTable");
        d.reset();
    }

    public void reload() {
        if (currentChromosome != null) {
            onSelect(currentChromosome);
        }
    }

    public void loadAllContigs() {
//        String fileName = getInputFilename(chromosomeForNonGeneContigs, false);
//        InputProcessor inputProcessor = new InputProcessor();
//        perLocationContigs = inputProcessor.getContigs(fileName);

        perLocationContigs = new PerLocationContigs(appData.getContigs(chromosomeForNonGeneContigs), chromosomeForNonGeneContigs, appData.getLocationFilterContigs(chromosomeForNonGeneContigs));
    }

    public void restrictContigsWithutGenes() {

    }

    public ArrayList<Gene> getSelectedGenes() {
        return selectedGenes;
    }

    public void setSelectedGenes(ArrayList<Gene> selectedGenes) {
        this.selectedGenes = selectedGenes;
    }

    public void clearSelectedGenes() {
        setSelectedGenes(null);
    }

    public void addSelectedGenesToDisplay() {
        for (Gene gene : selectedGenes) {
            addGeneToDisplay(gene);
        }
    }

    public void addGeneToDisplay(Gene gene) {
        if (selectedGenes != null) {
            if (selectedGenesForChartDisplay == null) {
                selectedGenesForChartDisplay = new ArrayList<>(selectedGenes.size());
            }
            if (!gene.isContainedInList(selectedGenesForChartDisplay)) {
                selectedGenesForChartDisplay.add(gene);
            } else {
                growl(FacesMessage.SEVERITY_WARN, "Chart already displayed for ", gene.getGeneId(), "growl");
            }
        }
    }

    public void removeFromChartDisplay(Gene gene) {
        if (selectedGenesForChartDisplay != null) {
            selectedGenesForChartDisplay.remove(gene);
            if (selectedGenesForChartDisplay.isEmpty()) {
                selectedGenesForChartDisplay = null;
            }
        }
    }

    public ArrayList<Gene> getSelectedGenesForChartDisplay() {
        return selectedGenesForChartDisplay;
    }

    public void clearSelectedGenesForChartDisplay() {
        setSelectedGenesForChartDisplay(null);
    }

    public void setSelectedGenesForChartDisplay(ArrayList<Gene> selectedGenesForChartDisplay) {
        this.selectedGenesForChartDisplay = selectedGenesForChartDisplay;
    }

    public boolean hasNoGenesSelectedForDisplay() {
        return selectedGenesForChartDisplay == null || selectedGenesForChartDisplay.isEmpty();
    }

    public void resetFilter() {
//        selectedDataModel = loadedDataModel;
        cM_filter.resetFilter();
//        setFilteredGenes(null);
        setSelectedGenes(null);
    }

//    public boolean isFiltered() {
//        return filteredGenes != null;
//    }
    public boolean filterIgnoreCaseContains(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (filterText == null || filterText.isEmpty()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return value.toString().toLowerCase().trim().contains(filterText);
    }

    public boolean isDataLoaded() {
//        if (selectedDataModel != null) {
        if (loadedGenes != null) {
            return true;
        }
        return false;
    }

    public Location_cMFilter getcM_filter() {
        return cM_filter;
    }

    public void setcM_filter(Location_cMFilter cM_filter) {
        this.cM_filter = cM_filter;
    }

    public boolean somethingSelected() {
        if (selectedGenes != null && !selectedGenes.isEmpty()) {
            return true;
        }
        return false;
    }

    public void exportWholeIWGSCFile() {
        if (selectedGenes == null || selectedGenes.isEmpty()) {
//            exportFile = null;
            System.err.println("Nothing selected on chromosome " + currentChromosome);
        } else {
//            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
            String newline = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            for (Gene c : selectedGenes) {
//                System.err.println("Selected: " + c.getGeneId() + " " + c.getContig().getId());
                sb.append(">");
                sb.append(c.getContig().getId());
//                sb.append(" ").append(p.getHit().getHitDef());
//                sb.append(" predicted POPSEQ location ");
//                sb.append(barleyChromosome);
//                sb.append("H:");
//                sb.append(c.getFrom());
//                sb.append("-").append(c.getTo());
//                sb.append(" ").append(c.getFrame());
                sb.append(newline);
//                sb.append(reusable.BlastOps.getCompleteSubjectSequence(c.getContig().getContigId(), "/var/tomcat/persist/coching_data/IWGSC_SS").get(0).getSequenceString());
//                sb.append(reusable.BlastOps.getCompleteSubjectSequence(c.getContig().getId(), extContext.getRealPath(BLAST_DB)).
                sb.append(reusable.BlastOps.getCompleteSubjectSequence(c.getContig().getId(), BLAST_DB).
                        get(0).getSequenceString()
                );
//                sb.append(reusable.BlastOps.getCompleteSubjectSequence(c.getContig().getId(), BLAST_DB_FOR_FETCHING).get(0).getSequenceString());
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

    public void setExportFileWholeIWGSC(StreamedContent exportFileWholeIWGSC) {
        this.exportFileWholeIWGSC = exportFileWholeIWGSC;
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

    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
//            Cell firstCell = cellIterator.next();
//            String geneId = firstCell.getStringCellValue();
//            cellIterator = row.cellIterator();
//            System.err.println("id: "+geneId);
            String geneId = null;
//            int j=0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String stringCellValue = cell.getStringCellValue();
                if (stringCellValue != null && !stringCellValue.isEmpty()) {
                    cell.setCellValue(stringCellValue.replaceAll("\\<[^>]*>", "")); //strip off HTML
                }
                if (geneId == null || geneId.isEmpty()) {
                    geneId = cell.getStringCellValue().trim();
                }
//                System.err.println("["+(j++)+"]"+cell.getStringCellValue());
            }
            ArrayList<Double> fpkms = genesTissuesFPKMsMap.get(geneId);
            if (fpkms != null && !fpkms.isEmpty()) {
                for (int i = 2; i < fpkms.size(); i++) {
                    Double fpkmDouble = fpkms.get(i);
                    Cell createdCell = row.createCell(row.getLastCellNum());
                    createdCell.setCellValue(fpkmDouble);
                }
            }
//            cellIterator = row.cellIterator();
//            String geneId = cellIterator.next().getStringCellValue();
//            Gene gene = selectedDataModel.getRowData(geneId);
//            ArrayList<Double> tissuesFPKMs = gene.getTissuesFPKMs();
//            for (Double double1 : tissuesFPKMs) {
////                row.i
//            }
        }

        Row row = sheet.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();
        String headers[] = TABLE_HEADERS.split(",");
        int p = 0;
        for (String h : headers) {
//            System.err.println("Adding "+h+" at "+(p++));
            cellIterator.next().setCellValue(h);

        }

        //Add headers for FPKM values
        for (int i = 3; i < fpkmTableHeaders.length; i++) {
            Cell createdCell = row.createCell(row.getLastCellNum());
            createdCell.setCellValue(fpkmTableHeaders[i]);
        }
    }

    public boolean contains(String queries, String elem) {
        String arr[] = null;
        if (queries != null) {
            arr = queries.split("[^_|\\w]"); //split on anything that is not a alphanumeric or an underscore
        }
        for (String string : arr) {
            if (string.equals(elem)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String queries, String elem, String elem1) {
        String arr[] = null;
        if (queries != null) {
            arr = queries.split("[^_|\\w]"); //split on anything that is not a alphanumeric or an underscore
        }
        for (String string : arr) {
            if (string.equals(elem) || string.equals(elem1)) {
                return true;
            }
        }
        return false;
    }

    //BELOW, BLAST-based ALIGNMEN_SEARCH,CODE RECYCLED FROM BWPF by Rad Suchecki
    private ArrayList<HitsForQuery> perQueryResults;
    private HitsForQueryDataModel hitsForQueryDataModel;
    private HitDataModel hitsDataModel;
    private HitsForQuery selectedQuery;
    private List<Hit> selectedHits;
    private Hit selectedHit;
    private String seqSearchTabActive = "0";

    public String getSeqSearchTabActive() {
        return seqSearchTabActive;
    }

    public void setSeqSearchTabActive(String seqSearchTabActive) {
        this.seqSearchTabActive = seqSearchTabActive;
    }

    public HitDataModel getHitsDataModel() {
        return hitsDataModel;
    }

    public boolean hasHitsDataModel() {
        return hitsDataModel != null && hitsDataModel.getRowCount() != 0;
    }

    public List<Hit> getSelectedHits() {
        return selectedHits;
    }

    public Hit getSelectedHit() {
        return selectedHit;
    }

    public void setSelectedHit(Hit selectedHit) {
        if (selectedHit != null) {
            this.selectedHit = selectedHit;
            setUserQuery(selectedHit.getHitId());
            searchAll(selectedHit.getHitId(), ":formSearch2:searchMessages2");
        }
    }

    public ArrayList<HitsForQuery> getPerQueryResults() {
        return perQueryResults;
    }

    public HitsForQueryDataModel getHitsForQueryDataModel() {
        return hitsForQueryDataModel;
    }

    public boolean hasHitsForQueryDataModel() {
        return hitsForQueryDataModel != null && hitsForQueryDataModel.getRowCount() != 0;
    }

    public HitsForQuery getSelectedQuery() {
        if (selectedQuery != null) {
            selectedHits = selectedQuery.getPromotersList(0);
        }
        return selectedQuery;
    }

    public void setSelectedQuery(HitsForQuery selectedQuery) {
        this.selectedQuery = selectedQuery;
        if (selectedQuery != null) {
            hitsDataModel = new HitDataModel(selectedQuery.getHits());
            setSeqSearchTabActive("2");
        }
    }

    public void sequenceSearchEventHandler(ActionEvent actionEvent) {
        Date submitTime = new Date();
        if (sequences != null && !sequences.isEmpty()) {

            perQueryResults = new ArrayList<>();
            QesHits finder = new QesHits();
            int totalRetrieved = 0;
            try {
//                ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
//                String blastdbIWGSC = extContext.getRealPath(BLAST_DB);
                String blastdbIWGSC = BLAST_DB;
                perQueryResults = finder.findHits(sequences, blastdbIWGSC);      //<------------------------------------------------------------            

                hitsForQueryDataModel = new HitsForQueryDataModel(perQueryResults);

                for (HitsForQuery qp : perQueryResults) {
                    for (Hit p : qp.getHits()) {
                        totalRetrieved++;
                    }
                }
                //result available for one query only so goto the last tab and display
                if (perQueryResults.size() == 1) {
                    setSelectedQuery(hitsForQueryDataModel.getRow(0)); //calls setSeqSearchTabActive("2");
                } else if (perQueryResults.size() > 1) {
                    setSeqSearchTabActive("1");
                } else {
                    setSeqSearchTabActive("0");
                }

            } catch (Exception e) {
                StringBuilder s = new StringBuilder();
                s.append(generateEmailContent(submitTime));
                ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletRequest request = (HttpServletRequest) extContext.getRequest();
                s.append("\nUSER: ").append(request.getRemoteAddr()).append("\n");
                for (StackTraceElement ste : e.getStackTrace()) {
                    s.append("\n").append(ste.toString());
                }
                reusable.ExecProcessor.email(s.toString(), "POTAGE FATAL exception!", "radoslaw.suchecki@adelaide.edu.au", "no-reply@hathor.acpfg.local");
                growl(FacesMessage.SEVERITY_FATAL, "Fatal error!", "Alignment failed!", ":formSearch2:searchMessages2");
                setSeqSearchTabActive("0");
            }

            if (perQueryResults.isEmpty() || totalRetrieved == 0) {
//                //injecting param for js
//                addMessage("No promoter regions found!", "", "mainpanel", FacesMessage.SEVERITY_FATAL);
//                RequestContext.getCurrentInstance().getCallbackParams().put("showResults", false); //should not be necessary as is set as false at Submit
//                RequestContext.getCurrentInstance().getCallbackParams().put("showXML", true); //allows user to save blastn results 
//                if (!email.isEmpty()) {
//                    StringBuilder sb1 = new StringBuilder();
//                    reusable.ExecProcessor.email("No promoter regions were retrieved", "PromoterFinder notification: job failed.", email, "no-reply@hathor.acpfg.local");
//                }
                growl(FacesMessage.SEVERITY_WARN, "Bad luck!", "No matches found!", "searchMessages");
                setSeqSearchTabActive("0");

            } else {
                growl(FacesMessage.SEVERITY_INFO, "Hit(s) found!", "Alignment successfull", ":formSearch2:searchMessages2");
            }
        } else {
            growl(FacesMessage.SEVERITY_FATAL, "Error!", "No input sequeces!", "searchMessages");
            setSeqSearchTabActive("0");

        }
    }

    private String generateEmailContent(Date submitTime) {
        StringBuilder sb = new StringBuilder("Results summary:\n");
        for (HitsForQuery prs : perQueryResults) {
            sb.append(prs.getQueryId()).append("\t:\t").append(prs.getHits().size()).append(" hits identified.\n");
        }
        sb.append("\nJob submitted: ").append(submitTime).append("\n");;
        sb.append("\nJob completed: ").append(new Date()).append("\n");
        sb.append("\nContact radoslaw.suchecki@adelaide.edu.au with questions or comments about the POTAGE application. \n\n"
                + "ACPFG Bioinformatics Group "
                //                + "University of Adelaide, School of Agriculture, Food and Wine \n "
                //                + "Plant Genomics Centre, Waite Campus, SA, Australia. \n "
                + "\n");
        return sb.toString();
    }

    public boolean isAutoDisplayCharts() {
        return autoDisplayCharts;
    }

    public void setAutoDisplayCharts(boolean autoDisplayCharts) {
        this.autoDisplayCharts = autoDisplayCharts;
    }

    public void chartItemSelect(ItemSelectEvent event) {
        int i = event.getItemIndex() + 3;
        if (fpkmTableHeaders != null && i >= 0 && i <= fpkmTableHeaders.length + 1) {
            growl(FacesMessage.SEVERITY_INFO, "Sample selected", fpkmTableHeaders[i], "growl");
        }
    }

    public void poll() {
        final DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent(":formCentre:chartsGrid");
        System.err.println(d.getEmptyMessage());
    }

    public void updateComponent(String id) {
        RequestContext.getCurrentInstance().update(id);
    }

    public boolean isAppendUnordered() {
        return appendUnordered;
    }

    public void setAppendUnordered(boolean appendUnordered) {
        this.appendUnordered = appendUnordered;
    }

    public Integer getIndexOfQuery(String key) {
        Integer idx = null;
        for (int i = 0; i < loadedGenes.size(); i++) {
            Gene current = loadedGenes.get(i);
            if (current.getGeneId().equalsIgnoreCase(key) || current.getContig().getId().equals(key)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    public ArrayList<Gene> getLoadedGenes() {
        return loadedGenes;
    }

//    public MapDbFrontBean getMapDbFrontBean() {
//        return mapDbFrontBean;
//    }
//
//    public void setMapDbFrontBean(MapDbFrontBean mapDbFrontBean) {
//        this.mapDbFrontBean = mapDbFrontBean;
//    }
}
