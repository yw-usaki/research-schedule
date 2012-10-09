package scheduling;

import java.util.ArrayList;


public class Proc{
	private int ID;
        private int freq_step;
	private double[] frequency = new double[20];
        private double end_time = 0;
	private ArrayList execute_task = new ArrayList();
        private int[] core_used;
        private double addtime = 0;
        Proc(){}
        
        Proc(String name,int value){
          if(name == "frequency")
              for(int i=0; i<value; i++)
                   frequency = new double[value];
        }
	
	public void set_value(String name, int ival, double[] dval){
		if(name == "proc_id")this.setID(ival);
		//else if(name == "frequency")this.setFrequency(dval);
//		else if(name == "execute_task")this.execute_task[] = ival;
		else System.out.println("Error: name is fail in set_vale @ Proc");
	}
        
        public void output_data(){
                    double[] freqlist = getFrequencys();
 		System.out.print("procs["+getID()+"] "+" freq_step="+getFreq_step()+" frequency:");
 		for(int i=0; i<freqlist.length; i++)System.out.print(freqlist[i]+" ");
 		System.out.print("execute:");
                for(int i=0; i<execute_task.size(); i++)System.out.print(execute_task.get(i)+" ");
 		System.out.println("");
 	}
        
        public int procused(DAG task_graph, int time){
            int execute_num = 0;
            
            
            return execute_num;
        }

        public double getexefreq(){
            int usedcore = 0;
            double freq = 0;

            for(int i=0; i<core_used.length; i++)
                if(core_used[i] != 0)usedcore++;
            switch(usedcore){
               case 0:
                   freq = frequency[0];
               case 1:
                   freq = frequency[1];
               case 2:
                   freq = frequency[2];
               case 3:
                   freq = frequency[3];
               default:
                   System.out.println("no frequency element");
                System.exit(0);
                   return freq;
            }
                        
                        
        }
                
        boolean searchtask(int taskID){
            int t_ID;
            for(int i=0; i<execute_task.size(); i++)
                if((t_ID = (Integer)execute_task.get(i)) == taskID)return true;
            return false;       
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
     * @return the freq_step
     */
    public int getFreq_step() {
        return freq_step;
    }

    /**
     * @param freq_step the freq_step to set
     */
    public void setFreq_step(int freq_step) {
        this.freq_step = freq_step;
    }

    /**
     * @return the frequency
     */
    public double[] getFrequencys() {
        return frequency;
    }
    
    public double getFrequency(int i){
        return frequency[i];
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequencys(double... freq) {
        this.frequency = freq;
    }

    /**
     * @return the execute_task
     */
    public int getExecute_task(int i){     
        return (Integer)execute_task.get(i);
    }
    
    public int getExecute_task_size(){
        return execute_task.size();

    }
    
    /**
     * @param execute_task the execute_task to set
     */
    public void setExecute_task(int ID) {
        execute_task.add(ID);
    }

    /**
     * @return the end_time
     */
    public double getEnd_time() {
        return end_time;
    }

    /**
     * @param end_time the end_time to set
     */
    public void setEnd_time(double end_time) {
        this.end_time = end_time;
    }

    /**
     * @return the addtime
     */
    public double getAddtime() {
        return addtime;
    }

    /**
     * @param addtime the addtime to set
     */
    public void setAddtime(double addtime) {
        this.addtime = addtime;
    }
}