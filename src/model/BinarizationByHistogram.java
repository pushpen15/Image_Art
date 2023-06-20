package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BinarizationByHistogram {
    
    private BufferedImage image;

    public BinarizationByHistogram(BufferedImage image) {
        this.image = image;
    }
    
    public BufferedImage apply(){
        
        int histogram [][] = new Histogram().histogram(image);
        
        // 0 = RED
        int thresholdRed;
        int thresholdGreen;
        int thresholdBlue;
        
        int size;
        int middle;
        ArrayList<ListHistogram> listRed = new ArrayList<>();
        ArrayList<ListHistogram> listGreen = new ArrayList<>();
        ArrayList<ListHistogram> listBlue = new ArrayList<>();
        
        for(int i = 120 ; i < 150 ; i++){
            listRed.add(new ListHistogram(i, histogram[0][i]));
            listGreen.add(new ListHistogram(i, histogram[1][i]));
            listBlue.add(new ListHistogram(i, histogram[2][i]));
        }
        
        Collections.sort(listRed, (ListHistogram o1, ListHistogram o2) -> {
            return o1.getValue() - o2.getValue();
        });
        
        Collections.sort(listGreen, (ListHistogram o1, ListHistogram o2) -> {
            return o1.getValue() - o2.getValue();
        });
                
        Collections.sort(listBlue, (ListHistogram o1, ListHistogram o2) -> {
            return o1.getValue() - o2.getValue();
        });
        
        size = listRed.size();
        middle = size / 2;
        thresholdRed = listRed.get(middle).getI();
        
        size = listGreen.size();
        middle = size / 2;
        thresholdGreen = listGreen.get(middle).getI();
        
        size = listBlue.size();
        middle = size / 2;
        thresholdBlue = listBlue.get(middle).getI();
        
        
        return new ChannelBinarization(image).apply(thresholdRed, thresholdGreen, thresholdBlue);
        
    }
    
}


class ListHistogram{
    private int i;
    private int value;

    public ListHistogram(int i, int value) {
        this.i = i;
        this.value = value;
    }

    public int getI() {
        return i;
    }

    public int getValue() {
        return value;
    }
       
}
