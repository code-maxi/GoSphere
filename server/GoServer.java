package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import data.GoConfig;
import data.GoJoin;
import data.GoVersion;

public class GoServer implements Runnable {
    public static final int MAX_ID = 9999;
    public static String FORMAT_ID(int id) { return String.format("%04d", id); }

    public final HashMap<Integer, GoGame> games = new HashMap<Integer, GoGame>();
    public final ServerSocket serverSocket;
    public final boolean DEBUG;
    public ArrayList<Integer> used_ids = new ArrayList<Integer>();
    public boolean exit = false;
    public int port;

    public GoServer(int port, boolean debug) throws IOException {
        this.port = port;
        this.DEBUG = debug;
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
            user.game = game;
            return game.addUser(user, -1);
        }
    }

    public String createNewGame(GoConfig conf, GoUser user) {
        int id = conf.preferred_id >= 0 ? conf.preferred_id : newID();
        System.out.println("preferred id " + conf.preferred_id + "  id "+id);
        boolean sphere_notok = conf.object == GoConfig.SPHERE_OBJECT && (conf.n < 4 || conf.n > 40 || conf.n % 4 != 0);
        boolean cube_notok   = conf.object == GoConfig.CUBE_OBJECT   && (conf.n < 3 || conf.n > 20);
        if (used_ids.contains(id)) return "The preferred ID " + id + " is in use.";
        else if (conf.object != GoConfig.CUBE_OBJECT && conf.object != GoConfig.SPHERE_OBJECT) return "The object must be either <0> or <1>.";
        else if (sphere_notok) return "The Size must be beween 0 and 40 and it must be divisible by 4.";
        else if (cube_notok)   return "The Size must be beween 3 and 20.";
        else if (conf.first_color > 1) return "The color must be either 0 (black) or 1 (white).";
        else {
            GoGame game = new GoGame(this, conf, id, DEBUG);
            user.name = conf.creator_name;
            user.game = game;
            game.addUser(user, conf.first_color);
            games.put(game.state.id, game);
            return null;
        }
    } 

    public void run() {
        System.out.println("GoServer (Version V"+GoVersion.version+") listening on port " + port + (DEBUG ? " in DEBUG mode" : "") + "...");
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
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 5555;
            boolean debug = args.length > 1 && args[1].equals("DEBUG");
            new GoServer(port, debug);
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }
}