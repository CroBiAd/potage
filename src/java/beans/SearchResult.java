/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import poplogic.Contig;
import poplogic.Gene;

/**
 *
 * @author rad
 */
public class SearchResult {
    private final Gene gene;
    private final Contig contig;
    private final String chromosome;
    private final Integer index;

    public SearchResult(Gene gene, Contig contig, String chromosome, int index) {
        this.gene = gene;
        this.contig = contig;
        this.chromosome = chromosome;
        this.index = index;
    }

    public Gene getGene() {
        return gene;
    }

    public Contig getContig() {
        return contig;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Integer getIndex() {
        return index;
    }
    
    
}
