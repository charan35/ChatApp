package com.example.altachatapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altachatapp.data.ContactsDB;
import com.example.altachatapp.data.FriendDB;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.model.FetchContacts;
import com.example.altachatapp.model.Friend;
import com.example.altachatapp.model.ListContact;
import com.example.altachatapp.model.Status;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactList2 extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerListContact;
    private ListContactAdapter adapter;
    ArrayList<String> selectUsers=null;
    ArrayList<String> fetchlist=null;

    EditText search;

    Cursor phones;

    public ListContact dataListContact = null;
    public ArrayList<String> listContactID = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LovelyProgressDialog dialogFindAllFriend;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1465;
    android.support.v7.widget.Toolbar toolbar;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list2);
        getSupportActionBar().hide();
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search=findViewById(R.id.edittext);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (dataListContact == null) {
            dataListContact = ContactsDB.getInstance(ContactList2.this).getListContact();
            if (dataListContact.getListContact().size() > 0) {
                listContactID = new ArrayList<>();
                for (FetchContacts fetchContacts : dataListContact.getListContact()) {
                    listContactID.add(fetchContacts.ID);
                   // Toast.makeText(this, "Loop executing", Toast.LENGTH_SHORT).show();
                }

            }
        }
        selectUsers = new ArrayList<String>();
        fetchlist = new ArrayList<String>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerListContact = (RecyclerView) findViewById(R.id.contacts_list);
        recyclerListContact.setLayoutManager(linearLayoutManager);
        adapter = new ListContactAdapter(this, dataListContact);
        recyclerListContact.setAdapter(adapter);
        dialogFindAllFriend = new LovelyProgressDialog(ContactList2.this);
        if (listContactID == null) {
            listContactID = new ArrayList<>();

            dialogFindAllFriend.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Get all contacts....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            getListContactUid();
        }
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ContactList2.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


    private void filter(String text) {
        // ArrayList<FetchContacts> filteredList = new ArrayList<FetchContacts>();
        ListContact dataListContact2=new ListContact();
        for (FetchContacts item : dataListContact.getListContact()) {
            if ((item.name.toLowerCase()).contains(text.toLowerCase())) {

                dataListContact2.getListContact().add(item);
            }

        }

        adapter.filterList(dataListContact2);
    }
    void getListContactUid(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                }

                while (phones.moveToNext()) {
                    // Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    // String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    phoneNumber = phoneNumber.replaceAll("\\s+", "");
                    if (phoneNumber.startsWith("+91")) {

                    } else {
                        phoneNumber = phoneNumber.replaceFirst("", "+91");
                        //Toast.makeText(ContactsList.this, phoneNumber, Toast.LENGTH_SHORT).show();
                    }
                    selectUsers.add(phoneNumber);
                    //getListContactInfo(0);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
        }
        listContactID.clear();
        dataListContact.getListContact().clear();
        fetchlist.clear();
        adapter.notifyDataSetChanged();
        ContactsDB.getInstance(ContactList2.this).dropDB();
        FirebaseDatabase.getInstance().getReference("user").orderByChild("phone").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                    FetchContacts fetchContacts = new FetchContacts();
                    fetchContacts.phone = (String) mapMessage.get("phone");
                    fetchContacts.name = (String) mapMessage.get("name");
                    fetchContacts.ID = (String) mapMessage.get("ID");
                    fetchContacts.avata = (String) mapMessage.get("avata");
                    if (selectUsers.contains(fetchContacts.phone)) {
                        if (listContactID.contains(fetchContacts.ID)){
                               // listContactID.remove(fetchContacts.ID);
                           // Toast.makeText(ContactList2.this, "If case exuting", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            listContactID.add(fetchContacts.ID);
                          // Toast.makeText(ContactList2.this, fetchContacts.ID, Toast.LENGTH_SHORT).show();

                        }
                        //Toast.makeText(ContactList2.this, fetchContacts.ID, Toast.LENGTH_SHORT).show();
                        // ContactsDB.getInstance(ContactsList.this).addContact(fetchContacts);

                    }
                    getListContactInfo(0);
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
    @Override
    public void onRefresh() {
        dataListContact=null;
        listContactID=null;
        fetchlist=null;
        dataListContact = ContactsDB.getInstance(ContactList2.this).getListContact();
        if (dataListContact.getListContact().size() > 0) {
            listContactID = new ArrayList<>();
            for (FetchContacts fetchContacts : dataListContact.getListContact()) {
                listContactID.add(fetchContacts.ID);
            }
            fetchlist=new ArrayList<String>();

            adapter = new ListContactAdapter(ContactList2.this, dataListContact);
            recyclerListContact.setAdapter(adapter);


            listContactID = new ArrayList<>();

            dialogFindAllFriend.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Get all contacts....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            getListContactUid();
        }
    }
    void getListContactInfo(final int index){
        if (index == listContactID.size()) {
            adapter.notifyDataSetChanged();
            dialogFindAllFriend.dismiss();
            //Toast.makeText(this, "List size is zero", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        else {
            final String id = listContactID.get(index);

            FirebaseDatabase.getInstance().getReference().child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        FetchContacts fetchContacts = new FetchContacts();
                        HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                        fetchContacts.name = (String) mapUserInfo.get("name");
                        fetchContacts.email = (String) mapUserInfo.get("email");
                        fetchContacts.phone=(String)mapUserInfo.get("phone");
                        fetchContacts.avata = (String) mapUserInfo.get("avata");
                        fetchContacts.ID = (String)mapUserInfo.get("ID");

                        if (fetchlist.contains(fetchContacts.ID)){

                        }
                        else {

                            dataListContact.getListContact().add(fetchContacts);
                            ContactsDB.getInstance(ContactList2.this).addContact(fetchContacts);
                            //Toast.makeText(ContactList2.this, fetchContacts.ID, Toast.LENGTH_SHORT).show();
                            fetchlist.add(fetchContacts.ID);

                        }



                    }
                    getListContactInfo(index + 1);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }
}
class ListContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ListContact listContact;
    private Context context;
    public static Map<String, Query> mapQuery;
    public static Map<String, DatabaseReference> mapQueryOnline;
    public static Map<String, ChildEventListener> mapChildListener;
    public static Map<String, ChildEventListener> mapChildListenerOnline;
    public static Map<String, Boolean> mapMark;
   // private FriendsFragment fragment;
    LovelyProgressDialog dialogWaitDeleting;

    public ArrayList<String> listUsers;


    public ListContactAdapter(Context context, ListContact listContact) {
        this.listContact = listContact;
        this.context = context;
        mapQuery = new HashMap<>();
        mapChildListener = new HashMap<>();
        mapMark = new HashMap<>();
        mapChildListenerOnline = new HashMap<>();
        mapQueryOnline = new HashMap<>();
        //this.fragment = fragment;
        dialogWaitDeleting = new LovelyProgressDialog(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contactlist, parent, false);
        return new ContactViewHolder1(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        listUsers=new ArrayList<String>();
        final String name = listContact.getListContact().get(position).name;
        final String id = listContact.getListContact().get(position).id;
        final String email = listContact.getListContact().get(position).email;
        final String phone = listContact.getListContact().get(position).phone;
        final String avata = listContact.getListContact().get(position).avata;
        final Status status = listContact.getListContact().get(position).status;
        ((ContactViewHolder1) holder).title.setText(name);
        ((ContactViewHolder1) holder).phone.setText(phone);
        if (listContact.getListContact().get(position).avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            ((ContactViewHolder1) holder).avata.setImageResource(R.drawable.profile);
        } else {
            byte[] decodedString = Base64.decode(listContact.getListContact().get(position).avata, Base64.DEFAULT);
            Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ContactViewHolder1) holder).avata.setImageBitmap(src);
        }
       if (listUsers.contains(id)){
           ((ContactViewHolder1)holder).addFriend.setVisibility(View.GONE);
       }
       else{
           ((ContactViewHolder1) holder).addFriend.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   findIDPhone(phone);
                   listUsers.add(id);
               }
           });
       }

    }
    public void findIDPhone(String phone) {
        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //email not found
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_add_friend)
                            .setTitle("Fail")
                            .setMessage("Phone Number not found")
                            .show();
                } else {
                    String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                    if (id.equals(StaticConfig.UID)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Fail")
                                .setMessage("Phone Number not valid")
                                .show();
                    } else {
                        HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
                        Friend user = new Friend();

                        user.name = (String) userMap.get("name");
                        user.email = (String) userMap.get("email");
                        user.phone = (String) userMap.get("phone");
                        user.avata = (String) userMap.get("avata");
                        user.id = id;
                        user.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                        checkBeforAddFriend(id, user, user.phone);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkBeforAddFriend(final String idFriend, Friend userInfo, final String Phone) {
        addFriend(idFriend, true, Phone);
        FriendDB.getInstance(context).addFriend(userInfo);
        //adapter.notifyDataSetChanged();

    }



    private void addFriend(final String idFriend, boolean isIdFriend, final String Phone) {
        if (idFriend != null) {
            if (isIdFriend) {
                FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).push().setValue(idFriend).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //addFriend(idFriend, false);
                            addFriend(idFriend, false, Phone);


                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // dialogWait.dismiss();
                                new LovelyInfoDialog(context)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("False")
                                        .setMessage("False to add friend success")
                                        .show();
                            }
                        });
            } else {
                FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend).push().setValue(StaticConfig.UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addFriend(null, false, Phone);
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //dialogWait.dismiss();
                                new LovelyInfoDialog(context)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("False")
                                        .setMessage("False to add friend success")
                                        .show();
                            }
                        });
            }
        } else {
            //dialogWait.dismiss();
            new LovelyInfoDialog(context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Success")
                    .setMessage("Add friend success")
                    .show();
        }
    }

    @Override
    public int getItemCount() {
        return listContact.getListContact() != null ? listContact.getListContact().size() : 0;
    }
    public void filterList(ListContact listContact1) {
        listContact = listContact1;
        notifyDataSetChanged();
    }

    /**
     * Delete friend
     *
     * @param idFriend
     */
}

class ContactViewHolder1 extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView phone;
    public CircleImageView avata;
    public ImageView addFriend;
    public Context mContext;

    public ContactViewHolder1(Context mContext, View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.txtName);
        phone = (TextView) itemView.findViewById(R.id.txtPhone);
        avata=(CircleImageView) itemView.findViewById(R.id.contact_avata);
        addFriend = (ImageView) itemView.findViewById(R.id.checkAddPeople);
        this.mContext = mContext;

    }
}


