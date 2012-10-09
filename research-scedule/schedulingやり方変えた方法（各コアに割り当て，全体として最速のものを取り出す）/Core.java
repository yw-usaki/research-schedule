/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.util.ArrayList;

/**
 *
 * @author 1151118
 */
public class Core {
    private int ID;
    private double endTime = 0;
    private double exe_frequency;
    private double[] frequency = new double[4];
    
    ArrayList ExecuteTask = new ArrayList();
     
    Core(int ID, double[] freq){
       // System.out.println("create core");
        this.ID = ID;
        frequency = freq;
        exe_frequency = 0;
    }
    
    Core Clone(){
        Core clone = new Core(this.ID, this.frequency);
        clone.exe_frequency = this.exe_frequency;
        clone.endTime = this.endTime;
        for(int i = 0; i < ExecuteTask.size(); i++){
            clone.ExecuteTask.add(this.ExecuteTask.get(i));
        }
        return clone;
    }
    
    //task_IDで示したタスクが後続関係にある他のタスクに対してこのコアから別のコアに対してデータを転送している間の時間Time を加算し，このコアで実行される後続タスクについても処理時間の調節を行う．
    void addCommunicationCost(DAG taskGraph, int task_ID, double time){
        int flag = 0;
        int index;
        double interval = 0;
        ///double min = Double.MAX_VALUE;
        
        for(int i = 0; i < ExecuteTask.size(); i++){
            if(flag == 1){
                interval = ((Tasknode)ExecuteTask.get(i)).getStart_time() - ((Tasknode)ExecuteTask.get(i-1)).getFinish_time();
                if(interval < time){
                    time -= interval;
                    ((Tasknode)ExecuteTask.get(i)).setStart_time(((Tasknode)ExecuteTask.get(i)).getStart_time() + time);
                    ((Tasknode)ExecuteTask.get(i)).setFinish_time(((Tasknode)ExecuteTask.get(i)).getStart_time() + ((Tasknode)ExecuteTask.get(i)).getWorking_time());
                    ((Tasknode)ExecuteTask.get(i)).setCommunicationEndTime(((Tasknode)ExecuteTask.get(i)).getFinish_time());
                    index = taskGraph.gettask_i(taskGraph, ((Tasknode)ExecuteTask.get(i)).getID());
                    taskGraph.task[index].setStart_time(((Tasknode)ExecuteTask.get(i)).getStart_time());
                    taskGraph.task[index].setFinish_time(((Tasknode)ExecuteTask.get(i)).getFinish_time());
                    System.out.println("adjuster in Core class::start time="+ ((Tasknode)ExecuteTask.get(i)).getStart_time()+" finish time="+ ((Tasknode)ExecuteTask.get(i)).getFinish_time());
                    //min = ((Tasknode)ExecuteTask.get(i)).getStart_time();
                }
                flag = 2;
            }
            if(flag == 2){
                    if(((Tasknode)ExecuteTask.get(i)).getStart_time() < ((Tasknode)ExecuteTask.get(i - 1)).getFinish_time()){
                        ((Tasknode)ExecuteTask.get(i)).setStart_time(((Tasknode)ExecuteTask.get(i - 1)).getFinish_time());
                        ((Tasknode)ExecuteTask.get(i)).setFinish_time(((Tasknode)ExecuteTask.get(i)).getStart_time() + ((Tasknode)ExecuteTask.get(i)).getWorking_time());
                    }
            }
            if(((Tasknode)ExecuteTask.get(i)).getID() == task_ID)flag =1;
        }
        setEndTime(((Tasknode)ExecuteTask.get(ExecuteTask.size()-1)).getFinish_time());
        System.out.print("CoreID="+getCoreID());
        System.out.println(" Procの終了時間を更新　　 set end time in method addCommunicationCost  "+ getEndTime()+" taskID="+ ((Tasknode)ExecuteTask.get(ExecuteTask.size()-1)).getID());
        //return min;
    }
    
    void nomalAdjuster(Tasknode task){
        int flag = 0;
        DAG taskgraph = new DAG();
        Tasknode succtask;
        
        System.out.println("in method normal ajuster");
        for(int i = 0; i < ExecuteTask.size(); i++){
            if(flag == 1){
                System.out.println("start time ="+ ((Tasknode)ExecuteTask.get(i)).getStart_time()+ " pred task finish Time ="+ ((Tasknode)ExecuteTask.get(i - 1)).getFinish_time());   
                if(((Tasknode)ExecuteTask.get(i)).getStart_time() < ((Tasknode)ExecuteTask.get(i - 1)).getFinish_time()){
                    ((Tasknode)ExecuteTask.get(i)).setStart_time(((Tasknode)ExecuteTask.get(i - 1)).getFinish_time());
                    ((Tasknode)ExecuteTask.get(i)).setFinish_time(((Tasknode)ExecuteTask.get(i)).getStart_time() + ((Tasknode)ExecuteTask.get(i)).getWorking_time());
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
            if((t_ID = ((Tasknode)ExecuteTask.get(i)).getID()) == taskID)return true;
        return false;       
    }

    void outputCoreData(){
        System.out.print("CoreID = " +getCoreID()+ " endtime = "+getEndTime()+" frequency:");
        for(int i = 0; i < frequency.length; i++)
            System.out.print(getFrequency(i)+" ");
        System.out.println();
    }
    //入力された時間帯にコアが使用されているかを判断
    int CheckUsed(double time){
        int i;
        for(i= 0; i < ExecuteTask.size(); i++){
            if((getExecuteTask(i).getStart_time() <= time) && (time < getExecuteTask(i).getFinish_time())){
                //System.out.println("task "+getExecuteTask(i).getID()+" start" + getExecuteTask(i).getStart_time() + "  time "+ time + " finish "+getExecuteTask(i).getFinish_time());
                return getExecuteTask(i).getID();
                
            }
        }
        return -1;
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
        return (Tasknode)ExecuteTask.get(i);
    }

    /**
     * @param ExecuteTask the ExecuteTask to set
     */
    public void setExecuteTask(Tasknode ExecuteTask) {
        this.ExecuteTask.add(ExecuteTask);
    }

    /**
     * @return the exe_frequency
     */
    public double getExe_frequency() {
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
    public void setFrequency(double[] frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the endTime
     */
    public double getEndTime() {
        return endTime;
    }

    /*
     * @param endTime the endTime to set
     */
    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }
    double getFrequency(int i){
        //ystem.out.println("in method getFrequency i="+i);
        if(i < 0)i =0;
        return frequency[i];
    }
    
}