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
package beans;

import poplogic.Contig;
import poplogic.Gene;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
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
