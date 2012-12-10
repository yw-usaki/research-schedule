/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author 1151118
 */
public class ExTaskList implements Serializable{
        int ProcID;
        int CoreID;
        String state = "";
        
        
        
        //このコアで処理するタスクノード群
        LinkedList<Tasknode> ExTaskList;

        public ExTaskList(int ProcID, int CoreID){
            this.ProcID = ProcID;
            this.CoreID = CoreID;
            ExTaskList = new LinkedList<Tasknode>();
        }
        
        public ExTaskList(){
            
        }

        public void outputData(){
            System.out.println("Proc"+ProcID+" Core"+CoreID+ " "+ state);
            for(Tasknode v : ExTaskList)v.output_result();        
        }
        
        public int size(){
            return ExTaskList.size();
        }
}
