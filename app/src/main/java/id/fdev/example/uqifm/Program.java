package id.fdev.example.uqifm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.fdev.example.uqifm.data.Data;
import id.fdev.example.uqifm.data.RecyclerAdapter;

public class Program extends AppCompatActivity implements ItemClickListener {

    public String URL = "http://api.uqi-fm.com/";

    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Data> listdata;
    private Button btnBack;

    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        btnBack = (Button) findViewById(R.id.btnBack);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        listdata = new ArrayList<Data>();
        Ambildata();

        recyclerAdapter = new RecyclerAdapter(this, listdata);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        recyclerAdapter.setClickListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_right);
                Intent i = new Intent(Program.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onClick(View view, int position) {
        final Data data = listdata.get(position);
        Log.i("Testing.......", data.getJudul());

        /*Intent i = new Intent(this, Program.class);
          i.putExtra("judul", data.getJudul());
          startActivity(i);*/
    }


    public void Ambildata(){

        JsonArrayRequest arrRequest = new JsonArrayRequest(URL+"/program.php",
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("sss",response.toString());

                       /* try {
                            JSONObject test = response.toJSONObject(response);
                            Log.i("Test", test.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        if(response.length()>0){
                            for (int i=0; i<response.length(); i++){
                                try {
                                    JSONObject data= response.getJSONObject(i);
                                    Data item = new Data();
                                    item.setId(data.getString("id"));
                                    item.setJudul(data.getString("judul"));
                                    item.setJam(data.getString("jam"));
                                    item.setThubnail(URL+"/image/"+data.getString("gambar"));
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
