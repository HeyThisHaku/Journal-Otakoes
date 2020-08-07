package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.weaboo.jurnalotaku.R;

import java.util.Vector;

import models.Post;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private int [] imageId = new int[]{R.drawable.content_1,R.drawable.content_2,R.drawable.content_3};
    private Vector<Post> listPostingan = new Vector<>();


    public static void loadImageUrl(String url,Context context,ImageView placeHolder){
        Picasso.get().load(url).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(placeHolder,new com.squareup.picasso.Callback(){

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

            }


        });
    }


    public ImageAdapter(Context context, Vector<Post>listPostingan){
        mContext = context;
        this.listPostingan = listPostingan;
    }


    public void insertimageId(){
        //ambil data
        //insert ke imageId
    }

    @Override
    public int getCount() {
        return listPostingan.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        loadImageUrl(listPostingan.get(position).getImageUrl(),mContext,imageView);
        container.addView(imageView,0);
        return  imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);
    }
}
