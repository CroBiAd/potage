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
import java.util.ArrayList;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class FastaOps  implements Serializable{

    
    public static void writeToFastaFile(ArrayList<Sequence> sequences, String fileName) {
        ArrayList<String> content = new ArrayList<String>(sequences.size()*2);
        for(Sequence s: sequences) {
            content.add(">"+s.getIdentifierString());
            content.add(s.getSequenceString());
        }
        new OutWriter(fileName, content);
    }
    
    /**
     * Reads in a fasta file and generates an ArrayList of Sequence objects
     * input fasta sequences can be broken over multiple lines
     *
     * @param fastaFileName
     * @return
     */
    public static ArrayList<Sequence> sequencesFromFasta(String fastaFileName) {
        ArrayList<Sequence> seqs = new ArrayList<Sequence>();
        InReader in = new InReader(fastaFileName);
        ArrayList<String> contents = in.returnInput();
        String id = "";
        StringBuilder seqBuilder = new StringBuilder();
        for (int i = 0; i < contents.size(); i++) {
            String line = contents.get(i);
            if (line.startsWith(">")) {
                if (seqBuilder.length() > 0) {
                    seqs.add(new Sequence(seqBuilder.toString(), id));
                    seqBuilder = new StringBuilder();
                }
                id = line.substring(1);
            } else {
                seqBuilder.append(line);
            }
        }
        seqs.add(new Sequence(seqBuilder.toString(), id));
        return seqs;
    }
    
    public static ArrayList<Sequence> sequencesFromFastaString(String fastaString, boolean fastaStringNotFilename) {
        ArrayList<Sequence> seqs = new ArrayList<Sequence>();
        String contents[] = fastaString.split("\n");
        String id = null;
        StringBuilder seqBuilder = new StringBuilder();
        for (int i = 0; i < contents.length; i++) {
            String line = contents[i];
            if (line.startsWith(">")) {
                if (seqBuilder.length() > 0) {
                    seqs.add(new Sequence(seqBuilder.toString(), id));
                    seqBuilder = new StringBuilder();
                }
                id = line.substring(1);
            } else {
                seqBuilder.append(line.trim());
            }
        }
        if(id != null ) {
            seqs.add(new Sequence(seqBuilder.toString(), id));
        }
        return seqs;
    }

    public static ArrayList<Sequence> sequencesFromFastaByteArray(byte[] fastaBytes) {
        String contents = new String(fastaBytes);
        ArrayList<Sequence> seqs = new ArrayList<Sequence>();
        String lines[] = contents.split("\n");
        String id = null;
        StringBuilder seqBuilder = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(">")) {
                if (seqBuilder.length() > 0) {
                    if (id == null || id.isEmpty()) {
                        return null;
                    }
                    seqs.add(new Sequence(seqBuilder.toString(), id));
                    seqBuilder = new StringBuilder();
                }
                id = line.substring(1);
            } else {
                seqBuilder.append(line);
            }
        }
        if (id == null || id.isEmpty()) {
            return null;
        }
        seqs.add(new Sequence(seqBuilder.toString(), id));
        return seqs;
    }

    /**
     *
     * @param fastaFileName
     * @return
     */
    public static void readFastaOutputFastaLongerThenX(String inputFastaFileName, String outputFastaFileName, int x) {
        ArrayList<Sequence> seqs = new ArrayList<Sequence>();
        InReader in = new InReader(inputFastaFileName);
        ArrayList<String> contents = in.returnInput();
        String id = "";
        StringBuilder seqBuilder = new StringBuilder();
        for (int i = 0; i < contents.size(); i++) {
            String line = contents.get(i);
            if (line.startsWith(">")) {
                if (seqBuilder.length() > 0) {
                    seqs.add(new Sequence(seqBuilder.toString(), id));
                    seqBuilder = new StringBuilder();
                }
                id = line.substring(1);
            } else {
                seqBuilder.append(line);
            }
        }
        seqs.add(new Sequence(seqBuilder.toString(), id));

        StringBuilder sb = new StringBuilder();
        int seqsCount = 0;
        for (Sequence s : seqs) {
            if (s.getLength() >= x) {
                seqsCount++;
                sb.append(">");
                sb.append(s.getIdentifierString());
                sb.append("\n");
                sb.append(s.getSequenceString());
                sb.append("\n");
            }
        }
        System.out.println(seqsCount + " of " + seqs.size() + " > " + x + "bp");
        new OutWriter(outputFastaFileName, sb.toString());
    }
    
    
    public String convertTwoAlignedSequenceStringsToMFS(String id0, String seq0, String id1, String seq1) {
        int len = seq0.length();
        StringBuilder sb = new StringBuilder("PileUp\n\nMSF:\t");
        sb.append(len);
        sb.append("\tType: N"); //N or P, should do that automatically!
        sb.append("\tCheck: 0"); //should be a cheksum composed of ASCII values of sequence chars
        sb.append("\n");
        
        StringBuilder line0 = new StringBuilder("id0  ");
        StringBuilder line1 = new StringBuilder("id1  ");
        for(int i=0; i<len; i++) {
            if(i%32==0) {
                sb.append(line0.toString());
                sb.append("\n");
                sb.append(line1.toString());
                sb.append("\n");
                line0 = new StringBuilder("id0  ");
                line1 = new StringBuilder("id1  ");                
            } 
            line0.append(seq0.charAt(i));
            line1.append(seq1.charAt(i));
            if(i%8==0) {
                line0.append(" ");
                line1.append(" ");
            }
        }
        
//PileUp
//
//MSF:	1011	 Type: P	Check:	8254  ..
//
// Name: ogihara_2426-1.15414.6D_beheaded_n_0           Len:   1011    Check: 8254    Weight:   1.0
//
////
//
//                                      1                                                   50
//ogihara_2426-1.15414.6D_beheaded_n_0  ATGGCGCTCG CCGCTCGCCT CGTATCCCGC TCCCGCCAGT TATATTCTGT

        return "";
    }
    

    public static void validateFastaForNonACGT(String fastaFileName) {


        ArrayList<String> goodList = new ArrayList<String>();
        ArrayList<String> nonAcgtList = new ArrayList<String>();
        ArrayList<String> polyAndNonACGT = new ArrayList<String>();
        ArrayList<String> homopolymersList = new ArrayList<String>();

        ArrayList<String> trimmedOffList = new ArrayList<String>();

        int statsTrimmed3prime = 0;
        int statsTrimmed5prime = 0;
        int statsTrimmed3prime5prime = 0;
        int statsSeqsWithPolyInMiddle = 0;
        int statsTotalPolysInMIddle = 0;
        int statsSeqsWithNonACGT = 0;
        int statsTotalNonACGTchars = 0;

        ArrayList<Sequence> sequences = sequencesFromFasta(fastaFileName);
        for (int i = 0; i < sequences.size(); i++) {
            Sequence s = sequences.get(i);
            char[] chars = s.getSequenceChars();
            char previous = ' ';
            int homoPolymerRun = 0;
            int nonACGTcount = 0;
            int homopolymerInTheMiddle = 0;
            int trim1 = 0;
            int trim2 = 0;
            StringBuilder flag = new StringBuilder();
            StringBuilder tempFlag = new StringBuilder();


//            if(s.getIdentifierString().startsWith("tplb0006b18")) {
//                int f=0;
//            }

            //after trimming 5' we may want to trim it again if meets criteria (i.e. <10 positions from the NEW begining of the sequence)
            //so....
            int start = 0;
            int length = chars.length;
            boolean rescanSequenceAfterMarking3PrimeForTrimming = false;
            for (int j = start; j < length; j++) {
                char c = chars[j];
//                System.err.print(c);
                if (Character.isLowerCase(c)) {
                    c = Character.toUpperCase(c);
                }
                if (c != 'A' && c != 'C' && c != 'G' && c != 'T') {
                    nonACGTcount++;
                }
                if (Character.compare(c, previous) == 0) {
                    if (c == 'A' || c == 'T' || c == 'N' || c == 'X') {
                        homoPolymerRun++;
                        if (homoPolymerRun >= 10 && j == length - 1) { //just in case we reached the end with a homopolymer
                            flag.append("_trimmedLast");
                            flag.append(homoPolymerRun + 1);
                            trim2 = j - homoPolymerRun + 1;
                            rescanSequenceAfterMarking3PrimeForTrimming = true;
                            j = start - 1;
                            length = trim2;
//                                length -= (j + homoPolymerRun + 1); //check that 1 !!!!!!!!!!!!1
                            tempFlag = new StringBuilder(); //reset any record of homopolymers in the middle
                            homopolymerInTheMiddle = 0;
                            nonACGTcount = 0;
                            homoPolymerRun = 0;
                            continue;
                        }
                    }
                } else {
                    if (homoPolymerRun >= 10) {
                        if (j >= length - 20 - 1) {
                            flag.append("_trimmedLast");
//                            flag.append(homoPolymerRun + 1);
                            flag.append(length - j + homoPolymerRun + 1);
                            trim2 = j - homoPolymerRun;
                            rescanSequenceAfterMarking3PrimeForTrimming = true;
                            length = trim2;//(j + homoPolymerRun + 1); //check that 1 !!!!!!!!!!!!1
                            j = start - 1;
                            tempFlag = new StringBuilder(); //reset any record of homopolymers in the middle
                            homopolymerInTheMiddle = 0;
                            nonACGTcount = 0;
                            homoPolymerRun = 0;
                            continue;
                        } else if (j - homoPolymerRun < start + 20) { //added start+ to accommodate for previously removed 3' so that we can remove more of it if meets criteria
                            //TRIM & flag!!!!                                                               
                            if (!rescanSequenceAfterMarking3PrimeForTrimming) {
                                flag.append("_trimmedFirst");
                                flag.append(j);
                                trim1 = j;
                                start = j;  //accommodating multiple trims of 5'
                                nonACGTcount = 0;
                            }
                        } else {
                            tempFlag.append("_poly");
                            tempFlag.append(previous);
                            tempFlag.append("(");
                            tempFlag.append(homoPolymerRun + 1);
                            tempFlag.append(")");
//                                tempFlag.append(j - homoPolymerCount+1);  //uncomment to obtain original coordinates of a homopolymer run (and comment out the following 3 lines)
//                                tempFlag.append("-");
//                                tempFlag.append(j);
                            tempFlag.append(j - homoPolymerRun - start);
                            tempFlag.append("-");
                            tempFlag.append(j - start);
                            homopolymerInTheMiddle++;
                            //just flag and store in separate file
                        }
                    }
                    homoPolymerRun = 0;
                }
                previous = c;
            }
            System.out.println(s.getIdentifierString());

            flag.append(tempFlag);
            if (nonACGTcount > 0) {
                flag.append("_");
                flag.append(nonACGTcount);
                flag.append("nonACGT");
                statsTotalNonACGTchars += nonACGTcount;
                statsSeqsWithNonACGT++;
            }
            String seq = s.getSequenceString();
            String trimmed = null;
            String trimmedOffString = null;
            if (trim1 > 0 && trim2 > 0) {
                trimmed = s.getSequenceString().substring(trim1, trim2 - 1);
                trimmedOffString = seq.substring(0, trim1);
                trimmedOffString += "\n" + seq.substring(trim2 - 1);
                System.out.println("\tTrimmed both ends");
                statsTrimmed3prime5prime++;
                if ((trimmed.length() + trimmedOffString.length()) != seq.length() + 1) {
                    System.err.println("Error:  lengths of trimmed and trimmed off do not add up to the length of original sequence! (b) " + s.getIdentifierString());
                }
            } else if (trim1 > 0) {
                trimmed = seq.substring(trim1);
                trimmedOffString = seq.substring(0, trim1);
                System.out.println("\tTrimmed 5'");
                statsTrimmed5prime++;
                if ((trimmed.length() + trimmedOffString.length()) != seq.length()) {
                    System.err.println("Error:  lengths of trimmed and trimmed off do not add up to the length of original sequence! (5')" + s.getIdentifierString());
                }
            } else if (trim2 > 0) {
                trimmed = seq.substring(0, trim2 - 1);
                trimmedOffString = seq.substring(trim2 - 1);
                System.out.println("\tTrimmed 3'");
                statsTrimmed3prime++;
                if ((trimmed.length() + trimmedOffString.length()) != seq.length()) {
                    System.err.println("Error:  lengths of trimmed and trimmed off do not add up to the length of original sequence! (3')" + s.getIdentifierString());
                }
            }



            StringBuilder sb = new StringBuilder(">");
            StringBuilder sbGarbage = new StringBuilder(">");
            sb.append(s.getIdentifierString());
            sbGarbage.append(s.getIdentifierString());
            if (trimmed != null) {
                sb.append(flag);
                sb.append("\n");
                sb.append(trimmed);

                sbGarbage.append(flag);
                sbGarbage.append("\n");
                sbGarbage.append(trimmedOffString);
//                sbGarbage.append("\n");
                trimmedOffList.add(sbGarbage.toString());


            } else {
                if (nonACGTcount > 0 || homopolymerInTheMiddle > 0) {
                    sb.append(flag);
                }
                sb.append("\n");
                sb.append(seq);
            }

            //DISTRIBUTE SEQUENCES BETWEEN DIFFERENT OUTPUT FILES 
            String out = sb.toString();
            if (nonACGTcount > 0 && homopolymerInTheMiddle > 0) {
                polyAndNonACGT.add(out);
//                nonAcgtList.add(out);
//                homopolymersList.add(out);
                statsSeqsWithPolyInMiddle++;
                statsTotalPolysInMIddle += homopolymerInTheMiddle;
                System.out.println("\t" + nonACGTcount + " non-ACGT char(s)");
            } else if (nonACGTcount > 0) {
                System.out.println("\t" + nonACGTcount + " non-ACGT char(s)");
                nonAcgtList.add(out);
            } else if (homopolymerInTheMiddle > 0) {
                System.out.println("\thomopolymers");
                homopolymersList.add(out);
                statsSeqsWithPolyInMiddle++;
                statsTotalPolysInMIddle += homopolymerInTheMiddle;
            } else {
                goodList.add(out);
                if (trimmed == null) {
                    System.out.println("\t...all goood");
                }
            }


        }
        System.out.println("Stats: ");
        System.out.println(statsSeqsWithPolyInMiddle + "\tsequences with homopolymers in 'the middle'");
        System.out.println(statsTotalPolysInMIddle + "\ttotal homopolymer strings in 'the middle' of the sequences");
        System.out.println(statsSeqsWithNonACGT + "\tsequences with non ACGT character(s)");
        System.out.println(statsTotalNonACGTchars + "\ttotal of non ACGT characters in all sequences");
        System.out.println(statsTrimmed5prime + "\tsequences with 5' end trimmed due to homopolymers");
        System.out.println(statsTrimmed3prime + "\tsequences with 3' end trimmed due to homopolymers");
        System.out.println(statsTrimmed3prime5prime + "\tsequences with both ends trimmed due to homopolymers");
        System.out.println("\nOutputting .good .poly .nACGT .trimmedOff");

        System.out.println(polyAndNonACGT.size() + " both poly and nACGTs");
        System.out.println(homopolymersList.size() + " just poly ");
        System.out.println(nonAcgtList.size() + " just nACGTs");

        new OutWriter(fastaFileName + ".good.fa", goodList);
        new OutWriter(fastaFileName + ".poly.fa", homopolymersList);
        new OutWriter(fastaFileName + ".nACGT.fa", nonAcgtList);
        new OutWriter(fastaFileName + ".poly_and_nACTG.fa", polyAndNonACGT);
        new OutWriter(fastaFileName + ".trimmedOff.fa", trimmedOffList);

    }

    public static void main(String[] args) {
        if (args.length == 1) {
            validateFastaForNonACGT(args[0]);
        } else {
            System.out.print("Usage: ");
            System.out.println("java -jar jarname.jar input.fasta");
        }
    }
}
