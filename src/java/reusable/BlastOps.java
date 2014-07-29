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
public class BlastOps  implements Serializable{
 
     /**
     * Given the xml results of blastn search against IWGS, retrieve complete
     * subject/hit sequences of interest (as opposed to their aligned fragments
     * available in xml)
     *
     * temp: just reading in from file
     *
     * @return
     */
    public static ArrayList<Sequence> getCompleteSubjectSequence(String identifierString) {
        //temp: just read in from file
        //  ArrayList<Sequence> completeSubjectSequences = sequencesFromFasta("retrieved_complete_sequences.fasta");
        // e.g.     blastdbcmd -db IWGSC/iwgsc_all -entry 2AL_6391883 > retrived.fasta 
        String c = "blastdbcmd -db IWGSC/iwgsc_all -entry " + identifierString + " -out temp.fasta";
        String cmd[] = c.split(" "); //{"blastx", "-seg no -query ogihara_good_beheaded_10_test -remote -db nr -evalue 1e-50 -out ogihara_good_beheaded_10_test.blasted.xml -max_target_seqs 3 -outfmt 5"};
        Process proc = reusable.ExecProcessor.execute(cmd);
        
        ArrayList<Sequence> completeSubjectSequences = FastaOps.sequencesFromFasta("temp.fasta");
//        System.err.println("To BE VERIFIED, switched to calling a static method: FastaOps.sequencesFromFasta(\"temp.fasta\")");
        //or read standard out?
        //      ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //      System.setOut(new PrintStream(baos));
        //      Then you can get the string with baos.toString().
        //      To specify encoding (and not rely on the one defined by the platform), use the PrintStream(stream, autoFlush, encoding) constructor, and baos.toString(encoding)
        //      If you want to revert back to the original stream, use
        //          new PrintStream(new FileOutputStream(FileDescriptor.out))

        return completeSubjectSequences;
    }
    
        /**
     * Given the xml results of blastn search against IWGS, retrieve complete
     * subject/hit sequences of interest (as opposed to their aligned fragments
     * available in xml)
     *
     * temp: just reading in from file
     *
     * @return
     */
    public static ArrayList<Sequence> getCompleteSubjectSequence(String identifierString, String databaseString) {
        //temp: just read in from file
        //  ArrayList<Sequence> completeSubjectSequences = sequencesFromFasta("retrieved_complete_sequences.fasta");
        // e.g.     blastdbcmd -db IWGSC/iwgsc_all -entry 2AL_6391883 > retrived.fasta 
        String tmp = "/tmp/"+identifierString+"_"+Math.random()+".fasta";
        String c = "blastdbcmd -db "+databaseString+" -entry " + identifierString + " -out "+tmp;
        String cmd[] = c.split(" "); //{"blastx", "-seg no -query ogihara_good_beheaded_10_test -remote -db nr -evalue 1e-50 -out ogihara_good_beheaded_10_test.blasted.xml -max_target_seqs 3 -outfmt 5"};
        Process proc = reusable.ExecProcessor.execute(cmd);
        
        ArrayList<Sequence> completeSubjectSequences = FastaOps.sequencesFromFasta(tmp);
        
        String cmd2[] = {"rm", tmp};
        reusable.ExecProcessor.execute(cmd2);
        
//        System.err.println("To BE VERIFIED, switched to calling a static method: FastaOps.sequencesFromFasta(\"temp.fasta\")");
        //or read standard out?
        //      ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //      System.setOut(new PrintStream(baos));
        //      Then you can get the string with baos.toString().
        //      To specify encoding (and not rely on the one defined by the platform), use the PrintStream(stream, autoFlush, encoding) constructor, and baos.toString(encoding)
        //      If you want to revert back to the original stream, use
        //          new PrintStream(new FileOutputStream(FileDescriptor.out))

        return completeSubjectSequences;
    }
}
