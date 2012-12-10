package scheduling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import node.NodeClientBean;

//import Beans.TaskPortBean;


public class Task extends Thread{

	final int PORT_BASE = 35000;
	final int DATA_TRANS_PORT_BASE = 38000;
	final int COMM_WEIGHT = 1 * 1000 * 1000 * 452 /8;
	byte[] dummyByte;
        int procnum;
	
	//MachineExamBeans task = null;
        Tasknode task;
	ProcessorBean processorBean = null;
	DataBean data = null;
        DataBean sendData = new DataBean();
        DataBean sendData2 = new DataBean();
	static int rebootTime = 0;
	
	public Task(Tasknode task, ProcessorBean processorBean, DataBean data , byte[] dummyByte) {
		
		this.task = task;
		this.processorBean = processorBean;
		this.data = data;
                this.procnum = procnum;
               
	}
        
        public void run() {
		boolean quitFlag = false;
		byte[] dummyByte = new byte[COMM_WEIGHT/10];
                
                
                System.out.println("thread"+task.getID()+" started");
                for(int i = 0; i < 12000000;) i++;
               

                
                
                //全てのタスクがスレッド化されるまでルートタスクが待機
                if(task.getID() == data.rootTaskID){
			LinkedList<ReadySignalGetter> readyThreads = new LinkedList<ReadySignalGetter>();
			try {
				ServerSocket rss = new ServerSocket(30021);
				for(int i = 0; i < data.TaskGraph.total_tasks -1  ; i++){
                                        System.out.println("total RSG:" + (data.TaskGraph.total_tasks -1));
					Socket soc = rss.accept();
					ReadySignalGetter rsg = new ReadySignalGetter(soc);
					rsg.start();
					readyThreads.add(rsg);
				}
				//準備完了するまで待つ
				for(ReadySignalGetter rsg: readyThreads){
					rsg.join();
				}
				rss.close();
				
				//タスクの開始をマスターに告げる
				Socket rsoc = new Socket(data.masterIp, 30026);	
				OutputStream rbos = rsoc.getOutputStream();
				rbos.write(0);
				rbos.flush();
				rbos.close();
				rsoc.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
                
                
                
                //ルートタスク以外は起動完了をルートへ告げる
                if(task.getID() != data.rootTaskID){
			//準備完了情報の送信
                        data.task = String.valueOf(task.getID());
			while(true){
				try {   
					//Thread.sleep((long) (Math.random()*1000));
					Socket rsoc = new Socket(getIp(getProcIdCoreId(data.rootTaskID, data), data), 30021);	
					//BufferedOutputStream roos = new BufferedOutputStream(rsoc.getOutputStream());
                                        ObjectOutputStream roos = new ObjectOutputStream(rsoc.getOutputStream());
                                        String Str = "task "+task.getID()+" stand by ready";
					roos.writeChars(Str);
					roos.flush();
					roos.close();
					rsoc.close();
					break;
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
                
                ServerSocket ss = openSeverSocket(PORT_BASE + task.getID());
                System.out.println("Task"+task.getID()+" on "+processorBean.procID+"-"+processorBean.coreID+" port:"+ss.getLocalPort()+" Thread is readied");
                //ServerSocket dss = openSeverSocket(DATA_TRANS_PORT_BASE + task.getID());
                //System.out.println("■Task"+task.getID()+" on "+processorBean.procID+"-"+processorBean.coreID+" data port:"+dss.getLocalPort()+" Thread is readied");
                
                int ThisTaskIndexOfExlist = data.ExTaskLists.ExTaskList.indexOf(task);
                
                Socket soc;


                
                //全親ノードからの終了信号を待つ
                if(task.getID() != data.rootTaskID){
			LinkedList<ReadySignalGetter> readyThreads = new LinkedList<ReadySignalGetter>();
			try {
				//ServerSocket rss = new ServerSocket(30021);
				for(int i = 0; i < task.predecessor.size() ; i++){
					soc = ss.accept();
					ReadySignalGetter rsg = new ReadySignalGetter(soc);
					rsg.start();
					readyThreads.add(rsg);
				}
				//準備完了するまで待つ
				for(ReadySignalGetter rsg: readyThreads){
					rsg.join();
				}
                                
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
                }
                System.out.println("task "+task.getID() +" all predececor finished");
                
                
                //同一コアで実行する先行タスクの終了信号を受信
                if(task.getID() != data.ExTaskLists.ExTaskList.get(0).getID()){
                    int PredIndex = ThisTaskIndexOfExlist - 1;
                    System.out.println("task"+task.getID()+" predIndex : "+PredIndex);
                    try {
                        soc = ss.accept();
                        ReadySignalGetter rsg = new ReadySignalGetter(soc);
                        rsg.start();
                        rsg.join();
                        System.out.println("task " +task.getID() +"  received finish signal");
                        //ss.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                Random rand =  new Random();
                                
                System.out.println("task "+task.getID() +" same proc ex predececor finished");
                //ダミーデータの生成
                if(data.TaskGraph.total_tasks == 90)
                        for(Byte v : dummyByte)v = Byte.parseByte(String.valueOf(rand.nextInt(100)));
                ServerSocket dss;
                //データ受信フェーズ
                if(task.getID() != data.rootTaskID){
                        
                        //親タスクにデータを送信してこいと要求
                        for(int v : task.predecessor){
                            String nextIP = getIp(getProcIdCoreId(v, data), data);
                            int nextPort = DATA_TRANS_PORT_BASE + v;
                            Socket nextsoc;
                            System.out.println("nextIP : "+nextIP+"nextPort : "+nextPort);
                            try{
                                nextsoc = new Socket(nextIP, nextPort);
                                BufferedOutputStream bos = new BufferedOutputStream(nextsoc.getOutputStream());
                                ObjectOutputStream oos = new ObjectOutputStream(bos);
                                sendData.task = "PredExtask"+task.getID()+":Finished"; 
                                oos.writeObject(task.getID());

                                oos.flush();
                                oos.close();
                                nextsoc.close();

                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        
                            System.out.println("task "+ task.getID()+" start received data-------");
                            
                            //データの受信
                            dss = openSeverSocket(DATA_TRANS_PORT_BASE + task.getID());
                            LinkedList<ReadySignalGetter2> readyThreads = new LinkedList<ReadySignalGetter2>();
                            try {
                                //ServerSocket rss = new ServerSocket(30021);
                                soc = dss.accept();
                                ReadySignalGetter2 rsg = new ReadySignalGetter2(soc);
                                rsg.start();
                                readyThreads.add(rsg);
                                System.out.println("create ReadySignalGetter2");
                                //準備完了するまで待つ
                                rsg.join();
                                dss.close();
                            } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                            }
                        }
                }
                
                    
                long startTime = System.currentTimeMillis();
		/////////////////////
		//タスクの実行開始/////
		///////////////////
		processorBean.execute(task);
		/////////////////////
		//タスクの実行終了////
		/////////////////
                System.out.println("●task:"+task.getID()+" extime:"+(System.currentTimeMillis() - startTime));
                
                
                //DataBean sendData = null;
                

                
                //次のタスクを各ノードに送信する
                for(int v: task.successor){
                        String clientIp = getIp(getProcIdCoreId(v,data), data);
                        int clientPort = PORT_BASE + v;
                        
                        //処理タスク情報送信
                        Socket ncsoc;
                        try {
                                ncsoc = new Socket(clientIp, clientPort);
                                BufferedOutputStream bos = new BufferedOutputStream(ncsoc.getOutputStream());
                                ObjectOutputStream oos = new ObjectOutputStream(bos);
                                String sendStr = "task"+task.getID()+":Finished"; 
                                oos.writeObject(sendStr);

                                oos.flush();
                                oos.close();
                                ncsoc.close();

                        } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println(task.getID());
                                System.err.println(getProcIdCoreId(v,data) + " "+clientPort);
                                System.err.println(getIp(getProcIdCoreId(v,data),data) + " "+clientPort);
                        }	

                }
                
                //同一コア上で実行する後続タスクに対してシグナルを送信する
                if(ThisTaskIndexOfExlist != data.ExTaskLists.ExTaskList.size() - 1){
                    String nextIP = getIp(getProcIdCoreId(data.ExTaskLists.ExTaskList.get(ThisTaskIndexOfExlist + 1).getID(), data), data);
                    int nextPort = PORT_BASE + data.ExTaskLists.ExTaskList.get(ThisTaskIndexOfExlist + 1).getID();
                    //DataBean senddata = new DataBean();
                    Socket nextsoc;
                    
                    try{
                        nextsoc = new Socket(nextIP, nextPort);
                        BufferedOutputStream bos = new BufferedOutputStream(nextsoc.getOutputStream());
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        sendData.task = "PredExtask"+task.getID()+":Finished"; 
                        oos.writeObject(sendData.task);

                        oos.flush();
                        oos.close();
                        nextsoc.close();
                        System.out.println("send finish signal to task " + data.ExTaskLists.ExTaskList.get(ThisTaskIndexOfExlist + 1).getID());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                }
                
                dss = openSeverSocket(DATA_TRANS_PORT_BASE + task.getID());
                System.out.println("task.sucessor size : "+task.successor.size());
                //データ送信フェーズ
                for(int v: task.successor){
                        try {
                            //データ受信準備完了？の受信
                            Socket resoc = dss.accept();
                            ObjectInputStream in = new ObjectInputStream(resoc.getInputStream());
                            int taskID = -1;
                            try {
                                taskID = (Integer)in.readObject();
                                
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("=====================================reseiced task ID : "+ taskID);
                                                        
                            String clientIp = getIp(getProcIdCoreId(taskID,data), data);
                            int clientPort = DATA_TRANS_PORT_BASE + taskID;

                            if(taskID == -1)System.exit(0);
                            
                            //処理タスク情報送信
                            Socket ncsoc;
                            try {
                                    ncsoc = new Socket(clientIp, clientPort);
                                    BufferedOutputStream bos = new BufferedOutputStream(ncsoc.getOutputStream());
                                    ObjectOutputStream oos = new ObjectOutputStream(bos);

                                    if(task.allocate_proc_I != data.TaskGraph.task[data.TaskGraph.gettask_i(data.TaskGraph, taskID)].allocate_proc_I){
                                        System.out.println("send  dataBeans sendData ");
                                        sendData.dummyByte = new byte[COMM_WEIGHT];
                                        for(byte n : sendData.dummyByte) n = 64;
                                        sendData.task = "task"+String.valueOf(task.getID())+":Finished"; 
                                        oos.writeObject(sendData);
                                    }else{
                                        System.out.println("send  dataBeans sendData2 ");
                                        sendData2.dummyByte = new byte[1];
                                        for(byte n : sendData2.dummyByte) n =100;
                                        sendData2.task = "task"+task.getID()+":Finished"; 
                                        oos.writeObject(sendData2);
                                    }

                                    oos.flush();
                                    oos.close();
                                    ncsoc.close();
                            } catch (Exception e) {
                                    e.printStackTrace();
                                    System.err.println(task.getID());
                                    System.err.println(getProcIdCoreId(taskID,data) + " "+clientPort);
                                    System.err.println(getIp(getProcIdCoreId(taskID,data),data) + " "+clientPort);
                            }	
                            
                        
                        } catch (IOException ex) {
                            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        

                }
                
                
                System.out.println("task:"+task.getID() +" data.finalTaskID:"+data.finalTaskID);
                if(task.getID() == data.finalTaskID){
                    
                    //全タスクの終了をマスターに告げる
                    Socket rsoc;	
                    try {
                        rsoc = new Socket(data.masterIp, 20000);
                        OutputStream rbos = rsoc.getOutputStream();
                        rbos.write(0);
                        rbos.flush();
                        rbos.close();
                        rsoc.close();
                        System.out.println("send finish signal to masternode");
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                System.out.println("thread"+task.getID()+" finished");
        }
      
        
	private ServerSocket openSeverSocket(int port) {
		
		ServerSocket ss = null;
		boolean isReady = false;
		int retry = 0;
		while(!isReady){
			try {
				ss = new ServerSocket(port);
				isReady = true;
				
			} catch (IOException e) {
				
				//e.printStackTrace();
				System.err.println("Can't open the port. port:"+port);
				//System.exit(1);
				isReady = false;
				
				try {
					Thread.sleep(1000);
					System.out.println("Opening port:" +port+ " please wait... "+(retry++));
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}			
		}
		return ss;
	}

	private static String getProcIdCoreId(int taskID, DataBean data) {
		String str = null;
		for(Tasknode v: data.TaskGraph.task){
			if(v.getID() == taskID){
                            return (v.allocate_proc_I + 1)+":"+(v.allocate_core_I);
                        }
		}
		return "not found succ information";
	}

	private static String getIp(String procIdCoreID, DataBean data) {
              String tmp[] = procIdCoreID.split(":");

              for(NodeClientBean v: data.nodeClientBeans){
			if(v.procID == Integer.parseInt(tmp[0]) && v.coreID == Integer.parseInt(tmp[1])){
				return v.clientAddress;
			}
              }
		return null;
	}

	private static DataBean getInputData(ServerSocket ss) {
		
		
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
			bis.close();
			soc.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		return data;
	}
	
	
	public class ReadySignalGetter extends Thread{
		
		Socket soc; 
		public DataBean data;
		public ReadySignalGetter(Socket soc) {
			this.soc = soc;
		}

		public void run(){
			try {
                                InputStream iis = soc.getInputStream();
                                //BufferedInputStream bis = new BufferedInputStream(soc.getInputStream());
				//ObjectInputStream iis = new ObjectInputStream(soc.getInputStream());
				System.out.print("ReadySignalGetter : ");
                                System.out.println(ConvertInputStreamToString(iis));
                                //System.out.println(ConvertInputStreamToString(iis)) ;
				iis.close();
				soc.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
                
                private String ConvertInputStreamToString(InputStream is)throws IOException{
                    InputStreamReader reader = new InputStreamReader(is);
                    StringBuffer builder = new StringBuffer();
                    char[] buf = new char[1024];
                    int numRead;
                    while(0 <= (numRead = reader.read(buf))){
                        builder.append(buf, 0, numRead);
                    }
                    return builder.toString();
                }
                
		
	}
        
        public class ReadySignalGetter2 extends Thread{
		
		Socket soc; 
		public DataBean data;
		public ReadySignalGetter2(Socket soc) {
			this.soc = soc;
		}

		public void run(){
			try {
                                //InputStream iis = soc.getInputStream();
                                //BufferedInputStream bis = new BufferedInputStream(soc.getInputStream());
				ObjectInputStream iis = new ObjectInputStream(soc.getInputStream());
				data = (DataBean)iis.readObject();
                                System.out.print("⇒ReadySignalGetter2 : ");
                                System.out.println(data.dummyByte.length);
                                //System.out.println(ConvertInputStreamToString(iis)) ;
				iis.close();
				soc.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
                
                private String ConvertInputStreamToString(InputStream is)throws IOException{
                    InputStreamReader reader = new InputStreamReader(is);
                    StringBuffer builder = new StringBuffer();
                    char[] buf = new char[1024];
                    int numRead;
                    while(0 <= (numRead = reader.read(buf))){
                        builder.append(buf, 0, numRead);
                    }
                    return builder.toString();
                }
                
		
	}

	

	public class Sender extends Thread{
		
		String sendIp;
		int sendPort;
		DataBean data;
		
		public Sender(String sendIp , int sendPort ,DataBean data){
			this.sendIp = sendIp;
			this.sendPort = sendPort;
			this.data = data;
		}
		
		public void run(){
			
			try {
				Socket ncsoc = new Socket(sendIp, sendPort);
	
				//BufferedOutputStream bos = new BufferedOutputStream(ncsoc.getOutputStream());
				ObjectOutputStream oos = new ObjectOutputStream(ncsoc.getOutputStream());
		
				oos.writeObject(data);
		    
				oos.flush();
				oos.close();
				ncsoc.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}

