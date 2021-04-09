package com.example.photoviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private int resourceId;

    public PhotoAdapter(Context context, int textViewResourceId, List<Photo> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Photo photo=getItem(position); //获取当前项的实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewHolder viewHolder;
        if (convertView==null){

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewHolder=new ViewHolder();
            viewHolder.id=view.findViewById(R.id.tv_id);
            viewHolder.title=view.findViewById(R.id.tv_title);
            viewHolder.author=view.findViewById(R.id.tv_author);
            viewHolder.photo=view.findViewById(R.id.tv_photo);

            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewHolder);
        } else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }

        // 获取控件实例，并调用set...方法使其显示出来
        viewHolder.id.setText(photo.getId());
        viewHolder.title.setText(photo.getTitle());
        viewHolder.author.setText(photo.getAuthor());
        viewHolder.photo.setImageBitmap(photo.getPhoto());
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewHolder{
        TextView id,title,author;
        ImageView photo;
    }

}
