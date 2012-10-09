package scheduling;

import java.awt.Color;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import scheduling.WriteResult2.MyRect;

public class main{

    static Proclist[] Plist;
    static DAG[] TaskGraph;
    static long[] startTime;
    static long[] endTime;
    
    public static void main(String[] args) throws Exception{
        File[] ProgramPath = new File[7];
        ProgramPath[0] = new File("src/scheduling/Program_1.dat");
        ProgramPath[1] = new File("src/scheduling/Program2.dat");
        ProgramPath[2] = new File("src/scheduling/Program3.dat");
        ProgramPath[3] = new File("src/scheduling/Program4.dat");
        ProgramPath[4] = new File("src/scheduling/robot.dat");
        ProgramPath[5] = new File("src/scheduling/sparse.dat");
        ProgramPath[6] = new File("src/scheduling/fpppp.dat");

       Edgecommunication comscheduling = new Edgecommunication();
        Plist = new Proclist[ProgramPath.length];
        TaskGraph = new DAG[ProgramPath.length];
        startTime = new long[ProgramPath.length];//アルゴリズムの実行時間計測用
        endTime = new long[ProgramPath.length];//アルゴリズムの実行時間計測用
       
        CalculationUtil calc = new CalculationUtil();
        CopyUtil copyUtil = new CopyUtil();
        Sort objsort = new Sort();
        
        WriteResult2[] outresult = new WriteResult2[ProgramPath.length];
        //for(int i = 0; i < ProgramPass.length; i++){
        for(int i = 0; i < 2; i++){
            TaskGraph[i] = new DAG();
            Plist[i] = new Proclist();
            System.out.println("program file path :"+ProgramPath[i].getAbsolutePath());
            TaskGraph[i].set_initialdata(ProgramPath[i].getAbsolutePath());
            Plist[i].set_initialdata();
            //System.out.println(calc.searchNode(Plist[i].procs[3], Plist[i].procs[4].getProcID(), 0));
            //System.exit(0);
            //if(i == 0)Plist[i].set_initialdata();
            //else Plist[i] = (Proclist)copyUtil.deepCopy(Plist[0]);
            //task_graph.outputdata();
            TaskGraph[i] = objsort.blsort(TaskGraph[i]);
            TaskGraph[i].outputdata();

            //System.exit(0);
            startTime[i] = System.currentTimeMillis();
            comscheduling.t_tf(TaskGraph[i], Plist[i], 4);
            
            endTime[i] =  System.currentTimeMillis();
            System.out.println("**************************************Program"+ i + " result is following****************************************************" );
            result_output(TaskGraph[i], Plist[i]);
            outresult[i] = new WriteResult2("result" + i , Plist[i], TaskGraph[i]);
        }
        
        for(int i = 0; i < ProgramPath.length; i++){
            System.out.print("Program "+ i + " scheduling time is : ");
            System.out.println(endTime[i] - startTime[i]);
        }
    }
    
   

    static void result_output(DAG task_graph, Proclist plist){
        System.out.println("************************** scheduling result ************************************");
        for(int i=0; i<task_graph.total_tasks;i++)
            task_graph.task[i].output_result();
    }
}