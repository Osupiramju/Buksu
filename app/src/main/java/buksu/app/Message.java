package buksu.app;

import androidx.appcompat.app.AppCompatActivity;

public class Message extends AppCompatActivity {

    private int sentTo, sentBy;
    private String msg;

    public Message() { }

    public int getSentTo() { return sentTo; }

    public void setSentTo(int sendTo) { this.sentTo = sendTo; }

    public int getSentBy() { return sentBy; }

    public void setSentBy(int sendBy) { this.sentBy = sendBy; }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }

}
