package coursera.coursework.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelfieListActivity extends ListActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private SelfieListAdapter sAdapter;
    private final long ALARM_INTERVAL = 1000L * 120;

    private AlarmManager mAlarmManager;
    private Intent mSelfieNotificationIntent;
    private PendingIntent mSelfieNotificationPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ListView placesListView = getListView();

        //Set up button to call camera application.
        final View buttonView = LayoutInflater.from(this).inflate(R.layout.activity_selfie_list, null);
        placesListView.addHeaderView(buttonView);

        //Click listener for enlarging an image when clicked upon.
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = placesListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"\"Zoomed Image (To Implement)\"", Toast.LENGTH_SHORT).show();
            }
        });


        sAdapter = new SelfieListAdapter(getApplicationContext());
        setListAdapter(sAdapter);
        //Load pictures into listview from the daily selfie


        loadSavedImages();


        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //Set up periodic notificaion alarm.
        mSelfieNotificationIntent = new Intent(SelfieListActivity.this,
                SelfieNotification.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mSelfieNotificationPendingIntent = PendingIntent.getBroadcast(
                SelfieListActivity.this, 0, mSelfieNotificationIntent, 0);

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                ALARM_INTERVAL,
                ALARM_INTERVAL,
                mSelfieNotificationPendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selfie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Deal with return of photo from the camera.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Extra the photo
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Create new selfie object and save this to the SD card
            java.sql.Timestamp ts = new java.sql.Timestamp(new Date().getTime());
            SelfieObject so = new SelfieObject(imageBitmap,ts);
            try {
                saveImageToFile(imageBitmap, ts);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //   so.setSelfieBitmap(imageBitmap);
          //  so.setTimeStamp(new java.sql.Timestamp(new Date().getTime()));
            sAdapter.add(so);
        }
    }

    /*
       Call to save a bitmap image as JPEG to the SD card.
     */
    private void saveImageToFile(Bitmap imageBitmap, java.sql.Timestamp ts) throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DailySelfie");

        storageDir.mkdirs();

        File image = new File(storageDir, imageFileName+".jpg");
        try {
            FileOutputStream out = new FileOutputStream(image);

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        galleryAddPic(storageDir +imageFileName+".jpg");
    }

    public void callTakePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /*
    load images in specified folder into the listview adapter.
     */
    public void loadSavedImages(){
        String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath();

        String targetPath = ExternalStorageDirectoryPath + "/DailySelfie";
        File targetDirector = new File(targetPath);
        File[] files = targetDirector.listFiles();
        if(files != null){
            for (File file : files){
                sAdapter.add(new SelfieObject(decodeSampledBitmapFromUri(file.getPath()), new java.sql.Timestamp(file.lastModified())));
            }
        }else{
            Toast.makeText(getApplicationContext(),"No images to load.", Toast.LENGTH_SHORT).show();
        }

    }
    /*
    decode a file path into a bitmap.
     */

    public Bitmap decodeSampledBitmapFromUri(String path) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }



    /*
        Add a picture via file path to the default photo gallery
     */
    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /*
        check whether external storage is writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
