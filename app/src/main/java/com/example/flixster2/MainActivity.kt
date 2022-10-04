package com.example.flixster2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flixster2.databinding.ActivityMainBinding
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.json.Json
import okhttp3.Headers
import org.json.JSONException

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

private const val TAG = "MainActivity/"
private const val SEARCH_API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
private const val MOVIE_SEARCH_URL =
    "https://api.themoviedb.org/3/movie/popular?api_key=34e61e3ddd6944220f1e09bcfb1b726d"

//private const val ARTICLE_SEARCH_URL =
//    "https://api.themoviedb.org/3/tv/popular?api_key=34e61e3ddd6944220f1e09bcfb1b726d"

//private const val SEARCH_API_KEY = "IkJpYIpSDBXK2zqqUtV8ANNQ35YqzopN"
//private const val ARTICLE_SEARCH_URL =
//    "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=${SEARCH_API_KEY}"

class MainActivity : AppCompatActivity() {
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        moviesRecyclerView = findViewById(R.id.articles)
        // TODO: Set up ArticleAdapter with articles

        val movieAdapter = MovieAdapter(this, movies)
        moviesRecyclerView.adapter = movieAdapter

        moviesRecyclerView.layoutManager = LinearLayoutManager(this).also {
            val dividerItemDecoration = DividerItemDecoration(this, it.orientation)
            moviesRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        moviesRecyclerView.layoutManager = GridLayoutManager(this, 2)

        val progressBar = view.findViewById<View>(R.id.progress) as ContentLoadingProgressBar

        progressBar.show()

        val client = AsyncHttpClient()
        client.get(MOVIE_SEARCH_URL, object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {

                progressBar.hide()
                Log.e(TAG, "Failed to fetch articles: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                progressBar.hide()
                Log.i(TAG, "Successfully fetched articles: $json")
                try {
                    // TODO: Create the parsedJSON
                    // Do something with the returned json (contains article information)
                    val parsedJson = createJson().decodeFromString(
                        Response.serializer(),
                        json.jsonObject.toString()
                    )

                    Log.v("JSON", parsedJson.toString())

                    // TODO: Do something with the returned json (contains article information)
                    parsedJson.results?.let { list ->
                        movies.addAll(list)
                    }

                    // TODO: Save the articles and reload the screen
                    parsedJson.results?.let { list ->
                        movies.addAll(list)

                        // Reload the screen
                        movieAdapter.notifyDataSetChanged()
                    }

                } catch (e: JSONException) {
                    Log.e(TAG, "Exception: $e")
                }
            }

        })

    }
}