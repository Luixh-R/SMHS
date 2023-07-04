package com.fyp.smhs.Activities;

import android.Manifest;
import android.app.AlarmManager;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.smhs.Database.Mood;
import com.fyp.smhs.Database.MoodDao;
import com.fyp.smhs.Database.Resources;
import com.fyp.smhs.Fragments.HomeFragment;
import com.fyp.smhs.Intro.IntroActivity;
import com.fyp.smhs.Models.Post;
import com.fyp.smhs.Notification.AlarmReceiver;
import com.fyp.smhs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fyp.smhs.Database.AppDatabase;
import com.fyp.smhs.Database.ResourcesDao;

public class Home extends AppCompatActivity {

    private static final String TAG = "DocSnippets";

    private AppBarConfiguration mAppBarConfiguration;

    private AppBarConfiguration rar;
        private static final int REQUESTCODE = 2;
        private static final int PReqCode =  2;
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        Dialog popAddPost ;
        ImageView popupUserImage,popupPostImage,popupAddBtn;
        TextView popupTitle,popupDescription;
        ProgressBar popupClickProgress;
        private Uri pickedImgUri = null;
        String imageDownloadLink;
         FirebaseFirestore  firebaseFirestore;
         CollectionReference collectionReference;
            NavController navController;

    public static final String QUERY_MOOD_PARAMETER = "Home.QueryMood";
    public static final String NOTIFICATION_CHANNEL_ID = "Home.NotificationChan";

    private int mCurrentMood = 1;
    private int mCurrentMoodIntensity = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //  ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // ini popup
        iniPopup();
        setupPopupImageClick();


       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popAddPost.show();

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_settings,R.id.nav_signout)
                .setDrawerLayout(drawer)
                .build();


         navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

              BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);

        NavigationUI.setupWithNavController(bottomNav, navController);


        updateNavHeader();

        //onSupportNavigateUp();
      //  setupResourcesDatabase();
        registerNotificationChannel();
        setupBottomNavigation();

        showCurrentMoodDialog();
        // Set the home fragment as the default one



    }

    private void setupBottomNavigation2(BottomNavigationView bottomNavigationView) {


    }

    private void setupPopupImageClick() {



        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...

                 checkAndRequestForPermission();

            }
        });
    }



    private void registerNotificationChannel() {
        /**
         * Registers a notification channel which is required to post notifications
         * to the user. This is done repeatedly whenever the app is started but
         * there is not problem with calling .createNotificationChannel repeatedly.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "DailyNotification", importance);
            channel.setDescription("Talk.Notifications");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    private void setupBottomNavigation() {

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
        final int Help = 7;

        // Create the list of all the resources
        Resources[] allResources = {

                // Depression Help
                 new Resources("Overview Clinical Depression", "Depression is more than simply feeling unhappy or fed up for a few days.", "https://www.nhs.uk/mental-health/conditions/clinical-depression/overview/", MoodDepressed),
                 new Resources("Dealing with depression at university", "While moving to university has always meant a period of adjustment, more students than ever are struggling at university, particularly with depression.", "https://www.theuniguide.co.uk/advice/student-life/dealing-with-depression-at-university", MoodDepressed),
                 new Resources("Helping students cope with depression ", "Students are gearing up for the midterms. It's a time that many students in college and high school can have problems with. Dr. Paul Valbuena from Valbuena 360 Wellness has some ideas on helping students get through the stress.", "https://www.youtube.com/watch?v=blF8t0GMjFY&ab_channel=azfamilypoweredby3TV%26CBS5AZ", MoodDepressed),
                 new Resources("Depression and College Students", "A lack of sleep, poor eating habits, and not enough exercise are a recipe for depression among college students.", "https://www.healthline.com/health/depression/college-students", MoodDepressed),

                // Sad Help

                  new Resources("How to tackle student loneliness", "Maria Loades, Ola Demkowicz, Pamela Qualter and Roz Shafran explain loneliness in young people and identify steps that we can take to help students as restrictions ease.", "https://wonkhe.com/blogs/how-to-tackle-student-loneliness/", MoodSad),
                  new Resources("6 Differences Between Sadness and Depression","When considering your mental health, what do you think is the difference between depression vs sadness?","https://www.youtube.com/watch?v=tNwRNmFT7-4&ab_channel=Psych2Go",MoodSad),
                  new Resources("Low mood, sadness and depression","Most people feel low sometimes, but if it's affecting your life, there are things you can try that may help.","https://www.nhs.uk/mental-health/feelings-symptoms-behaviours/feelings-and-symptoms/low-mood-sadness-depression/",MoodSad),
                  new Resources("Living with Sadness: How Does Sadness Differ from Depression?","Sadness and depression share some traits but are not the same. Understanding the difference is important since clinical depression requires treatment.","https://www.psycom.net/living-with-sadness-how-does-sadness-differ-from-depression/",MoodSad),


                // Angry Help

                new Resources("Anger management: 10 tips to tame your temper","Keeping your temper in check can be challenging. Use simple anger management tips — from taking a timeout to using \"I\" statements — to stay in control.","https://www.mayoclinic.org/healthy-lifestyle/adult-health/in-depth/anger-management/art-20045434",MoodAngry),
                new Resources("Anger","Most people feel angry sometimes, but if it's affecting your life, there are things you can try that may help.","https://www.nhs.uk/mental-health/feelings-symptoms-behaviours/feelings-and-symptoms/anger/",MoodAngry),
                new Resources("How to Control Anger: 25 Tips to Help You Stay Calm Desc","Anger is a normal feeling and can be a positive emotion when it helps you work through issues or problems, whether that’s at work or at home.","https://www.healthline.com/health/mental-health/how-to-control-anger#",MoodAngry),
                new Resources("How to be less angry: 7 proven ways to control and defuse rage","Do you want to be less angry? Here are seven proven ways to diffuse anger, shrug off frustrations and minimise the fallout for those times when we inevitably lose our cool.","https://www.stylist.co.uk/life/how-to-be-less-angry-the-seven-proven-ways-to-control-and-defuse-rage-temper-tips-anger-management/58705",MoodAngry),


                // Scared Help

                new Resources("Ten ways to fight your fears","Whatever it is that scares you, here are 10 ways to help you cope with your day-to-day fears and anxieties.","https://www.nhsinform.scot/healthy-living/mental-wellbeing/fears-and-phobias/ten-ways-to-fight-your-fears",MoodScared),
                new Resources("How to Stop Being Scared All the Time","Every human worries on occasion, but for some of us, the suffering is on a quite different and more life-destroying scale","https://www.theschooloflife.com/thebookoflife/how-to-stop-being-scared-all-the-time/",MoodScared),
                new Resources("How to stop being scared of something","Fear can be useful – a way of coping with extreme situations. However, fight or flight mode kicks in whether the danger is real or imagined.","https://www.thesimplethings.com/blog/how-to-stop-being-scared-of-something",MoodScared),
                new Resources("How to overcome fear and anxiety","Fear is one of the most powerful emotions. It has a very strong effect on your mind and body.","https://www.mentalhealth.org.uk/publications/overcome-fear-anxiety",MoodScared),


                // Moderate Help

                new Resources("8 Steps to Continuous Self-Motivation Even During the Difficult Times","Many of us find ourselves in motivational slumps that we have to work to get out of. Sometimes it’s like a continuous cycle where we are motivated for a period of time, fall out and then have to build things back up again.","https://www.lifehack.org/articles/featured/8-steps-to-continuous-self-motivation.html",MoodModerate),
                new Resources("How to Stay Productive + Be Motivated for School ","TIPS FOR PRODUCTIVITY AND MOTIVATION AFTER AND IN SCHOOL","https://www.youtube.com/watch?v=9XW9MWTKPYw&ab_channel=VanessaTiiu",MoodModerate),
                new Resources("21 Simple Ideas To Improve Student Motivation ","The best lessons, books, and materials in the world won’t get students excited about learning and willing to work hard if they’re not motivated.","https://www.teachthought.com/pedagogy/21-simple-ideas-to-improve-student-motivatio/",MoodModerate),
                new Resources("Tips for Staying Motivated","To succeed in high school and college, you have to do your best at all times. But sometimes it’s hard to stay motivated, even when you really care about the work you’re doing. Here are five ways to stay on the right track.","https://bigfuture.collegeboard.org/get-started/inside-the-classroom/tips-for-staying-motivated",MoodModerate),


                //  Happy Help

                new Resources("How to be happier","Try 6 tips to help you be happier, more in control, and able to cope better with life's ups and downs.","https://www.nhs.uk/mental-health/self-help/tips-and-support/how-to-be-happier/",MoodHappy),
                new Resources("How to Be Happy: 25 Habits to Add to Your Routine","Happiness looks different for everyone. For you, maybe it’s being at peace with who you are. Or having a secure network of friends who accept you unconditionally. Or the freedom to pursue your deepest dreams.","https://www.healthline.com/health/how-to-be-happy",MoodHappy),
                new Resources("10 Scientifically Proven Ways to Be Incredibly Happy","It's easy to think of happiness as a result, but happiness is also a driver.","https://www.inc.com/jeff-haden/10-scientifically-proven-ways-to-be-incredibly-happy-wed.html",MoodHappy),
                new Resources("How to Be Happy Every Day: It Will Change the World","The World Happiness Report states “Over 1 billion adults suffer from anxiety and depression.” How do we get to happy? Jacqueline Way, Founder of www.365give.ca shares a secret to happiness so simple a 3 – year old can do it.","https://www.youtube.com/watch?v=78nsxRxbf4w",MoodHappy),

                //  Help Help

                new Resources("Samirtans","0808 808 4994","https://www.samaritans.org/ ",Help),
                new Resources("SANEline","0300 304 7000","http://www.sane.org.uk/what_we_do/support/helpline  ",Help),
                new Resources("The Mix","0808 808 4994","https://www.themix.org.uk/get-support/speak-to-our-team/email-us",Help),
                new Resources("Papyrus HOPLINEUK","0800 068 4141","https://www.papyrus-uk.org/",Help),
                new Resources("CALM","0800 58 58 58","https://www.thecalmzone.net/",Help),

        };

        resDao.insertAll(allResources);


                Toast.makeText(this, "Resource database loaded", Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            // everything goes well : we have permission to access user gallery
            openGallery();
    }


    private void openGallery() {

        //TODO: open gallery intent and wait for user to pick an image !


     //   String title = popupTitle.getText().toString();
       // String description = popupDescription.getText().toString();

       // String userId = currentUser.getUid();
       // String userPhoto = currentUser.getPhotoUrl().toString();

       // User user = new User(title, description);
       // DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference();
      //  mDatabase.child("users").child(userId).setValue(user);


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }



    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // RESULT_OK
        if (requestCode == 2 && requestCode == REQUESTCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);
        }
    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        // load Current user profile photo

    //    Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);
        Glide.with(Home.this).load(R.drawable.default_user_photo).into(popupUserImage);

        // Add post click Listener

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields (Title and description) and post image

                if(!popupTitle.getText().toString().isEmpty()
                    && !popupDescription.getText().toString().isEmpty()
                    ) {



                                     if(currentUser.getPhotoUrl() != null){

                                         Post post = new Post(popupTitle.getText().toString(),
                                                 popupDescription.getText().toString(),
                                                 imageDownloadLink,
                                                 currentUser.getUid(),
                                                 currentUser.getPhotoUrl().toString());

                                         // Add post to firebase database

                                         addPost(post);
                                         // Add post to firebase database
                                     }else {

                                         Post post = new Post(popupTitle.getText().toString(),
                                                 popupDescription.getText().toString(),
                                                 imageDownloadLink,
                                                 currentUser.getUid(),
                                                 null);

                                         // Add post to firebase database

                                         addPost(post);
                                         // Add post to firebase database
                                     }


                }
                else{
                    showMessage("Please verify all input fields and choose Post Image");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }

             //   HomeFragment;

            }
        });

    }

    private void addPost(Post post) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        String documentId=db.collection("Posts").document().getId();
        post.setPostKey(documentId);



        db.collection("Posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {


                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        showMessage("Post Added successfully");
                        popupClickProgress.setVisibility(View.INVISIBLE);
                        popupTitle.setText("");
                        popupDescription.setText("");
                        popupAddBtn.setVisibility(View.VISIBLE);
                        popAddPost.dismiss();

                        getSupportFragmentManager().beginTransaction().replace(R.id.feed,new HomeFragment()).commit();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    protected void onResume() {
        /**
         * This callback is called when the app is being restored after being paused.
         * This could be due to one of two reasons: the user opened the app from
         * the app icon or by clicking a notification from Talk.
         */
        super.onResume();

        // TODO: Remove, this is for demo purposes
        boolean resumingFromNotification = getIntent().getBooleanExtra(QUERY_MOOD_PARAMETER, false);
        if (resumingFromNotification) {

            showCurrentMoodDialog();
        } else {
            // Show notification if opening the app

            showNotification();
        }
    }



    private void addPostOld() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
        StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
        String imageDownlaodLink = imageFilePath.getDownloadUrl().toString();
        // create post Object

        String title = popupTitle.getText().toString();
        String description = popupDescription.getText().toString();
        String picture = imageDownlaodLink;
        String userId = currentUser.getUid();
        String userPhoto = currentUser.getPhotoUrl().toString();
        Object timeStamp = ServerValue.TIMESTAMP;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("description", description);
        post.put("picture", picture);
        post.put("userId", userId);
        post.put("userPhoto", userPhoto);
        post.put("timeStamp", timeStamp);



        db.collection("Posts")
                .add(post)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                showMessage("Post Added successfully");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    private void showMessage(String message) {

        Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();

    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
       int id = item.getItemId();

               if (id == R.id.nav_signout){

                   Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG).show();
               }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateHome (){

    }

    public void updateNavHeader (){

        NavigationView navigationView = findViewById(R.id.nav_view);
        View  headerView =  navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());


        // now we will use Glide to load user image
        // first we need to import the library

        if(currentUser.getPhotoUrl() != null){
            Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);


        }else

            Glide.with(this).load(R.drawable.userphoto).into(navUserPhoto);


    }

    private void showCurrentMoodDialog() {
        /**
         * Shows the user a dialog that asks them for their current mood then
         * stores the result inside of an instance variable.
         */
        CharSequence moods[] = {
                // Depressed = 1, Sad = 2, Angry = 3, Scared = 4, Moderate = 5, Happy = 6
                "Depressed", "Sad", "Angry", "Scared", "Moderate", "Happy"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("How are you feeling today?");
        builder.setSingleChoiceItems(moods, 0, new MoodDialogChoiceListener());
        builder.setPositiveButton("Next", new MoodDialogListener());
        builder.show();
    }

    private void showMoodIntensityDialog() {
        /**
         * Shows the dialog that asks the user the intensity of the mood
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.DarkAlertDialog);
            SeekBar seekBar = new SeekBar(Home.this);
        seekBar.setMax(4);
        seekBar.setOnSeekBarChangeListener(new MoodIntensityDialogSeekListener());

        builder.setTitle("How intense are you feeling this?");
        builder.setView(seekBar);
        builder.setPositiveButton("Save", new MoodIntesityDialogListener());

        builder.show();
    }

    void saveMoodToDatabase() {
        /**
         * Saves the current mood state to the SQLite database.
         */
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        MoodDao moodDao = database.moodDao();

        // Get the last entered date
        List<Mood> allMoods = moodDao.getAll();
        int numberOfMoods = allMoods.size();
        int lastEnteredDate = 0;
        if (numberOfMoods > 0) {
            lastEnteredDate = allMoods.get(numberOfMoods - 1).date;
        }

        // Create the new Mood
        Mood currentMood = new Mood(lastEnteredDate + 1, mCurrentMood, mCurrentMoodIntensity);
        moodDao.insert(currentMood);

        // Ask the user (again)
        showNotification();
    }

    private void showNotification() {
        /**
         * Sets the alarm to display a notification in the notification bar asking the user to hit
         * the notification so that they get prompted to enter their mood. The notification is
         * shown 3 seconds after requested for demo purposes.
         * TODO: Remove.
         */
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            alarmMgr.set(
                    AlarmManager.ELAPSED_REALTIME,
                    //2 * 1000
                    SystemClock.elapsedRealtime() + 24*60*60*1000,
                    pendingIntent
            );
        }
    }

    class MoodDialogListener implements DialogInterface.OnClickListener {
        /**
         * Listeners for the click event when the user chooses the mood
         * that they're in and hits the submit button.
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            showMoodIntensityDialog();
        }
    }

    class MoodDialogChoiceListener implements DialogInterface.OnClickListener {
        /**
         * Listens for the event in which the user chooses a different mood
         * from the multiple choice menu.
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Offset the choice because it goes from 0-5
            mCurrentMood = which + 1;
        }
    }

    class MoodIntesityDialogListener implements DialogInterface.OnClickListener {
        /**
         * Listens for the event in which the user chooses a mood intensity
         * using the SeekBar then hits the submit button.
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            saveMoodToDatabase();
            Toast.makeText(Home.this, "Saved", Toast.LENGTH_LONG).show();
        }
    }

    class MoodIntensityDialogSeekListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * Listens for updates on the SeekBar used to get the user's current mood.
         * The data is stored which is then used to save to the local SQLite database.
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Progress goes from 0-5 but we use 1-6
            mCurrentMoodIntensity = progress + 1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

}