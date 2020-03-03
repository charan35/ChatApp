package com.example.altachatapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataProvider {

    public static HashMap<String, List<String>> getInfo()
    {
        HashMap<String, List<String>> MoviesDetails = new HashMap<String, List<String>>();

        List<String> Ques1 = new ArrayList<String>();
        Ques1.add("AltaChat is a messaging app with a focus on speed and security, it’s super-fast, simple and free. You can use AltaChat on all your devices at the same time — your messages sync seamlessly across any number of your phones, tablets or computers.\n" +
                "\n" +
                "With AltaChat, you can send messages, photos, videos and files of any type (doc, zip, mp3, etc), as well as create groups for up to 200,000 people or channels for broadcasting to unlimited audiences. AltaChat is like mobile number combined — and can take care of all your personal needs.");


        List<String> Ques2 = new ArrayList<String>();
        Ques2.add(" Unlike others, AltaChat is a  Firebase based messenger with seamless sync. As a result, you can access your messages from several devices at once, including tablets, and share an unlimited number of photos, videos and files (doc, zip, mp3, etc.) of up to 25 MB each.\n" +
                "\n" +
                "AltaChat is faster and way more secure. On top of that, AltaChat is free and will stay free — no ads, no subscription fees, forever.");


        List<String> Ques3= new ArrayList<String>();
        Ques3.add("You can use AltaChat on smartphones, tablets which are having Android (4.1 and up). You can also use AltaChat's web version for Windows(which we are going to launch soon).\n" +
                "\n" +
                "You can log in to AltaChat from as many of your devices as you like — all at the same time. Just use your mobile number and email to log in everywhere.");


        List<String> Ques4= new ArrayList<String>();
        Ques4.add("NO");

        List<String> Ques5= new ArrayList<String>();
        Ques5.add("We are not going to make money with this.\n"+"\n"+
                "We believe in fast and secure messaging. So, AltaChat is 100% free.");

        List<String> Ques6= new ArrayList<String>();
        Ques6.add("You can easily update AltaChat from your phone's application store. Please note if you received a message that isn't supported by your version of AltaChat, you'll need to update AltaChat.\n" +
                "\n" +
                "To update AltaChat:\n" +
                "Android: Go to Play Store, then tap Menu  > My apps & games. Tap UPDATE next to AltaChat.\n" +
                "Alternatively, go to Play Store and search for AltaChat. Tap UPDATE under AltaChat.\n\n"+
                "We encourage you to always use the latest version of AltaChat. Latest versions contain the newest features and bug fixes.");

        List<String> Ques7= new ArrayList<String>();
        Ques7.add("Yes, We can use AltaChat on multiple phones at a time.");

             List<String> Ques9= new ArrayList<String>();
        Ques9.add("AltaChat uses your phone's Internet connection (4G/3G/2G/EDGE or Wi-Fi, as available) to send and receive messages to your friends and family. You don't have to pay for every message. As long as you haven't exceeded your data limit or you're connected to a free Wi-Fi network, your carrier shouldn't charge you extra for messaging over AltaChat\n"+
                "\n" +
                "Please be aware that:\n" +
                        "\n" +
                        "If your phone is roaming, additional mobile data charges may apply. Learn how to configure roaming settings for AltaChat on: Android\n" +
                        "If you send SMS messages to your friends inviting them to use WhatsApp, service charges from your mobile provider may also apply.");

        List<String> Ques10= new ArrayList<String>();
        Ques10.add("To send to a message:\n" +
                "Android: Type your response and tap Send .");

        List<String> Ques11= new ArrayList<String>();
        Ques11.add("Last Seen and online tell you the last time your contacts were using AltaChat, or if they're online.\n" +
                "\n" +
                "If a contact is online, they have AltaChat open in the foreground on their device and are connected to the Internet. However, it doesn't necessarily mean the contact has read your message.\n" +
                "\n" +
                "Last Seen refers to the last time the contact used AltaChat. Please note you can't hide your online.");

        List<String> Ques12= new ArrayList<String>();
        Ques12.add("If you'd like to stop receiving messages from AltaChat, please follow these steps:\n" +

                "Android:\n" +
                "Open settings from navigation menu >> Turn off new message notification\n" +
                        "You won't get any notifications until you Turn on new message notification");

        List<String> Ques13= new ArrayList<String>();
        Ques13.add("To add a new Firend:\n" +
                "\n" +
                "Click on the ADD(+) button in friends list page. It will show your contacts list who are using AltaChat, and select the person to whom you are going to add and press ok.\n"+
                "It will add your friend in your friends list.");

        List<String> Ques14= new ArrayList<String>();
        Ques14.add("To add a new Group:\n" +
                "\n" +
                "Click on the AddGroup button which is at the right bottom of the groups page.It will ask to enter Group name, Group picture and the friends whom you wish to add(minimum members to add in group is 2) in that group(By checking on the friend name you can add him/her to the group)\n"+
                "And press Create Group button");


        MoviesDetails.put("What is AltaChat? What can we do here?", Ques1);
        MoviesDetails.put("How is AltaChat different from others?", Ques2);
        MoviesDetails.put("Which devices can we use?", Ques3);
        MoviesDetails.put("Will you have ads? Or sell my data?", Ques4);
        MoviesDetails.put("How are we going to make money out of this?", Ques5);
        MoviesDetails.put("How to update AltaChat?", Ques6);
        MoviesDetails.put("Can we use AltaChat account on multiple phones, or with multiple phone numbers?", Ques7);
        MoviesDetails.put("Is it free to send messages over AltaChat?", Ques9);
        MoviesDetails.put("How to send to a message?", Ques10);
        MoviesDetails.put("What is Last Seen and online?", Ques11);
        MoviesDetails.put("How to disable notifications from AltaChat?", Ques12);
        MoviesDetails.put("How to add Friends?", Ques13);
        MoviesDetails.put("How to add Group?", Ques14);

        return MoviesDetails;

    }

}
