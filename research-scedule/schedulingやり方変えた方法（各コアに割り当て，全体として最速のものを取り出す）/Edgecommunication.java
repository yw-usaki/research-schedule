package scheduling;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import scheduling.Proclist;
import scheduling.DAG;
import scheduling.Tasknode;
import java.util.ArrayList;


        
public class Edgecommunication{
        private int CoreID;
        static int upperRate = 50;
        //スケジュールの結果を反映させる仮候補を入れておく
        static DAG SubTaskGraph;
        static Proclist SubProclist;
        //スケジュールの際の組み合わせ順列のコピーを入れる　この中で優れているのもをsubに入れる
        static DAG CloneTaskGraph;
        static Proclist CloneProclist;
           
        
	public void t_tf(DAG task_graph, Proclist Plist){
           MyMath mymath = new MyMath();
           Combination Combi = new Combination();
           int succ_ID;
           int pred_ID;
           int ID;
           int proc_ID = -1;
           int core_ID = -1;
           int task_ID = -1;
           int index = 0;
           int lookAheadNumber = 1;
           Tasknode currentTask;
           Tasknode[] lookAheadTasks = new Tasknode[lookAheadNumber];
           double start_time , finish_time , pred_finish_time ;
           double proc_ftime ;
           double tdr = 0;
           double min;
           int sccc =0;
           SubTaskGraph = new DAG();
           SubProclist = new Proclist();


           for(int i=0; i<task_graph.total_tasks; i = i + lookAheadNumber){
                System.out.println("create prem task "+ sccc +" i="+i);
                sccc++;
               //先読みタスクの配列を生成
               for(index = i; index < i+lookAheadNumber; index++){
                   if(index < task_graph.total_tasks){
                       System.out.println("index="+index +" task_graph.task[index]="+task_graph.task[index]);
                       lookAheadTasks[index - i] = task_graph.task[index].Clone();
                       System.out.print("perm tasks["+(index-i)+"] :");
                       lookAheadTasks[index - i].output_result();
                   }else{
                       lookAheadNumber = index -i;
                       break;
                   }
               }               
               System.out.println(" lookaheadnumber="+ lookAheadNumber);
               ArrayList<Tasknode[]> combitasks = new ArrayList<Tasknode[]>();
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
               
              //先読みタスクの配列が完成したら割り当て処理を行う
              for(int combi = 0; combi < combitasks.size(); combi++){
               
                //System.exit(0);
                DAG[] CloneTaskGraph = new DAG[3];
                CloneTaskGraph[0] = new DAG();
                CloneTaskGraph[0] = task_graph.Clone();
                Proclist[] CloneProclist = new Proclist[3];
                CloneProclist[0] = new Proclist();
                CloneProclist[0] = Plist.Clone();
                System.out.println("■■■■■■■■■■■■■■■■■number:"+combi+" clone taskGraph and Proclist output data■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
              

                //組み合わせ順列の結果をクローンTaskGraphに反映
                CloneTaskGraph[0].changeTasknode(i, combitasks.get(combi));
              
                  System.out.println("△△△△△△△△△△△△△△△△ combi"+combi+" start △△△△△△△△△△△△△△△△△△△△△△△△△△△△△△");
                    for(int n = i; n < i + lookAheadNumber; n++){
                        min = Double.MAX_VALUE;
                        start_time = 0;
                        pred_finish_time = 0;
                        proc_ftime = 0;
                        tdr = 0;
                        

                        
                        System.out.println("---------------------------------------------------------task"+CloneTaskGraph[0].task[n].getID() +"schduling ------------------------------------------------------------------------");
                        for(int p=0; p<CloneProclist[0].procnum; p++){
                            if(CloneProclist[0].procs[p].getSw() != 1){
                                for(int core = 0; core < CloneProclist[0].procs[p].getCore_number() ; core++){
                                    CloneTaskGraph[1] = CloneTaskGraph[0].Clone();
                                    CloneProclist[1] = CloneProclist[0].Clone();
                                    start_time = 0;
                                    for(int xx = 0; xx < CloneTaskGraph[1].task[n].copyExecuteFrequency.length; xx++){
                                        CloneTaskGraph[1].task[n].copyExecuteFrequency[xx] = 0;
                                        System.out.print(" "+CloneTaskGraph[1].task[n].executeFrequencyStep[xx]);
                                    }
                                    System.out.println();
                                    for(int xx = 0; xx < CloneTaskGraph[1].task[n].copyExecuteFrequency.length; xx++){
                                        System.out.print(" "+CloneTaskGraph[1].task[n].copyExecuteFrequency[xx]);
                                    }
                                    System.out.println("\n■■Check Proc:" + CloneProclist[1].procs[p].getProcID() +" Core:" + core);
                                    start_time = t_s(CloneTaskGraph[1].task[n], p, core, CloneTaskGraph[1], CloneProclist[1]);
                                    finish_time = t_f(start_time, CloneTaskGraph[1].task[n],p,core, CloneTaskGraph[1], CloneProclist[1]);
                                    System.out.println("\n start time="+start_time+"finish time="+finish_time +" minimumtime="+min);
                                    setVariableToCurrentTask(CloneTaskGraph[1].task[n], start_time, finish_time, p, core);
                                    proc_ID = p;
                                    core_ID = core;
                                    task_ID = n;
                                    this.communicationTime(CloneTaskGraph[1], CloneProclist[1], task_ID);
                                    CloneTaskGraph[1].outputResult();
                                    //コア側にどのタスクを処理して終了時刻が何時かを保持させる
                                    CloneProclist[1].procs[proc_ID].Cores[core_ID].setExecuteTask(CloneTaskGraph[1].task[n]);
                                    CloneProclist[1].procs[proc_ID].Cores[core_ID].setEndTime(CloneTaskGraph[1].task[n].getFinish_time());

                                    for(int procI = 0; procI < CloneProclist[1].procnum; procI++){
                                        if(CloneProclist[1].procs[procI].getSw() == 0){
                                            //System.out.println("●●●●●●●●●●●●●●●● Proc ID"+CloneProclist.procs[procI].getProcID()+"●●●●●●●●●●●●●●●●●●●●");
                                            //System.out.print("input task start time to adjusterTBHT "); 
                                            CloneProclist[1].procs[procI].adjusterTBHT(CloneTaskGraph[1], CloneProclist[1], CloneTaskGraph[1].task[task_ID].getStart_time()); 
                                            //Proc側に保持させているタスクの情報をここで更新（現在のタスクまで）
                                            for(int c = 0; c < n; c++){
                                                int sampleindex = -1;
                                                for(int counterExecuteTaskList = 0; 
                                                        counterExecuteTaskList < CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].ExecuteTask.size();
                                                        counterExecuteTaskList++)
                                                    if(((Tasknode)CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].ExecuteTask.get(counterExecuteTaskList)).getID()
                                                            == CloneTaskGraph[1].task[c].getID()){
                                                        sampleindex = counterExecuteTaskList;
                                                    }

                                                    CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].ExecuteTask.set(sampleindex, CloneTaskGraph[1].task[c]);
                                                    CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].setEndTime(CloneTaskGraph[1].task[c].getFinish_time());

                                            } 

                                            // System.out.print("input pred task finish time to adjusterTBHT");
                                            CloneProclist[1].procs[procI].adjusterTBHT(CloneTaskGraph[1], CloneProclist[1], CloneTaskGraph[1].searchLatestPredFinishTIme(task_ID));
                                            //Proc側に保持させているタスクの情報をここで更新（現在のタスクまで）
                                            for(int c = 0; c < n; c++){
                                                int sampleindex = -1;
                                                for(int counterExecuteTaskList = 0; 
                                                        counterExecuteTaskList < CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].ExecuteTask.size();
                                                        counterExecuteTaskList++)
                                                    if(((Tasknode)CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].ExecuteTask.get(counterExecuteTaskList)).getID()
                                                            == CloneTaskGraph[1].task[c].getID()){
                                                        sampleindex = counterExecuteTaskList;
                                                    }

                                                    CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].ExecuteTask.set(sampleindex, CloneTaskGraph[1].task[c]);
                                                    CloneProclist[1].procs[CloneTaskGraph[1].task[c].allocate_proc_ID].Cores[CloneTaskGraph[1].task[c].allocate_core_ID].setEndTime(CloneTaskGraph[1].task[c].getFinish_time());

                                            } 
                                        }
                                    }

                                    for(int mmm= 0; mmm<CloneProclist[1].procnum;mmm++)
                                        for(int nnn=0; nnn<CloneProclist[1].procs[mmm].getCore_number(); nnn++)
                                            for(int xxx=0; xxx< CloneProclist[1].procs[mmm].Cores[nnn].ExecuteTask.size();xxx++){
                                                CloneProclist[1].procs[mmm].Cores[nnn].setEndTime(
                                                        ((Tasknode)CloneProclist[1].procs[mmm].Cores[nnn].ExecuteTask.get(xxx)).getFinish_time()                                      
                                                        );
                                                System.out.println("proc="+CloneProclist[1].procs[mmm].getProcID()
                                                            +" core="+CloneProclist[1].procs[mmm].Cores[nnn].getCoreID()
                                                            +" end time="+CloneProclist[1].procs[mmm].Cores[nnn].getEndTime());
                                    }




                                    CloneTaskGraph[1].outputResult();

                                    double totalFinishTime = CloneProclist[1].selectLatestFinishTime();
                                    if(totalFinishTime < min){
                                        System.out.println("∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞クローン2のデータを更新∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞");
                                        CloneTaskGraph[2] = CloneTaskGraph[1].Clone();
                                        CloneProclist[2] = CloneProclist[1].Clone();
                                        min = totalFinishTime;
                                    }
                                    
                                }//end for(int core = 0; core < Plist.procs[p].getCore_number() ; core++)
                            }//end if(CloneProclist[1].procs[procI].getSw() == 0)
                        }//end for(int p=0; p<Plist.procnum; p++)]
                        CloneTaskGraph[2].outputResult();
                        for(int mmm = 0; mmm< 10000; mmm++)
                            System.out.print(String.format(" %3d", mmm));
                        for(int mmm = 0; mmm< CloneTaskGraph[2].total_tasks; mmm++){
                            System.out.print("task"+CloneTaskGraph[2].task[mmm].getID());
                            CloneTaskGraph[2].task[mmm].outputExecuteFrequency();
                            System.out.println("");
                        }
                        

                    }//end for(int n = 0; n < lookAheadNumber; n++)
                    if(combi == 0){
                        SubTaskGraph = CloneTaskGraph[2].Clone();
                        SubProclist = CloneProclist[2].Clone();
                    }else{
                        if(selectOptimizationClone(SubTaskGraph, CloneTaskGraph[2])){
                            System.out.println("†change the SubTaskGraph < CloneTaskGraph");
                            SubTaskGraph.setDAG(CloneTaskGraph[2]);
                            SubProclist.setProclist(CloneProclist[2]);
                        }
                        //combitasks内の情報をtaskgrahに反映

                    }
                   System.gc();
                   System.out.println("△△△△△△△△△△△△△△△△ combi"+combi+" end △△△△△△△△△△△△△△△△△△△△△△△△△△△△△△");
                }//end for(int combi = 0; combi < combitasks.size(); combi++)

                task_graph.setDAG(SubTaskGraph);
                Plist.setProclist(SubProclist);
                task_graph.outputResult(); 
                
            }
            
        }
        
        
        //採用するスケジューリング結果を選択する
        boolean selectOptimizationClone(DAG TaskGraphA, DAG TaskGraphB){
            double TaskAfinalFinishTime;
            double TaskBfinalFinishTime;
            TaskAfinalFinishTime = TaskGraphA.selectLatestFinishTime();
            TaskBfinalFinishTime = TaskGraphB.selectLatestFinishTime();
            if(TaskAfinalFinishTime < TaskBfinalFinishTime)return false;
            else return true;        
        }
        
        
        //
        void setVariableToCurrentTask(Tasknode currentTask, double startTime, double finishTime, int procID, int coreID){
                currentTask.setStart_time(startTime);
                currentTask.setFinish_time(finishTime);
                currentTask.setWorking_time(currentTask.getWorkingsub());
                for(int freqStep = 0; freqStep < currentTask.copyExecuteFrequency.length; freqStep++)
                    currentTask.executeFrequencyStep[freqStep] = 0;
                for(int freqStep = 0; freqStep < currentTask.copyExecuteFrequency.length; freqStep++){
                    if(currentTask.copyExecuteFrequency[freqStep] != 0){
                        //System.out.print("-"+currentTask.copyExecuteFrequency[freqStep]);
                        currentTask.executeFrequencyStep[freqStep] = currentTask.copyExecuteFrequency[freqStep];
                        currentTask.copyExecuteFrequency[freqStep] = 0;
                    }
                }
                currentTask.allocate_proc_ID = procID;//Plist.procs[p].getProcID();
                currentTask.allocate_core_ID = coreID;//Plist.procs[p].Cores[core].getCoreID();
               
                System.out.print("\ntask[" + currentTask.getID() + "]" +" start Time="+ currentTask.getStart_time() +" working Time="+ currentTask.getWorking_time() +" finish Time=" + currentTask.getFinish_time()+" proc="+currentTask.allocate_proc_ID+" core="+ currentTask.allocate_core_ID);
        }
        
        //別コアで実行しているものへの通信時間の計算し,それぞれに加算
        void communicationTime(DAG taskGraph, Proclist Plist, int task_ID){
            System.out.println("in method communicationTIme task:"+taskGraph.task[task_ID].allocate_proc_ID);
            int pred_I;
            int succ_I;
            int[] flag = new int[Plist.procs[taskGraph.task[task_ID].allocate_proc_ID].getCore_number()];
            double min = Double.MAX_VALUE;
            double number;
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
        

        private double f_pred(Tasknode Task, DAG task_graph){
            double ftime = 0;
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

        private double f_proc(Processor Proc, int CoreID){
            //割り当てる予定のコアの処理完了時間を会得
            double ftime = 0;
            double commftime = 0;
            ftime = Proc.Cores[CoreID].getEndTime();
            if(!Proc.Cores[CoreID].ExecuteTask.isEmpty())
                commftime = ((Tasknode)Proc.Cores[CoreID].ExecuteTask.get(Proc.Cores[CoreID].ExecuteTask.size() - 1 )).getCommunicationEndTime();
            if(ftime < commftime)
                ftime = commftime;
            System.out.print(Proc.Cores[CoreID].getEndTime() +" ");
            return ftime;	
        }
        
        private double t_s(Tasknode Task, int proc, int core, DAG task_graph, Proclist Plist){
            //タスク「TaskID」の処理を開始出来る最遅時刻を求める
            double ftime = 0;
            double time = 0;
            //親ノードの最遅処理終了時刻を会得
            if(ftime < (time = f_pred(Task, task_graph))){
                ftime = time;
            }
            System.out.println("pred finish time="+ftime);
            //プロセッサが利用できるようになる時間を会得
            if(ftime < (time = f_proc(Plist.procs[proc], core))){
                ftime = time;
            }
            System.out.println("proc finish time="+ftime);
            //タスクの処理を開始するために必要な時間を加算
            ftime += t_dr(task_graph, Task, proc, core, Plist);
            System.out.println("communication finish time="+ ftime);
            return ftime;
            
        }
            
       


        //タスク[taskID]をプロセッサ[ProcID]のCoreIDで処理した場合の処理時間を計算し，開始時間にそれを加えたものを終了時間として返す
        private double t_f(double stime, Tasknode Task, int Proc_i, int Core_i, DAG task_graph, Proclist Plist){
            Task.setWorkingsub(w(task_graph, Task, Plist.procs[Proc_i], Plist.procs[Proc_i].Cores[Core_i], stime));
            return stime + Task.getWorkingsub();
        }
        
        private double t_dr(DAG task_graph, Tasknode Task, int Proc_i, int Core_i, Proclist Plist){
            CalculationUtil calc = new CalculationUtil();
            double max_ftime = 0, ftime = 0, comm_time = Double.MAX_VALUE;
            Tasknode m;
            int target_proc = 0;
            int task_i;
            for(int i = 0; i < Task.predecessor.size(); i++ ){
                for(task_i = 0; task_i < task_graph.total_tasks; task_i++)
                    if(task_graph.task[task_i].getID() == (Integer)Task.predecessor.get(i))break;
                target_proc = task_graph.task[task_i].allocate_proc_ID;
                max_ftime += calc.searchNode(Plist.procs[Proc_i], Plist.procs[target_proc].getProcID(), 0);
            }
            /*
            double time;
            if(Task.predecessor.size() == 0)comm_time = 0;
            else{ 
                for(int x = 0; x < Task.predecessor.size(); x++){
                    time = 0;
                    Tasknode pred = task_graph.task[task_graph.gettask_i(task_graph, (Integer)Task.predecessor.get(x))];
                    System.out.println("this;"+Plist.procs[Proc_i].getProcID() +" pred;"+ Plist.procs[pred.allocate_proc_ID].getProcID() +" predtaskID"+(Integer)Task.predecessor.get(x));
                    if((time = calc.searchNode(Plist.procs[Proc_i], Plist.procs[pred.allocate_proc_ID].getProcID(), 0)) < comm_time){
                        comm_time = time;
                        System.out.println(" commutime ="+ comm_time);
                    }

                }
            }
            * 
            */
            return max_ftime;
        }
        

        private double t_f(DAG task_graph, int TaskID){
            int index = task_graph.gettask_i(task_graph, TaskID);
            return task_graph.task[index].getFinish_time();
        }
        
        private double w(DAG taskGraph, Tasknode task,Processor Proc, Core C, double startTime){
            int UtilizationOfProcessor = 0;
            UtilizationOfProcessor = Proc.ProcessorUtilization(taskGraph, startTime);
            System.out.println("in method W(DAG, Tasknode, Processor, Core, double) Utilization of Processor =" + UtilizationOfProcessor 
                    +" frequenccy="+(double)C.getFrequency(UtilizationOfProcessor)
                    + " working time="+(double)task.getWeight()/(double)C.getFrequency(UtilizationOfProcessor)
                    +" start Time ="+ startTime);
            task.setsubExecuteFrequency(0, (int)(((double)task.getWeight()/C.getFrequency(UtilizationOfProcessor)) * upperRate), (double)C.getFrequency(UtilizationOfProcessor));
            task.outputExecuteFrequency();
            return (double)task.getWeight()/(double)C.getFrequency(UtilizationOfProcessor);
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
