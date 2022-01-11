package app.gigg.me.app.Model;

public class ChatModal {
    String id, picture, name, last, time;

    public ChatModal() {
    }

    public ChatModal(String id, String picture, String name, String last, String time) {
        this.id = id;
        this.picture = picture;
        this.name = name;
        this.last = last;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatModal{" +
                "id='" + id + '\'' +
                ", picture='" + picture + '\'' +
                ", name='" + name + '\'' +
                ", last='" + last + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
