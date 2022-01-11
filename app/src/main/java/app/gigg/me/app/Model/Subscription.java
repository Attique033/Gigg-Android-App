package app.gigg.me.app.Model;

public class Subscription {
    private String SKU;
    private boolean isSubscribed;

    public Subscription(String SKU, boolean isSubscribed) {
        this.SKU = SKU;
        this.isSubscribed = isSubscribed;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }
}
