package com.example.qrapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.vision.clearcut.LogUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class DecreptActivity extends AppCompatActivity {
    Button scan,dec;
    ImageView im;
    TextView tv;
    Bitmap bitmap;
    ImageButton copy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrept);

        scan =findViewById(R.id.QR_Decrept_ACT_scan);
        dec =findViewById(R.id.QR_Decrept_ACT_dec);
        im =findViewById(R.id.QR_Decrept_ACT_imageView);
        tv=findViewById(R.id.QR_Decrept_ACT_tv);
        copy =findViewById(R.id.copy_img);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(DecreptActivity.CLIPBOARD_SERVICE);
                ClipData clip =ClipData.newPlainText("TextView",tv.getText().toString());
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(DecreptActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> activityResult =registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        im.setImageURI(result.getData().getData());
                        Uri uri =result.getData().getData();
                        try {
                            //savePath =uri.getPath();
                            System.out.println(uri.getPath());
                            bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), uri);
                        }catch (IOException e) {
                            // TODO Handle the exception
                        }
                    }
                }
        );

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResult.launch(intent);
            }
        });


        ActivityResultLauncher<Intent> activityResultLaucher;

        activityResultLaucher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                IntentResult intentResult = IntentIntegrator.parseActivityResult( result.getResultCode(), result.getData()  );

                if (intentResult.getContents()!=null){
                    AlertDialog.Builder builder=new AlertDialog.Builder(DecreptActivity.this);
                    builder.setTitle("hi");
                    builder.setMessage(intentResult.getContents());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    tv.setText(intentResult.getContents());

                }else{
                    Toast.makeText(getBaseContext(),"no scanner found try again",Toast.LENGTH_LONG).show();
                }

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator =new IntentIntegrator(DecreptActivity.this);
                intentIntegrator.setPrompt("hi here");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                //intentIntegrator.initiateScan();
                activityResultLaucher.launch(intentIntegrator.createScanIntent());
            }
        });



        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (!Python.isStarted()) {
                        Python.start(new AndroidPlatform(getBaseContext()));
                    }

                    BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                    bitmap = drawable.getBitmap();
                    String imagestring = getstringimage(bitmap);

                    Python py = Python.getInstance();
                    PyObject pyobj = py.getModule("decrept");

                    PyObject obj = pyobj.callAttr("main", imagestring);
                    String str = String.copyValueOf(obj.toString().toCharArray());
                    str = str.substring(3, str.length() - 1);
                    ///str=str.replaceAll("\\\\","\\");
                    String x[]=str.split("\\n");
                    //Toast.makeText(getBaseContext(),str.substring(5,7), Toast.LENGTH_LONG).show();
                    for (String y:x) {
                        Toast.makeText(getBaseContext(), y, Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                    tv.setText(str);
                }catch (Exception e){
                    Toast.makeText(getBaseContext(),"please choose a correct image",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private String getstringimage(Bitmap bitmap){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imagebyte =baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encodedImage;
    }

}