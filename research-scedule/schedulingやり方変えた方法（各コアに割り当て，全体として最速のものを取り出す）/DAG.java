package scheduling;

import java.io.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import scheduling.Tasknode;
import java.util.ArrayList;




public class DAG implements Serializable, Cloneable{
    int total_tasks;
    int upperRate = 50;
    Tasknode[] task;

    DAG(){
       // System.out.println("create DAG");
    }	
    
    DAG Clone(){
        DAG clone = new DAG();
        System.out.println(this.total_tasks);
        clone.total_tasks = this.total_tasks;
        clone.upperRate = this.upperRate;
        clone.task = new Tasknode[total_tasks];
        for(int i = 0; i < this.total_tasks; i++){
            clone.task[i] = new Tasknode();
            clone.task[i] = this.task[i].Clone();
        }
        return clone;
    }
    
    //DAGのdeepcopy
    void setDAG(DAG original){
        this.total_tasks = original.total_tasks;
        this.upperRate = original.upperRate;
        for(int i = 0; i < this.total_tasks; i++){
            this.task[i].copyTasknode(original.task[i]);
        }
                
    }

    double selectLatestFinishTime(){
        double finishTime = this.task[0].getFinish_time();
        for(int i = 0; i < total_tasks; i++){
            if(finishTime < task[i].getFinish_time()){
               finishTime = task[i].getFinish_time();
            }
        }
        return finishTime;
    }
    
    double searchLatestPredFinishTIme(int index){
        double maxFinishTime = 0;
        for(int i = 0; i < task[index].predecessor.size(); i++){
            int predIndex = gettask_i(this, (Integer)task[index].predecessor.get(i));
            if(maxFinishTime < task[predIndex].getFinish_time())
                maxFinishTime = task[predIndex].getFinish_time();
        }
        
        return maxFinishTime;
    }
    //i番目のタスクから先読み分のタスクの順番を入れ替える
    void changeTasknode(int index, Tasknode[] newList){
        for(int i = 0; i < newList.length; i++){
            task[index + i].copyTasknode(newList[i]);
        }
    }
    
    public void set_initialdata(String Pass){
	String[] sline = new String[100];
        try{//open data file 
            
            //FileReader programreader = new FileReader("C:/Users/1151118/Dropbox/java/research-scedule/src/scheduling/Program3.dat");
            FileReader programreader = new FileReader(Pass);
            BufferedReader programbuffer = new BufferedReader(programreader);
            String line =new String();
            try {	
		//input task number
		line= programbuffer.readLine();
		sline = line.split("\t");
		total_tasks = Integer.parseInt(sline[0]);
		System.out.println("total_task"+total_tasks);
                
                //create object
                task = new Tasknode[total_tasks];
                for(int i=0; i<total_tasks; i++)	
                    task[i] = new Tasknode();
                         
                //input task parameter
                for(int i=0; i<total_tasks; i++){
                    if((line = programbuffer.readLine()) != null){
                        sline = line.split("\t");
                        //System.out.println(sline[0]+sline[1]+sline[2]);
                        task[i].setID(Integer.parseInt(sline[0]));
                        //System.out.println(i+" "+task[i].number+" ");
                        task[i].setWeight(Integer.parseInt(sline[1]));
                        //System.out.print(task[i].weight+" ");
                        task[i].setConnection_number(Integer.parseInt(sline[2]));
                        //System.out.print(task[i].connection_number+" ");
                        for(int j=0,k=3; j<task[i].getConnection_number(); j++,k++){
                            task[i].predecessor.add(Integer.parseInt(sline[k]));
                            //System.out.print(task[i].connection[j]+" ");
                        }
                        for(int j=0,k=3+task[i].getConnection_number(); j<task[i].getConnection_number(); j++,k++){
                            task[i].edge_cost_r.add(Double.parseDouble(sline[k]));
                            //System.out.print(task[i].edge_cost[j]+" ");
                        }
                        System.out.println("task="+task[i].getID()+" goege "+(int)((double)task[i].getWeight() / 1.3) * upperRate);
                        task[i].initializeExecuteFrequencyStep((int)((double)task[i].getWeight() / 1.3) * upperRate, 0.0);
                    }
                }
            } catch (IOException e) {//read file line
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	}catch(FileNotFoundException e){//open data file
            System.out.println(e);
    	}
        setsuccessor();
        calc_bl(this);
        outputdata();
    }
    
    public void calc_bl(DAG task_graph){
        int next,k, ID = -1;
        for(int i=0; i<task_graph.total_tasks; i++){//determine bottom level
                if(task_graph.task[i].getConnection_number() != 0)
                        task[i].bl = 10000;
                        for(int j=0; j<task_graph.task[i].getConnection_number(); j++){
                              if(task[i].bl < task[(Integer)task[i].predecessor.get(j)].bl + task[(Integer)task[i].predecessor.get(j)].getWeight()){
                                task[i].bl = task[(Integer)task[i].predecessor.get(j)].bl +  task[(Integer)task[i].predecessor.get(j)].getWeight();
                              }
                        }
                //System.out.println(task[i].bl);
        }
    }
    
    void outputdata(){
        for(int i=0; i<total_tasks; i++)
            task[i].output_data();
    }
   
    void outputResult(){
        for(int i=0; i<total_tasks; i++)
            task[i].output_result();
    }
    
    public int gettask_i(DAG taskgraph, int taskID){
        for(int i = 0; i < taskgraph.total_tasks; i++)
            if(taskgraph.task[i].getID() == taskID)return i;
        System.out.println("not found task["+taskID+"] node");
        return -1;
    }
    
    void setsuccessor(){
        for(int i=0; i<total_tasks; i++){
            for(int k=0; k<total_tasks; k++)
                for(int m=0; m<task[k].getConnection_number(); m++)
                    if(task[i].getID() == task[k].predecessor.get(m)){
                        task[i].successor.add(task[k].getID());
                        task[i].edge_cost_s.add(task[k].edge_cost_r.get(m));
                    }
        }
    }
}