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
public class Processor implements Serializable{
    static final boolean printOutFlag = false;
    private int ID;
    private int Core_number;
    private int connect_num = 0;
    private int processorUtilization = 0;
    private boolean sw = false;
    private float addtime = 0;
    float ftime = Float.MAX_VALUE;
    int Counter; //プロセッサのみの番号（スイッチを含まない）
    static int upperRate = 50;
    Core[] Cores;
    ArrayList<Tasknode> restTasks = new ArrayList();
    int[] connect_ID;
    ArrayList connect_ID_r = new ArrayList();
    ArrayList connect_s = new ArrayList();
    ArrayList connect_r = new ArrayList();
    ArrayList<Tasknode> FixExtasks = new ArrayList<Tasknode>();
    boolean[] flagFixExTasks = new boolean[8];
    

    
    Processor(){
        
    }
    
    Processor(int ID,int connect_num, int[] connect_ID){
        this.ID = ID;
        this.connect_num = connect_num;
        this.connect_ID = new int[connect_num];
        this.connect_ID = connect_ID;
        //this.flagFixExTasks = new boolean[8];
        sw = true;
    }
        
    Processor(int ID,int connect_num, int[] connect_ID, int Core_number, float[] frequency, int Counter) {
        this.ID = ID;
        this.connect_num = connect_num;
        this.Counter = Counter;
        this.connect_ID = new int[connect_num];
        this.connect_ID = connect_ID;
        this.Core_number = Core_number;
        //this.flagFixExTasks = new boolean[Core_number];
        this.Cores = new Core[Core_number];
        for(int i = 0; i < this.Core_number; i++){
            Cores[i] = new Core(i, frequency);
            this.flagFixExTasks[i] = true;
        }
    }
    
    void setProcessor(Processor proc){
        this.ID = proc.ID;
        this.Core_number = proc.Core_number;
        this.connect_num = proc.connect_num;
        this.processorUtilization = proc.processorUtilization;
        this.sw = proc.sw;
        this.addtime = proc.addtime;
        this.Counter = proc.Counter;
        this.upperRate = proc.upperRate;
        this.Cores = new Core[Core_number];
        for(int i = 0; i < Core_number; i++){
            this.Cores[i] = proc.Cores[i].Clone();
        }
        this.restTasks.clear();
        for(int i = 0; i < proc.restTasks.size(); i++){
            this.restTasks.add(proc.restTasks.get(i));
        }
        this.connect_ID = new int[connect_num];
        this.connect_s.clear();
        this.connect_r.clear();
        this.connect_ID_r.clear();
        for(int i = 0; i < proc.connect_num; i++){
            this.connect_ID[i] = proc.connect_ID[i];
        }
        for(int i = 0; i < proc.connect_s.size(); i++){
            this.connect_s.add(proc.connect_s.get(i));
        }
        for(int i = 0; i < proc.connect_ID_r.size(); i++){
            this.connect_ID_r.add(proc.connect_ID_r.get(i));
            this.connect_r.add(proc.connect_r.get(i));
        }
        if(!getSw()){
            for(int i = 0; i < proc.flagFixExTasks.length; i++){
                this.flagFixExTasks[i] = proc.flagFixExTasks[i];
            }
        }
    }
    
    int getCoreID(int i){
        return Cores[i].getCoreID();
    }
    int getProcID(){
        return ID;
    }

    int SerchCoreIDonExeTask(int TaskID){
        int CoreID;
        for(CoreID = 0; CoreID < getCore_number(); CoreID++)
            if(Cores[CoreID].SearchExeTask(TaskID)){
//                System.out.println("Execute task"+TaskID+" on Core" + CoreID);
                return CoreID;
            }
        
        return -1;
    }
   

    void outputProcData(){
        System.out.print("ProcID = "+ID+"Connection number ="+connect_num +" Core number = "+getCore_number());
        if(this.getSw())System.out.print(" this is SW");
        System.out.print("\n Connection:");
        for(int i = 0;i < connect_ID.length; i++)
            System.out.print(connect_ID[i]+ " ");
        System.out.print("connect_s.size="+connect_s.size()+"\n connect  s:");
        for(int i = 0;i < connect_s.size(); i++)
            System.out.print(((Processor)connect_s.get(i)).getProcID() + " ");
        System.out.print("\n Connection:");
        for(int i = 0;i < connect_ID_r.size(); i++)
            System.out.print(connect_ID_r.get(i) + " ");
        System.out.print("\n connect  r:");
        for(int i = 0;i < connect_ID_r.size(); i++)
            System.out.print(((Processor)connect_r.get(i)).getProcID() + " ");
        System.out.print("\n");

        for(int i = 0;i < getCore_number(); i++){
            System.out.print("  ");
            Cores[i].outputCoreData();
        }
    }

    void redivideTask(DAG taskGraph, Proclist Plist, float dividetime){
        for(int i = 0; i < Cores.length; i++){
            if(Cores[i].CheckUsed(dividetime)){
                
                if(Cores[i].ExTaskIndex >= 0)Cores[i].redivideTask(taskGraph, Plist, dividetime);
            }
        }
    }
    
    void fixOfTBHT(DAG taskGraph, Proclist Plist, float stime, float etime, float weight, boolean flag){
        int UtilizationOfProcessor;
        int[] ExtaskIndex = new int[Core_number];
        boolean[] inRestTasks = new boolean[Core_number];
        float currentFrequency = 0;
        float oldcurrentFrequency = 0;
        float Exweight = 0;
        float Stime = findEarliestTaskStartTime(flag);
        
        boolean ExFlag = false;
        ArrayList<Tasknode> restTasks = new ArrayList<Tasknode>();
        FixofTurboBoostandHyperThreading fixTBHT = new FixofTurboBoostandHyperThreading();
        if(flag)System.out.println("in processor" +getProcID()+" fixOfTBHT stime "+Stime +" weight "+ weight+" currentWeight "+ Exweight);
        CalculationUtil calUtil = new CalculationUtil();
        ftime = Float.MAX_VALUE;
        //initialization
        for(int i = 0; i < Core_number; i++){
            inRestTasks[i] = true;
        }
        
        //etime = calUtil.carryUp100(etime);
        UtilizationOfProcessor = this.ProcessorUtilization(Stime);
        //UtilizationOfProcessor = restTasks.size() <= 0 ?  0 : restTasks.size() -1;
        currentFrequency = Cores[0].getFrequency(UtilizationOfProcessor);
        oldcurrentFrequency = currentFrequency;
        
        if(taskGraph.total_tasks < 60)redivideTask(taskGraph, Plist, Stime);
        
        //start タスクへの影響の有無を調べ，影響ありならresttasksに追加
        for(int coreI = 0; coreI < Core_number; coreI++){
            if(inRestTasks[coreI]){
                if(Cores[coreI].findExeTask(Stime)){
                    if(flag)System.out.println("add resttasks with task" + Cores[coreI].getExecuteTask(Cores[coreI].ExTaskIndex).getID());
                    restTasks.add(Cores[coreI].getExecuteTask(Cores[coreI].ExTaskIndex));
                    ExtaskIndex[coreI] = Cores[coreI].getExecuteTask(Cores[coreI].ExTaskIndex).getID();
                    inRestTasks[coreI] = false;
                    ExFlag = true;
                }
            }
        }
        

        
           
        
        for(;; Stime+=0.1){
            //プロセッサの使用率と動作周波数を決定
            Stime = calUtil.carryUp10(Stime);
            
            //start タスクへの影響の有無を調べ，影響ありならresttasksに追加
            for(int coreI = 0; coreI < Core_number; coreI++){
                if(inRestTasks[coreI]){
                    if(Cores[coreI].findExeTask(Stime)){
                        if(flag)System.out.println("add resttasks with task" + Cores[coreI].getExecuteTask(Cores[coreI].ExTaskIndex).getID());
                        restTasks.add(Cores[coreI].getExecuteTask(Cores[coreI].ExTaskIndex));
                        ExtaskIndex[coreI] = Cores[coreI].getExecuteTask(Cores[coreI].ExTaskIndex).getID();
                        inRestTasks[coreI] = false;
                        ExFlag = true;
                    }
                }
            }
            UtilizationOfProcessor = this.ProcessorUtilization(Stime);
            //UtilizationOfProcessor = restTasks.size() <= 0 ?  0 : restTasks.size() -1;
            currentFrequency = Cores[0].getFrequency(UtilizationOfProcessor);

            //end タスクへの影響の有無を調べ，影響ありならresttasksに追加
            if(restTasks.size() != 0){
                if(flag){
                    System.out.print("†††††††††† stime "+String.format("%4.2f", Stime) + " currentWeight "+ Exweight +" limitweight "+ String.format(" %4.2f", weight) + " currentFreq "+ currentFrequency+" ExTaskIndex:");
                    for(int x : ExtaskIndex)
                        System.out.print(" "+x);
                    System.out.println();
                }
                
                if(fixTBHT.compareTaskWeight(restTasks, Exweight)){
                    if(flag)System.out.println("exWeight = "+Exweight);
                    break;
                }
                
                //動作周波数の変更が行われた場合の処理
                if(oldcurrentFrequency != currentFrequency ){
                    //これまでの変更された情報の保存
                    if(flag)System.out.println("old freq "+ oldcurrentFrequency +" currentfreq "+ currentFrequency);
                    break;
                }
                
                //一つ前の動作周波数を保存
                oldcurrentFrequency = currentFrequency;
                
            }
            Exweight += currentFrequency * 0.1;
            if(Exweight >= weight){
                if(flag)System.out.println("break" +Exweight +" "+ weight);
                break;
            }
        }
        if(restTasks.size() != 0){
            //これまでの変更された情報の保存
            for(int restTaskIndex = 0; restTaskIndex < restTasks.size(); restTaskIndex++){
                if(flag)System.out.println("update");
                fixTBHT.splitTasknode(taskGraph, Plist, restTasks.get(restTaskIndex), Cores[restTasks.get(restTaskIndex).allocate_core_I], Stime, oldcurrentFrequency, flag);
            }

        }
        if(ExFlag)ftime = Stime;
        if(flag)System.out.println("in processor end " +getProcID()+" fixOfTBHT stime "+Stime +" weight "+weight+" ftime "+ftime);
    }
    
    float searchNextStime(){
        float Stime = Float.MAX_VALUE;
        
        for(int i = 0; i < Core_number; i++){
            //System.out.println("corenumber" +Core_number+" i"+i);
            float time = Cores[i].getExTaskStime();
            if(Stime > time)Stime = time;
        }
        
        return Stime;       
    }
    
    float findEarliestTaskFinishTime(){
        float minimumTime = Float.MAX_VALUE;
        int CoreIndex = -1;
        for(int coreI = 0; coreI < this.Core_number; coreI++){
            //System.out.println("core id "+coreI+" Exindex "+Cores[coreI].ExTaskIndex);
            
            if(Cores[coreI].ExTaskIndex != -1){

                float time = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).finish_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 1);
                if(time == -1){
                    int index = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() == 1 ? 0 : Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 2;
                    time = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).finish_time.get(index);
                }
                /*
                System.out.println(
                        "core id "+coreI+
                        " Exindex "+Cores[coreI].ExTaskIndex +
                        " task"+Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).getID()+
                        " ftime"+time);
                        * 
                        */
                if(time < minimumTime && time != -1){
                    //System.out.println("time" + time);
                    minimumTime = time;
                    CoreIndex = coreI;
                }
            }
        }
        
        return minimumTime;
    }

    float findEarliestTaskStartTime(boolean flag){
        float minimumTime = Float.MAX_VALUE;
        int CoreIndex = -1;
        for(int coreI = 0; coreI < this.Core_number; coreI++){
            if(flag)System.out.println("core id "+coreI+" Exindex "+Cores[coreI].ExTaskIndex);
            
            if(Cores[coreI].ExTaskIndex != -1){
                if(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 1) != -1
                        && Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).finish_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).finish_time.size() - 1) == -1){
                    float time = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 1);
                    
                    if(time == -1){
                        int index = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() == 1 ? 0 : Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 2;
                        time = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.get(index);
                    }
                    if(flag){
                        System.out.println(
                                "core id "+coreI+
                                " Exindex "+Cores[coreI].ExTaskIndex +
                                " task"+Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).getID()+
                                " ftime"+time);
                    }
                    if(time < minimumTime && time != -1){
                        if(flag)System.out.println("time" + time);
                        minimumTime = time;
                        CoreIndex = coreI;
                    }
                }
            }
        }
        
        return minimumTime;
    }
    
    
    float findNextSwitchTime(float time, boolean flag){
        float minimumTime = Float.MAX_VALUE;
        int CoreIndex = -1;
        //System.out.println("================== in findNextEndTIme function======================");
        for(int coreI = 0; coreI < this.Core_number; coreI++){
            //System.out.println("core id "+coreI+" Exindex "+Cores[coreI].ExTaskIndex);
            
            if(Cores[coreI].ExTaskIndex != -1){
                //select nest time
                Tasknode task = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex);
                float stime = task.start_time.get(task.start_time.size() - 1);
                float etime = task.finish_time.get(task.finish_time.size() -1);
                /*
                     System.out.println(
                            "core id "+coreI+
                            " Exindex "+Cores[coreI].ExTaskIndex +
                            " task"+task.getID()+
                            " ftime"+time);
                            * 
                            */
                    if(time < minimumTime && stime != -1){
                        //System.out.println("time" + time);
                        minimumTime = stime;
                        CoreIndex = coreI;
                    }
            }
        }
        
        return minimumTime;
    }
    
    float findMinimumTaskWeight(DAG taskGraph, Proclist Plist, float stime, boolean flag){
        float miniWeight = Float.MAX_VALUE;
        int CoreIndex = -1;
        for(int coreI = 0; coreI < this.Core_number; coreI++){
            if(flag)System.out.println("proc "+getProcID()+" core id "+coreI+" Exindex "+Cores[coreI].ExTaskIndex+" stime "+ stime);
            
            if(Cores[coreI].ExTaskIndex != -1){
                int taskI = taskGraph.gettask_i(taskGraph, Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).getID());
                calcStartTime(taskGraph, Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex), Plist, flag);
                if(flag)System.out.print("after calcStartTime ");
                if(flag)Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).output_result();
                if(
                    (Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).finish_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).finish_time.size() - 1) ==  -1)
                        &&
                    (Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 1) !=  -1)
                        &&
                    (Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).start_time.size() - 1) <= stime)
                        ){
                    if(flag){
                        System.out.println(
                                "core id "+coreI+
                                " Exindex "+Cores[coreI].ExTaskIndex +
                                " task "+Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).getID()+
                                " weight "+Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).weight.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).weight.size() - 1));
                    }

                    float weight = Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).weight.get(Cores[coreI].ExecuteTask.get(Cores[coreI].ExTaskIndex).weight.size() - 1);
                    if(flag)System.out.print("weight" + weight);
                    if(weight < miniWeight){
                        if(flag)System.out.print(" update");
                        miniWeight = weight;
                        CoreIndex = coreI;
                    }
                    if(flag)System.out.println();
                }
            }
        }
        
        return miniWeight;
    }
    
    boolean calcStartTime(DAG taskGraph, Tasknode task, Proclist Plist, boolean flag){
        int predIndex;
        int procIndex = task.allocate_proc_I;
        float maxPredFtime = 0;
        float maxProcFtime = 0;
        float commTime = 0;
        CalculationUtil calUtil = new CalculationUtil();
        if(flag)System.out.println("******* calc start time for task"+ task.getID());
        for(int i = 0; i < task.predecessor.size(); i++){
            predIndex = taskGraph.gettask_i(taskGraph, task.predecessor.get(i));
                
            if(flag)taskGraph.task[predIndex].output_result();
            int procPredIndex = Plist.procs[task.allocate_proc_I].Cores[task.allocate_core_I].SearchExePredTaskIndex(task.getID());
            if(flag)System.out.println("procPredIndex "+procPredIndex);
            if(procPredIndex != -1){
                Tasknode procPredTask = Plist.procs[task.allocate_proc_I].Cores[task.allocate_core_I].ExecuteTask.get(procPredIndex);
                if(flag)procPredTask.output_result();
                if(procPredTask.finish_time.get(procPredTask.finish_time.size() -1) == -1){
                    if(flag)System.out.println("proc predecessor is not finished");
                    return false;
                }else{
                    maxProcFtime = procPredTask.finish_time.get(procPredTask.finish_time.size() -1) + procPredTask.communicationTime;
                }
            }
            //親タスクの処理完了時刻が不定であればfalse
            //すでにStartTimeを計算した物もFalse
            if(taskGraph.task[predIndex].finish_time.get(taskGraph.task[predIndex].finish_time.size() -1) == -1 || task.start_time.get(0) != -1){
                if(flag)System.out.println("predecessor is not finished");
                return false;
            }
            else{
                float predTime = taskGraph.task[predIndex].finish_time.get(taskGraph.task[predIndex].finish_time.size() -1);
                if(maxPredFtime == 0)maxPredFtime = predTime;
                if(maxPredFtime < predTime){
                    maxPredFtime = predTime;
                    if(flag)System.out.println("maxpredFtime update: new predFtime"+predTime +" maxpredFtime"+maxPredFtime);
                }
                if(flag)System.out.println("maxPredFtime "+ maxPredFtime);
                if(task.allocate_proc_I != taskGraph.task[predIndex].allocate_proc_I){
                    commTime += calUtil.calculationOfHopCost(Plist.procs[task.allocate_proc_I], Plist.procs[taskGraph.task[predIndex].allocate_proc_I].getProcID(), 0);
                    if(flag)System.out.println(" comm time "+ commTime+" task"+task.getID() +" proc" +task.allocate_proc_I+" predtask"+taskGraph.task[predIndex].getID() +" proc"+taskGraph.task[predIndex].allocate_proc_I);
                }
                if(flag)System.out.println("maxProcFtime "+ maxProcFtime);
            }
        }
        float stime;
        //ルートのデータ入力
        if(task.predecessor.size() == 0){
            task.start_time.set(0, (float)0);
        }else{
            if(maxPredFtime < maxProcFtime)stime = maxProcFtime + commTime;
            else stime = maxPredFtime + commTime;
            task.start_time.set(0, stime);
        }
        if(flag)task.output_result();
        return true;
    }
    
    
    //全てのコアのExTaskIndexが-1であるならTrue
    boolean checkAllExTaskIndex(){
        for(int coreI = 0; coreI < Core_number; coreI++){
            if(Cores[coreI].ExTaskIndex != -1)return false;
        }
        return true;
    }

    boolean checkPred(ArrayList<Tasknode> restTasks, Tasknode currentTask){
        for(int i = 0; i < currentTask.predecessor.size(); i++){
            for(Tasknode x : restTasks){
                if((Integer)currentTask.predecessor.get(i) == x.getID()){
                    //System.out.println("falfalfalfal"+currentTask.getID());
                    return false;
                }
            }
        }
        return true;
    }
     
    private void addTasknode(DAG taskGraph, Tasknode task){
        int index = taskGraph.gettask_i(taskGraph, task.getID());
        taskGraph.task[index].setTasknode(task);
    }
    

    //timeで指定された時間のプロセッサの使用率を返す
    int ProcessorUtilization(float time){
        ArrayList<Integer> coreID = new ArrayList<Integer>();
        int count = -1,num;
        
        for(int i = 0; i < this.Core_number; i++){
            if(this.Cores[i].ExTaskIndex != -1){
                if(this.Cores[i].CheckUsed(time)){
                    //System.out.println("被ってるコア："+ this.Cores[i].getCoreID());
                    coreID.add(Cores[i].getCoreID());
                    count++;
                }
            }
        }
        if(count == -1)count = 0;
        //System.out.print("proc used "+count +" "+ coreID);
        return count;
    }
    
    public boolean getSw() {
        return sw;
    }
    public void setSw(boolean sw) {
        this.sw = sw;
    }
    public int getCore_number() {
        return Core_number;
    }
    public float getAddtime() {
        return addtime;
    }
    public void setAddtime(float addtime) {
        this.addtime = addtime;
    }
    public int getConnect_num() {
        return connect_num;
    }
    public void setConnect_num(int connect_num) {
        this.connect_num = connect_num;
    }
    public void setCore_number(int Core_number) {
        this.Core_number = Core_number;
    }
    public Tasknode getFixExTasks(int index){
        return this.FixExtasks.get(index);
    }
    void addFixExTasks(Tasknode task){
        this.FixExtasks.add(task);
    }
    public void removeFixExTasks(int index){
        this.FixExtasks.remove(index);
    }
    public void outputFixExtasks(){
        for(Tasknode x : FixExtasks){
            System.out.print(" "+ x.getID());
        }
    }
        
}

