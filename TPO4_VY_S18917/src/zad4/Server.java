/**
 * @author Voiko Yehor S18917
 */

package zad4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
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

    private static final int BSIZE = 1024;

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
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            InetSocketAddress hostaddress = new InetSocketAddress(host, port);
            serverSocket.socket().bind(hostaddress);

            selector = Selector.open();
//            int ops = serverSocket.validOps();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            running = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        server = Executors.newSingleThreadExecutor();
        server.execute(() -> {
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
                            client.register(selector, SelectionKey.OP_READ);
                            continue;
                        }
                        if(ky.isReadable()){
//                            System.out.println("READ SMTH");
                            SocketChannel client = (SocketChannel)ky.channel();
                            serviceRequest(client);
                            continue;
                        }
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

    private StringBuffer reqString = new StringBuffer();
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);
    private static Charset charset  = Charset.forName("UTF-8");

    private void serviceRequest(SocketChannel sc) {
//        System.out.println("Im here!");
//        String reqString = "";
        reqString.setLength(0);
        bbuf.clear();
        try {
            readLoop:
            // Czytanie jest nieblokujące
            while (true) {               // kontynujemy je dopóki
                int n = sc.read(bbuf);     // nie natrafimy na koniec wiersza
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = charset.decode(bbuf);
                    while (cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        reqString.append(c);
                    }
                }
            }

//        try {
//            sc.read(bbuf);
//            reqString = new String(bbuf.array());
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
        String req = reqString.substring(0);
            String[] reqArr = req.split(" ");
            String clientName = reqArr[reqArr.length-1].trim();
//            System.out.println("[SERVER]CLIENTNAME: " + clientName);
//                System.out.println("[SERVER] New output: " + req);
                if (req.contains("login")) {
                    ArrayList<String> tmp = new ArrayList<String>() {
                        @Override
                        public String toString() {
                            StringBuilder sb = new StringBuilder();
                            forEach(e -> sb.append(e).append("\n"));
                            return sb.toString();
                        }
                    };
                    tmp.add("=== " + clientName + " log start ===");
                    tmp.add("logged in");
//                                System.out.println(output.split(" ")[1].trim() + " logged in at " + LocalTime.now());
                    clientLogs.put(clientName, tmp);
                    serverLog.add(clientName + " logged in at " + LocalTime.now());
                    sc.write(ByteBuffer.wrap("logged in \r".getBytes()));
                } else if (req.contains("bye")) {
                    clientLogs.get(clientName).add("logged out");
                    clientLogs.get(clientName).add("=== " + clientName + " log end ===");
//                                System.out.println("[SERVER] Bye! + Name:" + clientName);
//                                System.out.println("[SERVER} NULL?: " + clientLogs.get(clientName) + ", ClientName: " + clientName);
                    serverLog.add(clientName + " logged out at " + LocalTime.now());
                    sc.write(ByteBuffer.wrap((clientLogs.get(clientName)+"\r").toString().getBytes()));
                    sc.close();
                    sc.socket().close();
//                                System.out.println("The Client messages are complete; close the session.");
                } else {
                    serverLog.add(clientName + " request at " + LocalTime.now() + ": \"" + reqArr[0] + " " + reqArr[1] + "\"");
                    String result = Time.passed(reqArr[0], reqArr[1]);
                    clientLogs.get(clientName).add("Request: " + reqArr[0] + " " + reqArr[1]);
                    clientLogs.get(clientName).add("Result:\n" + result);
//                                System.out.println("[SERVER]Message read from client: " + output);
//                                System.out.println("[SERVER]Result: " + result);
                    sc.write(ByteBuffer.wrap((result + "\r").getBytes()));
//                                System.out.println("Wrote to client");
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
