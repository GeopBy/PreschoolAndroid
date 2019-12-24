package com.example.preschool.Menu;

public class Menu {
    private String iSang;
    private String iTrua;
    private String iChieu;

    public Menu(String iSang, String iTrua, String iChieu) {
        this.iSang = iSang;
        this.iTrua = iTrua;
        this.iChieu = iChieu;
    }

    public Menu() {
    }

    public String getiSang() {
        return iSang;
    }

    public void setiSang(String iSang) {
        this.iSang = iSang;
    }

    public String getiTrua() {
        return iTrua;
    }

    public void setiTrua(String iTrua) {
        this.iTrua = iTrua;
    }

    public String getiChieu() {
        return iChieu;
    }

    public void setiChieu(String iChieu) {
        this.iChieu = iChieu;
    }
}
