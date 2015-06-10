package coursera.coursework.dailyselfie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by neil on 05/06/15.
 */
public class SelfieListAdapter extends BaseAdapter{

    private ArrayList<SelfieObject> list = new ArrayList<SelfieObject>();
    private static LayoutInflater inflater = null;
    private Context mContext;
    private String mCurrentPhotoPath;

    public SelfieListAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView = convertView;
        ViewHolder holder;

        SelfieObject ds = list.get(position);

        if(convertView== null){
            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.selfie, parent, false);
            holder.selfie = (ImageView) newView.findViewById(R.id.selfie);

            holder.timestamp = (TextView) newView.findViewById(R.id.timestamp);
            newView.setTag(holder);
        }else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.selfie.setImageBitmap(ds.getSelfieBitmap());
        holder.timestamp.setText(ds.getTimeStamp().toString());

        return newView;
    }

    static class ViewHolder {

        ImageView selfie;
        TextView timestamp;

    }

    public void add(SelfieObject selfie){
        list.add(selfie);
        notifyDataSetChanged();
    }

    public void add(String path){

    }

    public void saveImage(SelfieObject so){

    }

    public ArrayList<SelfieObject> getList(){
        return list;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }
}
