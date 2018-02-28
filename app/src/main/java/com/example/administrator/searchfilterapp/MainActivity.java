package com.example.administrator.searchfilterapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.searchfilterapp.fragment.HomeFragment;
import com.example.administrator.searchfilterapp.model.FilteringData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String url="http://gofenzbeta.azurewebsites.net/api/Mobile/GetTrackingData";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<FilteringData>filteringDatalist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        filteringDatalist=new ArrayList<>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d(TAG, response.toString());

                        Log.e("response","...."+response);

                        try {

                            JSONArray tripData = response.getJSONArray("TripData");


                            for (int i = 0; i < tripData.length(); i++) {

                                JSONObject data_object = tripData.getJSONObject(i);
                                JSONArray tripList = data_object.getJSONArray("TripList");

                                for (int j = 0; j < tripList.length(); j++) {
                                    JSONObject tripList_object = tripList.getJSONObject(j);
                                    FilteringData filterdata=new FilteringData();
                                    filterdata.longitude = tripList_object.getString("Longitude");

                                    filteringDatalist.add(filterdata);
                                }
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                        setupViewPager(viewPager);

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i=0;i<filteringDatalist.size();i++) {


            adapter.addFrag(new HomeFragment(),String.valueOf(filteringDatalist.get(i).longitude));

        }
        viewPager.setAdapter(adapter);


    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

}
