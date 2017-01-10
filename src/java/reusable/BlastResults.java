/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reusable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
public class BlastResults implements Serializable {

    private final boolean hasInput;
    private final ArrayList<HitsForQuery> results;
    private Integer exitValue;


    public BlastResults(boolean hasInput, ArrayList<HitsForQuery> results, Integer exitValue) {
        this.hasInput = hasInput;
        this.results = results;
        this.exitValue = exitValue;
    }

    public ArrayList<HitsForQuery> getResults() {
        return results;
    }

    public int size() {
        return results.size();
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public boolean hasInput() {
        return hasInput;
    }
    
    public boolean hasResults() {
        return results != null && !isEmpty();
    }
    
    public boolean hasExitValue() {
        return exitValue != null;
    }

    public Integer getExitValue() {
        return exitValue;
    }
    
    

}
