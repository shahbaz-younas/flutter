package com.rifcode.randochat.Views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kyleduo.switchbutton.SwitchButton;
import com.rifcode.randochat.R;
import com.rifcode.randochat.Utils.CustomTypefaceSpan;
import com.rifcode.randochat.Utils.DialogUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG ="ProfileActivity" ;
    private TextView tvUsernameSetting,tvAge,tvGenderProfile;
    private static final int GALLERY_PICK=1;
    private FirebaseAuth mAuth;
    private String myuserID;
    private FirebaseUser currentUser;
    private DatabaseReference dbusers;
    private SwitchButton swAnounymous;
    private ImageView btnchangeImage;
    private View mViewInflatedialogUploadImage;
    /// storage firebase
    private StorageReference mStorageImage;
    private CircleImageView cirImgSetting;
    SpannableStringBuilder SS;
//    TemplateView template;
    // progress bar
    private ProgressDialog proDialImage;
    private Button btnLanguageChange;
    private View mViewInflateChangeLanguage;
    private LinearLayout lyenlish,lyarabic,lyFrench,lyGerman,lyTurkish,lyPortuguese,lySpanish,lyhindi,lyturki;
    String codelang="";
    //private AdLoader adLoader;
    private NativeAd nativeAd;
    private FrameLayout fl_adplaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fl_adplaceholder=findViewById(R.id.fl_adplaceholder);
        mStorageImage = FirebaseStorage.getInstance().getReference();
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        // native ads admob :



        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Medium.ttf");
        SS = new SpannableStringBuilder(getString(R.string.profiefl));
        SS.setSpan(new CustomTypefaceSpan("Ubuntu-Medium", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(SS);

        tvUsernameSetting = findViewById(R.id.tvUsernameSetting);
        tvAge = findViewById(R.id.tvAge);
        tvGenderProfile = findViewById(R.id.tvGenderProfile);
        btnchangeImage = findViewById(R.id.btnChangeImage);
//        template = findViewById(R.id.my_template_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myuserID = mAuth.getUid();
        dbusers = FirebaseDatabase.getInstance().getReference().child("Users");
        swAnounymous = findViewById(R.id.swtAnuonomosly);
        cirImgSetting = findViewById(R.id.imgvSetting);
        btnLanguageChange = findViewById(R.id.btnLanguageChange);


        btnLanguageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChangeLanguage();
            }
        });

        swAnounymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(swAnounymous.isChecked()){
                    dbusers.child(myuserID).child("Anounymous").setValue("true");
                    tvUsernameSetting.setText("Anonymously");
                }else{
                    dbusers.child(myuserID).child("Anounymous").setValue("false");
                    dbusers.child(myuserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String username = String.valueOf(dataSnapshot.child("username").getValue());
                            String image = String.valueOf(dataSnapshot.child("image").getValue());
                            tvUsernameSetting.setText(username);
                            if(!image.equals("default")) {

                                //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                                Picasso.get().load(R.drawable.portrait_placeholder).into(cirImgSetting);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        dbusers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                tvUsernameSetting.setText(username);
                String age = String.valueOf(dataSnapshot.child("age").getValue());
                tvAge.setText(age);
                String gender = String.valueOf(dataSnapshot.child("sex").getValue());
                final String image = String.valueOf(dataSnapshot.child("image").getValue());
                final String purchase = String.valueOf(dataSnapshot.child("purchase").getValue());

                tvGenderProfile.setText(gender);

                if(purchase.equals("false")){
                    fl_adplaceholder.setVisibility(View.VISIBLE);

                }else{
                    fl_adplaceholder.setVisibility(View.GONE);
                }

                if(!image.equals("default")) {

                    //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.portrait_placeholder).into(cirImgSetting, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.portrait_placeholder).into(cirImgSetting);
                        }
                    });
                }


                if(dataSnapshot.hasChild("Anounymous") && dataSnapshot.child("Anounymous").getValue().toString().equals("true")){
                    swAnounymous.setChecked(true);
                    tvUsernameSetting.setText("Anonymously");
                    if(!image.equals("default")) {

                        //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                        Picasso.get().load(R.drawable.portrait_placeholder).into(cirImgSetting);
                    }
                }else
                if(dataSnapshot.hasChild("Anounymous") && dataSnapshot.child("Anounymous").getValue().toString().equals("false")){
                    swAnounymous.setChecked(false);
                    String username1 = String.valueOf(dataSnapshot.child("username").getValue());
                    tvUsernameSetting.setText(username1);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /// change image
        btnchangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUploadImage();
            }
        });

    }

    private void dialogChangeLanguage(){
        mViewInflateChangeLanguage = getLayoutInflater().inflate(R.layout.dialog_change_language,null);

        lyenlish = mViewInflateChangeLanguage.findViewById(R.id.lyenglish);
        lyarabic = mViewInflateChangeLanguage.findViewById(R.id.lyArabic);
        lyFrench = mViewInflateChangeLanguage.findViewById(R.id.lyFrench);
        lyGerman = mViewInflateChangeLanguage.findViewById(R.id.lyGerman);
        lyTurkish = mViewInflateChangeLanguage.findViewById(R.id.lyTurkish);
        lyPortuguese = mViewInflateChangeLanguage.findViewById(R.id.lyPortuguese);
        lySpanish = mViewInflateChangeLanguage.findViewById(R.id.lySpanish);


        lyhindi = mViewInflateChangeLanguage.findViewById(R.id.lyhindi);
        lyturki = mViewInflateChangeLanguage.findViewById(R.id.lyturki);

        Button btnSavelanguage = mViewInflateChangeLanguage.findViewById(R.id.btnSavelanguage);
        Button btnCancelDialogLang = mViewInflateChangeLanguage.findViewById(R.id.btnCancelDialogLang);
        final TextView tvselectedlang = mViewInflateChangeLanguage.findViewById(R.id.tvselectedlang);

        AlertDialog.Builder alertDialogBuilderChangeLang = DialogUtils.CustomAlertDialog(mViewInflateChangeLanguage
                ,ProfileActivity.this);
        final android.app.AlertDialog alertDialogChangeLang = alertDialogBuilderChangeLang.create();
        alertDialogChangeLang.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogChangeLang.setCancelable(true);
        alertDialogChangeLang.show();

        btnCancelDialogLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogChangeLang.dismiss();
            }
        });

        lyturki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="tr";
                tvselectedlang.setText(R.string.turkish);
            }
        });

        lyhindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="hi";
                tvselectedlang.setText(R.string.hindi);
            }
        });

        lyenlish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="en";
                tvselectedlang.setText(R.string.english);
            }
        });
        lyarabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="ar";
                tvselectedlang.setText(R.string.arabic);

            }
        });
        lyFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="fr";
                tvselectedlang.setText(R.string.french);

            }
        });
        lyGerman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="de";
                tvselectedlang.setText(R.string.german);

            }
        });
        lyPortuguese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="pt";
                tvselectedlang.setText(R.string.portuguese);

            }
        });
        lyTurkish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="ru";
                tvselectedlang.setText(R.string.russian);

            }
        });
        lySpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="es";
                tvselectedlang.setText(R.string.spanish);

            }
        });
        btnSavelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, getString(R.string.change_lang_sucs), Toast.LENGTH_SHORT).show();
                changeLanguage(codelang);
            }
        });
    }

    private void changeLanguage(String codeLang){
        SharedPreferences.Editor editor = getSharedPreferences("changeLanguage", MODE_PRIVATE).edit();
        editor.putString("language_key", codeLang);
        editor.apply();

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(codeLang.toLowerCase()));
        }else{
            conf.locale = new Locale(codeLang.toLowerCase());
        }
        res.updateConfiguration(conf,dm);

        // restart app
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    private void dialogUploadImage(){

        mViewInflatedialogUploadImage= getLayoutInflater().inflate(R.layout.dialog_warning_uploadimage,null);
        final CheckBox cb = mViewInflatedialogUploadImage.findViewById(R.id.cbCheckImageUpload);
        Button btnupload = mViewInflatedialogUploadImage.findViewById(R.id.btnUpload);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogUploadImage,this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb.isChecked()){
                    /// open galery :
                    alertDialog.dismiss();
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent,getString(R.string.select_gallery)),GALLERY_PICK);

                }else{
                    Toast.makeText(ProfileActivity.this, getString(R.string.risk_deletaccount), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            final Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                /// progress Dialog :
                proDialImage = new ProgressDialog(ProfileActivity.this);
                proDialImage.setMessage(getString(R.string.image_upload));
                proDialImage.setCanceledOnTouchOutside(false);

                proDialImage.show();

                final Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                // set id user to name image .jpg
                String idUserForImage= myuserID;


                //// Bitmap Upload image//////////////////////////////
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(160)
                        .setMaxWidth(160)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //////////////////////// end upload //////////////////////////////////////

                final StorageReference filePath = mStorageImage.child("profile_image").child(idUserForImage+".jpg");


                Uri file = Uri.fromFile(new File(thumb_filePath.getAbsolutePath()));
                UploadTask uploadTask = filePath.putFile(file);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if (downloadUri != null) {

                                final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!

                                // images reviews
                                String puchKey =  FirebaseDatabase.getInstance().getReference().child("images_reviews").push().getKey();
                                HashMap<String, String> imagesReviewsMap = new HashMap<>();
                                imagesReviewsMap.put("userID", currentUser.getUid());
                                imagesReviewsMap.put("image", photoStringLink);
                                FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).setValue(imagesReviewsMap);
                                FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).child("time")
                                        .setValue(-1 * System.currentTimeMillis())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) { }
                                        });

                                Map updateHash_map = new HashMap<>();
                                updateHash_map.put("image",photoStringLink);
                                dbusers.child(myuserID).updateChildren(updateHash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, R.string.success_upload_image, Toast.LENGTH_SHORT).show();
                                            proDialImage.dismiss();

                                            Picasso.get().load(photoStringLink).networkPolicy(NetworkPolicy.OFFLINE)
                                                    .placeholder(R.drawable.portrait_placeholder).into(cirImgSetting, new Callback() {
                                                @Override
                                                public void onSuccess() {

                                                }
                                                @Override
                                                public void onError(Exception e) {
                                                    Picasso.get().load(photoStringLink).placeholder(R.drawable.portrait_placeholder).into(cirImgSetting);
                                                }
                                            });

                                        }
                                    }
                                });

                            }

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: "+error);
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshAd();
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.NativeAdmobID));

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    // OnLoadedListener implementation.
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        boolean isDestroyed = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = isDestroyed();
                        }
                        if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                            nativeAd.destroy();
                            return;
                        }
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
                        if (ProfileActivity.this.nativeAd != null) {
                            ProfileActivity.this.nativeAd.destroy();
                        }
                        ProfileActivity.this.nativeAd = nativeAd;
                        NativeAdView adView =
                                (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        fl_adplaceholder.removeAllViews();
                        fl_adplaceholder.addView(adView);
                    }
                });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(false).build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                                        String error =
                                                String.format(
                                                        "domain: %s, code: %d, message: %s",
                                                        loadAdError.getDomain(),
                                                        loadAdError.getCode(),
                                                        loadAdError.getMessage());
                                        Toast.makeText(
                                                ProfileActivity.this,
                                                "Failed to load native ad with error " + error,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                        .build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}
