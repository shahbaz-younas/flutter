    package com.rifcode.randochat.Views;

    import android.annotation.SuppressLint;
    import android.content.ActivityNotFoundException;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.res.Configuration;
    import android.content.res.Resources;
    import android.graphics.Typeface;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.text.SpannableStringBuilder;
    import android.util.DisplayMetrics;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.ScrollView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;

    import com.android.billingclient.api.BillingClient;
    import com.android.billingclient.api.BillingResult;
    import com.android.billingclient.api.Purchase;
    import com.android.billingclient.api.PurchasesUpdatedListener;
    import com.facebook.ads.Ad;
    import com.facebook.ads.AdError;
    import com.facebook.ads.AdOptionsView;
    import com.facebook.ads.AdSize;
    import com.facebook.ads.AdView;
    import com.facebook.ads.AudienceNetworkAds;
    import com.facebook.ads.InterstitialAd;
    import com.facebook.ads.MediaView;
    import com.facebook.ads.NativeAd;
    import com.facebook.ads.NativeAdLayout;
    import com.facebook.ads.NativeAdListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.analytics.FirebaseAnalytics;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.ChildEventListener;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.rifcode.randochat.R;
    import com.rifcode.randochat.Utils.CustomTypefaceSpan;
    import com.rifcode.randochat.Utils.DataFire;
    import com.rifcode.randochat.Utils.DialogSubscriptions;
    import com.skyfishjy.library.RippleBackground;
    import com.squareup.picasso.Callback;
    import com.squareup.picasso.NetworkPolicy;
    import com.squareup.picasso.Picasso;

    import java.text.DateFormat;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    import de.hdodenhof.circleimageview.CircleImageView;

    import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

    public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private NativeAd nativeAd;
    private RippleBackground rippleBackground;
    private Button btnStartChat;
    private Boolean isStart =false;
    private ImageView imgvWoman,imgvMan,imgvChatVideo,imgvChatText,imgvBoth;
    private String gender="both";
    private TextView tvWoman,tvMan,tvBoth;
    private LinearLayout lySelect;
    private String typeChat="video";
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private DatabaseReference dbusers;
    private DatabaseReference dbsearch;
    private String myuserID;
    private CircleImageView imgvUserRipBack;
    private FirebaseUser currentUser;
    private Typeface fontUbuntuRegular;
    private CustomTypefaceSpan typefaceSpanUbuntuRegular;
    private InterstitialAd mInterstitialAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private ScrollView scvSearching;
    private AdView adViewBanner;
    private LinearLayout adContainer;
    private boolean isSearch=false;
    private DialogSubscriptions dialogSubscriptions;
    private DataFire dataFire;

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("changeLanguage", MODE_PRIVATE);
        String language_key = prefs.getString("language_key", null);
        if(language_key!=null){
            changeLanguage(language_key);
        }
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(MainActivity.this);

        //facebook ads interstital ad
        mInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.Interstitial_FacebbokAds));
        // load the ad
        mInterstitialAd.loadAd();

        dbusers = FirebaseDatabase.getInstance().getReference().child("Users");
        dbsearch = FirebaseDatabase.getInstance().getReference().child("Search");


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            myuserID = mAuth.getUid();
        }

        fontUbuntuRegular = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Regular.ttf");

        typefaceSpanUbuntuRegular = new CustomTypefaceSpan("Ubuntu-Regular", fontUbuntuRegular);

        // widgets
        rippleBackground= findViewById(R.id.ripback);
        btnStartChat= findViewById(R.id.btnStartChat);
        imgvMan = findViewById(R.id.imgvManSignup);
        imgvWoman= findViewById(R.id.imgvWomanSignup);
        tvWoman= findViewById(R.id.tvWoman);
        tvMan= findViewById(R.id.tvMen);
        imgvChatText = findViewById(R.id.imgvChatText);
        imgvChatVideo = findViewById(R.id.imgvChatVideo);
        lySelect = findViewById(R.id.lySelect);
        imgvUserRipBack = findViewById(R.id.imgvUserRipBack);
        nativeAdLayout = findViewById(R.id.native_ad_container);
        scvSearching = findViewById(R.id.scvSearching);
        // Find the Ad Container
        adContainer = findViewById(R.id.banner_container);
        imgvBoth = findViewById(R.id.imgvBoth);
        tvBoth = findViewById(R.id.tvBoth);

        // change font toolbar
        TextView textView1 = (TextView) toolbar.getChildAt(0); //title
        //TextView textView2 = (TextView) toolbar.getChildAt(1); //subtitle
        Typeface PoppinsRegular = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Medium.ttf");
        textView1.setTypeface(PoppinsRegular);

        dataFire = new DataFire();

        // check date Finish Subscribe for user //
            if(dataFire.getCurrentUser()!=null) {
                dataFire.getDbRefUsers().child(dataFire.getUserID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String purchase = String.valueOf(snapshot.child("purchase").getValue());

                        if (purchase.equals("true")) {
                            String dateFinishSubscribe = String.valueOf(snapshot.child("dateFinishSubscribe").getValue());

                            if (!dateFinishSubscribe.equals("lifetime")) {
                                DateFormat dateNowSub = new SimpleDateFormat("yyyy:MM:dd:HH:mm", Locale.getDefault());
                                String dateEnterNow = dateNowSub.format(new Date());

                                Date convertedDate = new Date();
                                Date convertedDate2 = new Date();
                                try {
                                    convertedDate = dateNowSub.parse(dateFinishSubscribe);
                                    convertedDate2 = dateNowSub.parse(dateEnterNow);
                                    if (convertedDate2.after(convertedDate)) {
                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("purchase")
                                                .setValue("false");
                                    }
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        // end check date Finish Subscribe for user //

            if(currentUser!=null){

            dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final String image = String.valueOf(dataSnapshot.child("image").getValue());
                    if(dataSnapshot.hasChild("Anounymous")) {

                        final String Anounymous = String.valueOf(dataSnapshot.child("Anounymous").getValue());
                        if(Anounymous.equals("true")){
                                Picasso.get().load(R.drawable.portrait_placeholder).into(imgvUserRipBack);
                        }else{
                            if(!image.equals("default")) {

                                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.portrait_placeholder).into(imgvUserRipBack, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(image).placeholder(R.drawable.portrait_placeholder).into(imgvUserRipBack);
                                    }
                                });
                            }
                        }
                    }else {
                        if (!image.equals("default")) {

                            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.portrait_placeholder).into(imgvUserRipBack, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.portrait_placeholder).into(imgvUserRipBack);
                                }
                            });
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        imgvChatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvChatText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                imgvChatVideo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                typeChat = "text";
            }
        });

        imgvChatVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvChatText.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                imgvChatVideo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                typeChat = "video";
            }
        });

        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                dialogSubscriptions= new DialogSubscriptions(MainActivity.this);

                if(isNetworkConnectionAvailable()) {

                    if (isStart == false) {

                        if (gender.equals("girl")) {

                            dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("purchase")) {
                                        dialogSubscriptions.dialog_subscription(MainActivity.this);
                                    } else {
                                        String purchase = String.valueOf(dataSnapshot.child("purchase").getValue());
                                        if (!purchase.equals("true")) {
                                            // subscription :
                                            dialogSubscriptions.dialog_subscription(MainActivity.this);
                                        }
                                        else {

                                            isSearch = true;

                                            scvSearching.setVisibility(View.GONE);

                                            getSupportActionBar().setTitle(R.string.searching);

                                            dbsearch.child(myuserID).child("type_chat").setValue(typeChat);

                                            dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dbsearch.child(myuserID).child("gender")
                                                            .setValue(String.valueOf(dataSnapshot.child("sex").getValue()));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            dbsearch.child(myuserID).child("genderMeet")
                                                    .setValue(gender);

                                            btnStartChat.setText(R.string.stop);
                                            isStart = true;
                                            rippleBackground.startRippleAnimation();

                                            lySelect.setVisibility(View.GONE);
                                            adContainer.setVisibility(View.VISIBLE);
                                            rippleBackground.setVisibility(View.VISIBLE);
                                            getSearchResult(gender, typeChat);

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                        else{
                            isSearch = true;

                            scvSearching.setVisibility(View.GONE);

                            getSupportActionBar().setTitle(R.string.searching);

                            dbsearch.child(myuserID).child("type_chat").setValue(typeChat);

                            dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dbsearch.child(myuserID).child("gender")
                                            .setValue(String.valueOf(dataSnapshot.child("sex").getValue()));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            dbsearch.child(myuserID).child("genderMeet")
                                    .setValue(gender);

                            btnStartChat.setText(R.string.stop);
                            isStart = true;
                            rippleBackground.startRippleAnimation();

                            lySelect.setVisibility(View.GONE);
                            adContainer.setVisibility(View.VISIBLE);
                            rippleBackground.setVisibility(View.VISIBLE);
                            getSearchResult(gender, typeChat);
                        }
                    } else {

                        isSearch=false;

                        scvSearching.setVisibility(View.VISIBLE);

                        getSupportActionBar().setTitle(R.string.app_name);
                        adContainer.setVisibility(View.GONE);

                        btnStartChat.setText(R.string.start_chat);
                        isStart = false;
                        rippleBackground.stopRippleAnimation();
                        lySelect.setVisibility(View.VISIBLE);
                        rippleBackground.setVisibility(View.GONE);
                        dbsearch.child(myuserID).removeValue();
                    }
                }
            }
        });

        imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
        imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
        imgvBoth.setImageResource(R.drawable.bothgenders_selected);
        gender = "both";
            tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple_fill));
            tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message));
            tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba));
            tvBoth.setTextColor(getResources().getColor(R.color.colorWhite));
            tvMan.setTextColor(getResources().getColor(R.color.colorBlue));
            tvWoman.setTextColor(getResources().getColor(R.color.colorRedBara));

        tvBoth.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                imgvBoth.setImageResource(R.drawable.bothgenders_selected);
                gender = "both";
                tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple_fill));
                tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message));
                tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba));
                tvBoth.setTextColor(getResources().getColor(R.color.colorWhite));
                tvMan.setTextColor(getResources().getColor(R.color.colorBlue));
                tvWoman.setTextColor(getResources().getColor(R.color.colorRedBara));
            }
        });

        imgvBoth.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                imgvBoth.setImageResource(R.drawable.bothgenders_selected);
                gender = "both";
                tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple_fill));
                tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message));
                tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba));
                tvBoth.setTextColor(getResources().getColor(R.color.colorWhite));
                tvMan.setTextColor(getResources().getColor(R.color.colorBlue));
                tvWoman.setTextColor(getResources().getColor(R.color.colorRedBara));
            }
        });


        imgvMan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                imgvBoth.setImageResource(R.drawable.bothgenders_noselected);
                gender = "guy";
                tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message_fill));
                tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple));
                tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba));

                tvBoth.setTextColor(getResources().getColor(R.color.colorDeepPurple));
                tvMan.setTextColor(getResources().getColor(R.color.colorWhite));
                tvWoman.setTextColor(getResources().getColor(R.color.colorRedBara));
            }
        });

        imgvWoman.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                imgvBoth.setImageResource(R.drawable.bothgenders_noselected);
                gender = "girl";
                tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba_fill));
                tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple));
                tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message));


                tvBoth.setTextColor(getResources().getColor(R.color.colorDeepPurple));
                tvMan.setTextColor(getResources().getColor(R.color.colorBlue));
                tvWoman.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });

        tvMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                imgvBoth.setImageResource(R.drawable.bothgenders_noselected);

                gender = "guy";

                tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message_fill));
                tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple));
                tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba));


                tvBoth.setTextColor(getResources().getColor(R.color.colorDeepPurple));
                tvMan.setTextColor(getResources().getColor(R.color.colorWhite));
                tvWoman.setTextColor(getResources().getColor(R.color.colorRedBara));
            }
        });

        tvWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                imgvBoth.setImageResource(R.drawable.bothgenders_noselected);

                gender = "girl";

                tvWoman.setBackground(getResources().getDrawable(R.drawable.corner_barba_fill));
                tvBoth.setBackground(getResources().getDrawable(R.drawable.corner_purple));
                tvMan.setBackground(getResources().getDrawable(R.drawable.corner_message));

                tvBoth.setTextColor(getResources().getColor(R.color.colorDeepPurple));
                tvMan.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvWoman.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });

        if(currentUser!=null) {
            dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild("purchase")) {

                        nativeAdLayout.setVisibility(View.VISIBLE);

                        loadNativeAd();
                        showAdWithDelay();
                        bannerFacebbok();

                    } else {

                        String purchase = String.valueOf(dataSnapshot.child("purchase").getValue());
                        if (purchase.equals("false")) {

                            nativeAdLayout.setVisibility(View.VISIBLE);

                            loadNativeAd();
                            showAdWithDelay();
                            bannerFacebbok();

                        } else {
                            nativeAdLayout.setVisibility(View.GONE);
                            adContainer.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void changeLanguage(String codeLang){

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(codeLang.toLowerCase()));
        }else{
            conf.locale = new Locale(codeLang.toLowerCase());
        }
        res.updateConfiguration(conf,dm);
    }

    private void bannerFacebbok(){

        adViewBanner = new AdView(MainActivity.this, getString(R.string.Banner_ads_facebook), AdSize.BANNER_HEIGHT_50);

// Add the ad view to your activity layout
        adContainer.addView(adViewBanner);

// Request an ad
        adViewBanner.loadAd();
    }

    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, getString(R.string.NativeAds_Facebook));

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e("nativeAdListener", "Native ad finished downloading all assets.");
            }
            
            @Override
            public void onError(Ad ad, AdError adError) {
                
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d("nativeAdListener", "Native ad is loaded and ready to be displayed!");
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d("nativeAdListener", "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d("nativeAdListener", "Native ad impression logged!");
            }
        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void inflateAd(NativeAd nativeAd) {


        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    private void showAdWithDelay() {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if(mInterstitialAd == null || !mInterstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if(mInterstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                mInterstitialAd.show();
            }
        }, 1000 * 60 * 3); // Show the ad after 3 minutes
    }
    
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        dialogSubscriptions= new DialogSubscriptions(MainActivity.this);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            loginActActivity();
        }else{

            isSearch=false;
            getSupportActionBar().setTitle(R.string.app_name);
            isStart = false;
            scvSearching.setVisibility(View.VISIBLE);
            lySelect.setVisibility(View.VISIBLE);
            rippleBackground.setVisibility(View.GONE);
            rippleBackground.stopRippleAnimation();
            btnStartChat.setText(R.string.start_chat);
            adContainer.setVisibility(View.GONE);

            //Toast.makeText(this, typeChat, Toast.LENGTH_SHORT).show();

            dbsearch.child(myuserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("chatnow")) {

                        String chatnow = dataSnapshot.child("chatnow").getValue().toString();
                        String type_chat = dataSnapshot.child("type_chat").getValue().toString();

                        if(type_chat.equals("text")) {

                            Intent chatTextActIntent = new Intent(MainActivity.this, ChatTextActivity.class);
                            chatTextActIntent.putExtra("userIDvisited", chatnow);
                            startActivity(chatTextActIntent);

                            dbsearch.child(myuserID).removeValue();
                            dbsearch.child(chatnow).removeValue();
                            isStart = false;
                            scvSearching.setVisibility(View.VISIBLE);
                            lySelect.setVisibility(View.VISIBLE);
                            rippleBackground.setVisibility(View.GONE);
                            btnStartChat.setText(R.string.start_chat);
                           adContainer.setVisibility(View.GONE);

                        }else
                            if(type_chat.equals("video"))
                            {
                                if(dataSnapshot.hasChild("video_room_id")) {
                                    String video_room_id = dataSnapshot.child("video_room_id").getValue().toString();
                                    Intent chatTextActIntent = new Intent(MainActivity.this, VideoChatViewActivity.class);
                                    chatTextActIntent.putExtra("userIDvisited", chatnow);
                                    chatTextActIntent.putExtra("video_room_id", video_room_id);
                                    startActivity(chatTextActIntent);


                                    dbsearch.child(myuserID).removeValue();
                                    dbsearch.child(chatnow).removeValue();
                                    isStart = false;
                                    scvSearching.setVisibility(View.VISIBLE);
                                    lySelect.setVisibility(View.VISIBLE);
                                    rippleBackground.setVisibility(View.GONE);
                                    btnStartChat.setText(R.string.start_chat);
                                    adContainer.setVisibility(View.GONE);

                                }

                            }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    private  void loginActActivity(){
        // if user used com first time :
        Intent loginActIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginActIntent);

        finish();
    }

    private void getSearchResult(final String genderSelected, final String typeChatSelected){

        if(gender.equals("both")){
            dbsearch.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                    if (!dataSnapshot.getKey().equals(myuserID) && dataSnapshot.hasChild("type_chat")
                            && dataSnapshot.hasChild("gender")
                    ) {

                        if (dataSnapshot.child("type_chat").getValue().equals(typeChatSelected)) {

                            if (dataSnapshot.child("type_chat").getValue().equals("text")) {
                                isSearch = false;

                                rippleBackground.stopRippleAnimation();

                                Intent chatTextActIntent = new Intent(MainActivity.this, ChatTextActivity.class);
                                chatTextActIntent.putExtra("userIDvisited", dataSnapshot.getKey());
                                startActivity(chatTextActIntent);

                                dbsearch.child(dataSnapshot.getKey().toString()).child("chatnow").setValue(myuserID);
                                dbsearch.child(myuserID).child("chatnow").setValue(dataSnapshot.getKey());


                            } else {
                                isSearch = false;

                                rippleBackground.stopRippleAnimation();

                                Intent chatTextActIntent = new Intent(MainActivity.this, VideoChatViewActivity.class);
                                chatTextActIntent.putExtra("userIDvisited", dataSnapshot.getKey());
                                chatTextActIntent.putExtra("video_room_id", dataSnapshot.getKey());
                                startActivity(chatTextActIntent);

                                dbsearch.child(dataSnapshot.getKey()).child("chatnow").setValue(myuserID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dbsearch.child(dataSnapshot.getKey()).child("video_room_id").setValue(dataSnapshot.getKey());
                                        dbsearch.child(myuserID).child("video_room_id").setValue(dataSnapshot.getKey());
                                        dbsearch.child(myuserID).child("chatnow").setValue(dataSnapshot.getKey());

                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            dbsearch.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                    if (!dataSnapshot.getKey().equals(myuserID) && dataSnapshot.hasChild("type_chat")
                            && dataSnapshot.hasChild("gender")  && dataSnapshot.hasChild("genderMeet")
                    ) {

                        if (dataSnapshot.child("gender").getValue().equals(genderSelected) && !dataSnapshot.child("genderMeet").getValue().equals(genderSelected)
                                && dataSnapshot.child("type_chat").getValue().equals(typeChatSelected)) {

                            if (dataSnapshot.child("type_chat").getValue().equals("text")) {
                                isSearch = false;

                                rippleBackground.stopRippleAnimation();

                                Intent chatTextActIntent = new Intent(MainActivity.this, ChatTextActivity.class);
                                chatTextActIntent.putExtra("userIDvisited", dataSnapshot.getKey());
                                startActivity(chatTextActIntent);

                                dbsearch.child(dataSnapshot.getKey().toString()).child("chatnow").setValue(myuserID);
                                dbsearch.child(myuserID).child("chatnow").setValue(dataSnapshot.getKey());


                            } else {
                                isSearch = false;

                                rippleBackground.stopRippleAnimation();

                                Intent chatTextActIntent = new Intent(MainActivity.this, VideoChatViewActivity.class);
                                chatTextActIntent.putExtra("userIDvisited", dataSnapshot.getKey());
                                chatTextActIntent.putExtra("video_room_id", dataSnapshot.getKey());
                                startActivity(chatTextActIntent);

                                dbsearch.child(dataSnapshot.getKey()).child("chatnow").setValue(myuserID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dbsearch.child(dataSnapshot.getKey()).child("video_room_id").setValue(dataSnapshot.getKey());
                                        dbsearch.child(myuserID).child("video_room_id").setValue(dataSnapshot.getKey());
                                        dbsearch.child(myuserID).child("chatnow").setValue(dataSnapshot.getKey());

                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    //// insert menu to main activity:
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        // change font menu
        for (int i = 0; i <menu.getItem(2).getSubMenu().size(); i++) {
            MenuItem menuItem = menu.getItem(2).getSubMenu().getItem(i);
            SpannableStringBuilder spannableTitle = new SpannableStringBuilder(menuItem.getTitle().toString());
            spannableTitle.setSpan(typefaceSpanUbuntuRegular, 0, spannableTitle.length(), 0);
            menuItem.setTitle(spannableTitle);
        }

        return true;
    }

    /// selected items menu:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int check = item.getItemId();
        switch(check) {

            case R.id.account:
                Intent ProfileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(ProfileActivity);
                break;

            case R.id.pp:

                Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
                startActivity(intent);

                break;

            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                loginActActivity();

                break;

            case R.id.shareapp:

                onClickShareApp();

                break;

            case R.id.subscrition:

                subscriptions();

                break;

            case R.id.rateapp:
                launchMarket();
                break;

            default:
        }
        return super.onOptionsItemSelected(item);

    }

    private void subscriptions() {
            dialogSubscriptions= new DialogSubscriptions(MainActivity.this);
        dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("purchase")){
                    dialogSubscriptions.dialog_subscription(MainActivity.this);
                }else {
                    String purchase = String.valueOf(dataSnapshot.child("purchase").getValue());
                    if (!purchase.equals("true")) {
                        // subscription :
                        dialogSubscriptions.dialog_subscription(MainActivity.this);
                    }else{
                        Toast.makeText(MainActivity.this, getString(R.string.yorsbc), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(this, " unable to find market com", Toast.LENGTH_LONG).show();
        }
    }


    public void onClickShareApp(){
        Intent sharePst = new Intent(Intent.ACTION_SEND);
        sharePst.setType("text/plain");
        sharePst.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app)+"https://play.google.com/store/apps/details?id="+getPackageName());

        startActivity(Intent.createChooser(sharePst,getString(R.string.choose_app_share)));
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");

            return true;
        }
        else{
            Log.d("Network","Not Connected");
            Toast.makeText(this, getString(R.string.trt), Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(currentUser!=null)
            dbsearch.child(myuserID).removeValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(currentUser!=null)
//            dbsearch.child(myuserID).removeValue();
        if(currentUser!=null && isSearch==true)
            dbsearch.child(myuserID).removeValue();
    }

    @Override
    protected void onDestroy() {
        if (adViewBanner != null) {
            adViewBanner.destroy();
        }
        super.onDestroy();
        if(currentUser!=null && isSearch==true)
            dbsearch.child(myuserID).removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(currentUser!=null)
//        dbsearch.child(myuserID).removeValue();
        if(currentUser!=null && isSearch==true)
            dbsearch.child(myuserID).removeValue();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            dialogSubscriptions.handlePurchases(list);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = dialogSubscriptions.billingClient.queryPurchases(SUBS);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                dialogSubscriptions.handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
