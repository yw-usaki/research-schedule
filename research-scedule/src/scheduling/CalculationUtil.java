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
    static final boolean printOutFlag = false;
    ArrayList checkStack = new ArrayList();
    
    
    //targetIDで指定したProcまでの通信に必要なHop数を返す
    int calculationOfHopCost(Processor Proc, int targetID, int counter){
        int ConnectionDiray = 0;
        //if(printOutFlag)
           // System.out.println(Proc.getProcID() + " "+ targetID);
        checkStack.add(Proc.getProcID());
        //if(printOutFlag)
            //System.out.println(checkStack);
        Deque<Integer> hop_queue = new ArrayDeque<Integer>();
        Deque<Processor> bfs_queue = new ArrayDeque<Processor>();
        checkStack.clear();
        bfs_queue.offer(Proc);
        hop_queue.add(0);
        
        while(!bfs_queue.isEmpty()){
            Processor proc = bfs_queue.poll();
            int hop_counter = hop_queue.poll();
            //if(printOutFlag)
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
    
    float carryUp1(float value){
        if(value == 0)return 0;
        value = (float)((int)(value + 0.5));
        return value;
    }

    float carryUp10(float value){
        if(value == 0)return 0;
        value = (float)((int)(value * 10 + 0.5)) / 10;
        return value;
    }
    
    float carryUp100(float value){
        if(value == 0)return 0;
        value = (float)((int)(value * 100 + 0.5)) / 100;
        return value;
    }
    
    float carryUp100000(float value){
        if(value == 0)return 0;
        value = (float)((int)(value * 100000 + 5)) / 100000;
        return value;
    }
    /*
    void checkTaskWeight(Tasknode task){
        float removeTime = 0;
        float overWeight = 0;
        float sum = 0;
        boolean flag = false;
        
        for(int i = 0; i < task.executeFrequencyStep.size(); i++){
            if(flag){
                removeTime += 1 / task.UpperRate;
                task.executeFrequencyStep.remove(i);
            }else{
                if((overWeight = task.weight.get(0) - sum) < (task.executeFrequencyStep.get(i) / task.UpperRate)){
                    task.executeFrequencyStep.set(i, task.weight.get(0) - sum);
                    flag = true;
                }
                sum += task.executeFrequencyStep.get(i) / task.UpperRate;
            }
        }
    }
    
    * 
    */
    //残りのタスクを逐次実行した場合の処理時間と通信時間の合計を計算し，格納
    void calculateRestTasksProcessingTime(DAG TaskGraph, Proclist Plist, int currentTaskI){
        int ProcIndex = -1;
        float communicationTime;
        float communicationTimeSub = Float.MAX_VALUE;
        
        float latestCoreEndTime = 0;
        float latestProcEndTime = 0;
        
        for(int i = 1; i < Plist.procnum; i++){
            if(!Plist.procs[i].getSw()){
                if(ProcIndex == -1)ProcIndex = i;
                for(int j = 0; j < Plist.procs[i].getCore_number(); j++){
                    float time = Plist.procs[i].Cores[j].getEndTime();
                    if(latestCoreEndTime < time){
                        latestCoreEndTime = time;
                    }
                }
                if(latestProcEndTime < latestCoreEndTime){
                    latestProcEndTime = latestCoreEndTime;
                }
            }
        }
        if(printOutFlag)
            System.out.println(currentTaskI+"only latest processing time="+ latestCoreEndTime);
        TaskGraph.restTasksProcessingTime = latestCoreEndTime;
        TaskGraph.restTasksProcessingTime += calculateRestTotalTaskWeights(TaskGraph) / Plist.procs[ProcIndex].Cores[0].getFrequency(0);
        if(printOutFlag)
            System.out.println("latest processing time  and  rest tasks total weight="+ TaskGraph.restTasksProcessingTime
                    +" ("+calculateRestTotalTaskWeights(TaskGraph) / Plist.procs[ProcIndex].Cores[0].getFrequency(0)
                    +" (weight="+calculateRestTotalTaskWeights(TaskGraph)
                    );
        for(; ProcIndex < Plist.procnum; ProcIndex++){
            communicationTime = 0;
            for(int i = 0; i <= currentTaskI; i++){
                if(i < TaskGraph.total_tasks)
                for(int j = 0; j < TaskGraph.task[i].successor.size(); j++){
                    //saccessorが未割当である場合，これから起こるであろう通信遅延を追加
                    int successorI = TaskGraph.gettask_i(TaskGraph, TaskGraph.task[i].successor.get(j));
                    if((TaskGraph.task[successorI].working_time.get(0) == 0) && (TaskGraph.task[i].allocate_proc_I != -1)){
                        if(printOutFlag)
                            System.out.println(i + "taskftagadfga = "+ TaskGraph.task[i].getID());
                        communicationTime += calculationOfHopCost(Plist.procs[TaskGraph.task[i].allocate_proc_I], Plist.procs[ProcIndex].getProcID(), 0);
                    }
                }
            }
            if(communicationTime < communicationTimeSub)
                communicationTimeSub = communicationTime;
        }
        TaskGraph.restTasksProcessingTime += communicationTimeSub;
        if(printOutFlag)
            System.out.println("latest processing time  and  rest tasks total weight  and  communication time ="+ TaskGraph.restTasksProcessingTime+" ("+communicationTimeSub);
    }
    
    int searchLatestEndTaskID(DAG TaskGraph, float currentTaskFinishTime){
        float MaxFinishTime = currentTaskFinishTime;
        int index = -1;
        for(Tasknode x : TaskGraph.task){
            if(MaxFinishTime <= x.finish_time.get(0)){
                MaxFinishTime = x.finish_time.get(0);
                index = x.getID();
            }
        }
        return index;
    }
    
    //TaskGraph全体の残り処理量を計算してかえす
    float calculateRestTotalTaskWeights(DAG TaskGraph){
        float totalWeight = 0;
        for(Tasknode x : TaskGraph.task){
            if(x.working_time.get(0) == 0)
                totalWeight += x.weight.get(0);
        }
        return totalWeight;
    }
}
