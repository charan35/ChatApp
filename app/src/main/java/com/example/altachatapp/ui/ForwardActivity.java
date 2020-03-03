package com.example.altachatapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altachatapp.R;
import com.example.altachatapp.data.FriendDB;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.model.Friend;
import com.example.altachatapp.model.ListFriend;
import com.example.altachatapp.model.Status;
import com.example.altachatapp.model.User;
import com.example.altachatapp.service.ServiceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForwardActivity extends AppCompatActivity {

    private CountDownTimer detectFriendOnline;
    private RecyclerView recyclerListFrends;
    private ListFriend listFriend;

    private ListForwardFriendsAdapter adapter;
    public ListFriend dataListFriend = null;
    public ArrayList<String> listFriendID = null;
    private LovelyProgressDialog dialogFindAllFriend;
    public static int ACTION_START_CHAT = 1;

    android.support.v7.widget.Toolbar toolbar;
    ImageView back;

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view,int position);
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        getSupportActionBar().hide();
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent1=getIntent();
       // final String forwardMessage=intent1.getStringExtra("ForwardMessage");

      //  Toast.makeText(this, forwardMessage, Toast.LENGTH_SHORT).show();

        listFriend = FriendDB.getInstance(this).getListFriend();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerListFrends = (RecyclerView) findViewById(R.id.recycler_view1);
        recyclerListFrends.setLayoutManager(linearLayoutManager);
        adapter = new ListForwardFriendsAdapter(ForwardActivity.this, listFriend);
        recyclerListFrends.setAdapter(adapter);

        recyclerListFrends.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerListFrends,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        final String id = listFriend.getListFriend().get(position).id;

                        Intent intent = new Intent();
                       // intent.putExtra("fwdmsg",forwardMessage);
                        intent.putExtra("STATIC_KEY_FRIEND", listFriend.getListFriend().get(position).name);
                        ArrayList<CharSequence> idFriend = new ArrayList<CharSequence>();
                        idFriend.add(id);
                        intent.putCharSequenceArrayListExtra("STATIC_KEY_ID", idFriend);
                        intent.putExtra("STATIC_KEY_ROOMID", listFriend.getListFriend().get(position).idRoom);
                        setResult(RESULT_OK,intent);
                        //startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


    }


    void getListFriendUId() {
        listFriendID.clear();
        dataListFriend.getListFriend().clear();
        adapter.notifyDataSetChanged();
        FriendDB.getInstance(ForwardActivity.this).dropDB();
        detectFriendOnline.cancel();
        FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                    Iterator listKey = mapRecord.keySet().iterator();


                    while (listKey.hasNext()) {

                        String key = listKey.next().toString().trim();
                        String USERID= mapRecord.get(key).toString().trim();

                        if (listFriendID.contains(USERID) ) {
                        }
                        else{
                            listFriendID.add(USERID);
                        }



                    }
                    getAllFriendInfo(0);
                } else {
                    dialogFindAllFriend.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAllFriendInfo(final int index) {
        if (index == listFriendID.size()) {
            adapter.notifyDataSetChanged();
            dialogFindAllFriend.dismiss();
            detectFriendOnline.start();
        } else {
            final String id = listFriendID.get(index);
            FirebaseDatabase.getInstance().getReference().child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Friend user = new Friend();
                        HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                        user.name = (String) mapUserInfo.get("name");
                        user.email = (String) mapUserInfo.get("email");
                        user.avata = (String) mapUserInfo.get("avata");
                        user.id = id;
                        user.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();

                        dataListFriend.getListFriend().add(user);
                        FriendDB.getInstance(ForwardActivity.this).addFriend(user);
                    }
                    getAllFriendInfo(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_START_CHAT == requestCode && data != null && ListFriendsAdapter.mapMark != null) {
            ListFriendsAdapter.mapMark.put(data.getStringExtra("idFriend"), false);
        }
    }

}
class ListForwardFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ListFriend listFriend;
    private Context context;


    public ListForwardFriendsAdapter(Context context, ListFriend listFriend){
        this.listFriend = listFriend;
        this.context = context;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_fetch_friend, parent, false);
        return new ItemForwardFriendViewHolder(context, view);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Intent intent = ((Activity) context).getIntent();
        final String name = listFriend.getListFriend().get(position).name;
        final String phone = listFriend.getListFriend().get(position).phone;
        final String email = listFriend.getListFriend().get(position).email;
        final String id = listFriend.getListFriend().get(position).id;
        final String idRoom = listFriend.getListFriend().get(position).idRoom;
        final String avata = listFriend.getListFriend().get(position).avata;
        final Status status = listFriend.getListFriend().get(position).status;
        if (!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
            ((ItemForwardFriendViewHolder) holder).avata.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        } else {
            ((ItemForwardFriendViewHolder) holder).avata.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile));
        }
        ((ItemForwardFriendViewHolder) holder).txtName.setText(name);
        ((ItemForwardFriendViewHolder) holder).txtPhone.setText(phone);
        ((ItemForwardFriendViewHolder) holder).txtEmail.setText(email);

    }

    @Override
    public int getItemCount() {
        return listFriend.getListFriend() != null ? listFriend.getListFriend().size() : 0;
    }
}
class ItemForwardFriendViewHolder extends RecyclerView.ViewHolder{
    public CircleImageView avata;
    public TextView txtName, txtEmail, txtPhone;
    private Context context;

    ItemForwardFriendViewHolder(Context context, View itemView) {
        super(itemView);
        avata = (CircleImageView) itemView.findViewById(R.id.icon_avata);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
        txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
        this.context = context;
    }
}
