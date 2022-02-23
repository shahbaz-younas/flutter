package com.rifcode.randochat.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.randochat.R;
import com.rifcode.randochat.Utils.Chat;
import com.rifcode.randochat.Utils.DialogUtils;

public class ChatTextActivity extends AppCompatActivity {

    private String userID;
    public static String hisID;
    private FirebaseUser user;
    private RecyclerView rcvMessages;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference dbrefHischat,dbrefMyChat;
    private DatabaseReference dbrefUsers;
    private ImageView imgvSend;
    private EditText edtMessage;
    private FirebaseRecyclerAdapter<Chat,chatViewHolder> chatRecyclerAdapte;
    private DatabaseReference dbMessagingHis,dbMessagingMy;
    //    private AdView adView;
    //private DatabaseReference dbrefnotifi;
    private View mViewInflate;
    private DatabaseReference dbReportAbuseOfContent;
    private DatabaseReference dbsearch;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String myuserID;
    private AdView adAdmobBannerChat;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private AdRequest adRequest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_text);

        dbReportAbuseOfContent = FirebaseDatabase.getInstance().getReference().child("ReportAbuse");
        dbsearch = FirebaseDatabase.getInstance().getReference().child("Search");
        adAdmobBannerChat = findViewById(R.id.adAdmobBannerChat);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myuserID = mAuth.getUid();

        widgets();
        rcvMessages = findViewById(R.id.rclViewMessages);
        rcvMessages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ChatTextActivity.this);
        rcvMessages.setLayoutManager(mLayoutManager);

        //dbrefnotifi = FirebaseDatabase.getInstance().getReference().child("Notifications");

        hisID = getIntent().getStringExtra("userIDvisited");


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        dbrefMyChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(userID).child(hisID);
        dbrefHischat = FirebaseDatabase.getInstance().getReference().child("Chat").child(hisID).child(userID);
        dbMessagingMy = FirebaseDatabase.getInstance().getReference().child("Messaging").child(userID).child(hisID);
        dbMessagingHis = FirebaseDatabase.getInstance().getReference().child("Messaging").child(hisID).child(userID);
        dbrefUsers = FirebaseDatabase.getInstance().getReference().child("Users");




        // interstial admob
        adRequest2 = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.InterstitialAdAdmobID), adRequest2,
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


        // admob ads banner
        adRequest = new AdRequest.Builder().build();


        dbrefUsers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String purchase = String.valueOf(dataSnapshot.child("purchase").getValue());

                if(purchase.equals("false")) {
                    adAdmobBannerChat.setVisibility(View.VISIBLE);
                    adAdmobBannerChat.loadAd(adRequest);
                }else
                    if(purchase.equals("true"))
                    {
                        adAdmobBannerChat.setVisibility(View.GONE);
                    }else{
                        adAdmobBannerChat.setVisibility(View.VISIBLE);
                        adAdmobBannerChat.loadAd(adRequest);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbrefUsers.child(hisID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                String anonismos = String.valueOf(dataSnapshot.child("Anounymous").getValue());
                if(anonismos.equals("true")){
                    getSupportActionBar().setTitle(getString(R.string.dsd));

                }else{
                    getSupportActionBar().setTitle(username);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        onClick();
    }

    private void onClick() {
        imgvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = edtMessage.getText().toString();

                if (!txt.isEmpty())
                    sendMessage(txt);

            }
        });




    }

    private void widgets() {

        edtMessage = findViewById(R.id.txtSendMessage);
        imgvSend = findViewById(R.id.imgBtnSend);

    }

    private void sendMessage(String message){

        DatabaseReference dbrefMyChatsend = dbrefMyChat.push();
        DatabaseReference dbrefHIschattsend = dbrefHischat.push();
        //String pushMessage = String.valueOf(dbrefMyChatsend.getKey());
        String keypuch = dbrefMyChatsend.getKey();

        // Toast.makeText(this, pushMessage, Toast.LENGTH_SHORT).show();


        // send to my messages
        dbrefMyChatsend.child("from").setValue(userID);
        dbrefMyChatsend.child("message").setValue(message);

        dbrefHIschattsend.child("from").setValue(userID);
        dbrefHIschattsend.child("message").setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                edtMessage.setText("");
            }
        });

        rcvMessages.getAdapter().notifyDataSetChanged();
        rcvMessages.smoothScrollToPosition(rcvMessages.getAdapter().getItemCount());

        chatRecyclerAdapte.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = chatRecyclerAdapte.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rcvMessages.scrollToPosition(positionStart);
                }
            }
        });

        dbMessagingHis.child("TimeAgo").setValue(ServerValue.TIMESTAMP);
        dbMessagingMy.child("TimeAgo").setValue(ServerValue.TIMESTAMP);

        DatabaseReference dbrefreviewsmsg = FirebaseDatabase.getInstance().getReference().child("messages_reviews").child(keypuch);
        dbrefreviewsmsg.child("from").setValue(userID);
        dbrefreviewsmsg.child("to").setValue(hisID);
        dbrefreviewsmsg.child("message").setValue(message);
        dbrefreviewsmsg.child("time").setValue(-1*System.currentTimeMillis());

        // for notification messages
//        DatabaseReference notifRef = dbrefnotifi.child(hisID).push();
//        notifRef.child("From").setValue(userID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerview();

        dbrefUsers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String purchase = String.valueOf(dataSnapshot.child("purchase").getValue());
                if(purchase.equals("false")) {
                    //load so show the ads InterstitialAd
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(ChatTextActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        return true;
    }

    /// selected items menu:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int check = item.getItemId();
        switch(check) {
            case R.id.ic_abuse:
                dialogRateThisApp();
                break;

            default:
        }
        return super.onOptionsItemSelected(item);

    }

    private void dialogRateThisApp(){

        mViewInflate = getLayoutInflater().inflate(R.layout.dialog_abusecontent,null);
        TextView btnSentReport = mViewInflate.findViewById(R.id.btnSentReport);
        TextView btnCancel = mViewInflate.findViewById(R.id.btnCancel);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflate,ChatTextActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnSentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keypush =dbReportAbuseOfContent.push().getKey();
                dbReportAbuseOfContent.child(keypush).child("userID").setValue(myuserID);
                dbReportAbuseOfContent.child(keypush).child("reportUserID").setValue(hisID);
                dbReportAbuseOfContent.child(keypush).child("time").setValue(System.currentTimeMillis()*-1);
                alertDialog.dismiss();
                Toast.makeText(ChatTextActivity.this, getString(R.string.send_succes), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }


    private void firebaseRecyclerview(){

        chatRecyclerAdapte = new FirebaseRecyclerAdapter<Chat, chatViewHolder>(

                Chat.class
                ,R.layout.pack_msg_chat
                ,ChatTextActivity.chatViewHolder.class
                ,dbrefMyChat

        ) {
            @Override
            protected void populateViewHolder(final chatViewHolder viewHolder, Chat model, int position) {

                final String list_msg_id = getRef(position).getKey();

                //Toast.makeText(ChatActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                dbrefMyChat.child(list_msg_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String msg = String.valueOf(dataSnapshot.child("message").getValue());
                        final String from = String.valueOf(dataSnapshot.child("from").getValue());
                        viewHolder.setTextMyMessage(msg);

                        if(!from.equals(myuserID)){
                            viewHolder.lymymessage.setGravity(Gravity.LEFT);
                            viewHolder.tvMessage.setBackground(getResources().getDrawable(R.drawable.border_box_barared_gray_highdp));
                            viewHolder.tvMessage.setTextColor(getResources().getColor(R.color.colorPrimary));
                            dbrefUsers.child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String username = String.valueOf(dataSnapshot.child("username").getValue());
                                            String anonismos = String.valueOf(dataSnapshot.child("Anounymous").getValue());
                                            if(anonismos.equals("true")){
                                                viewHolder.setTexttvnamUser(getString(R.string.dsd));
                                            }else{
                                                viewHolder.setTexttvnamUser(username);
                                            }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else{
                            dbrefUsers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String username = String.valueOf(dataSnapshot.child("username").getValue());
                                    viewHolder.setTexttvnamUser(username);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            viewHolder.lymymessage.setGravity(Gravity.RIGHT);
                            viewHolder.tvMessage.setBackground(getResources().getDrawable(R.drawable.border_box_blue));
                            viewHolder.tvMessage.setTextColor(getResources().getColor(R.color.colorWhite));
                        }





                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        rcvMessages.setAdapter(chatRecyclerAdapte);
        rcvMessages.getAdapter().notifyDataSetChanged();
        rcvMessages.getLayoutManager().scrollToPosition(rcvMessages.getAdapter().getItemCount());
        chatRecyclerAdapte.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = chatRecyclerAdapte.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rcvMessages.scrollToPosition(positionStart);
                }

            }
        });
    }

    public static class chatViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView tvMessage;
        TextView tvusername ;
        LinearLayout lymymessage;
        public chatViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            tvMessage =  mView.findViewById(R.id.txtMyMessage);
            tvusername = mView.findViewById(R.id.tvnameUser);
            lymymessage = mView.findViewById(R.id.lymymessage);
        }

        public void setTextMyMessage(String myMessage){
            tvMessage.setText(myMessage);
        }

        public void setTexttvnamUser(String myMessage){

            tvusername.setText(myMessage);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        dbsearch.child(hisID).removeValue();
        dbsearch.child(myuserID).removeValue();

        Intent main = new Intent(ChatTextActivity.this, MainActivity.class);
        startActivity(main);
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbsearch.child(hisID).removeValue();
        dbsearch.child(myuserID).removeValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbsearch.child(hisID).removeValue();
        dbsearch.child(myuserID).removeValue();
    }
}
