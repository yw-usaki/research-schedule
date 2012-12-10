/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author 1151118
 */
public class Core implements Serializable{
    static final boolean printOutFlag = false;
    private int ID;
    private float endTime = 0;
    private float exe_frequency;
    private float[] frequency;
    int ExTaskIndex = -1;   //-1であるならこのコアで実行するタスクが無い　or　TBHTの調節が全て終わっている　それ以外の値ならこれから調節されるタスク群の先頭を示す
    ArrayList<Tasknode> ExecuteTask = new ArrayList<Tasknode>();
     
    Core(int ID, float[] freq){
       // System.out.println("create core");
        this.ID = ID;
        frequency = new float[freq.length];
        frequency = freq;
        exe_frequency = 0;
    }
    
    Core Clone(){
        Core clone = new Core(this.ID, this.frequency);
        clone.exe_frequency = this.exe_frequency;
        clone.endTime = this.endTime;
        clone.ExTaskIndex = this.ExTaskIndex;
        for(int i = 0; i < ExecuteTask.size(); i++){
            clone.ExecuteTask.add(this.ExecuteTask.get(i));
        }
        return clone;
    }
    
    float finishTimeOfPredExTask(int index){
        return ExecuteTask.get(index - 1).finish_time.get(ExecuteTask.get(index - 1).finish_time.size());
    }
    
    float getExTaskStime(){
        float Stime = Float.MAX_VALUE;
        if(ExTaskIndex != -1){
            for(int i = 0; i < ExecuteTask.get(ExTaskIndex).start_time.size(); i++){
                if(ExecuteTask.get(ExTaskIndex).start_time.get(i) != -1 && ExecuteTask.get(ExTaskIndex).finish_time.get(i) == -1){
                    Stime = ExecuteTask.get(ExTaskIndex).start_time.get(i);
                }
            }
        }
        return Stime;
    }
    
    //task_IDで示したタスクが後続関係にある他のタスクに対してこのコアから別のコアに対してデータを転送している間の時間Time を加算し，このコアで実行される後続タスクについても処理時間の調節を行う．
    void addCommunicationCost(DAG taskGraph, int task_ID, float time){
        int flag = 0;
        int index;
        float interval = 0;
        
        //System.out.println("in add CommunicationCost task "+ task_ID);
        
        for(int i = 0; i < ExecuteTask.size(); i++){
            //通信時間の加算
            if(flag == 1){
                
                interval = ExecuteTask.get(i).start_time.get(0) - ExecuteTask.get(i-1).finish_time.get(0);
                if(interval < time){
                    time -= interval;
                    //Tasknode comm = new Tasknode(0, 0, time);
                    //ExecuteTask.add(i, comm);
                    ExecuteTask.get(i).start_time.set(0, ExecuteTask.get(i).start_time.get(0) + time);
                    ExecuteTask.get(i).finish_time.set(0, ExecuteTask.get(i).start_time.get(0) + ExecuteTask.get(i).working_time.get(0));
                    ExecuteTask.get(i).communicationTime = time;//ExecuteTask.get(i).finish_time.get(0);
                    index = taskGraph.gettask_i(taskGraph, ExecuteTask.get(i).getID());
                    taskGraph.task[index].start_time.set(0, ExecuteTask.get(i).start_time.get(0));
                    taskGraph.task[index].finish_time.set(0, ExecuteTask.get(i).finish_time.get(0));
                    if(printOutFlag){
                        System.out.println("adjuster in Core class::start time="+ ExecuteTask.get(i).start_time.get(0) +" finish time="+ ExecuteTask.get(i).finish_time.get(0));
                        System.out.println("adjuster in Core class::start time="+ taskGraph.task[index].start_time.get(0) +" finish time="+ taskGraph.task[index].finish_time.get(0) +" ID="+taskGraph.task[index].getID());
                    
                    }
                    //min = ((Tasknode)ExecuteTask.get(i)).getStart_time();
                }
                /*
                ExecuteTask.get(i).start_time.set(0, ExecuteTask.get(i - 1).finish_time.get(0) + ExecuteTask.get(i - 1).communicationTime);
                ExecuteTask.get(i).finish_time.set(0, ExecuteTask.get(i).start_time.get(0) + ExecuteTask.get(i).working_time.get(0));
                * 
                */
                flag = 2;
            }
            //後続タスクの処理時間を調節
            if(flag == 2){
                    if(ExecuteTask.get(i).start_time.get(0) < ExecuteTask.get(i - 1).finish_time.get(0)){
                        ExecuteTask.get(i).start_time.set(0, ExecuteTask.get(i - 1).finish_time.get(0));
                        ExecuteTask.get(i).finish_time.set(0, ExecuteTask.get(i).start_time.get(0) + ExecuteTask.get(i).working_time.get(0));
                    }
            }
            //ExecuteTask内の該当ノードのインデックスを見つける
            if(ExecuteTask.get(i).getID() == task_ID){
                //System.out.println("ExecuteTask.get(i).getID "+ ExecuteTask.get(i).getID() +" commtime "+ ExecuteTask.get(i).communicationTime);
                ExecuteTask.get(i).communicationTime = time;
                taskGraph.task[taskGraph.gettask_i(taskGraph, task_ID)].communicationTime = ExecuteTask.get(i).communicationTime;
                flag =1;
            }
        }
        setEndTime(ExecuteTask.get(ExecuteTask.size()-1).finish_time.get(0));
        if(printOutFlag){
            System.out.print("CoreID="+getCoreID());
            System.out.println(" Procの終了時間を更新　　 set end time in method addCommunicationCost  "+ getEndTime()+" taskID="+ ExecuteTask.get(ExecuteTask.size()-1).getID());
        }
        //return min;
    }
    
    void nomalAdjuster(Tasknode task){
        int flag = 0;
        DAG taskgraph = new DAG();
        Tasknode succtask;
        
        if(printOutFlag)
            System.out.println("in method normal ajuster");
        for(int i = 0; i < ExecuteTask.size(); i++){
            if(flag == 1){
                if(printOutFlag)
                    System.out.println("start time ="+ ExecuteTask.get(i).start_time.get(0) + " pred task finish Time ="+ ExecuteTask.get(i - 1).finish_time.get(0));   
                if(ExecuteTask.get(i).start_time.get(0) < ExecuteTask.get(i - 1).finish_time.get(0)){
                    ExecuteTask.get(i).start_time.set(0, ExecuteTask.get(i - 1).finish_time.get(0));
                    ExecuteTask.get(i).finish_time.set(0, ExecuteTask.get(i).start_time.get(0) + ExecuteTask.get(i).working_time.get(0));
                }
            }
            if(task.getID() == ((Tasknode)ExecuteTask.get(i)).getID())
                ExecuteTask.set(i, task);
                flag = 1;
        }
    }
    

    boolean SearchExeTask(int taskID){
        int t_ID;
        for(int i=0; i<ExecuteTask.size(); i++)
            if((t_ID = ExecuteTask.get(i).getID()) == taskID)return true;
        return false;       
    }
    int SearchExeTaskIndex(int taskID){
        int index = -1;
        for(int i = 0; i < ExecuteTask.size(); i++){
            if(ExecuteTask.get(i).getID() == taskID)
                index = i;
        }
        return index;
    }
    
    int SearchExePredTaskIndex(int taskID){
        int index = -1;
        for(int i = 0; i < ExecuteTask.size(); i++){
            if(ExecuteTask.get(i).getID() == taskID)
                index = i - 1;
        }
        return index;
    }
    
    /*
    boolean findExeTask(float time){
        for(int index =ExTaskIndex; index < ExecuteTask.size(); index++){
            for(int i = 0; i < ExecuteTask.get(index).start_time.size(); i++){
                if(ExecuteTask.get(index).start_time.get(i) <= time && time <= ExecuteTask.get(index).finish_time.get(i)){
                    return true;
                }
            }
        }
        return false;
    }
    * 
    */
    
    /*
    boolean findExeTask(float time){
        if(ExTaskIndex != -1){
            for(int i = 0; i < ExecuteTask.get(ExTaskIndex).start_time.size(); i++){
                if(ExecuteTask.get(ExTaskIndex).start_time.get(i) <= time && time <= ExecuteTask.get(ExTaskIndex).finish_time.get(i)){
                    ExecuteTask.get(ExTaskIndex).output_result();
                    System.out.print("return true\n");
                    return true;
                }
            }
        }
        return false;
    }
    * 
    */
    
    boolean findExeTask(float stime){
        if(ExTaskIndex != -1){
            if(ExecuteTask.get(ExTaskIndex).finish_time.get(ExecuteTask.get(ExTaskIndex).finish_time.size() - 1) == -1 && 
               ExecuteTask.get(ExTaskIndex).start_time.get(ExecuteTask.get(ExTaskIndex).start_time.size() - 1) != -1 &&
               ExecuteTask.get(ExTaskIndex).start_time.get(ExecuteTask.get(ExTaskIndex).start_time.size() - 1) <= stime){
                //System.out.print("return true\n");
                return true;
            }
        }
        return false;
    }

    void outputCoreData(){
        System.out.print("CoreID = " +getCoreID()+ " endtime = "+getEndTime()+" ExTaskIndex = "+ExTaskIndex+" frequency:");
        for(int i = 0; i < frequency.length; i++)
            System.out.print(getFrequency(i)+" ");
        System.out.print("\nExtaskID: ");
        for(Tasknode x : ExecuteTask){
            System.out.print(x.getID()+ " ");
        }
        System.out.println();
    }
    //入力された時間帯にコアが使用されているかを判断
    boolean CheckUsed(float time){
        for(int i = 0; i < ExecuteTask.size(); i++){
            Tasknode Extask = ExecuteTask.get(i);
            if(Extask.start_time.get(0) != -1)
                if((Extask.start_time.get(0) <= time &&  time < Extask.finish_time.get(Extask.finish_time.size() -1))
                        ||
                    (Extask.start_time.get(0) <= time && Extask.finish_time.get(Extask.finish_time.size() -1) == -1))
                    return true; 
        }
        return false;
    }
    
    
    void redivideTask(DAG taskGraph, Proclist Plist, float time){
        float reWork;
        CalculationUtil calUtil = new CalculationUtil();
        
        for(int index = ExTaskIndex - 1 < 0 ? 0 : ExTaskIndex - 1; index <= ExTaskIndex; index++){
            Tasknode task = ExecuteTask.get(index);
            if(task.start_time.get(0) != -1)
                if((task.start_time.get(0) <= time &&  time < task.finish_time.get(task.finish_time.size() -1))
                        ||
                    (task.start_time.get(0) <= time && task.finish_time.get(task.finish_time.size() -1) == -1)){
                    for(int cv = 0; cv < task.start_time.size(); cv++){
                        //System.out.println("cv "+ cv + " task "+ task.getID() +" start step "+ task.start_time.size());
                        if(task.start_time.get(cv) <= time && time < task.finish_time.get(cv)){
                            System.out.print("true "+ task.finish_time.get(cv) +" dividetime:"+ time);
                                //timeの該当する区間がまだ未確定である場合の処理
                                if(task.finish_time.get(cv) == -1){
                                    System.out.println(" unclear");

                                //timeの該当する区間がタスクの終わり部分の場合
                                }else if(cv  == task.start_time.size() - 1){
                                    System.out.println(" last");
                                    reWork = calUtil.carryUp1((task.finish_time.get(cv) - time) * task.ExFrequency.get(cv));
                                    if(reWork != 0){
                                        task.finish_time.set(cv, time);
                                        task.working_time.set(cv, time - task.start_time.get(cv));
                                        task.weight.set(cv, task.weight.get(cv) - reWork);
                                        task.start_time.add(time);
                                        task.working_time.add((float)0);
                                        task.finish_time.add((float)-1);
                                        task.ExFrequency.add((float)2);
                                        task.weight.add(reWork);
                                        ExTaskIndex = SearchExeTaskIndex(task.getID());
                                        for(int nn = 0; nn < task.successor.size(); nn++){
                                            int succI = taskGraph.gettask_i(taskGraph, task.successor.get(nn));
                                            taskGraph.task[succI].start_time.set(0, (float)-1);
                                            taskGraph.task[succI].working_time.set(0, (float)-1);
                                            taskGraph.task[succI].finish_time.set(0, (float)-1);
                                            if(taskGraph.task[succI].start_time.size() > 1)
                                            for(int nnn = 1; nnn < taskGraph.task[succI].start_time.size(); nnn++){
                                                taskGraph.task[succI].weight.set(0, taskGraph.task[succI].weight.get(0) + taskGraph.task[succI].weight.get(nnn));
                                                taskGraph.task[succI].removeParameterAllTimeWeightExfreq(nnn);

                                            }
                                            int succIndexOnCore = Plist.procs[taskGraph.task[succI].allocate_proc_I].Cores[taskGraph.task[succI].allocate_core_I].SearchExeTaskIndex(taskGraph.task[succI].getID());
                                            Plist.procs[taskGraph.task[succI].allocate_proc_I].Cores[taskGraph.task[succI].allocate_core_I].ExecuteTask.set(succIndexOnCore, taskGraph.task[succI]);
                                            //Plist.procs[taskGraph.task[succI].allocate_proc_I].Cores[taskGraph.task[succI].allocate_core_I].ExTaskIndex = Plist.procs[taskGraph.task[succI].allocate_proc_I].Cores[taskGraph.task[succI].allocate_core_I].SearchExeTaskIndex(taskGraph.task[succI].getID());
                                        }
                                    }
                                    int CoreExTaskI = SearchExeTaskIndex(task.getID());
                                    int CoreNextExTaskI = CoreExTaskI+ 1 == ExecuteTask.size() ? -1 : CoreExTaskI + 1;
                                    if(CoreNextExTaskI != -1){
                                        ExecuteTask.get(CoreNextExTaskI).start_time.set(0, (float)-1);
                                        ExecuteTask.get(CoreNextExTaskI).recheckFlag = true;
                                    }
                                    
                                    //task.output_result();
                                    break;
                                //timeの該当する区間がタスクの途中である場合の処理
                                //該当するtimeで分割し，その後を未確定にする
                                }else{
                                    System.out.println(" else");
                                    reWork = calUtil.carryUp1((task.finish_time.get(cv) - time) * task.ExFrequency.get(cv));
                                    if(reWork != 0){
                                        task.finish_time.set(cv, time);
                                        task.working_time.set(cv, time - task.start_time.get(cv));
                                        task.weight.set(cv, task.weight.get(cv) - reWork);
                                        if(cv + 2 < task.weight.size())
                                            for(int mm = cv + 2; mm < task.weight.size();){
                                                //System.out.println("hyohyohyo "+mm);
                                                reWork += task.weight.get(mm);
                                                task.start_time.remove(mm);
                                                task.working_time.remove(mm);
                                                task.finish_time.remove(mm);
                                                task.weight.remove(mm);
                                                task.ExFrequency.remove(mm);
                                            }
                                        task.start_time.set(cv + 1, time);
                                        task.working_time.set(cv + 1, (float)0);
                                        task.finish_time.set(cv + 1, (float)-1);
                                        task.ExFrequency.set(cv + 1, (float)2);
                                        task.weight.set(cv + 1, task.weight.get(cv + 1) + reWork);
                                        ExTaskIndex = SearchExeTaskIndex(task.getID());
                                        
                                        for(int nn = 0; nn < task.successor.size(); nn++){
                                            int succI = taskGraph.gettask_i(taskGraph, task.successor.get(nn));
                                            taskGraph.task[succI].start_time.set(0, (float)-1);
                                            taskGraph.task[succI].working_time.set(0, (float)-1);
                                            taskGraph.task[succI].finish_time.set(0, (float)-1);
                                            if(taskGraph.task[succI].start_time.size() > 1)
                                            for(int nnn = 1; nnn < taskGraph.task[succI].start_time.size(); nnn++){
                                                taskGraph.task[succI].weight.set(0, taskGraph.task[succI].weight.get(0) + taskGraph.task[succI].weight.get(nnn));
                                                taskGraph.task[succI].removeParameterAllTimeWeightExfreq(nnn);

                                            }
                                            int succIndexOnCore = Plist.procs[taskGraph.task[succI].allocate_proc_I].Cores[taskGraph.task[succI].allocate_core_I].SearchExeTaskIndex(taskGraph.task[succI].getID());
                                            Plist.procs[taskGraph.task[succI].allocate_proc_I].Cores[taskGraph.task[succI].allocate_core_I].ExecuteTask.set(succIndexOnCore, taskGraph.task[succI]);
                                        }
                                        
                                    }
                                    int CoreExTaskI = SearchExeTaskIndex(task.getID());
                                    int CoreNextExTaskI = CoreExTaskI+ 1 == ExecuteTask.size() ? -1 : CoreExTaskI + 1;
                                    if(CoreNextExTaskI != -1){
                                        ExecuteTask.get(CoreNextExTaskI).start_time.set(0, (float)-1);
                                        ExecuteTask.get(CoreNextExTaskI).recheckFlag = true;
                                    }
                                    //task.output_result();
                                    break;
                                }
                        }
                    }
             }
                    
        }
    }

    Tasknode getExecuteTaskNode(int ID){
        for(int i = 0; i < ExecuteTask.size(); i++)
            if(getExecuteTask(i).getID() == ID)
                return getExecuteTask(i);
        return null;                           
    }
    /**
     * @return the ExecuteTask
     */
    public Tasknode getExecuteTask(int i) {
        return ExecuteTask.get(i);
    }

    /**
     * @param ExecuteTask the ExecuteTask to set
     */
    public void setExecuteTask(Tasknode ExecuteTask) {
        //System.out.println("set ex_task on Core"+this.getCoreID() + " task"+ ExecuteTask.getID());
        this.ExecuteTask.add(ExecuteTask);
        if(ExTaskIndex == -1){
            //System.out.println("!!!!! set ExTaskIndex = 0;");
            ExTaskIndex = 0;
        }
    }

    /**
     * @return the exe_frequency
     */
    public float getExe_frequency() {
        return exe_frequency;
    }
    
    /**
     * @return the ID
     */
    public int getCoreID() {
        return ID;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(float[] frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the endTime
     */
    public float getEndTime() {
        return endTime;
    }

    /*
     * @param endTime the endTime to set
     */
    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }
    float getFrequency(int i){
        //ystem.out.println("in method getFrequency i="+i);
        if(i < 0)i =0;
        return frequency[i];
    }
    int getFrequencyLength(){
        return frequency.length;
    }
    
}