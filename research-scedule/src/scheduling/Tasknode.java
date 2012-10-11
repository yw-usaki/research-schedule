package scheduling;

import java.io.Serializable;
import java.util.ArrayList;

public class Tasknode implements Serializable, Cloneable{
    static final boolean printOutFlag = false;
	private int ID;
	private float weight;
        private float restweight;
	private int connection_number;
        private float workingsub;
        int index;
        int allocate_proc_ID;
        int allocate_core_ID;
        int bl;
        int UpperRate = 50;
        ArrayList successor = new ArrayList();
        ArrayList predecessor = new ArrayList();
        private float start_time;
        private float working_time;
        private float finish_time;
        private float communicationEndTime = 0;
        ArrayList<Float> executeFrequencyStep = new ArrayList<Float>();
        ArrayList<Float> copyExecuteFrequency = new ArrayList<Float>();
        ArrayList edge_cost_r = new ArrayList();
        ArrayList edge_cost_s = new ArrayList();
	boolean permFlag;
        
	Tasknode(){
            this.setStart_time(0);
            this.setWorking_time(0);
            this.setFinish_time(0);
            this.setRestweight(0);
            
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
            this.executeFrequencyStep.clear();
            this.copyExecuteFrequency.clear();
            for(int i = 0; i < task.executeFrequencyStep.size(); i++)
                this.executeFrequencyStep.add(task.executeFrequencyStep.get(i));
            for(int i = 0; i < task.copyExecuteFrequency.size(); i++)
                this.copyExecuteFrequency.add(task.copyExecuteFrequency.get(i));
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
            this.executeFrequencyStep.clear();
            this.copyExecuteFrequency.clear();
            for(int i = 0; i < task.executeFrequencyStep.size(); i++)
                this.executeFrequencyStep.add(task.executeFrequencyStep.get(i));
            for(int i = 0; i < task.copyExecuteFrequency.size(); i++)
                this.copyExecuteFrequency.add(task.copyExecuteFrequency.get(i));
           
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
            for(int i = 0; i < task.executeFrequencyStep.size(); i++)
                this.executeFrequencyStep.add(task.executeFrequencyStep.get(i));
            for(int i = 0; i < task.copyExecuteFrequency.size(); i++)
                this.copyExecuteFrequency.add(task.copyExecuteFrequency.get(i));
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
           for(int i = 0; i < this.executeFrequencyStep.size(); i++)
                Clonetask.executeFrequencyStep.add(this.executeFrequencyStep.get(i));
           for(int i = 0; i < this.copyExecuteFrequency.size(); i++)
                Clonetask.copyExecuteFrequency.add(this.copyExecuteFrequency.get(i));
           return Clonetask;
       }
        
              
 	public void output_data(){
            //if(printOutFlag){
 		System.out.print("task["+getID()+"] W="+getWeight()+" bl="+bl+" comnum="+getConnection_number()+" pred:");
 		for(int i=0; i<predecessor.size(); i++)System.out.print(predecessor.get(i) +" ");
 		System.out.print("succ=");
                for(int i=0; i<successor.size(); i++)System.out.print(successor.get(i)+" ");
                System.out.print("edgecost_r:");
 		for(int i=0; i<edge_cost_r.size(); i++)System.out.print(edge_cost_r.get(i) +" ");
 		System.out.println("");
            //}
 	}
        
        public void output_result(){
                System.out.print("task["+String.format("%4d", getID())
                         +"] W="+String.format("%4f", getWeight())
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

        float calculateActualWorkingTime(){
            float sum = 0;
            for(float x : executeFrequencyStep){
                if(x != 0)sum += x;
            }
            return sum / UpperRate;
        }
        
    void initializeRestTask(Tasknode task, float dividTime){
        this.copyTasknode(task);
        this.setRestweight(task.getRestweight());
        this.setWorking_time(dividTime - task.getStart_time());
        this.setFinish_time(task.getStart_time() + task.getWorking_time());
        this.setWorkingsub(0);
    }
        
    void setExecuteFrequency(int from, int to, float frequency){
        if((to - from) != 0){
            if(printOutFlag)
                System.out.println("method setExecuteFrequency task"+this.getID()+" from="+from+" to="+to);
            for(int i = from; i < to - from ; i++){
                executeFrequencyStep.set(i, frequency);
            }
        }
    }
    void setsubExecuteFrequency(int from, int to, float frequency){
        if((to - from) != 0){
            if(printOutFlag)
                System.out.println("method setsubExecuteFrequency task"+this.getID()+" from="+from+" to="+to);
            for(int i = from; i < to - from ; i++){
                copyExecuteFrequency.set(i, frequency);
            }
        }
    }
    
    void adjusterSameProcessor(DAG taskGraph, float PredFinishTime){
        int index;
        if(printOutFlag)
            System.out.println("in method normal ajuster Same Processor\ntask"+getID()+" PredFinishTime="+PredFinishTime);
        if(this.getStart_time() != 0){
            if(this.getStart_time() < PredFinishTime){
                this.setStart_time(PredFinishTime);
                this.setFinish_time(this.getStart_time() + this.getWorking_time());
                if(printOutFlag)
                    this.output_result();
            }

            for(int i = 0; i < successor.size(); i++){
                index = taskGraph.gettask_i(taskGraph, (Integer)successor.get(i));
                //if(taskGraph.task[index].allocate_proc_ID == this.allocate_proc_ID){
                if(printOutFlag)
                    System.out.println("index="+index+" successer="+(Integer)successor.get(i));
                taskGraph.task[index].adjusterSameProcessor(taskGraph, this.getFinish_time());
                //}else{
                //   taskGraph.task[index].adjusterDifferentProcessor(taskGraph, this.getFinish_time());
                //}
            }
        }
    }
    
    
    float calculateRestWeight(float divideTime, int upperRate){
        float restWeight = 0;
        float weight = 0;
        boolean flag = true;
        int counter37 = 0;
        int counter35 = 0;
        int counter33 = 0;
        int counter31 = 0;
        int counter25 = 0;
        int restIndex;
        
        
        restIndex = (int)(((divideTime -this.getStart_time()) * upperRate) * 10 +5) / 10;
        if(restIndex < 0)restIndex = 0;
        if(printOutFlag)
            System.out.println("in method calculateRestWeight  dividetime="+ divideTime 
               +" start time="+this.getStart_time()
              +" finish time="+this.getFinish_time()
                +" restIndex="+restIndex
                +" frequency step end point="+(int)(this.getFinish_time() - this.getStart_time()) * upperRate
                +" exesize="+executeFrequencyStep.size());
        if((int)divideTime*1000 == (int)this.getStart_time()*1000)return this.getWeight();
        for(int i = restIndex; i < executeFrequencyStep.size(); i++){  
            //System.out.println(i+" "+ executeFrequencyStep[i]);
            if(flag){
                float time = (getStart_time() + 1 / UpperRate * restIndex+1) - divideTime;
                //System.out.println("time ="+time);
                if(time < 0)time = 0;
                weight =time * executeFrequencyStep.get(i);
                if(time == 0){
                    switch((int)(executeFrequencyStep.get(i) * 10)){
                        case 37: counter37++;break;
                        case 35: counter35++;break;
                        case 33: counter33++;break;
                        case 31: counter31++;break;
                        case 25: counter25++;break;
                    }
                }
                flag = false;
            }else {
                switch((int)(executeFrequencyStep.get(i) * 10)){
                    case 37: counter37++;break;
                    case 35: counter35++;break;
                    case 33: counter33++;break;
                    case 31: counter31++;break;
                    case 25: counter25++;break;
                }
            }
        }
        
        restWeight += counter37 * 3.7;
        restWeight += counter35 * 3.5;
        restWeight += counter33 * 3.3;
        restWeight += counter31 * 3.1;
        restWeight += counter25 * 2.5;
        restWeight += weight;
        restWeight = restWeight/(float)upperRate;
        //System.out.println(counter37 +" "+counter35+" "+counter33+" "+counter31+" "+counter30);
        //System.out.println("weight="+weight+" tetegegegeg="+executeFrequencyStep.get(executeFrequencyStep.size()-1)+" restweight="+restWeight);
        if(executeFrequencyStep.get(executeFrequencyStep.size() -1) < 1.3)
            restWeight += executeFrequencyStep.get(executeFrequencyStep.size() -1);
        restWeight = (restWeight * 10000 + 5 ) / 10000;
        if(getWeight() < restWeight)
            restWeight = getWeight();
        //System.out.println("in calculateRestWeight task "+this.getID() +" restWeight="+ restWeight);
        return restWeight;
        
    }
    
    void outputExecuteFrequency(){
        System.out.println("executeFrequencyStep");
        for(int i = 0; i < executeFrequencyStep.size(); i++ )
            if(executeFrequencyStep.get(i) != 0)
                System.out.print(" "+ executeFrequencyStep.get(i));
        System.out.println("copyExecuteFrequencyStep");
        for(int i = 0; i < copyExecuteFrequency.size(); i++ )
            if(copyExecuteFrequency.get(i) != 0)
                System.out.print(" "+ copyExecuteFrequency.get(i));
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
    public float getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(float weight) {
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
    public float getStart_time() {
        return start_time;
    }

    /**
     * @param start_time the start_time to set
     */
    public void setStart_time(float start_time) {
        this.start_time = start_time;
    }

    /**
     * @return the working_time
     */
    public float getWorking_time() {
        return working_time;
    }

    /**
     * @param working_time the working_time to set
     */
    public void setWorking_time(float working_time) {
        this.working_time = working_time;
    }

    /**
     * @return the finish_time
     */
    public float getFinish_time() {
        return finish_time;
    }

    /**
     * @param finish_time the finish_time to set
     */
    public void setFinish_time(float finish_time) {
        this.finish_time = finish_time;
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

    /**
     * @return the restweight
     */
    public float getRestweight() {
        return restweight;
    }

    /**
     * @param restweight the restweight to set
     */
    public void setRestweight(float restweight) {
        this.restweight = restweight;
    }


    /**
     * @return the communicationEndTime
     */
    public float getCommunicationEndTime() {
        return communicationEndTime;
    }

    /**
     * @param communicationEndTime the communicationEndTime to set
     */
    public void setCommunicationEndTime(float communicationEndTime) {
        this.communicationEndTime = communicationEndTime;
    }


	
        
}