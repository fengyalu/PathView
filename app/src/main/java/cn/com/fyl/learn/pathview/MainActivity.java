package cn.com.fyl.learn.pathview;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private Button reset, save;
    private PathView pathView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reset = (Button) findViewById(R.id.reset);
        save = (Button) findViewById(R.id.save);
        pathView = (PathView) findViewById(R.id.pathView);
        reset.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                pathView.clear();
                break;
            case R.id.save:

                try {
                    //检测是否有写的权限
                    int permission = ActivityCompat.checkSelfPermission(this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = viewToBitmap(pathView);
                if (null != bitmap) {
                    saveBitmap(bitmap);
                }
                break;

            default:
                break;
        }
    }

    private Bitmap viewToBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 把bitmap本地然后存到相册，然后插入相册uri ，在发广播通知相册更新
     *
     * @param bitmap
     */
    private void saveBitmap(Bitmap bitmap) {
        File qrCache = new File(getExternalCacheDir(), "cache");//储存路径可以存在内部存储
        if (!qrCache.exists()) {
            qrCache.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";//图片文件名字
        File picFile = new File(qrCache, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(picFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //插入相册uri
        try {
            String result = MediaStore.Images.Media.insertImage(getContentResolver(),
                    picFile.getAbsolutePath(), picFile.getName(), null);
            Uri.parse(result);
            //发送广播，发送广播这里注意，只要更新你存的路径就好了，如果更新整个sdk 速度将会非常慢
            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            sendBroadcast(scannerIntent);
            Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}

