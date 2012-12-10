package socket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import scheduling.DAG;
import scheduling.Tasknode;

public class Cliant{
    

    
    public static void main(String[] args) {
        //　ソケットや入出力用のストリームの宣言
        DataOutputStream os = null;
        BufferedReader is = null;
        DAG TaskList = new DAG();
        ArrayList<Tasknode> ExTaskList = new ArrayList<Tasknode>();
        
        // ポート9999番を開く
        try {
            InetSocketAddress socketAddress = new InetSocketAddress("localhost", 9998);
            Socket sock = new Socket();
            sock.connect(socketAddress, 1000000);
            InetAddress inadr;
            
            if((inadr = sock.getInetAddress()) != null){
                System.out.println("Connect to "+ inadr);
            }else{
                System.out.println("connenction failed");
                return;
            }
            os = new DataOutputStream(sock.getOutputStream());
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            long time = System.currentTimeMillis();
            String str = String.valueOf(time)+"\n";
            System.out.println("sysout:"+String.valueOf(time));
            // サーバーにメッセージを送る
            if (sock != null && os != null && is != null) {
                try {
                    // メッセージを送ります
                   
                    os.writeBytes(str);
                    System.out.println("heare");
                    // サーバーからのメッセージを受け取り画面に表示します
                    String responseLine;
                    
                    if ((responseLine = is.readLine()) != null) {
                        time = Long.parseLong(responseLine);
                        System.out.println("Server: " + responseLine);
                    }
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
                    // 開いたソケットなどをクローズ
                    //os.close();
                    //is.close();
                    //echoSocket.close();
                } catch (UnknownHostException e) {
                    System.err.println("Trying to connect to unknown host: " + e);
                } catch (IOException e) {
                    System.err.println("IOException: " + e);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: localhost");
        }

    }
}