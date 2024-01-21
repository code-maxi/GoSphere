package client;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.HashMap;

public class GoPromt {
    public static final int INT = 0;
    public static final int BOOL = 1;
    public static final int DOUB = 2;
    public static final int STR = 3;

    public final String id;
    public final String name;
    public final int type;
    public final String pattern;
    public Object value;

    public GoPromt(String id, String name, int type, String pattern, Object value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.pattern = pattern;
        this.value = value;
    }

    public String toString() {
        return "[" + id + "]  " + name;
    }

    public static void promt(HashMap<String, GoPromt> promts, BufferedReader reader, String prmtstr) {
        Collection<GoPromt> values = promts.values();

        for (GoPromt promt : values) {
            while (promt.value == null) {
                System.out.print("\n" + promt + "\n" + GoConsole.ANSI_GREEN + (prmtstr != null ? prmtstr : "") + " >>> "
                        + GoConsole.ANSI_RESET);
                try {
                    Object input = reader.readLine();
                    if (promt.type == INT)
                        input = Integer.parseInt((String) input);
                    if (promt.type == DOUB)
                        input = Double.parseDouble((String) input);
                    if (promt.type == BOOL)
                        input = Boolean.parseBoolean((String) input);

                    if (input != null && promt.pattern != null
                            && !input.toString().toUpperCase().matches(promt.pattern)) {
                        System.out.print("Error, the given input does not match.\n");
                        input = null;
                    }
                    promt.value = input;
                } catch (NumberFormatException ex) {
                    System.out.print("Error, could not parse number.\n");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
