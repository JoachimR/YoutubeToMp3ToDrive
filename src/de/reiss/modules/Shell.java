package de.reiss.modules;

import de.reiss.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Shell {

    public static String executeCommand(String command, boolean silent) {
        StringBuilder stringBuilder = new StringBuilder();
        print("executing :" + command, silent);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                print(line, silent);
                stringBuilder.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        print("finished with executing :" + command, silent);
        return stringBuilder.toString();
    }

    private static void print(String s, boolean silent) {
        if (!silent) {
            Logger.log(s);
        }
    }

}
