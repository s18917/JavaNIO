/**
 * @author Voiko Yehor S18917
 */

package zad4;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask extends Thread {
    static HashMap<Client, String> logs = new HashMap<>();
    final Client c;
    ClientTask(Runnable r, Client c) {
        super(r);
        this.c = c;
    }

    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes) {
        return new ClientTask(() -> {
                c.connect();
                if (showSendRes) {
                    System.out.println(c.send("login"));
                    reqs.forEach(e-> System.out.println(c.send(e)));
                    String msg;
                    System.out.println(msg = c.send("bye and log transfer"));
                    logs.put(c, msg);
                    synchronized (c) {
                        c.isFinished = true;
                        c.notify();
                    }
                } else {
                    c.send("login");
                    reqs.forEach(c::send);
                    logs.put(c, c.send("bye and log transfer"));
                    synchronized (c) {
                        c.isFinished = true;
                        c.notify();
                    }
                }
        }, c);
    }

    public String get() throws InterruptedException, ExecutionException {
        synchronized (c) {
            while (!c.isFinished)
                c.wait();
        }
        return logs.get(this.c);
    }
}

//                    for (int i = 0; i < reqs.size(); i++) {
//                        try {
//                            Thread.sleep(200);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        logs.put(c, logs.get(c) + c.send(reqs.get(i)) + "\n");
//                        if(i==reqs.size()-1){
//                            c.isFinished = true;
//                            c.notify();
//                        }
//                    }

