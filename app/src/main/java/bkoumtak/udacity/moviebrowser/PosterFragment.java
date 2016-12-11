package bkoumtak.udacity.moviebrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kondeelai on 2016-07-21.
 */
public class PosterFragment extends Fragment {
    static final String EXTRA_MOVIE = "bkoumtak.udacity.moviebrowser.EXTRA_MOVIE";

    private MovieAdapter mMovieAdapter;
    private boolean justStarted = true;

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
            mMovieAdapter.setOfflineMode(false);
            updateMovieList();
        } else{
            if (!justStarted)
                Toast.makeText(getActivity(), "Network Not Found, Cannot Update Data", Toast.LENGTH_LONG).show();
        }

        justStarted = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.poster_fragment, container, false);

        GetPosterTask posterTask = new GetPosterTask();

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        gridView.setAdapter(mMovieAdapter);
        // Delete below if it does not work

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

        if(!isOnline()) {
            String[] sample_jpeg = {"suicide_squad.jpg", "jason_bourne.jpg", "now_you_see_me_2.jpg",
            "civil_war.jpg", "boy_next_door.jpg", "mechanic_resurrection.jpg", "jungle_book.jpg",
            "batman_vs_superman.jpg", "mad_max.jpg", "mockingjay_part1.jpg", "jurassic_world.jpg",
            "interstellar.jpg", "furious_7.jpg", "deadpool.jpg", "maze_runner.jpg", "conjuring_2.jpg",
            "terminator_genisys.jpg", "guardians_of_the_galaxy.jpg", "neighbors_2.jpg", "fury.jpg"};

            Toast.makeText(getActivity(), "Network Not Found, Loading Sample List", Toast.LENGTH_LONG).show();
            AssetManager am = getContext().getAssets();

            //File file  = new File("movieJSON.txt");

            // Read text from file
            StringBuilder text = new StringBuilder();

            try{
                InputStream is = am.open("movieJSON.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }

                br.close();
            } catch (IOException e){
                Log.e("Error","Error", e);
            }

            ArrayList<Movie> movies = new ArrayList<>();

            try {
                movies = getMoviePoster(text.toString());
            } catch(JSONException e){
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < movies.size(); i++){
                Log.v("Movies Offline:", movies.get(i).title);
            }

            for (int i = 0; i < movies.size(); i++){
                movies.get(i).jpeg_file = sample_jpeg[i];
            }

            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
            mMovieAdapter.setOfflineMode(true);
            mMovieAdapter.notifyDataSetChanged();
        }


        return rootView;
    }

    public class GetPosterTask extends AsyncTask<String, Void, ArrayList<Movie>>{
        private final String LOG_TAG = GetPosterTask.class.getSimpleName();
        private final String URL_PATH = "https://api.themoviedb.org/3/movie/";
        private final String API_KEY = "c7520be353d2a89927b9b5d021cc2d03";
        private final String POP = "popular";
        private final String TOP_RATED = "top_rated";

        @Override
        protected void onPostExecute(ArrayList<Movie> list_of_movies) {

            List<Movie> movieList;
            movieList = list_of_movies;
            int numAttributes = 5;


            if (list_of_movies != null && list_of_movies.size() == 20) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movieList);
                mMovieAdapter.notifyDataSetChanged();

                Log.v(LOG_TAG, "" + movieList.size());
            }

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            ArrayList<Movie> movies;
            movies = new ArrayList<Movie>();
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

                //-------------------------------------------------------------------------------


                movies = getMoviePoster(movieJSONstr);


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

            return movies;
        }
    }

    private ArrayList<Movie> getMoviePoster(String movieJSONstr) throws JSONException, MalformedURLException {
        int numMovies = 20;
        final String MDB_RESULTS = "results";
        final String MDB_POSTERPATH = "poster_path";
        final String MDB_TITLE = "title";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_VOTE_AVG = "vote_average";
        final String MDB_SYNOPSIS = "overview";
        final String MDB_ID = "id";

        // For getting the trailers
        final String URL_PATH = "https://api.themoviedb.org/3/movie/";
        final String API_KEY = "c7520be353d2a89927b9b5d021cc2d03";
        final String API_QUERY = "api_key";
        final String LANGUAGE_QUERY = "language";
        final String LANGUAGE = "en-US";
        final String VIDEOS = "videos";

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;

        String trailerJSON = null;


        JSONObject movieJSON = new JSONObject(movieJSONstr);
        JSONArray resultsArray  = movieJSON.getJSONArray(MDB_RESULTS);

        ArrayList<Movie> movies = new ArrayList<Movie>();

        for (int i = 0; i < numMovies; i++){


            JSONObject movieData = resultsArray.getJSONObject(i);
            String poster_ref = movieData.getString(MDB_POSTERPATH);
            String title = movieData.getString(MDB_TITLE);
            String release_date = movieData.getString(MDB_RELEASE_DATE);
            String vote_avg = Double.toString(movieData.getDouble(MDB_VOTE_AVG));
            String synopsis = movieData.getString(MDB_SYNOPSIS);

            Uri.Builder builder = Uri.parse(URL_PATH).buildUpon();
            builder.appendPath(Integer.toString(movieData.getInt(MDB_ID)));
            builder.appendPath(VIDEOS);
            builder.appendQueryParameter(API_QUERY, API_KEY);
            builder.appendQueryParameter(LANGUAGE_QUERY, LANGUAGE);

            URL url = new URL(builder.build().toString());

            try{
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

                trailerJSON = buffer.toString();

            } catch(IOException e){
                Log.e("getMoviePoster", "Error", e);
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("getMoviePoster", "Error closing stream", e);
                    }
                }
            }

            //Log.v("Testing ID Call: ", trailerJSON);

            String[] trailers;
            trailers = getTrailers(trailerJSON);

            String list_of_trailers;
            list_of_trailers = "";
            for(int j = 0; j < trailers.length; j++){
                list_of_trailers += trailers[j] +" ";
            }
            Log.v("Testing Trailers", list_of_trailers);
            movies.add(new Movie(poster_ref, title, release_date, vote_avg, synopsis, trailers));
        }

        return movies;

    }

    public String[] getTrailers(String trailerJSON){
        String[] trailers = {};
        final String resultsStr = "results";
        final String trailerKey = "key";

        try {
            JSONObject trailerJSONObj = new JSONObject(trailerJSON);
            JSONArray trailerJSONArray;
            trailerJSONArray = trailerJSONObj.getJSONArray(resultsStr);
            trailers = new String[trailerJSONArray.length()];
            for(int i = 0; i < trailerJSONArray.length(); i++){
                JSONObject trailerObj = trailerJSONArray.getJSONObject(i);
                trailers[i] = trailerObj.getString(trailerKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailers;
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
