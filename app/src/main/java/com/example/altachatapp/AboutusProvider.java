package com.example.altachatapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutusProvider {



    public static HashMap<String, List<String>> getInfo() {
        HashMap<String, List<String>> MoviesDetails = new HashMap<String, List<String>>();

        List<String> Ques1 = new ArrayList<String>();
        Ques1.add("Quality tops our check list in every project work we do. Quality is the way we make sure that we meet customer expectation and it gives significance and respect for their brands. We trust in doing quality work than quantity of marketing we do to promote our self.");



        List<String> Ques2 = new ArrayList<String>();
        Ques2.add("We feel proud of being honest way of doing business. We believe, great achievements need lot of trust among the customers, management and employees and we always put in utmost efforts to be an open book and win hearts.");


        List<String> Ques3 = new ArrayList<String>();
        Ques3.add("We're approachable and love to be part of the creative process. You're welcome to sit in with us and share your ideas come to life.");

        List<String> Ques4 = new ArrayList<String>();
        Ques4.add("We give amazing design and technology at affordable cost. We ensure specializing in finding the most cost-effective ways to grow your business.");




        MoviesDetails.put("Quality First Last and Forever", Ques1);
        MoviesDetails.put("Clear Cut", Ques2);
        MoviesDetails.put("Approachable", Ques3);
        MoviesDetails.put("Cost Effective", Ques4);


        return MoviesDetails;

    }


    }


