package app.gigg.me.app.Activity;

import static app.gigg.me.app.Activity.SplashScreenActivity.initAD;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ankushgrover.hourglass.Hourglass;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeBannerAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.gigg.me.app.Model.Question;
import app.gigg.me.app.R;

public class QuestionActivity extends AppCompatActivity {
    Hourglass hourglass;
    boolean isPaused= false;
    private TextView timer, numberOfQuestion, theQuestion, questionPoints;
    private String bannerBottomType, url, categoryId, categoryName, categoryLevel, rightAnswer, falseAnswerOne, falseAnswerTwo, submittedAnswer, userId;
    private int allQuestions, i, questionEarningPoints;
    private RequestQueue queue;
    private ArrayList<Question> questions;
    private RadioGroup mRadioGroup;
    private RadioButton answer1, answer2, answer3, answer4, selectedRadioButton;
    private Button submitBtn, finishBtn, fiftyFifty, rewardVideoBtn;
    private AdView bannerAdView;
    private RewardedAd mRewardedAd;
    private LinearLayout adsLinear;
    private int collectedPoints, actualQuestionNum;
    private SharedPreferences userSituationId, points;
    private int earnedPoints, totalQuizPoints, rightAnswersNumber, seconds;
    private boolean gameOver;
    private boolean gamePaused;
    private int rewarded;
    private SharedPreferences fiftyFiftyOption, startAppID, rewardVideoOption, questionTime, userSituation, admobBanner, bottomBannerType, facebookBanner, facebookNative, admobVideo, admobInterstitial, facebookInterstitial;
    private NativeBannerAd nativeBannerAd;
    private LinearLayout adView;
    private AdView bannerAdmobAdView;
    private com.facebook.ads.InterstitialAd facebookInterstitialAd;
    private com.facebook.ads.AdView facebookBannerAdView;
    //    StartAppAd startAppAdVideoAd;
    private boolean temp = false;
    private SharedPreferences tempValue;
    static boolean active = false;


    //12
    private LinearLayout adsLinear12;
    private AdView bannerAdmobAdView12;

    private RelativeLayout banner_layout, banner_layout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        startAppID = getSharedPreferences("startAppID", MODE_PRIVATE);
        tempValue = getSharedPreferences("tempValue", MODE_PRIVATE);

        //StartAppSDK.init(this, startAppID.getString("startAppID",""), false);
        setContentView(R.layout.activity_question);
        //startAppAdVideoAd = new StartAppAd(QuestionActivity.this);
        //startAppAdVideoAd.loadAd(StartAppAd.AdMode.OFFERWALL);

        //Unity AD
        banner_layout = findViewById(R.id.bannerView3);
        //  banner_layout1 = findViewById(R.id.bannerView4);


        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        userSituationId = getSharedPreferences("userId", MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
        facebookNative = getSharedPreferences("facebookNative", MODE_PRIVATE);
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
        admobVideo = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        admobInterstitial = getSharedPreferences("admobVideo", MODE_PRIVATE);
        questionTime = getSharedPreferences("seconds", MODE_PRIVATE);
        fiftyFiftyOption = getSharedPreferences("fiftyFiftyOption", Context.MODE_PRIVATE);
        rewardVideoOption = getSharedPreferences("rewardVideoOption", Context.MODE_PRIVATE);
        initialiseFaceBookBannerAds();
        collectedPoints = 0;
        earnedPoints = 0;
        totalQuizPoints = 0;
        rightAnswersNumber = 0;
        seconds = questionTime.getInt("seconds", 0);
        userId = userSituationId.getString("userId", null);
        points = getSharedPreferences("points", MODE_PRIVATE);
        points.edit().putInt("points", 0).apply();
        queue = Volley.newRequestQueue(this);
        // Rewarded Video Ads
        loadRewardedVideoAd();
        loadInitAd();
        // Admob Banner Bottom
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");


        adsLinear12 = (LinearLayout) findViewById(R.id.ads_linear_profile12);
        bannerAdmobAdView12 = new AdView(this);
        bannerAdmobAdView12.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bannerAdmobAdView12.setAdSize(com.google.android.gms.ads.AdSize.BANNER);

        adsLinear12.addView(bannerAdmobAdView12);
        adsLinear12.setGravity(Gravity.CENTER_HORIZONTAL);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        bannerAdmobAdView12.loadAd(adRequest1);
        bannerAdmobAdView12.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adsLinear12.setVisibility(View.VISIBLE);
            }
        });

        // Questions
        questions = new ArrayList<>();
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        answer1 = (RadioButton) findViewById(R.id.answer1);
        answer2 = (RadioButton) findViewById(R.id.answer2);
        answer3 = (RadioButton) findViewById(R.id.answer3);
        answer4 = (RadioButton) findViewById(R.id.answer4);
        questionPoints = (TextView) findViewById(R.id.question_points);
        submitBtn = (Button) findViewById(R.id.submit);
        ////
        finishBtn = (Button) findViewById(R.id.finish);
        fiftyFifty = (Button) findViewById(R.id.fifty_fifty);
        rewardVideoBtn = (Button) findViewById(R.id.reward_video);
        /////
        timer = (TextView) findViewById(R.id.timer);
        numberOfQuestion = (TextView) findViewById(R.id.number_of_question);
        theQuestion = (TextView) findViewById(R.id.question);

        if (fiftyFiftyOption.getString("fiftyFiftyOption", "").equals("yes")) {
            fiftyFifty.setVisibility(View.VISIBLE);
        } else {
            fiftyFifty.setVisibility(View.GONE);
        }
        if (rewardVideoOption.getString("rewardVideoOption", "").equals("yes")) {
            rewardVideoBtn.setVisibility(View.VISIBLE);
        } else {
            rewardVideoBtn.setVisibility(View.GONE);
        }
        resetTimer();

        hourglass.startTimer();

        if (tempValue.getBoolean("tempValue", false)) {
            tempValue.edit().putBoolean("tempValue", true).commit();
        }
        // Get the API url from Intent
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        categoryId = intent.getStringExtra("categoryId");
        categoryLevel = intent.getStringExtra("categoryLevel");
        categoryName = intent.getStringExtra("categoryName");
        // Launch Request & get Data & parse JSON
        parseJson();
        // Fifty Fifty Option
        answer1.setVisibility(View.VISIBLE);
        answer2.setVisibility(View.VISIBLE);
        answer3.setVisibility(View.VISIBLE);
        answer4.setVisibility(View.VISIBLE);
        fiftyFifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( mInterstitialAd!= null) {
                    Activity activityContext = QuestionActivity.this;
                    mInterstitialAd.show(activityContext);
                }
                loadInitAd();
                int fiftyPoints = (questionEarningPoints / 2);
                questionEarningPoints = (questionEarningPoints / 2);
                questionPoints.setText(" Points : " + String.valueOf(fiftyPoints));
                questionPoints.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_50, 0, 0, 0);
                if (answer1.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
                    answer1.setVisibility(View.GONE);
                }
                if (answer1.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
                    answer1.setVisibility(View.GONE);
                }
                if (answer2.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
                    answer2.setVisibility(View.GONE);
                }
                if (answer2.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
                    answer2.setVisibility(View.GONE);
                }
                if (answer3.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
                    answer3.setVisibility(View.GONE);
                }
                if (answer3.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
                    answer3.setVisibility(View.GONE);
                }
                if (answer4.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
                    answer4.setVisibility(View.GONE);
                }
                if (answer4.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
                    answer4.setVisibility(View.GONE);
                }
                Toast.makeText(QuestionActivity.this, "50/50 Mode Activated!", Toast.LENGTH_SHORT).show();
                fiftyFifty.setVisibility(View.GONE);
                rewardVideoBtn.setVisibility(View.GONE);
            }
        });
        rewardVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPaused){

                    hourglass.resumeTimer();
                    isPaused = false;
                    rewardVideoBtn.setText("Pause Timer");
                }else {
                    if (mRewardedAd != null) {
                        rewardRequested=false;
                        Activity activityContext = QuestionActivity.this;
                        mRewardedAd.show(activityContext, rewardItem -> {

                        });
                        isPaused = true;
                        rewardVideoBtn.setText("Play Timer");
                        hourglass.pauseTimer();
                    }else {
                        Toast.makeText(QuestionActivity.this,"Ad not loaded, can't pause time",Toast.LENGTH_LONG).show();
                    }

                    loadRewardedVideoAd();
                }



            }
        });

//        startAppAdVideoAd.setVideoListener(new VideoListener() {
//            @Override
//            public void onVideoCompleted() {
//
//                startAppAdVideoAd.loadAd();
//                if (answer1.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
//                    answer1.setVisibility(View.GONE);
//                }
//                if (answer1.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
//                    answer1.setVisibility(View.GONE);
//                }
//                if (answer2.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
//                    answer2.setVisibility(View.GONE);
//                }
//                if (answer2.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
//                    answer2.setVisibility(View.GONE);
//                }
//                if (answer3.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
//                    answer3.setVisibility(View.GONE);
//                }
//                if (answer3.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
//                    answer3.setVisibility(View.GONE);
//                }
//                if (answer4.getText().toString().equalsIgnoreCase(falseAnswerOne)) {
//                    answer4.setVisibility(View.GONE);
//                }
//                if (answer4.getText().toString().equalsIgnoreCase(falseAnswerTwo)) {
//                    answer4.setVisibility(View.GONE);
//                }
//                fiftyFifty.setVisibility(View.GONE);
//                rewardVideoBtn.setVisibility(View.GONE);
//                // Restart Timer
//                rewarded = 1;
//
//            }
//        });

        // Next Question if Submit Clicked
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hourglass!=null)
                    hourglass.pauseTimer();
                answer1.setVisibility(View.VISIBLE);
                answer2.setVisibility(View.VISIBLE);
                answer3.setVisibility(View.VISIBLE);
                answer4.setVisibility(View.VISIBLE);
                questionPoints.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    /******* Nothing is Checked *******/
                    Toast.makeText(QuestionActivity.this, getString(R.string.java_question_toast), Toast.LENGTH_LONG).show();
                } else {
                    // Get Submitted Answer
                    int selectedId = mRadioGroup.getCheckedRadioButtonId();
                    selectedRadioButton = (RadioButton) findViewById(selectedId);
                    submittedAnswer = selectedRadioButton.getText().toString();
                    /******* IF ANSWER IS RIGHT *******/
                    if (submittedAnswer.equals(rightAnswer)) {
                        rightAnswersNumber = rightAnswersNumber + 1;
                        earnedPoints = earnedPoints + questionEarningPoints;
                        // Add Points To User
                        int actualPoints = points.getInt("points", 0);
                        int newPoints = actualPoints + collectedPoints;
                        points.edit().putInt("points", newPoints).apply();
                        /* Display Alert Dialog That Answer is True && Clear Radio Group Check */
                        mRadioGroup.clearCheck();
                        if (actualQuestionNum == 0) {
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.correct);
                            mp.start();
                            AlertDialog.Builder alert = new AlertDialog.Builder(QuestionActivity.this)
                                    .setTitle(getString(R.string.java_question_nice_job))
                                    .setMessage(getString(R.string.java_question_congrats))
                                    .setPositiveButton(getString(R.string.java_question_show_score), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            goToScore();
                                        }
                                    })
                                    .setIcon(getDrawable(R.drawable.ic_clap))
                                    .setCancelable(false);
                            try {
                                alert.show();
                            } catch (WindowManager.BadTokenException e) {
                                e.getMessage();
                            }
                        } else {
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.correct);
                            mp.start();
                            AlertDialog.Builder alertt = new AlertDialog.Builder(QuestionActivity.this)
                                    .setTitle(getString(R.string.java_question_nice_job))
                                    .setMessage(getString(R.string.java_question_answer_true))
                                    .setPositiveButton(getString(R.string.java_question_next_question), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Go to next question
                                            getNextQuestion();
                                        }
                                    })
                                    .setIcon(getDrawable(R.drawable.ic_clap))
                                    .setCancelable(false);
                            try {
                                alertt.show();
                            } catch (WindowManager.BadTokenException e) {
                                e.getMessage();
                            }
                        }
                    } else {
                        /******* IF ANSWER IS FALSE *******/
                        earnedPoints = earnedPoints + 0;
                        mRadioGroup.clearCheck();
                        if (actualQuestionNum == 0) {
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                            mp.start();
                            AlertDialog.Builder allert = new AlertDialog.Builder(QuestionActivity.this)
                                    .setTitle(getString(R.string.java_question_sorry))
                                    .setMessage(getString(R.string.java_question_false))
                                    .setPositiveButton(getString(R.string.java_question_show_score), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            goToScore();
                                        }
                                    })
                                    .setIcon(getDrawable(R.drawable.ic_anxiety))
                                    .setCancelable(false);
                            try {
                                allert.show();
                            } catch (WindowManager.BadTokenException e) {
                                e.getMessage();
                            }
                        } else {
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                            mp.start();
                            AlertDialog.Builder aalert = new AlertDialog.Builder(QuestionActivity.this)
                                    .setTitle(getString(R.string.java_question_sorry))
                                    .setMessage(getString(R.string.java_question_false_good_luck))
                                    .setPositiveButton(getString(R.string.java_question_next_question), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Go to next question
                                            getNextQuestion();
                                        }
                                    })
                                    .setIcon(getDrawable(R.drawable.ic_anxiety))
                                    .setCancelable(false);
                            try {
                                aalert.show();
                            } catch (WindowManager.BadTokenException e) {
                                e.getMessage();
                            }
                        }
                    }
                    //                    if (mRewardedAd != null) {
                    //                        Activity activityContext = QuestionActivity.this;
                    //                        mRewardedAd.show(activityContext, rewardItem -> {
                    //                        });
                    //                    }
                    //                    loadRewardedVideoAd();
                }

            }
        });
        // if Stop QUIZ button is clicked
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToScore();
//                if (mRewardedAd != null) {
//                    Activity activityContext = QuestionActivity.this;
//                    mRewardedAd.show(activityContext, rewardItem -> {
//                    });
//                }
//                loadRewardedVideoAd();
            }
        });
        // Facebook Ads
        AudienceNetworkAds.initialize(this);
    }
    private InterstitialAd mInterstitialAd;
    void loadInitAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        String adId = initAD;
        InterstitialAd.load(this,adId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    void resetTimer(){
       if( hourglass!=null) {
           hourglass.pauseTimer();
           hourglass = null;
       }
        hourglass = new Hourglass(seconds*1000, 1000) {
            @Override
            public void onTimerTick(long timeRemaining) {
                // Update UI
                timer.setText(String.valueOf((timeRemaining/1000)));
                rewardVideoBtn.setText("Pause Timer");
            }



            @Override
            public void onTimerFinish() {
                // Timer finished
// If Time is Finished --> submit button Automatically
                timer.setText(getString(R.string.java_question_time_finished));
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    if (actualQuestionNum == 0) {
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                        mp.start();
                        AlertDialog.Builder alertDialogg = new AlertDialog.Builder(QuestionActivity.this)
                                .setTitle(getString(R.string.java_question_time_finished_title))
                                .setMessage(getString(R.string.java_question_time_finished_message))
                                .setPositiveButton(getString(R.string.java_question_show_my_score), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        goToScore();
                                    }
                                })
                                .setIcon(getDrawable(R.drawable.timer))
                                .setCancelable(false);
                        try {
                            alertDialogg.show();
                        } catch (WindowManager.BadTokenException e) {
                            e.getMessage();
                        }
                    } else {
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                        mp.start();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuestionActivity.this)
                                .setTitle(getString(R.string.java_question_time_finished_title))
                                .setMessage(getString(R.string.java_question_time_finished_message))
                                .setPositiveButton(getString(R.string.java_question_next_question_title), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Go to next question
                                        getNextQuestion();
                                    }
                                })
                                .setIcon(getDrawable(R.drawable.ic_warning))
                                .setCancelable(false);
                        try {
                            alertDialog.show();
                        } catch (WindowManager.BadTokenException e) {
                            e.getMessage();
                        }
                    }
                } else {
                    submitBtn.performClick();
                }

            }
        };
    }

    @Override
    protected void onDestroy() {
        if (facebookBannerAdView != null) {
            facebookBannerAdView.destroy();
        }


        super.onDestroy();

    }

    private void initialiseFaceBookBannerAds() {

        String ID = facebookBanner.getString("facebookBanner", "");
        facebookBannerAdView = new com.facebook.ads.AdView(this, ID, AdSize.BANNER_HEIGHT_50);


        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(facebookBannerAdView);

        // Request an ad
        facebookBannerAdView.loadAd();

    }


    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    // Parse JSON Function
    private void parseJson() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject record = jsonArray.getJSONObject(i);
                                String question = record.getString("question");
                                String trueAnswer = record.getString("trueAnswer");
                                String falseAnswer1 = record.getString("falseAnswer1");
                                String falseAnswer2 = record.getString("falseAnswer2");
                                String falseAnswer3 = record.getString("falseAnswer3");
                                int points = record.getInt("points");
                                Question newQuestion = new Question(question, trueAnswer, falseAnswer1, falseAnswer2, falseAnswer3, points);
                                questions.add(newQuestion);
                            }
                            // Choose a random Question to Show
                            Random random = new Random();
                            int randomNum = random.nextInt(questions.size());
                            // All Questions Size
                            allQuestions = questions.size();
                            // Set Text for Number Of Questions
                            Question firstQuestion = questions.get(randomNum);
                            i = 1;
                            actualQuestionNum = allQuestions - i;
                            numberOfQuestion.setText(String.valueOf(allQuestions - (actualQuestionNum)) + " / " + allQuestions);
                            // Get Question Text
                            theQuestion.setText(firstQuestion.getQuestion());
                            // Get True Answer
                            rightAnswer = firstQuestion.getTrueAnswer();
                            falseAnswerOne = firstQuestion.getFalseAnswer1();
                            falseAnswerTwo = firstQuestion.getFalseAnswer2();
                            questionEarningPoints = firstQuestion.getPoints();
                            totalQuizPoints = firstQuestion.getPoints();
                            // Create a List of Answer to Shuffle Them
                            List<String> listOfAnswers = new ArrayList<>();
                            listOfAnswers.add(firstQuestion.getTrueAnswer());
                            listOfAnswers.add(firstQuestion.getFalseAnswer1());
                            listOfAnswers.add(firstQuestion.getFalseAnswer2());
                            listOfAnswers.add(firstQuestion.getFalseAnswer3());
                            Collections.shuffle(listOfAnswers);
                            // Set Answers To Radio Buttons
                            answer1.setText(listOfAnswers.get(0));
                            answer2.setText(listOfAnswers.get(1));
                            answer3.setText(listOfAnswers.get(2));
                            answer4.setText(listOfAnswers.get(3));
                            questionPoints.setText(getString(R.string.java_question_questions_points) + String.valueOf(firstQuestion.getPoints()));
                            collectedPoints = firstQuestion.getPoints();
                            // Remove This Question From Array To Show Another Next One
                            questions.remove(randomNum);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void getNextQuestion() {
        if (fiftyFiftyOption.getString("fiftyFiftyOption", "").equals("yes")) {
            fiftyFifty.setVisibility(View.VISIBLE);
        } else {
            fiftyFifty.setVisibility(View.GONE);
        }
        if (rewardVideoOption.getString("rewardVideoOption", "").equals("yes")) {
            rewardVideoBtn.setVisibility(View.VISIBLE);
        } else {
            rewardVideoBtn.setVisibility(View.GONE);
        }
        answer1.setVisibility(View.VISIBLE);
        answer2.setVisibility(View.VISIBLE);
        answer3.setVisibility(View.VISIBLE);
        answer4.setVisibility(View.VISIBLE);
        // Restart Timer
//        hourglass.startTimer();
        // Get Next question
        Random random = new Random();
        int randomNum = random.nextInt(questions.size());
        // Set Text for Number Of Questions
        Question secondQuestion = questions.get(randomNum);
        i = i + 1;
        actualQuestionNum = allQuestions - i;
        numberOfQuestion.setText(String.valueOf(allQuestions - (actualQuestionNum)) + " / " + allQuestions);
        // Get Question Text
        theQuestion.setText(secondQuestion.getQuestion());
        // Get True Answer
        rightAnswer = secondQuestion.getTrueAnswer();
        falseAnswerOne = secondQuestion.getFalseAnswer1();
        falseAnswerTwo = secondQuestion.getFalseAnswer2();
        questionEarningPoints = secondQuestion.getPoints();
        totalQuizPoints = totalQuizPoints + secondQuestion.getPoints();
        // Create a List of Answer to Shuffle Them
        List<String> listOfAnswers = new ArrayList<>();
        listOfAnswers.add(secondQuestion.getTrueAnswer());
        listOfAnswers.add(secondQuestion.getFalseAnswer1());
        listOfAnswers.add(secondQuestion.getFalseAnswer2());
        listOfAnswers.add(secondQuestion.getFalseAnswer3());
        Collections.shuffle(listOfAnswers);
        // Set Answers To Radio Buttons
        answer1.setText(listOfAnswers.get(0));
        answer2.setText(listOfAnswers.get(1));
        answer3.setText(listOfAnswers.get(2));
        answer4.setText(listOfAnswers.get(3));
        questionPoints.setText(getString(R.string.java_question_questions_points) + String.valueOf(secondQuestion.getPoints()));
        collectedPoints = secondQuestion.getPoints();
        // Remove This Question From Array To Show Another Next One
        questions.remove(randomNum);
        // Restart Timer
        resetTimer();
        hourglass.startTimer();
    }

    // Change Player Points
    public void goToScore() {
        Intent homePage = new Intent(QuestionActivity.this, ScoreActivity.class);
        homePage.putExtra("earnedPoints", String.valueOf(earnedPoints));
        homePage.putExtra("totalPoints", String.valueOf(totalQuizPoints));
        homePage.putExtra("allQuestions", String.valueOf(allQuestions));
        homePage.putExtra("trueAnswers", String.valueOf(rightAnswersNumber));
        homePage.putExtra("categoryId", categoryId);
        homePage.putExtra("categoryName", categoryName);
        homePage.putExtra("categoryLevel", categoryLevel);
        homePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePage);
        finish();
    }
    boolean rewardRequested=false;
    private void loadRewardedVideoAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        Log.e("RewardAdID",admobVideo.getString("admobVideo","")+"");
        String rewardID = admobVideo.getString("admobVideo", "");
        Log.d("rewardID", "run: "+rewardID);
        Log.e("RewardAdID",admobVideo.getString("admobVideo","")+"");
        if (SplashScreenActivity.rewardAD!=null && !SplashScreenActivity.rewardAD.isEmpty())
        rewardRequested=true;
        RewardedAd.load(this, SplashScreenActivity.rewardAD,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("Reward Error",loadAdError.getMessage()+"");
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onResume() {


        super.onResume();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }



    @Override
    public void onBackPressed() {
        Intent main = new Intent(QuestionActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
        super.onBackPressed();
    }

}
