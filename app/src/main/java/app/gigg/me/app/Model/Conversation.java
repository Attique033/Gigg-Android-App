package app.gigg.me.app.Model;

import java.util.ArrayList;

public class Conversation {
    ArrayList<ChatModal> messages;
    MessageModal lastMsg;

    public Conversation() {
    }

    public ArrayList<ChatModal> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ChatModal> messages) {
        this.messages = messages;
    }

    public MessageModal getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(MessageModal lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Conversation(ArrayList<ChatModal> messages, MessageModal lastMsg) {
        this.messages = messages;
        this.lastMsg = lastMsg;
    }
}
