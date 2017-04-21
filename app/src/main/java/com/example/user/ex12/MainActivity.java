package com.example.user.ex12;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MyDialog.ResultsListener{
    //finals
    final static int LEVEL_START = 1;
    final static int LEVEL_SECOND = 2;
    final static int LEVEL_THIRD = 3;
    final static int LEVEL_FINAL = 4;
    final static int NUMBER_OF_LEVELS = LEVEL_FINAL-1;
    static int DEFAULT_COUNT = 20;

    //variables
    ImageView im;
    TextView tv_count;
    Button resetButton;
    SharedPreferences settings;
    static int count = DEFAULT_COUNT;
    boolean reachZero = false;
    static int userLevel = LEVEL_START;
    static boolean orientationChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization views
        im = (ImageView)findViewById(R.id.imageViewEgg);
        tv_count = (TextView)findViewById(R.id.TVCounter);
        resetButton = (Button)findViewById(R.id.resetButton);
        settings = PreferenceManager.getDefaultSharedPreferences(this);


        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                DEFAULT_COUNT = Integer.parseInt(settings.getString("startNumberKey", "20"));
                orientationChange = settings.getBoolean("orientationKey", true);
                if(orientationChange)
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                }
                else
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
            }
        };
        settings.registerOnSharedPreferenceChangeListener(spChanged);


        if(savedInstanceState==null)//actions for the first time the app up
        {
            //get the last user status
            DEFAULT_COUNT = Integer.parseInt(settings.getString("startNumberKey", "20"));
            count = settings.getInt("count_prefernces", DEFAULT_COUNT);
            userLevel = settings.getInt("userLevel_preferneces", LEVEL_START);
            reachZero = settings.getBoolean("reachZero_prefernces", false);
            orientationChange = settings.getBoolean("orientationKey", true);

            //print his name
            String user = settings.getString("yourNameKey", "Guest");
            if(user.compareTo("")==0)
            {
                user = "Guest";
            }
            Toast.makeText(this,"Hello " + user, Toast.LENGTH_LONG).show();

            if(orientationChange)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            }
            else
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }

        //set count number
        if(reachZero)//check if the user reach to zero
        {
            setTextCount(getResources().getString(R.string.END_OF_THE_GAME));
        }
        else
        {
            setTextCount(Integer.toString(count));
        }

        makeImage(userLevel);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set animation to the image view
                Animation shake  = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake);
                im.startAnimation(shake);

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(35);

                if(!reachZero)//check if the user reach to zero
                {
                    count--;
                    setTextCount(Integer.toString(count));
                    if(count<=(DEFAULT_COUNT-((DEFAULT_COUNT/NUMBER_OF_LEVELS)*userLevel)))
                    {
                        userLevel++;
                        makeImage(userLevel);
                    }
                }
                if(count<=0 && !reachZero)
                {
                    makeZero();
                }


            }
        });
    }

    @Override
    protected void onDestroy() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("count_prefernces", count);
        editor.putInt("userLevel_preferneces", userLevel);
        editor.putBoolean("reachZero_prefernces", reachZero);
        editor.commit();
        super.onDestroy();
    }

    /*
        this function will set the text of the number that left for the count
         input: the cast string from the count
         output: none
         */
    public void setTextCount(String strToUpdate)
    {
        tv_count.setText(strToUpdate);
    }
    /*
    this function take care of the function when the user reach to zero
    input: none
    output: none
     */
    public void makeZero()
    {
        makeImage(LEVEL_FINAL);//call the function with the final level
        setTextCount(getResources().getString(R.string.END_OF_THE_GAME));//set the text to the final string
        reachZero = true;
    }
    /*
    this function will set the image of the clickable image of the user
    input: the level of the user in the app
    output: none
     */
    public void makeImage(int level)
    {
        switch(level)
        {
            case LEVEL_START:
            {
                im.setImageResource(R.drawable.egg);//the start egg
                break;
            }
            case LEVEL_SECOND:
            {
                im.setImageResource(R.drawable.egg2);
                break;
            }
            case LEVEL_THIRD:
            {
                im.setImageResource(R.drawable.egg3);
                break;
            }
            case LEVEL_FINAL:
            {
                if(count==0)
                    im.setImageResource(R.drawable.zero_egg);//the final egg
                break;
            }
            default:
            {
                im.setImageResource(R.drawable.egg);
                break;
            }
        }
    }

    public void reset()
    {
        count = DEFAULT_COUNT;
        userLevel = LEVEL_START;
        reachZero = false;

        setTextCount(Integer.toString(count));
        makeImage(userLevel);
    }

    //this function will restore the variables after oriention
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        count = savedInstanceState.getInt("count");
        userLevel = savedInstanceState.getInt("level");
        reachZero = savedInstanceState.getBoolean("reach");
        super.onRestoreInstanceState(savedInstanceState);
    }

    //this function will save the variables before oriention
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("count", count);
        outState.putInt("level", userLevel);
        outState.putBoolean("reach", reachZero);
        super.onSaveInstanceState(outState);
    }


    /*
    this function will create the option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    //this function will cal the right function by the choice of the user
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings:
                getFragmentManager().beginTransaction().add(android.R.id.content,new MyPrefernces()).addToBackStack(null).commit();
                return true;
            case R.id.action_exit:
                MyDialog.newInstance().show(getFragmentManager(), "exit dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //this function will called by the dialog when he is finished
    @Override
    public void OnfinishDialog(Object result) {
        finish();
        return;
    }

    //this class is for the prefernces layout
    public static class MyPrefernces extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            return view;
        }
    }

}
