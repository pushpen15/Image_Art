package model;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class LocalBinarization {
    
    private BufferedImage image;

    public LocalBinarization(BufferedImage image) {
        this.image = image;
    }
    
    public BufferedImage apply(int threshold1, int threshold2, int threshold3, int threshold4){
        
        int height = image.getHeight();
        int width = image.getWidth();
        // height  width threshold
        
        int red, green, blue;
        
        Color [][] colors = new Color[height][width];
        
        int threshold;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int srcPixel = image.getRGB(i, j);
                Color c = new Color(srcPixel);
                red = c.getRed();
                green = c.getGreen();
                blue = c.getBlue();
                
                if(i < halfWidth && j < halfHeight){
                    threshold = threshold1;
                }
                else if(i >= halfWidth && j < halfHeight){
                    threshold = threshold2;
                }
                else if(i < halfWidth && j >= halfHeight){
                    threshold = threshold3;
                }
                else{
                    threshold = threshold4;
                }
                
                
                if(red > threshold){
                    red = 0;
                }else{
                    red = 255;
                }
                
                if(green > threshold){
                    green = 0;
                }else{
                    green = 255;
                }
                
                if(blue > threshold){
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
