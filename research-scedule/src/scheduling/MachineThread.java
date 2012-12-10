/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
import com.sun.swing.internal.plaf.metal.resources.metal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduling.Tasknode;
/**
 *
 * @author 1151118
 */public class MachineThread extends DAG implements Runnable{
    public static MessageBox msgBox = new MessageBox();
    String Name;
    public MachineThread(String name){
        this.Name =name;
    }
    
   

    
  public void run() {  //(2)スレッド実行コードをrunメソッドに記載
    for(int i = 1; i <= 20; i++) {  //(3)
        System.out.println(Name+":" + i);
        try {
            sleep(1000);
            msgBox.SendMessage("generate :"+Math.random());
            System.out.println("sender "+ Name+msgBox.getMessage());            
            System.out.println("Receiver"+Name+ "  message is "+msgBox.ReceivedMessage());
        } catch (InterruptedException ex) {
            System.out.println("error loger");
            Logger.getLogger(MachineThread.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
  }

  public void run(DAG TaskGraph){
    //(4)クラスExThread1のオブジェクトを生成
    Runnable runnable1 = new MachineThread("100");
    Runnable runnable2 = new MachineThread("101");
    Runnable runnable3 = new MachineThread("102");
    Runnable runnable4 = new MachineThread("103");
    Runnable runnable5 = new MachineThread("104");
      
    Thread thread1 = new Thread(runnable1);
    Thread thread2 = new Thread(runnable2);
    Thread thread3 = new Thread(runnable3);
    Thread thread4 = new Thread(runnable4);
    Thread thread5 = new Thread(runnable5);
    
    //(5)スレッドの実行
    thread1.start();
    thread2.start();
    //thread3.start();
    //thread4.start();
    //thread5.start();
    
  }

  private void sleep(long millis)throws InterruptedException{
      long startTime = System.currentTimeMillis();
      long nowTime;
      while(true){
          nowTime = System.currentTimeMillis();
          if(nowTime - startTime >= millis){
              break;
          }
      }
  }
}


class MessageBox{
        private String message;
        
        public String getMessage(){
            return message;
        }
        
        synchronized boolean SendMessage(String Data){
        // メッセージがすでに入っていれば、なくなるまで待つ
        try {
            while(message != null ){
                wait();
            }
            // メッセージを入れる
            message = Data;

            // メッセージを入れ終わったら別のスレッドを起こす
            notifyAll();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MachineThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
    
    synchronized String ReceivedMessage(){
        try{
            // メッセージがなければ、メッセージが入るまで待つ
       	    while(message == null){
                wait();
            }

            // メッセージ取りだし
            String s = message;
            message = null;

            // 取り出したら別のスレッドを起こす
            notifyAll();
            return s;

        // 以下例外処理
        }catch(Exception e){}
        System.err.println("messageOut:Error");
        System.exit(1);
        return "";
    }
}