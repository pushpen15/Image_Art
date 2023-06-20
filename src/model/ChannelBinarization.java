package model;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ChannelBinarization {
    
    private BufferedImage image;

    public ChannelBinarization(BufferedImage image) {
        this.image = image;
    }
    
    public BufferedImage apply(int thresholdRed, int thresholdGreen,int thresholdBlue){
        
        int height = image.getHeight();
        int width = image.getWidth();
        
        int red, green, blue;
        
        Color [][] colors = new Color[height][width];
        
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int srcPixel = image.getRGB(i, j);
                Color c = new Color(srcPixel);
                red = c.getRed();
                green = c.getGreen();
                blue = c.getBlue();
                
                if(red > thresholdRed){
                    red = 0;
                }else{
                    red = 255;
                }
                
                if(green > thresholdGreen){
                    green = 0;
                }else{
                    green = 255;
                }
                
                if(blue > thresholdBlue){
                    blue = 0;
                }else{
                    blue = 255;
                }
                
                colors[j][i] = new Color(red,green,blue,255);
            }
        }
        
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newImage.setRGB(j, i, colors[i][j].getRGB());
            }
        }
        
        return newImage;
        
    }
    
}
