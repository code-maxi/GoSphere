package server;

import java.io.IOException;
import java.net.Socket;

import data.GoConfig;
import data.GoJoin;
import data.GoMove;
import network.GoSocket;

public class GoUser extends GoSocket {
    public GoServer server;
    public GoGame game;
    public String name = "UNNAMED";

    public GoUser(Socket socket, GoServer server) throws IOException {
        super(socket);
        this.server = server;
    }

    public void onMessage(Object message) {
        System.out.println("User " + name + " recieved " + message);
        boolean isConfig = message instanceof GoConfig;
        boolean isJoin = message instanceof GoJoin;
        if (isConfig || isJoin) {
            if (game == null) {
                if (isConfig) {
                    GoConfig config = (GoConfig) message;
                    String error = server.createNewGame(config, this);
                    if (error == null) {
                        send("INF The game with ID [" + GoServer.FORMAT_ID(game.state.id) + "] was created.\nWaiting for other player...");
                    }
                    else send("ERR " + error);
                }
                if (isJoin) {
                    GoJoin join = (GoJoin) message;
                    String error = server.joinGame(join, this);
                    if (error == null) {
                        send("INFYou successfully joined the game. Have fun!");
                    }
                    else send("ERR " + error);
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
            String str = (String) message;
            if (str.equals("LISTGAMES")) send("INF\n" + server.listGames());
        }
    }
}
