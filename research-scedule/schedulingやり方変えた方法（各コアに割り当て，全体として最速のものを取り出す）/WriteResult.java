/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

/**
 *
 * @author 1151118
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class WriteResult extends JFrame{
    Proclist Plist = new Proclist();
    private int Max = 255;
    JPanel panel;
    JLabel label;
    
    public WriteResult(String str, Proclist Plist){
        panel = new JPanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(str);
        this.setSize(1500, 1000);
        this.setVisible(true);
        MyRect rect = new MyRect(Plist);
        
        getContentPane().add(rect);
    }
   
    class MyRect extends JPanel{
        Proclist Plist;
        private int x = 0;
        private int y = 0;
        private int up = 10;
        private double taskx;
        private int tasky = 10;
        private int width;
        private int height = 10;
        
        JLabel procID;
        JLabel coreID;
        
        public MyRect(Proclist Plist){
            this.Plist = new Proclist();
            this.Plist = Plist;
        }
        
    @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            for(int i = 0; i < Plist.procnum; i++){
                if(Plist.procs[i].getSw() != 1){
                    g2.drawString("Proc "+ Plist.procs[i].getProcID() , 10, y);
                    g2.setColor(Color.BLACK);
                    y+=40;
                    for(int j = 0; j < Plist.procs[i].getCore_number(); j++){
                        g2.drawString("CoreID "+ j, 30, y);
                       
                        for(int m = 0; m <  Plist.procs[i].Cores[j].ExecuteTask.size(); m++){
                            double length = 0;
                            taskx = 0;
                            length = Plist.procs[i].Cores[j].getExecuteTask(m).getWorking_time() * up;
                            taskx  = Plist.procs[i].Cores[j].getExecuteTask(m).getStart_time() * up + 100;
                            
                            //タスクIDの会得（黒字）
                            g2.setColor(Color.BLACK);
                            String str ="n"+Plist.procs[i].Cores[j].getExecuteTask(m).getID(); 
                            if(Plist.procs[i].Cores[j].getExecuteTask(m).getID() != 0)g2.drawString(str, (float)(taskx + length/2 - 5), (float)(y -10));
                            
                            //スタートラインの描画
                            Line2D startline = new Line2D.Double(taskx, y -2, taskx, y + height +2);
                            g2.draw(startline);
                            
                            System.out.println("task"+ Plist.procs[i].Cores[j].getExecuteTask(m).getID());
                            Plist.procs[i].Cores[j].getExecuteTask(m).outputExecuteFrequency();
                            System.out.println("");
                            
                            //タスクの処理時間を書きこむ
                            for(int n = 0; n < Plist.procs[i].Cores[j].getExecuteTask(m).executeFrequencyStep.length; n++){
                                if((Plist.procs[i].Cores[j].getExecuteTask(m).executeFrequencyStep[n] == 2.0) || (Plist.procs[i].Cores[j].getExecuteTask(m).executeFrequencyStep[n] == 0.0))break;
                                else{
                                    Rectangle2D shape2 = new Rectangle2D.Double(taskx += 1 , y, 1, height);
                                    if(Plist.procs[i].Cores[j].getExecuteTask(m).executeFrequencyStep[n] == 2.8)g2.setColor(Color.RED);
                                    else if(Plist.procs[i].Cores[j].getExecuteTask(m).executeFrequencyStep[n] == 2.5)g2.setColor(Color.GREEN);
                                    else if(Plist.procs[i].Cores[j].getExecuteTask(m).executeFrequencyStep[n] == 1.3)g2.setColor(Color.CYAN);
                                    g2.fill(shape2);
                                }
                            }
                            
                            //エンドラインの描画
                            g2.setColor(Color.BLACK);
                            Line2D endline = new Line2D.Double(taskx , y -2, taskx, y + height +2);
                            g2.draw(endline);
                       }    
                       y+=40;
                    }
                }
                else {
                    x= 100;
                    y = 20;
                }
            }
        }
    }
}
  
