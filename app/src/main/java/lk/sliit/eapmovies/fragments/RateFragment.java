package lk.sliit.eapmovies.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import lk.sliit.eapmovies.HttpSingleton;
import lk.sliit.eapmovies.R;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Fragment to get movie rates of theatres
 */
public class RateFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private OnFragmentInteractionListener mListener;
    private Spinner theatreSpinner;
    private Spinner showTimeSpinner;
    private TextView dateText;
    private LinearLayout table;
    private TextView movieTitle;
    private ArrayAdapter<CharSequence> showTimeAdapter;
    private List<CharSequence> showTimeList = new ArrayList<>();
    private Map<String, String> showTimeIdMap = new ArrayMap<>();
    private String selectedTId;

    public RateFragment() {
    }


    public static RateFragment newInstance() {
        RateFragment fragment = new RateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rate, container, false);

        table = view.findViewById(R.id.movie_rate_table);
        dateText = view.findViewById(R.id.dateText);
        movieTitle = view.findViewById(R.id.movie_title);
        final Button dateSelectBtn = view.findViewById(R.id.date_select_btn);

        dateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        String m = (month < 10) ? ("0" + Integer.toString(month)) : (Integer.toString(month));
                        String d = (dayOfMonth < 10) ? ("0" + Integer.toString(dayOfMonth)) : (Integer.toString(dayOfMonth));


                        dateText.setText(year + "-" + m + "-" + d);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        theatreSpinner = view.findViewById(R.id.theatre_spinner);

        ArrayAdapter<CharSequence> theatreAdapter = ArrayAdapter.createFromResource(getContext(), R.array.theatres_array, android.R.layout.simple_spinner_item);
        theatreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        theatreSpinner.setAdapter(theatreAdapter);
        theatreSpinner.setOnItemSelectedListener(this);

        showTimeSpinner = view.findViewById(R.id.show_spinner);

        showTimeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, showTimeList);
        showTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        showTimeSpinner.setAdapter(showTimeAdapter);
        showTimeSpinner.setOnItemSelectedListener(this);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        if (parent.getId() == theatreSpinner.getId()) {
            Object item = parent.getItemAtPosition(pos);
            getShowTimes(item.toString());
        }

        if (parent.getId() == showTimeSpinner.getId()) {
            Object item = parent.getItemAtPosition(pos);
            getMovieRate(item.toString());
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void getShowTimes(String value) {
        Map<String, String> theatreIds = new ArrayMap<>();
        theatreIds.put("Savoy 2 - Wellawatte", "2");
        theatreIds.put("Savoy 3D Dolby ATMOS - Wellawatte", "3");
        theatreIds.put("Concord - Dehiwala", "7");
        theatreIds.put("Cinemax 3D - Jaela", "8");
        theatreIds.put("Willmax 3D - Anuradhapura", "10");
        theatreIds.put("Excel 3D - Colombo 10", "11");
        theatreIds.put("Queens 3D - Galle", "12");
        theatreIds.put("Jothi 3D - Rathnapura", "14");
        theatreIds.put("Sinexpo 3D - Kurunegala", "15");
        theatreIds.put("Samantha - Dematagoda", "16");
        theatreIds.put("Regal - Ambalangoda", "19");
        theatreIds.put("Tower - Moratuwa", "20");
        theatreIds.put("Savoy Premiere (Roxy Cinema) - Wellawatte", "208");
        theatreIds.put("Savoy Premier Rajagiriya - Rajagiriya", "238");
        theatreIds.put("Savoy Metro - Gampaha - Gampaha", "242");

        String id = theatreIds.get(value);
        selectedTId = id;

        final RequestQueue queue = HttpSingleton.getInstance(getContext()).getRequestQueue();
        String baseUrl = getString(R.string.base_url);
        String url = baseUrl + "/movie-rate/show-time/" + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray showTimes = new JSONArray(response);

                    showTimeList.clear();

                    for (int i = 0; i < showTimes.length(); i++) {
                        JSONArray tuple = showTimes.getJSONArray(i);
                        showTimeIdMap.put(tuple.getString(1), tuple.getString(0));
                        showTimeList.add(tuple.getString(1));
                    }

                    showTimeAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        queue.add(request);
    }

    public void getMovieRate(String value) {
        String sid = showTimeIdMap.get(value);
        String date = (String) dateText.getText();
        String tid = selectedTId;

        final RequestQueue queue = HttpSingleton.getInstance(getContext()).getRequestQueue();
        String baseUrl = getString(R.string.base_url);
        String url = baseUrl + "/movie-rate/rate?sid=" + sid + "&tid=" + tid + "&date=" + date;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                table.removeAllViews();

                try {
                    JSONArray rates = new JSONArray(response);

                    if (rates.length() < 2) {
                        Toast.makeText(getContext(), "No Results found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0; i < rates.length(); i++) {
                        LinearLayout row = new LinearLayout(getContext());
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        row.setOrientation(LinearLayout.HORIZONTAL);

                        JSONArray tableRow = rates.getJSONArray(i);

                        for (int j = 0; j < tableRow.length(); j++) {
                            TextView textView = new TextView(getContext());

                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                            textView.setPadding(8, 8, 8, 8);

                            textView.setText(tableRow.getString(j));


                            row.addView(textView);
                        }

                        table.addView(row);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        String url2 = baseUrl + "/movie-rate/movie-name?sid=" + sid + "&tid=" + tid + "&date=" + date;
        StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                movieTitle.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        queue.add(request);
        queue.add(request2);
    }

}
