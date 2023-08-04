package com.angel.model;

public class Shirley {

    private String Image;

    private String vipUser;

    @Override
    public String toString() {
        return "Shirley{" +
                "Image='" + Image + '\'' +
                ", vipUser='" + vipUser + '\'' +
                '}';
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getVipUser() {
        return vipUser;
    }

    public void setVipUser(String vipUser) {
        this.vipUser = vipUser;
    }
}
