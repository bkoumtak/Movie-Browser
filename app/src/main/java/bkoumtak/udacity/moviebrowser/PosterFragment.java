package bkoumtak.udacity.moviebrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kondeelai on 2016-07-21.
 */
public class PosterFragment extends Fragment {
    static final String EXTRA_MOVIE = "bkoumtak.udacity.moviebrowser.EXTRA_MOVIE";

    private MovieAdapter mMovieAdapter;

    public PosterFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart(){
        super.onStart();

        if (isOnline()) {
            updateMovieList();
        } else{
            Toast.makeText(getActivity(), "Network Not Found, Cannot Update Data", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.poster_fragment, container, false);

        GetPosterTask posterTask = new GetPosterTask();

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        gridView.setAdapter(mMovieAdapter);

        if(isOnline()) {
            posterTask.execute();


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String title = mMovieAdapter.getItem(i).title;
                    Toast.makeText(getActivity(), title, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    intent.putExtra(EXTRA_MOVIE, mMovieAdapter.getItem(i));

                    startActivity(intent);
                }
            });
        } else{
            Toast.makeText(getActivity(), "Network Not Found, Cannot Update Data", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    public class GetPosterTask extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = GetPosterTask.class.getSimpleName();
        private final String URL_PATH = "https://api.themoviedb.org/3/movie/";
        private final String API_KEY = "";
        private final String POP = "popular";
        private final String TOP_RATED = "top_rated";

        @Override
        protected void onPostExecute(String[] strings) {
            int numAttributes = 5;
            if (strings != null){
                List<Movie> movieList = new ArrayList<Movie>();

                for (int i = 0; i < strings.length/numAttributes; i++){
                    movieList.add(new Movie(strings[i*numAttributes], strings[i*numAttributes+1],
                            strings[i*numAttributes+2], strings[i*numAttributes+3],
                            strings[i*numAttributes+4]));
                }



                mMovieAdapter.clear();
                mMovieAdapter.addAll(movieList);
                mMovieAdapter.notifyDataSetChanged();

                Log.v(LOG_TAG, ""+movieList.size());
            }
        }

        @Override
        protected String[] doInBackground(String... params) {
            //String SORT_PARAM = "popularity.desc";
            if (params.length == 0){
                return null;
            }

            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            String movieJSONstr = null;
            String[] moviePosters = {};

            String sort_pref = params[0];



            try {
                // final String QUERY_SORT = "sort_by";
                final String QUERY_KEY = "api_key";

                Uri.Builder builder = Uri.parse(URL_PATH).buildUpon();

                if (sort_pref.equals("POPULARITY")){
                    builder.appendPath(POP);
                } else if (sort_pref.equals("RATING")){
                    builder.appendPath(TOP_RATED);
                }
                builder.appendQueryParameter(QUERY_KEY, API_KEY);

                String urlString = builder.build().toString();

                URL url = new URL(urlString);

                // Send request to themoviedb and open connection
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    movieJSONstr = null;
                }

                movieJSONstr = buffer.toString();

                moviePosters = getMoviePoster(movieJSONstr);

            } catch(IOException e){
                Log.e(LOG_TAG, "Error", e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return moviePosters;
        }
    }

    private String[] getMoviePoster(String movieJSONstr) throws JSONException{

        int numMovies = 20;
        int numAttributes = 5;
        final String MDB_RESULTS = "results";
        final String MDB_POSTERPATH = "poster_path";
        final String MDB_TITLE = "title";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_VOTE_AVG = "vote_average";
        final String MDB_SYNOPSIS = "overview";

        JSONObject movieJSON = new JSONObject(movieJSONstr);
        JSONArray resultsArray  = movieJSON.getJSONArray(MDB_RESULTS);

        String[] movie_posters = new String[numMovies*numAttributes];

        for (int i = 0; i < numMovies; i++){
            JSONObject movieData = resultsArray.getJSONObject(i);

            movie_posters[i*numAttributes] = movieData.getString(MDB_POSTERPATH);
            movie_posters[i*numAttributes+1] = movieData.getString(MDB_TITLE);
            movie_posters[i*numAttributes+2] = movieData.getString(MDB_RELEASE_DATE);
            movie_posters[i*numAttributes+3] = Double.toString(movieData.getDouble(MDB_VOTE_AVG));
            movie_posters[i*numAttributes+4] = movieData.getString(MDB_SYNOPSIS);
        }

        return movie_posters;

    }

    public void updateMovieList(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortValue = sharedPref.getString(getString(R.string.sort_key), "POPULARITY");

        GetPosterTask getPosterTask = new GetPosterTask();

        getPosterTask.execute(sortValue);

        if(sortValue.equals("POPULARITY")){
            Toast.makeText(getActivity(), "Popular Movies", Toast.LENGTH_LONG).show();
        } else if(sortValue.equals("RATING")){
            Toast.makeText(getActivity(), "Top Rated Movies", Toast.LENGTH_LONG).show();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
