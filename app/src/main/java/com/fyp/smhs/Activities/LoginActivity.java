package com.fyp.smhs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fyp.smhs.Database.AppDatabase;
import com.fyp.smhs.Database.Resources;
import com.fyp.smhs.Database.ResourcesDao;
import com.fyp.smhs.Intro.IntroActivity;
import com.fyp.smhs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {



    private EditText userMail,userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        String bad = "dad";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("first", bad);
        user.put("last", "Lovelace");
        user.put("born", 1815);

        db.collection("Posts")
                .add(user);

*/

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPrefs.getBoolean(IntroActivity.LAUNCHED_APP_BEFORE, false)) {
            Intent intent = new Intent(this, IntroActivity.class);
            finish();
            startActivity(intent);
            setupResourcesDatabase();
        }


        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginBtn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, Home.class);
        loginPhoto = findViewById(R.id.login_photo);
        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();



            }
        });

        loginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Please Verify All Field");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    signIn(mail,password);


                }



            }
        });




    }

    void setupResourcesDatabase() {


            /**
             * Sets up the Help database with all the articles.
             */
            AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
            ResourcesDao resDao = database.resourcesDao();

            // Depressed = 1, Sad = 2, Angry = 3, Scared = 4, Moderate = 5, Happy = 6
            final int MoodDepressed = 1;
            final int MoodSad = 2;
            final int MoodAngry = 3;
            final int MoodScared = 4;
            final int MoodModerate = 5;
            final int MoodHappy = 6;

            // Create the list of all the resources
            Resources[] allResources = {
                    // Depressed
                    new Resources("Coping with depression", "When you’re depressed, you can’t just will yourself to “snap out of it.” But these tips can help put you on the road to recovery.", "https://www.helpguide.org/articles/depression/coping-with-depression.htm", MoodDepressed),
                    new Resources("What is depression?", "Depression is a disorder that is evidenced by excessive sadness, loss of interest in enjoyable things, and low motivation.", "https://thiswayup.org.au/how-do-you-feel/sad/", MoodDepressed),
                    new Resources("Cat", "Watch this video.", "https://www.youtube.com/watch?v=xbs7FT7dXYc", MoodDepressed),
                    new Resources("Depression Symptoms and Warning Signs", "Do you think you might be depressed? Here are some of the signs and symptoms to look for—and tips for getting the help you need.", "https://www.helpguide.org/articles/depression/depression-symptoms-and-warning-signs.htm", MoodDepressed),

                    // Sad
                    new Resources("Alone in the crowd - How loneliness affects the mind and body", "Watch this video about being lonely.", "https://www.youtube.com/watch?v=R8A7JodFx4s", MoodSad),
                    new Resources("Am I Depressed or Just Really Sad?", "People often think they’re depressed when they’re sad, or sad when they’re depressed.", "https://www.vice.com/en_us/article/9kzqa7/am-i-depressed-difference-sadness-depression", MoodSad),
                    new Resources("Why am I sad all the time?", "Ever felt sad or stressed for no apparent reason?", "https://au.reachout.com/articles/why-am-i-sad-all-the-time", MoodSad),
                    new Resources("How do I know if I'm sad or depressed?", "If you're afraid that your depressed, there are many things you can do to help figure it out.", "https://www.7cups.com/qa-depression-3/how-do-i-know-if-im-sad-or-depressed-650/", MoodSad),

                    // Angry
                    new Resources("Anger Management", "Is your temper hijacking your life? These tips and techniques can help you get anger under control and express your feelings in healthier ways.", "https://www.helpguide.org/articles/relationships-communication/anger-management.htm", MoodAngry),
                    new Resources("Controlling anger before it controls you", "We all know what anger is, and we've all felt it: whether as a fleeting annoyance or as full-fledged rage.", "https://www.apa.org/topics/anger/control", MoodAngry),
                    new Resources("I'm Angry", "Watch this video.", "https://www.youtube.com/watch?v=vyMx7s9cThU", MoodAngry),
                    new Resources("Why Am I So Angry?", "Anger can be a force for good. But ongoing, intense anger is neither helpful nor healthy. Here's how to get a grip.", "https://www.webmd.com/mental-health/features/why-am-i-so-angry#1", MoodAngry),

                    // Scared
                    new Resources("Phobias and Irrational Fears", "Is a phobia keeping you from doing things you’d like to do? Learn how to recognize, treat, and overcome the problem.", "https://www.helpguide.org/articles/anxiety/phobias-and-irrational-fears.htm", MoodScared),
                    new Resources("I'm Scared", "The fact that you feel scared about these intrusive thought means that you need to see a psychotherapist.", "https://www.mentalhelp.net/advice/i-m-scared/", MoodScared),
                    new Resources("Jeremy Zucker - Scared (Lyrics)", "Listen to song about loneliness.", "https://www.youtube.com/watch?v=iyEUvUcMHgE", MoodScared),
                    new Resources("How To Stop Being So Goddamn Scared All The Time", "So, you're scared. Let's finally talk about that, shall we?", "https://ittybiz.com/how-to-stop-being-scared/", MoodScared),

                    // Moderate
                    new Resources("5 Steps To Avoid Complacency", "Remember the fire in the belly you felt on the way to achieving a goal?", "https://thetobincompany.com/5-steps-to-avoid-complacency/", MoodModerate),
                    new Resources("How to be human: what it means to feel normal", "Leah Reich was one of the first internet advice columnists", "https://www.theverge.com/2017/2/5/14514224/how-to-be-human-depression-anxiety-feeling-normal", MoodModerate),
                    new Resources("NEVER GET COMFORTABLE - Best Motivational Video", "Motivate yourself with this video", "https://www.youtube.com/watch?v=2o8fmUlHAyk", MoodModerate),
                    new Resources("10 Best Things To Do With Your Free Time", "Watch this video about using your free time", "https://www.youtube.com/watch?v=afoAXho6EHs", MoodModerate),

                    // Happy
                    new Resources("Feeling Happy and Being Happy Aren't the Same", "Can you be wrong about whether you are happy?", "https://www.psychologytoday.com/us/blog/am-i-right/201310/feeling-happy-and-being-happy-arent-the-same", MoodHappy),
                    new Resources("How to feel happier, according to neuroscientists and psychologists", "Researchers have known for decades that certain activities make us feel better, and they're just beginning to understand what happens in the brain to boost our mood.", "https://www.businessinsider.com/how-feel-happy-happier-better-2017-7", MoodHappy),
                    new Resources("Pharrell Williams - Happy", "Listen to Pharrell sing about being Happy!", "https://www.youtube.com/watch?v=ZbZSe6N_BXs", MoodHappy),
                    new Resources("The Science of Happiness: What Actually Makes Us Happy", "We all want to be happy. Period. In fact, I would argue that nearly everything we do, whether it’s working, marrying, running, or even filing our taxes is done with an overarching purpose: To feel happier.", "https://medium.com/@MaxWeigand/the-science-of-happiness-what-actually-makes-us-happy-78edcc9bdd58", MoodHappy),
            };

            resDao.insertAll(allResources);

            Toast.makeText(this, "Resource database loaded", Toast.LENGTH_LONG).show();
        }



    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){
                    closeKeyboard();
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();

                }
                else
                {
                    closeKeyboard();
                    showMessage(task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);

                }


            }
        });



    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateUI() {



        startActivity(HomeActivity);
        finish();

    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();



        if(user != null){
            // user is already connected so we need to redirect him to home page
            updateUI();

        }




    }
}