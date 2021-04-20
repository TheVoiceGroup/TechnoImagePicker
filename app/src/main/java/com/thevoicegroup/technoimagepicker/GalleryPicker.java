package com.thevoicegroup.technoimagepicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class GalleryPicker extends AppCompatActivity {

    private RecyclerView recycler_images;
    private ImageAdapter adapter;
    private ArrayList<ImageItem> imageList;
    private Cursor cursor;
    private final String DIRECTORY = Environment.getExternalStorageDirectory().toString();
    private TextView txt_images_count;
    private AppCompatButton btnCancel, btnSelect;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count, max, min, size = 0;
    private String colorPrimary, colorSecondary, colorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_picker);

        Intent intent = getIntent();
        colorSecondary = intent.getStringExtra("SECONDARY");
        colorPrimary = intent.getStringExtra("PRIMARY");
        colorText = intent.getStringExtra("TEXT");
        max = intent.getIntExtra("MAX", 1);
        min = intent.getIntExtra("MIN", 1);

        imageList = new ArrayList<>();
        txt_images_count = findViewById(R.id.txt_images_count);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelect = findViewById(R.id.btnSelect);
        recycler_images = findViewById(R.id.recycler_images);
        recycler_images.setHasFixedSize(false);
        recycler_images.setLayoutManager(new GridLayoutManager(this, 3));

        btnSelect.setBackgroundColor(Color.parseColor(colorPrimary));
        btnCancel.setBackgroundColor(Color.parseColor(colorPrimary));
        btnSelect.setTextColor(Color.parseColor(colorText));
        btnCancel.setTextColor(Color.parseColor(colorText));
        txt_images_count.setBackgroundColor(Color.parseColor(colorPrimary));
        txt_images_count.setTextColor(Color.parseColor(colorText));

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + imageList.get(i).getThumbnail() + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                }else {
                    Log.d("SelectedImages", selectImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectImages);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                GalleryPicker.super.onBackPressed();
            }
        });

        getGalleryImages();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    public void getGalleryImages() {
        String selection = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media.DATA,
        };
        String orderBy = android.provider.MediaStore.Video.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                orderBy + " DESC");

        ImageItem imageItem;
        while (cursor.moveToNext()) {
            imageItem = new ImageItem();
            imageItem.setThumbnail(cursor.getString(0));
            imageList.add(imageItem);
        }
        thumbnailsselection = new boolean[imageList.size()];
        adapter = new ImageAdapter(imageList);
        recycler_images.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private LayoutInflater mInflater;
        private ArrayList<ImageItem> Images;

        public ImageAdapter(ArrayList<ImageItem> allImages) {
            this.Images = allImages;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new ViewHolder(v);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            setCheckBoxColor(holder.chkImage, Color.parseColor(colorSecondary), Color.parseColor(colorPrimary));
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);

            Picasso.get()
                    .load(new File(Images.get(position).getThumbnail()))
                    .fit()
                    .centerCrop()
                    .into(holder.imgThumb);

            holder.chkImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        txt_images_count.setText(size + " Images Selected");
                        size--;
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        if (size>=max) {
                            Toast.makeText(GalleryPicker.this, "You've Reached maximum limit", Toast.LENGTH_SHORT).show();
                            cb.setChecked(false);
                        }else {
                            size++;
                            txt_images_count.setText(size + " Images Selected");
                            cb.setChecked(true);
                            thumbnailsselection[id] = true;
                        }
                    }
                }
            });

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        size--;
                        txt_images_count.setText(size + " Images Selected");
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        if (size>=max) {
                            Toast.makeText(GalleryPicker.this, "You've Reached maximum limit", Toast.LENGTH_SHORT).show();
                            holder.chkImage.setChecked(false);
                        }else {
                            size++;
                            txt_images_count.setText(size + " Images Selected");
                            holder.chkImage.setChecked(true);
                            thumbnailsselection[id] = true;
                        }
                    }
                }
            });



            try {
//                setBitmap(holder.imgThumb, ids[position]);
                //holder.imgThumb.setImageBitmap(bitmaps.get(position));
            } catch (Throwable e) {
            }
//            holder.chkImage.setChecked(thumbnailsselection[position]);
        }

        @Override
        public int getItemCount() {
            return Images.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgThumb;
            AppCompatCheckBox chkImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgThumb = itemView.findViewById(R.id.imgThumb);
                chkImage = itemView.findViewById(R.id.chkImage);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  void setCheckBoxColor(AppCompatCheckBox checkBox, int uncheckedColor, int checkedColor) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },
                new int[] {
                        uncheckedColor,
                        checkedColor
                }
        );
        checkBox.setButtonTintList(colorStateList);
    }
}