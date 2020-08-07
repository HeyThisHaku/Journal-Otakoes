package layoutrecycle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.R;

public class PendingRequestHolder extends RecyclerView.ViewHolder{

    public Button acceptBtn,declineBtn;
    public ImageView profileImg;
    public TextView username;
    public PendingRequestHolder(@NonNull View itemView) {
        super(itemView);
        this.acceptBtn=itemView.findViewById(R.id.req_accept_btn);
        this.declineBtn=itemView.findViewById(R.id.req_decline_btn);
        this.profileImg=itemView.findViewById(R.id.pending_profile_img);
        this.username=itemView.findViewById(R.id.pending_username);
    }

}
