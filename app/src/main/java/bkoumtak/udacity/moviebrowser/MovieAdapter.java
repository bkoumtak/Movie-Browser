package bkoumtak.udacity.moviebrowser;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kondeelai on 2016-07-22.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies){
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String baseURL = "http://image.tmdb.org/t/p/w185";
        Movie movie = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_poster,
                    parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster);

        String fullURL = baseURL + movie.poster_reference;

        Log.v(LOG_TAG, fullURL);
        Picasso.with(getContext()).load(fullURL).into(posterView);

        return convertView;
    }
}
