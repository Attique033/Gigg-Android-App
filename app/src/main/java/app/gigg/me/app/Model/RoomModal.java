package app.gigg.me.app.Model;

public class RoomModal {

    String id, message, sid, rid, time, date, name, pic, image;

    public RoomModal() {
    }

    public RoomModal(String id, String message, String sid, String rid, String time, String date, String name, String pic, String image) {
        this.id = id;
        this.message = message;
        this.sid = sid;
        this.rid = rid;
        this.time = time;
        this.date = date;
        this.name = name;
        this.pic = pic;
        this.image = image;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
