package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class GoSocket implements Runnable {
    protected Socket socket;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;
    public boolean exit = false;

    public GoSocket(Socket socket) throws IOException {
        this.socket = socket;
        new Thread(this).start();
    }

    public void send(Object message) {
        try { output.writeObject(message); output.flush(); }
        catch (Exception ex) { }
    }

    protected void oninit() {}

    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream()); output.flush();
            input  = new ObjectInputStream(socket.getInputStream());
            oninit();
            
            while (!exit && !socket.isClosed()) {
                try {
                    Object message = input.readObject();
                    if (message != null) {
                        if (message.equals("BYE")) close(false);
                        onMessage(message);
                    }
                }
                catch (ClassNotFoundException e) { e.printStackTrace(); }
                catch (IOException e) { }
            }
        }
        catch (IOException exc) { exc.printStackTrace(); }
    }

    public void close(boolean bye) {
        try {
            if (bye) send("BYE");
            input.close();
            output.close();
            socket.close();
        }
        catch (IOException exc) { exc.printStackTrace(); }
        //System.out.println("Socket closed.");
    }

    public void close() { close(true); }

    public abstract void onMessage(Object message);
}
