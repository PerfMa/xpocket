package com.perfma.xlab.xpocket.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class AsciiArtUtil {

    private static final Map<String, char[][]> dict = new HashMap<>();

    //init asciiart dict
    static {
        try {
            InputStream is = AsciiArtUtil.class.getClassLoader().getResourceAsStream("asciiart/asciiartdb");

            char[][] ttf = new char[8][];
            String[] index = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                String indexLine = reader.readLine();
                index = indexLine.split(" ");
                for (int i = 0; i < 8; i++) {
                    ttf[i] = reader.readLine().toCharArray();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            int indexPos = 0;
            int typePos = 0;

            for (;;) {
                int startPos = typePos;

                for (;;) {
                    if (' ' == ttf[0][typePos]
                            && ' ' == ttf[1][typePos]
                            && ' ' == ttf[2][typePos]
                            && ' ' == ttf[3][typePos]
                            && ' ' == ttf[4][typePos]
                            && ' ' == ttf[5][typePos]
                            && ' ' == ttf[6][typePos]
                            && ' ' == ttf[7][typePos]) {

                        int length = typePos - startPos;
                        char[][] type = new char[8][length];
                        dict.put(index[indexPos], type);
                        for (int i = 0; i < 8; i++) {
                            System.arraycopy(ttf[i], startPos, type[i], 0, length);
                        }
                        typePos++;
                        indexPos++;
                        break;
                    }
                    typePos++;
                }

                if (indexPos >= index.length) {
                    break;
                }
            }
            char[][] space = new char[][]{{' '}, {' '}, {' '}, {' '}, {' '}, {' '}, {' '}, {' '}};
            dict.put(" ", space);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static String text2AsciiArt(String text) {
        text = text.startsWith(" ") ? text : (" " + text);
        char[] input = text.toCharArray();
        char[][][] inputMatrix = new char[input.length][][];

        int i = 0;
        for (char inputc : input) {
            String key = String.valueOf(inputc);
            inputMatrix[i++] = dict.containsKey(key) 
                    ? dict.get(String.valueOf(inputc)) 
                    : dict.get("?");
        }

        //precompute the length of final 
        StringBuilder stringBuilder = new StringBuilder(8 * inputMatrix.length * 6);

        for (int k = 0; k < 8; k++) {
            for (char[][] inputLine : inputMatrix) {
                stringBuilder.append(inputLine[k]);
            }
            stringBuilder.append(TerminalUtil.lineSeparator);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println(AsciiArtUtil.text2AsciiArt("A s y c - S t a c k"));
    }

}
