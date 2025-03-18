public class Multithreading {

    public static void main(String[] args) {

        for(int i = 1; i <= 5; i++){

            Threads thread = new Threads(i);

            thread.start();



        }
        
    }
}
