package buksu.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class adapterSearchBook extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context context;
    String[] data;
    int[] valoration;

    public adapterSearchBook(Context context, String[] data, int[] valoration) {

        this.context = context;
        this.data = data;
        this.valoration = valoration;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final View view = inflater.inflate(R.layout.search_book_data, null);

        TextView bookData = view.findViewById(R.id.data2);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar2);
        bookData.setText(data[i]);
        ratingBar.setProgress(valoration[i]);

        return view;
    }

    @Override
    public int getCount() {
        return data.length;
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


