package socket;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
 
import scheduling.DAG;
import scheduling.Tasknode;        

public class Server {
    //Proc1 all cores
    LinkedList<Tasknode> Proc1Core0TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core1TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core2TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core3TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core4TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core5TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core6TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc1Core7TaskList = new LinkedList<Tasknode>();
    //Proc2 all cores
    LinkedList<Tasknode> Proc2Core0TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core1TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core2TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core3TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core4TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core5TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core6TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc2Core7TaskList = new LinkedList<Tasknode>();
    //Proc3 all cores
    LinkedList<Tasknode> Proc3Core0TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core1TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core2TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core3TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core4TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core5TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core6TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc3Core7TaskList = new LinkedList<Tasknode>();
    //Proc4 all cores
    LinkedList<Tasknode> Proc4Core0TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core1TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core2TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core3TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core4TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core5TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core6TaskList = new LinkedList<Tasknode>();
    LinkedList<Tasknode> Proc4Core7TaskList = new LinkedList<Tasknode>();
            
    public Server(){
        
    }

    public void setTaskNode(DAG TaskGraph){
        for(Tasknode x : TaskGraph.task){
            switch(x.all)
        }
    }
    
    public void run(String args[]) {
        // ソケットや入出力用のストリームの宣言
        ServerSocket echoServer = null;
        String line;
        
 
        // ポート9999番を開く
        try {
            echoServer = new ServerSocket(9998);
            boolean flag =true;
            //
            System.out.println("Waiting for Connection");
            long time = System.currentTimeMillis();
            while(flag){
                Socket clientSocket = null;
                clientSocket = echoServer.accept();
                System.out.println("hjkl");
                //accept()はクライアントから接続要求があるまでブロックする．要求があれば，次の命令に移る
                //スレッドを起動し，クライアントと通信する
                
                new SrvThread(clientSocket, time).start();
                System.out.println("Waiting for New Connection");
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }finally{
            try {
                if(echoServer != null){
                    echoServer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class SrvThread extends Thread{
    private Socket sock;
    BufferedReader is;
    DataOutputStream os;
    static long startTime = 0;
    String sendMessage;
        
    public SrvThread(Socket sock, long time){
        this.sock = sock;
        startTime = time;
        System.out.println("Thread is Generated. Connect to "+ this.sock.getInetAddress());
    }
    
    public void run(){
        try {
            
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            os = new DataOutputStream(sock.getOutputStream());
            
            System.out.println("hahaha");
            String line;
            line = is.readLine();
            
            System.out.println("Message from client ;"+ line);
                startTime += 200000;
                sendMessage = startTime + "\n";
            
            System.out.println("start time :"+startTime);
            os.writeBytes(sendMessage);
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(sock != null){
                    sock.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
                
    }
}