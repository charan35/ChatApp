package com.example.altachatapp.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.altachatapp.R;
import com.example.altachatapp.Wallpapers;
import com.example.altachatapp.data.SharedPreferenceHelper;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.emoji.Emojicon;
import com.example.altachatapp.emoji.EmojiconGridView;
import com.example.altachatapp.emoji.EmojiconsPopup;
import com.example.altachatapp.model.Consersation;
import com.example.altachatapp.model.Message;
import com.example.altachatapp.service.ServiceUtils;
import com.example.altachatapp.view.FullScreenImageActivity;
import com.example.altachatapp.view.VideoPlayerActivity;
import com.rockerhieu.emojicon.EmojiconEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static PopupWindow popWindow;

    private RecyclerView recyclerChat;
    public RelativeLayout contentRoot;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Consersation consersation;
    private ImageButton btnSend,btnAdd,btnCamera,btnSend1;
    private LinearLayout fileUploadContainer;
    private EditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;
    public  int selectedPosition;
    EmojiconEditText emojiconEditText;

    public static final String mypreference = "mypref";
    SharedPreferences sharedpreferences;
    private String latitude="", longitude="", address="";
    String checker="";

    StorageReference mStorageReference,sRef;

    private final static int PICK_PDF_CODE = 2342;
    private static final int PICK_IMAGE = 1995;
    private static final int PLACE_PICKER_REQUEST = 3;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 143;

    public static int RC_TAKE_VIDEO = 30;

    public static int RC_TAKE_AUDIO = 40;
    public static int RC_TAKE_DOCUMENT = 50;
    public static int RC_FORWARD_MESSAGE = 60;

    public static int NOTIFICATION_ID = 1000;

    public Uri filePath;
    private final String ADMIN_CHANNEL_ID ="admin_channel";
    String from,FriendId;
    TextView empty;
    public int mode=0;
    public  int CHAT_MODE=70;
    public  int SELECT_MODE=80;

    public String TYPE;
    LinearLayout mRevealView;
    boolean hidden=true;

    ProgressBar progressBar;
    android.support.v7.widget.Toolbar toolbar;
    ImageView back,copytext,forward,share;

    String fwdmsg;
    String nameFriend;


    /*public static interface ClickListener{
        public void onClick(View view,int position);
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
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
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
    }*/


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mStorageReference = FirebaseStorage.getInstance().getReference();
        fileUploadContainer = findViewById(R.id.file_upload_container);
        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);


        empty=(TextView) findViewById(R.id.empty);
        progressBar=findViewById(R.id.progressbar);
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        copytext=findViewById(R.id.copy_text);
        forward=findViewById(R.id.forward_message);
        share=findViewById(R.id.share);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getSupportActionBar().show();
                toolbar.setVisibility(View.GONE);
                getSupportActionBar().show();

            }
        });
        copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ChatMessage", consersation.getListMessageData().get(selectedPosition).text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ForwardActivity.class);
                // intent.putExtra("ForwardMessage",consersation.getListMessageData().get(selectedPosition).text);
                startActivityForResult(intent, RC_FORWARD_MESSAGE);
                Log.d("ForwardMessage", consersation.getListMessageData().get(selectedPosition).text);
            }
        });

        if(ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= LOLLIPOP){
                requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_REQUEST_CODE);
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }

        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        nameFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);
        SharedPreferences room = ChatActivity.this.getSharedPreferences("ROOMID", MODE_PRIVATE);
        SharedPreferences.Editor edit = room.edit();
        edit.clear();
        edit.putString("room", roomId);
        edit.commit();

        btnSend1 = (ImageButton) findViewById(R.id.btnSend1);
        FriendId=intentData.getStringExtra("FriendId");
        // fwdmsg=intentData.getStringExtra("fwdmsg");
        from=intentData.getStringExtra("from");
        if (from!=null&&FriendId!=null) {
            if (from.equals("memberslist")) {
                editWriteMessage.setText("Plese add these " + FriendId + "to the group");
            }
        }

        consersation = new Consersation();
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnAdd=(ImageButton)findViewById(R.id.btnAdd);
        btnSend.setOnClickListener(this);
        btnCamera=(ImageButton)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog();

               /* CharSequence options[]=new CharSequence[]{

                        "Images",
                        "Documents",
                        "Videos",
                        "Audios"

                };
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            getImage();
                        }
                        if(i==1){
                            launchAttachDocument();
                        }
                        if(i==2){
                            launchGalleryVideo();
                        }
                        if(i==3){
                            launchGalleryAudio();
                        }
                    }
                });
                builder.show();*/
            }
        });






        String base64AvataUser = SharedPreferenceHelper.getInstance(this).getUserInfo().avata;
        if (!base64AvataUser.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataUser = null;
        }

        if (idFriend != null && nameFriend != null) {
            getSupportActionBar().setTitle(nameFriend);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
            contentRoot=(RelativeLayout)findViewById(R.id.contentRoot);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, bitmapAvataUser);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.urlFile=(String)mapMessage.get("urlFile");
                        newMessage.localFileUrl=(String)mapMessage.get("localFileUrl");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        newMessage.type=(String) mapMessage.get("type");
                        newMessage.download=(String)mapMessage.get("download");
                        newMessage.address=(String)mapMessage.get("address");
                        newMessage.latitude=(String)mapMessage.get("latitude");
                        newMessage.longitude=(String)mapMessage.get("longitude");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);

                        progressBar.setVisibility(View.GONE);
                        empty.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            recyclerChat.setAdapter(adapter);
        }


        sharedpreferences = getSharedPreferences(mypreference, MODE_PRIVATE);
        String value = sharedpreferences.getString("person_name", "defaultValue");

        if(value.equalsIgnoreCase("Black"))
        {
            contentRoot.setBackgroundResource(R.color.black);
        }
        if(value.equalsIgnoreCase("LightBlue"))
        {
            contentRoot.setBackgroundResource(R.color.white);
        }
        if(value.equalsIgnoreCase("Grey"))
        {
            contentRoot.setBackgroundResource(R.color.cardview_dark_background);
        }
        if(value.equalsIgnoreCase("Blue"))
        {
            contentRoot.setBackgroundResource(R.color.design_default_color_primary);
        }
        if(value.equalsIgnoreCase("orange"))
        {
            contentRoot.setBackgroundResource(R.color.orange);
        }
        if(value.equalsIgnoreCase("lightyellow"))
        {
            contentRoot.setBackgroundResource(R.color.lightyellow);
        }
        if(value.equalsIgnoreCase("color1"))
        {
            contentRoot.setBackgroundResource(R.color.color1);
        }
        if(value.equalsIgnoreCase("color2"))
        {
            contentRoot.setBackgroundResource(R.color.color2);
        }
        if(value.equalsIgnoreCase("color3"))
        {
            contentRoot.setBackgroundResource(R.color.color3);
        }
        if(value.equalsIgnoreCase("color4"))
        {
            contentRoot.setBackgroundResource(R.color.color4);
        }
        if(value.equalsIgnoreCase("color5"))
        {
            contentRoot.setBackgroundResource(R.color.color5);
        }
        if(value.equalsIgnoreCase("color6"))
        {
            contentRoot.setBackgroundResource(R.color.color6);
        }

        //EMOJIS DECLARATIONS
        emojiconEditText = (EmojiconEditText) findViewById(R.id.editEmojicon);
        final View rootView = findViewById(R.id.contentRoot);
        final ImageView emojiButton = (ImageView) findViewById(R.id.btnSend1);

        emojiconEditText.setText("");//set status to current value
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (editWriteMessage == null || emojicon == null) {
                    return;
                }

                int start = editWriteMessage.getSelectionStart();
                int end = editWriteMessage.getSelectionEnd();
                if (start < 0) {
                    editWriteMessage.append(emojicon.getEmoji());
                } else {
                    editWriteMessage.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                editWriteMessage.dispatchKeyEvent(event);
            }
        });
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!popup.isShowing()) {
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                    else {
                        editWriteMessage.setFocusableInTouchMode(true);
                        editWriteMessage.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editWriteMessage, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }
                else {
                    popup.dismiss();
                }
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {

                    progressBar.setVisibility(View.VISIBLE);
                    empty.setText("No Data Available\n"+"Please Check Your Internet Connection and Try again");
                    recyclerChat.setVisibility(View.INVISIBLE);

                }else {
                    recyclerChat.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getBaseContext(), "SOMETHING WENT WRONG ",Toast.LENGTH_SHORT);
            }
        });
       // setItemTouchListener();
    }
    public void forwardMethod(int selectedPosition){
        Intent intent = new Intent(ChatActivity.this, ForwardActivity.class);
        // intent.putExtra("ForwardMessage",consersation.getListMessageData().get(selectedPosition).text);
        startActivityForResult(intent, RC_FORWARD_MESSAGE);
        Log.d("ForwardMessage", consersation.getListMessageData().get(selectedPosition).text);
    }


    private void openDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate the custom popup layout
        final View inflatedView;

        inflatedView = layoutInflater.inflate(R.layout.custom_dialog_options_menu, null, false);

        LinearLayout layoutGallery,layoutAudio,layoutVideo,layoutDocument;
        layoutGallery = (LinearLayout) inflatedView.findViewById(R.id.layoutImages);
        layoutAudio = (LinearLayout) inflatedView.findViewById(R.id.layoutAudio);
        layoutVideo = (LinearLayout) inflatedView.findViewById(R.id.layoutVideo);
        layoutDocument = (LinearLayout) inflatedView.findViewById(R.id.layoutDoument);

        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingView.dismissWindow();
                getImage();
            }
        });

        layoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryVideo();
            }
        });

        layoutDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAttachDocument();
            }
        });

        layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryAudio();
            }
        });

        FloatingView.onShowPopup(this, inflatedView);
    }

    public static class FloatingView {
        private FloatingView() {
        }

        public static void onShowPopup(Activity activity, View inflatedView) {

            // get device size
            Display display = activity.getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            // fill the data to the list items
            // set height depends on the device size
            popWindow = new PopupWindow(inflatedView, size.x, ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            // set a background drawable with rounders corners
            popWindow.setBackgroundDrawable(activity.getResources().getDrawable(
                    R.drawable.bg_input_cursor));
            // make it focusable to show the keyboard to enter in `EditText`
            popWindow.setFocusable(true);
            // make it outside touchable to dismiss the popup window
            popWindow.setOutsideTouchable(true);

            popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            // show the popup at bottom of the screen and set some margin at
            // bottom ie,

            popWindow.showAtLocation(activity.getCurrentFocus(), Gravity.BOTTOM, 0,
                    160);
        }

        public static void dismissWindow() {

            popWindow.dismiss();
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ChatActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // case PERMISSION_REQUEST_CODE:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    @Override
    protected void onStart() {
        super.onStart();
        ServiceUtils.stopServiceFriendChat(getApplicationContext(), false);

    }
    /*private void setItemTouchListener() {
        recyclerChat.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerChat, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                *//*if(mode==SELECT_MODE){
                    final Message message=consersation.getListMessageData().get(position);
                    selectedPosition=position;
                    toolbar.setVisibility(View.VISIBLE);
                    if (message.type.equals("text")){
                        share.setVisibility(View.GONE);
                    }
                    else{
                        share.setVisibility(View.VISIBLE);
                        copytext.setVisibility(View.GONE);

                    }
                    Log.d("onClick", consersation.getListMessageData().get(position).text + position);
                }*//*


            }

            @Override
            public void onLongClick(View view, int position) {
                final Message message=consersation.getListMessageData().get(position);
                selectedPosition=position;

                getSupportActionBar().hide();
                Animation in = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.fade_in);
                toolbar.startAnimation(in);
                toolbar.setVisibility(View.VISIBLE);
                //toolbar.setVisibility(View.VISIBLE);
                if (message.type.equals("text")){
                    share.setVisibility(View.GONE);
                }
                else{
                    share.setVisibility(View.VISIBLE);
                    copytext.setVisibility(View.GONE);
                }
                //
                // adapter.notifyDataSetChanged();
                mode=SELECT_MODE;

                Log.d("Long Click", consersation.getListMessageData().get(position).text + position);

            }
        }));

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wallpaper, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent result = new Intent();
            result.putExtra("idFriend", idFriend.get(0));
            setResult(RESULT_OK, result);
            this.finish();
        }
        else if (item.getItemId() == R.id.wallpaper) {
            Intent intent = new Intent(ChatActivity.this, Wallpapers.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend.get(0));
        setResult(RESULT_OK, result);
        this.finish();
    }

    private void getPDF() {
        checker="pdf";
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
    }
    private void getImage(){
        checker="image";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }
    private void captureImage(){
        checker="camera";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }
            catch (Exception e){
            }

            if(photoFile!=null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,filePath);
                startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);
            }
        }
    }
    /*private void locationPlacesIntent(){
      //  checker="location";
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }*/
    private void launchGalleryVideo() {
        checker="video";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,RC_TAKE_VIDEO);
    }

    private void launchGalleryAudio() {
        checker="audio";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,RC_TAKE_AUDIO);
    }
    private void launchAttachDocument() {
        checker="document";
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"),RC_TAKE_DOCUMENT);
    }

    private File createImageFile() throws IOException {
        String timeStamp = (String) DateFormat.format("yyyydd_HHmmss", new Date().getTime());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );
        filePath = Uri.fromFile(image);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_FORWARD_MESSAGE && resultCode == Activity.RESULT_OK){
            Message forwardMessage = consersation.getListMessageData().get(selectedPosition);
           /* Log.d("Reciever Name and ID and RoomID", data.getStringExtra("STATIC_KEY_FRIEND")
                    + " "
                    +data.getStringExtra("STATIC_KEY_ID")+" "+data.getStringExtra("STATIC_KEY_ROOMID"));*/
            nameFriend=data.getStringExtra("STATIC_KEY_FRIEND");
            idFriend=data.getCharSequenceArrayListExtra("STATIC_KEY_ID");
            roomId=data.getStringExtra("STATIC_KEY_ROOMID");

            if (forwardMessage.type.equals("text")) {
                Message newMessage = new Message();
                // newMessage.text = data.getStringExtra("fwdmsg");
                newMessage.text = forwardMessage.text;
                newMessage.idSender = StaticConfig.UID;
                newMessage.idReceiver = roomId;
                newMessage.type = "text";
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                Toast.makeText(this, "Message forwarded", Toast.LENGTH_SHORT).show();
            }
            if (forwardMessage.type.equals("image")){
                Message newMessage = new Message();
                newMessage.text = forwardMessage.text;
                newMessage.type=forwardMessage.type;
                newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                newMessage.idReceiver = roomId;
                newMessage.urlFile =forwardMessage.urlFile;
                newMessage.download="notDownload";
                newMessage.localFileUrl=forwardMessage.localFileUrl;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                Toast.makeText(this, "Message forwarded", Toast.LENGTH_SHORT).show();

            }
            if (forwardMessage.type.equals("audio")){
                Message newMessage = new Message();
                newMessage.text = forwardMessage.text;
                newMessage.type=forwardMessage.type;
                newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                newMessage.idReceiver = roomId;
                newMessage.urlFile =forwardMessage.urlFile;
                newMessage.download="notDownload";
                newMessage.localFileUrl=forwardMessage.localFileUrl;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                Toast.makeText(this, "Message forwarded", Toast.LENGTH_SHORT).show();

            }
            if (forwardMessage.type.equals("video")){
                Message newMessage = new Message();
                newMessage.text = forwardMessage.text;
                newMessage.type=forwardMessage.type;
                newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                newMessage.idReceiver = roomId;
                newMessage.urlFile =forwardMessage.urlFile;
                newMessage.download="notDownload";
                newMessage.localFileUrl=forwardMessage.localFileUrl;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                Toast.makeText(this, "Message forwarded", Toast.LENGTH_SHORT).show();

            }
            if (forwardMessage.type.equals("document")){
                Message newMessage = new Message();
                newMessage.text = forwardMessage.text;
                newMessage.type=forwardMessage.type;
                newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                newMessage.idReceiver = roomId;
                newMessage.urlFile =forwardMessage.urlFile;
                newMessage.download="notDownload";
                newMessage.localFileUrl=forwardMessage.localFileUrl;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                Toast.makeText(this, "Message forwarded", Toast.LENGTH_SHORT).show();

            }

        }
        if (checker.equals("image")) {
            if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                try {
                    uploadFile(data.getData(),"Images", "image");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (checker.equals("camera")){
            if (requestCode==MY_CAMERA_REQUEST_CODE&&resultCode==Activity.RESULT_OK){
                try {
                    uploadFile(filePath,"Images", "image");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (checker.equals("document")){
            if (requestCode == RC_TAKE_DOCUMENT && resultCode == RESULT_OK && data != null && data.getData() != null) {

                try {
                    uploadFile(data.getData(),"Documents", "document");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (checker.equals("location")){
            if (requestCode==PLACE_PICKER_REQUEST&&requestCode==RESULT_OK){
                Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    address = place.getAddress().toString();
                    latitude = latLng.latitude+"";
                    longitude = latLng.longitude+"";
                    fileUploadContainer.setVisibility(View.VISIBLE);
                    Message newMessage = new Message();
                    newMessage.text = "Location";
                    newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    newMessage.idReceiver = roomId;
                    newMessage.address=address;
                    newMessage.latitude=latitude;
                    newMessage.longitude=longitude;
                    newMessage.timestamp = System.currentTimeMillis();
                    newMessage.type="location";
                    FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                    fileUploadContainer.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(this,"Place Not Picked",Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (checker.equals("audio")){
            if (requestCode == RC_TAKE_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {

                try {
                    uploadFile(data.getData(),"Audios", "audio");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (checker.equals("video")){
            if (requestCode == RC_TAKE_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
                try {
                    uploadFile(data.getData(),"Videos", "video");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadFile(Uri data, String typeOfData, final String contentType) throws IOException {
        if(typeOfData.equals("Images")) {
            compressAndSendImage(data);
        }
        else {
            if(new File(data.getPath()).length()<=26214400) {
                String fileName = null;
                String localName = null;
                try {
                    String filePath = getPath(this, data);
                    localName = filePath.substring(filePath.lastIndexOf("/")+1);
                    fileName = new Date().getTime() + " " +  localName;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                sRef = mStorageReference.child(typeOfData).child( FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(roomId).child(fileName);
                Log.d("File Upload", fileName);
                UploadTask uploadTask = sRef.putFile(data);
                showNotification(uploadTask, contentType, data, this, localName);
            }
            else {
                showToast("Files larger thar 25 MB cannot be uploaded");
            }
        }
    }

    private void compressAndSendImage (Uri data) throws IOException {
        byte[] bdata = compressImage(data);
        String localName = getFileName(data);
        String fileName = new Date().getTime() + localName;
        sRef = mStorageReference.child("Images").child( FirebaseAuth.getInstance().getCurrentUser().getUid()).child(fileName);
        UploadTask uploadTask = sRef.putBytes(bdata);

        showNotification(uploadTask, "image", data, this, localName);
    }

    @SuppressLint("NewApi")
    private byte[] compressImage (Uri imageUri) throws IOException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(bitmap.getAllocationByteCount()>512000) {
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
        }
        else if(bitmap.getAllocationByteCount()>256000) {
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        }
        else {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        }
        byte[] bdata = byteArrayOutputStream.toByteArray();
        return bdata;
    }


    private void showNotification(final UploadTask uploadTask, final String contentType, final Uri data, final Context context, final String fileName) {
        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        fileUploadContainer.setVisibility(View.VISIBLE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID)
                .setContentTitle("Uploading File: " + fileName)
                .setContentText("Uploading...")
                .setSmallIcon(R.drawable.ic_attach_file_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100,0,false);

        notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setupChannels(notificationManager);
        }
        Log.d("Upload Local Uri", data.toString());

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0f*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                mBuilder.setProgress(100,(int)progress,false);
                notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());
            }
        })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }
                        else {
                            return sRef.getDownloadUrl();
                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        mBuilder.setContentText("Download complete")
                                .setProgress(0,0,false);
                        notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());
                        String status;
                        if(task.isSuccessful()) {
                            try {
                                sendData(task.getResult().toString(), contentType, null, getPath(context, data), fileName);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                            status = "Upload Succesful";
                            fileUploadContainer.setVisibility(View.GONE);

                        }
                        else {
                            status = "Upload Failed";
                            fileUploadContainer.setVisibility(View.GONE);
                        }
                        showToast(status);
                    }
                });
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;

        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    Log.d("File Path", cursor.getString(column_index));
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.d("File Name", result);
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void showToast(String text) {
        Toast.makeText(this, text,Toast.LENGTH_LONG).show();
    }
    private void sendData(String data, String contentType, String thumbnailURL, String localMediaURL, String fileName) {
        long Time = new Date().getTime();

        Message newMessage = new Message();
        newMessage.text = fileName;
        newMessage.type=contentType;
        newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newMessage.idReceiver = roomId;
        newMessage.urlFile =data;
        newMessage.download="notDownload";
        newMessage.localFileUrl=localMediaURL;
        newMessage.timestamp = System.currentTimeMillis();
        FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                Message newMessage = new Message();
                newMessage.text = content;
                newMessage.idSender = StaticConfig.UID;
                newMessage.idReceiver = roomId;
                newMessage.type="text";
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
            }
        }
    }
}

class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Consersation consersation;
    private HashMap<String, Bitmap> bitmapAvata;
    private HashMap<String, DatabaseReference> bitmapAvataDB;
    private Bitmap bitmapAvataUser;
    ValueEventListener eventListener;
    private ChatActivity chatActivity;

   int lastClickedPosition =-1;



    public ListMessageAdapter(Context context, Consersation consersation, HashMap<String, Bitmap> bitmapAvata, Bitmap bitmapAvataUser) {
        this.context = context;
        this.consersation = consersation;
        this.bitmapAvata = bitmapAvata;
        this.bitmapAvataUser = bitmapAvataUser;
        bitmapAvataDB = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {

        return consersation.getListMessageData().get(position).idSender.equals(StaticConfig.UID) ? ChatActivity.VIEW_TYPE_USER_MESSAGE : ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;

    }

    @Override
    public int getItemCount() {
        return consersation.getListMessageData().size();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemMessageFriendHolder) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    CharSequence options[]=new CharSequence[]{

                            "Forward"

                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                ((ChatActivity)context).forwardMethod(position);
                                ((ChatActivity)context).selectedPosition=position;
                                Log.d("Long Click", consersation.getListMessageData().get(position).text + position);

                            }


                        }
                    });
                    builder.show();
                    return true;

                }
            });
            final Message message = consersation.getListMessageData().get(position);
            if (message.type.equals("text")) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        CharSequence options[]=new CharSequence[]{


                                "Copy",
                                "Forward"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("ChatMessage", consersation.getListMessageData().get(position).text);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                                }
                                if(i==1){
                                    ((ChatActivity)context).forwardMethod(position);
                                    ((ChatActivity)context).selectedPosition=position;
                                    Log.d("Long Click", consersation.getListMessageData().get(position).text + position);

                                }

                            }
                        });
                        builder.show();
                        return true;

                    }
                });
                ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.VISIBLE);
                ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);



            } else {
                ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
            }


            if (message.type.equals("image")) {
                if (message.urlFile != null) {
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);

                    if (message.download.equals("notDownload")) {
                        ((ItemMessageFriendHolder) holder).download.setVisibility(View.VISIBLE);
                        ((ItemMessageFriendHolder) holder).download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.VISIBLE);

                                final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(message.urlFile);
                                storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                                        String fileName = task.getResult().getName();
                                        Log.d("File Download", fileName);
                                        File downloadLocation = null;

                                        downloadLocation = new File(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES),
                                                "Alta Messaging Images");

                                        downloadLocation.mkdir();

                                        Log.d("Download", fileName);

                                        final File downloadFile = new File(downloadLocation, fileName);
                                        storageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).download.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.VISIBLE);
                                                SharedPreferences room = context.getSharedPreferences("ROOMID", MODE_PRIVATE);
                                                final String roomId = room.getString("room", "");
                                              /*  SharedPreferences friend = context.getSharedPreferences("FriendId", MODE_PRIVATE);
                                                final String friendid=friend.getString("friend","");*/
                                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    Message message = snapshot.getValue(Message.class);
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("download", "downloaded");
                                                                    snapshot.getRef().updateChildren(hashMap);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                            }
                                        });
                                        storageReference.getFile(downloadFile).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to Download", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {
                        ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.VISIBLE);

                    }

                    Glide.with(context).load(message.urlFile)
                            .into(((ItemMessageFriendHolder) holder).imageView);

                    ((ItemMessageFriendHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, FullScreenImageActivity.class);
                            intent.putExtra("userId", consersation.getListMessageData().get(position).idReceiver);
                            intent.putExtra("urlPhotoUser", "");
                            intent.putExtra("user", "friend");
                            intent.putExtra("urlPhotoClick", message.urlFile);
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("video")) {
                if (message.urlFile != null) {
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);

                    if (message.download.equals("notDownload")) {
                        ((ItemMessageFriendHolder) holder).download.setVisibility(View.VISIBLE);
                        ((ItemMessageFriendHolder) holder).download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.VISIBLE);

                                final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(message.urlFile);
                                storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                                        String fileName = task.getResult().getName();
                                        Log.d("File Download", fileName);
                                        File downloadLocation = null;

                                        downloadLocation = new File(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_MOVIES),
                                                "Alta Messaging Videos");

                                        downloadLocation.mkdir();

                                        Log.d("Download", fileName);

                                        final File downloadFile = new File(downloadLocation, fileName);
                                        storageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).download.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.VISIBLE);
                                                SharedPreferences room = context.getSharedPreferences("ROOMID", MODE_PRIVATE);
                                                final String roomId = room.getString("room", "");
                                              /*  SharedPreferences friend = context.getSharedPreferences("FriendId", MODE_PRIVATE);
                                                final String friendid=friend.getString("friend","");*/
                                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    Message message = snapshot.getValue(Message.class);
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("download", "downloaded");
                                                                    snapshot.getRef().updateChildren(hashMap);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });


                                            }
                                        });
                                        storageReference.getFile(downloadFile).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to Download", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {
                        ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.VISIBLE);

                    }


                    ((ItemMessageFriendHolder) holder).videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("Video URL", message.urlFile);
                            intent.putExtra("type", "video");
                            intent.putExtra("user", "friend");
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("document")) {
                if (message.urlFile != null) {
                    final Uri uri = Uri.parse(message.localFileUrl);

                    final String fileName = uri.getLastPathSegment();

                    if (fileName.contains(".doc") || fileName.contains(".docx")) {
                        ((ItemMessageFriendHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_doc_file));

                    } else if (fileName.contains(".pdf")) {
                        ((ItemMessageFriendHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_pdf_file));

                    } else if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
                        ((ItemMessageFriendHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_ppt_file));

                    } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
                        ((ItemMessageFriendHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_excel_file));

                    } else {
                        ((ItemMessageFriendHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_attach_document));

                    }


                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                    if (message.download.equals("notDownload")) {
                        ((ItemMessageFriendHolder) holder).download.setVisibility(View.VISIBLE);
                        ((ItemMessageFriendHolder) holder).download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.VISIBLE);

                                final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(message.urlFile);
                                storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                                        String fileName = task.getResult().getName();
                                        Log.d("File Download", fileName);
                                        File downloadLocation = null;

                                        downloadLocation = new File(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_DOCUMENTS),
                                                "Alta Messaging Documents");

                                        downloadLocation.mkdir();

                                        Log.d("Download", fileName);

                                        final File downloadFile = new File(downloadLocation, fileName);
                                        storageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).download.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.VISIBLE);
                                                SharedPreferences room = context.getSharedPreferences("ROOMID", MODE_PRIVATE);
                                                final String roomId = room.getString("room", "");
                                              /*  SharedPreferences friend = context.getSharedPreferences("FriendId", MODE_PRIVATE);
                                                final String friendid=friend.getString("friend","");*/
                                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    Message message = snapshot.getValue(Message.class);
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("download", "downloaded");
                                                                    snapshot.getRef().updateChildren(hashMap);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });


                                            }
                                        });
                                        storageReference.getFile(downloadFile).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to Download", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {
                        ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.VISIBLE);

                    }


                    ((ItemMessageFriendHolder) holder).pdfContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            if (fileName.contains(".doc") || fileName.contains(".docx")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/msword");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/msword");

                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }
                            } else if (fileName.contains(".pdf")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                // viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/pdf");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/pdf");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }

                            } else if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                // viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/vnd.ms-powerpoint");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/vnd.ms-powerpoint");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }

                            } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/vnd.ms-excel");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/vnd.ms-excel");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                String extension = MimeTypeMap.getFileExtensionFromUrl(message.urlFile);
                                if (extension != null) {
                                    Intent viewIntent = new Intent();
                                    viewIntent.setAction(Intent.ACTION_VIEW);
                                    //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)),  MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                    viewIntent.setDataAndType(Uri.parse(message.urlFile), MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                    List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                    if (resolved != null && resolved.size() > 0) {
                                        context.startActivity(viewIntent);
                                    } else {
                                        Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    });

                } else {
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("audio")) {
                if (message.urlFile != null) {
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);


                    if (message.download.equals("notDownload")) {
                        ((ItemMessageFriendHolder) holder).download.setVisibility(View.VISIBLE);
                        ((ItemMessageFriendHolder) holder).download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.VISIBLE);

                                final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(message.urlFile);
                                storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                                        String fileName = task.getResult().getName();
                                        Log.d("File Download", fileName);
                                        File downloadLocation = null;

                                        downloadLocation = new File(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_MUSIC),
                                                "Alta Messaging Audios");

                                        downloadLocation.mkdir();

                                        Log.d("Download", fileName);

                                        final File downloadFile = new File(downloadLocation, fileName);
                                        storageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).download.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.VISIBLE);
                                                SharedPreferences room = context.getSharedPreferences("ROOMID", MODE_PRIVATE);
                                                final String roomId = room.getString("room", "");
                                              /*  SharedPreferences friend = context.getSharedPreferences("FriendId", MODE_PRIVATE);
                                                final String friendid=friend.getString("friend","");*/
                                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    Message message = snapshot.getValue(Message.class);
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("download", "downloaded");
                                                                    snapshot.getRef().updateChildren(hashMap);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });


                                            }
                                        });
                                        storageReference.getFile(downloadFile).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to Download", Toast.LENGTH_SHORT).show();
                                                ((ItemMessageFriendHolder) holder).progress.setVisibility(View.GONE);

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {
                        ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.VISIBLE);

                    }


                    ((ItemMessageFriendHolder) holder).audioView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("Video URL", message.urlFile);
                            intent.putExtra("type", "audio");
                            intent.putExtra("user", "friend");
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("location")) {
                if (message.address != null) {
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);


                    ((ItemMessageFriendHolder) holder).tvLocation.setText(consersation.getListMessageData().get(position).address);
                    ((ItemMessageFriendHolder) holder).tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String latitude = consersation.getListMessageData().get(position).latitude;
                            String longitude = consersation.getListMessageData().get(position).longitude;
                            String uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude, longitude, latitude, longitude);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageFriendHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            Bitmap currentAvata = bitmapAvata.get(consersation.getListMessageData().get(position).idSender);
            if (currentAvata != null) {
                ((ItemMessageFriendHolder) holder).avata.setImageBitmap(currentAvata);

            } else {
                final String id = consersation.getListMessageData().get(position).idSender;
                if (bitmapAvataDB.get(id) == null) {
                    bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                    bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String avataStr = (String) dataSnapshot.getValue();
                                if (!avataStr.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                    byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                    ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                } else {
                                    ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.profile));
                                }
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        } else if (holder instanceof ItemMessageUserHolder) {

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    CharSequence options[]=new CharSequence[]{

                            "Forward"

                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                ((ChatActivity)context).forwardMethod(position);
                                ((ChatActivity)context).selectedPosition=position;
                                Log.d("Long Click", consersation.getListMessageData().get(position).text + position);

                            }


                        }
                    });
                    builder.show();
                    return true;

                }
            });
            final Message message = consersation.getListMessageData().get(position);
            if (message.type.equals("text")) {

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        CharSequence options[]=new CharSequence[]{


                                "Copy",
                                "Forward"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("ChatMessage", consersation.getListMessageData().get(position).text);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                                }
                                if(i==1){

                                    ((ChatActivity)context).forwardMethod(position);
                                    ((ChatActivity)context).selectedPosition=position;
                                    Log.d("Long Click", consersation.getListMessageData().get(position).text + position);

                                }

                            }
                        });
                        builder.show();
                        return true;

                    }
                });
                ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.VISIBLE);
                ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);


            } else {
                ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);
            }

            if (message.type.equals("image")) {

                if (message.urlFile != null) {
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);


                    Glide.with(context).load(message.urlFile)
                            .into(((ItemMessageUserHolder) holder).imageView);
                    ((ItemMessageUserHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, FullScreenImageActivity.class);
                            intent.putExtra("userId", consersation.getListMessageData().get(position).idSender);
                            intent.putExtra("urlPhotoUser", "");
                            intent.putExtra("urlPhotoClick", message.urlFile);
                            intent.putExtra("user", "user");
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("document")) {
                if (message.urlFile != null) {
                    final Uri uri = Uri.parse(message.localFileUrl);
                    final String fileName = uri.getLastPathSegment();

                    if (fileName.contains(".doc") || fileName.contains(".docx")) {
                        ((ItemMessageUserHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_doc_file));

                    } else if (fileName.contains(".pdf")) {
                        ((ItemMessageUserHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_pdf_file));

                    } else if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
                        ((ItemMessageUserHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_ppt_file));

                    } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
                        ((ItemMessageUserHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_excel_file));

                    } else {
                        ((ItemMessageUserHolder) holder).pdfContent.setBackground(context.getResources().getDrawable(R.drawable.ic_attach_document));
                    }


                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);


                    ((ItemMessageUserHolder) holder).pdfContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            if (fileName.contains(".doc") || fileName.contains(".docx")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                //   viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/msword");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/msword");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }
                            } else if (fileName.contains(".pdf")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/pdf");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/pdf");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }

                            } else if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/vnd.ms-powerpoint");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/vnd.ms-powerpoint");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }

                            } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
                                Intent viewIntent = new Intent();
                                viewIntent.setAction(Intent.ACTION_VIEW);
                                //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)), "application/vnd.ms-excel");
                                viewIntent.setDataAndType(Uri.parse(message.urlFile), "application/vnd.ms-excel");
                                List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                if (resolved != null && resolved.size() > 0) {
                                    context.startActivity(viewIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                String extension = MimeTypeMap.getFileExtensionFromUrl(message.urlFile);
                                if (extension != null) {
                                    Intent viewIntent = new Intent();
                                    viewIntent.setAction(Intent.ACTION_VIEW);
                                    //viewIntent.setDataAndType(Uri.fromFile(new File(message.urlFile)),  MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                    viewIntent.setDataAndType(Uri.parse(message.urlFile), MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                    List<ResolveInfo> resolved = context.getPackageManager().queryIntentActivities(viewIntent, 0);
                                    if (resolved != null && resolved.size() > 0) {
                                        context.startActivity(viewIntent);
                                    } else {
                                        Toast.makeText(context, "Cannot open this file", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    });

                } else {
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("audio")) {
                if (message.urlFile != null) {
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);

                    ((ItemMessageUserHolder) holder).audioView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("Video URL", message.urlFile);
                            intent.putExtra("type", "audio");
                            intent.putExtra("user", "user");
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("video")) {
                if (message.urlFile != null) {
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);

                    ((ItemMessageUserHolder) holder).videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("Video URL", message.urlFile);
                            intent.putExtra("type", "video");
                            intent.putExtra("user", "user");
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (message.type.equals("location")) {
                if (message.address != null) {
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);

                    ((ItemMessageUserHolder) holder).tvLocation.setText(consersation.getListMessageData().get(position).address);
                    ((ItemMessageUserHolder) holder).tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String latitude = consersation.getListMessageData().get(position).latitude;
                            String longitude = consersation.getListMessageData().get(position).longitude;
                            String uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude, longitude, latitude, longitude);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ItemMessageUserHolder) holder).imageView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).pdfContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoView.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tvLocation.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).audioView.setVisibility(View.GONE);
                }
            }
            if (bitmapAvataUser != null) {
                ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);

            }
        }
    }
}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent,tvLocation;
    public CircleImageView avata;
    public ImageButton pdfContent;
    public ImageView imageView,videoView,audioView;


    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView2);
        pdfContent=(ImageButton)itemView.findViewById(R.id.documentUser);
        imageView = itemView.findViewById(R.id.image_holder);
        tvLocation=itemView.findViewById(R.id.tvLocationUser);
        videoView=itemView.findViewById(R.id.videoUser);
        audioView=itemView.findViewById(R.id.audioUser);


    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent,tvLocation;
    public CircleImageView avata;
    public ImageButton pdfContent;
    public ProgressBar progress;
    public ImageView imageView,videoView,audioView,download;

    public FrameLayout messageselect;


    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView3);
        pdfContent=(ImageButton)itemView.findViewById(R.id.documentFriend);
        imageView = itemView.findViewById(R.id.image_holder1);
        tvLocation=itemView.findViewById(R.id.tvLocationFriend);
        videoView=itemView.findViewById(R.id.videoFriend);
        audioView=itemView.findViewById(R.id.audiofriend);
        download=itemView.findViewById(R.id.download_button);
        progress=itemView.findViewById(R.id.download_progress);



    }
}
