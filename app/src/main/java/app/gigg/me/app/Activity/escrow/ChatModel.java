package app.gigg.me.app.Activity.escrow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

/**
 * Created by kishon on 16,September,2021
 */
public class ChatModel {
    private int id;
    private String name;
    private String message;
    private String time;
    private String unread;

    public ChatModel() {
    }

    public ChatModel(int id, String name, String message, String time, String unread) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.time = time;
        this.unread = unread;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public static DiffUtil.ItemCallback<ChatModel> itemCallback = new DiffUtil.ItemCallback<ChatModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatModel oldItem, @NonNull ChatModel newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatModel oldItem, @NonNull ChatModel newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatModel chatModel = (ChatModel) o;
        return id == chatModel.id && Objects.equals(name, chatModel.name) && Objects.equals(message, chatModel.message) && Objects.equals(time, chatModel.time) && Objects.equals(unread, chatModel.unread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, message, time, unread);
    }

    @Override
    public String toString() {
        return "ChatModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", unread='" + unread + '\'' +
                '}';
    }
}
