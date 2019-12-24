package com.example.preschool.NghiPhep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.preschool.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonNghiPhepFullViewAdapter extends RecyclerView.Adapter<DonNghiPhepFullViewAdapter.ViewHolder> {

    private ArrayList<DonNghiPhep> list;

    public DonNghiPhepFullViewAdapter(ArrayList<DonNghiPhep> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.don_nghi_phep_full_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.setIsRecyclable(false);
        try {
            holder.tenHocSinh.setText("Bé: "+list.get(i).getKidName());
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
        private TextView tenHocSinh, ngayNghi, soNgayNghi, lyDo, parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenHocSinh = itemView.findViewById(R.id.view_ten_nguoi_nghi);
            ngayNghi = itemView.findViewById(R.id.view_ngay_nghi_full);
            soNgayNghi = itemView.findViewById(R.id.view_so_ngay_nghi_full);
            lyDo = itemView.findViewById(R.id.view_ly_do_full);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
