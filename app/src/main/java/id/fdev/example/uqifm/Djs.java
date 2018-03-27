package id.fdev.example.uqifm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import id.fdev.example.uqifm.data.Data;
import id.fdev.example.uqifm.data.RecyclerAdapterDjs;

public class Djs extends AppCompatActivity implements ItemClickListener {

    public String URL = "http://api.uqi-fm.com/";
    public String URL2 = "http://uqi-fm.com/";

    private RecyclerAdapterDjs recyclerAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Data> listdata;
    private Button btnBack;

    private TextView txt_titleToolbar;

    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.djs);

        btnBack = (Button) findViewById(R.id.btnBack);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if(layoutManager.findFirstCompletelyVisibleItemPosition()==listdata.size()-1){
                    Log.i("Get the last ID : ",listdata.get(listdata.size()-1).getNama());
                }
            }
        });


        listdata = new ArrayList<Data>();
        Ambildata();
        recyclerAdapter = new RecyclerAdapterDjs(this, listdata);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        recyclerAdapter.setClickListener(this);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_right);
                Intent i = new Intent(Djs.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onClick(View view, int position) {
        final Data data = listdata.get(position);
        Log.i("Testing.......", data.getNama());

        /*Intent i = new Intent(this, Program.class);
          i.putExtra("judul", data.getJudul());
          startActivity(i);*/
    }


    public void Ambildata(){

        JsonArrayRequest arrRequest = new JsonArrayRequest(URL+"/djs.php",
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length()>0){
                            for (int i=0; i<response.length(); i++){
                                try {
                                    JSONObject data= response.getJSONObject(i);
                                    Data item = new Data();
                                    item.setId(data.getString("id"));
                                    item.setNama(data.getString("nama"));
                                    item.setBio(data.getString("bio"));
                                    item.setThubnail(URL2+"/wp-content/uploads/2014/02/"+data.getString("gambar"));
                                    listdata.add(item);
                                    recyclerAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
        };
        Volley.newRequestQueue(this).add(arrRequest);
    }



}
