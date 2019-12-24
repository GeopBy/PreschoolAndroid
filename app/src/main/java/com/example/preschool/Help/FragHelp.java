package com.example.preschool.Help;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.preschool.R;

public class FragHelp extends BaseExpandableListAdapter {
    Context context;
    String[]title={
            "Tôi quên mật khẩu. Tôi phải làm sao?",
            "Làm thế nào để gửi tin nhắn cho giáo viên?",
            "Làm sao tôi biết mình có tin nhắn mới?",
            "Liên hệ với chúng tôi",

    };
    String [][] description={{
            "Bước 1: Đừng sợ\n"+"Bước 2: Hãy nhập địa chỉ email của bạn và nhấp vào Quên mật khẩu trên trang đăng nhập và làm theo các hướng dẫn"},
            {"Mở thanh công cụ Tin nhắn và tìm người bạn muốn nói chuyện. Nhập tin nhắn của bạn vào hộp tin nhắn và nhấp vào gửi đi"},
            {"Bạn sẽ thấy một dấu chấm màu hông trên thanh công cụ tin nhắn của bạn nếu có tin nhắn mới. Các tin nhắn chưa đọc sẽ được in đậm."},
            {"Bạn gặp vấn đề khi dùng ứng dụng? Hoặc có thể bạn có ý tưởng hay mà bạn muốn chia sẻ với chúng tối? Hãy gửi cậu hỏi hoặc nhận xét của bạn cho chúng tôi theo email pressschool.gmail.com"},
    };

    public FragHelp(Context context) {
        this.context = context;
    }

    @Override

    public int getGroupCount() {
        return title.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return description[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return title[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return description[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String TitlePaq =(String) getGroup(groupPosition);
        if(convertView==null)
        {
            LayoutInflater inflater =(LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.fags_title,null);

        }
        TextView TitlePaq2=convertView.findViewById(R.id.TitleView);
        TitlePaq2.setTypeface(null, Typeface.NORMAL);
        TitlePaq2.setText(TitlePaq);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String description =(String)getChild(groupPosition,childPosition);
        if(convertView==null)
        {
            LayoutInflater inflater =(LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.fags_description,null);
        }
        TextView description2=convertView.findViewById(R.id.TextDescription);
        description2.setTypeface(null, Typeface.NORMAL);
        description2.setText(description);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
