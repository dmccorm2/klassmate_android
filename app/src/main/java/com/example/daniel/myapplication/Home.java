package com.example.daniel.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;




public class Home extends Activity {
    private static final int CAPTURE_IMAGE_ACTIVTY_REQUEST_CODE = 100;
    String image_path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Button newNote = (Button) findViewById(R.id.newnote);
        newNote.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
             dispatchTakePictureIntent();


        }
        });
    }

    public interface FileWebService {
        @Headers({
                "Cookie: user_id=2; addCourse=0; courses=j%3A%5B%22CSE40113%22%2C%22CSE40232%22%5D; crns=j%3A%5B11241%2C15",
                "Referer: http://104.131.82.38:3000/classpage?id=11241",

                })
        @Multipart
        @POST("/fileupload")
        Response upload(@Part("name") TypedFile image,
                    @Part("filename") TypedString filename,
                    @Part("title") TypedString title);
    }



    private void dispatchTakePictureIntent(){



        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVTY_REQUEST_CODE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();


            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.v("Photo is not null: ", Uri.fromFile(photoFile).toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));


            }

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVTY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // image captured
                Toast.makeText(this, "Uploading image", Toast.LENGTH_LONG).show();
                File photo = new File(image_path);
                new uploadPhoto().execute(photo);
            }
            if(resultCode == RESULT_CANCELED){
                // cancelled image capture
            }
            else{
                Toast.makeText(this, "Failed to take photo", Toast.LENGTH_LONG).show();
            }
        }

    }

    class uploadPhoto extends AsyncTask<File, Void, String> {

        private Exception exception;



        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed
        }


        @Override
        protected String doInBackground(File... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://104.131.82.38:3000")
                    .build();
            URI url = params[0].toURI();
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtensionFromUrl(url.toString()));
            Log.w("Mimetype: ", mimeType);
            TypedFile fileToSend;
            if(params[0].exists()) Log.v("File: ", params[0].getAbsolutePath());
            FileWebService fws = restAdapter.create(FileWebService.class);
           // File test = new File("/storage/emulated/0/Pictures/JPEG_20141210_001748_-842374887.jpg");
            fileToSend = new TypedFile(mimeType, params[0]);
            TypedString fname = new TypedString("android_test.jpg");
            fws.upload(fileToSend, fname, fname);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
    String mCurrentPhotoPath = "";
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
        new uploadPhoto().execute(image);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        image_path = image.getAbsolutePath();
        Log.w("image file: ", image_path);
        return image;
    }

}
