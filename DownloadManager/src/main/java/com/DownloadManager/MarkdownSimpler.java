//package com.DownloadManager;
//
//import java.io.*;
//
//public class MarkdownSimpler {
//
//    final File f;
//
//    public MarkdownSimpler(File f) throws Exception {
//        this.f = f;
//    }
//
//    public String seperate() throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader(f));
//        String st;
//        String str = null;
//        String[] prevTokens = new String[0];
//        String[] Tokens ;
//        while ((st = br.readLine()) != null)
//            prevTokens = st.trim().split("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
//        for (String s : prevTokens) {
//            Tokens=s.split("#@\\+\\*\\[!]");
//            str = str + " " + s;
//        }
//        return str;
//    }
//
//}
//}
