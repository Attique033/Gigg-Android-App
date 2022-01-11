package app.gigg.me.app.Activity.freelance.model;

public class Client {
    public String environment;
    public String paypal_sdk_version;
    public String platform;
    public String product_name;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getPaypal_sdk_version() {
        return paypal_sdk_version;
    }

    public void setPaypal_sdk_version(String paypal_sdk_version) {
        this.paypal_sdk_version = paypal_sdk_version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
