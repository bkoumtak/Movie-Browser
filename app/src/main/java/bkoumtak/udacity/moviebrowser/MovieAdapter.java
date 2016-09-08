package bkoumtak.udacity.moviebrowser;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by kondeelai on 2016-07-22.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private boolean offlineMode = false;

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

            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder holder = (ViewHolder)convertView.getTag();

        //ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster);

        String fullURL = baseURL + movie.poster_reference;

        Log.v(LOG_TAG, fullURL);

        if (!offlineMode) {
            Picasso.with(getContext()).load(fullURL).into(holder.posterView);
        }
        else{
            InputStream bitmap = null;
            try{
                bitmap = getContext().getAssets().open(movie.jpeg_file);
                Bitmap bit = BitmapFactory.decodeStream(bitmap);
                holder.posterView.setImageBitmap(bit);
            } catch(IOException e){
                e.printStackTrace();
            } finally{
                if(bitmap != null)
                    try{
                        bitmap.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        return convertView;
    }

    public static class ViewHolder{
        ImageView posterView;

        public ViewHolder(View view){
            posterView = (ImageView)view.findViewById(R.id.movie_poster);
            //Bitmap bitmap = ((BitmapDrawable)posterView.getDrawable()).getBitmap();
        }
    }

    public void setOfflineMode(boolean offlineMode)
    {
        this.offlineMode = offlineMode;
    }

}
