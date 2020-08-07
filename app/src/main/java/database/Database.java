package database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database {
    private static Database database= null;
    private  DatabaseReference ref;
    private  FirebaseDatabase firebase = FirebaseDatabase.getInstance();

    private  Database(){ }

    public static Database getInstance(){
        if(database == null){
            database = new Database();
        }
        return database;
    }

    public void setRefer(String refer){
        this.ref = firebase.getReference(refer);
    }

    public DatabaseReference getRefer(){return ref;}

}
