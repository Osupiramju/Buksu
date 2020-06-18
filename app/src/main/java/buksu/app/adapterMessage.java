package buksu.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class adapterMessage extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context context;
    String[] msg;
    String[] nick;

    public adapterMessage(Context context, String[] msg, String[] nick) {

        this.context = context;
        this.msg = msg;
        this.nick = nick;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final View view = inflater.inflate(R.layout.msg_data, null);

        TextView msgData = view.findViewById(R.id.msgData);
        TextView nickData = view.findViewById(R.id.nickData);
        msgData.setText(msg[i]);
        nickData.setText(nick[i]);

        return view;
    }

    @Override
    public int getCount() {
        return msg.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
