package server;

import java.util.ArrayList;
import data.GoConfig;
import data.GoMove;
import data.GoPosAbstract;
import data.GoStateSphere;
import data.GoStateAbstract;
import data.GoStateCube;

public class GoGame {
    public static final int STATUS_DELAY = 1000;

    public GoStateAbstract state;
    public final boolean DEBUG;
    private final GoConfig conf;
    
    private final GoUser[] players = new GoUser[2];
    
    private final int[] captives = new int[2];
    private final int[] fieldPoints = new int[]{ 0, 0 };
    private final double[] pointsSum = new double[]{ 0, 0 };

    private final boolean[] players_next = new boolean[2];
    
    private final ArrayList<int[][]> history = new ArrayList<int[][]>();

    private final GoServer server;

    public GoGame(GoServer server, GoConfig conf, int id, boolean debug) {
        this.conf = conf;
        this.server = server;
        this.DEBUG = debug;
        this.state = (conf.object == GoConfig.CUBE_OBJECT) ? new GoStateCube(conf.n, id) : new GoStateSphere(conf.n, id);
        setStatus(GoStateAbstract.WAIT_STATUS);
    }

    public String addUser(GoUser user, int p) {
        int player = p == -1 ? (players[0] == null ? 0 : 1) : p;
        if (players[player] == null) {
            players[player] = user;
            if (DEBUG && players[0] != null && players[1] != null) {
                setStatus(GoStateAbstract.RUNNING_STATUS);
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
                GoStateAbstract s = state.copy(i, state.turn, state.status);
                players[i].send(s);
            }
        }
    }

    private String label_text(int player) {
        String nextText = "---";
        if (state.status == GoStateAbstract.WAIT_STATUS) nextText = "STARTS";
        if (state.status == GoStateAbstract.RUNNING_STATUS) nextText = "PASSED";
        if (state.status == GoStateAbstract.DELETE_STATUS) nextText = "FINISHED";
        if (state.status == GoStateAbstract.END_STATUS) nextText = "REMATCH";
        
        boolean end = state.status == GoStateAbstract.END_STATUS;
        String captivesStr = ""+captives[player];
        String komiStr     = player == 1 ? (" + " + conf.komi) : "";
        String fieldStr    = end ? (" + " + fieldPoints[player]) : "";
        String sumStr      = end ? (" = " + pointsSum[player]) : "";
        double pointsDifference = pointsSum[player] - pointsSum[(player+1)%2];

        String[] components = {
            players[player] != null ? players[player].name : "Waiting...",
            "[" + captivesStr + komiStr + fieldStr + sumStr + "]",
            players_next[player] ? "["+nextText+"]" : "",
            end && pointsDifference > 0 ? "–" : "",
            end && pointsDifference > 0 ? ("Won by " + pointsDifference + "!") : ""
        };

        String result = "";
        for (int i = 0; i < components.length; i ++) {
            result += components[player == 0 ? i : (components.length-1 - i)];
            if (i < components.length-1) result += " ";
        }
        return result;
    }

    public void changeTurns() {
        state = state.copy(state.me, (state.turn + 1) % 2, state.status);
    }

    public String playerNext(int me) {
        if (me == 0 || me == 1) {
            if (me == state.turn || state.turn == -1) {
                players_next[me] = !players_next[me];
                if (players_next[0] && players_next[1]) {
                    if (!DEBUG) {
                        sendState();
                        try { Thread.sleep(STATUS_DELAY); }
                        catch (InterruptedException ex) {}
                    }
                    setStatus((state.status + 1) % (GoStateAbstract.END_STATUS + 1));
                }
                else if (state.status == GoStateAbstract.RUNNING_STATUS) {
                    changeTurns();
                }
                sendState();
                return null;
            }
            else return "It's not your turn.";
        }
        else return "The user id " + me + " is not valid.";
    }

    public void closeUser(GoUser user) {
        GoUser other = null;
        if (players[0] == user) { players[0] = null; other = players[1]; }
        if (players[1] == user) { players[1] = null; other = players[0]; }
        setStatus(GoStateAbstract.WAIT_STATUS);
        sendState();
        if (other != null) other.send("GUISorry, your opponent left the game. That's why the game had to be restarted.");
        if (players[0] == null && players[1] == null) {
            server.games.remove(state.id);
            System.out.println("Go Game " + state.id + " was closed.");
        }
    }

    public void setStatus(int status) {
        if (status != state.status) {
            System.out.println("Setting status from " + state.status + " to " + status + ".");
            state = state.copy(-1, -1, status);
            players_next[0] = false;
            players_next[1] = false;

            if (state.status == GoStateAbstract.WAIT_STATUS) {
                state.clearArray(true, true);

                captives[0] = 0; captives[1] = 0;
                history.clear();
            }
            if (state.status == GoStateAbstract.RUNNING_STATUS) {
                state = state.copy(-1, 0, state.status);
            }
            if (state.status == GoStateAbstract.DELETE_STATUS) {
                state.clearArray(false, true);
                //if (DEBUG) setStatus(GoStateAbstract.END_STATUS);
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
                state.clearArray(false, true);
                endGame();
            }
            System.out.println("STATUS SET TO " + status);
        }
    }

    private ArrayList<GoStoneGroup> fillRanges(int color) {
        ArrayList<GoStoneGroup> groups = new ArrayList<GoStoneGroup>();
        ArrayList<GoPosAbstract> usedPositions = new ArrayList<GoPosAbstract>();

        for (int y = 0; y < state.stones.length; y ++) {
            for (int x = 0; x < state.stones[y].length; x ++) {
                GoPosAbstract pos = state.posOnMe(x, y);
                if (!usedPositions.contains(pos) && pos.s == 0) {
                    GoStoneGroup group = new GoStoneGroup().fill(pos, state, new int[] { 0, enemy(color) });
                    for (GoPosAbstract stone : group.stones) usedPositions.add(stone);
                    if (!group.containColor(enemy(color))) {
                        groups.add(group);
                    }
                }
            }
        }
        return groups;
    }

    public void endGame() {
        fieldPoints[0] = 0; fieldPoints[1] = 0;

        ArrayList<ArrayList<GoStoneGroup>> rangesColors = new ArrayList<ArrayList<GoStoneGroup>>();
        rangesColors.add(fillRanges(1));
        rangesColors.add(fillRanges(2));

        for (int i = 0; i < 2; i ++) {
            for (GoStoneGroup group : rangesColors.get(i)) {
                for (GoPosAbstract pos : group.stones) {
                    state.colors[pos.y][pos.x] = (i == 0 ? GoStateAbstract.BLACK : GoStateAbstract.WHITE);
                    fieldPoints[i] += 1;
                }
            }
            pointsSum[i] = captives[i] + (i == 1 ? conf.komi : 0) + fieldPoints[i];
        }
    }

    public int enemy(int stone) { return stone == 1 ? 2 : (stone == 2 ? 1 : -1); }

    public String move(GoMove move) {
        if (state.stones.length > move.pos.y && state.stones[move.pos.y].length > move.pos.x) {
            GoPosAbstract pos = move.pos.changeStone(state.stones[move.pos.y][move.pos.x]);
            
            /*if (players_next[enemy(state.turn+1)-1]) {
                state.clearArray(false, true);
                GoStoneGroup group = new GoStoneGroup().fill(pos, state.stones, new int[] { 0, enemy(state.turn + 1) });
                System.out.println("Filling Group at " + pos + " on colors 0 and " + enemy(state.turn + 1));
                for (GoPosAbstract gpos : group.stones) {
                    System.out.println(gpos);
                    state.colors[gpos.y][gpos.x] = (state.turn == 0 ? GoStateAbstract.BLACK : GoStateAbstract.WHITE);
                }
                System.out.println("INCLUDES: " + group.containColor(enemy(state.turn+1)));
                changeTurns();
            }*/

            if (state.status == GoStateAbstract.RUNNING_STATUS) {
                if (move.me != state.turn) return "MOVEERROR: It is not your turn!";
                if (state.stones[pos.y][pos.x] != 0) return "MOVEERROR: You can not put a stone on another one.";
                
                boolean MARKSTONES = DEBUG;
                state.clearArray(false, true);
            
                int[][] last_state = state.cloneArray(state.stones);
                history.add(last_state);

                state.stones[pos.y][pos.x] = state.turn + 1;
                state.colors[pos.y][pos.x] = state.turn == 0 ? GoStateAbstract.WHITE : GoStateAbstract.BLACK;

                pos = pos.changeStone(state.stones[pos.y][pos.x]);

                ArrayList<GoPosAbstract> neighbours = pos.neighbours(state);
                if (MARKSTONES) {
                    for (GoPosAbstract stone : neighbours) state.colors[stone.y][stone.x] = GoStateAbstract.GREEN;
                }

                for (GoPosAbstract neighbour : neighbours) {
                    if (pos.s == enemy(neighbour.s)) {
                        GoStoneGroup group = new GoStoneGroup().fill(neighbour, state);
                        if (MARKSTONES) {
                            for (GoPosAbstract stone : group.stones) state.colors[stone.y][stone.x] = GoStateAbstract.RED;
                            for (GoPosAbstract stone : group.liberties) state.colors[stone.y][stone.x] = GoStateAbstract.BLUE;
                        }

                        if (group.liberties.size() == 0) {
                            for (GoPosAbstract stone : group.stones) {
                                state.stones[stone.y][stone.x] = 0;
                                captives[pos.s - 1] += 1;
                            }
                        }
                    }
                }

                GoStoneGroup currentGroup = new GoStoneGroup().fill(pos, state);
                if (currentGroup.liberties.size() == 0){
                    state.updateArray(last_state);
                    return "MOVEERROR: Suicide is not allowed!";
                }
                else if (history.size() >= 2) {
                    int[][] ko_state = history.get(history.size()-2);
                    //System.out.println("–––\n" + state.stringArray(ko_state) + "\n"+state+"\n\n");
                    if (state.equalsArray(ko_state)) {
                        state.updateArray(last_state);
                        history.remove(last_state);
                        return "MOVEERROR: Its Ko, you fool!";
                    };
                }
                players_next[0] = false; players_next[1] = false;
                changeTurns();
            }
            else if (state.status == GoStateAbstract.DELETE_STATUS) {
                if (pos.s == 1 || pos.s == 2) {
                    if (state.colors[pos.y][pos.x] == GoStateAbstract.RED) {
                        state.colors[pos.y][pos.x] = 0;
                    }
                    else state.colors[pos.y][pos.x] = GoStateAbstract.RED;
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
        String type = conf.object == GoConfig.CUBE_OBJECT ? "CUBE" : "SPHERE";
        return " Game ["+ GoServer.FORMAT_ID(state.id) + "], <"+type+">, players: " + names[0] + " (black) : " + names[1] + " (white)";
    }
}
