package bkoumtak.udacity.moviebrowser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bkoumtak.udacity.moviebrowser.data.FavoritesColumns;
import bkoumtak.udacity.moviebrowser.data.FavoritesProvider;

/**
 * Created by kondeelai on 2016-07-29.
 */
public class InfoActivity extends ActionBarActivity{
    InfoMovieAdapter infoMovieAdapter;

    ViewPager mInfoPager;

    public InfoActivity () {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        setContentView(R.layout.info_pager);

        mInfoPager = (ViewPager)findViewById(R.id.info_pager);

        infoMovieAdapter = new InfoMovieAdapter(getSupportFragmentManager());

        mInfoPager.setAdapter(infoMovieAdapter);*/

        setContentView(R.layout.activity_info);


        if (savedInstanceState == null){

            Bundle args = new Bundle();
            args.putSerializable(PosterFragment.EXTRA_MOVIE, getIntent().getSerializableExtra(
                    PosterFragment.EXTRA_MOVIE
            ));

            InfoFragment infoFragment = new InfoFragment();
            infoFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.info_container, infoFragment)
                    .commit();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        findViewById(R.id.scroll_info).scrollTo(0,0);
    }

    /*
    public void getReviews(View view) {
        Intent review_intent = new Intent(this, ReviewActivity.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PosterFragment.EXTRA_MOVIE)){
            Movie movieClicked = (Movie) intent.getSerializableExtra(PosterFragment.EXTRA_MOVIE);
            review_intent.putExtra(PosterFragment.EXTRA_MOVIE, movieClicked);

            startActivity(review_intent);
        }

    }*/



    public static class InfoFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            View rootView = inflater.inflate(R.layout.info_fragment, container, false);
            //Intent intent = getActivity().getIntent();

            Bundle args = getArguments();


            if (args != null){
                ListView trailerListView;
                String baseURL = "http://image.tmdb.org/t/p/w185";
                final Movie movieClicked = (Movie) args.getSerializable(PosterFragment.EXTRA_MOVIE);
                final String movie_title = movieClicked.title;
                String release_date = movieClicked.release_date;
                final String vote_avg = movieClicked.vote_avg;
                final String synopsis = movieClicked.synopsis;
                String[] trailers = movieClicked.trailers;
                Review[] reviews = movieClicked.reviews;

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

                ImageButton favoritesButton = (ImageButton)rootView.findViewById(R.id.favorites_button);


                Button reviewButton = (Button)rootView.findViewById(R.id.btn_reviews);
                if(reviewButton != null) {
                    reviewButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getReviews(movieClicked);
                        }
                    });
                }

                favoritesButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Uri favoritesUri = FavoritesProvider.Favorites.CONTENT_URI;
                        String[] PROJECTION = {FavoritesColumns.TITLE};
                        String SELECTION = FavoritesColumns.TITLE + " LIKE ?";
                        String[] selectionArgs = {movie_title};


                        Cursor cursor = getContext().getContentResolver().query(favoritesUri,
                                PROJECTION,
                                SELECTION,
                                selectionArgs,
                                null);

                        if (cursor.getCount() == 0) {
                            Toast.makeText(getContext(), movie_title + " added to favorites!", Toast.LENGTH_LONG).show();

                            ContentValues contentValues = new ContentValues();

                            contentValues.put(FavoritesColumns.TITLE, movie_title);
                            contentValues.put(FavoritesColumns.RATING, vote_avg);
                            contentValues.put(FavoritesColumns.SYNOPSIS, synopsis);

                            getContext().getContentResolver().insert(favoritesUri,
                                    contentValues);
                        } else{
                            getContext().getContentResolver().delete(favoritesUri,
                                    FavoritesColumns.TITLE + " = ?", selectionArgs);
                            Toast.makeText(getContext(), movie_title + " deleted from favorites",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            return rootView;
        }

        public void getReviews(Movie movieClicked){
            Intent review_intent = new Intent(getActivity(), ReviewActivity.class);
            review_intent.putExtra(PosterFragment.EXTRA_MOVIE, movieClicked);

            startActivity(review_intent);
        }
    }

    public static class ReviewFragment extends Fragment{
        public static final String REVIEW_NUMBER = "review_number";
        public static final String REVIEW_NULL = "review_null";
        public static final String TWOPANE_MODE = "two_pane";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.review_fragment, container, false);


            Movie movieClicked = (Movie) getArguments().getSerializable(PosterFragment.EXTRA_MOVIE);
            int index = 0;
            boolean twoPane = getArguments().getBoolean(TWOPANE_MODE);

            if (!twoPane)
                index = getArguments().getInt(REVIEW_NUMBER);

            boolean no_reviews = getArguments().getBoolean(REVIEW_NULL);

            if(twoPane){
                TextView review_content;
                review_content = (TextView) rootView.findViewById(R.id.review_content);
                if(!no_reviews){

                    for (int i = 0; i < movieClicked.reviews.length; i++){
                        review_content.append("Reviewer: " + movieClicked.reviews[i].author +
                                            "\n \n" + movieClicked.reviews[i].content +
                                            "\n \n");
                        review_content.append("------------------------------------------------" +
                                "--------------------------------------------------------------\n");
                    }

                } else{
                    review_content.setText("There are currently no reviews in the database.");
                }
            }
            else {
                if (!no_reviews) {
                    TextView review_content;
                    TextView review_author;

                    review_author = (TextView) rootView.findViewById(R.id.review_author);
                    review_content = (TextView) rootView.findViewById(R.id.review_content);
                    review_content.setText(movieClicked.reviews[index].content);
                    review_author.setText("Reviewer:  " + movieClicked.reviews[index].author);
                }
            }


            return rootView;
        }

    }
    public static class InfoMovieAdapter extends FragmentPagerAdapter {
        public InfoMovieAdapter(FragmentManager fm){ super(fm);}

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new InfoFragment();

                default:
                    return new ReviewFragment();
            }
        }

        @Override
        public int getCount() {return 2; }
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
