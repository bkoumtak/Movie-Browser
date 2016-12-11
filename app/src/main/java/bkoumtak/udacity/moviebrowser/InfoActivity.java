package bkoumtak.udacity.moviebrowser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kondeelai on 2016-07-29.
 */
public class InfoActivity extends ActionBarActivity{
    public InfoActivity () {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new InfoFragment())
                    .commit();
        }

    }

    public static class InfoFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.info_fragment, container, false);
            Intent intent = getActivity().getIntent();


            if (intent != null && intent.hasExtra(PosterFragment.EXTRA_MOVIE)){
                ListView trailerListView;
                String baseURL = "http://image.tmdb.org/t/p/w185";
                Movie movieClicked = (Movie) intent.getSerializableExtra(PosterFragment.EXTRA_MOVIE);
                String movie_title = movieClicked.title;
                String release_date = movieClicked.release_date;
                String vote_avg = movieClicked.vote_avg;
                String synopsis = movieClicked.synopsis;
                String[] trailers = movieClicked.trailers;
                final ArrayList<Trailer> trailerObjects = new ArrayList<Trailer>();

                ImageView movie_poster = (ImageView)rootView.findViewById(R.id.info_poster);

                ((TextView)rootView.findViewById(R.id.movie_title)).setText(movie_title);
                Picasso.with(getActivity()).load(baseURL + movieClicked.poster_reference)
                        .into(movie_poster);

                ((TextView)rootView.findViewById(R.id.txt_date)).setText("Release Date: \n" + release_date);

                ((TextView)rootView.findViewById(R.id.txt_vote_avg)).setText("Movie Rating: \n"
                                            + vote_avg + "/10");

                ((TextView)rootView.findViewById(R.id.txt_synopsis)).setText(synopsis);

                for(int i = 0; i < trailers.length; i++){
                    Log.v("Trailer: ", trailers[i]);
                    trailerObjects.add(i, new Trailer(movie_title, trailers[i]));
                }

                trailerListView = (ListView)rootView.findViewById(R.id.listview_trailers);

                trailerListView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });

                setListViewHeightBasedOnChildren(trailerListView);

                TrailerAdapter trailerAdapter= new TrailerAdapter(getContext(), trailerObjects);

                trailerListView.setAdapter(trailerAdapter);

                trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String yt_link = "http://www.youtube.com/watch?v="+
                                trailerObjects.get(i).youtube_link;
                        startActivity(new Intent(
                                Intent.ACTION_VIEW, Uri.parse(yt_link)

                        ));

                        Toast.makeText(getContext(), yt_link, Toast.LENGTH_LONG).show();
                    }
                });
            }

            return rootView;
        }
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
