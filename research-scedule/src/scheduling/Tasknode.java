package scheduling;

import java.io.Serializable;
import java.util.ArrayList;

 class Tasknode implements Serializable, Cloneable{
    static final boolean printOutFlag = false;
    
    private int ID;
    float totalWeight;
    ArrayList<Float> weight;
    ArrayList<Float> restweight;
    private int connection_number;
    private float workingsub;
    int index;
    int allocate_proc_I;
    int allocate_core_I;
    int bl;
    int UpperRate = 50;
    ArrayList<Integer> successor = new ArrayList<Integer>();
    ArrayList<Integer> predecessor = new ArrayList<Integer>();
    ArrayList<Float> start_time;
    ArrayList<Float> working_time;
    ArrayList<Float> finish_time;
    ArrayList<Float> ExFrequency;
    float communicationTime = 0;
    ArrayList edge_cost_r = new ArrayList();
    ArrayList edge_cost_s = new ArrayList();
    boolean permFlag;
    boolean recheckFlag = false;
        
    Tasknode(){
        float initial = 0;
        this.restweight = new ArrayList<Float>();
        this.weight = new ArrayList<Float>();
        this.start_time = new ArrayList<Float>();
        this.working_time = new ArrayList<Float>();
        this.finish_time = new ArrayList<Float>();
        this.ExFrequency = new ArrayList<Float>();

        this.start_time.add((initial));
        this.working_time.add(initial);
        this.finish_time.add(initial);
        this.restweight.add(initial);
        this.weight.add(initial);
        this.ExFrequency.add(initial);
        //System.out.println("create tasknode");
    }

    Tasknode(int from, int to, float stime, float commTime){
        this.start_time.add(stime);
        this.working_time.add(commTime);
        this.finish_time.add(this.start_time.get(0) + this.working_time.get(0));
        this.ExFrequency.add((float)100);
    }
    public void setTasknode(Tasknode task){
        this.setID(task.getID());
        for(int i = 0; i < task.weight.size(); i++)
            this.weight.set(i, task.weight.get(i));
        for(int i = 0; i <task.start_time.size(); i++){
            this.start_time.set(i, task.start_time.get(i));
            this.working_time.set(i, task.working_time.get(i));
            this.finish_time.set(i, task.finish_time.get(i));
        }
        for(int i = 0; i < task.restweight.size(); i++)
            this.restweight.set(i, task.restweight.get(i));
        this.setWorkingsub(task.getWorkingsub());
        this.setConnection_number(task.getConnection_number());
        this.allocate_proc_I = task.allocate_proc_I;
        this.allocate_core_I = task.allocate_core_I;
        this.bl = task.bl;
        this.index = task.index;
        for(int i = 0; i < successor.size(); i++){
            this.successor.set(i, (Integer)task.successor.get(i));
            this.edge_cost_s.set(i, task.edge_cost_s.get(i));
        }
        for(int i = 0; i < predecessor.size(); i++){
            this.predecessor.set(i, task.predecessor.get(i));
            this.edge_cost_r.set(i, task.edge_cost_r.get(i));
        }
        this.ExFrequency = task.ExFrequency;
        this.communicationTime = task.communicationTime;
        this.recheckFlag = task.recheckFlag;
    }


    void copyTasknode(Tasknode task){
        this.setID(task.getID());
        //task.output_result();
        for(int i = 0; i < 1; i++)
            this.weight.set(i, task.weight.get(i));
        for(int i = 0; i <1; i++){
            this.start_time.set(i, task.start_time.get(i));
            this.working_time.set(i, task.working_time.get(i));
            this.finish_time.set(i, task.finish_time.get(i));
        }
        this.communicationTime = task.communicationTime;
        this.recheckFlag = task.recheckFlag;
        for(int i = 0; i < 1; i++)
            this.restweight.set(i, task.restweight.get(i));
        this.setConnection_number(task.getConnection_number());
        this.setWorkingsub(task.getWorkingsub());
        this.allocate_proc_I = task.allocate_proc_I;
        this.allocate_core_I = task.allocate_core_I;
        this.bl = task.bl;
        this.index = task.index;
        this.successor.clear();
        this.edge_cost_s.clear();
        this.predecessor.clear();
        this.edge_cost_r.clear();
        for(int i = 0; i < task.successor.size(); i++){
            this.successor.add(task.successor.get(i));
            this.edge_cost_s.add(task.edge_cost_s.get(i));
        }
        for(int i = 0; i < task.predecessor.size(); i++){
            this.predecessor.add(task.predecessor.get(i));
            this.edge_cost_r.add(task.edge_cost_r.get(i));
        }
        this.ExFrequency = task.ExFrequency;
    }
    
    void removeParameterAllTimeWeightExfreq(int index){
        this.start_time.remove(index);
        this.working_time.remove(index);
        this.finish_time.remove(index);
        this.weight.remove(index);
        this.ExFrequency.remove(index);
    }
    
    Tasknode Clone(){
        Tasknode Clonetask = new Tasknode();
        Clonetask.setID(this.getID());
        Clonetask.totalWeight = this.totalWeight;
        for(int i = 0; i < this.weight.size(); i++)
            Clonetask.weight.set(i, this.weight.get(i));
        for(int i = 0; i < this.restweight.size(); i++)
            Clonetask.restweight.set(i, this.restweight.get(i));
        Clonetask.communicationTime = this.communicationTime;
        Clonetask.recheckFlag = this.recheckFlag;
        Clonetask.setWorkingsub(this.getWorkingsub());
        Clonetask.setConnection_number(this.getConnection_number());
        for(int i = 0; i < this.start_time.size(); i++){
            Clonetask.start_time.set(i, this.start_time.get(i));
            Clonetask.working_time.set(i, this.working_time.get(i));
            Clonetask.finish_time.set(i, this.finish_time.get(i));
            Clonetask.ExFrequency.set(i, this.ExFrequency.get(i));
        }
        Clonetask.allocate_proc_I = this.allocate_proc_I;
        Clonetask.allocate_core_I = this.allocate_core_I;
        Clonetask.bl = this.bl;
        Clonetask.index = this.index;
        for(int i = 0; i < this.successor.size(); i++){
            Clonetask.successor.add(this.successor.get(i));
            Clonetask.edge_cost_s.add(this.edge_cost_s.get(i));
        }
        for(int i = 0; i < this.predecessor.size(); i++){
            Clonetask.predecessor.add(this.predecessor.get(i));
            Clonetask.edge_cost_r.add(this.edge_cost_r.get(i));
        }           
        return Clonetask;
    }


    public void output_data(){
        //if(printOutFlag){
            System.out.print("task["+getID()+"] W="+weight.get(0) +" bl="+bl+" comnum="+getConnection_number()+" pred:");
            for(int i=0; i<predecessor.size(); i++)System.out.print(predecessor.get(i) +" ");
            System.out.print("succ=");
            for(int i=0; i<successor.size(); i++)System.out.print(successor.get(i)+" ");
            System.out.print("edgecost_r:");
            for(int i=0; i<edge_cost_r.size(); i++)System.out.print(edge_cost_r.get(i) +" ");
            System.out.println("");
        //}
    }

    public void output_result(){
            System.out.print("task["+String.format("%4d", getID())+"] totalWeight="+String.format("%4.1f", totalWeight)+" W=");
            for(int i = 0; i < weight.size(); i++)
                System.out.print(" "+String.format("%4.1f", weight.get(i)));
            System.out.print(
                        " step "+weight.size()
                    +" restWeight="+ String.format("%4.2f", restweight.get(0))
                    +" bl="+bl);
            System.out.print(
                        " ProcID="+(allocate_proc_I+1)
                    +" CoreID="+ (allocate_core_I+1) 
                    +" Start_time=");
            for(int i = 0; i < start_time.size(); i++)
                System.out.print(" "+String.format("%4.2f", start_time.get(i)));
            System.out.print(
                        " step "+ start_time.size()
                    +" Working_time=");
            for(int i = 0; i < working_time.size(); i++)
                System.out.print(" "+String.format("%4.2f", working_time.get(i)));
            System.out.print(" Exfreq:");
            for(int i = 0; i < ExFrequency.size(); i++)
                System.out.print(" "+ String.format("%3.2f", ExFrequency.get(i)));
            System.out.print(
                    " finish_time=");
            for(int i = 0; i < finish_time.size(); i++)
                System.out.print(" "+String.format("%4.2f", finish_time.get(i)));
            System.out.print("\tpred:");
            for(int i=0; i<predecessor.size(); i++)System.out.print(predecessor.get(i)+"-"+edge_cost_r.get(i)+" ");
            System.out.print("\tsucc=");
            for(int i=0; i<successor.size(); i++)System.out.print(successor.get(i)+"-"+edge_cost_s.get(i)+" ");                
            System.out.println("commtime "+communicationTime + " divide?"+ recheckFlag);
    }

    void adjusterSameProcessor(DAG taskGraph, float PredFinishTime){
        int index;
        if(printOutFlag)
            System.out.println("in method normal ajuster Same Processor\ntask"+getID()+" PredFinishTime="+PredFinishTime);
        if(this.start_time.get(0) != 0){
            if(this.start_time.get(0) < PredFinishTime){
                this.start_time.set(0, PredFinishTime);
                this.finish_time.set(0, this.start_time.get(0) + this.working_time.get(0));
                if(printOutFlag)
                    this.output_result();
            }

            for(int i = 0; i < successor.size(); i++){
                index = taskGraph.gettask_i(taskGraph, (Integer)successor.get(i));
                //if(taskGraph.task[index].allocate_proc_ID == this.allocate_proc_ID){
                if(printOutFlag)
                    System.out.println("index="+index+" successer="+(Integer)successor.get(i));
                taskGraph.task[index].adjusterSameProcessor(taskGraph, this.finish_time.get(0));
                //}else{
                //   taskGraph.task[index].adjusterDifferentProcessor(taskGraph, this.getFinish_time());
                //}
            }
        }
    }
    
    float getTaskFinishTime(){
        return finish_time.get(finish_time.size() - 1);
    }
    
    /**
     * @return the ID
     */
    public int getID() {
        return ID;
    }
    
    /**
     * @param ID the ID to set
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * @return the connection_number
     */
    public int getConnection_number() {
        return connection_number;
    }

    /**
     * @param connection_number the connection_number to set
     */
    public void setConnection_number(int connection_number) {
        this.connection_number = connection_number;
    }


    /**
     * @return the workingsub
     */
    public float getWorkingsub() {
        return workingsub;
    }

    /**
     * @param workingsub the workingsub to set
     */
    public void setWorkingsub(float workingsub) {
        this.workingsub = workingsub;
    }

}