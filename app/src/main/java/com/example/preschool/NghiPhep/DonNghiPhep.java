package com.example.preschool.NghiPhep;

public class DonNghiPhep {
    String userId,parentName,kidName,ngayNghi,soNgay,lyDo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getKidName() {
        return kidName;
    }

    public void setKidName(String kidName) {
        this.kidName = kidName;
    }

    public String getNgayNghi() {
        return ngayNghi;
    }

    public void setNgayNghi(String ngayNghi) {
        this.ngayNghi = ngayNghi;
    }

    public String getSoNgay() {
        return soNgay;
    }

    public void setSoNgay(String soNgay) {
        this.soNgay = soNgay;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public DonNghiPhep(String userId, String parentName, String kidName, String ngayNghi, String soNgay, String lyDo) {
        this.userId = userId;
        this.parentName = parentName;
        this.kidName = kidName;
        this.ngayNghi = ngayNghi;
        this.soNgay = soNgay;
        this.lyDo = lyDo;
    }

    public DonNghiPhep() {
    }
}
