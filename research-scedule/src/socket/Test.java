/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import java.awt.image.SampleModel;
import java.util.Random;

/**
 *
 * @author 1151118
 */
public class Test {
    int length = 10000000;
    double[] array;
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
        Test test = new Test();
        test.sampleLoad();
    }
    * */
    
    
    void sampleLoad(){
        Random rand = new Random();
        int index1 = 0;
        int index2 = 0;
        array = new double[length];
        long startTime = new Long(System.currentTimeMillis());
        
        //for(double x : array)
        //    x = rand.nextInt();
        double counter = 0;
        while(counter < 900000000){
            for(int i = 0; i < 1; i++) {
                index1 = rand.nextInt(length);
                index2 = rand.nextInt(length);
            }
            swap(index1, index2);
            counter++;
            //System.out.println(counter);
        }
        long endTime = new Long(System.currentTimeMillis());
        
        System.out.println("execute finish  calculate time="+(endTime - startTime));
                
    }
    
    void swap(int index1, int index2){
        double value;
        value = array[index1];
        array[index1] = array[index2];
        array[index2] = value;
    }
}
