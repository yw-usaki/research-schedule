package scheduling;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import java.util.ArrayList;


        
public class Edgecommunication{
    static final boolean printOutFlag = false;
    private int CoreID;
    static int upperRate = 50;
    static int combinum = 0;
    //スケジュールの結果を反映させる仮候補を入れておく
    static DAG SubTaskGraph;
    static Proclist SubProclist;
    //スケジュールの際の組み合わせ順列のコピーを入れる　この中で優れているのもをsubに入れる
    static DAG CloneTaskGraph;
    static Proclist CloneProclist;

        
	public void t_tf(DAG task_graph, Proclist Plist, int lookAheadNumber){
           MyMath mymath = new MyMath();
           Combination Combi = new Combination();
           CalculationUtil calutil = new CalculationUtil();
           int succ_ID;
           int pred_ID;
           int ID;
           int proc_ID = -1;
           int core_ID = -1;
           int task_ID = -1;
           int index = 0;
           Tasknode currentTask;
           Tasknode[] lookAheadTasks = new Tasknode[lookAheadNumber];
           float start_time , finish_time , pred_finish_time ;
           float proc_ftime ;
           float tdr = 0;
           float min;
           SubTaskGraph = new DAG();
           SubProclist = new Proclist();


           for(int i=0; i<task_graph.total_tasks; i = i + lookAheadNumber){
               //先読みタスクの配列を生成
               for(index = i; index < i+lookAheadNumber; index++){
                   if(index < task_graph.total_tasks){
                       if(printOutFlag)
                           System.out.println("index="+index +" task_graph.task[index]="+task_graph.task[index]);
                       lookAheadTasks[index - i] = task_graph.task[index].Clone();
                       if(printOutFlag)
                           System.out.print("perm tasks["+(index-i)+"] :");
                       lookAheadTasks[index - i].output_result();
                   }else{
                       lookAheadNumber = index -i;
                       break;
                   }
               }
               if(printOutFlag)
                   System.out.println(" lookaheadnumber="+ lookAheadNumber);
               ArrayList<Tasknode[]> combitasks = new ArrayList<Tasknode[]>();
               ArrayList<Integer> combitasksID = new ArrayList<Integer>();
               combitasks.clear();
               Combi.make_perm(0, combitasks, new Tasknode[lookAheadNumber], lookAheadTasks);
               
               //作成した組み合わせ順列をすべて表示
               Combi.printPermutation(combitasks);
               
               //作成した組み合わせ順列の中でタスクの依存関係を無視しているものを除外する
               for(int count = 0; count < combitasks.size(); count++){
                   if(Combi.checkPermutation(task_graph, combitasks.get(count))){
                   }else{
                       combitasks.remove(count);
                       count--;
                   }
               }
               
               //依存関係に違反しているものを除外した後の組み合わせ順列を表示
               Combi.printPermutation(combitasks);
               combinum += combitasks.size();
              //先読みタスクの配列が完成したら割り当て処理を行う
              for(int combi = 0; combi < combitasks.size(); combi++){
                  
                //System.exit(0);
                CloneTaskGraph = new DAG();
                CloneTaskGraph = task_graph.Clone();
                CloneProclist = new Proclist();
                CloneProclist = Plist.Clone();
              
                if(printOutFlag)
                    System.out.println("■■■■■■■■■■■■■■■■■number:"+combi+" clone taskGraph and Proclist output data■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
                //CloneTaskGraph.outputResult();
                CloneProclist.outputProclist();
              

                //組み合わせ順列の結果をクローンTaskGraphに反映
                CloneTaskGraph.changeTasknode(i, combitasks.get(combi));
              
                  if(printOutFlag)
                      System.out.println("△△△△△△△△△△△△△△△△ combi"+combi+" start △△△△△△△△△△△△△△△△△△△△△△△△△△△△△△");
                    for(int n = i; n < i + lookAheadNumber; n++){
                        min = Float.MAX_VALUE;
                        start_time = 0;
                        pred_finish_time = 0;
                        proc_ftime = 0;
                        tdr = 0;
                        if(printOutFlag)
                            System.out.println("---------------------------------------------------------task"+CloneTaskGraph.task[n].getID() +"schduling ------------------------------------------------------------------------");
                        for(int p=0; p<CloneProclist.procnum; p++){
                            if(CloneProclist.procs[p].getSw() != 1){
                                for(int core = 0; core < CloneProclist.procs[p].getCore_number() ; core++){
                                    start_time = 0;
                                    CloneTaskGraph.task[n].copyExecuteFrequency.clear();
                                    if(printOutFlag){
                                        for(int xx = 0; xx < CloneTaskGraph.task[n].copyExecuteFrequency.size(); xx++){
                                            System.out.print(" "+CloneTaskGraph.task[n].executeFrequencyStep.get(xx));
                                        }
                                        System.out.println();
                                        for(int xx = 0; xx < CloneTaskGraph.task[n].copyExecuteFrequency.size(); xx++){
                                            System.out.print(" "+CloneTaskGraph.task[n].copyExecuteFrequency.get(xx));
                                        }
                                    }
                                    if(printOutFlag)
                                        System.out.println("\n■■Check Proc:" + CloneProclist.procs[p].getProcID() +" Core:" + core);
                                    start_time = calutil.carryUp(t_s(CloneTaskGraph.task[n], p, core, CloneTaskGraph, CloneProclist));
                                    finish_time = calutil.carryUp(t_f(start_time, CloneTaskGraph.task[n],p,core, CloneTaskGraph, CloneProclist));

                                    if(printOutFlag)
                                        System.out.println("\n start time="+start_time+"finish time="+finish_time +" minimumtime="+min);
                                    if(finish_time < min){
                                        setVariableToCurrentTask(CloneTaskGraph.task[n], start_time, finish_time, p, core);
                                        if(printOutFlag)
                                            System.out.println(" allocate Proc:" + CloneProclist.procs[p].getProcID() +" Core:" + core);
                                        min = finish_time;
                                        proc_ID = p;
                                        core_ID = core;
                                        task_ID = n;
                                    }else{
                                        if(printOutFlag)
                                            System.out.println("***not update tasknode data");
                                    }

                                }

                            }//end for(int core = 0; core < Plist.procs[p].getCore_number() ; core++)

                        }//end for(int p=0; p<Plist.procnum; p++)]
                        if(printOutFlag){
                            CloneTaskGraph.outputResult();
                            for(int mmm = 0; mmm< 10000; mmm++)
                                System.out.print(String.format(" %3d", mmm));
                            for(int mmm = 0; mmm< CloneTaskGraph.total_tasks; mmm++){
                                System.out.print("task"+CloneTaskGraph.task[mmm].getID());
                                CloneTaskGraph.task[mmm].outputExecuteFrequency();
                                System.out.println("");
                            }
                        }
                        this.communicationTime(CloneTaskGraph, CloneProclist, task_ID);
                        if(printOutFlag)
                            CloneTaskGraph.outputResult();
                        //コア側にどのタスクを処理して終了時刻が何時かを保持させる
                        CloneProclist.procs[proc_ID].Cores[core_ID].setExecuteTask(CloneTaskGraph.task[n]);
                        CloneProclist.procs[proc_ID].Cores[core_ID].setEndTime(CloneTaskGraph.task[n].getFinish_time());
                        for(int procI = 0; procI < CloneProclist.procnum; procI++){
                            if(CloneProclist.procs[procI].getSw() == 0){
                                CloneProclist.procs[procI].adjusterTBHT(CloneTaskGraph, CloneProclist, CloneTaskGraph.task[task_ID].getStart_time());
                                updateProclist(CloneTaskGraph, CloneProclist, n);
                                CloneProclist.procs[procI].adjusterTBHT(CloneTaskGraph, CloneProclist, CloneTaskGraph.searchLatestPredFinishTIme(task_ID)); 
                                updateProclist(CloneTaskGraph, CloneProclist, n);
                            }
                        }
                        if(printOutFlag)
                            CloneTaskGraph.outputResult();
                        
                        for(int mmm= 0; mmm<CloneProclist.procnum;mmm++)
                            for(int nnn=0; nnn<CloneProclist.procs[mmm].getCore_number(); nnn++)
                                for(int xxx=0; xxx< CloneProclist.procs[mmm].Cores[nnn].ExecuteTask.size();xxx++){
                                    CloneProclist.procs[mmm].Cores[nnn].setEndTime(
                                            ((Tasknode)CloneProclist.procs[mmm].Cores[nnn].ExecuteTask.get(xxx)).getFinish_time()                                      
                                            );
                                    if(printOutFlag)
                                        System.out.println("proc="+CloneProclist.procs[mmm].getProcID()
                                                        +" core="+CloneProclist.procs[mmm].Cores[nnn].getCoreID()
                                                        +" end time="+CloneProclist.procs[mmm].Cores[nnn].getEndTime());
                                }
                        if(printOutFlag){
                            for(int mmm = 0; mmm< 10000; mmm++)
                                System.out.print(String.format(" %3d", mmm));
                            for(int mmm = 0; mmm< CloneTaskGraph.total_tasks; mmm++){
                                System.out.print("task"+CloneTaskGraph.task[mmm].getID());
                                CloneTaskGraph.task[mmm].outputExecuteFrequency();
                                System.out.println("");
                            }
                       }
                    }//end for(int n = 0; n < lookAheadNumber; n++)
                    //CloneTaskGraph.outputResult();
                    //calutil.calculateRestTasksProcessingTime(CloneTaskGraph, CloneProclist, currentTaskI);
                    OliverListScheduling Oliver = new OliverListScheduling();
                    CloneTaskGraph.restTasksProcessingTime = Oliver.t_tf(CloneTaskGraph, CloneProclist,  i + lookAheadNumber);
                    //System.out.println("\nrest tasks processing time"+ CloneTaskGraph.restTasksProcessingTime);
                    if(combi == 0){
                        SubTaskGraph = CloneTaskGraph.Clone();
                        SubProclist = CloneProclist.Clone();
                    }else{
                        if(selectOptimizationClone(SubTaskGraph, CloneTaskGraph)){
                                //System.out.println("†change the SubTaskGraph < CloneTaskGraph"+combi);
                            SubTaskGraph.setDAG(CloneTaskGraph);
                            SubProclist.setProclist(CloneProclist);
                        }
                        //combitasks内の情報をtaskgrahに反映

                    }
                   if(printOutFlag)
                       System.out.println("△△△△△△△△△△△△△△△△ combi"+combi+" end △△△△△△△△△△△△△△△△△△△△△△△△△△△△△△");
                }//end for(int combi = 0; combi < combitasks.size(); combi++)

                task_graph.setDAG(SubTaskGraph);
                Plist.setProclist(SubProclist);
                //task_graph.outputResult(); 
                System.gc();
            }
           System.out.println("Combi number = "+combinum);
           combinum = 0;
            
        }
        
        void updateProclist(DAG CloneTaskGraph, Proclist CloneProclist, int CurrentTask){
            //Proc側に保持させているタスクの情報をここで更新（現在のタスクまで）
            for(int c = 0; c <= CurrentTask; c++){
                int sampleindex = -1;
                for(int counterExecuteTaskList = 0; 
                        counterExecuteTaskList < CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].Cores[CloneTaskGraph.task[c].allocate_core_ID].ExecuteTask.size();
                        counterExecuteTaskList++)
                    if(((Tasknode)CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].Cores[CloneTaskGraph.task[c].allocate_core_ID].ExecuteTask.get(counterExecuteTaskList)).getID()
                            == CloneTaskGraph.task[c].getID()){
                        sampleindex = counterExecuteTaskList;
                    }

                    CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].Cores[CloneTaskGraph.task[c].allocate_core_ID].ExecuteTask.set(sampleindex, CloneTaskGraph.task[c]);
                    CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].Cores[CloneTaskGraph.task[c].allocate_core_ID].setEndTime(CloneTaskGraph.task[c].getFinish_time());
                    if(printOutFlag){
                        System.out.print("proc="+CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].getProcID()
                                +" Core="+CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].Cores[CloneTaskGraph.task[c].allocate_core_ID].getCoreID());
                        System.out.println("Procの終了時間をセット2　task:"+CloneTaskGraph.task[c].getID()+" set end time="+CloneProclist.procs[CloneTaskGraph.task[c].allocate_proc_ID].Cores[CloneTaskGraph.task[c].allocate_core_ID].getEndTime());
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
        void setVariableToCurrentTask(Tasknode currentTask, float startTime, float finishTime, int procID, int coreID){
            CalculationUtil calutil = new CalculationUtil();
            
            currentTask.setStart_time(startTime);
            currentTask.setFinish_time(finishTime);
            currentTask.setWorking_time(calutil.carryUp(currentTask.getWorkingsub()));
            currentTask.executeFrequencyStep.clear();
            for(int freqStep = 0; freqStep < currentTask.copyExecuteFrequency.size(); freqStep++){
                currentTask.executeFrequencyStep.add(currentTask.copyExecuteFrequency.get(freqStep));
            }
            currentTask.copyExecuteFrequency.clear();
                /*
                if(currentTask.copyExecuteFrequency[freqStep] != 0){
                    if(printOutFlag)
                        System.out.print("-"+currentTask.copyExecuteFrequency[freqStep]);
                    currentTask.executeFrequencyStep[freqStep] = currentTask.copyExecuteFrequency[freqStep];
                    currentTask.copyExecuteFrequency[freqStep] = 0;
                }
            }
            * 
            */
            currentTask.allocate_proc_ID = procID;//Plist.procs[p].getProcID();
            currentTask.allocate_core_ID = coreID;//Plist.procs[p].Cores[core].getCoreID();

            if(printOutFlag)
                System.out.println("\ntask[" + currentTask.getID() + "]" +" start Time="+ currentTask.getStart_time() +" working Time="+ currentTask.getWorking_time() +" finish Time=" + currentTask.getFinish_time()+" proc="+currentTask.allocate_proc_ID+" core="+ currentTask.allocate_core_ID);
        }
        
        //別コアで実行しているものへの通信時間の計算し,それぞれに加算
        void communicationTime(DAG taskGraph, Proclist Plist, int task_ID){
            if(printOutFlag)
                System.out.println("in method communicationTIme task:"+taskGraph.task[task_ID].allocate_proc_ID);
            int pred_I;
            int succ_I;
            int[] flag = new int[Plist.procs[taskGraph.task[task_ID].allocate_proc_ID].getCore_number()];
            float min = Float.MAX_VALUE;
            float number;
            for(int i = 0; i < flag.length; i++)
                flag[i] = 0;

            for(int m=0; m < taskGraph.task[task_ID].predecessor.size(); m++){
                pred_I = taskGraph.gettask_i(taskGraph, (Integer)taskGraph.task[task_ID].predecessor.get(m));
                //System.out.println(" predtask = "+taskGraph.task[pred_I].allocate_proc_ID);
                    if(taskGraph.task[task_ID].allocate_proc_ID != taskGraph.task[pred_I].allocate_proc_ID){
                       // System.out.println("ID "+taskGraph.task[task_ID].getID()+" pred "+taskGraph.task[pred_I].getID());
                        if(taskGraph.task[pred_I].getFinish_time() < min)
                            min = taskGraph.task[pred_I].getFinish_time();
                        int[] procIDs = Plist.getexeProcID(taskGraph.task[pred_I].getID());
                        if(flag[procIDs[1]] != 1){
                            //System.out.println("   add communication time");
                            Plist.procs[procIDs[0]].Cores[procIDs[1]].addCommunicationCost(taskGraph, taskGraph.task[pred_I].getID() , 1);
                           //Plist.procs[procIDs[0]].adjusterTBHT(taskGraph, Plist, taskGraph.task[pred_I].getFinish_time());
                            flag[procIDs[1]] = 1;
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
		if(ftime < task_graph.task[pred_i].getFinish_time()){
                    ftime = task_graph.task[pred_i].getFinish_time();
		}
            }
            return ftime;	
        }

        private float f_proc(Processor Proc, int CoreID){
            //割り当てる予定のコアの処理完了時間を会得
            float ftime = 0;
            float commftime = 0;
            ftime = Proc.Cores[CoreID].getEndTime();
            if(!Proc.Cores[CoreID].ExecuteTask.isEmpty())
                commftime = ((Tasknode)Proc.Cores[CoreID].ExecuteTask.get(Proc.Cores[CoreID].ExecuteTask.size() - 1 )).getCommunicationEndTime();
            if(ftime < commftime)
                ftime = commftime;
            if(printOutFlag)
                System.out.print(Proc.Cores[CoreID].getEndTime() +" ");
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
                if(task_graph.task[predI].allocate_proc_ID != Proc_i){
                    //System.out.println("task"+task_graph.task[predI].getID()+" procID="+ Plist.procs[task_graph.task[predI].allocate_proc_ID].getProcID());
                    communicationTime += calutil.searchNode(Plist.procs[task_graph.task[predI].allocate_proc_ID], Plist.procs[Proc_i].getProcID() , 0);
                }
            }

            return communicationTime;
        }
        

        private float t_f(DAG task_graph, int TaskID){
            int index = task_graph.gettask_i(task_graph, TaskID);
            return task_graph.task[index].getFinish_time();
        }
        
        private float w(DAG taskGraph, Proclist Plist, Tasknode task,Processor Proc, Core C, float startTime){
            int UtilizationOfProcessor = 0;
            int exeFreqStepIndex = 0;
            float currentFrequency = -1;
            float workingTime = 0;
            float weight = task.getWeight();
            while(true){
                UtilizationOfProcessor = Proc.ProcessorUtilization(taskGraph, startTime + workingTime);
                currentFrequency = C.getFrequency(UtilizationOfProcessor);
                if(weight < currentFrequency / upperRate){
                    task.copyExecuteFrequency.add(weight);
                    workingTime += (float)weight / currentFrequency;
                    weight = 0;
                }else{
                    weight -= currentFrequency / upperRate;
                    task.copyExecuteFrequency.add(currentFrequency);
                    workingTime += (float)1 / (float)upperRate;
                }
                if(weight <= 0)break;

                //System.out.println("ProcUtili="+UtilizationOfProcessor +" currentfreq = "+currentFrequency+" weight ="+ weight+" working time="+workingTime+" index="+ exeFreqStepIndex);
            }
            if(printOutFlag)
                System.out.println("working time ="+workingTime);
            return workingTime;
        }
        
        int proc(Proclist plist, int TaskID){
            int[] ProcAndCore = plist.getexeProcID(TaskID);
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
