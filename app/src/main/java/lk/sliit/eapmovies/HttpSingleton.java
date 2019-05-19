package lk.sliit.eapmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 *
 * Singleton instance to manage network
 * requests using `Volley`
 *
 * Author : Gigarthan Vijayakumaran
 *
 */
public class HttpSingleton {
    private static HttpSingleton ourInstance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    public static synchronized HttpSingleton getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new HttpSingleton(context);
        }
        return ourInstance;
    }

    private HttpSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

//  Return Request Queue Instance
    public RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }
//  Add Requests to the Queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
//  Return ImageLoader Instance
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
