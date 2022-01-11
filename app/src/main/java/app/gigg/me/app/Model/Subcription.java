package app.gigg.me.app.Model;

public class Subcription {
    private int id;
    private String title;
    private String tag;
    private String discount;
    private String price;
    private String color;
    private String SKU;
    private String withdrawal;

    public Subcription(int id, String title, String tag, String discount, String price, String color, String SKU, String withdrawal) {
        this.id = id;
        this.title = title;
        this.tag = tag;
        this.discount = discount;
        this.price = price;
        this.color = color;
        this.SKU = SKU;
        this.withdrawal = withdrawal;
    }

    public String getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(String withdrawal) {
        this.withdrawal = withdrawal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }
}
