class Threads extends Thread {

int threadNum;

    public Threads(int threadNum){

        this.threadNum = threadNum;

     }
    
        @Override
    public void run(){

        for(int i = 1; i <=5; i++){

             System.err.println(i + " is printed from thread " + threadNum);

            try{
                    Thread.sleep(1000);
                }
        catch(InterruptedException e){

                    }
                }
            }
        }
