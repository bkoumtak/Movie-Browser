package bkoumtak.udacity.moviebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
                String baseURL = "http://image.tmdb.org/t/p/w185";
                Movie movieClicked = (Movie) intent.getSerializableExtra(PosterFragment.EXTRA_MOVIE);
                String movie_title = movieClicked.title;
                String release_date = movieClicked.release_date;
                String vote_avg = movieClicked.vote_avg;
                String synopsis = movieClicked.synopsis;

                ImageView movie_poster = (ImageView)rootView.findViewById(R.id.info_poster);

                ((TextView)rootView.findViewById(R.id.movie_title)).setText(movie_title);
                Picasso.with(getActivity()).load(baseURL + movieClicked.poster_reference)
                        .into(movie_poster);

                ((TextView)rootView.findViewById(R.id.txt_date)).setText("Release Date: \n" + release_date);

                ((TextView)rootView.findViewById(R.id.txt_vote_avg)).setText("Movie Rating: \n"
                                            + vote_avg + "/10");

                ((TextView)rootView.findViewById(R.id.txt_synopsis)).setText(synopsis);

            }

            return rootView;
        }
    }
}
