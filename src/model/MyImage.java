package model;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MyImage extends Component
{
    private BufferedImage   img;
    private double [][]     matrixImg;		 //matrixImg of the grayscale image
    private double [][]     matrixImg_R;         //matrixImg of the image in R 
    private double [][]     matrixImg_G;         //matrix of the image in G
    private double [][]     matrixImg_B;         //matrix of the image in B
    private int             noColumns;            //numeber of row nobre
    private int             noRow;               //numero de nobre
    private int numberOfchannels;                   // nobre
    
    public MyImage(String nobleImage){
        try{
            img = ImageIO.read(new File(nobleImage));
            convertirImageAMatrix();
        }
        catch (IOException e) {}
    }

    public MyImage(BufferedImage image){
        this.img = image;
        convertirImageAMatrix();
    }
    
    public void convertirImageAMatrix()
    {
        noRow = img.getHeight();
    	noColumns = img.getWidth();

    	matrixImg 	= new double [noRow][noColumns];
    	matrixImg_R	= new double [noRow][noColumns];
     	matrixImg_G     = new double [noRow][noColumns];
    	matrixImg_B	= new double [noRow][noColumns];
    	double r;
    	double g;
    	double b;

    	WritableRaster raster=img.getRaster();
    	this.numberOfchannels =raster.getNumBands(); 

    	for (int i=0;i<noRow;i++){
            
            for(int j=0;j<noColumns;j++){
                
                if (this.numberOfchannels == 3){
                    r=raster.getSampleDouble(j,i,0);
                    g=raster.getSampleDouble(j,i,1);
                    b=raster.getSampleDouble(j,i,2);
                 
                    matrixImg[i][j]=(r+g+b)/3;
                    matrixImg_R[i][j]=r;
                    matrixImg_G[i][j]=g;
                    matrixImg_B[i][j]=b;
                }
                if (this.numberOfchannels == 1){
                    matrixImg[i][j]=raster.getSampleDouble(j,i,0);
                }
            }
        }
    }

    public BufferedImage convertirMatrixGrayAImage(double [][] matrix){
        int height = matrix.length;
        int width = matrix[0].length;

        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = image.getRaster();

        for (int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                wr.setSample(j,i,0,matrix[i][j]);
            }
        }
        image.setData(wr);
        
        return image;
    }

    public BufferedImage convertirMatrixRGBAImage(double [][]matrix_R, double [][]matrix_G, double [][]matrix_B){
        
        BufferedImage image = new BufferedImage(matrix_R[0].length,matrix_R.length,BufferedImage.TYPE_INT_RGB);
        WritableRaster wr = image.getRaster();

        for (int i=0;i<matrix_R.length;i++){
            for(int j=0;j<matrix_R[0].length;j++){
                wr.setSample(j,i,0,matrix_R[i][j]);
                wr.setSample(j,i,1,matrix_G[i][j]);
                wr.setSample(j,i,2,matrix_B[i][j]);
            }
        }

        image.setData(wr);
        return image;
    }
    
    public static void saveImage(double [][]matrix, String path){       
        BufferedImage imgNew = new BufferedImage(matrix[0].length,matrix.length,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = imgNew.getRaster();

        for (int i=0;i<matrix.length;i++)
        {            for(int j=0;j<matrix[0].length;j++)
            {
                wr.setSample(j,i,0,matrix[i][j]);
            }
        }

        imgNew.setData(wr);

        try
        {
            ImageIO.write(imgNew, "JPG", new File(path+".jpg"));
        }
        catch(IOException e){}
    }

    public static void saveImage(double [][]matrix_R, double [][]matrix_G, double [][]matrix_B,String path)
 {
        BufferedImage imgn = new BufferedImage(matrix_R[0].length,matrix_R.length,BufferedImage.TYPE_INT_RGB);
        WritableRaster wr = imgn.getRaster();
        int i, j;
        
        for(i=0;i<matrix_R.length;i++){
            for(j=0;j<matrix_R[0].length;j++){
                wr.setSample(j,i,0,matrix_R[i][j]);
                wr.setSample(j,i,1,matrix_G[i][j]);
                wr.setSample(j,i,2,matrix_B[i][j]);
            }
        }

        imgn.setData(wr);
        try{
            path = path + "\\im.jpg";
            ImageIO.write(imgn, "JPG", new File(path));
            System.out.println("-" + path);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
   
    public int getNumberOfchannels() {
        return numberOfchannels;
    }
    
    public double[][] getMatrixImg() {

        return matrixImg;
    }
    public int getColumns() {
        return noColumns;
    }
    public int getRows() {
        return noRow;
    }
    public BufferedImage getImg() {
        return img;
    }
    public double[][] getMatrixImg_B() {
        return matrixImg_B;
    }
    public double[][] getMatrixImg_G() {
        return matrixImg_G;
    }
    public double[][] getMatrixImg_R() {
        return matrixImg_R;
    }
    public void setMatrixImg(double[][] matrixImg) {
        this.matrixImg = matrixImg;
    }
    public void setImg(BufferedImage img) {
        this.img=img;
    }
    public void setMatrixImg_B(double[][] matrixImg_B) {
        this.matrixImg_B = matrixImg_B;
    }
    public void setMatrixImg_G(double[][] matrixImg_G) {
        this.matrixImg_G = matrixImg_G;
    }
    public void setMatrixImg_R(double[][] matrixImg_R) {
        this.matrixImg_R = matrixImg_R;
    }

}
