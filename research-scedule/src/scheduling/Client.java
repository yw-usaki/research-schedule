package scheduling;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import node.NodeClientBean;

import scheduling.DAG;
import scheduling.Tasknode;

public class Client{
    static final int COMM_WEIGHT = 1000*1000*452/8;
    
    //マスター側の情報
    static final String masterIp = "163.221.92.218";
    static final int masterPort = 4000;
    
    //スレーブ側の情報
    static String slaveIp;
    static int slavePort = 4010;
    int procID;
    int coreID;
    
    
    static int dummyLoadSec;
    
    boolean allTaskExFinish = false;
    
    
    LinkedList<Tasknode> ExTaskList = new LinkedList<Tasknode>();
    ExTaskList ExTaskLists = new ExTaskList();
    
        
    //Socket sock;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    LinkedList<Tasknode> exList = null;
    
    public static void main(String[] args){
        Client client = new Client();
        client.ClientMain();
        
    }
    
    public void ClientMain(){
        LinkedList<node.NodeClientBean> nodeclients = new LinkedList<NodeClientBean>();
        //ローカル間でデータ転送する時に使う
        LinkedList<ProcessorBean> processorBeans = new LinkedList<ProcessorBean>();
        LinkedList<Integer> procNumbers = new LinkedList<Integer>();
        
        //データ受信用のポート決定，使えるポートを見つけるまでループ
        ServerSocket ss;
        while(true){
            try{
            ss = new ServerSocket(slavePort);
            System.out.println("Slave port"+slavePort);
            break;
            }catch(Exception e){
              slavePort++;
              continue;
            }
            
        }
        
        //自分の待ち受けポートをマスターに投げる
        setMyNode2Master(masterIp, masterPort, slavePort);
        LinkedList<Thread> threads = new LinkedList<Thread>();
        
        while(true){
            System.out.println("--------------------------------------------------------------------");
            
            DataBean data = null;
            data = getInputData(ss);
            
            data.masterIp = masterIp;
            data.masterPort = masterPort + 1;
            
            //自分のIPアドレスをマスターに教えてもらう
            //IFごとに違うIPがあってそれを取ってくるときのための対策
            if(data != null && data.slaveIp != null){
                    slaveIp = data.slaveIp;
                    System.out.println("I am "+slaveIp+":"+slavePort);
            }
            
            
            
            if(data != null && data.task.equals("quit")){
                    System.out.println("quit!");
                    System.exit(1);
            }
            
            if(data != null && data.task.equals("info")){
                //処理するタスクの情報を会得
                ExTaskLists = getInputTaskNodes(ss);
                ExTaskLists.outputData();
                System.out.println("state : "+ ExTaskLists.state);
                //全コアの情報を会得
                //nodeclients = getNodeClients(ss);
                System.out.println("------ output data of nodeclients ------");
                for(NodeClientBean v : data.nodeClientBeans){
                    v.outputdata();
                }
                data.ExTaskLists = ExTaskLists;
            }
            
            //このコアにタスクがスケジュールされていない時の処理
            if(ExTaskLists.state.equals("quit")){
                    System.out.println("quit!");
                    //System.exit(1);
            }
            
            if(ExTaskLists.state.equals("set")){
                System.out.println("in set");
                threads = new LinkedList<Thread>();
                ExTaskLists.outputData();
                NodeClientBean currentclient = new NodeClientBean();
                
                for(NodeClientBean v : nodeclients){
                    if(v.clientAddress == slaveIp && v.clientPort == slavePort)
                         currentclient = v; 
                }
                ProcessorBean procBean = new ProcessorBean(currentclient.procID , currentclient.coreID);
                
                //タスクスレッドの起動
                for(Tasknode v : ExTaskLists.ExTaskList){
                    System.out.print("task"+v.getID() +" set ......");
                    Thread Extask = new Task(v, procBean, data, new byte[COMM_WEIGHT/1000]);
                    Extask.start();
                    threads.add(Extask);
                }
                
                for(Thread v : threads){
                    
                    try {
                        v.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                allTaskExFinish = true;
            }            
            //System.out.println(getMemoryInfo());
            
            
            if(allTaskExFinish){
                break;
            }
        }
        System.out.println("slave end");
    }
    
    
    public static String getMemoryInfo() {
        DecimalFormat f1 = new DecimalFormat("#,###KB");
        DecimalFormat f2 = new DecimalFormat("##.#");
        long free = Runtime.getRuntime().freeMemory() / 1024;
        long total = Runtime.getRuntime().totalMemory() / 1024;
        long max = Runtime.getRuntime().maxMemory() / 1024;
        long used = total - free;
        double ratio = (used * 100 / (double)total);
        String info = 
        "Java Memory : Total=" + f1.format(total) + "、" +
        "Usage=" + f1.format(used) + " (" + f2.format(ratio) + "%)、" +
        "Available="+f1.format(max);
        return info;
    }

    private ExTaskList getInputTaskNodes(ServerSocket ss) {
            Socket soc = null;
            ExTaskList receiveLists = null;
            try {

                    soc = ss.accept();

                    BufferedInputStream bis = new BufferedInputStream(soc.getInputStream());
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    //TODO server
                    receiveLists = (ExTaskList) ois.readObject();

                    ois.close();


            } catch (IOException e) {
                    e.printStackTrace();
            } catch (ClassNotFoundException e) {

                    e.printStackTrace();
            }
            System.out.println("function getInputTaskNodes");
            return receiveLists;
    }
    
    private LinkedList<NodeClientBean> getNodeClients(ServerSocket ss) {
            Socket soc = null;
            LinkedList<NodeClientBean> receiveLists = null;
            try {

                    soc = ss.accept();

                    BufferedInputStream bis = new BufferedInputStream(soc.getInputStream());
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    //TODO server
                    receiveLists = (LinkedList<NodeClientBean>) ois.readObject();

                    ois.close();


            } catch (IOException e) {
                    e.printStackTrace();
            } catch (ClassNotFoundException e) {

                    e.printStackTrace();
            }
            System.out.println("function getInputnodeclients");
            return receiveLists;
    }
    
    private DataBean getInputData(ServerSocket ss) {


            Socket soc = null;
            DataBean data = null;
            try {

                    soc = ss.accept();

                    BufferedInputStream bis = new BufferedInputStream(soc
                                    .getInputStream());

                    ObjectInputStream ois = new ObjectInputStream(bis);

                    //TODO server
                    data = (DataBean) ois.readObject();
                    ois.close();


            } catch (IOException e) {
                    e.printStackTrace();
            } catch (ClassNotFoundException e) {

                    e.printStackTrace();
            }
            System.out.println("finish GetInputData");
            return data;
    }
    
    private static void setMyNode2Master(String remoteHost, int remotePort, int localPort) {
            // 自ノード登録 マスターサーバのIP指定
            String sAddr = remoteHost;
            int sPort = remotePort;
            try {
                    Socket soc = new Socket(sAddr, sPort); // ソケット(Socket)を開く
                    PrintStream out = new PrintStream(soc.getOutputStream());
                    out.println(localPort);
                    soc.close();
            } catch (Exception e) {

                    System.out.println("Can not connect Master Server");
                    System.exit(1);
            }
    }
    
}
/*
 * 
    public void run(){
        //　ソケットや入出力用のストリームの宣言
        DAG TaskList = new DAG();
        try {
            // ポート9998番を開く
            ConnectionTO(InetAddress.getLocalHost(), 9998);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        long time = System.currentTimeMillis();
        String str = String.valueOf(time)+"\n";
        System.out.println("sysout:"+String.valueOf(time));
        // サーバーにメッセージを送る
        //oos.writeBytes(str);

        System.out.println("heare");
        try {
            // サーバーからのメッセージを受け取り
            time = (Long)ReceiveFrom();
            //get task list
            exList = (LinkedList<Tasknode>)ReceiveFrom();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("start Time : "+time);
  
        long time2;
        while(true){
            time2 = System.currentTimeMillis();
            //System.out.println(time2-time);
            if(time2 > time){
                System.out.println("start exe");

                Test testload = new Test();
                testload.sampleLoad();

                break;
            }
        }
        System.out.println("client");
        for(Tasknode task : exList){
            task.output_result();
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
            oos = new ObjectOutputStream( sock.getOutputStream());
            oos.writeObject(sendObject);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }finally{

        }
    }
    
    Object ReceiveFrom() throws IOException, ClassNotFoundException{
        try{
            ois = new ObjectInputStream(sock.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ois.readObject();
    }
    
    
    public void ReceiveTaskList(LinkedList<Tasknode> TaskLists){
        for(Tasknode task: TaskLists){
            ExTaskList.add(task);
        }
    }
 * 
 * 
 */