package model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import view.frmPrincipal;

public class ScaleImage {
    
    private final int WIDHT = 0;
    private final int HEIGHT = 1;
    
    private int height;
    private int width;
    
    private BufferedImage image;
    
    public BufferedImage getScaleImage(BufferedImage image,int percentage,int type){
        MyImage myImage = new MyImage(image);
        this.width = image.getWidth();
        this.height = image.getHeight();
        int channels = myImage.getNumberOfchannels();
        this.image = image;
        if(channels == 3){
            return scaleRGB(myImage.getMatrixImg_R(),myImage.getMatrixImg_G(),myImage.getMatrixImg_B(), percentage, type);
        }else{
            return scaleGray(myImage.getMatrixImg(),percentage,type);
        }
    }
    
    public BufferedImage scaleRGB(double [][] matrixR, double [][] matrixG, double [][] matrixB,int percentage, int type){

        if( percentage > 198 ){
            percentage = percentage - 1;
        }

        double [][] R;
        double [][] G;
        double [][] B;
        
        if(type == WIDHT){
            if( percentage > 100){
                int newPixels = getNumberOfNewPixels(width, percentage - 100);
                int step = Math.round(width / newPixels);
                
                if( frmPrincipal.scaleAlgorithmAverage ){System.out.println("Average");
                   
                }else if( frmPrincipal.scaleAlgorithmLineBefore ){
                    
                    int cutterWidth = 0;
                    int i;
                    int j;
                    
                    //Find the size of the scaled image 
                    //convertMatrixRGBImage
                    for ( j = 0; j < width; j++) {
                        if(j % step == 0 && j!= 0){
                            cutterWidth++;
                        }
                        cutterWidth++;
                    }
                    
                    
                    R = new double[height][cutterWidth];
                    G =  new double[height][cutterWidth];
                    B = new double[height][cutterWidth];
                    
                    int x;
                    cutterWidth = 0;

                    for ( j = 0; j < width; j++) {
                        for(x = 0; x < height ; x ++){
                            R[x][cutterWidth] = new Color(image.getRGB(j, x)).getRed();
                            G[x][cutterWidth] = new Color(image.getRGB(j, x)).getGreen();
                            B[x][cutterWidth] = new Color(image.getRGB(j, x)).getBlue();
                        }

                        if(j % step == 0 && j!= 0){
                            cutterWidth++;
                            for(x = 0; x < height ; x ++){
                                R[x][cutterWidth] = new Color(image.getRGB(j, x)).getRed();
                                G[x][cutterWidth] = new Color(image.getRGB(j, x)).getGreen();
                                B[x][cutterWidth] = new Color(image.getRGB(j, x)).getBlue();
                            }
                        }

                        cutterWidth++;
                    }
                    
                    return convertMatrixRGBImage(R, G, B);
                    
                }
                
            }
            else if(percentage < 100) {
                int deletePixels = getNumberOfNewPixels(width, 100 -percentage);
                int step = Math.round(width / deletePixels);
                int cutterWidth = 0;
                int j;

                //Find the size of the scaled image

                for (j = 0; j < width; j++) {
                    if(j % step != 0){
                        cutterWidth++;
                    }
                }

                R = new double[height][cutterWidth];
                G = new double[height][cutterWidth];
                B = new double[height][cutterWidth];

                int x;
                cutterWidth = 0;
                for ( j = 0; j < width; j++) {
                    if(j % step != 0){
                        for(x = 0; x < height ; x ++){
                            R[x][cutterWidth] = new Color(image.getRGB(j, x)).getRed();
                            G[x][cutterWidth] = new Color(image.getRGB(j, x)).getGreen();
                            B[x][cutterWidth] = new Color(image.getRGB(j, x)).getBlue();
                        }
                        cutterWidth++;
                    }
                }
                    
                return convertMatrixRGBImage(R, G, B);
            }
        }else if(type == HEIGHT){
            if( percentage > 100){
                int newPixels = getNumberOfNewPixels(height, percentage - 100);
                int step = Math.round(height / newPixels);
                int cutterHeight = 0;
                int j;

                //Find the size of the scaled image
                for ( j = 0; j < height; j++) {
                    if(j % step == 0 && j!= 0){
                        cutterHeight++;
                    }
                    cutterHeight++;
                }
                
                
                R = new double[cutterHeight][width];
                G = new double[cutterHeight][width];
                B = new double[cutterHeight][width];

                int x;
                cutterHeight = 0;
                
                for ( j = 0; j < height; j++) {
                    for(x = 0; x < width ; x ++){
                        R[cutterHeight][x] = new Color(image.getRGB(x, j)).getRed();
                        G[cutterHeight][x] = new Color(image.getRGB(x, j)).getGreen();
                        B[cutterHeight][x] = new Color(image.getRGB(x, j)).getBlue();
                    }

                    if(j % step == 0 && j!= 0){
                        cutterHeight++;
                        for(x = 0; x < width ; x ++){
                            R[cutterHeight][x] = new Color(image.getRGB(x, j)).getRed();
                            G[cutterHeight][x] = new Color(image.getRGB(x, j)).getGreen();
                            B[cutterHeight][x] = new Color(image.getRGB(x, j)).getBlue();
                        }
                    }

                    cutterHeight++;
                }
                
                return convertMatrixRGBImage(R, G, B);

            }else if( percentage < 100){
                int deletePixels = getNumberOfNewPixels(height, 100 - percentage);
                int step = Math.round(height / deletePixels);
                int cutterHeight = 0;
                int j;

                //Find the size of the scaled image

                for (j = 0; j < height; j++) {
                    if(j % step != 0){
                        cutterHeight++;
                    }
                }

                R = new double[cutterHeight][width];
                G = new double[cutterHeight][width];
                B = new double[cutterHeight][width];

                int x;
                cutterHeight = 0;
                for ( j = 0; j < height; j++) {
                    if(j % step != 0){
                        for(x = 0; x < width ; x ++){
                            R[cutterHeight][x] = new Color(image.getRGB(x, j)).getRed();
                            G[cutterHeight][x] = new Color(image.getRGB(x, j)).getGreen();
                            B[cutterHeight][x] = new Color(image.getRGB(x, j)).getBlue();
                        }
                        cutterHeight++;
                    }
                }
                    
                return convertMatrixRGBImage(R, G, B);
            }
        }
        return null;
    }
    
    
    public BufferedImage scaleGray(double [][] matrix, int percentage, int type){
        if(type == WIDHT){
            if(percentage > 100){
                int newPixels = getNumberOfNewPixels(width, percentage - 100);
                int step = Math.round(width / newPixels);
                
                int cutterWidth = 0;
                int j;

                //Find the size of the scaled image

                for ( j = 0; j < width; j++) {
                    if(j % step == 0 && j!= 0){
                        cutterWidth++;
                    }
                    cutterWidth++;
                }

                double [][] G;

                G =  new double[height][cutterWidth];

                int x;
                cutterWidth = 0;

                WritableRaster raster = image.getRaster();
                
                for ( j = 0; j < width; j++) {
                    for(x = 0; x < height ; x ++){
                        G[x][cutterWidth] = raster.getSampleDouble(j, x, 0);
                    }

                    if(j % step == 0 && j!= 0){
                        cutterWidth++;
                        for(x = 0; x < height ; x ++){
                            G[x][cutterWidth] = raster.getSampleDouble(j, x, 0);
                        }
                    }

                    cutterWidth++;
                }

                return convertMatrixGrayImage(G);
                
            }
            else if(percentage < 100){
                int deletePixels = getNumberOfNewPixels(width, 100 - percentage);
                int step = Math.round(width / deletePixels);
                int cutterWidth = 0;
                int j;

                //Find the size of the scaled image

                for (j = 0; j < width; j++) {
                    if(j % step != 0){
                        cutterWidth++;
                    }
                }
                
                double [][] G = new double[height][cutterWidth];
                WritableRaster raster = image.getRaster();
                
                int x;
                cutterWidth = 0;
                for ( j = 0; j < width; j++) {
                    if(j % step != 0){
                        for(x = 0; x < height ; x ++){
                            G[x][cutterWidth] = raster.getSampleDouble(j, x, 0);
                        }
                        cutterWidth++;
                    }
                }
                    
                return convertMatrixGrayImage(G);
            }

        }
        else if(type == HEIGHT){
            if(percentage > 100){
                int newPixels = getNumberOfNewPixels(height, percentage - 100);
                int step = Math.round(height / newPixels);
                
                int cutterHeight = 0;
                int j;

                //Find the size of the scaled image

                for ( j = 0; j < height; j++) {
                    if(j % step == 0 && j!= 0){
                        cutterHeight++;
                    }
                    cutterHeight++;
                }

                double [][] G;

                G =  new double[cutterHeight][width];

                int x;
                cutterHeight = 0;

                WritableRaster raster = image.getRaster();
                
                for ( j = 0; j < height; j++) {
                    for(x = 0; x < width ; x ++){
                        G[cutterHeight][x] = raster.getSampleDouble(x, j, 0);
                    }

                    if(j % step == 0 && j!= 0){
                        cutterHeight++;
                        for(x = 0; x < width ; x ++){
                            G[cutterHeight][x] = raster.getSampleDouble(x, j, 0);
                        }
                    }

                    cutterHeight++;
                }

                return convertMatrixGrayImage(G);
            }
            else{
                int deletePixels = getNumberOfNewPixels(height, 100 - percentage);
                int step = Math.round(height / deletePixels);
                int cutterHeight = 0;
                int j;

                //Find the size of the scaled image
                //raster

                for (j = 0; j < height; j++) {
                    if(j % step != 0){
                        cutterHeight++;
                    }
                }
                
                double [][] G = new double[cutterHeight][width];
                WritableRaster raster = image.getRaster();
                
                int x;
                cutterHeight = 0;
                for ( j = 0; j < height; j++) {
                    if(j % step != 0){
                        for(x = 0; x < width ; x ++){
                            G[cutterHeight][x] = raster.getSampleDouble(x, j, 0);
                        }
                        cutterHeight++;
                    }
                }
                    
                return convertMatrixGrayImage(G);
            }
        }
        
        return null;
    }
    
    private int getNumberOfNewPixels(int pixels, int percetange){
        return ( percetange * pixels ) / 100;
    } 
    
    public BufferedImage convertMatrixRGBImage(double [][]Matrix_R, double [][]Matrix_G, double [][]Matrix_B){
        
        BufferedImage bi = new BufferedImage(Matrix_R[0].length,Matrix_R.length,BufferedImage.TYPE_INT_RGB);
        WritableRaster wr = bi.getRaster();

        for (int i=0;i<Matrix_R.length;i++){
            for(int j=0;j<Matrix_R[0].length;j++){
                wr.setSample(j,i,0,Matrix_R[i][j]);
                wr.setSample(j,i,1,Matrix_G[i][j]);
                wr.setSample(j,i,2,Matrix_B[i][j]);
            }
        }
        
        bi.setData(wr);
        return bi;
    }
    
    public BufferedImage convertMatrixGrayImage(double [][] Matrix){
        int height = Matrix.length;
        int width = Matrix[0].length;

        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = image.getRaster();

        for (int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                wr.setSample(j,i,0,Matrix[i][j]);
            }
        }
        image.setData(wr);
        
        return image;
    }
    
}
