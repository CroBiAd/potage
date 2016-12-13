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
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
@ManagedBean(name = "randomBean")
@ViewScoped
public class RandomChoiceBean  implements Serializable{
    Random generator;

    public RandomChoiceBean() {        
        generator = new Random();
    }

    public Integer getRandom(int max) {
        return generator.nextInt(max);
    }
 
    public String getRandomFilename() {
        StringBuilder sb = new StringBuilder("");
        
//        for (int j = 0; j < 10000; j++) {
//            System.out.print((char)(48+generator.nextInt(10)));
//            System.out.print((char)(65+generator.nextInt(26)));
//            if(j % 50 == 0)
//                System.out.println();
//                
//        }
        for (int i = 0; i < 4; i++) {            
            sb.append((char)(48+generator.nextInt(10)));
            sb.append((char)(65+generator.nextInt(26)));
        }
        return sb.toString();
        
    }
    
}
