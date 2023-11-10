package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

import data.GoConfig;
import data.GoJoin;
import data.GoMove;
import data.GoVersion;
import network.GoSocket;

public class GoUser extends GoSocket {
    public GoServer server;
    public GoGame game;
    public String name = "UNNAMED";

    public GoUser(Socket socket, GoServer server) throws IOException {
        super(socket);
        this.server = server;
    }

    @Override
    public void oninit() { send("VRS"+GoVersion.version); }

    public void onMessage(Object message) {
        if (server.DEBUG) System.out.println("User " + name + " recieved " + message);
        boolean isConfig = message instanceof GoConfig;
        boolean isJoin = message instanceof GoJoin;
        if (isConfig || isJoin) {
            if (game == null) {
                if (isConfig) {
                    GoConfig config = (GoConfig) message;
                    String error = server.createNewGame(config, this);
                    if (error == null) {
                        send("INFThe game with ID [" + GoServer.FORMAT_ID(game.state.id) + "] was created.\nWaiting for other player...");
                    }
                    else send("ERR" + error);
                }
                if (isJoin) {
                    GoJoin join = (GoJoin) message;
                    String error = server.joinGame(join, this);
                    if (error == null) {
                        send("INFYou successfully joined the game. Have fun!");
                    }
                    else send("ERR" + error);
                }
            }
            else send("ERRYou are already in a game.");
        }
        if (message instanceof GoMove) {
            if (game != null) {
                GoMove move = (GoMove) message;
                String error = game.move(move);
                if (error != null) send("GUI" + error);
            }
            else send("ERRYou are not in a game yet.");
        }
        if (message instanceof String) {
            String sub = ((String) message).substring(0, 3);
            String con = ((String) message).substring(3);
            if (sub.equals("LST")) send("INF\n" + server.listGames());
            if (sub.equals("NXT")) {
                if (game != null) {
                    try { 
                        int me = Integer.parseInt(con);
                        String error = game.playerNext(me);
                        if (error != null) send("GUI" + error);
                    }
                    catch(NumberFormatException ex) { send("ERRCould not read id '"+con+"'."); }
                }
            }
            if (sub.equals("CHT")) {
                if (game != null) {
                    game.chatMessage(name+": "+con);
                }
            }
            if (sub.equals("BYE")) {
                if (game != null) game.closeUser(this);
            }
            if (sub.equals("UPG")) upgradeClient();
        }
    }

    public void upgradeClient() {
        System.out.println("Updating client...");
        try {
            byte[] content = Files.readAllBytes(GoVersion.jarFile);
            send(content);
        }
        catch (FileNotFoundException ex) { send("ERRCould not find jar file."); }
        catch (IOException ex) { send("ERRIOException while reading jar."); }
    }
}
