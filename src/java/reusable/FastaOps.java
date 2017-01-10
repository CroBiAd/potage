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

    


}
