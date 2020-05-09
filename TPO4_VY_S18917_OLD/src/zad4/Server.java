/**
 * @author Voiko Yehor S18917
 */

package zad4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    String host;
    int port;
    ServerSocketChannel serverSocket;
    Selector selector;
    boolean running;
    SelectionKey selectKey;
    ArrayList<String> serverLog;
    HashMap<String, ArrayList<String>> clientLogs;
    ExecutorService server;
    HashMap<String, SocketChannel> clients;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        serverLog = new ArrayList<String>() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                forEach(e -> sb.append(e).append("\n"));
                return sb.toString();
            }
        };
        clientLogs = new HashMap<>();
        try {
            clients = new HashMap<>();
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            InetSocketAddress hostaddress = new InetSocketAddress(host, port);
            serverSocket.bind(hostaddress);
            serverSocket.configureBlocking(false);
            int ops = serverSocket.validOps();
            selectKey = serverSocket.register(selector, ops, null);
            running = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        server = Executors.newSingleThreadExecutor();
        server.execute(() -> {
            String clientName = "";
            String output = "";
            String result="";
            while (running) {
                try {
//                    System.out.println("Waiting for the select operation...");
                    int noOfKeys = selector.select();
//                    System.out.println("The Number of selected keys are: " + noOfKeys);
                    Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
                    while (itr.hasNext()) {
                        SelectionKey ky = itr.next();
                        itr.remove();
                        if (ky.isAcceptable()) {
//                            System.out.println("New connection!");
                            // The new client connection is accepted
                            SocketChannel client = serverSocket.accept();
                            client.configureBlocking(false);
                            // The new connection is added to a selector
                            System.out.println(1);
                            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            System.out.println(2);
                            continue;
                        } else if (ky.isReadable()) {
                            // Data is read from the client
                            System.out.println(3);
                            SocketChannel client = (SocketChannel) ky.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            client.read(buffer);
                            output = new String(buffer.array()).trim();
                            System.out.println("[SERVER] New output: " + output);
                            if (output.contains("login")) {
                                ArrayList<String> tmp = new ArrayList<String>() {
                                    @Override
                                    public String toString() {
                                        StringBuilder sb = new StringBuilder();
                                        forEach(e -> sb.append(e).append("\n"));
                                        return sb.toString();
                                    }
                                };
                                clientName = output.split(" ")[1].trim();
                                tmp.add("=== " + clientName + " log start ===");
                                tmp.add("logged in");
//                                System.out.println(output.split(" ")[1].trim() + " logged in at " + LocalTime.now());
                                clientLogs.put(clientName, tmp);
                                serverLog.add(clientName + " logged in at " + LocalTime.now());
                                client.write(ByteBuffer.wrap("logged in".getBytes()));
                            } else if (output.contains("bye")) {
                                clientName = output.split(" ")[output.split(" ").length - 1];
                                clientLogs.get(clientName).add("logged out");
                                clientLogs.get(clientName).add("=== " + clientName + " log end ===");
//                                System.out.println("[SERVER] Bye! + Name:" + clientName);
//                                System.out.println("[SERVER} NULL?: " + clientLogs.get(clientName) + ", ClientName: " + clientName);
                                serverLog.add(clientName + " logged out at " + LocalTime.now());
                                client.write(ByteBuffer.wrap(clientLogs.get(clientName).toString().getBytes()));
                                ky.interestOps(SelectionKey.OP_READ);
                                client.close();
                                System.out.println("The Client messages are complete; close the session.");
                            } else {
                                String[] dates = output.split(" ");
                                clientName = dates[dates.length - 1].trim();
                                serverLog.add(clientName + " request at " + LocalTime.now() + ": \"" + dates[0] + " " + dates[1] + "\"");
                                result = Time.passed(dates[0], dates[1]);
                                clientLogs.get(clientName).add("Request: " + dates[0] + " " + dates[1]);
                                clientLogs.get(clientName).add("Result:\n" + result);
//                                System.out.println("[SERVER]Message read from client: " + output);
//                                System.out.println("[SERVER]Result: " + result);
                                client.write(ByteBuffer.wrap(result.getBytes()));
//                                System.out.println("Wrote to client");
                            }
                            continue;
                        }
//                        }else if(ky.isWritable()){
//                            SocketChannel client = (SocketChannel)ky.channel();
//                            if(output.contains("login")){
//                                client.write(ByteBuffer.wrap("logged in".getBytes()));
//                                ky.interestOps(SelectionKey.OP_READ);
//                            }
//                            else if(output.contains("bye")){
//                                System.out.println("[SERVER] Bye! + Name:" + clientName);
////                                System.out.println("[SERVER} NULL?: " + clientLogs.get(clientName) + ", ClientName: " + clientName);
//                                serverLog.add(clientName + " logged out at " + LocalTime.now());
//                                client.write(ByteBuffer.wrap(clientLogs.get(clientName).toString().getBytes()));
//                                ky.interestOps(SelectionKey.OP_READ);
//                                client.close();
//                            }
//                            else {
//                                client.write(ByteBuffer.wrap(result.getBytes()));
//                                ky.interestOps(SelectionKey.OP_READ);
//                            }
//                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void stopServer() {
        running = false;
        server.shutdownNow();
    }

    public String getServerLog() {
        return serverLog.toString();
    }
}
