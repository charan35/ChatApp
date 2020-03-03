package com.example.altachatapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altachatapp.HelpDesk;
import com.example.altachatapp.R;
import com.example.altachatapp.data.FriendDB;
import com.example.altachatapp.data.GroupProfileDB;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.model.Group;
import com.example.altachatapp.model.GroupProfile;
import com.example.altachatapp.model.ListFriend;
import com.example.altachatapp.model.Room;
import com.example.altachatapp.util.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerListFriend;
    private ListPeopleAdapter adapter;
    private ListFriend listFriend;
    private LinearLayout btnAddGroup;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    public Set<String> listIDChooseAdmin;
    public Set<String> listIDRemoveAdmin;
    private EditText editTextGroupName;
    private TextView  txtActionName;
    private LovelyProgressDialog dialogWait;
    private boolean isEditGroup;
    private Group groupEdit;
    ImageView back;
    android.support.v7.widget.Toolbar toolbar;


    private CircleImageView group_avatar;
    private static final int PICK_IMAGE = 1994;
    private LovelyProgressDialog waitingDialog;
    TextView sample,sample1;
    String imageBase64;
    private GroupProfile groupProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        toolbar = findViewById(R.id.toolbar);
        group_avatar=findViewById(R.id.grp_avatar);
        sample=findViewById(R.id.textsample);
        sample1=findViewById(R.id.textsample1);


        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intentData = getIntent();
        txtActionName = (TextView) findViewById(R.id.txtActionName);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listFriend = FriendDB.getInstance(this).getListFriend();
        listIDChoose = new HashSet<>();
        listIDRemove = new HashSet<>();
        listIDChooseAdmin = new HashSet<>();
        listIDRemoveAdmin = new HashSet<>();
        listIDChoose.add(StaticConfig.UID);
        listIDChooseAdmin.add(StaticConfig.UID);
        btnAddGroup = (LinearLayout) findViewById(R.id.btnAddGroup);
        editTextGroupName = (EditText) findViewById(R.id.editGroupName);
        dialogWait = new LovelyProgressDialog(this).setCancelable(false);


        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listIDChoose.size() < 3) {
                    Toast.makeText(AddGroupActivity.this, "Add at lease two people to create group", Toast.LENGTH_SHORT).show();
                } else {
                    if (editTextGroupName.getText().length() == 0) {
                        Toast.makeText(AddGroupActivity.this, "Enter group name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isEditGroup) {
                            listIDChooseAdmin.clear();
                           final  String idGroup=groupEdit.id;
                           FirebaseDatabase.getInstance().getReference().child("group").child(idGroup).child("groupInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.getValue()!=null){
                                       HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                                            String groupAvatar=(String) mapRecord.get("avatar");
                                            sample1.setText(groupAvatar);
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                            FirebaseDatabase.getInstance().getReference().child("group").child(idGroup).child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue()!=null) {
                                        ArrayList<String> list = (ArrayList<String>) dataSnapshot.getValue();
                                        Iterator listKey = list.iterator();
                                        while (listKey.hasNext()) {
                                            String key = listKey.next().toString().trim();
                                            if (listIDChooseAdmin.contains(key)) {

                                            } else {
                                                listIDChooseAdmin.add(key);

                                            }
                                        }
                                    }editGroup();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                           // editGroup();
                        } else {
                            createGroup();
                        }
                    }
                }
            }

        });

        if (intentData.getStringExtra("groupId") != null) {
            isEditGroup = true;
            String idGroup = intentData.getStringExtra("groupId");
            txtActionName.setText("Save");
            btnAddGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
           // groupEdit = GroupDB.getInstance(this).getGroup(idGroup);
            groupEdit= GroupProfileDB.getInstance(this).getGroup(idGroup);

            editTextGroupName.setText(groupEdit.groupInfo.get("name"));
            sample.setText(groupEdit.groupInfo.get("avatar"));


            String base=sample.getText().toString();

            if (base.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                group_avatar.setImageResource(R.drawable.profile);
            } else {
                byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
                Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
               group_avatar.setImageBitmap(src);
            }


        } else {
            isEditGroup = false;
        }

        recyclerListFriend = (RecyclerView) findViewById(R.id.recycleListFriend);
        recyclerListFriend.setLayoutManager(linearLayoutManager);
        adapter = new ListPeopleAdapter(this, listFriend, btnAddGroup,listIDChooseAdmin,listIDRemoveAdmin, listIDChoose, listIDRemove, isEditGroup, groupEdit);
        recyclerListFriend.setAdapter(adapter);

        group_avatar.setOnClickListener(onAvatarClick);
        waitingDialog = new LovelyProgressDialog(AddGroupActivity.this);

    }
    //////////on Create ends



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        AddGroupActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    private View.OnClickListener onAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(AddGroupActivity.this)
                    .setTitle("Profile Picture")
                    .setMessage("Are you sure want to change profile picture?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(AddGroupActivity.this, "An error has occurred, please try again", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                InputStream inputStream = AddGroupActivity.this.getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                imageBase64 = ImageUtils.encodeBase64(liteImage);
                groupProfile=new GroupProfile();
                groupProfile.groupavatar=imageBase64;
                sample.setText(imageBase64);
                group_avatar.setImageDrawable(ImageUtils.roundedImage(AddGroupActivity.this, liteImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void editGroup() {
        //Show dialog wait
        dialogWait.setIcon(R.drawable.ic_add_group_dialog)
                .setTitle("Editing....")
                .setTopColorRes(R.color.colorPrimary)
                .show();
        //Delete group
        final String idGroup = groupEdit.id;
        Room room = new Room();
        for (String id : listIDChoose) {
            room.member.add(id);
        }
        for (String id1 : listIDChooseAdmin){
            room.admin.add(id1);
        }
        room.groupInfo.put("name", editTextGroupName.getText().toString());
        //room.groupInfo.put("admin", StaticConfig.UID);
       // room.groupInfo.put("avatar", groupProfile.groupavatar);
        if (sample.getText().toString().equals(sample1.getText().toString()))
        {
            room.groupInfo.put("avatar",sample.getText().toString());
        }
        else {
            room.groupInfo.put("avatar", groupProfile.groupavatar);
        }
        FirebaseDatabase.getInstance().getReference().child("group/" + idGroup).setValue(room)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        addRoomForUser(idGroup, 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogWait.dismiss();
                        new LovelyInfoDialog(AddGroupActivity.this) {
                            @Override
                            public LovelyInfoDialog setConfirmButtonText(String text) {
                                findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dismiss();
                                    }
                                });
                                return super.setConfirmButtonText(text);
                            }
                        }
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_group_dialog)
                                .setTitle("False")
                                .setMessage("Cannot connect database")
                                .setCancelable(false)
                                .setConfirmButtonText("Ok")
                                .show();
                    }
                });
    }


    private void createGroup() {
        //Show dialog wait
        dialogWait.setIcon(R.drawable.ic_add_group_dialog)
                .setTitle("Registering....")
                .setTopColorRes(R.color.colorPrimary)
                .show();

        final String idGroup = (StaticConfig.UID + System.currentTimeMillis()).hashCode() + "";
        Room room = new Room();
        for (String id : listIDChoose) {
            room.member.add(id);
        }
        for (String id1: listIDChooseAdmin){
            room.admin.add(id1);
        }


        room.groupInfo.put("name", editTextGroupName.getText().toString());
       // room.groupInfo.put("avatar", groupProfile.groupavatar);

        //room.groupInfo.put("admin", StaticConfig.UID);
        if( sample.getText().toString().equals("default"))
        {
            room.groupInfo.put("avatar","default");
        }
        else {
            room.groupInfo.put("avatar", groupProfile.groupavatar);
        }

        FirebaseDatabase.getInstance().getReference().child("group/" + idGroup).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addRoomForUser(idGroup, 0);
            }
        });
    }

    private void deleteRoomForUser(final String roomId, final int userIndex) {
        if (userIndex == listIDRemove.size()) {
            dialogWait.dismiss();
            Toast.makeText(this, "Edit group success", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, null);
            AddGroupActivity.this.finish();
        } else {
            FirebaseDatabase.getInstance().getReference().child("user/" + listIDRemove.toArray()[userIndex] + "/group/" + roomId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            deleteRoomForUser(roomId, userIndex + 1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogWait.dismiss();
                            new LovelyInfoDialog(AddGroupActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_add_group_dialog)
                                    .setTitle("False")
                                    .setMessage("Cannot connect database")
                                    .setCancelable(false)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    });
        }
    }

    private void addRoomForUser(final String roomId, final int userIndex) {
        if (userIndex == listIDChoose.size()) {
            if (!isEditGroup) {
                dialogWait.dismiss();
                Toast.makeText(this, "Create group success", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                AddGroupActivity.this.finish();
            } else {
                deleteRoomForUser(roomId, 0);
            }
        } else {
            FirebaseDatabase.getInstance().getReference().child("user/" + listIDChoose.toArray()[userIndex] + "/group/" + roomId).setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addRoomForUser(roomId, userIndex + 1);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogWait.dismiss();
                    new LovelyInfoDialog(AddGroupActivity.this) {
                        @Override
                        public LovelyInfoDialog setConfirmButtonText(String text) {
                            findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dismiss();
                                }
                            });
                            return super.setConfirmButtonText(text);
                        }
                    }
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_add_group_dialog)
                            .setTitle("False")
                            .setMessage("Create group false")
                            .setCancelable(false)
                            .setConfirmButtonText("Ok")
                            .show();
                }
            });
        }
    }
}

class ListPeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ListFriend listFriend;
    private LinearLayout btnAddGroup;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    public Set<String> listIDChooseAdmin;
    public Set<String> listIDRemoveAdmin;
    private boolean isEdit;
    private Group editGroup;

    private ArrayList<String>listAdmins=null;

    public ListPeopleAdapter(Context context, ListFriend listFriend, LinearLayout btnAddGroup,Set<String>listIDChooseAdmin,Set<String>listIDRemoveAdmin, Set<String> listIDChoose, Set<String> listIDRemove, boolean isEdit, Group editGroup) {
        this.context = context;
        this.listFriend = listFriend;
        this.btnAddGroup = btnAddGroup;
        this.listIDChoose = listIDChoose;
        this.listIDRemove = listIDRemove;
        this.listIDChooseAdmin = listIDChooseAdmin;
        this.listIDRemoveAdmin = listIDRemoveAdmin;
        this.isEdit = isEdit;
        this.editGroup = editGroup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_add_friend, parent, false);
        return new ItemFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemFriendHolder) holder).txtName.setText(listFriend.getListFriend().get(position).name);
        ((ItemFriendHolder) holder).txtEmail.setText(listFriend.getListFriend().get(position).phone);
        String avata = listFriend.getListFriend().get(position).avata;
        final String id = listFriend.getListFriend().get(position).id;
        if (!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
            ((ItemFriendHolder) holder).avata.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        }else{
            ((ItemFriendHolder) holder).avata.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile));
        }
        ((ItemFriendHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listIDChoose.add(id);
                    listIDRemove.remove(id);
                } else {
                    listIDRemove.add(id);
                    listIDChoose.remove(id);
                }
                if (listIDChoose.size() >= 3) {
                    btnAddGroup.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                } else {
                    btnAddGroup.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        if (isEdit && editGroup.member.contains(id)) {
            ((ItemFriendHolder) holder).checkBox.setChecked(true);
        }else if(editGroup != null && !editGroup.member.contains(id)){
            ((ItemFriendHolder) holder).checkBox.setChecked(false);

        }
    }

    @Override
    public int getItemCount() {
        return listFriend.getListFriend().size();
    }
}

class ItemFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtName, txtEmail;
    public CircleImageView avata;
    public CheckBox checkBox;

    public ItemFriendHolder(View itemView) {
        super(itemView);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
        avata = (CircleImageView) itemView.findViewById(R.id.icon_avata);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkAddPeople);
    }
}

