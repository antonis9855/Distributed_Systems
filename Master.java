import java.io.*;
import java.net.*;
import org.json.*;

public class Master {
    static int masterPort = 5000;
    static String[] workerHosts = {"127.0.0.1","127.0.0.1","127.0.0.1"};
    static int workerBasePort = 6000;

    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(masterPort)){
            System.out.println("Master started on port " + masterPort);
            while(true){
                Socket managerSocket = serverSocket.accept();
                new Thread(new ManagerHandler(managerSocket)).start();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

class ManagerHandler implements Runnable {
    private Socket managerSocket;
    ManagerHandler(Socket socket){
        this.managerSocket = socket;
    }

    @Override
    public void run(){
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(managerSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(
                managerSocket.getOutputStream(), true)
        ) {
            String request = reader.readLine();
            if(request == null) return;
            String[] parts = request.split(" ",2);
            String command = parts[0];
            String payload = parts.length>1 ? parts[1].trim() : "";

            if (command.equals("SEARCH")) {
                
                String resp = handleSearch(request);
                writer.println(resp);

            } else if (command.equals("TOTAL_SALES_PER_PRODUCT")
                    || command.equals("TOTAL_SALES_BY_STORE_TYPE")
                    || command.equals("TOTAL_SALES_BY_PRODUCT_CATEGORY")) {
                
                String resp = handleAggregate(command, payload);
                writer.println(resp);

            } else if (command.equals("ADD_SHOP")
                    || command.equals("ADD_ITEM")
                    || command.equals("REMOVE_ITEM")
                    || command.equals("RESTOCK")
                    || command.equals("BUY")) {
               
                String storeName;
                if (command.equals("REMOVE_ITEM")) {
                   
                    storeName = payload.split("\\|", 2)[0];
                } else {
                    storeName = new JSONObject(payload).getString("StoreName");
                }
                int idx  = Math.abs(storeName.hashCode()) % Master.workerHosts.length;
                String host = Master.workerHosts[idx];
                int    port = Master.workerBasePort + idx;

                String resp = talk(host, port, request);
                writer.println(resp != null ? resp : "ERROR");

            } else {
                writer.println("ERROR Unknown command");
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try { managerSocket.close(); }
            catch(IOException ignored){}
        }
    }

    private String handleSearch(String request){
        JSONArray merged = new JSONArray();
        for(int i=0; i<Master.workerHosts.length; i++){
            String resp = talk(
                Master.workerHosts[i],
                Master.workerBasePort + i,
                request
            );
            if(resp != null){
                JSONArray arr = new JSONArray(resp);
                for(int j=0; j<arr.length(); j++){
                    merged.put(arr.get(j));
                }
            }
        }
        return merged.toString();
    }

    private String handleAggregate(String command, String payload){
        JSONObject merged = new JSONObject();
        double totalSum = 0;
        for(int i=0; i<Master.workerHosts.length; i++){
            String line = command + (payload.isEmpty() ? "" : " " + payload);
            String resp = talk(
                Master.workerHosts[i],
                Master.workerBasePort + i,
                line
            );
            if(resp == null) continue;
            JSONObject obj = new JSONObject(resp);
            for(String key : obj.keySet()){
                double val = obj.getDouble(key);
                if(key.equals("total")) totalSum += val;
                else merged.put(key, merged.optDouble(key,0)+val);
            }
        }
        if(command.equals("TOTAL_SALES_BY_STORE_TYPE")
        || command.equals("TOTAL_SALES_BY_PRODUCT_CATEGORY")){
            merged.put("total", totalSum);
        }
        return merged.toString();
    }

    private String talk(String host, int port, String msg){
        try (
            Socket sock = new Socket(host, port);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()))
        ) {
            out.println(msg);
            return in.readLine();
        } catch(IOException e){
            return null;
        }
    }
}
