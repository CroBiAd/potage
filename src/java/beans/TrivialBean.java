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

import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
@ManagedBean(name = "trivialBean")
@RequestScoped
public class TrivialBean implements Serializable {
    
    //move elswhere
    public ArrayList<String> getChromosomes() {
        ArrayList<String> chromosomes = new ArrayList<>();
        String genomes[] = {"A", "B", "D"};
        for (int i = 1; i < 8; i++) {
            for (String g : genomes) {
                String chromosome = i + g;
                chromosomes.add(chromosome);
            }
        }        
        return chromosomes;
    }
    
//    public ArrayList<Double> getValues() {
//        ArrayList<Double> vals = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            vals.add(Math.random());
//        }        
//        return vals;
//    }
}
