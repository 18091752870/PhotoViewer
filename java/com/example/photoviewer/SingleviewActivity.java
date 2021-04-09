package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SingleviewActivity extends AppCompatActivity {

    private DataBase DB;
    private String filePath = "/data/data/com.example.photoviewer/files/";//图片存储路径
    private ArrayList<Photo> photoList;
    private ImageView photo;
    private TextView title,author,hint;
    private int index;//选定图片在list中的位置
    private int photoSum;//图片总数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleview);

        DB=new DataBase();//连接数据库
        photoList=getPhotoList();//读取数据列表
        Intent intent=getIntent();
        String comingId=intent.getStringExtra("id");//获取参数
        photo =(ImageView) findViewById(R.id.singleImage);
        title=(TextView)findViewById(R.id.singleTitle);
        author=(TextView)findViewById(R.id.singleAuthor);
        hint=(TextView)findViewById(R.id.singleHint);
        photoSum=photoList.size();

        for(int i=0;i<photoSum;i++){
            String _id=photoList.get(i).getId();
            if(_id.equals(comingId)) {
                index=i;
                photo.setImageBitmap(photoList.get(i).getPhoto());//设置图片
                title.setText(photoList.get(i).getTitle());
                author.setText(photoList.get(i).getAuthor());
                hint.setText(index+1 +"/"+photoSum);  //显示图片是第几张
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        DB.close();
    }

    public ArrayList<Photo> getPhotoList() {//从数据库读文件存入photoList
        Cursor cursor = DB.selectAll();
        ArrayList<Photo> photoList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            photoList.add(new Photo(cursor.getString(cursor.getColumnIndex("_id")),
                cursor.getString(cursor.getColumnIndex("title")),
                cursor.getString((cursor.getColumnIndex("author")))));
            cursor.moveToNext();
        }
        cursor.close();
        for (int i = 0; i < photoList.size(); i++) {
            try {
                Bitmap image = BitmapFactory.decodeStream(openFileInput(photoList.get(i).getId() + ".png"));//依次找每一项对应的图片
                photoList.get(i).setPhoto(image);
            } catch (FileNotFoundException e) {
            }
        }
        return photoList;
    }

    public void lastImg(View view) {
        //如果当前图片是第一张，则上一张图片为最后一张图片
        if (index == 0) {
            index = photoSum-1;
        } else {
            //否则改为上一张图片索引
            index = index - 1;
        }
        photo.setImageBitmap(photoList.get(index).getPhoto());   //切换图片
        title.setText(photoList.get(index).getTitle());
        author.setText(photoList.get(index).getAuthor());
        hint.setText(index+1 +"/"+photoSum);  //显示图片是第几张
    }

    public void nextImg(View view) {
        //如果当前图片是最后一张，则下一张图片为第一张图片
        if (index == photoSum - 1) {
            index = 0;
        } else {
            //否则改为下一张图片索引
            index = index + 1;
        }
        photo.setImageBitmap(photoList.get(index).getPhoto());   //切换图片
        title.setText(photoList.get(index).getTitle());
        author.setText(photoList.get(index).getAuthor());
        hint.setText(index+1+"/"+photoSum);  //显示图片是第几张
    }

    public void edit(View view) {
        Intent intent=new Intent(SingleviewActivity.this,RotateActivity.class);
        String id=photoList.get(index).getId();
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void SingleDelete(View view) {//删除单张图片
        AlertDialog.Builder builder = new AlertDialog.Builder(
            SingleviewActivity.this);
        builder.setMessage("确定要删除吗？");
        // 确定按钮点击事件
        builder.setPositiveButton("确定", (dialog, which) -> {
            String _id=photoList.get(index).getId();
            DB.delete(_id);
            File f=new File(filePath+_id+".png");
            f.delete();//删除文件
            photoList.remove(index);//在photolist表中删除此项
            photoSum=photoList.size();//更新图片数
            if(photoSum==0) {//如果没有图片
                Intent intent = new Intent(SingleviewActivity.this,EmptyviewActivity.class);
                startActivity(intent);
                finish();
            }
            else nextImg(view);//刷新界面
        });
        // 取消按钮点击事件
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}

