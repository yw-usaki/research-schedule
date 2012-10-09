package scheduling;

import java.util.Collections;
import java.util.ArrayList;
import scheduling.Tasknode;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 1151118
 */
public class Sort {
    ArrayList list = new ArrayList();
    Tasknode[] result;
    
    Sort(){
    
    }
    
    Sort(int num){
        for(int i = 0; i < num; i++){
            result[i] = new Tasknode();
        }
    }
    
    DAG blsort(DAG taskgraph){
        Tasknode copy = new Tasknode();
        for(int i = 0; i < taskgraph.total_tasks; i++)
            for(int j = i; j < taskgraph.total_tasks; j++)
                if(taskgraph.task[i].bl > taskgraph.task[j].bl){
                    copy = taskgraph.task[i];
                    taskgraph.task[i] = taskgraph.task[j];
                    taskgraph.task[j] = copy;
                }
                    
        return taskgraph;
    }
    
}
