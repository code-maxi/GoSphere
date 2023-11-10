package client;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

import data.*;
import network.GoSocket;

public class GoClient extends GoSocket {
    public boolean exit = false;
    public String connection = "";
    public int serverVersion = -1;
    private GoViewer viewer;
    public GoStateAbstract state;

    public GoClient(String server, int port) throws IOException {
        super(new Socket(server, port));
        connection = server + ":" + port;
    }

    public void onMessage(Object message) {
        //System.out.println("Client recieved " + message + "\n");
        if (message instanceof String) {
            String sub = ((String) message).substring(0, 3);
            String con = ((String) message).substring(3);
            if (sub.equals("ERR")) System.out.println(GoConsole.ANSI_RED + "\nSERVER ERROR: " + con + GoConsole.ANSI_RESET);
            if (sub.equals("INF")) System.out.println(GoConsole.ANSI_BLUE + "\nSERVER INFO: " + con + GoConsole.ANSI_RESET);
            if (sub.equals("GUI") && viewer != null) viewer.labels.showError(con); 
            if (sub.equals("CHT") && viewer != null) viewer.canvas.chatMessage(con);
            if (sub.equals("VRS")) { serverVersion = Integer.parseInt(con); }
        }
        if (message instanceof GoStateAbstract) {
            state = (GoStateAbstract) message;
            if (viewer == null) viewer = new GoViewer(state, this);
            else viewer.setState(state);
        }
        if (message instanceof byte[]) {
            try {
                Files.write(GoVersion.jarFile, (byte[])message);
                System.out.println("Go3D has been ugraded now. You can restart the program.");
                close();
                System.exit(0);
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
    }
}