import java.io.*;
import java.net.*;

class WorkerThread extends Thread {
    private Socket clientSocket;

    public WorkerThread(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            System.out.println("Received request: " + request);

            // Simulate processing
            Thread.sleep(2000);

            String response = "Processed: " + request;
            out.println(response);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}