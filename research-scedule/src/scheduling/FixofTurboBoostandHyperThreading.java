package scheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduling.DAG;
import scheduling.Proclist;
import scheduling.Processor;
import scheduling.Tasknode;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 1151118
 */
public class FixofTurboBoostandHyperThreading {
    
    
    public DAG fixOfTBHT(DAG taskGraph, Proclist Plist, boolean printFlag){
        float stime = 0;
        float etime = 0;
        float miniWeight;
        float minitime  = Float.MAX_VALUE;
        float oldWeight = Float.MAX_VALUE;
        float oldtime = 0;
        float ftime;
        
        //System.out.println("∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞v∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞");
        //全プロセッサコアの中で最も早く処理が終わるタスクの処理完了時刻を探す
        //ただし，もうTBHTの調節を行った部分を除く
        while(true){
            //initialization
            miniWeight = Float.MAX_VALUE;
            ftime = 0;
            
            //find earliest task finish time
            for(int i = 0; i < Plist.procnum; i++){
                if(!Plist.procs[i].getSw()){
                    float weight = Plist.procs[i].findMinimumTaskWeight(taskGraph, Plist, stime, printFlag);
                    if(weight < miniWeight){
                        if(printFlag)System.out.println("fix class miniweight "+ miniWeight);
                        miniWeight = weight;
                    }
                }
            }
            //check error eternal loop
           if(printFlag)System.out.println("miniWeight "+ miniWeight);
            
            if(miniWeight == Float.MAX_VALUE){
                //find earliest task finish time
                miniWeight = 0;
                minitime  = Float.MAX_VALUE;
                if(printFlag)System.out.println("calc new stime");
                for(int i = 0; i < Plist.procnum; i++){
                    if(!Plist.procs[i].getSw()){
                        float time = Plist.procs[i].findEarliestTaskStartTime(printFlag);
                        if(printFlag)System.out.print(" testtime "+ time);
                        if(time < minitime){
                            if(printFlag)System.out.println(" time "+ time+" minitime "+minitime);
                            minitime = time;
                        }
                    }
                }
                stime = minitime;
                if(printFlag)System.out.println("mini time "+ minitime);
            }
            
            
            
            if(printFlag)System.out.println("stime "+stime+" miniWeight "+ miniWeight);
            //fix of turbo boost and hyperthreading for all processor
            for(Processor p : Plist.procs){
                if(!p.getSw()){
                    p.fixOfTBHT(taskGraph, Plist, stime, etime,  miniWeight, printFlag);
                }
            }
            
            //find 
            for(Processor p : Plist.procs){
                if(!p.getSw()){
                    if(ftime == 0)ftime = p.ftime; 
                    if(p.ftime < ftime)ftime = p.ftime; 
                    
                }
            }
            oldtime = stime;
            if(ftime != Float.MAX_VALUE)stime = ftime;
            else stime = 0;
            if(printFlag)System.out.println("next stime "+ stime);
            if(printFlag){
                taskGraph.outputResult();
                Plist.outputProclist();
            }
            if(Plist.checkAllCoresExTaskIndedx())break;
        }

        return taskGraph;
    }    
    
    public DAG fixOfTBHT2(DAG taskGraph, Proclist Plist, boolean flag){
        float[] ProcFreq = new float[Plist.procnum];
        float[] oldProcFreq = new float[Plist.procnum];
        float[] Weight = new float[Plist.procnum];
        float[] MinWeight = new float[Plist.procnum];
        int[] ProcUtil = new int[Plist.procnum]; 
        //int[] oldProcUtil = new int[Plist.procnum]; 
        float Xtime = 0;
        float minitime  = Float.MAX_VALUE;
        float oldWeight = Float.MAX_VALUE;
        float oldtime = 0;
        float ftime;
        int divideFlag = -1;
        
        //initialized 
        for(float x : Weight)x = 0;
        for(float x : MinWeight)x = Float.MAX_VALUE;
        for(int x = 0; x < Plist.procnum; x++){
            ProcUtil[x] = -1;
            ProcFreq[x] = -1;
            oldProcFreq[x] = -1;
            //oldProcUtil[x] = -1;
        }
        
        
        //System.out.println("∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞v∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞∞");
        //全プロセッサコアの中で最も早く処理が終わるタスクの処理完了時刻を探す
        //ただし，もうTBHTの調節を行った部分を除く
        while(true){
            if(flag)System.out.println("∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈∈");
            for(int ProcI = 0; ProcI < Plist.procnum; ProcI++){
            if(!Plist.procs[ProcI].getSw()){
                Processor targetProc = Plist.procs[ProcI];
                
                //calc start time of each task on all core
                for(int coreI = 0; coreI < Plist.procs[ProcI].getCore_number(); coreI++){
                    if(targetProc.Cores[coreI].ExTaskIndex != -1)
                        targetProc.calcStartTime(taskGraph, targetProc.Cores[coreI].ExecuteTask.get(targetProc.Cores[coreI].ExTaskIndex), Plist, flag);
                }
            
                //start タスクへの影響の有無を調べ，影響ありならresttasksに追加
                for(int coreI = 0; coreI < targetProc.getCore_number(); coreI++){
                    if(targetProc.Cores[coreI].findExeTask(Xtime) && targetProc.flagFixExTasks[coreI]){
                        if(flag)System.out.println("add resttasks with task" + targetProc.Cores[coreI].getExecuteTask(targetProc.Cores[coreI].ExTaskIndex).getID());
                        targetProc.addFixExTasks(targetProc.Cores[coreI].getExecuteTask(targetProc.Cores[coreI].ExTaskIndex));
                        targetProc.flagFixExTasks[coreI] = false;
                        //ExtaskID[coreI] = Plist.procs[ProcI].Cores[coreI].getExecuteTask(Plist.procs[ProcI].Cores[coreI].ExTaskIndex).getID();
                    }
                }
            
                if(flag)System.out.print("Xtime " +Xtime+" Wieght " +Weight[ProcI]+" Proc "+targetProc.getProcID()+" FixExtasks :");
                if(flag)targetProc.outputFixExtasks();
                if(flag)System.out.println();
                
                //対象時間にタスクの処理が行われるならば．
                if(targetProc.FixExtasks.size() != 0){
                    //calc Proc Util and Frequency
                    ProcUtil[ProcI] = targetProc.ProcessorUtilization(Xtime);
                    ProcFreq[ProcI] = targetProc.Cores[0].getFrequency(ProcUtil[ProcI]);
                    if(flag)System.out.println("new procUtil "+ProcUtil[ProcI]+" oldProcFreq "+oldProcFreq[ProcI]+ " ProcFreq "+ProcFreq[ProcI]);
                    //タスクが増えたりした場合に一度其処で分割し，WeightをリセットかつもしFixExTasks内のタスクが処理完了していればそれを取り除く
                    if(oldProcFreq[ProcI] != ProcFreq[ProcI]){
                        if(flag)System.out.println("change exe frequency");
                        for(int fixTaskIndex = 0; fixTaskIndex < targetProc.FixExtasks.size(); fixTaskIndex++){
                            boolean removeFlag = this.splitTasknode(taskGraph, Plist, targetProc.getFixExTasks(fixTaskIndex), targetProc.Cores[targetProc.getFixExTasks(fixTaskIndex).allocate_core_I], Xtime, oldProcFreq[ProcI], flag);
                            if(removeFlag){
                                targetProc.flagFixExTasks[targetProc.FixExtasks.get(fixTaskIndex).allocate_core_I] = true;
                                targetProc.removeFixExTasks(fixTaskIndex);
                                fixTaskIndex--;
                            }
                        }
                        Weight[ProcI] = 0;
                    }
                    if(this.compareTaskWeight(targetProc.FixExtasks, Weight[ProcI])){
                        if(flag)System.out.println("equal or over  Weight"+Weight[ProcI]+" Xtime "+Xtime);
                        for(int fixTaskIndex = 0; fixTaskIndex < targetProc.FixExtasks.size(); fixTaskIndex++){
                            boolean removeFlag = this.splitTasknode(taskGraph, Plist, targetProc.getFixExTasks(fixTaskIndex), targetProc.Cores[targetProc.getFixExTasks(fixTaskIndex).allocate_core_I], Xtime, oldProcFreq[ProcI], flag);
                            if(removeFlag){
                                targetProc.flagFixExTasks[targetProc.FixExtasks.get(fixTaskIndex).allocate_core_I] = true;
                                targetProc.removeFixExTasks(fixTaskIndex);
                                fixTaskIndex--;
                            }
                        }
                        Weight[ProcI] = 0;
                    }
                    /*
                    //タスクの重さがプロック内の最小値に到達した時
                    if(MinWeight[ProcI] >= Weight[ProcI]){
                        
                    }
                    * 
                    */

                    oldProcFreq[ProcI] = ProcFreq[ProcI];
                    //calc weight
                    if(!targetProc.getSw())Weight[ProcI] += ProcFreq[ProcI] * 0.1;
                }
            }//end if(!Plist.procs[ProcI].getSw()){
            }//end for(int ProcI = 0; ProcI < Plist.procnum; ProcI++){
            Xtime+=0.1;
            //end flag
            if(Plist.checkAllCoresExTaskIndedx())break;
        }

        return taskGraph;
    }

 
    
    //タスクの処理時間をtimeの時点で分割し，それまでの動作周波数currentfrequencyとして保存
    boolean splitTasknode(DAG taskGraph, Proclist Plist, Tasknode task, Core core, float time, float currentFrequency, boolean flag){
        boolean removeFlag = false;
        int index = task.start_time.size() - 1;
        
        if(flag)System.out.println("in splitTasknode index = "+ index);
        CalculationUtil calUtil = new CalculationUtil();
        if(flag)taskGraph.task[taskGraph.gettask_i(taskGraph, task.getID())].output_result();
        if(flag)task.output_result();
        //if(task.start_time.get(task.start_time.size() - 1) != time){
            if(flag)System.out.println("different start time and dividetime");
            //後続のデータ格納領域を生成と初期化
            task.start_time.add(time);
            task.working_time.add((float)0);
            task.finish_time.add((float)-1);                
            task.weight.add(task.weight.get(index));
            task.ExFrequency.add((float)2.0);
            if(flag)System.out.println("task "+task.getID()+" index weight "+ task.weight.get(index) +" index+1 weight "+ task.weight.get(index + 1));
            if(flag)task.output_result();
            //time以前の情報を保存
            task.working_time.set(index, time - task.start_time.get(index));
            task.finish_time.set(index, time);
            task.weight.set(index, calUtil.carryUp1(task.working_time.get(index) * currentFrequency) < 0 ? 0 : calUtil.carryUp1(task.working_time.get(index) * currentFrequency));
            task.ExFrequency.set(index, currentFrequency);
            if(flag)System.out.println("task "+task.getID()+" index weight "+ task.weight.get(index) +" index+1 weight "+ task.weight.get(index + 1) +" calc "+calUtil.carryUp1(task.working_time.get(index) * currentFrequency));
            if(flag)task.output_result();
            //後続データの残り処理量を計算，算出した処理量を定格の消費電力で動作させた場合の処理時間及び処理完了時刻の再計算と格納
            task.weight.set(index + 1, task.weight.get(index + 1) - task.weight.get(index));
            task.working_time.set(index + 1, task.weight.get(index + 1) / task.ExFrequency.get(index + 1));
            if(task.weight.get(index+1) < 0){
                System.out.println("error minus ");
                task.output_result();
                System.exit(0);
            }
            if(task.weight.get(index) < 0){
                System.out.println("error minus ");
                task.output_result();
                System.exit(0);
            }
            //タスクの処理を全て完了した物に対する処理
            if(task.weight.get(index + 1) <= 0){
                if(flag)System.out.print("task"+ task.getID()+" adjuster finished");
                task.weight.remove(index + 1);
                task.start_time.remove(index + 1);
                task.working_time.remove(index + 1);
                task.finish_time.remove(index + 1);
                task.ExFrequency.remove(index + 1);
                if(flag)System.out.print(" coreID "+core.getCoreID()+" before exindex="+core.ExTaskIndex);
                core.ExTaskIndex++;
                if(core.ExTaskIndex == core.ExecuteTask.size())
                    core.ExTaskIndex = -1;
                if(flag)System.out.println(" after exindex="+core.ExTaskIndex);
                removeFlag = true;
            }
            /*
        }else{
            if(flag)System.out.println("equal start time and dividetime");
            if(task.getID() == 0 || task.getID() == taskGraph.total_tasks - 1){
                core.ExTaskIndex++;
                if(core.ExTaskIndex == core.ExecuteTask.size())
                    core.ExTaskIndex = -1;
                task.working_time.set(index, time - task.start_time.get(index));
                task.finish_time.set(index, time);
                task.ExFrequency.set(index, currentFrequency);
            }else{
                System.out.println("else in else");
            }
        }
        * 
        */
        //task.output_result();
        margeTasknode(task);
        //task.output_result();
        return removeFlag;
    }
    
    
    //タスク内の分割された処理時間に対し，連続して同じ動作周波数で動作している部分についてマージする
    void margeTasknode(Tasknode task){
        float freq = 0;
        int index = 0;

        for(int i = 0; i < task.start_time.size(); i++){
            if(i == 0){
                freq = task.ExFrequency.get(i);
            }else{
                if(freq != task.ExFrequency.get(i)){
                    freq = task.ExFrequency.get(i);
                    index = i;
                }else{
                    //処理時間，仕事量の結合
                    task.weight.set(index, task.weight.get(index) + task.weight.get(i));
                    task.working_time.set(index, task.working_time.get(index) + task.working_time.get(i));
                    task.finish_time.set(index, task.finish_time.get(i));
                    //不必要な情報の削除
                    task.weight.remove(i);
                    task.start_time.remove(i);
                    task.working_time.remove(i);
                    task.finish_time.remove(i);
                    task.ExFrequency.remove(i);
                    i--;
                }
            }
            
        }
    }
    
    //処理した仕事量とタスクの保有する仕事量を比較する
    boolean compareTaskWeight(ArrayList<Tasknode> restTasknodes, float exWeight){
        
        for(int i = 0; i < restTasknodes.size(); i++){
            if(restTasknodes.get(i).weight.get(restTasknodes.get(i).weight.size() - 1) <= exWeight){
                //System.out.print("task "+restTasknodes.get(i).getID()+" ");
                return true;
            }
        }
        
        return false;
    }
    
    /*
    //後続タスクの処理開始時刻と通信時間を再計算する
    void recalcualteStartTimeAndCommunicationTime(DAG taskGraph, Tasknode task, Proclist Plist){
        CalculationUtil calUtil = new CalculationUtil();
        float latestPredFinishTime = -1;
        int procIndex = -1;
        int coreIndex = -1;
        float commTime = 0;
        int taskIndex = Plist.procs[task.allocate_proc_I].Cores[task.allocate_core_I].searchIndexOftask(task.getID()) -1;
        float coreFinishTime = 0;
        System.out.println("----end initialization task"+task.getID());
        //親ノードの中で最も処理完了時刻が遅いものを見つける
        for(int i = 0; i < task.predecessor.size(); i++){
            int index = taskGraph.gettask_i(taskGraph, task.predecessor.get(i));
            if(latestPredFinishTime < taskGraph.task[index].finish_time.get(taskGraph.task[index].finish_time.size() - 1)){
                latestPredFinishTime = taskGraph.task[index].finish_time.get(taskGraph.task[index].finish_time.size() - 1);
                System.out.println(
                        "pred task"+taskGraph.task[index].getID()+ 
                        " procID"+taskGraph.task[index].allocate_proc_I+
                        " core"+taskGraph.task[index].allocate_core_I +
                        " ftime "+taskGraph.task[index].finish_time.get(taskGraph.task[index].finish_time.size() - 1));
                procIndex = taskGraph.task[index].allocate_proc_I;
                coreIndex = taskGraph.task[index].allocate_core_I;
            }
        }
        System.out.println("end pred recalc");
        if(taskIndex != -1){
            Tasknode CorePredTask = Plist.procs[task.allocate_proc_I].Cores[task.allocate_core_I].ExecuteTask.get(taskIndex);
            CorePredTask.output_result();
            coreFinishTime = CorePredTask.finish_time.get(CorePredTask.finish_time.size() - 1);
        }
        
        if(latestPredFinishTime < coreFinishTime)
            latestPredFinishTime = coreFinishTime;
        System.out.println("end corepred");
        
        //見つけた最も遅いノードが処理されているプロセッサから自分のプロセッサへの通信時間を計算
        System.out.println("latesttime "+ latestPredFinishTime +" procIndex " + procIndex+" coreIndex "+coreIndex);
        for(int i = 0; i < task.predecessor.size(); i++){
            int predIndex = taskGraph.gettask_i(taskGraph, task.predecessor.get(i));
            if(task.allocate_proc_I != taskGraph.task[predIndex].allocate_proc_I)
                commTime += calUtil.calculationOfHopCost(Plist.procs[task.allocate_proc_I], Plist.procs[procIndex].getProcID(), 0);
        }
        System.out.println("Proc" + Plist.procs[task.allocate_proc_I].getProcID() + "target Proc "+ Plist.procs[procIndex].getProcID() +" commtime "+ commTime);
        latestPredFinishTime += commTime;
        System.out.println("latestPredfinishtime "+latestPredFinishTime);
        recalcStartWorkFinishTime(task, latestPredFinishTime);
        System.out.println("finish recalc function");
    }
    * 
    */
    
    void recalcStartWorkFinishTime(Tasknode task, float time){
        float stime = time;
        for(int i = 0; i < task.start_time.size(); i++){
            task.start_time.set(i, stime);
            task.finish_time.set(i, task.start_time.get(i) + task.working_time.get(i));
            stime = task.finish_time.get(i);
            System.out.println("step "+ i + " starttime "+ task.start_time.get(i)+" workingtime "+ task.working_time.get(i) + " finishtime "+ task.finish_time.get(i));
        }
    }
    
}
