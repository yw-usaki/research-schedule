/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 1151118
 */
public class perm {
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
