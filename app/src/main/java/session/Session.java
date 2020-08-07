package session;

import java.util.HashMap;
import java.util.Vector;

import models.Post;

public class Session {
    private  static HashMap<String,String> session = new HashMap<>();
    private static Vector<Post> listIdForJournal = new Vector<>();
    public static void setSession(String key,String value){
        session.put(key,value);
    }

    public static String getData(String key){
        if(session.get(key) != null)
            return session.get(key).toString();
        return null;
    }
    public static void deleteJournalQueue(Post post){listIdForJournal.remove(post);}
    public static void addJournalQueue(Post post){
        listIdForJournal.add(post);
    }
    public static Vector<Post> getJournalQueue(){
        return listIdForJournal;
    }
    public static void clearJournal(){
        listIdForJournal.clear();
    }
}
