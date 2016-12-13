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
package reusable;

import java.io.Serializable;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class Hit implements Serializable {

    private String queryId;
    private String hitId;
    private String promoterAndMissingString;
    private String actualPromoterString;
    private String missingFromTranscriptString;
    private final BlastHit hit;
    private final double percIdOverQlen;
    private final double percIdOverAlignedQlen;
    private final double percQueryAligned;
    private final String uniqueIdString; //not used????????
    private int qframe;
    private int sframe;
    private int contigCoordinates[];
    private String alignedSectionOfHitContig;
    
    private Sequence wholeContigSequence;

    public Hit(String queryId, String hitId, BlastHit hit, double percIdOverQlen, double percIdOverAlignedQlen, double percQueryAligned) {
        this.queryId = queryId;
        this.hitId = hitId;
        this.hit = hit;
        this.percIdOverQlen = percIdOverQlen * 100;
        this.percIdOverAlignedQlen = percIdOverAlignedQlen * 100;
        this.percQueryAligned = percQueryAligned * 100;
        uniqueIdString = hitId + Math.random();
    }

    public String getUniqueIdString() {
        return uniqueIdString;
    }

    public void setPromoterString(String promoterAndMissingString, String missingFromTranscriptString, String actualPromoter) {
        this.promoterAndMissingString = promoterAndMissingString;
        this.missingFromTranscriptString = missingFromTranscriptString;
        actualPromoterString = actualPromoter;
    }

    public Sequence getWholeContigSequence() {
        return wholeContigSequence;
    }

    public void setWholeContigSequence(Sequence wholeContigString) {
        this.wholeContigSequence = wholeContigString;
    }
    
    

    public BlastHit getHit() {
        return hit;
    }

    private String formatDouble(double d, String color) {
        if (d == 100) {
            return "<font color=\"" + color + "\">&nbsp;&nbsp;&nbsp;100%</font>";
        } else {
            return "<font color=\"" + color + "\">" + String.format("%2.2f", reusable.CommonMaths.round(d, 2)) + "%</font>";
        }
    }

    public String getFromatedString(double value) {
        if (value >= 95) {
            return formatDouble(value, "green");
        } else if (value >= 90) {
            return formatDouble(value, "#FF7E00");
        } else {
            return formatDouble(value, "red");
        }
    }

    public String getTraficLightColor() {
        if (percIdOverQlen >= 95) {
            return "green";
        } else if (percIdOverQlen >= 90) {
            return "#FF7E00";
        } else {
            return "red";
        }
    }

    public double getPercIdOverQlenDouble() {
        return percIdOverQlen;
    }

    public String getPercIdOverQlen() {
        return getFromatedString(percIdOverQlen);
//        if(percIdOverQlen >= 95) {
//            return formatDouble(percIdOverQlen, "green");
//        } else if (percIdOverQlen >= 90) {
//            return formatDouble(percIdOverQlen, "#FF7E00");
//        } else {
//            return formatDouble(percIdOverQlen, "red");
//        }
    }

    public String getPercIdOverAlignedQlen() {
//        return percIdOverAlignedQlen;
        return getFromatedString(percIdOverAlignedQlen);
    }

    public double getPercIdOverAlignedQlenDouble() {
        return percIdOverAlignedQlen;
    }
    
    

    public String getPercQueryAligned() {
        return getFromatedString(percQueryAligned);
    }

    public int getQframe() {
        return qframe;
    }

    public void setQframe(int qframe) {
        this.qframe = qframe;
    }

    public int getSframe() {
        return sframe;
    }

    public void setSframe(int sframe) {
        this.sframe = sframe;
    }

    public String getFrames() {
        StringBuilder sb = new StringBuilder();
        if (qframe < 0) {
            sb.append("-/");
        } else {
            sb.append("+/");
        }
        if (sframe < 0) {
            sb.append("-");
        } else {
            sb.append("+");
        }
        return sb.toString();
    }

//    
//    
//    public Promoter(String queryID, String hitId, String promoterString) {
//        this.hitId = hitId;
//        this.promoterString = promoterString;
//        queryId = queryID;
//    }
    public String getHitId() {
        return hitId;
    }

    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    public String getPromoterString() {
        return promoterAndMissingString;
    }

    public String getQueryId() {
        return queryId;
    }

    public int getLength() {
        return promoterAndMissingString.length();
    }

    //    public int getFrame() {
    //        return hit.
    //    }

    public String getContigCoordinatesHTML() {
        if (hit != null) {
            int[] rangeMinMax = hit.getRangeMinMax(false);
            return rangeMinMax[0] + "-" + rangeMinMax[1];
        } else {
            return "<font color=\"red\">no coordinates???</font>";
        }
    }

    public String getContigCoordinates() {
        if (contigCoordinates != null) {
            return contigCoordinates[0] + "-" + contigCoordinates[1];
        } else {
            return "";
        }
    }
//
//    public String getFormatedString() {
//        String formated = actualPromoterString + "<font color=\"blue\" style=\"BACKGROUND-COLOR: yellow\">" + missingFromTranscriptString + "</font>";
//
//        return formated;
//    }

    public String getAlignedSectionOfHitContig() {
        return alignedSectionOfHitContig;
    }

    public String getFormatedString(int bases, int hasATG) {
        
        StringBuilder sb = new StringBuilder();
        if(actualPromoterString != null) {
//            sb.append(highlightATG(actualPromoterString));
            sb.append(actualPromoterString);
        }
        if (hasATG == 0) {
            sb.append("<font color=\"blue\" style=\"BACKGROUND-COLOR: cornsilk\">").append(highlightATG(getFirstBasesOfAlignedSectionOfHitContig(bases))).append("</font>");
            return sb.toString();
        } else {
            sb.append("<font color=\"blue\" style=\"BACKGROUND-COLOR: yellow\">").append(highlightATG(missingFromTranscriptString)).append("</font>");
            sb.append("<font color=\"blue\" style=\"BACKGROUND-COLOR: khaki\">").append(highlightATG(getFirstBasesOfAlignedSectionOfHitContig(bases))).append("</font>");
            return sb.toString();
        }

    }
    
    public String highlightATG(String inputString) {
//        StringBuilder formated = new StringBuilder("<font color=\"blue\" style=\"BACKGROUND-COLOR: "+color+"\">");
        StringBuilder formated = new StringBuilder("");
        char c[] = inputString.toCharArray();
        for(int i=0; i<c.length-2; i++) {
            if(c[i] == 'A' && c[i+1] == 'T' && c[i+2] == 'G' ) {
                formated.append("<b>").append("ATG").append("</b>");
                i += 2;
                if(i == c.length-2) {
                    formated.append(c[i+1]);
                } else if(i == c.length-3) {
                    formated.append(c[i+1]);
                    formated.append(c[i+2]);
                }
            } else {
                formated.append(c[i]);
                if(i == c.length-3) {
                    formated.append(c[i+1]);
                    formated.append(c[i+2]);
                }
            }
        }
        return formated.toString();
    }

    public String getFirstBasesOfAlignedSectionOfHitContig(int bases) {
        if (bases > alignedSectionOfHitContig.length()) {
            bases = alignedSectionOfHitContig.length(); //ensure we attempt to retrieve only as much as can be retrieved;
        }
        return alignedSectionOfHitContig.substring(0, bases);
    }

    public void setAlignedSectionOfHitContig(String alignedSectionOfHitContig) {
        this.alignedSectionOfHitContig = alignedSectionOfHitContig;
    }
}
