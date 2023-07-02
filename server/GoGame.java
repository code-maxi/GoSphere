package server;

import data.GoConfig;
import data.GoMove;
import data.GoPos;
import data.GoState;

public class GoGame {
    public final GoState state;
    
    private final GoConfig conf;
    
    private final GoUser[] players = new GoUser[2];
    private final int[] stones_caught = new int[2];
    private final boolean[] players_passed = new boolean[2];

    public GoGame(GoConfig conf, int id) {
        this.conf = conf;
        this.state = new GoState(conf.n, id);
    }

    public String addUser(GoUser user, int p) {
        int player = p == -1 ? (players[0] == null ? 0 : 1) : p;
        if (players[player] == null) {
            players[player] = user;
            if (players[0] != null && players[1] != null) {
                state.turn = 0;
                System.out.print("GAME STARTS!");
            }
            sendState();
            return null;
        }
        else return "The game is already occupied.";
    }

    private void sendState() {
        state.labels[0] = label_text(0);
        state.labels[1] = label_text(1);
        for (int i = 0; i < players.length; i ++) {
            if (players[i] != null) {
                GoState s = state.copy(i);
                players[i].send(s);
            }
        }
    }

    private String label_text(int player) {
        String[] components = {
            players[player] != null ? players[player].name : "Waiting...",
            "[" + stones_caught[player] + (player == 1 ? (" + " + conf.komi + "]") : "]"),
            players_passed[player] ? "PASSED" : ""
        };
        String result = "";
        for (int i = 0; i < components.length; i ++) {
            result += components[player == 0 ? i : (components.length-1 - i)];
            if (i < components.length-1) result += "  ";
        }
        return result;
    }

    public String move(GoMove move) {
        if (state.turn == -1) return "MOVEERROR: The game has not begun yet!";
        if (move.me != state.turn) return "MOVEERROR: It's not your turn!";
        if (move.action == GoMove.POS) {
            if (move.pos != null && state.stones.length > move.pos.y && state.stones[move.pos.y].length > move.pos.x) {
                state.stones[move.pos.y][move.pos.x] = state.turn + 1;
            }
            else return "MOVEERROR: The move "+move+" is out of area! " + state.stones.length + " – " + state.stones[move.pos.y].length;
        }
        if (move.action == GoMove.PASS) {
            players_passed[move.me] = true;
            players_passed[(move.me+1)%2] = false;
        }
        state.turn = (state.turn + 1) % 2;
        sendState();
        return null;
    }

    public String toString() {
        String[] names = { 
            players[0] != null ? players[0].name : "-", 
            players[1] != null ? players[1].name : "-"
        };
        return "Game ["+ GoServer.FORMAT_ID(state.id) + "], players: " + names[0] + " (black) : " + names[1] + " (white)";
    }
}
