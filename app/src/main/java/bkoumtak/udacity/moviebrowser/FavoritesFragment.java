package bkoumtak.udacity.moviebrowser;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import bkoumtak.udacity.moviebrowser.data.FavoritesColumns;
import bkoumtak.udacity.moviebrowser.data.FavoritesProvider;

/**
 * Created by kondeelai on 2017-01-12.
 */
public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static String[] FROM_COLUMNS = {
            FavoritesColumns.TITLE,
            FavoritesColumns.RATING,
            FavoritesColumns.SYNOPSIS
    };

    private final static int[] TO_IDS = {
            R.id.favorites_title,
            R.id.favorites_rating,
            R.id.favorites_synopsis
    };

    private static final String[] PROJECTION =
            {
                    FavoritesColumns._ID,
                    FavoritesColumns.TITLE,
                    FavoritesColumns.RATING,
                    FavoritesColumns.SYNOPSIS
            };

    private static final int FAVORITES_TITLE = 0;
    private static final int FAVORITES_RATING = 1;
    private static final int FAVORITES_SYNOPSIS = 2;


    ListView mFavoritesList;

    Uri mFavoritesUri;
    private SimpleCursorAdapter mCursorAdapter;


    public FavoritesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_list_view, container, false);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFavoritesUri = FavoritesProvider.Favorites.CONTENT_URI;

        mFavoritesList = (ListView) getActivity().findViewById(R.id.favorites_list_view);

        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.favorites_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);

        mFavoritesList.setAdapter(mCursorAdapter);


        // Initialize the Loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                mFavoritesUri,
                PROJECTION,
                null,
                null,
                null

        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
