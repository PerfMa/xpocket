package com.perfma.xlab.xpocket.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class AsciiArtUtil {
    
    private static final int WEIGHT = 100;
    private static final int HEIGHT = 12;
    
    public static String text2AsciiArt(String text) {
        
        BufferedImage bufferedImage = new BufferedImage(
                WEIGHT, HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        graphics2D.drawString(text, 0, 11);
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WEIGHT; x++) {
                stringBuilder.append(bufferedImage.getRGB(x, y) == -16777216 ? " " : "*");
            }
            stringBuilder.append("\n");
        }
        
        return stringBuilder.toString();
    }
    
    public static void main(String[] args) {
        
        System.out.println(text2AsciiArt("A R T H A S"));
        
    }
    
}
