package app.gigg.me.app.Model;

public class MessageModal {

    String id, message, sid, rid, time, date, image,name;

    public MessageModal(String id, String message, String sid, String rid, String time, String date, String image) {
        this.id = id;
        this.message = message;
        this.sid = sid;
        this.rid = rid;
        this.time = time;
        this.date = date;
        this.image = image;
    }

    public MessageModal() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageModal( String message,String id, String sid, String rid, String time, String date, String image, String name) {
        this.id = id;
        this.message = message;
        this.sid = sid;
        this.rid = rid;
        this.time = time;
        this.date = date;
        this.image = image;
    }

    public MessageModal(String message, String sid, String time, String date, String image,String name) {
        this.message = message;
        this.sid = sid;
        this.time = time;
        this.date = date;
        this.image = image;
        this.name = name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
