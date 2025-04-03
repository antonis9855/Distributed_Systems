import java.io.*;
import java.net.*;

public class Master {
    private static final int PORT = 5000;

    private static final String[] WORKER_IPS = {"127.0.0.1", "127.0.0.1", "127.0.0.1"};
    private static final int WORKER_PORT = 6000;
    private static int currentWorker = 0;

   
    public static String selectWorker(String storeName) {
       
        int WorkerIndex = Math.abs(storeName.hashCode()) % WORKER_IPS.length;
        String workerIP = WORKER_IPS[WorkerIndex];
        return workerIP;
    }

    public static int getWorkerPort() {
        return WORKER_PORT;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 10, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Master started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new WorkerStarter(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Error in Master: " + e.getMessage());
        }
    }
}




class WorkerStarter extends Thread {
    private Socket clientSocket;

    public WorkerStarter(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            
            String command = in.readLine();
            System.out.println("Received command from Manager: " + command);
            out.println("Acknowledged: " + command);

            
            String workerIP = Master.selectWorker(command);
            int workerPort = Master.getWorkerPort();

           
            try (Socket workerSocket = new Socket(workerIP, workerPort);
                 BufferedReader workerIn = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
                 PrintWriter workerOut = new PrintWriter(workerSocket.getOutputStream(), true)) {
                 
                workerOut.println(command);
                
                String response = workerIn.readLine();
                
                out.println(response);
            } catch (IOException e) {
                System.out.println("Error connecting to worker " + workerIP + ": " + e.getMessage());
                out.println("Error connecting to worker node");
            }
        } catch (IOException e) {
            System.out.println("Error in WorkerThread: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}