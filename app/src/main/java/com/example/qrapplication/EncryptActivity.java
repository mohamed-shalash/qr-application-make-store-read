package com.example.qrapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptActivity extends AppCompatActivity {
    Button generate,save;
    EditText editText;
    ImageView im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        generate =findViewById(R.id.QR_main_ACT_Generate);
        save =findViewById(R.id.QR_main_ACT_save);
        editText =findViewById(R.id.QR_main_ACT_ET);
        im =findViewById(R.id.QR_main_ACT_imageView);
        save.setVisibility(View.GONE);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data =editText.getText().toString();
                if(data.isEmpty()){
                    Toast.makeText(getBaseContext(),"plese inter data",Toast.LENGTH_LONG).show();
                }else{
                    /*QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension);
                    qrgEncoder.setColorBlack(Color.RED);
                    qrgEncoder.setColorWhite(Color.BLUE);
                    try {
                        // Getting QR-Code as Bitmap
                        bitmap = qrgEncoder.getBitmap();
                        // Setting Bitmap to ImageView
                        qrImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.v(TAG, e.toString());
                    }*/
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
                        int width = 512;
                        int height = 512;
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                if (bitMatrix.get(x, y)==false)
                                    bmp.setPixel(x, y, Color.WHITE);
                                else
                                    bmp.setPixel(x, y, Color.BLACK);
                            }
                        }
                        im.setImageBitmap(bmp);
                    } catch (WriterException e) {
                        //Log.e("QR ERROR", ""+e);

                    }
                    save.setVisibility(View.VISIBLE);
                }
            }
        });
save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
        save_image(drawable.getBitmap());
    }
});

    }
    private void save_image(Bitmap bitmap){
        Bitmap bitmap2 = bitmap;

        // Save image to gallery
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(), bitmap2,
                "Birdaya" + ".png", "drawing");/*(
                getContentResolver(),
                bitmap2,
                "Bird",
                "Image of bird"
        );*/

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(savedImageURL);

        Toast.makeText(getBaseContext(),"Image saved to gallery.\n" + savedImageURL,Toast.LENGTH_SHORT).show();
    }
}