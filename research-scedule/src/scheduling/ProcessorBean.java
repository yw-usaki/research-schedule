package scheduling;

import java.util.Random;


public class ProcessorBean {

	//パラメータ
	static long compWeight = 1000L;
	//static long compWeight = 1000L;
	static int length = 10000000;
        static double[] array;
	
	public int procID;
        public int coreID;
        
	public ProcessorBean(int procID , int coreID) {
		this.procID = procID;	
                this.coreID = coreID;
	}

	public synchronized void execute(Tasknode task){
		
		System.out.println("Task"+task.getID()+" on Proc"+(task.allocate_proc_I + 1)+" Start");
		
        	dummyLoad(task.totalWeight , task.getID());
		
		System.out.println("Task"+task.getID()+" on Proc"+task.allocate_proc_I+" Finish");
		
	}

	private static void dummyLoad(float weight, int taskID){
                long totalWeight = (long)weight*10*1000*1000/4;

                
                Random rand = new Random();
                int index1 = 0;
                int index2 = 0;
                array = new double[length];
                
                System.out.println("dammyload in");
                //for(double x : array)
                //    x = rand.nextInt();
                for(long counter = 0 ; counter < totalWeight; counter++){
                    //long startTime = new Long(System.currentTimeMillis());
                    for(int i = 0; i < 1; i++) {
                        index1 = rand.nextInt(length);
                        index2 = rand.nextInt(length);
                    }
                    swap(index1, index2);
                    //long endTime = new Long(System.currentTimeMillis());
                    //System.out.println("execute finish  calculate time="+(endTime - startTime));
                }
                /*
		for(long i = 0L; i < totalWeight ;){
                    i++;
                }
                */
	}
	
        static void swap(int index1, int index2){
            double value;
            value = array[index1];
            array[index1] = array[index2];
            array[index2] = value;
        }
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessorBean) {
			return ((ProcessorBean) obj).procID == procID;
		} else {
			return false;
		}
	}
	
}
