package com.thevoicegroup.technoimagepicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImagePicker imagePicker;
    ArrayList<Bitmap> images = new ArrayList<>();
    ImageView imgThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgThumb = findViewById(R.id.imgThumb);
        imagePicker = new ImagePicker(this);
//        imagePicker.CaptureImageFromCamera();
        imagePicker.SelectImageFromGallery("#000000","#000000", "#FFFFFF", 1,1);

        imagePicker.imageResult = (imagesPathList, ImagePath, bitmap, bitmaps, imageType) -> {
            if (imageType==ImageType.CAMERA){
                images.add(bitmap);
                for (int i = 0; i < images.size(); i++) {

                }
                imgThumb.setImageBitmap(images.get(0));
            }else {
                images.addAll(bitmaps);
                imgThumb.setImageBitmap(images.get(0));
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onImageActivityResult(requestCode, resultCode, data);
    }
}