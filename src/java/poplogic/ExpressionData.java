/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import reusable.InReader;
import reusable.Reporter;

/**
 *
 * @author rad
 */
public class ExpressionData {

    private String shortName;
    private String longName;
    private String unit;
    private String[] header;
    private HashMap<String, ArrayList<Double>> genesToExpressionMap;

    public ExpressionData(String configFileName, boolean DEBUG) {
        InReader in = new InReader(configFileName);
        String parentPath = in.getParentPath();
        ArrayList<String> returnInput = in.returnInput();
        String fileName = null;
        for (String line : returnInput) {
            String toks[] = line.split("[ \t]+");
            switch (toks[0].toLowerCase().replaceFirst("#.*", "").trim()) {
                case "":
                    break;
                case "shortname":
                    shortName = line.replaceFirst("[^ \t]+[ \t]+", "");
                    break;
                case "longname":
                    longName = line.replaceFirst("[^ \t]+[ \t]+", "");
                    break;
                case "unit":
                    unit = line.replaceFirst("[^ \t]+[ \t]+", "");
                    break;
                case "filename": {
                    fileName = line.replaceFirst("[^ \t]+[ \t]+", "");
                    if (!fileName.startsWith("/")) {
                        fileName = parentPath + "/" + fileName;
                    }
                    break;
                }
                default: {
                    Reporter.report("[ERROR]", "Unable to parse data config file " + configFileName + ", offending line: " + line, this.getClass().getCanonicalName());
                }
            }
        }
        if(shortName == null || longName ==null || unit == null ) {
            Reporter.report("[ERROR]", "Incomplete data config file " + configFileName + " ", this.getClass().getCanonicalName());            
        }
        if(fileName == null ) {
            Reporter.report("[ERROR]", "Incomplete data config file, missing FileName " + configFileName + " ", this.getClass().getCanonicalName());                        
        } else {
            genesToExpressionMap = getGenesTissuesFPKMs(fileName, DEBUG);            
        }

    }

    public ExpressionData(String shortName, String longName, String unit, String fileName, boolean DEBUG) {
        this.shortName = shortName;
        this.longName = longName;
        this.unit = unit;
        genesToExpressionMap = getGenesTissuesFPKMs(fileName, DEBUG);
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getUnit() {
        return unit;
    }

    public String[] getHeader() {
        return header;
    }

    public ArrayList<Double> getExpressionValues(String geneId) {
        return genesToExpressionMap.get(geneId);
    }

    private HashMap<String, ArrayList<Double>> getGenesTissuesFPKMs(String FPKMsFileName, boolean DEBUG) {
//        HashMap<String, ArrayList<Double>> genesTissuesFPKMsMap = new HashMap<>(100000, 1);
        if (genesToExpressionMap == null) {
            genesToExpressionMap = new HashMap<>(100000, 1);
        }
        File file = new File(FPKMsFileName);
        BufferedReader myData = null;
        try {
            int countCasesOfMultipleFpkmValuesPerGenePrediction = 0;
            String inputLine;
            myData = new BufferedReader(new FileReader(file));
            String readLine = myData.readLine();
            Pattern p = Pattern.compile("\t");
            header = p.split(readLine);

            while ((inputLine = myData.readLine()) != null) {
                if (DEBUG && !inputLine.startsWith("Traes_1")) {
                    continue;
                }
                String toks[] = p.split(inputLine);
                if (toks != null && toks.length > 1 && toks[0].startsWith("Traes")) {
                    ArrayList<Double> storedGenePerTissueFPKMs = genesToExpressionMap.get(toks[0]);
                    boolean overwrite = false;
                    if (storedGenePerTissueFPKMs == null) {
                        overwrite = true;
                    } else {
                        countCasesOfMultipleFpkmValuesPerGenePrediction++;
                        double lenStored = storedGenePerTissueFPKMs.get(2) - storedGenePerTissueFPKMs.get(1) + 1;
                        double lenCurrent = Double.parseDouble(toks[2]) - Double.parseDouble(toks[1]) + 1;
                        if (lenCurrent > lenStored) {
                            overwrite = true;
                        }
                    }
                    if (overwrite) { //storing only if empty or if cufflinks reported length is longer for the same gene
                        storedGenePerTissueFPKMs = new ArrayList<>(18);
                        for (int i = 1; i < toks.length; i++) { //1 and 2 are coordinates
                            storedGenePerTissueFPKMs.add(Double.parseDouble(toks[i]));
                        }
                        genesToExpressionMap.put(toks[0], storedGenePerTissueFPKMs);
                    }
                    ///storedGeneIds.add(toks[0]);
                }
            }
//            System.err.println("not allowing multipleExpressionValuesPerGenePrediction = " + countCasesOfMultipleFpkmValuesPerGenePrediction + "  in " + this.toString());

        } catch (FileNotFoundException ex) {
            System.err.println("File not found exception!\t" + file.getName());
//            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (myData != null) {
                    myData.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return genesToExpressionMap;
    }

}
