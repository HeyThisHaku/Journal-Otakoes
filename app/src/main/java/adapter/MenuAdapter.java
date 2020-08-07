package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.ManageProfile;
import com.weaboo.jurnalotaku.R;
import com.weaboo.jurnalotaku.ShowFavouritePost;

import java.util.Vector;

import layoutrecycle.MenuClickListener;
import layoutrecycle.MenuHolder;
import models.ProfileMenu;

public class MenuAdapter extends RecyclerView.Adapter<MenuHolder>{
    public static final int EDIT_PROFILE = 0;
    public static final int CHANGE_LANGUAGE = 1;
    private Context c;
    private Vector<ProfileMenu> profileMenus;

    public MenuAdapter(Context c, Vector<ProfileMenu> profileMenus) {
        this.c = c;
        this.profileMenus = profileMenus;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_rows,parent,false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        if(position==EDIT_PROFILE){

            holder.title.setText(R.string.edit_profile);
        }
        else if(position==CHANGE_LANGUAGE){
            holder.title.setText(R.string.favourite_post);
        }
        holder.img.setImageResource(profileMenus.get(position).getImg());
        holder.setMenuClickListener(new MenuClickListener() {
            @Override
            public void onMenuClickListener(View v, int position) {
                if(position==EDIT_PROFILE){
                    c.startActivity(new Intent(c, ManageProfile.class));
                }
                else if(position==CHANGE_LANGUAGE){
                    //favorit post
                    c.startActivity(new Intent(c, ShowFavouritePost.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileMenus.size();
    }
}
