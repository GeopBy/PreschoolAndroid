package com.example.preschool.NghiPhep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.preschool.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonNghiPhepViewAdapter extends RecyclerView.Adapter<DonNghiPhepViewAdapter.ViewHolder> {

    private ArrayList<DonNghiPhep> list;

    public DonNghiPhepViewAdapter(ArrayList<DonNghiPhep> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DonNghiPhepViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.don_nghi_phep_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        try {
            holder.ngayNghi.setText("Ngày nghỉ: " + list.get(i).getNgayNghi());
            holder.soNgayNghi.setText("Số ngày: " + list.get(i).getSoNgay());
            holder.lyDo.setText("Lý do: " + list.get(i).getLyDo());
            holder.parent.setText("Người viết: " + list.get(i).getParentName());
        } catch (Exception e) {
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ngayNghi, soNgayNghi, lyDo, parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ngayNghi = itemView.findViewById(R.id.view_ngay_nghi);
            soNgayNghi = itemView.findViewById(R.id.view_so_ngay_nghi);
            lyDo = itemView.findViewById(R.id.view_ly_do);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
