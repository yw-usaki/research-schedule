/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import java.util.ArrayList;

/**
 *
 * @author 1151118
 */
public class Combination {
    static void print_perm(Tasknode[] perm){
        for(int x = 0; x < perm.length; x++){
            System.out.print(perm[x].getID() + " ");
        }
        System.out.println();
    }
    //組み合わせ順列の生成
    static void make_perm(int n, ArrayList<Tasknode[]> combitasks, Tasknode[] subtasks, Tasknode[] originalTasks){
        if(n == subtasks.length){
            Tasknode[] clone = new Tasknode[subtasks.length];
            for(int i = 0; i < subtasks.length; i++){
                clone[i] = subtasks[i].Clone();
            }
            combitasks.add(clone);
            //print_perm(subtasks);
        } else {
            for(int i = 0; i< subtasks.length; i++){
                if(originalTasks[i].permFlag) continue;
                subtasks[n] = originalTasks[i];
                originalTasks[i].permFlag = true;
                make_perm(n + 1, combitasks, subtasks, originalTasks);
                originalTasks[i].permFlag = false;
            }
        }
    }
    
    //組み合わせを変えたlooktasknodes内で依存関係が保たれているかを判断
    boolean checkPermutation(DAG TaskGraph, Tasknode[] lookTasknodes){
        int index;
        boolean loopEndFlag = false;
        boolean addFlag = true;
        ArrayList<Integer> predIDs;
        //System.out.print("start checkPermutation ");
        
        for(int i = 0; i < lookTasknodes.length; i++){
            predIDs = new ArrayList<Integer>();
            predIDs.add(lookTasknodes[i].getID());
            for(int x = 0; x < predIDs.size(); x++){
                System.out.println("System out predIDs contents :"+predIDs);
                index = TaskGraph.gettask_i(TaskGraph, predIDs.get(x));
                System.out.println("index = "+ index);
                while(true){
                    System.out.print("test");
                    if(TaskGraph.task[index].getID() == 0)
                        break;
                    int counter = 0;
                    while(counter < TaskGraph.task[index].predecessor.size()){
                        int predID;
                        if(counter == TaskGraph.task[index].predecessor.size() - 1)
                            loopEndFlag = true;
                        if((predID = (Integer)TaskGraph.task[index].predecessor.get(counter)) == 0)
                            loopEndFlag = true;
                        for(int looktaskIndex = i; looktaskIndex < lookTasknodes.length; looktaskIndex++){
                            if(predID == lookTasknodes[looktaskIndex].getID()){
                                System.out.println(" False!");
                                return false;
                            }
                        }
                        System.out.println("add predID to predIDs");
                        for(int m : predIDs){
                            if(m == predID)addFlag = false;
                        }
                        if(addFlag)predIDs.add(predID);
                        counter++;
                    }
                    if(loopEndFlag)
                        break;
                }
            }
        }
        System.out.println(" true!");
        return true;
    }

    
    //依存関係に違反しているものを除外した後の組み合わせ順列を表示
    void printPermutation(ArrayList<Tasknode[]> perm){
        System.out.println("combitasks size="+perm.size());
        for(int count = 0; count < perm.size(); count++){
            for(int n = 0; n < perm.get(count).length; n++)
                System.out.print(perm.get(count)[n].getID()+ " ");
            System.out.println(" length="+ perm.get(count).length);
        }
    }
}


/*
 * 
 public class perm1 {
  static void print_perm(int[] perm){
    for(int x: perm){
      System.out.print(x + " ");
    }
    System.out.println();
  }
  
  static void make_perm(int n, int[] perm, boolean[] flag){
    if(n == perm.length){
      print_perm(perm);
    } else {
      for(int i = 1; i <= perm.length; i++){
        if(flag[i]) continue;
        perm[n] = i;
        flag[i] = true;
        make_perm(n + 1, perm, flag);
        flag[i] = false;
      }
    }
  }

  public static void main(String[] args){
    make_perm(0, new int [4], new boolean [5]);
  }
}
 * 
 */