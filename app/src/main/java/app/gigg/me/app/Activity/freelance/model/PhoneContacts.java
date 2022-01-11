package app.gigg.me.app.Activity.freelance.model;

import android.graphics.Bitmap;

public class PhoneContacts {

    private String name;
    private String phone;
    private Bitmap image;


    public PhoneContacts(String name, String phone, Bitmap image) {
        this.name = name;
        this.phone = phone;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
