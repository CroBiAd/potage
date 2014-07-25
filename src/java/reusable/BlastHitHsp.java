/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author rad
 */
public class BlastHitHsp implements Comparable<BlastHitHsp>, Serializable {

    

    private String qseqid;
    private String sseqid;
    private int hspNum;
    private double pident;
    private int length;
    private int mismatch;
    private int gapopen;
    private int qstart;
    private int qend;
    private int sstart;
    private int send;
    private double evalue;
    private double bitscore;
    private int qlen;
    private int slen;
    private int qframe;
    private int sframe;
    private boolean queryStartsWithMethonine = false; //for blastx output only 
    private String qseq;
    private boolean subjectStartsWithMethonine = false; //for blastx output only 
    private String sseq;
    private String subjectDescription;
    

    
    /**
     * Constructor used with xml, perhaps should replace with a simpler one + .add() methods
     * @param qseqid
     * @param sseqid
     * @param hspNum
     * @param pident
     * @param qstart
     * @param qend
     * @param qframe
     * @param sframe
     * @param qseq
     * @param sseq
     * @param subjectDescription
     * @param sstart
     * @param send 
     */
    public BlastHitHsp(String qseqid, String sseqid, int hspNum, double pident, int qstart, int qend, int qframe, int sframe, String qseq, String sseq, String subjectDescription, int sstart, int send) {
        this.hspNum = hspNum;
        this.qseqid = qseqid;
        this.sseqid = sseqid;
        this.pident = pident;
        this.qstart = qstart;
        this.qend = qend;
        this.qframe = qframe;
        this.sframe = sframe;
        if(qseq.startsWith("M")) {
            queryStartsWithMethonine = true;
        }
        this.qseq = qseq;
        if(sseq.startsWith("M")) {
            subjectStartsWithMethonine = true;
        }
        this.sseq = sseq;
        this.subjectDescription = subjectDescription;
        
        //added for blast n
        this.sstart = sstart;
        this.send = send;
    }
    
    
    

    /**
     * TODO : update constructor so that the columns can be specified by blast-like setting e.g. 
     * -outfmt "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore qframe sframe"
     * Perhaps use a hashtable to link a label to a position e.g. key="qseqid", value=0
     * 
     * 
     * @param blastTabEntry
     * @param separator 
     */
    public BlastHitHsp(String blastTabEntry, String separator) {
        String[] tokens = blastTabEntry.split(separator);
        qseqid = tokens[0];
        sseqid = tokens[1];
        pident = Double.valueOf(tokens[2]);
        length = Integer.parseInt(tokens[3]);
        mismatch = Integer.parseInt(tokens[4]);
        gapopen = Integer.parseInt(tokens[5]);
        qstart = Integer.parseInt(tokens[6]);
        qend = Integer.parseInt(tokens[7]);
        sstart = Integer.parseInt(tokens[8]);
        send = Integer.parseInt(tokens[9]);
        evalue = Double.valueOf(tokens[10]);
        bitscore = Double.valueOf(tokens[11].trim());
        qlen = Integer.parseInt(tokens[12]);
        slen = Integer.parseInt(tokens[13]);
        //adding for promoter finding
        qframe = Integer.parseInt(tokens[14]);
        sframe = Integer.parseInt(tokens[15]);
        
    }

    public String getSseq() {
        return sseq;
    }

    

    @Override
    public int compareTo(BlastHitHsp another) {
//        if(another.getLength() - getLength() == 0) {
//            //if equal length sort by start position on the query
//            return getActualQstart() - another.getActualQstart() ;
//        } else 
//            return another.getLength() - getLength();
        return another.getLengthOnQuery() - getLengthOnQuery();
    }
    
    public int getLengthOnQuery() {
        return getActualQend() - getActualQstart() +1;
    }

    
    public enum Decision {
        EQUAL, A_IN_B, B_IN_A, A_OVERLAP_B, B_OVERLAP_A, A_DISJOINT_B, B_DISJOINT_A
    }
    
    public int getOvverLapWithAnother(BlastHitHsp anotherAlignment) {
        //ensure start/stop are in the right order
        int start = getActualQstart();
        int end = getActualQend();
        int startOfAnother = anotherAlignment.getActualQstart();
        int endOfAnother = anotherAlignment.getActualQend();
        Decision decision = identifyOrderOfThisToAnotherAlignmentInRelationToQuery(anotherAlignment);
        if(decision == Decision.A_OVERLAP_B) {
            return end - startOfAnother +1;
        } else if(decision == Decision.B_OVERLAP_A) {
            return endOfAnother - start +1;
        } else {
            return -1;
        }
        
    }
    
    public int getGapToAnother(BlastHitHsp anotherAlignment) {
        //ensure start/stop are in the right order
        int start = getActualQstart();
        int end = getActualQend();
        int startOfAnother = anotherAlignment.getActualQstart();
        int endOfAnother = anotherAlignment.getActualQend();
        Decision decision = identifyOrderOfThisToAnotherAlignmentInRelationToQuery(anotherAlignment);
        if(decision == Decision.A_DISJOINT_B) {
            return startOfAnother - end +1;
        } else if(decision == Decision.B_DISJOINT_A) {
            return start - endOfAnother  +1;
        } else {
            return -1;
        }
        
    }

    /**
     *
     * @param anotherAlignment
     * @return
     */
    public Decision identifyOrderOfThisToAnotherAlignmentInRelationToQuery(BlastHitHsp anotherAlignment) {
        //ensure start/stop are in the right order
        int start = getActualQstart();
        int end = getActualQend();
        int startOfAnother = anotherAlignment.getActualQstart();
        int endOfAnother = anotherAlignment.getActualQend();

//        if(this.getLabelString().equals("5D") && anotherAlignment.getLabelString().equals("8D"))
//        {
//            int k = 10;
//        }
        String text = "";        
        //Possible outcomes:
        if (start == startOfAnother && end == endOfAnother) {
            //0 alignemts overlap exactly (in terms of start-end-points on the ___QUERY____!!!!!)
            text = "0 alignemts overlap exactly";
            return Decision.EQUAL;
        } else if (start <= startOfAnother && end >= endOfAnother) {
            text = "2 another contained in alignment";
            // another contained in alignment 
            return Decision.B_IN_A;
        } else if (start >= startOfAnother && end <= endOfAnother) {
            text = "1 alignment contained in another";
            // alignment contained in another
            return Decision.A_IN_B;
        } else if (start <= startOfAnother && end >= startOfAnother) {
            // alignments overlap A then B
            text = "3 alignments overlap A then B";
            return Decision.A_OVERLAP_B;
        } else if (startOfAnother <= start && endOfAnother >= start) {
            text = "4 alignments overlap B then A";
            // alignments overlap B then A
            return Decision.B_OVERLAP_A;
        } else if (end < startOfAnother) {
            text = "5 alignments disjoint A then B";
            // alignments disjoint A then B
            return Decision.A_DISJOINT_B;
        } else if (endOfAnother < start) {
            text = "6 alignments disjoint B then A";
            // alignments disjoint B then A
            return Decision.B_DISJOINT_A;
        } else {
            text = "7 What else?";
            //What else?
        }

        System.out.printf("A: %20s, %5d, %5d\n", getSseqid(), start, end);
        System.out.printf("B: %20s, %5d, %5d\n", anotherAlignment.getSseqid(), startOfAnother, endOfAnother);
        System.out.println(text + "\n");
                
        return null;
    }

    private boolean alignmentContainedInAnother(int aStart, int aEnd, int anotherStart, int anotherEnd) {
        if (aStart >= anotherStart && aEnd <= anotherEnd) {
            return true;
        }
        return false;
    }

    private boolean alignmentsOverlapExactly(int aStart, int aEnd, int anotherStart, int anotherEnd) {
        if (aStart == anotherStart && aEnd == anotherEnd) {
            return true;
        }
        return false;
    }


    /**
     * Get the value of sseqid
     *
     * @return the value of sseqid
     */
    public String getSseqid() {
        return sseqid;
    }

    /**
     * Get the value of qseqid
     *
     * @return the value of qseqid
     */
    public String getQseqid() {
        return qseqid;
    }

    public double getPident() {
        double temp = pident*100;        
        temp = Math.round(temp);
        temp /= 100;
        return temp;
    }

    public int getLength() {
        return length;
    }

    public int getMismatch() {
        return mismatch;
    }

    public void setMismatch(int mismatch) {
        this.mismatch = mismatch;
    }

    public int getGapopen() {
        return gapopen;
    }

    public void setGapopen(int gapopen) {
        this.gapopen = gapopen;
    }

    public int getQstart() {
        return qstart;
    }

    public int getActualQstart() {
        return Math.min(qstart, qend);
    }
    public int getActualSstart() {
        return Math.min(sstart, send);
    }

    public int getQend() {
        return qend;
    }

    public int getActualQend() {
        return Math.max(qstart, qend);
    }
    
    public int getActualSend() {
        return Math.max(sstart, send);
    }

    public int getSstart() {
        return sstart;
    }

    public int getSEnd() {
        return send;
    }

    public double getEvalue() {
        return evalue;
    }

    public void setEvalue(String evalue) {
        this.evalue = Double.valueOf(evalue);
    }

    public double getBitscore() {
        return bitscore;
    }

    public int getQlen() {
        return qlen;
    }

    public int getSlen() {
        return slen;
    }

    public int getQframe() {
        return qframe;
    }

    public int getSframe() {
        return sframe;
    }

    public boolean queryStartsWithMethonine() {
        return queryStartsWithMethonine;
    }

    public boolean subjectStartsWithMethonine() {
        return subjectStartsWithMethonine;
    }
    
    public boolean bothSubjectAndQueryStartsWithMethonine() {
        if(subjectStartsWithMethonine && queryStartsWithMethonine) {
            return true;
        }
        return false;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public int getHspNum() {
        return hspNum;
    }
    
    public boolean isTopHspOfAHit() {
        if(hspNum == 1) {
            return true;
        }
        return false;
    }
    
    
    
    

    /**
     * 
     * @return aligned query sequence, so e.g. for blastx this could be: MAMKGPGLFTDIGKKAKDLLTRDYTYDQKLSV....
     */
    public String getQseq() {
        return qseq;
    }
    
    public int getTrimPoint() {
        if(qframe > 0) { 
            return qstart;
        }
        else if(qframe < 0) {
            return qend;
        }
        else {
            System.err.println("qframe should never == 0 ");
            return 0;
        }
    }
    
}
