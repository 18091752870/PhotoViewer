package com.example.photoviewer;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MultiviewActivity extends Activity {

    private DataBase DB;
    private ListView values;
    public static MultiviewActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiview);
        instance=this;

        values = (ListView) findViewById(R.id.values_list);//现在的数据库列表？
        // 获取SQLiteDatabase以操作SQL语句//新建或读取数据库
        DB=new DataBase();
        replaceList();

        // 点击，进入单张浏览模式
        values.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            // 获取所点击项的_id
            TextView tv = (TextView) arg1.findViewById(R.id.tv_id);
            final String id = tv.getText().toString();
            Intent intent=new Intent(MultiviewActivity.this,SingleviewActivity.class);
            intent.putExtra("id",id);//传递id
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        // 长按更新
        values.setOnItemLongClickListener((arg0, arg1, arg2, arg3) -> {
            // 获取_id,title,author项
            TextView tvId = (TextView) arg1.findViewById(R.id.tv_id);
            TextView tvTitle = (TextView) arg1
                .findViewById(R.id.tv_title);
            TextView tvAuthor = (TextView) arg1
                .findViewById(R.id.tv_author);
            final String id = tvId.getText().toString();
            String title = tvTitle.getText().toString();
            String author = tvAuthor.getText().toString();
            // 通过Dialog弹出修改界面
            Builder builder = new Builder(MultiviewActivity.this);
            builder.setTitle("修改");
            // 自定义界面包括两个文本输入框
            View v = View.inflate(MultiviewActivity.this, R.layout.alertdialog,
                null);
            final EditText etName = (EditText) v
                .findViewById(R.id.alert_name);
            final EditText etPass = (EditText) v
                .findViewById(R.id.alert_pass);
            // Dialog弹出就显示原内容
            etName.setText(title);
            etPass.setText(author);
            builder.setView(v);
            // 确定按钮点击事件
            builder.setPositiveButton("保存", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = etName.getText().toString();
                    String newPass = etPass.getText().toString();
                    DB.update(newName, newPass, id);
                    replaceList();// 更新后刷新列表
                }
            });
            // 取消按钮点击事件
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return true;
        });
    }
    /**
     * 关闭程序关闭数据库
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DB.close();
    }

    public void goHome(View view){//回到主页
        Intent intent = new Intent(MultiviewActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onAddPhoto(View v) {//响应函数，点击弹出对话框，添加一张图片
        Intent intent = new Intent(MultiviewActivity.this, AddDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void replaceList() {//从数据库读文件写入listView
        Cursor cursor = DB.selectAll();
        ArrayList<Photo> photoList= new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            photoList.add(new Photo(cursor.getString(cursor.getColumnIndex("_id")),
                cursor.getString(cursor.getColumnIndex("title")),
                cursor.getString((cursor.getColumnIndex("author")))));
            cursor.moveToNext();
        }
        cursor.close();
        for(int i=0;i<photoList.size();i++){
            try {
                Bitmap image = BitmapFactory.decodeStream(openFileInput(photoList.get(i).getId()+".png"));//依次找每一项对应的图片
                photoList.get(i).setPhoto(image);
            } catch (FileNotFoundException e) {

            }
        }
        PhotoAdapter adapter=new PhotoAdapter(MultiviewActivity.this,R.layout.values_item,photoList);
        ListView listView=findViewById(R.id.values_list);
        listView.setAdapter(adapter);
    }
}


    /*@Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==2){
            Intent receive=getIntent();
            String title=receive.getStringExtra("title");
            String author=receive.getStringExtra("author");
            Bitmap photo=receive.getParcelableExtra("photo");

            File imageFolder =new File(filePath);
            Cursor cursor_id;
            try {
                cursor_id=insert(title, author);
            } catch (Exception e) {
                create();
                cursor_id=insert(title, author);
            }
            cursor_id.moveToFirst();
            String id=cursor_id.getString(0);
            cursor_id.close();
            if (imageFolder.exists()) {//保存图片，根据插入得到的id值为图片命名
                try {
                    File photoPath = new File(filePath + id + ".png");
                    photoPath.createNewFile();
                    FileOutputStream fileOS = new FileOutputStream(photoPath);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, fileOS);//注意是PNG格式的。若设置为JPG格式，背景色会变黑
                    fileOS.flush();
                    fileOS.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
            replaceList();// 更新后刷新列表

        }
    }*/

    /*public void find(View v){
        String name = title.getText().toString();
        String pass = author.getText().toString();
        Cursor cursor = select1(name,pass);
        while(cursor.moveToNext()){
            String findname=cursor.getString(cursor.getColumnIndex("title"));
            String findpath=cursor.getString(cursor.getColumnIndex("author"));

        }
        Toast.makeText(this, "hello world", Toast.LENGTH_SHORT).show();

    }*/
    /**
     * ListView的适配器
     */

/* // 通过Dialog提示是否删除
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    MultiviewActivity.this);
                builder.setMessage("确定要删除吗？");
                // 确定按钮点击事件
                builder.setPositiveButton("确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(id);
                        File f=new File(filePath+id+".png");
                        f.delete();//删除文件
                        replaceList();// 删除后刷新列表
                    }
                });
                // 取消按钮点击事件
                builder.setNegativeButton("取消", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;*/