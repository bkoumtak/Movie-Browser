package bkoumtak.udacity.moviebrowser;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by kondeelai on 2017-01-12.
 */
public class FavoritesActivity extends ActionBarActivity {

    public FavoritesActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FavoritesFragment())
                    .commit();
        }
    }

}
