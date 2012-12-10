package scheduling;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


        
public class Edgecommunication{
    static final boolean printOutFlag = false;
    private int CoreID;
    static int upperRate = 50;
    static int combinum = 0;
    //スケジュールの結果を反映させる仮候補を入れておく
    static DAG SubTaskGraph;
    static Proclist SubProclist;
    //スケジュールの際の組み合わせ順列のコピーを入れる　この中で優れているのもをsubに入れる
    static DAG task_graph;
    static Proclist CloneProclist;

        
	public void t_tf(DAG task_graph, Proclist Plist, int lookAheadNumber){
           MyMath mymath = new MyMath();
           Combination Combi = new Combination();
           CalculationUtil calutil = new CalculationUtil();
           int succ_ID;
           int pred_ID;
           int ID;
           int proc_I = -1;
           int core_I = -1;
           int task_I = -1;
           int index = 0;
           Tasknode currentTask;
           Tasknode[] lookAheadTasks = new Tasknode[lookAheadNumber];
           float start_time , finish_time , pred_finish_time ;
           float proc_ftime ;
           float tdr = 0;
           ArrayList<Float> predStime = new ArrayList<Float>();
           float min;
           float minimumTime = Float.MAX_VALUE;
           boolean setFlag = false;
           boolean flag = true;
           SubTaskGraph = new DAG();
           SubProclist = new Proclist();
           
           OliverListScheduling Sinnen = new OliverListScheduling();
           

           DAG TargetTaskGraph = new DAG();
           Proclist TargetPlist = new Proclist();

           //task_graph.outputResult();
           
           for(int i=0; i<task_graph.total_tasks; i++){
               long startCounter = System.currentTimeMillis();
               long endCounter;
                min = Float.MAX_VALUE;
                start_time = 0;
                pred_finish_time = 0;
                proc_ftime = 0;
                tdr = 0;
                minimumTime = Float.MAX_VALUE;
                flag = true;
                //task_graph.outputResult();
                if(printOutFlag)
                    System.out.println("---------------------------------------------------------task"+task_graph.task[i].getID() +"schduling number I "+i +" ------------------------------------------------------------------------");
                for(int p=0; p<Plist.procnum; p++){
                    if(!Plist.procs[p].getSw()){
                        predStime.clear();
                        for(int core = 0; core < Plist.procs[p].getCore_number() ; core++){
                            //タスクグラフとプロセッサグラフのコピーを生成
                            //System.out.println("\n\n\noutput target task graph and target proc list\n\n\n");
                            TargetTaskGraph = task_graph.Clone();
                            TargetPlist = Plist.Clone();
                            //TargetTaskGraph.outputResult();
                            //TargetPlist.outputProclist();
                            
                            start_time = 0;
                            if(printOutFlag)
                                System.out.println("\n■■Check Proc:" + Plist.procs[p].getProcID() +" Core:" + core);
                            start_time = calutil.carryUp100000(t_s(TargetTaskGraph.task[i], p, core, TargetTaskGraph, TargetPlist));
                            boolean addFlag = true;
                            if(predStime.size() == 0)predStime.add(start_time);
                            else{
                                for(float PredTime : predStime)
                                    if(PredTime == start_time)addFlag = false;
                                if(addFlag)predStime.add(start_time);
                            }
                            if(addFlag){
                            finish_time = calutil.carryUp100000(t_f(start_time, TargetTaskGraph.task[i],p,core, TargetTaskGraph, TargetPlist));
                            
                            if(printOutFlag)
                                System.out.println("\n start time="+start_time+"finish time="+finish_time +" minimumtime="+min);
                            if(true){//if(finish_time < min){
                                setVariableToCurrentTask(TargetTaskGraph.task[i], start_time, finish_time, p, core);
                                if(printOutFlag)
                                    System.out.println(" allocate Proc:" + TargetPlist.procs[p].getProcID() +" Core:" + core);
                                min = finish_time;
                                proc_I = p;
                                core_I = core;
                                task_I = i;
                            }else{
                                if(printOutFlag)
                                    System.out.println("***not update tasknode data");
                            }
                    if(printOutFlag){
                        TargetTaskGraph.outputResult();
                        
                    }
                    this.communicationTime(TargetTaskGraph, TargetPlist, task_I);
                    if(printOutFlag)
                        TargetTaskGraph.outputResult();
                    //コア側にどのタスクを処理して終了時刻が何時かを保持させる
                    TargetPlist.procs[proc_I].Cores[core_I].setExecuteTask(TargetTaskGraph.task[i]);
                    TargetPlist.procs[proc_I].Cores[core_I].setEndTime(TargetTaskGraph.task[i].finish_time.get(0));
                    if(printOutFlag)
                        TargetTaskGraph.outputResult();

                    for(int mmm= 0; mmm<TargetPlist.procnum;mmm++)
                        for(int nnn=0; nnn<TargetPlist.procs[mmm].getCore_number(); nnn++)
                            for(int xxx=0; xxx< TargetPlist.procs[mmm].Cores[nnn].ExecuteTask.size();xxx++){
                                TargetPlist.procs[mmm].Cores[nnn].setEndTime(TargetPlist.procs[mmm].Cores[nnn].ExecuteTask.get(xxx).finish_time.get(0));
                                if(printOutFlag)
                                    System.out.println("proc="+TargetPlist.procs[mmm].getProcID()
                                                    +" core="+TargetPlist.procs[mmm].Cores[nnn].getCoreID()
                                                    +" end time="+TargetPlist.procs[mmm].Cores[nnn].getEndTime());
                            }
                    if(printOutFlag){
                        for(int mmm = 0; mmm< 10000; mmm++)
                            System.out.print(String.format(" %3d", mmm));
                        for(int mmm = 0; mmm< TargetTaskGraph.total_tasks; mmm++){
                            System.out.print("task"+TargetTaskGraph.task[mmm].getID());
                            System.out.println("");
                        }
                    }
                    //TargetTaskGraph.outputResult(); 
                    
                    DAG CloneTaskGraph = new DAG();
                    Proclist ClonePlist = new Proclist();
                    CloneTaskGraph = TargetTaskGraph.Clone();
                    ClonePlist = TargetPlist.Clone();
                    
                    //System.out.println("\n\n\noutput Clone task graph and Clone proc list\n\n\n");
                    //CloneTaskGraph.outputResult();
                    //ClonePlist.outputProclist();
                    //System.out.println("\n\n in oliver \n\n");
                    Sinnen.t_tf(CloneTaskGraph, ClonePlist, i+1);
                    
                    System.gc();
                    /*
                    for(int x = 0; x < task_graph.total_tasks; x++){  
                        task_graph.task[x].start_time.set(0, (float) -1);
                        task_graph.task[x].finish_time.set(0, (float) -1);
                    }

                    FixofTurboBoostandHyperThreading fixTBHT = new FixofTurboBoostandHyperThreading();
                    fixTBHT.fixOfTBHT(task_graph, Plist);
                    
                    * 
                    */
                    //処理完了時間が最小となるものを選ぶ
                    if(flag){
                        if(printOutFlag)
                            System.out.println("●●●●●●●●●　update of subtaskgraph and subproclist ●●●●●●●●●●●●●●●●●●●● minitime" +minimumTime +" new time"+CloneTaskGraph.task[CloneTaskGraph.total_tasks -1 ].getTaskFinishTime());
                        SubTaskGraph = TargetTaskGraph.Clone();
                        SubProclist = TargetPlist.Clone();
                        minimumTime = CloneTaskGraph.task[CloneTaskGraph.total_tasks -1 ].getTaskFinishTime();
                        //SubTaskGraph.outputResult();
                        //System.out.println();
                        setFlag = true;
                        flag = false;
                    }else if(minimumTime > CloneTaskGraph.task[CloneTaskGraph.total_tasks -1 ].getTaskFinishTime()){
                        if(printOutFlag)
                            System.out.println("●●●●●●●●●　update of subtaskgraph and subproclist ●●●●●●●●●●●●●●●●●●●● minitime" +minimumTime +" new time"+CloneTaskGraph.task[CloneTaskGraph.total_tasks -1 ].getTaskFinishTime());
                        SubTaskGraph.setDAG(TargetTaskGraph);
                        SubProclist.setProclist(TargetPlist);
                        minimumTime = CloneTaskGraph.task[CloneTaskGraph.total_tasks -1 ].getTaskFinishTime();
                        //SubTaskGraph.outputResult();
                        setFlag = true;
                    }
                            }
                    }//end for(int core = 0; core < Plist.procs[p].getCore_number() ; core++)
                    }//end if(!CloneProclist.procs[p].getSw()){
                }//end for(int p=0; p<Plist.procnum; p++)]
                task_graph.setDAG(SubTaskGraph);
                Plist.setProclist(SubProclist);
                //Plist.outputProclist();
                endCounter = System.currentTimeMillis();
                System.out.println("counter :"+ (endCounter-startCounter));
            }//end for(int i=0; i<task_graph.total_tasks; i++)
            for(int x = 0; x < task_graph.total_tasks; x++){  
                task_graph.task[x].start_time.set(0, (float) -1);
                task_graph.task[x].finish_time.set(0, (float) -1);
                int ExTaskI = Plist.procs[task_graph.task[x].allocate_proc_I].Cores[task_graph.task[x].allocate_core_I].SearchExeTaskIndex(task_graph.task[x].getID());
                Plist.procs[task_graph.task[x].allocate_proc_I].Cores[task_graph.task[x].allocate_core_I].ExecuteTask.set(ExTaskI, task_graph.task[x]);
            }
            FixofTurboBoostandHyperThreading fixTBHT = new FixofTurboBoostandHyperThreading();
            
            //task_graph.outputResult();
            //Plist.outputProclist();
            fixTBHT.fixOfTBHT2(task_graph, Plist, false);
            task_graph.outputResult();
        }            


        
        void updateProclist(DAG CloneTaskGraph, Proclist CloneProclist, int CurrentTask){
            //Proc側に保持させているタスクの情報をここで更新（現在のタスクまで）
            for(int c = 0; c <= CurrentTask; c++){
                int sampleindex = -1;
                for(int counterExecuteTaskList = 0; 
                        counterExecuteTaskList < CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].Cores[CloneTaskGraph.task[c].allocate_core_I].ExecuteTask.size();
                        counterExecuteTaskList++)
                    if(((Tasknode)CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].Cores[CloneTaskGraph.task[c].allocate_core_I].ExecuteTask.get(counterExecuteTaskList)).getID()
                            == CloneTaskGraph.task[c].getID()){
                        sampleindex = counterExecuteTaskList;
                    }

                    CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].Cores[CloneTaskGraph.task[c].allocate_core_I].ExecuteTask.set(sampleindex, CloneTaskGraph.task[c]);
                    CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].Cores[CloneTaskGraph.task[c].allocate_core_I].setEndTime(CloneTaskGraph.task[c].finish_time.get(0));
                    if(printOutFlag){
                        System.out.print("proc="+CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].getProcID()
                                +" Core="+CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].Cores[CloneTaskGraph.task[c].allocate_core_I].getCoreID());
                        System.out.println("Procの終了時間をセット2　task:"+CloneTaskGraph.task[c].getID()+" set end time="+CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_I].Cores[CloneTaskGraph.task[c].allocate_core_I].getEndTime());
                    }
            } 
        }
        
        //採用するスケジューリング結果を選択する
        boolean selectOptimizationClone(DAG TaskGraphA, DAG TaskGraphB){
            CalculationUtil calutil = new CalculationUtil();
            //System.out.println("taskGraphA resttime ="+TaskGraphA.restTasksProcessingTime + " taskGraphB restTIme="+TaskGraphB.restTasksProcessingTime);
            if(TaskGraphA.restTasksProcessingTime < TaskGraphB.restTasksProcessingTime)return false;
            else return true;        
        }
        
        
        //
        void setVariableToCurrentTask(Tasknode currentTask, float startTime, float finishTime, int procI, int coreI){
            CalculationUtil calutil = new CalculationUtil();
            
            currentTask.start_time.set(0, startTime);
            currentTask.finish_time.set(0, finishTime);
            currentTask.working_time.set(0,calutil.carryUp100000(currentTask.getWorkingsub()));
            currentTask.allocate_proc_I = procI;//Plist.procs[p].getProcID();
            currentTask.allocate_core_I = coreI;//Plist.procs[p].Cores[core].getCoreID();

            if(printOutFlag)
                System.out.println(
                        "\ntask[" + currentTask.getID() + "]" +
                        " start Time="+ currentTask.start_time.get(0) +
                        " working Time="+ currentTask.working_time.get(0) +
                        " finish Time=" + currentTask.finish_time.get(0) +
                        " proc="+currentTask.allocate_proc_I +
                        " core="+ currentTask.allocate_core_I);
        }
        
        //別コアで実行しているものへの通信時間の計算し,それぞれに加算
        void communicationTime(DAG taskGraph, Proclist Plist, int task_I){
            int pred_I;
            int succ_I;
            boolean[] flag = new boolean[Plist.procs[taskGraph.task[task_I].allocate_proc_I].getCore_number()];
            float number;
            
            if(printOutFlag)
                System.out.println("in method communicationTIme task:"+taskGraph.task[task_I].allocate_proc_I);

            for(int i = 0; i < flag.length; i++)
                flag[i] = true;

            for(int m=0; m < taskGraph.task[task_I].predecessor.size(); m++){
                pred_I = taskGraph.gettask_i(taskGraph, taskGraph.task[task_I].predecessor.get(m));
                //System.out.println(" predtask = "+taskGraph.task[pred_I].allocate_proc_ID);
                if(taskGraph.task[task_I].allocate_proc_I != taskGraph.task[pred_I].allocate_proc_I){
                    // System.out.println("ID "+taskGraph.task[task_ID].getID()+" pred "+taskGraph.task[pred_I].getID());
                    int[] procIDs = Plist.getexeProcIDCoreID(taskGraph.task[pred_I].getID());
                    if(flag[procIDs[1]]){
                        //System.out.println("   add communication time");
                        Plist.procs[procIDs[0]].Cores[procIDs[1]].addCommunicationCost(taskGraph, taskGraph.task[pred_I].getID() , 1);
                        flag[procIDs[1]] = false;
                    }
                }
            }
        }
        

        private float f_pred(Tasknode Task, DAG task_graph){
            float ftime = 0;
            int pred_i;
            //親ノードの最遅処理終了時刻を会得
            for(int i = 0; i < Task.getConnection_number() ;i++){
                pred_i = gettask_i(task_graph, (Integer)Task.predecessor.get(i));
		if(ftime < task_graph.task[pred_i].finish_time.get(0)){
                    ftime = task_graph.task[pred_i].finish_time.get(0);
		}
            }
            return ftime;	
        }

        private float f_proc(Processor Proc, int CoreID){
            //割り当てる予定のコアの処理完了時間を会得
            float ftime = 0;
            float commftime = 0;
            int index = Proc.Cores[CoreID].ExecuteTask.size() - 1;
            if(index < 0)ftime = 0;
            else ftime = Proc.Cores[CoreID].ExecuteTask.get(index).finish_time.get(0);
            if(!Proc.Cores[CoreID].ExecuteTask.isEmpty())
                commftime = Proc.Cores[CoreID].ExecuteTask.get(Proc.Cores[CoreID].ExecuteTask.size() - 1).communicationTime;
            //if(ftime < commftime)
            if(printOutFlag)
                System.out.print("ftime "+ ftime +" commtime "+ commftime);
            ftime += commftime;
            return ftime;	
        }
        
        private float t_s(Tasknode Task, int proc, int core, DAG task_graph, Proclist Plist){
            //タスク「TaskID」の処理を開始出来る最遅時刻を求める
            CalculationUtil calutil = new CalculationUtil();
            float ftime = 0;
            float time = 0;
            float communicationTime = 0;
            //親ノードの最遅処理終了時刻を会得
            if(ftime < (time = f_pred(Task, task_graph))){
                ftime = time;
            }

            if(printOutFlag)
                System.out.println("pred finish time="+ftime);
            //プロセッサが利用できるようになる時間を会得
            if(ftime < (time = f_proc(Plist.procs[proc], core))){
                ftime = time;
            }
            if(printOutFlag)
                System.out.println("proc finish time="+ftime);
            //タスクの処理を開始するために必要な時間を加算
            ftime += t_dr(task_graph, Task, proc, core, Plist);
            if(printOutFlag)
                System.out.println("communication finish time="+ ftime);
            return ftime;
            
        }
            
       


        //タスク[taskID]をプロセッサ[ProcID]のCoreIDで処理した場合の処理時間を計算し，開始時間にそれを加えたものを終了時間として返す
        private float t_f(float stime, Tasknode Task, int Proc_i, int Core_i, DAG task_graph, Proclist Plist){
            Task.setWorkingsub(w(task_graph, Plist, Task, Plist.procs[Proc_i], Plist.procs[Proc_i].Cores[Core_i], stime));
            return stime + Task.getWorkingsub();
        }
        
        private float t_dr(DAG task_graph, Tasknode Task, int Proc_i, int Core_i, Proclist Plist){
            CalculationUtil calutil = new CalculationUtil();
            float communicationTime = 0, ftime = 0, comm_time = Float.MAX_VALUE;
            Tasknode m;
            int target_proc = 0;
            int task_i;
            for(int i = 0; i < Task.predecessor.size(); i++){
                int predI = task_graph.gettask_i(task_graph, (Integer)Task.predecessor.get(i));
                if(task_graph.task[predI].allocate_proc_I != Proc_i){
                    //System.out.println("task"+task_graph.task[predI].getID()+" procID="+ Plist.procs[task_graph.task[predI].allocate_proc_ID].getProcID());
                    communicationTime += calutil.calculationOfHopCost(Plist.procs[task_graph.task[predI].allocate_proc_I], Plist.procs[Proc_i].getProcID() , 0);
                }
            }

            return communicationTime;
        }
        

        private float t_f(DAG task_graph, int TaskID){
            int index = task_graph.gettask_i(task_graph, TaskID);
            return task_graph.task[index].finish_time.get(0);
        }
        
        private float w(DAG taskGraph, Proclist Plist, Tasknode task,Processor Proc, Core C, float startTime){
            int UtilizationOfProcessor = 0;
            int exeFreqStepIndex = 0;
            float currentFrequency = (float)2.0;
            float workingTime = 0;
            float weight = task.weight.get(0);

            workingTime = task.weight.get(0) / currentFrequency;
            task.ExFrequency.set(0, currentFrequency);
            
            if(printOutFlag)
                System.out.println("working time ="+workingTime);
            return workingTime;
        }
        
        int proc(Proclist plist, int TaskID){
            int[] ProcAndCore = plist.getexeProcIDCoreID(TaskID);
            return ProcAndCore[0];
        }
        
        int pred(Tasknode task, int i){
            return (Integer)task.predecessor.get(i);
        }
        
        int succ(Tasknode task, int i){
            return (Integer)task.successor.get(i);
        }

        
    public int gettask_i(DAG taskgraph, int taskID){
        for(int i = 0; i < taskgraph.total_tasks; i++)
            if(taskgraph.task[i].getID() == taskID)return i;
        return -1;
    }
  

    public int getCoreID() {
        return CoreID;
    }


    public void setCoreID(int CoreID) {
        this.CoreID = CoreID;
    }
    
    
}
