/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poplogic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author rad
 */
public class Annotation implements Serializable {

    private String geneId;
    private String hitId;
    private String evalue; // AHRD-Quality-Code if annotation is directly from HCS not from rice hits
    private String annotationString;
    private String interpro;

    public Annotation(String gene, HashMap<String, String[]> mipsIdToAnnotationStringToksMap, boolean isRice) {
        String annotationToks[] = null;
        String key = null;
        if (gene.endsWith("*")) {
            key = gene.substring(0, gene.length() - 1);
        } else {
            key = gene;
        }
        geneId = gene;
        annotationToks = mipsIdToAnnotationStringToksMap.get(key);
        if (annotationToks != null && (!isRice || annotationToks.length > 3)) {
            int i = 1;
            try {
                if (isRice) {
                    i += 2;
                }
                hitId = annotationToks[i++];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                evalue = annotationToks[i++];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                annotationString = annotationToks[i++];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                interpro = annotationToks[i++];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        } else {
//            System.err.println("no annotation!");
            annotationString = "nothing found in rice";

        }
    }

    public String getGeneId() {
        return geneId;
    }

    public String getHitId() {
        return hitId;
    }

    public String getEvalue() {
        return evalue;
    }

    public Double getEvalueDouble() {
        return Double.valueOf(evalue);
    }

    public String getAnnotationString() {
        return annotationString;
    }

    public String getInterpro() {
        return interpro;
    }

    public String getRiceHTML(String fontColour) {
        if (hitId != null) {
            StringBuilder html = new StringBuilder();
            if (hitId.startsWith("LOC")) {
                html.append("<a href=\"http://rice.plantbiology.msu.edu/cgi-bin/ORF_infopage.cgi?db=osa1r6&orf=");
                html.append(hitId);
                html.append("\" value=\"");
                html.append(hitId);
                html.append("\" target=\"_blank\" style=\"color: ").append(fontColour).append("\">");
                html.append(hitId).append("</a> ");
                return html.toString();
            } else {
                return hitId;
            }
        }
        return null;
    }

    public String getMipsHTML(String fontColour) {
        if (hitId != null) {
            StringBuilder html = new StringBuilder();
            boolean isLink = true;
            if (hitId.startsWith("AT")) {
                html.append("<a href=\"http://www.arabidopsis.org/servlets/TairObject?type=locus&name=");
                html.append(hitId.replaceAll("\\.\\d", ""));
            } else if (hitId.startsWith("sp|")) {
                html.append("<a href=\"http://www.uniprot.org/uniprot/");
                html.append(hitId.split("\\|")[1]);
            } else if (hitId.startsWith("UniRef")) {
                html.append("<a href=\"http://www.uniprot.org/uniref/");
                html.append(hitId);
            } else {
                isLink = false;
                html.append("<font color=\"").append(fontColour).append("\">").append(hitId).append("</font> ");
            }

            //if AT/sp/uniprot
            if (isLink) {
                html.append("\" value=\"");
                html.append(hitId);
                html.append("\" target=\"_blank\" style=\"color: ").append(fontColour).append("\">");
                html.append(hitId).append("</a> ");
            }
            return html.toString();
        }
        return null;
    }

    public String getInterproHTML(String fontColour) {
        if (interpro != null) {
            StringBuilder html = new StringBuilder();
            String[] splits = interpro.split(" ");
            html.append("<font color=\"").append(fontColour).append("\">");
            for (String string : splits) {
                if (string.startsWith("IPR")) {
                    html.append("<a href=\"http://www.ebi.ac.uk/interpro/entry/");
                    html.append(string);
                    html.append("\" value=\"");
                    html.append(string);
                    html.append("\" target=\"_blank\" style=\"color: ").append(fontColour).append("\">");
                    html.append(string).append("</a> ");
                } else {
                    html.append(string).append(" ");
//                    html.append("<font color=\"").append(fontColour).append("\">").append(string).append("</font> ");
                }
            }
            html.append("</font> ");
            return html.toString();
        }
        return null;
    }

    public boolean hasInterproID() {
        if (interpro != null) {
            return true;
        }
        return false;
    }
}
