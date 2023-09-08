package server;

import java.util.ArrayList;
import data.GoConfig;
import data.GoMove;
import data.GoPos;
import data.GoPosAbstract;
import data.GoState;
import data.GoStateAbstract;

public class GoGame {
    public GoStateAbstract state;
    private final GoConfig conf;
    
    private final GoUser[] players = new GoUser[2];
    private final int[] captives = new int[2];
    private final int[] ranges = new int[2];
    private final boolean[] players_next = new boolean[2];
    
    private final ArrayList<int[][]> history = new ArrayList<int[][]>();

    public GoGame(GoConfig conf, int id) {
        this.conf = conf;
        this.state = new GoState(conf.n, id);
    }

    public String addUser(GoUser user, int p) {
        int player = p == -1 ? (players[0] == null ? 0 : 1) : p;
        if (players[player] == null) {
            players[player] = user;
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
                GoStateAbstract s = state.copy(i, state.turn, state.status);
                players[i].send(s);
            }
        }
    }

    private String label_text(int player) {
        String next_text = "---";
        if (state.status == GoStateAbstract.WAIT_STATUS) next_text = "STARTS";
        if (state.status == GoStateAbstract.RUNNING_STATUS) next_text = "PASSED";
        if (state.status == GoStateAbstract.DELETE_STATUS) next_text = "FINISHED";
        if (state.status == GoStateAbstract.END_STATUS) next_text = "REMATCH";
        String[] components = {
            players[player] != null ? players[player].name : "Waiting...",
            "[" + captives[player] + (player == 1 ? (" + " + conf.komi + "]") : "]"),
            players_next[player] ? next_text : ""
        };
        String result = "";
        for (int i = 0; i < components.length; i ++) {
            result += components[player == 0 ? i : (components.length-1 - i)];
            if (i < components.length-1) result += "  ";
        }
        return result;
    }

    public void changeTurns() {
        state = state.copy(state.me, (state.turn + 1) % 2, state.status);
    }

    public String playerNext(int me) {
        if (me == 0 || me == 1) {
            players_next[me] = !players_next[me];
            if (players_next[0] && players_next[1]) {
                int ns = (state.status + 1) % (GoStateAbstract.END_STATUS + 1);
                System.out.println("Status changes from " + state.status + " to " + ns + ".");
                setStatus(ns);
            }
            else if (state.status == GoStateAbstract.RUNNING_STATUS) {
                changeTurns();
            }
            sendState();
            return null;
        }
        else return "The user id " + me + " is not valid.";
    }

    public void setStatus(int status) {
        state = state.copy(-1, -1, status);
        state.clearArray(false, true);
        if (state.status == GoStateAbstract.WAIT_STATUS) {
            state.clearArray(true, false);
        }
        if (state.status == GoStateAbstract.RUNNING_STATUS) {
            state = state.copy(-1, 0, state.status);
        }
        if (state.status == GoStateAbstract.END_STATUS) {
            // remove all red marked stones
            for (int y = 0; y < state.stones.length; y ++) {
                for (int x = 0; x < state.stones[y].length; x ++) {
                    if (state.colors[y][x] == GoStateAbstract.RED) {
                        state.stones[y][x] = 0;
                    }
                }
            }
        }
        System.out.println("STATUS SET TO " + status);
    }

    public int enemy(int stone) { return stone == 1 ? 2 : 1; }

    public String move(GoMove move) {
        state.clearArray(false, true);
    
        if (state.turn == -1) return "MOVEERROR: The game did not begin yet!";
    
        if (state.stones.length > move.pos.y && state.stones[move.pos.y].length > move.pos.x) {
            if (state.status == GoStateAbstract.RUNNING_STATUS) {
                if (move.me != state.turn) return "MOVEERROR: It is not your turn!";
            
                int[][] last_state = state.cloneArray(state.stones);
                history.add(last_state);

                state.stones[move.pos.y][move.pos.x] = state.turn + 1;

                ArrayList<GoPosAbstract> neighbours = move.pos.neighbours(state.stones);
                for (GoPosAbstract stone : neighbours) state.colors[stone.y][stone.x] = GoState.GREEN;

                for (GoPosAbstract neighbour : neighbours) {
                    if (neighbour.s == enemy(move.pos.s)) {
                        GoStoneGroup group = new GoStoneGroup().fillNear(neighbour, state.stones);
                        for (GoPosAbstract stone : group.stones) state.colors[stone.y][stone.x] = GoState.RED;
                        for (GoPosAbstract stone : group.liberties) state.colors[stone.y][stone.x] = GoState.BLUE;

                        if (group.liberties.size() == 0) {
                            for (GoPosAbstract stone : group.stones) {
                                state.stones[stone.y][stone.x] = 0;
                                captives[move.pos.s - 1] += 1;
                            }
                        }
                    }
                }

                GoStoneGroup currentGroup = new GoStoneGroup().fillNear(move.pos, state.stones);
                if (currentGroup.liberties.size() == 0){
                    return "MOVEERROR: Your do not have any liberties after this move.";
                }
                else if (history.size() >= 2) {
                    int[][] ko_state = history.get(history.size()-2);
                    System.out.println("–––\n" + state.stringArray(ko_state) + "\n"+state+"\n\n");
                    if (state.equalsArray(ko_state)) {
                        state.updateArray(last_state);
                        history.remove(last_state);
                        return "MOVEERROR: Its Ko!";
                    };
                }
                changeTurns();
            }

            else if (state.status == GoStateAbstract.DELETE_STATUS) {
                if (move.pos.s == 1 || move.pos.s == 2) {
                    if (state.colors[move.pos.y][move.pos.x] == GoStateAbstract.RED) {
                        state.colors[move.pos.y][move.pos.x] = 0;
                    }
                    else state.colors[move.pos.y][move.pos.x] = GoStateAbstract.RED;
                }
                else return "MOVEERROR: You can only mark stones.";
            }

            sendState();
            return null;
        }
        else return "MOVEERROR: The move "+move+" is out of area! " + state.stones.length + " – " + state.stones[move.pos.y].length;
    }

    public String toString() {
        String[] names = { 
            players[0] != null ? players[0].name : "[-]", 
            players[1] != null ? players[1].name : "[-]"
        };
        return "Game ["+ GoServer.FORMAT_ID(state.id) + "], players: " + names[0] + " (black) : " + names[1] + " (white)";
    }
}
