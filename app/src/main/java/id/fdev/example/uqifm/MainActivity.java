package id.fdev.example.uqifm;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import id.fdev.example.uqifm.service.StreamService;
import id.fdev.example.uqifm.utils.Utils;

import com.skyfishjy.library.RippleBackground;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Intent serviceIntent;
    private Button btnPlay;
    //private TextView textwelcomm;
    private static boolean isStreaming = false;
    private ProgressDialog pdBuff = null;
    private boolean mBufferBroadcastIsRegistered;
    private Drawable iconStop,iconPlay;
    public String ipport,tagline,serverstatus;
    public static final String url_setting= "http://api.uqi-fm.com/setting.php";

    ObjectAnimator objAnim;


    FloatingActionMenu materialDesignFAM;
    FloatingActionButton djButton, progrmButton, shareButton, newsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RippleBackground ripplebackground = (RippleBackground) findViewById(R.id.content);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        //textwelcomm = (TextView) findViewById(R.id.txtwelcome);

        iconStop = this.getResources().getDrawable(R.drawable.ic_stop_white_24dp);
        iconPlay = this.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp);

        btnPlay.setCompoundDrawablesWithIntrinsicBounds(iconPlay,null,null,null);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        btnPlay.setAnimation(myanim);


        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        materialDesignFAM.setAnimation(myanim);

        djButton = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        progrmButton = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
        shareButton = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item4);
        newsButton = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item5);

        //getSetting();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == btnPlay) {
                    Log.d("playStatus", "" + isStreaming);

                    if (!isStreaming) {
                        btnPlay.setCompoundDrawablesWithIntrinsicBounds(iconStop,null,null,null);
                        btnPlay.setText("Stop");
                        startStreaming();
                        ripplebackground.startRippleAnimation();
                        Utils.setDataBooleanToSP(MainActivity.this, Utils.IS_STREAM, true);
                    } else {
                        if (isStreaming) {
                            btnPlay.setCompoundDrawablesWithIntrinsicBounds(iconPlay,null,null,null);
                            btnPlay.setText("Play");
                            Toast.makeText(MainActivity.this, "Stop Streaming..", Toast.LENGTH_SHORT).show();
                            stopStreaming();
                            isStreaming = false;
                            ripplebackground.stopRippleAnimation();
                            Utils.setDataBooleanToSP(MainActivity.this, Utils.IS_STREAM, false);
                        }
                    }
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent  share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody = "We are Stream @ http://uqi-fm.com/streaming";
                String shareSub = "UQI-FM 107.1 STREAMING";
                share.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                share.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(share, "Share Using"));
            }
        });

        progrmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                Intent i = new Intent(MainActivity.this, Program.class);
                startActivity(i);
                finish();
            }
        });

        djButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                Intent i = new Intent(MainActivity.this, Djs.class);
                startActivity(i);
                finish();
            }
        });

        newsButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "this page will be provided on next version", Toast.LENGTH_SHORT).show();
            }
        });

        serviceIntent = new Intent(this, StreamService.class);
        isStreaming = Utils.getDataBooleanFromSP(this, Utils.IS_STREAM);
        if (isStreaming) {
            btnPlay.setText("Stop");
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(iconStop, null, null, null);
            ripplebackground.startRippleAnimation();
        }else{
            btnPlay.setText("Play");
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(iconPlay, null, null, null);
            ripplebackground.stopRippleAnimation();
        }


    }


    public void getSetting(){

        JsonArrayRequest strReq = new JsonArrayRequest(url_setting,
                new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject data= response.getJSONObject(0);
                    String http ="http://";
                    String delimiter =":";
                    ipport = http+data.getString("ip")+delimiter+data.getString("port");
                    serverstatus = data.getString("serverstatus");
                    tagline = data.getString("info");
                    //textwelcomm.setText(tagline);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());

            }
        });

        Volley.newRequestQueue(this).add(strReq);
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (mBufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    StreamService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }
    }

    private void startStreaming() {
        Toast.makeText(this, "Start Streaming..", Toast.LENGTH_SHORT).show();
        stopStreaming();
        try {
            serviceIntent.putExtra("url","http://50.22.217.113:27651");
            startService(serviceIntent);
        } catch (Exception e) {
        }

    }

    private void stopStreaming() {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {

        }
    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showProgressDialog(bufferIntent);
        }
    };

    private void showProgressDialog(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);
        switch (bufferIntValue) {
            case 0:
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                break;

            case 1:
                pdBuff = ProgressDialog.show(MainActivity.this, "",
                        "Streaming...", true);
                break;
        }
    }


}
