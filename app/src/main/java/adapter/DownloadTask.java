package adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.weaboo.jurnalotaku.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {
    private static final String TAG = "Download Task";
    private Context context;

    private String downloadUrl = "", downloadFileName = "";
    private ProgressDialog progressDialog;

    public DownloadTask(Context context, String downloadUrl) {
        this.context = context;

        this.downloadUrl = downloadUrl;


        downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'), downloadUrl.length());//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    progressDialog.dismiss();
                    ContextThemeWrapper ctw = new ContextThemeWrapper( context, R.style.Theme_AppCompat_Dialog_Alert);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("Download  ");
                    alertDialogBuilder.setMessage("Journal Downloaded Successfully ");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

                    alertDialogBuilder.setNegativeButton("Open report",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/JournalOtaku/" + downloadFileName);  // -> filename = maven.pdf
                            Uri path = Uri.fromFile(pdfFile);
                            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                            pdfIntent.setDataAndType(path, "application/zip");
                            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            try{
                                context.startActivity(pdfIntent);
                            }catch(ActivityNotFoundException e){
                                Toast.makeText(context, R.string.no_task, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertDialogBuilder.show();
//                    Toast.makeText(context, "Document Downloaded Successfully", Toast.LENGTH_SHORT).show();
                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }


            public boolean isSDCardPresent() {
                if (Environment.getExternalStorageState().equals(

                        Environment.MEDIA_MOUNTED)) {
                    return true;
                }
                return false;
            }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (isSDCardPresent()) {

                    apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "JournalOtaku");
                } else
                    Toast.makeText(context, R.string.no_sd_card, Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}