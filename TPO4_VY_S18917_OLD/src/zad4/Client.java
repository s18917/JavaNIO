/**
 * @author Voiko Yehor S18917
 */

package zad4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Client {
    SelectionKey sk;
    Selector selector;
    String id;
    boolean isFinished;
    SocketChannel client;
    InetSocketAddress hostAddress;

    public Client(String host, int port, String id) {
        hostAddress = new InetSocketAddress(host, port);
        this.id = id;
        isFinished = false;
    }

    public void connect() {
        try {
            selector = Selector.open();
            client = SocketChannel.open(hostAddress);
            client.configureBlocking(false);
            int clientOps = client.validOps();
            client.register(selector, clientOps-, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String send(String req){
        String output ="";
        try {
            System.out.println("REQ IS: " + req);
            selector.select();
            Set selectedKeys = selector.selectedKeys();
            Iterator itr = selectedKeys.iterator();
            while (itr.hasNext()) {
                SelectionKey ky = (SelectionKey) itr.next();
                if (ky.isReadable()) {
//                    SocketChannel client = (SocketChannel) ky.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    client.read(buffer);
                    output = new String(buffer.array()).trim();
                    System.out.println(output);
                    continue;
                }
                if (ky.isWritable()) {
                    client.write(ByteBuffer.wrap((req + " " + id).getBytes()));
                    ky.interestOps(SelectionKey.OP_READ);
                    continue;
                }
                itr.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
