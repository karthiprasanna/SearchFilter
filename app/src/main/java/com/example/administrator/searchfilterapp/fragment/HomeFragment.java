package com.example.administrator.searchfilterapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.searchfilterapp.AppController;
import com.example.administrator.searchfilterapp.R;
import com.example.administrator.searchfilterapp.model.FilteringData;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static String url="http://gofenzbeta.azurewebsites.net/api/Mobile/GetTrackingData";
    private RecyclerView listView;
    List<FilteringData>filteringDataList;
    private FilterDataAdapter filterDataAdapter;
    private SearchView searchView;


    public static HomeFragment newInstance() {

        return new HomeFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filteringDataList=new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = (RecyclerView)view.findViewById(R.id.listView);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        listView.setLayoutManager(layoutManager);;
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setHasFixedSize(true);



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG, response.toString());

                        Log.e("response1","...."+response);

                        try {

                            JSONArray tripData = response.getJSONArray("TripData");


                            for (int i = 0; i < tripData.length(); i++) {

                                JSONObject data_object = tripData.getJSONObject(i);
                                JSONArray tripList = data_object.getJSONArray("TripList");



                                for (int j = 0; j < tripList.length(); j++) {
                                    JSONObject tripList_object = tripList.getJSONObject(j);

                                    FilteringData filteringData=new FilteringData();


                                    filteringData.longitude = tripList_object.getString("Longitude");

                                    //filteringData.latitude = Float.valueOf(tripList_object.getString("Latitude"));
                                    Log.e("long","...."+ filteringData.longitude);

                                    filteringDataList.add(filteringData);

                                   // Log.e("filteringDataList","...."+filteringDataList.size());

                                }
                            }

                            filterDataAdapter=new FilterDataAdapter(getActivity(),filteringDataList);
                            listView.setAdapter(filterDataAdapter);
                            filterDataAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {

                            e.printStackTrace();

                        }



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjReq);


        searchView=(SearchView) view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), query, Toast.LENGTH_LONG).show();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDataAdapter.getFilter().filter(newText);
                //Toast.makeText(getActivity(), newText, Toast.LENGTH_LONG).show();
                return true;
            }
        });


        return view;

    }






    private class FilterDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Activity activity;
        List<FilteringData> infoList;
        private List<FilteringData> contactList_data;

        public FilterDataAdapter(Activity activity, List<FilteringData> infoList) {

            this.activity=activity;
            this.infoList=infoList;
            this.contactList_data= infoList;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_items, parent, false);
            return new MyHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyHolder itemHolder = (MyHolder) holder;
            FilteringData filterdata=infoList.get(position);

            itemHolder.lattitude.setText(""+filterdata.longitude);
        }

        @Override
        public int getItemCount() {
            return infoList.size();
        }

        private class MyHolder extends RecyclerView.ViewHolder {


            public TextView lattitude;


            public MyHolder(View itemView) {
                super(itemView);
                lattitude=(TextView)itemView.findViewById(R.id.lattitude);


            }


        }

        public Filter getFilter() {


            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    String charString = constraint.toString();
                    if (charString.isEmpty()) {
                        contactList_data = infoList;
                        Log.e("contactList_data","..."+contactList_data.size());
                    }
                    else {


                        ArrayList<FilteringData> filteredList = new ArrayList<>();

                        for (FilteringData androidVersion : infoList) {

                            if (androidVersion.longitude.toString().toLowerCase().contains(charString.toString().toLowerCase()) || androidVersion.longitude.toString().toLowerCase().contains(charString.toString()) || androidVersion.longitude.toString().toLowerCase().contains(charString.toString())) {

                                filteredList.add(androidVersion);
                            }
                        }

                        contactList_data = filteredList;

                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = contactList_data;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    contactList_data = (ArrayList<FilteringData>) filterResults.values;
                    Log.e("contactList_data","..."+contactList_data.size());


                    Toast.makeText(getActivity(),"position"+contactList_data.size(),Toast.LENGTH_SHORT).show();


                    notifyDataSetChanged();
                }
            };
            }

        }
    }












