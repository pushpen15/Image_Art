package model;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class CMYK {
    
    private BufferedImage image;

    public CMYK(BufferedImage image) { 
        this.image = image;
    }
    
    public BufferedImage apply(){
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Color color;
        
        double rFirst;
        double gFirst;
        double bFirst;
        
        double c;
        double m;
        double y;
        double k;
        
        double r;
        double g;
        double b;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color = new Color(image.getRGB(j, i));
                
                //conversion a CMYK
                rFirst = color.getRed() / 255.0;
                gFirst = color.getGreen() / 255.0;
                bFirst = color.getBlue() / 255.0;
                
                k = 1 - max(rFirst, gFirst, bFirst);
                if(k == 1){
                    c = 0;
                    m = 0;
                    y = 0;
                }
                else{
                    c = ( 1 - rFirst - k ) / ( 1 - k ); 
                    m = ( 1 - gFirst - k) / ( 1 - k );
                    y = ( 1 - bFirst - k) / (1 - k);
                }

                //simulacion a RGB
                r = 255 * ( 1 - c ) * ( 1 - k );
                g = 255 * ( 1 - m ) * ( 1 - k );
                b = 255 * ( 1 - y ) * ( 1 - k );
                
                newImage.setRGB(j, i, new Color((int)r,(int)g,(int)b).getRGB());
            }
        }
        
        return newImage;   
    }
    
    
    public double max(double r, double g, double b){
        if(r >= g && r >= b){
            return r;
        }
        else if(g >= r && g >= b){
            return g;
        }
        else if(b >= r && b >= g){
            return b;
        }
        return r;
    }
    
    
}
