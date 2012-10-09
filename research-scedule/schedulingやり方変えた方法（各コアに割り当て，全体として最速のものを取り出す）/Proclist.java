package scheduling;

import java.awt.List;
import java.io.*;

import java.util.Arrays;
import java.util.ArrayList;


public class Proclist implements Serializable{
    int procnum;
    //double[] frequnecy = {0,0,0,0};
    Processor[] procs;

    
    Proclist(){
        
       // for(int i=0; i<procnum; i++)
       //     procs[i] = new Processor(i, 4, frequnecy);
    }
    
    Proclist Clone(){
        Proclist clone = new Proclist();
        clone.procnum = this.procnum;
        clone.procs = new Processor[procnum];
        for(int i = 0; i < procnum; i++){
            clone.procs[i] = new Processor();
            clone.procs[i].setProcessor(this.procs[i]);
        }
        return clone;
    }
    
    void setProclist(Proclist original){
        this.procnum = original.procnum;
        for(int i = 0; i < this.procnum; i++){
            this.procs[i].setProcessor(original.procs[i]);
        }
    }
    
    double selectLatestFinishTime(){
        double maxFinishTime = 0;
        for(int i = 0; i < procnum; i++){
            if(procs[i].getSw() != 1)
                for(int j = 0; j < procs[i].getCore_number(); j++){
                    if(maxFinishTime < procs[i].Cores[j].getEndTime())
                        maxFinishTime = procs[i].Cores[j].getEndTime();
                }
        }    
        return maxFinishTime;
    }
    
    public void set_initialdata(){
                int Counter = 0;
		String[] sline = new String[100];
		try{
			//open data file 
                        File inputfile = new File("src/scheduling/proclist3.dat");
			FileReader procreader = new FileReader(inputfile.getAbsolutePath());
			BufferedReader procbuffer = new BufferedReader(procreader);
			String line =new String();

                        try {	
                            //input processor num
                            line= procbuffer.readLine();
                            sline = line.split("\t");
                            procnum = Integer.parseInt(sline[0]);
                            System.out.println("\nProcnum="+procnum);

                            //create object
                            procs = new Processor[procnum];
                            for(int i = 0; i < procnum; i++){
                              //input processor parameter
                              if((line = procbuffer.readLine()) != null){
                                sline = line.split("\t");
                                int Proc_ID = Integer.parseInt(sline[0]);//プロセッサのIDを読み込み
                                int conn_NUM = Integer.parseInt(sline[1]);//後続プロセッサのつながりの数を読み込み
                                int[] conn_ID = new int[conn_NUM];
                                int num = 2;
                                for(int j = 0; j < conn_NUM; j++, num++)
                                    conn_ID[j] = Integer.parseInt(sline[num]);
                                System.out.println("num =" + num);
                                int Core_num = Integer.parseInt(sline[num]);//各プロセッサのコア数を読み込み
                                if(Core_num == 0){
                                    procs[i] = new Processor(Proc_ID, conn_NUM, conn_ID);
                                }else{                                
                                    int freq_num = Integer.parseInt(sline[num+1]);//周波数の段数を読み込み
                                    num = num + 2;
                                    
                                    double[] frequency = new double[freq_num];
                                    for(int j = 0; j < freq_num; j++,num++){
                                       frequency[j] = Double.parseDouble(sline[num]);
                                       //System.out.println(frequency[j]);
                                    }
                                    
                                    procs[i] = new Processor(Proc_ID, conn_NUM, conn_ID, Core_num * 2, frequency, Counter);  
                                    Counter++;
                                    //print(task[i].connection[j]+" ");
                                }                                
                              }
                              //procs[i].output_data();
                            }				
			} catch (IOException e) {//read file line
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(FileNotFoundException e){//open file
			System.out.println(e);
		}
                //outputProclist();
                setConnPointer();
		outputProclist();
 		
 	}
    int[] getexeProcID(int taskID){
        int ProcID = 0;
        int CoreID = 0;
        int[] result = new int[2];
        for(int i=0; i<procnum; i++){
            if((CoreID = procs[i].SerchCoreIDonExeTask(taskID)) != -1){
                ProcID = i;
                break;
            }
        }
        result[0] = ProcID;
        result[1] = CoreID;
        System.out.println("task "+taskID+" Proc "+ProcID +" core "+CoreID);
        return result;
    }
    
    void outputProclist(){
        for(int i = 0; i < procnum; i++){
            //System.out.print("");
            procs[i].outputProcData();
        }
    }
    void setConnPointer(){
        for(int i = 0; i < procs.length; i++){
            for(int j = 0; j < procs[i].getConnect_num(); j++){
                for(int m = 0; m < procs.length; m++){
                    if(procs[i].connect_ID[j] == procs[m].getProcID()){
                        procs[i].connect_s.add(procs[m]);
                        procs[m].connect_ID_r.add(procs[i].getProcID());
                        procs[m].connect_r.add(procs[i]);
                    }
                }
            }
          
        }
        for(int i = 0; i < procs.length; i++)
            procs[i].connect_s.addAll(procs[i].connect_r);
    }
    

    
    
}