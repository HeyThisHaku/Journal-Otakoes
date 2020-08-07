package layoutrecycle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.R;

public class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView img;
    public TextView title;
    MenuClickListener menuClickListener;
    public MenuHolder(@NonNull View itemView) {
        super(itemView);
        this.img=itemView.findViewById(R.id.menu_icon);
        this.title=itemView.findViewById(R.id.menu_title);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.menuClickListener.onMenuClickListener(v, getLayoutPosition());
    }
    public void setMenuClickListener(MenuClickListener mc){
        this.menuClickListener=mc;
    }
}
