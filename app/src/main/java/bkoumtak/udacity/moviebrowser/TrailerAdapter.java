package bkoumtak.udacity.moviebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kondeelai on 2016-12-10.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_layout,
                                              parent, false);
        }

        TextView trailer_text = (TextView)convertView.findViewById(R.id.trailer_txt);
        trailer_text.setText("Trailer " + (position + 1));

        return convertView;
    }
}
