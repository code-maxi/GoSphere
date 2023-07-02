package client;
import java.io.IOException;
import java.net.Socket;
import data.*;
import network.GoSocket;

public class GoClient extends GoSocket {
    public boolean exit = false;
    public String connection = "";
    private GoViewer viewer;
    public GoState state;

    public GoClient(String server, int port) throws IOException {
        super(new Socket(server, port));
        connection = server + ":" + port;
    }

    public void onMessage(Object message) {
        if (message instanceof String) {
            String sub = ((String) message).substring(0, 3);
            String con = ((String) message).substring(3);
            if (sub.equals("ERR")) System.out.println(GoConsole.ANSI_RED + "\nSERVER ERROR: " + con + GoConsole.ANSI_RESET);
            if (sub.equals("INF")) System.out.println(GoConsole.ANSI_BLUE + "\nSERVER INFO: " + con + GoConsole.ANSI_RESET);
        }
        if (message instanceof GoState) {
            state = (GoState) message;
            System.out.println("\nGoState recieved:\n" + state);
            if (viewer == null) viewer = new GoViewer(state, this);
            else viewer.setState(state);
        }
    }

    public static void main(String[] args) {
        GoState state = new GoState(20, 2345);
        for (int i = 0; i < state.stones.length; i ++) {
            for (int j = 0; j < state.stones[i].length; j ++) {
                int stone = (int)(Math.random() > 0.8 ? (Math.random() * 2d + 1) : 0);
                state.stones[i][j] = stone;
            }
        }
        state.labels[0] = "Erster Satz";
        state.labels[1] = "Zweiter Satz";
        state.turn = 1;
        System.out.println("State____\n" + state);
        new GoViewer(state, null);
    }
}