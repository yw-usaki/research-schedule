/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

/**
 *
 * @author 1151118
 */
public class MyMath {
    //階上の計算
    static int factorial(int n){
        int fact=1;

        if(n==0)
            return fact;
        else{
            for(int i=n; i>0; i--)
                fact*=i;
            return fact;
        }
    }
}
