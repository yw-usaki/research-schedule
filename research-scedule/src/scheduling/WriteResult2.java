/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.awt.BorderLayout;
import java.text.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

/**
 *
 * @author 1151118
 */
public class WriteResult2 extends JFrame{
    private int Max = 255;
    JPanel panel;
    JLabel label;
    
    public WriteResult2(String str, Proclist Plist, DAG TaskGraph){
        panel = new JPanel();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(str);
        this.setSize(1500, 1000);
        this.setVisible(true);
        MyRect rect = new MyRect(TaskGraph, Plist);
        //JScrollBar scrollbarH = new JScrollBar(JScrollBar.HORIZONTAL);
        //JScrollBar scrollbarV = new JScrollBar(JScrollBar.VERTICAL);
        //rect.add(scrollbarH);
        //rect.add(scrollbarV);
        
        //getContentPane().add(scrollbarH, BorderLayout.SOUTH);
        //getContentPane().add(scrollbarV, BorderLayout.EAST);
        
        
        getContentPane().add(rect);
    }
   
    class MyRect extends JPanel{
        DAG TaskGraph;
        Proclist Plist;
        float stime , ftime;
        private int initialx = 10;
        private int initialy = 10;
        private int up = 10;
        private float taskx;
        private float tasky = 10;
        private int width;
        private int height = 10;
        
        JLabel procID;
        JLabel coreID;
        
        public MyRect(DAG TaskGraph, Proclist Plist){
            this.TaskGraph = new DAG();
            this.TaskGraph = TaskGraph;
            this.Plist = new Proclist();
            this.Plist = Plist;

        }
        
    @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            //プロセッサのIDとコアのIDを描画
            for(int i = 0; i < Plist.procnum; i++){
                if(Plist.procs[i].getSw() != 1){
                    g2.setColor(Color.BLACK);
                    g2.drawString("Proc "+ Plist.procs[i].getProcID(), initialx, Plist.procs[i].Counter * (Plist.procs[i].getCore_number() +1) * 50 + initialy);
                    for(int j = 0; j < Plist.procs[i].getCore_number(); j++){
                        g2.drawString("Core "+ Plist.procs[i].Cores[j].getCoreID(), initialx * 2, Plist.procs[i].Counter * (Plist.procs[i].getCore_number() +1) * 50 + (j + 1) * 50 + initialy);
                        
                    }
                }
            }
            //タスクの処理時間等を描画
            for(int i = 0; i < TaskGraph.total_tasks; i++){
                int counter = 0;
                float length = 0;
                            taskx = TaskGraph.task[i].getStart_time() * up + 100;
                            tasky = Plist.procs[TaskGraph.task[i].allocate_proc_ID].Counter 
                                    * (Plist.procs[TaskGraph.task[i].allocate_proc_ID].getCore_number() +1) * 50
                                    + (TaskGraph.task[i].allocate_core_ID + 1) * 50 + initialy;
                            length = TaskGraph.task[i].getWorking_time() * up  * 10;
                            
                            //タスクIDの会得（黒字）
                            g2.setColor(Color.BLACK);
                            String str ="n"+TaskGraph.task[i].getID(); 
                            if(!str.equalsIgnoreCase("n0")){
                                g2.drawString(str, (float)taskx, (float)tasky - 5);
                                
                                String stime = String.format("%.3f", TaskGraph.task[i].getStart_time());
                                String ftime = String.format("%.3f", TaskGraph.task[i].getFinish_time());

                                g2.drawString(stime, (float)taskx, (float)tasky +20);
                                g2.drawString(ftime, (float)taskx , (float)tasky +32);
                            }
                            //スタートラインの描画
                            Line2D startline = new Line2D.Float(taskx, tasky -2, taskx, tasky + height +2);
                            g2.draw(startline);
                            
                            //タスクの処理時間を書きこむ
                            for(int n = 0; n < TaskGraph.task[i].executeFrequencyStep.size(); n++){
                                if(TaskGraph.task[i].executeFrequencyStep.get(n) != 0.0){
                                    if(counter == (int)length)break;
                                    Rectangle2D shape2 = new Rectangle2D.Float(taskx , tasky, (float)0.2 , height);
                                    taskx+=0.2;
                                    //System.out.print(" "+TaskGraph.task[i].executeFrequencyStep[n]);
                                    if(TaskGraph.task[i].executeFrequencyStep.get(n) == (float)3.7)g2.setColor(Color.RED);
                                    else if(TaskGraph.task[i].executeFrequencyStep.get(n) == (float)3.5)g2.setColor(Color.GREEN);
                                    else if(TaskGraph.task[i].executeFrequencyStep.get(n) == (float)3.3)g2.setColor(Color.BLUE);
                                    else if(TaskGraph.task[i].executeFrequencyStep.get(n) == (float)3.1)g2.setColor(Color.LIGHT_GRAY);
                                    else if(TaskGraph.task[i].executeFrequencyStep.get(n) == (float)3.0)g2.setColor(Color.CYAN);
                                    else if(TaskGraph.task[i].executeFrequencyStep.get(n) == (float)2.5)g2.setColor(Color.CYAN);
                                    
                                    g2.fill(shape2);
                                    counter++;
                                }
                            }
                            //System.out.println("\ntask"+ TaskGraph.task[i].getID() + " counter=" + counter + " worktime="+TaskGraph.task[i].getWorking_time()+" step="+TaskGraph.task[i].executeFrequencyStep.length);
                            //エンドラインの描画
                            g2.setColor(Color.red);
                            Line2D endline = new Line2D.Float(taskx, tasky -10, taskx, tasky+ 1 + height +10);
                            g2.draw(endline);
                            g2.setColor(Color.cyan);
                            taskx = TaskGraph.task[i].getFinish_time() * up + 100;
                            Line2D endline2 = new Line2D.Float(taskx, tasky -5, taskx, tasky + 1 + height +5);
                            g2.draw(endline2);
            
            }
            
        }
    }
}
