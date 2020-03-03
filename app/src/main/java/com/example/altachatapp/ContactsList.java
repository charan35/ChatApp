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
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altachatapp.data.ContactsDB;
import com.example.altachatapp.data.FriendDB;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.model.FetchContacts;
import com.example.altachatapp.model.Friend;
import com.example.altachatapp.model.ListContact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<String> selectUsers;
    ArrayList<FetchContacts> fetchedUsers = null;
    private RecyclerView recyclerView;
    private ListContact contactListFriend = null;
    private ArrayList<String> listIDChoose = null;
    private RecyclerView.LayoutManager layoutManager;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private LovelyProgressDialog dialogFindAllFriend;

    AllContactsAdapter adapter;
    Cursor phones;
    LayoutInflater inflater;
    List<String> phoneBookList = new ArrayList<>();

    android.support.v7.widget.Toolbar toolbar;
    ImageView back;
    Button btnCNT;


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1467;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        //inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getSupportActionBar().hide();
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        recyclerView = (RecyclerView) findViewById(R.id.contacts_list);


        fetchedUsers = new ArrayList<FetchContacts>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        listIDChoose = new ArrayList<String>();
        dialogFindAllFriend = new LovelyProgressDialog(ContactsList.this);
        btnCNT = findViewById(R.id.btnCNT);
 btnCNT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

       recyclerView.setHasFixedSize(true);
/*
 mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
*/

        selectUsers = new ArrayList<String>();
   }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AllContactsAdapter(fetchedUsers, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
         /*if (contactListFriend == null) {

            contactListFriend = ContactsDB.getInstance(ContactsList.this).getListContact();
            if (contactListFriend.getListContact().size() > 0) {
               // fetchedUsers.clear();
                for (FetchContacts fetchContacts : contactListFriend.getListContact()) {
                    fetchedUsers.add(fetchContacts);

                    Set<FetchContacts> set = new HashSet<FetchContacts>(fetchedUsers);
                    fetchedUsers.clear();
                    fetchedUsers.addAll(set);


                    if (fetchedUsers.){
                        Toast.makeText(this, "User is already there", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        fetchedUsers.add(fetchContacts);
                        //Toast.makeText(this, "User is not there", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        }*/


        showContacts();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        
    }

    private void showContacts() {

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            /*LoadContact loadContact = new LoadContact();
            loadContact.execute();*/

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
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
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
                            fetchedUsers.add(fetchContacts);
                           // ContactsDB.getInstance(ContactsList.this).addContact(fetchContacts);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        //  showContacts();
    }
}


    //class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder> {
        class AllContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<FetchContacts> contactVOList;

        private Context mContext;
        private SparseBooleanArray itemStateArray = new SparseBooleanArray();
        public ContactsList contactsList;


        public AllContactsAdapter(List<FetchContacts> contactVOList, Context mContext, ContactsList contactsList) {
            this.contactVOList = contactVOList;
            this.mContext = mContext;
            this.contactsList = contactsList;
        }

        /* @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View view = (View) LayoutInflater.from(mContext).inflate(R.layout.contactlist,null,false);
            View view = (View) LayoutInflater.from(mContext).inflate(R.layout.contactlist, parent, false);


            ContactViewHolder contactViewHolder = new ContactViewHolder(view);
            return contactViewHolder;
            return new ContactViewHolder(mContext, view);

        }*/

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.contactlist, parent, false);
            return new ContactViewHolder(mContext, view);
        }

        @Override
      //  public void onBindViewHolder(final RecyclerView holder, int position) {
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            final FetchContacts contactVO = contactVOList.get(position);
            final String phnno = contactVO.getPhone().toString();
            final String name=contactVO.getName().toString();
            final String avata= contactVO.getAvata().toString();
            ((ContactViewHolder) holder).title.setText(name);
            ((ContactViewHolder) holder).phone.setText(phnno);



            if (contactVO.getAvata().equals(StaticConfig.STR_DEFAULT_BASE64)) {
                ((ContactViewHolder)holder).avata.setImageResource(R.drawable.profile);
            } else {
                byte[] decodedString = Base64.decode(contactVO.avata, Base64.DEFAULT);
                Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ((ContactViewHolder)holder).avata.setImageBitmap(src);
            }
            ((ContactViewHolder)holder).addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findIDPhone(phnno);
                    ((ContactViewHolder)holder).addFriend.setVisibility(View.GONE);
                }
            });

        }

        public void findIDPhone(String phone) {
            FirebaseDatabase.getInstance().getReference().child("user").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        //email not found
                        new LovelyInfoDialog(mContext)
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Fail")
                                .setMessage("Phone Number not found")
                                .show();
                    } else {
                        String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                        if (id.equals(StaticConfig.UID)) {
                            new LovelyInfoDialog(mContext)
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
            FriendDB.getInstance(mContext).addFriend(userInfo);
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
                                    new LovelyInfoDialog(mContext)
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
                                    new LovelyInfoDialog(mContext)
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
                new LovelyInfoDialog(mContext)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_add_friend)
                        .setTitle("Success")
                        .setMessage("Add friend success")
                        .show();
            }
        }


        @Override
        public int getItemCount() {

            return contactVOList.size();
        }
    }


         class ContactViewHolder extends RecyclerView.ViewHolder {

            public TextView title;
            public TextView phone;
            public CircleImageView avata;
            public ImageView addFriend;
            public Context mContext;

            public ContactViewHolder(Context mContext, View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.txtName);
                phone = (TextView) itemView.findViewById(R.id.txtPhone);
                avata=(CircleImageView) itemView.findViewById(R.id.contact_avata);
                addFriend = (ImageView) itemView.findViewById(R.id.checkAddPeople);
                this.mContext = mContext;

            }
        }




