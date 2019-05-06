package lk.sliit.eapmovies;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import lk.sliit.eapmovies.fragments.MovieFragment;
import lk.sliit.eapmovies.fragments.NowShowingFragment;
import lk.sliit.eapmovies.fragments.OfferFragment;
import lk.sliit.eapmovies.fragments.RateFragment;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener, OfferFragment.OnListFragmentInteractionListener, RateFragment.OnFragmentInteractionListener, NowShowingFragment.OnListFragmentInteractionListener {

    private TextView mTextMessage;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return setActiveFragment(item.getItemId());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int id = navigation.getSelectedItemId();
        setActiveFragment(id);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    /**
     * Load the intended fragment into the frame layout in the main activity
     *
     * @param fragment
     */
    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void onListFragmentInteraction() {

    }

    private boolean setActiveFragment(int id) {
        switch (id) {
            case R.id.navigation_home:
                loadFragment(MovieFragment.newInstance(2));
                return true;
            case R.id.navigation_dashboard:
                loadFragment(RateFragment.newInstance());
                return true;
            case R.id.navigation_notifications:
                loadFragment(OfferFragment.newInstance(1));
                return true;
            case R.id.navigation_nowshowing:
                loadFragment(NowShowingFragment.newInstance(2));
                return true;
        }
        return false;
    }
}
