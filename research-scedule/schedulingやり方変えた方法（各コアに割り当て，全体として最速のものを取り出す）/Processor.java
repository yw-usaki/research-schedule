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
    private int ID;
    private int Core_number;
    private int connect_num = 0;
    private int processorUtilization = 0;
    private int sw = 0;
    private double addtime = 0;
    int Counter; //プロセッサのみの番号（スイッチを含まない）
    static int upperRate = 50;
    Core[] Cores = new Core[4];
    ArrayList<Tasknode> restTasks = new ArrayList();
    int[] connect_ID;
    ArrayList connect_ID_r = new ArrayList();
    ArrayList connect_s = new ArrayList();
    ArrayList connect_r = new ArrayList();
    
    Processor(){
        
    }
    
    Processor(int ID,int connect_num, int[] connect_ID){
        this.ID = ID;
        this.connect_num = connect_num;
        this.connect_ID = new int[connect_num];
        this.connect_ID = connect_ID;
        sw = 1;
    }
        
    Processor(int ID,int connect_num, int[] connect_ID, int Core_number, double[] frequency, int Counter) {
        this.ID = ID;
        this.connect_num = connect_num;
        this.Counter = Counter;
        this.connect_ID = new int[connect_num];
        this.connect_ID = connect_ID;
        this.Core_number = Core_number;
        for(int i = 0; i < this.Core_number; i++){
            Cores[i] = new Core(i, frequency);
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
   
   int CheckCoreUsed(double time){
       int counter = 0;
       for(int i = 0; i < getCore_number(); i++){
           if(Cores[i].CheckUsed(time) != -1)counter++;
       }
       return counter;
   }



    void outputProcData(){
        System.out.print("ProcID = "+ID+"Connection number ="+connect_num +" Core number = "+getCore_number());
        if(this.getSw() == 1)System.out.print(" this is SW");
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


    
    
    //start_timeで与えた時刻以降のそのプロセッサでのタスクの処理速度を調節する
    public DAG adjusterTBHT(DAG taskGraph,Proclist Plist,  double start_time){
        int UtilizationOfProcessor;
        int addFlag = 0;
        int commflag = 0; //１なら通信時間の考慮をしたため他のプロセッサ上に配置されているタスクの周波数も再度確認変更する．フラグ
        double currentFrequency;
        double divideTime = start_time;
        Tasknode restTask;
        ArrayList<Integer> finishTasks = new ArrayList<Integer>();
        Edgecommunication edcom = new Edgecommunication();

        
        System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆in method adjusterTBHT☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
        System.out.println("start Time = "+ start_time);
        //edcom.communicationTime(taskGraph, Plist, taskGraph.gettask_i(taskGraph, task.getID()));
        //開始直後に対象となるタスクノードを見つけ出し,resttasksに入れる
        for(int coreI = 0; coreI < this.Core_number; coreI++){
            
            System.out.print("Cores["+coreI+"].ExecuteTask:");
            for(int i = 0; i < Cores[coreI].ExecuteTask.size(); i++){
                System.out.print(" "+((Tasknode)Cores[coreI].ExecuteTask.get(i)).getID());
            }System.out.println("");
            
            int ID = Cores[coreI].CheckUsed(divideTime);
            if(ID != -1){
                ID = taskGraph.gettask_i(taskGraph, ID);
                restTask = new Tasknode();
                taskGraph.task[ID].setRestweight(taskGraph.task[ID].calculateRestWeight(divideTime, upperRate));  
                taskGraph.task[ID].setWorking_time((divideTime - taskGraph.task[ID].getStart_time() < 0) ? 0 : divideTime - taskGraph.task[ID].getStart_time());
                taskGraph.task[ID].setWorkingsub(0);
                restTask.initializeRestTask(taskGraph.task[ID], divideTime);
                restTask.output_result();
                restTasks.add(restTask);
                System.out.println(" task"+ taskGraph.task[ID].getID()+" addFLAG="+ addFlag +" working time "+(divideTime - taskGraph.task[ID].getStart_time()));
            }
        }
        System.out.println("resttasks size" + restTasks.size());
        //動作周波数を変更し，変更した周波数での実行時間を再計算
        //UtilizationOfProcessor = this.ProcessorUtilization(taskGraph, divideTime);
        UtilizationOfProcessor = ((restTasks.size() -1) > 0 )?  restTasks.size() -1 : 0;
        
        System.out.println("Utilization Of Processor="+UtilizationOfProcessor);
        
        
        //動作周波数の調節を開始
        while(true){
            int flag = 0;
            if(UtilizationOfProcessor >= 4)UtilizationOfProcessor = 3;
            currentFrequency = Cores[0].getFrequency(UtilizationOfProcessor);
            //新規で設定されたdividetimeに対して同時に処理される予定のタスクノードがあれば追加する
                for(int coreI = 0; coreI < this.Core_number; coreI++){
                    addFlag = 1;
                    //System.out.println("--Core"+Cores[coreI].getCoreID());
                    int ID = Cores[coreI].CheckUsed(divideTime);
                    if(ID != -1){
                        //System.out.print("hit task ID="+ID + " addaFlag="+ addFlag);
                        //すでにresttasksに入っているかどうかの判断　入っていたらFLAG=0，そうでなければFLAG=1
                        for(int m = 0; m < restTasks.size(); m++)
                            if(ID == restTasks.get(m).getID()){
                                addFlag = 0;
                                //System.out.print(" task ID="+ID +" addFlag="+addFlag);
                                break;
                            }
                        for(int m = 0; m < finishTasks.size(); m++){
                            if(ID == finishTasks.get(m)){
                                addFlag = 0;
                                break;
                            }
                        } 
                        //System.out.println(" → "+ addFlag);

                        if(addFlag == 1){
                            ID = taskGraph.gettask_i(taskGraph, ID);
                            restTask = new Tasknode();
                            taskGraph.task[ID].setRestweight(taskGraph.task[ID].getRestweight() + taskGraph.task[ID].calculateRestWeight(divideTime, upperRate));
                            taskGraph.task[ID].setWorking_time((divideTime - taskGraph.task[ID].getStart_time() < 0) ? 0 : divideTime - taskGraph.task[ID].getStart_time());
                            restTask.initializeRestTask(taskGraph.task[ID], divideTime);
                            finishTasks.add(restTask.getID());
                            restTasks.add(restTask);
                            //System.out.println("☆☆☆add task"+ restTask.getID()+" dividetime="+divideTime+" UtilizationPRoc="+UtilizationOfProcessor+" resttasks.size="+restTasks.size()+" addFLAG="+ addFlag);
                            //System.out.println("finish tasks :"+finishTasks);
                            //for(int j = 0; j < restTasks.size(); j++)
                            //    restTasks.get(j).output_result();
                            ID = -1;

                        }
                    }
                }
            //System.out.println("divitime "+ divideTime+"Utilizationprocessor"+UtilizationOfProcessor);
            if(!restTasks.isEmpty()){
                for(int i = 0; i < restTasks.size(); i++){
                    //各タスクノードに対して0.01づつ処理時間を加算していき，その時の動作周波数＊0.01分の処理量を減らす
                    //そのあとに各タスクノードの対応する処理時間の配列に動作周波数を入れていく
                    if(restTasks.get(i).getID() != 0){
                        restTasks.get(i).setWorkingsub(restTasks.get(i).getWorkingsub() + 0.02);
                        restTasks.get(i).setRestweight(restTasks.get(i).getRestweight() - currentFrequency/upperRate);
                        double time = (divideTime - restTasks.get(i).getStart_time());
                        if(time <= 0)time = 0;
                        restTasks.get(i).executeFrequencyStep[(int)(time * upperRate)] = currentFrequency;
                    }
                    
                    //もし，残りの処理量が0になったやつの処理
                    if(restTasks.get(i).getRestweight() <= 0){
                        flag = 1;
                        System.out.println("#"+(int)((divideTime - restTasks.get(i).getFinish_time()) * upperRate));
                        System.out.print("task "+ restTasks.get(i).getID());
                        System.out.print("old star time "+ taskGraph.task[taskGraph.gettask_i(taskGraph, restTasks.get(i).getID())].getStart_time());
                        System.out.println("old finish time "+ taskGraph.task[taskGraph.gettask_i(taskGraph, restTasks.get(i).getID())].getFinish_time());
                        
                        //対象のタスクノードの処理時間と終了時間を更新
                        for(int j = 0; j < restTasks.size(); j++)
                            restTasks.get(j).output_result();
                        restTasks.get(i).setWorking_time(
                                restTasks.get(i).getWorking_time()
                                + restTasks.get(i).getWorkingsub()
                                );
                        restTasks.get(i).setFinish_time(
                                restTasks.get(i).getStart_time()
                                +restTasks.get(i).getWorking_time()
                                );
                        restTasks.get(i).setWorkingsub(0);
                        Cores[restTasks.get(i).allocate_core_ID].setEndTime(restTasks.get(i).getFinish_time());
                        System.out.println("procの終了時間を更新２proc="+Plist.procs[restTasks.get(i).allocate_proc_ID].getProcID()
                                        +" core="+Cores[restTasks.get(i).allocate_core_ID].getCoreID()
                                        +" end time ="+Cores[restTasks.get(i).allocate_core_ID].getEndTime());
                        Cores[restTasks.get(i).allocate_core_ID].nomalAdjuster(restTasks.get(i));
                        for(int num = 0; num < restTasks.get(i).successor.size(); num++){
                            System.out.println("successer ID="+(Integer)restTasks.get(i).successor.get(num));
                            int index = taskGraph.gettask_i(taskGraph, (Integer)restTasks.get(i).successor.get(num));
                            System.out.println("index="+index+" resttasksget(i)="+restTasks.get(i));
                            if(index != -1)
                                taskGraph.task[index].adjusterSameProcessor(taskGraph, restTasks.get(i).getFinish_time());
                        }
                        for(int num = 0; num < restTasks.size();num++){
                            int index = taskGraph.gettask_i(taskGraph, restTasks.get(num).getID());
                            restTasks.get(num).setStart_time(taskGraph.task[index].getStart_time());
                        }
                        System.out.println("task "+ restTasks.get(i).getID()+" new star time "+ restTasks.get(i).getStart_time()+" new finish time "+ restTasks.get(i).getFinish_time());
                        int num = 0;
                        

                        //終了時刻以降の周波数ステップを0リセット
                        num =(int)((divideTime - restTasks.get(i).getStart_time()) * upperRate);
                        if(num < 0)num =0;
                        //System.out.println("num="+num+" starTime="+restTasks.get(i).getStart_time()+" divideTime="+divideTime);
                        while(true){
                            if(num == restTasks.get(i).executeFrequencyStep.length)break;
                            restTasks.get(i).executeFrequencyStep[num] = 0;
                            num++;
                         }
                        /*
                        num= 0;
                        while(true){
                            if(num == restTasks.get(i).executeFrequencyStep.length)break;
                            System.out.print(" "+restTasks.get(i).executeFrequencyStep[num]);
                            num++;
                         }
                        */
                        addTasknode(taskGraph, restTasks.get(i));
                        System.out.println("task "+ taskGraph.task[taskGraph.gettask_i(taskGraph, restTasks.get(i).getID())].getID()
                                +" new star time "+ taskGraph.task[taskGraph.gettask_i(taskGraph, restTasks.get(i).getID())].getStart_time()
                                +" new finish time "+ taskGraph.task[taskGraph.gettask_i(taskGraph, restTasks.get(i).getID())].getFinish_time());
                        divideTime += 0.02;
                        
                        
                        restTasks.remove(i);
                        //UtilizationOfProcessor = this.ProcessorUtilization(taskGraph, divideTime);
                        System.out.println("★☆☆");
                        UtilizationOfProcessor = restTasks.size() -1;
                        if(restTasks.size() != (UtilizationOfProcessor + 1)){
                           // UtilizationOfProcessor = restTasks.size() - 1;
                        }
                                    
                        
                    }
                }
                if(flag != 1){
                    divideTime+=0.02;
                }                
                double oldUtilizationOfProcessor = UtilizationOfProcessor;
                //UtilizationOfProcessor = this.ProcessorUtilization(taskGraph, divideTime);
                UtilizationOfProcessor = restTasks.size() -1;
                //if(restTasks.size() < UtilizationOfProcessor)
                    addFlag = 1;
            }else{
                break;
            }
        }
        System.out.println("end in method adjusterTBHT");
        return taskGraph;
        
  
    }

     
    private void addTasknode(DAG taskGraph, Tasknode task){
        int index = taskGraph.gettask_i(taskGraph, task.getID());
        taskGraph.task[index].setTasknode(task);
    }
    

    //timeで指定された時間のプロセッサの使用率を返す
    int ProcessorUtilization(DAG taskgraph, double time){
        int count = 0,num;
        for(int i = 0; i < this.Core_number; i++){
            if((num = this.Cores[i].CheckUsed(time)) != -1){
                System.out.println("被ってるコア："+ this.Cores[i].getCoreID());
                count++;
            }
        }
        return count;
    }
    
    public int getSw() {
        return sw;
    }
    public void setSw(int sw) {
        this.sw = sw;
    }
    public int getCore_number() {
        return Core_number;
    }
    public double getAddtime() {
        return addtime;
    }
    public void setAddtime(double addtime) {
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
}

