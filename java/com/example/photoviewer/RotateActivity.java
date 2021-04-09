package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RotateActivity extends AppCompatActivity {
    //前进和后退的按钮
    private String filePath = "/data/data/com.example.photoviewer/files/";//图片存储路径
    private Button B3;
    private String _id;//图片ID
    //用ImageView来存放图片
    private Bitmap image;
    private ImageView Xianshi;
    private int clickNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);

        B3 = (Button) findViewById(R.id.Rotate);
        Xianshi = (ImageView) findViewById(R.id.imagexianshi);

        Intent intent=getIntent();
        _id=intent.getStringExtra("id");
        try {
            FileInputStream FIS=new FileInputStream(filePath+_id+".png");
            image = BitmapFactory.decodeStream(FIS);//依次找每一项对应的图片
            Xianshi.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bitmap bitmapRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
            (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else if (orientationDegree == 270) {
            targetX = 0;
            targetY = bm.getWidth();
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
            Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }

    public void rotate(View view) {
        image=bitmapRotation(image,90);
       //BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapRotation(image,90));
        Xianshi.setImageBitmap(image);
    }

    public void rotateSave(View view) throws IOException {//将旋转过的图片更新
        try {
            File photoPath = new File(filePath + _id + ".png");
            if(photoPath.exists())//删除原图片
                photoPath.delete();
            photoPath.createNewFile();
            FileOutputStream fileOS = new FileOutputStream(photoPath);
            image.compress(Bitmap.CompressFormat.PNG, 100, fileOS);//注意是PNG格式的。若设置为JPG格式，背景色会变黑
            fileOS.flush();
            fileOS.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        Intent intent=new Intent(RotateActivity.this,SingleviewActivity.class);
        intent.putExtra("id",_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}