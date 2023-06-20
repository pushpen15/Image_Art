package model;
 
import java.awt.Color;
import java.awt.image.BufferedImage;
 
public class Histogram {
     
     
    /**
     * Return the histogram of the image.
     * @param imagen BufferedImagen whose histogram is to be obtained
     * @return Return an variable int[5][256], where the first field[0] corresponds
     * to the Red Channel, [1]=green [2]=blue [3]=high [4]=scale gray
     */
    public int[][] histogram(BufferedImage imagen){
        Color colorAuxiliar;
        /*create the variable that will contain the histogram
        The first field [0], will store the histogram Red
        [1]=green [2]=blue [3]=alpha [4]=grayscale*/
        int histogramReturn[][]=new int[3][256];
        //loop through the image
        for( int i = 0; i < imagen.getWidth(); i++ ){
            for( int j = 0; j < imagen.getHeight(); j++ ){
                //get color of the current pixel
                colorAuxiliar=new Color(imagen.getRGB(i, j));
                //We add a unit in the red row [0], 
                //in the column of the red color obtained
                
                histogramReturn[0][colorAuxiliar.getRed()]+=1;
                histogramReturn[1][colorAuxiliar.getGreen()]+=1;
                histogramReturn[2][colorAuxiliar.getBlue()]+=1;
                //histogramReturn[3][colorAuxiliar.getAlpha()]+=1;
                //histogramReturn[4][calcularMedia(colorAuxiliar)]+=1;
            }
        }
        return histogramReturn;
    }
}