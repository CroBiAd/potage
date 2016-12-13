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
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Radoslaw Suchecki <radoslaw.suchecki@adelaide.edu.au>
 */
@ManagedBean(name = "effectsBean")
@RequestScoped
public class EffectsBean implements Serializable{
    private String effectType;

    public EffectsBean() {
        String arr[] = {"fade","slide","blind","clip","drop","explode","fold","puff","scale"};
        Random generator = new Random();
        effectType = arr[generator.nextInt(arr.length)];
    }

    public String getEffectType() {
        return effectType;
    }
 
    
    
}
