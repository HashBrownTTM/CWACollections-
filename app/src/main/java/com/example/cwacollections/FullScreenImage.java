package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cwacollections.databinding.ActivityFullScreenImageBinding;
import com.ortiz.touchview.TouchImageView;

import java.io.File;

public class FullScreenImage extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    ImageButton btnDownload, btnBack;
    TouchImageView imgFullScreen;
    RelativeLayout fullImgHeader, rlFullImage ;

    String itemUrl, fileName = "untitled";
    int toolbarVisibility = 0;

    ActivityFullScreenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        binding = ActivityFullScreenImageBinding.inflate(getLayoutInflater());

        btnDownload = findViewById(R.id.btnDownload);
        btnBack = findViewById(R.id.btnBack);
        btnDownload.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        imgFullScreen = findViewById(R.id.imgFullScreen);
        imgFullScreen.setOnClickListener(this);

        fullImgHeader = findViewById(R.id.fullImgHeader);
        rlFullImage = findViewById(R.id.rlFullImage);

        Intent intent = getIntent();
        itemUrl = intent.getStringExtra("itemUrl");

        Glide.with(this).asBitmap().load(itemUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imgFullScreen.setImageBitmap(resource);                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;

            case R.id.btnDownload:
                try {
                    DownloadManager downloadManager;
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    Uri downloadUrl = Uri.parse(itemUrl);

                    DownloadManager.Request request = new DownloadManager.Request(downloadUrl);

                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                            .setAllowedOverRoaming(false)
                            .setTitle(fileName)
                            .setMimeType("image/png")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator+fileName+".png");

                    downloadManager.enqueue(request);

                    Toast.makeText(this, fileName + ".png downloaded", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(this, "Image download failed:\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imgFullScreen :
                if(toolbarVisibility == 0){
                    float scroll = rlFullImage.getHeight() - fullImgHeader.getHeight();
                    fullImgHeader.animate().translationY(-(scroll));
                    ++toolbarVisibility;
                }
                else{
                    fullImgHeader.animate().translationY(0);
                    --toolbarVisibility;
                }
                break;

        }
    }

    @SuppressLint({"ClickableViewAccessibility", "NonConstantResourceId"})
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getId();
        return true;
    }
}

/* References
GeeksforGeeks. 2022. How to Download File from URL in Android Programmatically using Download Manager? - GeeksforGeeks. [ONLINE]
Available at: https://www.geeksforgeeks.org/how-to-download-file-from-url-in-android-programmatically-using-download-manager/.
[Accessed 20 May 2022].

GitHub. 2022. GitHub - MikeOrtiz/TouchImageView for Android[ONLINE]
Available at: https://github.com/MikeOrtiz/TouchImageView.
[Accessed 20 May 2022].
*/