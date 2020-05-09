/**
 * @author Voiko Yehor S18917
 */

package zad4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Client {
    Selector selector;
    String id;
    boolean isFinished;
    SocketChannel client;
    InetSocketAddress hostAddress;
    private static final int BSIZE = 1024;

    public Client(String host, int port, String id) {
        hostAddress = new InetSocketAddress(host, port);
        this.id = id;
        isFinished = false;
    }

    public void connect() {
        try {
//            selector = Selector.open();
            client = SocketChannel.open(hostAddress);
            client.configureBlocking(false);
//            int clientOps = client.validOps();
//            client.register(selector, clientOps, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String send(String req) {
        String output = "";
            try {
                client.write(ByteBuffer.wrap((req + " " + id + "\r").getBytes()));
                output = get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return output;
    }


    private StringBuffer reqString = new StringBuffer();
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);
    private static Charset charset = Charset.forName("UTF-8");

    public String get() {
            reqString.setLength(0);
            bbuf.clear();
            try {
                readLoop:
                while (true) {
                    int n = client.read(bbuf);
                    if (n > 0) {
                        bbuf.flip();
                        CharBuffer cbuf = charset.decode(bbuf);
                        while (cbuf.hasRemaining()) {
                            char c = cbuf.get();
                            if (c == '\r') break readLoop;
                            reqString.append(c);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reqString.substring(0);
    }
}
