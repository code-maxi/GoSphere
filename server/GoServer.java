package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import data.GoConfig;
import data.GoJoin;

public class GoServer implements Runnable {
    public static final int MAX_ID = 9999;
    public static String FORMAT_ID(int id) { return String.format("%04d", id); }

    public final HashMap<Integer, GoGame> games = new HashMap<Integer, GoGame>();
    public ArrayList<Integer> used_ids = new ArrayList<Integer>();
    public final ServerSocket serverSocket;
    public boolean exit = false;
    public int port;

    public GoServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        new Thread(this).start();
    }

    public int newID() {
        int id = -1;
        while (id < 0 || used_ids.contains(id))
            id = (int)(Math.random() * MAX_ID);
        return id;
    }

    public String joinGame(GoJoin join, GoUser user) {
        GoGame game = games.get(join.id);
        if (game == null) return "The game with ID [" + FORMAT_ID(join.id) + "] does not exist.";
        else {
            user.name = join.name;
            return game.addUser(user, -1);
        }
    }

    public String createNewGame(GoConfig conf, GoUser user) {
        if (conf.n < 0 || conf.n > 40 || conf.n % 4 != 0) return "The Size must be beween 0 and 40 and it must be divisible by 4.";
        else if (conf.first_color > 1) return "The color must be either 0 (black) or 1 (white).";
        else {
            GoGame game = new GoGame(conf, newID());
            user.name = conf.creator_name;
            user.game = game;
            game.addUser(user, conf.first_color);
            games.put(game.state.id, game);
            return null;
        }
    } 

    public void run() {
        System.out.println("Server listening on port " + port + "...");
        while (!exit) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("GoUser accepted.");
                new GoUser(socket, this);
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public String listGames() {
        String res = games.size() == 0 ? "There are no games yet." : "";
        for (GoGame g : games.values()) res += g + "\n";
        return res;
    }

    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 1234;
            new GoServer(port);
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }
}