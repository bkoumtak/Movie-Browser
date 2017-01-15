package bkoumtak.udacity.moviebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ReviewActivity extends AppCompatActivity{
    ReviewActivityPagerAdapter mReviewActivityPagerAdapter;
    ViewPager mViewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();



        if (intent != null && intent.hasExtra(PosterFragment.EXTRA_MOVIE)) {
            Movie movieClicked = (Movie) intent.getSerializableExtra(PosterFragment.EXTRA_MOVIE);
            boolean no_reviews;

            if (movieClicked.reviews == null)
                no_reviews = true;
            else
                no_reviews = false;

            mReviewActivityPagerAdapter = new ReviewActivityPagerAdapter(getSupportFragmentManager(),
                        movieClicked, no_reviews);


            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mViewPager.setAdapter(mReviewActivityPagerAdapter);


            // Specify that the Home/Up button should not be enabled, since there is no hierarchical
            // parent.

            // Specify that we will be displaying tabs in the action bar.

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

        }


    }


    public static class ReviewActivityPagerAdapter extends FragmentPagerAdapter {
        Movie movieClicked;
        boolean no_reviews;

        public ReviewActivityPagerAdapter(FragmentManager fm, Movie movieClicked,
                    boolean no_reviews) {
            super(fm);
            this.movieClicked = movieClicked;
            this.no_reviews = no_reviews;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                default:
                    Fragment fragment = new InfoActivity.ReviewFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(PosterFragment.EXTRA_MOVIE, movieClicked);
                    args.putInt(InfoActivity.ReviewFragment.REVIEW_NUMBER, position);
                    args.putBoolean(InfoActivity.ReviewFragment.REVIEW_NULL, no_reviews);
                    args.putBoolean(InfoActivity.ReviewFragment.TWOPANE_MODE, false);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount(){
            if (no_reviews)
                return 1;
            else
                return movieClicked.reviews.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (no_reviews)
                return "There are currently no reviews in this database";
            else
                return "Review " + (position + 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
