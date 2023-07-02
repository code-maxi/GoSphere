package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import data.GoConfig;
import data.GoJoin;
import server.GoUser;

public class GoConsole implements Runnable {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String NAMEPATTERN = "\\w{3,20}";;

    private GoClient client = null;
    private HashMap<String, GoPromt> promts = new HashMap<String, GoPromt>();
    private String[] args;
    private BufferedReader reader;
    private String prmtstr;

    public GoConsole(String[] args) { this.args = args; }

    public void listen() { new Thread(this).start(); }

    public void sleep(int time) {
        try { Thread.sleep(time); }
        catch (InterruptedException ex) {}
    }
    
    public void run() {
        reader = new BufferedReader(new InputStreamReader(System.in));

        while (client == null) {
            try {
                String host = args.length > 0 ? args[0] : null;
                Object port = args.length > 1 ? Integer.parseInt(args[1]) : null;

                promts.put("server", new GoPromt("server", "Enter the server host you want to connect to.", GoPromt.STR, null, host));
                promts.put("port",   new GoPromt("port", "Enter the server port.", GoPromt.INT, null, port));
                GoPromt.promt(promts, reader, prmtstr);

                client = new GoClient(
                    (String) promts.get("server").value, 
                    (int) promts.get("port").value
                );
            }
            catch (IOException exc) {
                System.out.println("\n\nError: No connection to server!");
                client = null;
            }
        }

        System.out.println("Client connected to " + client.connection);
        prmtstr = client.connection;
        
        while (promts.get("cjgame") == null || promts.get("cjgame").value.equals("L")) {
            promts.put("cjgame", new GoPromt("cjgame", "Do you want to create <C> or join <J> a game or list all aviable games <L>?", GoPromt.STR, "[CJL]", args.length > 2 ? args[2] : null));
            GoPromt.promt(promts, reader, prmtstr);
            if (promts.get("cjgame").value.equals("L")) {
                client.send("LISTGAMES");
                sleep(500);
            }
        }

        while (client.state == null) {
            if (promts.get("cjgame").value.equals("C")) {
                promts.put("creator_name", new GoPromt("creator_name", "CREATE GAME: What's your name?", GoPromt.STR, NAMEPATTERN, args.length > 3 ? args[3] : null));
                promts.put("n",            new GoPromt("n", "CREATE GAME: Enter the size of the game (between 4 and 40, devidable by 4).", GoPromt.INT, null, args.length > 4 ? Integer.parseInt(args[4]) : null));
                promts.put("komi",         new GoPromt("komi", "CREATE GAME: Enter the komi.", GoPromt.DOUB, null, args.length > 5 ? Double.parseDouble(args[5]) : null));
                promts.put("first_color",  new GoPromt("first_color", "CREATE GAME: Which color do you want to play width (black <0>, white <1>)?", GoPromt.INT, "[01]", args.length > 6 ? Integer.parseInt(args[6]) : null));
                GoPromt.promt(promts, reader, prmtstr);

                GoConfig config = new GoConfig(
                    (double) promts.get("komi").value,
                    (int) promts.get("n").value, 
                    (int) promts.get("first_color").value, 0,
                    (String) promts.get("creator_name").value
                );
                
                client.send(config);
            }
            else {
                promts.put("name", new GoPromt("name", "JOIN GAME: What's your name?", GoPromt.STR, NAMEPATTERN, args.length > 3 ? args[3] : null));
                promts.put("id", new GoPromt("id", "JOIN GAME: Enter the id of your game.", GoPromt.INT, null, null));
                GoPromt.promt(promts, reader, prmtstr);

                GoJoin join = new GoJoin((String) promts.get("name").value, (int) promts.get("id").value);
                client.send(join);
            }
            sleep(500);
        }
    }

    public void close() {
        try { reader.close(); }
        catch(Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        new GoConsole(args).listen();
    }
}
