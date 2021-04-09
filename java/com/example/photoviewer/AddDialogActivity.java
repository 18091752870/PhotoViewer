package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AddDialogActivity extends AppCompatActivity {

    private EditText title;
    private EditText author;
    private ImageView photo;
    private boolean isPhoto=false;//判断是否选择图片
    private String filePath = "/data/data/com.example.photoviewer/files/";//图片存储路径
    private DataBase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dialog);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        photo = findViewById(R.id.photo);
        DB = new DataBase();
    }

    protected void onDestroy() {
        super.onDestroy();
        DB.close();
    }

    public void readPhoto(View v) {//从相册选择图片
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //设置请求码，以便我们区分返回的数据
        startActivityForResult(intent, 100);
        isPhoto=true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (100 == requestCode) {
            if (data != null) {
                //获取数据
                //获取内容解析者对象
                try {
                    Bitmap mBitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(data.getData()));
                    photo.setImageBitmap(mBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addConfirm(View v) {//确认，首次插入由于没有表必然报错，简化程序利用try-catch在catch中创建表
        String titleText = title.getText().toString();
        String authorText = author.getText().toString();
        if (titleText.isEmpty() || authorText.isEmpty() || isPhoto == false)//如果有信息未填，确定无效
            return;

        Bitmap image = ((BitmapDrawable) photo.getDrawable()).getBitmap();

        File imageFolder =new File(filePath);
        Cursor cursor_id;
        try {
            cursor_id=DB.insert(titleText, authorText);
        } catch (Exception e) {
            DB.create();
            cursor_id=DB.insert(titleText, authorText);
        }
        cursor_id.moveToFirst();
        String id=cursor_id.getString(0);
        cursor_id.close();
        if (imageFolder.exists()) {//保存图片，根据插入得到的id值为图片命名
            try {
                File photoPath = new File(filePath + id + ".png");
                photoPath.createNewFile();
                FileOutputStream fileOS = new FileOutputStream(photoPath);
                image.compress(Bitmap.CompressFormat.PNG, 100, fileOS);//注意是PNG格式的。若设置为JPG格式，背景色会变黑
                fileOS.flush();
                fileOS.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(AddDialogActivity.this,MultiviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void goMulti(View view) {
        Intent intent = new Intent(AddDialogActivity.this,MultiviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}