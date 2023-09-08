java.lang.ArrayIndexOutOfBoundsException: Index 18 out of bounds for length 1
        at server.GoGame.move(GoGame.java:67)
        at server.GoUser.onMessage(GoUser.java:49)
        at network.GoSocket.run(GoSocket.java:35)
        at java.base/java.lang.Thread.run(Thread.java:833)

private ArrayList<GoStoneGroup> fillRanges(int color) {
        ArrayList<GoStoneGroup> groups = new ArrayList<GoStoneGroup>();
        ArrayList<GoPos> usedPositions = new ArrayList<GoPos>();
        for (int y = 0; y < state.stones.length; y ++) {
            for (int x = 0; x < state.stones[y].length; x ++) {
                GoPos pos = new GoPos(x, y, state.stones);
                if (!usedPositions.contains(pos)) {
                    GoStoneGroup group = new GoStoneGroup().fillNear(pos, state.stones, new int[] { 0, enemy(color) });
                    for (GoPos stone : group.stones) usedPositions.add(stone);
                    if (!group.containColor(enemy(color))) {
                        groups.add(group);
                    }
                }
            }
        }
        return groups;
    }