/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
/**
 *
 * @author 1151118
 */
public class CalculationUtil {
    ArrayList checkStack = new ArrayList();
    //targetIDで指定したProcまでの通信に必要なHop数を返す
    int searchNode(Processor Proc, int targetID, int counter){
        int ConnectionDiray = 0;
        //System.out.println(Proc.getProcID() + " "+ targetID);
        checkStack.add(Proc.getProcID());
        //System.out.println(checkStack);
        Deque<Integer> hop_queue = new ArrayDeque<Integer>();
        Deque<Processor> bfs_queue = new ArrayDeque<Processor>();
        checkStack.clear();
        bfs_queue.offer(Proc);
        hop_queue.add(0);
        
        while(!bfs_queue.isEmpty()){
            Processor proc = bfs_queue.poll();
            int hop_counter = hop_queue.poll();
            //System.out.println(proc.getProcID()+" hop:"+hop_counter);

            if(proc.getProcID() == targetID)
                return hop_counter;
            for(int i = 0; i < proc.connect_s.size(); i++){
                bfs_queue.offer((Processor)proc.connect_s.get(i));
                if(i == 0)hop_counter++;
                hop_queue.add(hop_counter);
            }
            
        }
        return 0;
    }
    
    boolean checkstack(Processor Proc){
        for(int i = 0; i < checkStack.size(); i++)
            if(Proc.getProcID() == checkStack.get(i))return true;
        return false;
    }
    
}
