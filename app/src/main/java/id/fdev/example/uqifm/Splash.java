package id.fdev.example.uqifm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plash);

        tv = (TextView) findViewById(R.id.txtwelcome);
        iv = (ImageView) findViewById(R.id.gambar);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        tv.setAnimation(myanim);
        iv.setAnimation(myanim);

        final Intent i = new Intent(this,MainActivity.class);

        Thread timer = new Thread(){
          public void run(){
              try{
                 sleep(3000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }finally {
                  overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                  startActivity(i);
                  finish();
              }
          }
        };

        timer.start();
    }
}
