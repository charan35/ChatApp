package com.example.altachatapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altachatapp.R;
import com.example.altachatapp.data.FriendDB;
import com.example.altachatapp.data.GroupProfileDB;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.model.Friend;
import com.example.altachatapp.model.Group;
import com.example.altachatapp.model.ListFriend;
import com.example.altachatapp.model.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersList extends AppCompatActivity {

    private RecyclerView recyclerListFriend;
    private ListPeopleAdapter adapter;
    private ListFriend listFriend=null;

    private ArrayList<String>listMembers=null;
    private ArrayList<String>listAdmins=null;

    private Set<String> listIDChooseAdmin;
    private Set<String> listIDRemoveAdmin;

    TextView text;
    private boolean isEditGroup;
    private Group groupEdit;
    TextView id;
    String ID;
    android.support.v7.widget.Toolbar toolbar;
    ImageView back;

    PopupWindow mPopupWindow;
    RelativeLayout mRelativeLayout;
    Context mcontext;
    String idGroup;
    int index=0;
    private LovelyProgressDialog dialogFindAllFriend;

    public AddGroupActivity addGroupActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);

        mcontext = getApplicationContext();
        dialogFindAllFriend = new LovelyProgressDialog(MembersList.this);


        getSupportActionBar().hide();
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listAdmins=new ArrayList<String>();
        text = (TextView)findViewById(R.id.text);
        listMembers=new ArrayList<String>();
        id = (TextView)findViewById(R.id.id);
        listIDChooseAdmin = new HashSet<>();
        listIDRemoveAdmin = new HashSet<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        if (listFriend==null){
            listFriend = FriendDB.getInstance(this).getListFriend();
                if (listFriend.getListFriend().size()>0){
                    /*for (Friend friend:listFriend.getListFriend()){
                        listMembers.add(friend.id);
                    }*/
                }
        }
        Intent intentData = getIntent();
        if (intentData.getStringExtra("groupId") != null) {
            isEditGroup = true;
            idGroup = intentData.getStringExtra("groupId");
            groupEdit = GroupProfileDB.getInstance(this).getGroup(idGroup);
            //Toast.makeText(MembersList.this,idGroup , Toast.LENGTH_SHORT).show();
            text.setText(groupEdit.groupInfo.get("name"));
            while(index < groupEdit.member.size()){
               String list=groupEdit.member.get(index);
                    if (listMembers.contains(list)){

                    }
                    else{
                        listMembers.add(list);
                    }

                index=index+1;
            }
            //listMembers=groupEdit.member;

            dialogFindAllFriend.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Get all Members....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();

            getAllListMembers(0);
            listFriend.getListFriend().clear();
            getAllListInfo();

        }
        /*if (listFriend==null){
            groupEdit= GroupProfileDB.getInstance(this).getGroup(idGroup);
            if (groupEdit.member.size()>0){
                for (String list:groupEdit.member){
                    listMembers.add(list);
                }
            }
        }*/

        recyclerListFriend = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerListFriend.setLayoutManager(linearLayoutManager);
        //adapter = new ListPeopleAdapter(this, listFriend, isEditGroup, groupEdit);
        adapter = new ListPeopleAdapter(this, listFriend, isEditGroup, groupEdit);
        recyclerListFriend.setAdapter(adapter);

    }

    private void getAllListInfo(){

        FirebaseDatabase.getInstance().getReference().child("group").child(idGroup).child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    ArrayList<String> list = (ArrayList<String>) dataSnapshot.getValue();

                    Iterator listKey = list.iterator();
                    while (listKey.hasNext()) {
                        String key = listKey.next().toString().trim();
                        if (listAdmins.contains(key)) {

                        } else {
                            listAdmins.add(key);

                        }
                    }
                }

                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getAllListMembers(final int index) {
        if (index == listMembers.size()) {
            adapter.notifyDataSetChanged();
            dialogFindAllFriend.dismiss();
        } else {
            final String id = listMembers.get(index);
            //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference().child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Friend user = new Friend();
                        HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                        user.name = (String) mapUserInfo.get("name");
                        user.email = (String) mapUserInfo.get("email");
                        user.phone= (String) mapUserInfo.get("phone");
                        user.avata = (String) mapUserInfo.get("avata");
                        user.id = id;

                        listFriend.getListFriend().add(user);

                    }
                    getAllListMembers(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    class ListPeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //private ArrayList<String> listMembers;
        private Context context;
        private ListFriend listFriend;
        private boolean isEdit;
        private Group editGroup;

        public ListPeopleAdapter(Context context, ListFriend listFriend, boolean isEdit, Group editGroup) {

            this.context = context;
            this.listFriend = listFriend;
           // this.listMembers=listMembers;
            this.isEdit = isEdit;
            this.editGroup = editGroup;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_group_menu, parent, false);
            return new ItemFriendHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            //final String name = listFriend.getListFriend().get(position).name;
            ((ItemFriendHolder) holder).txtName.setText(listFriend.getListFriend().get(position).name);
            ((ItemFriendHolder) holder).txtEmail.setText(listFriend.getListFriend().get(position).phone);
            //((ItemFriendHolder) holder).txtEmail.setVisibility(View.INVISIBLE);
            String avata = listFriend.getListFriend().get(position).avata;
            final String id = listFriend.getListFriend().get(position).id;
            final String groupName=groupEdit.groupInfo.get("name");
            if (!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
                ((ItemFriendHolder) holder).avata.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            }else{
                ((ItemFriendHolder) holder).avata.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile));
            }
            if (listAdmins.contains(id)){
                ((ItemFriendHolder)holder).groupadmin.setVisibility(View.VISIBLE);
            }
            else{
                ((ItemFriendHolder)holder).groupadmin.setVisibility(View.GONE);
            }
            ((ItemFriendHolder) holder).btnmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mcontext, ((ItemFriendHolder) holder).btnmenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                   /* String id1=groupEdit.admin.get(0);
                                    Toast.makeText(context, id1, Toast.LENGTH_LONG).show();*/
                                    if (listAdmins.contains(StaticConfig.UID)) {
                                        if (listAdmins.contains(id)) {
                                            Toast.makeText(context, "User has already been Admin", Toast.LENGTH_SHORT).show();
                                        } else {
                                            listAdmins.add(id);
                                            Room room = new Room();
                                            for (String idi : listAdmins) {
                                                room.admin.add(idi);
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("group").child(idGroup).child("admin").setValue(room.admin);
                                            Toast.makeText(context, "Make admin Successful", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                    else{
                                        Toast.makeText(context, "You are not admin", Toast.LENGTH_SHORT).show();
                                    }

                                    return true;
                                case R.id.menu2:
                                    //handle menu2 click
                                   /* if (groupEdit.admin.contains(StaticConfig.UID)) {
                                        listAdmins.remove(id);
                                        Room room = new Room();
                                        for (String idi : listAdmins) {
                                            room.admin.add(idi);
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("group").child(idGroup).child("admin").setValue(room.admin);
                                        Toast.makeText(context, "Remove admin Successful", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(context, "You are not admin", Toast.LENGTH_SHORT).show();

                                    }

                                    return true;*/
                                if (listAdmins.contains(StaticConfig.UID)) {
                                    if (id.equals(listAdmins.get(0))){
                                        Toast.makeText(context, "You Can't remove\t" + listFriend.getListFriend().get(position).name + "\t as admin because they created the group", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (id.equals(StaticConfig.UID)){
                                        Toast.makeText(context, "You are the admin, you will not able to remove you", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        if (listAdmins.contains(id)){
                                            listAdmins.remove(id);
                                            Room room = new Room();
                                            for (String idi : listAdmins) {
                                                room.admin.add(idi);
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("group").child(idGroup).child("admin").setValue(room.admin);
                                            Toast.makeText(context, "Remove admin Successful", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
                                        }
                                        else{
                                            Toast.makeText(context, "User is not admin", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                }else{
                                    Toast.makeText(context, "You are not admin", Toast.LENGTH_SHORT).show();

                                }

                                return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });

        }
        @Override
        public int getItemCount() {
            return listFriend.getListFriend().size();
        }
    }

    class ItemFriendHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtEmail,groupadmin;
        public CircleImageView avata;
        public ImageButton btnmenu;
        // public CheckBox checkBox;

        public ItemFriendHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            avata = (CircleImageView) itemView.findViewById(R.id.icon_avata);
            btnmenu = (ImageButton) itemView.findViewById(R.id.btnmenu);
            groupadmin=(TextView)itemView.findViewById(R.id.groupadmin);
            //checkBox = (CheckBox) itemView.findViewById(R.id.checkAddPeople);
        }
    }
}
