package network;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class GoSocket implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    public boolean exit = false;

    public GoSocket(Socket socket) throws IOException {
        this.socket = socket;
        new Thread(this).start();
    }

    public void send(Object message) {
        try { output.writeObject(message); output.flush(); }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("Socket listening...");
            while (!exit) {
                try { onMessage(input.readObject()); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        catch (EOFException exc) { }
        catch (IOException exc) { exc.printStackTrace(); }
        finally { close(); }
    }

    public void close() {
        try { socket.close(); }
        catch (IOException exc) {}
        System.out.println("Socket closed.");
    }

    public abstract void onMessage(Object message);
}
