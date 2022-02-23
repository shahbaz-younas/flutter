package com.rifcode.randochat.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.rifcode.randochat.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.facebook.FacebookSdk.getApplicationContext;

public class DialogSubscriptions {

    private  AlertDialog alertDialog;
    private   List<String> skuList;
    public  BillingClient billingClient;
    private  Activity Acttiv;
    private String id_sub;
    private DataFire dataFire;


    public DialogSubscriptions(Activity mconnet) {
        this.Acttiv = mconnet;

        dataFire=new DataFire();
        skuList = new ArrayList<>();
        billingClient = BillingClient.newBuilder(Acttiv)
                .enablePendingPurchases().setListener((PurchasesUpdatedListener) Acttiv).build();
        skuList.add(Acttiv.getString(R.string.SUBSCRIBE_ID_1_week));
        skuList.add(Acttiv.getString(R.string.SUBSCRIBE_ID_1_months));
        skuList.add(Acttiv.getString(R.string.SUBSCRIBE_ID_lifetime));
    }

    @SuppressLint("SetTextI18n")
    public  void dialog_subscription(final Activity activity) {

        View mViewInflatesubscription_gold = activity.getLayoutInflater().inflate(R.layout.dialog_subscriptions,null);

        final android.widget.Button btnBuysubscription_goldDialog = mViewInflatesubscription_gold.findViewById(R.id.btnSuscription);
        android.widget.Button btnCancelsubscription_goldDialog = mViewInflatesubscription_gold.findViewById(R.id.btnCancelSubscription);
        LinearLayout ly1Week = mViewInflatesubscription_gold.findViewById(R.id.ly1Week);
        LinearLayout ly1month = mViewInflatesubscription_gold.findViewById(R.id.ly1month);
        LinearLayout lyLifeTime = mViewInflatesubscription_gold.findViewById(R.id.ly1LifeTime);
        final TextView price1Month = mViewInflatesubscription_gold.findViewById(R.id.price1Month);
        final TextView price1week = mViewInflatesubscription_gold.findViewById(R.id.price1week);
        final TextView priceLifeTime = mViewInflatesubscription_gold.findViewById(R.id.priceLifeTime);

        AlertDialog.Builder alertDialogBuilderpost = DialogUtils.CustomAlertDialog(mViewInflatesubscription_gold,activity);
        alertDialog = alertDialogBuilderpost.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

        // initial layout
        id_sub = Acttiv.getString(R.string.SUBSCRIBE_ID_1_week);
        selectLayout(1,btnBuysubscription_goldDialog);



        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);

                    pricesGpay(price1week,price1Month,priceLifeTime);

                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }

                }

            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });

        ly1Week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id_sub = Acttiv.getString(R.string.SUBSCRIBE_ID_1_week);
                selectLayout(1,btnBuysubscription_goldDialog);
            }
        });
        ly1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_sub = Acttiv.getString(R.string.SUBSCRIBE_ID_1_months);
                selectLayout(2,btnBuysubscription_goldDialog);
            }
        });
        lyLifeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_sub = Acttiv.getString(R.string.SUBSCRIBE_ID_lifetime);
                selectLayout(3,btnBuysubscription_goldDialog);

            }
        });

        btnBuysubscription_goldDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(PremiumActivity.this,id_sub, Toast.LENGTH_SHORT).show();
                subscribe(id_sub);
            }
        });



        btnCancelsubscription_goldDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(billingClient!=null){
                    billingClient.endConnection();
                }
            }
        });
    }

    private void selectLayout(int pos, Button btnBuysubscription_goldDialog){

        if(pos==1) {
            btnBuysubscription_goldDialog.setText(Acttiv.getString(R.string.subscriptions)+" "+Acttiv.getString(R.string._1_week));
        }
        if(pos==2) {
            btnBuysubscription_goldDialog.setText(Acttiv.getString(R.string.subscriptions)+" "+Acttiv.getString(R.string._1_month));
        }
        if(pos==3) {
            btnBuysubscription_goldDialog.setText(Acttiv.getString(R.string.subscriptions)+" "+Acttiv.getString(R.string._life_time));
        }
    }

   public void handlePurchases(List<Purchase>  purchases) {

        for(Purchase purchase:purchases) {

            if (Acttiv.getString(R.string.SUBSCRIBE_ID_1_months).equals(purchase.getSku())
                    && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased/subscribed and not Acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();

                    billingClient.acknowledgePurchase(acknowledgePurchaseParams,
                            new AcknowledgePurchaseResponseListener() {
                                @Override
                                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                                    if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                                        //if purchase is acknowledged
                                        //then saved value in preference
                                        // subscribed  //
                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("purchase")
                                                .setValue("true");


                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("dateFinishSubscribe")
                                                .setValue(addHours(744));

                                        Toast.makeText(getApplicationContext(), Acttiv.getString(R.string.subseccf), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                }
                //else item is purchased and also acknowledged
                else {
                    // subscribed  //
                    dataFire.getDbRefUsers()
                            .child(dataFire.getUserID())
                            .child("purchase")
                            .setValue("true");


                    dataFire.getDbRefUsers()
                            .child(dataFire.getUserID())
                            .child("dateFinishSubscribe")
                            .setValue(addHours(744));
                    Toast.makeText(getApplicationContext(), Acttiv.getString(R.string.subseccf), Toast.LENGTH_LONG).show();

                }

            }

            if (Acttiv.getString(R.string.SUBSCRIBE_ID_lifetime).equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased/subscribed and not Acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();

                    billingClient.acknowledgePurchase(acknowledgePurchaseParams,
                            new AcknowledgePurchaseResponseListener() {
                                @Override
                                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                                    if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                                        //if purchase is acknowledged
                                        //then saved value in preference
                                        // subscribed  //
                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("purchase")
                                                .setValue("true");

                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("dateFinishSubscribe")
                                                .setValue("lifetime");

                                        Toast.makeText(getApplicationContext(), Acttiv.getString(R.string.subseccf), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                }
                //else item is purchased and also acknowledged
                else {
                    // subscribed  //
                    dataFire.getDbRefUsers()
                            .child(dataFire.getUserID())
                            .child("purchase")
                            .setValue("true");

                    dataFire.getDbRefUsers()
                            .child(dataFire.getUserID())
                            .child("dateFinishSubscribe")
                            .setValue("lifetime");
                    Toast.makeText(Acttiv, Acttiv.getString(R.string.subseccf), Toast.LENGTH_LONG).show();

                }

            }


            if (Acttiv.getString(R.string.SUBSCRIBE_ID_1_week).equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased/subscribed and not Acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();

                    billingClient.acknowledgePurchase(acknowledgePurchaseParams,
                            new AcknowledgePurchaseResponseListener() {
                                @Override
                                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                                    if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                                        //if purchase is acknowledged
                                        //then saved value in preference
                                        // subscribed  //
                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("purchase")
                                                .setValue("true");


                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("dateFinishSubscribe")
                                                .setValue(addHours(168));

                                        Toast.makeText(getApplicationContext(), Acttiv.getString(R.string.subseccf), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                }
                //else item is purchased and also acknowledged
                else {
                    // subscribed  //
                    dataFire.getDbRefUsers()
                            .child(dataFire.getUserID())
                            .child("purchase")
                            .setValue("true");


                    dataFire.getDbRefUsers()
                            .child(dataFire.getUserID())
                            .child("dateFinishSubscribe")
                            .setValue(addHours(168));
                    Toast.makeText(Acttiv, Acttiv.getString(R.string.subseccf), Toast.LENGTH_LONG).show();

                }

            }

        }


    }


    private String addHours(int hours){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        Date date = null;
        try {
            date = sdf.parse(currentDateandTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);

        return calendar.getTime().toString();
        //Toast.makeText(getActivity(), calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
    }



    private void pricesGpay(final TextView tvPriceSubscribe1Week, final TextView tvPriceSubscribeMonthly, final TextView tvPriceSubscribeLifeTime){

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                     @Nullable List<com.android.billingclient.api.SkuDetails> list) {
                        for (com.android.billingclient.api.SkuDetails details: list) {

                            String item = details.getSku();
                            String price = details.getPrice();
                            String description = details.getDescription();
                            String currencyCode = details.getPriceCurrencyCode();
                            String title = details.getTitle();

                            if (item.equals(Acttiv.getString(R.string.SUBSCRIBE_ID_1_week))){
                                tvPriceSubscribe1Week.setText(price);
                            }

                            if (item.equals(Acttiv.getString(R.string.SUBSCRIBE_ID_1_months))){
                                tvPriceSubscribeMonthly.setText(price);
                            }


                            if (item.equals(Acttiv.getString(R.string.SUBSCRIBE_ID_lifetime))){
                                tvPriceSubscribeLifeTime.setText(price);
                            }

                        }
                    }
                });


    }


    //initiate purchase on button click
    public void subscribe(final String id) {
        //initiate purchase on selected product/subscribe item click
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase(id);
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(Acttiv).enablePendingPurchases()
                    .setListener((PurchasesUpdatedListener) Acttiv).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(id);
                    } else {
                        Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }

    private void initiatePurchase(final String id) {

        List<String> skuList = new ArrayList<>();
        skuList.add(id);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SUBS);

        BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);

        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    billingClient.launchBillingFlow(Acttiv, flowParams);
                                } else {
                                    //try to add item/product id "s1" "s2" "s3" inside subscription in google play console
                                    Toast.makeText(getApplicationContext(), "Subscribe Item " + id + " not Found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Sorry Subscription not Supported. Please Update Play Store", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            //for old playconsole
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            //for new play console
            //To get key go to Developer Console > Select your app > Monetize > Monetization setup

            String base64Key = Acttiv.getString(R.string.LICENSE_KEY);
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }



}
