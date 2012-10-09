package scheduling;

import java.io.Serializable;
import java.util.ArrayList;

public class Tasknode implements Serializable, Cloneable{
	private int ID;
	private int weight;
        private double restweight;
	private int connection_number;
        private double workingsub;
        int index;
        int allocate_proc_ID;
        int allocate_core_ID;
        int bl;
        int UpperRate = 50;
        ArrayList successor = new ArrayList();
        ArrayList predecessor = new ArrayList();
        private double start_time;
        private double working_time;
        private double finish_time;
        private double communicationEndTime = 0;
        double[] executeFrequencyStep;
        double[] copyExecuteFrequency;
        ArrayList edge_cost_r = new ArrayList();
        ArrayList edge_cost_s = new ArrayList();
	boolean permFlag;
        
	Tasknode(){
            this.setStart_time(0);
            this.setFinish_time(0);
            this.setRestweight(0);
            this.initializeExecuteFrequencyStep(10000, 0);
            //System.out.println("create tasknode");
	}
	
        public void setTasknode(Tasknode task){
            
            this.setID(task.getID());
            this.setWeight(task.getWeight());
            this.setRestweight(task.getRestweight());
            this.setWorkingsub(task.getWorkingsub());
            this.setConnection_number(task.getConnection_number());
            this.setStart_time(task.getStart_time());
            this.setWorking_time(task.getWorking_time());
            this.setFinish_time(task.getFinish_time());
            this.allocate_proc_ID = task.allocate_proc_ID;
            this.allocate_core_ID = task.allocate_core_ID;
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
            for(int i = 0; i < task.executeFrequencyStep.length; i++){
                this.executeFrequencyStep[i] = task.executeFrequencyStep[i];
                this.copyExecuteFrequency[i] = task.copyExecuteFrequency[i];
            }
        }
        
        void copyTasknode(Tasknode task){
            this.setID(task.getID());
            this.setWeight(task.getWeight());
            this.setRestweight(task.getRestweight());
            this.setConnection_number(task.getConnection_number());
            this.setStart_time(task.getStart_time());
            this.setWorking_time(task.getWorking_time());
            this.setWorkingsub(task.getWorkingsub());
            this.setFinish_time(task.getFinish_time());
            this.allocate_proc_ID = task.allocate_proc_ID;
            this.allocate_core_ID = task.allocate_core_ID;
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
            for(int i = 0; i < task.executeFrequencyStep.length; i++){
               this.executeFrequencyStep[i] = task.executeFrequencyStep[i];
               this.copyExecuteFrequency[i] = task.copyExecuteFrequency[i];
           }
       }

       Tasknode Clone(Tasknode task){
           Tasknode Clonetask = new Tasknode();
           Clonetask.setID(task.getID());
           Clonetask.setWeight(task.getWeight());
           Clonetask.setRestweight(task.getRestweight());
           Clonetask.setWorkingsub(task.getWorkingsub());
           Clonetask.setConnection_number(task.getConnection_number());
           Clonetask.setStart_time(task.getStart_time());
           Clonetask.setWorking_time(task.getWorking_time());
           Clonetask.setFinish_time(task.getFinish_time());
           Clonetask.allocate_proc_ID = task.allocate_proc_ID;
           Clonetask.allocate_core_ID = task.allocate_core_ID;
           Clonetask.bl = task.bl;
           Clonetask.index = task.index;
           for(int i = 0; i < task.successor.size(); i++){
               Clonetask.successor.add(task.successor.get(i));
               Clonetask.edge_cost_s.add(task.edge_cost_s.get(i));
           }
           for(int i = 0; i < task.predecessor.size(); i++){
               Clonetask.predecessor.add(task.predecessor.get(i));
               Clonetask.edge_cost_r.add(task.edge_cost_r.get(i));
           }           
           for(int i = 0; i < task.executeFrequencyStep.length; i++){
               Clonetask.executeFrequencyStep[i] = task.executeFrequencyStep[i];
               Clonetask.copyExecuteFrequency[i] = task.copyExecuteFrequency[i];
           }
           return Clonetask;
       }
        
        Tasknode Clone(){
           Tasknode Clonetask = new Tasknode();
           Clonetask.setID(this.getID());
           Clonetask.setWeight(this.getWeight());
           Clonetask.setRestweight(this.getRestweight());
           Clonetask.setWorkingsub(this.getWorkingsub());
           Clonetask.setConnection_number(this.getConnection_number());
           Clonetask.setStart_time(this.getStart_time());
           Clonetask.setWorking_time(this.getWorking_time());
           Clonetask.setFinish_time(this.getFinish_time());
           Clonetask.allocate_proc_ID = this.allocate_proc_ID;
           Clonetask.allocate_core_ID = this.allocate_core_ID;
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
           for(int i = 0; i < this.executeFrequencyStep.length; i++){
               Clonetask.executeFrequencyStep[i] = this.executeFrequencyStep[i];
               Clonetask.copyExecuteFrequency[i] = this.copyExecuteFrequency[i];
           }
           return Clonetask;
       }
        
              
 	public void output_data(){
 		System.out.print("task["+getID()+"] W="+getWeight()+" bl="+bl+" comnum="+getConnection_number()+" pred:");
 		for(int i=0; i<predecessor.size(); i++)System.out.print(predecessor.get(i) +" ");
 		System.out.print("succ=");
                for(int i=0; i<successor.size(); i++)System.out.print(successor.get(i)+" ");
                System.out.print("edgecost_r:");
 		for(int i=0; i<edge_cost_r.size(); i++)System.out.print(edge_cost_r.get(i) +" ");
 		System.out.println("");
 	}
        
        public void output_result(){
 		System.out.print("task["+String.format("%4d", getID())
                         +"] W="+String.format("%4d", getWeight())
                        +" restWeight="+ String.format("%4.4f", getRestweight())
                        +" bl="+bl);
                System.out.print(
                        " ProcID="+(allocate_proc_ID+1)
                        +" CoreID="+ (allocate_core_ID+1) 
                        +" Start_time="+String.format("%4.4f", getStart_time())
                        +" Working_time="+String.format("%4.4f", getWorking_time())
                        +" actual Working TIme="+ String.format("%4.4f", calculateActualWorkingTime())
                        +" WorkingSUBtime="+String.format("%4.4f", getWorkingsub())
                        +" finish_time="+String.format("%4.4f", getFinish_time()));
                System.out.print("\tpred:");
 		for(int i=0; i<predecessor.size(); i++)System.out.print(predecessor.get(i)+"-"+edge_cost_r.get(i)+" ");
                System.out.print("\tsucc=");
 		for(int i=0; i<successor.size(); i++)System.out.print(successor.get(i)+"-"+edge_cost_s.get(i)+" ");                
                System.out.println("");
 	}

        double calculateActualWorkingTime(){
            double sum = 0;
            for(double x : executeFrequencyStep){
                if(x != 0)sum += x;
            }
            return sum / UpperRate;
        }
        
    void initializeRestTask(Tasknode task, double dividTime){
        this.initializeExecuteFrequencyStep(task.executeFrequencyStep.length, 0.0);
        this.copyTasknode(task);
        this.setRestweight(task.getRestweight());
        this.setWorking_time(dividTime - task.getStart_time());
        this.setFinish_time(task.getStart_time() + task.getWorking_time());
        this.setWorkingsub(0);
    }
        
    void setExecuteFrequency(int from, int to, double frequency){
        if((to - from) != 0){
            System.out.println("method setExecuteFrequency task"+this.getID()+" from="+from+" to="+to);
            for(int i = from; i < to - from ; i++){
                executeFrequencyStep[i] = frequency;
            }
        }
    }
    void setsubExecuteFrequency(int from, int to, double frequency){
        if((to - from) != 0){
            System.out.println("method setsubExecuteFrequency task"+this.getID()+" from="+from+" to="+to);
            for(int i = from; i < to - from ; i++){
                copyExecuteFrequency[i] = frequency;
            }
        }
    }
    
    void adjusterSameProcessor(DAG taskGraph, double PredFinishTime){
        int index;
        System.out.println("in method normal ajuster Same Processor\ntask"+getID()+" PredFinishTime="+PredFinishTime);
        if(this.getStart_time() != 0){
            if(this.getStart_time() < PredFinishTime){
                this.setStart_time(PredFinishTime);
                this.setFinish_time(this.getStart_time() + this.getWorking_time());
                this.output_result();
            }

            for(int i = 0; i < successor.size(); i++){
                index = taskGraph.gettask_i(taskGraph, (Integer)successor.get(i));
                //if(taskGraph.task[index].allocate_proc_ID == this.allocate_proc_ID){
                System.out.println("index="+index+" successer="+(Integer)successor.get(i));
                    taskGraph.task[index].adjusterSameProcessor(taskGraph, this.getFinish_time());
                //}else{
                //   taskGraph.task[index].adjusterDifferentProcessor(taskGraph, this.getFinish_time());
                //}
            }
        }
    }
    
    
    void adjusterDifferentProcessor(DAG taskGraph, double PredFinishTime){
        int index;
        System.out.println("in method normal ajuster");
        if(this.getStart_time() != 0)
        if(this.getStart_time() < PredFinishTime){
            this.setStart_time(PredFinishTime);
            this.setFinish_time(this.getStart_time() + this.getWorking_time());
       
        }
        
        for(int i = 0; i < successor.size(); i++){
            index = taskGraph.gettask_i(taskGraph, (Integer)successor.get(i));
            if(taskGraph.task[index].allocate_proc_ID == this.allocate_proc_ID){
                taskGraph.task[index].adjusterSameProcessor(taskGraph, this.getFinish_time());
            }else{
                taskGraph.task[index].adjusterSameProcessor(taskGraph, this.getFinish_time());
            }
        }
    }
    /*
    double calculateRestWeight(double divideTime, int upperRate){
        double restWeight = 0;
        int restIndex = (int)((divideTime -this.getStart_time()) * upperRate);
        if(restIndex < 0)restIndex = 0;
        System.out.println("in method calculateRestWeight  dividetime="+ divideTime 
                +" start time="+this.getStart_time()
                +" finish time="+this.getFinish_time()
                +" restIndex="+restIndex
                + " frequency step end point="+(this.getFinish_time() - this.getStart_time()) * upperRate);
        //for(int i = restIndex; i < (this.getFinish_time() - this.getStart_time()) * upperRate ; i++){
        for(int i = restIndex; i < (this.getFinish_time() - this.getStart_time()) * upperRate ; i++){  
            //System.out.print(" "+ executeFrequencyStep[i]);
            restWeight += executeFrequencyStep[i];
        }
        System.out.println("restweight= "+restWeight);
        restWeight = restWeight/(double)upperRate;
        System.out.println("in calculateRestWeight task "+this.getID() +" restWeight="+ restWeight);
        return restWeight;
        
    }
    * 
    */
    
    double calculateRestWeight(double divideTime, int upperRate){
        double restWeight = 0;
        int counter27 = 0;
        int counter25 = 0;
        int counter19 = 0;
        int counter13 = 0;
        int restIndex = (int)((divideTime -this.getStart_time()) * upperRate);
        if(restIndex < 0)restIndex = 0;
        //System.out.println("in method calculateRestWeight  dividetime="+ divideTime 
         //       +" start time="+this.getStart_time()
          //      +" finish time="+this.getFinish_time()
        ///        +" restIndex="+restIndex
       //         +" frequency step end point="+(this.getFinish_time() - this.getStart_time()) * upperRate);
        //for(int i = restIndex; i < (this.getFinish_time() - this.getStart_time()) * upperRate ; i++){
        for(int i = restIndex; i < (this.getFinish_time() - this.getStart_time()) * upperRate ; i++){  
            //System.out.print(" "+ executeFrequencyStep[i]);
            switch((int)(executeFrequencyStep[i] * 10)){
                case 27: counter27++;break;
                case 25: counter25++;break;
                case 19: counter19++;break;
                case 13: counter13++;break;
            }
        }
        restWeight += counter27 * 2.7;
        restWeight += counter25 * 2.5;
        restWeight += counter19 * 1.9;
        restWeight += counter13 * 1.3;
        restWeight = restWeight/(double)upperRate;
       // System.out.println("in calculateRestWeight task "+this.getID() +" restWeight="+ restWeight);
        return restWeight;
        
    }
    
    void outputExecuteFrequency(){
        for(int i = 0; i < executeFrequencyStep.length; i++ )
            if(executeFrequencyStep[i] != 0)
                System.out.print(" "+ executeFrequencyStep[i]);
    }
    
    void initializeExecuteFrequencyStep(int num, double initialValue){
        executeFrequencyStep = new double[10000];
        copyExecuteFrequency = new double[10000];
        for(int i = 0; i < num; i++){
           executeFrequencyStep[i] = initialValue;
           copyExecuteFrequency[i] = initialValue;
        }
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
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
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
     * @return the start_time
     */
    public double getStart_time() {
        return start_time;
    }

    /**
     * @param start_time the start_time to set
     */
    public void setStart_time(double start_time) {
        this.start_time = start_time;
    }

    /**
     * @return the working_time
     */
    public double getWorking_time() {
        return working_time;
    }

    /**
     * @param working_time the working_time to set
     */
    public void setWorking_time(double working_time) {
        this.working_time = working_time;
    }

    /**
     * @return the finish_time
     */
    public double getFinish_time() {
        return finish_time;
    }

    /**
     * @param finish_time the finish_time to set
     */
    public void setFinish_time(double finish_time) {
        this.finish_time = finish_time;
    }

    /**
     * @return the workingsub
     */
    public double getWorkingsub() {
        return workingsub;
    }

    /**
     * @param workingsub the workingsub to set
     */
    public void setWorkingsub(double workingsub) {
        this.workingsub = workingsub;
    }

    /**
     * @return the restweight
     */
    public double getRestweight() {
        return restweight;
    }

    /**
     * @param restweight the restweight to set
     */
    public void setRestweight(double restweight) {
        this.restweight = restweight;
    }

    /**
     * @return the executeFrequencyStep
     */
    public double getExecuteFrequencyStep(int index) {
        return executeFrequencyStep[index];
    }

    /**
     * @param executeFrequencyStep the executeFrequencyStep to set
     */
    public void setExecuteFrequencyStep(int index, double Value) {
        this.executeFrequencyStep[index] = Value;
    }

    /**
     * @return the communicationEndTime
     */
    public double getCommunicationEndTime() {
        return communicationEndTime;
    }

    /**
     * @param communicationEndTime the communicationEndTime to set
     */
    public void setCommunicationEndTime(double communicationEndTime) {
        this.communicationEndTime = communicationEndTime;
    }
	
        
}