import java.io.*;
import java.net.*;

public class Master {
    private static final int PORT = 5000;
    private static final String[] WORKER_IPS = {"192.168.1.101", "192.168.1.102", "192.168.1.103"};
    private static final int WORKER_PORT = 6000;
    private static int currentWorker = 0;

  
    public static synchronized String getNextWorkerIP() {
        String workerIP = WORKER_IPS[currentWorker];
        currentWorker = (currentWorker + 1) % WORKER_IPS.length;
        return workerIP;
    }

    public static int getWorkerPort() {
        return WORKER_PORT;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Master started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new WorkerThread(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
