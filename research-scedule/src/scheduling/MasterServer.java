package scheduling;

import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.security.ntlm.Client;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import scheduling.DAG;
import scheduling.Tasknode;        
import sun.misc.OSEnvironment;

import node.NodeBean;
import node.NodeClientBean;

public class MasterServer {
    static final int PORT = 4000;//エコーポート

    ObjectInputStream ois;
    ObjectOutputStream oos;
    Socket sock;
    ArrayList<Integer> ProcIDs = new ArrayList<Integer>();
    LinkedList<LinkedList<ExTaskList>> ProcExTaskLists = new LinkedList<LinkedList<ExTaskList>>();
    
    public MasterServer(){
        
    }

    public void run(DAG TaskGraph, Proclist Plist)throws IOException{
        long compWeight = 1000000000L;
        int commWeight = 1000*1000*100;
        
        //プロセッサのIDを検出
        boolean flag;
        for(Processor v : Plist.procs){
            if(!v.getSw()){
                ProcIDs.add(v.getProcID());
            }
        }
        /*
        //タスクがり当てられているプロセッサIDを検出
        for(Tasknode task : TaskGraph.task){
            flag = true;
            if(ProcIDs != null){
                for(int x : ProcIDs){
                    if(x == task.allocate_proc_I){
                        flag = false;
                        break;
                    }
                }
            }
            if(flag)ProcIDs.add(task.allocate_proc_I);
            if(ProcIDs.size() == 4)break;
        }
        */

        System.out.println(ProcIDs);
        
        //スケジュールをプロセッサごとに分割
        //LinkedList<ExTaskList> ExtaskLists = new LinkedList<ExTaskList>();
        for(int index = 0; index < ProcIDs.size(); index++){
            ProcExTaskLists.add(setProcExTaskList(TaskGraph, ProcIDs.get(index)));
        }
        
        List<NodeBean> nodes = new ArrayList<NodeBean>();
        // ソケットや入出力用のストリームの宣言
        ServerSocket echoServer = null;
        String line;
        
        SlaveManager sm = new SlaveManager(nodes);
        sm.start();
        System.out.println("Please connect Slave node...");
        
        //プロセッサを接続するまでの待機(enter押したら終了)
        InputStreamReader sin = new InputStreamReader(System.in);
        sin.read();
        
        //スケジュール上のプロセッサと実機の関連付け
        LinkedList<NodeClientBean> nodeclients = new LinkedList<NodeClientBean>();
        int index = 0;
        for(NodeBean v : nodes){
            NodeClientBean nodeclinet = new NodeClientBean();
            nodeclinet.procID = ProcIDs.get(index);
            nodeclinet.coreID = v.getCoreId();
            nodeclinet.clientId = v.getProcId();
            nodeclinet.clientAddress = v.getIp();
            nodeclinet.clientPort = v.getPort();
            nodeclients.add(nodeclinet);
            if(nodeclients.size() % 8 == 0)index++;
        }
        /*
        //関連付けした情報を全コアに送信
        for(NodeBean v : nodes){
            sendObject(nodeclients, v.getIp(), v.getPort());
        }
        */
        
        //全端末にスケジュール情報を渡す
        for(NodeBean  v:  nodes){
                System.out.println("send dataBean "+v.getIp()+":"+v.getPort());
                Socket soc = new Socket(v.getIp(), v.getPort()); // ソケット(Socket)を開く
                BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream());
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                DataBean data = new DataBean();
                data.task = "info";
                data.compWeight = compWeight;
                data.commWeight = commWeight;
                data.rootTaskID = getRootTaskID(TaskGraph);
                data.finalTaskID = getFinalTaskID(TaskGraph);
                data.nodeClientBeans = nodeclients;
                data.TaskGraph = TaskGraph;
                data.Plist = Plist;
                //localData = data;
                oos.writeObject(data);
                oos.flush();

                oos.close(); // 出力ストリームを閉じる
        }
        
        
        //タスクを各コアに割り当て
        index = 0;
        for(NodeBean  v:  nodes){
            v.outputdata();
            //System.out.print("IP : "+v.getIp()+" Port : "+v.getPort());
            Socket soc = new Socket(v.getIp(), v.getPort()); // ソケット(Socket)を開く
            //Socket soc = ss.accept();
            BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            //System.out.println(" ProcID : "+ v.getProcId()+" CoreID : "+v.getCoreId());
            oos.writeObject(ProcExTaskLists.get(v.getProcId()).get(v.getCoreId()));
            oos.flush();
            oos.close(); // 出力ストリームを閉じる
            bos.close();
            soc.close(); // ソケットを閉じる
        }
        

        //タスク開始時間の設定
        ServerSocket startss = new ServerSocket(30026);
        Socket startsoc = startss.accept();
        startsoc.getInputStream().read();
        double startTime = System.currentTimeMillis();
        startsoc.close();
        startss.close();
        System.out.println("receive start signal");
        
        //タスク終了情報を取得
        ServerSocket finss = new ServerSocket(20000); 
        Socket finsoc = finss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(finsoc.getInputStream()));
        System.out.println(in.readLine());
        
        
        double excTime = (System.currentTimeMillis()- startTime) / 1000;
        System.out.println("Exc : "+excTime+ " sec");
        //roundSumExecTime += excTime;
        finss.close();

        
        //slaveの終了受信まで待つ
        ServerSocket sss = new ServerSocket(30020);


        //System.exit(1);
    }
    
    /*
    private static void showNodeSlaveAllocate(LinkedList<NodeClientBean> nodeClientBeans) {
            System.out.println("---Node List---");
            for (NodeClientBean v : nodeClientBeans) {
                    System.out.println(v.clientId + " " + v.clientAddress + ":"
                                    + v.clientPort + " p"+v.procNum);
            }
    }
    */
    
    void sendObject(Object object, String addr, int port) throws IOException{
            Socket soc = new Socket(addr, port); // ソケット(Socket)を開く
            //Socket soc = ss.accept();
            BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream());
            ObjectOutputStream soos = new ObjectOutputStream(bos);
            //System.out.println(" ProcID : "+ v.getProcId()+" CoreID : "+v.getCoreId());
            soos.writeObject(object);
            soos.flush();
            soos.close(); // 出力ストリームを閉じる
            bos.close();
            soc.close(); // ソケットを閉じる
    }

    void SendTo(Object sendObject){
        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(sendObject);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(scheduling.Client.class.getName()).log(Level.SEVERE, null, ex);
        }finally{

        }
    }
    
    Object ReceiveFrom(){
        Object obj = null;
        try{
            ois = new ObjectInputStream(sock.getInputStream());
            obj = ois.readObject();
            //ois.close();
        } catch (IOException ex) {
            Logger.getLogger(scheduling.Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(scheduling.Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(obj == null)System.out.println("null");
        return obj;
    }
    
    public int getRootTaskID(DAG TaskGraph){
        for(Tasknode v : TaskGraph.task){
            if(v.predecessor.size() == 0)
                return v.getID();
        }
        return -1;
    }
    
    public int getFinalTaskID(DAG TaskGraph){
        for(Tasknode v : TaskGraph.task){
            if(v.getID() == TaskGraph.total_tasks - 1)
                return v.getID();
        }
        return -1;
    }
    
    public LinkedList<ExTaskList> setProcExTaskList(DAG TaskGraph, int procID){
        LinkedList<ExTaskList> newProcTaskList = new LinkedList<ExTaskList>();
        ExTaskList[] CoreExTaskList = new ExTaskList[8];
        
        
        for(int index = 0; index < 8; index++)
            CoreExTaskList[index] = new ExTaskList(procID, index);
        
        for(Tasknode x : TaskGraph.task){
            if(x.allocate_proc_I + 1 == procID)
                switch(x.allocate_core_I){
                    case 0:
                        CoreExTaskList[0].ExTaskList.add(x);
                        break;
                    case 1:
                        CoreExTaskList[1].ExTaskList.add(x);
                        break;
                    case 2:
                        CoreExTaskList[2].ExTaskList.add(x);
                        break;
                    case 3: 
                        CoreExTaskList[3].ExTaskList.add(x);
                        break;
                    case 4: 
                        CoreExTaskList[4].ExTaskList.add(x);
                        break;
                    case 5:
                        CoreExTaskList[5].ExTaskList.add(x);
                        break;
                    case 6:
                        CoreExTaskList[6].ExTaskList.add(x);
                        break;
                    case 7:
                        CoreExTaskList[7].ExTaskList.add(x);
                        break;
                    default:
                        System.out.println("core default");
                        break;
                }
        }
        
        for(ExTaskList v : CoreExTaskList){
            if(v.ExTaskList == null)v.state = "quit";
            else{
                v.state = "set";
            }
            newProcTaskList.add(v);
        }
        return newProcTaskList;
              
    }
            
    static class SlaveManager extends Thread{


            List<NodeBean> nodes = null;//ノード情報が格納されていく　ID，HOST,PORT

            public SlaveManager(List<NodeBean> nodes ) {
                    this.nodes = nodes;
            }

            public void run(){

                    ServerSocket ss = null;
                    int ProcId = 0;
                    int CoreId = 0;
                    try {
                            ss = new ServerSocket(PORT);
                    } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            System.err.println("port:" +PORT+" is already used.");
                            System.exit(0);
                    }
                    try {
                            while (true) {

                                    Thread.sleep(0);

                                    try{
                                            Socket soc = ss.accept();
                                            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));


                                            InetAddress cAddr = soc.getInetAddress(); // クライアントのIPアドレス
                                            int cPort = soc.getPort(); // クライアントのポート番号
                                            String slaveText = in.readLine();

                                            int csPort = Integer.parseInt(slaveText);

                                            System.out.println("Connected    (" // 接続したクライアント情報の表示
                                                            + cAddr.getHostAddress() + " onPort=" + cPort + ")");

                                            nodes.add(new NodeBean(ProcId, CoreId, cAddr.getHostAddress(), csPort));

                                            System.out.println("---Node List---");
                                            for (NodeBean v : nodes) {
                                                    System.out.println(v.getProcId()+"-"+v.getCoreId() + " " + v.getIp() + ":"+ v.getPort());
                                            }

                                            soc = new Socket(cAddr.getHostAddress(), csPort);

                                            // soc.setSoTimeout(1000);
                                            BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream());
                                            ObjectOutputStream oos = new ObjectOutputStream(bos);
                                            DataBean data = new DataBean(cAddr.getHostAddress());

                                            oos.writeObject(data);
                                            oos.flush();
                                            //oos.reset();
                                            oos.close(); // 出力ストリームを閉じる
                                            bos.close();
                                            soc.close(); // ソケットを閉じる

                                            System.out.println("If the master is connected to all slave machine, you push the enter...");

                                    }catch (Exception ee) {
                                            System.err.println("SlaveConnector Error");
                                    }

                                    CoreId++;
                                    if(CoreId == 8){
                                        CoreId = 0;
                                        ProcId++;
                                    }

                            }

                    }catch (InterruptedException e) {
                            try {
                                    ss.close();
                            } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                            }
                            System.out.println("SlaveConnecter exit.");


                    }


            }
    }
    
}

/*
 * 
 *         // ポート9999番を開く
        try {
            echoServer = new ServerSocket(PORT);
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
                
                SrvThread server = new SrvThread(clientSocket, time);
                server.setTaskNode(TaskGraph);
                server.start();


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
         
        
class SrvThread extends Thread{

    
    private Socket sock;
    static long startTime = 0;
    static int index = 0;
    String sendMessage;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    
    public SrvThread(Socket sock, long time){
        this.sock = sock;
        startTime = time;
        System.out.println("Thread is Generated. Connect to "+ this.sock.getInetAddress());
    }
    
    

    public void run(){
        try {
            
            startTime += 200000;
            sendMessage = startTime + "\n";
            
            System.out.println("start time :"+startTime);
            System.out.println("send start time");
            SendTo(startTime);
            System.out.println("send task list");
            SendTo(test.get(index++));
            System.out.println("end");
            
            
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
    
   
    void ConnectionTO(InetAddress addr, int port){
        InetSocketAddress socketAddress = new InetSocketAddress(addr, port);
        sock = new Socket();
        //Socket sock2 = new Socket();
        try{
            sock.connect(socketAddress, 1000000);
        }catch(IOException e){
            e.printStackTrace();
        }
        InetAddress inadr;

        if((inadr = sock.getInetAddress()) != null){
            System.out.println("Connect to "+ inadr);
        }else{
            System.out.println("connenction failed");
        }
    }
    
    
    void SendTo(Object sendObject){
        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(sendObject);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(scheduling.Client.class.getName()).log(Level.SEVERE, null, ex);
        }finally{

        }
    }
    
    Object ReceiveFrom(){
        Object obj = null;
        try{
            ois = new ObjectInputStream(sock.getInputStream());
            obj = ois.readObject();
            //ois.close();
        } catch (IOException ex) {
            Logger.getLogger(scheduling.Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(scheduling.Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(obj == null)System.out.println("null");
        return obj;
    }
}
*/ 