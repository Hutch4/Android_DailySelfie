package coursera.coursework.dailyselfie;

import android.graphics.Bitmap;

import java.sql.Timestamp;
import java.sql.Time;

/**
 * Created by neil on 05/06/15.
 */
public class SelfieObject {

    private Bitmap selfieBitmap;
    private java.sql.Timestamp ts;

    public SelfieObject(){

    }
        public SelfieObject(Bitmap bm, Timestamp s){
        this.selfieBitmap = bm;
        this.ts = s;
    }
    public java.sql.Timestamp getTimeStamp(){
        return ts;
    }

    public void setTimeStamp(java.sql.Timestamp nts){
        this.ts = nts;
    }

    public Bitmap getSelfieBitmap(){
        return selfieBitmap;
    }

    public void setSelfieBitmap(Bitmap b){
        this.selfieBitmap = b;
    }
}
