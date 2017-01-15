package bkoumtak.udacity.moviebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MovieBrowserMain extends ActionBarActivity implements PosterFragment.Callback{

    private static final String INFOFRAGMENT_TAG = "INFOTAG";
    private static final String REVIEWFRAGMENT_TAG = "REVIEWTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_browser_main);

        if(findViewById(R.id.info_container) != null){
            mTwoPane = true;

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.info_container, new Fragment(), INFOFRAGMENT_TAG)
                        .commit();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.review_container, new Fragment(), REVIEWFRAGMENT_TAG)
                        .commit();

                Toast.makeText(this, "Tablet Mode", Toast.LENGTH_LONG).show();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_browser_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_menu){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        else if (id == R.id.favorites_menu){
            Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
            startActivity(favoritesIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movieParam) {
        if(mTwoPane){
            // If the app is in two pane mode...
            Bundle args = new Bundle();
            args.putSerializable(PosterFragment.EXTRA_MOVIE, movieParam);

            InfoActivity.InfoFragment infoFragment = new InfoActivity.InfoFragment();
            infoFragment.setArguments(args);

            boolean no_reviews;

            if (movieParam.reviews == null)
                no_reviews = true;
            else
                no_reviews = false;

            Bundle review_args = new Bundle();
            review_args.putSerializable(PosterFragment.EXTRA_MOVIE, movieParam);
            review_args.putBoolean(InfoActivity.ReviewFragment.REVIEW_NULL, no_reviews);
            review_args.putBoolean(InfoActivity.ReviewFragment.TWOPANE_MODE, true);

            InfoActivity.ReviewFragment reviewFragment = new InfoActivity.ReviewFragment();
            reviewFragment.setArguments(review_args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.info_container, infoFragment, INFOFRAGMENT_TAG)
                    .commit();


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.review_container, reviewFragment, REVIEWFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra(PosterFragment.EXTRA_MOVIE, movieParam);
            startActivity(intent);
        }
    }
}
