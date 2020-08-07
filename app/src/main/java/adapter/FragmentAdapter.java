package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Vector;

public class FragmentAdapter extends FragmentPagerAdapter {
    private Vector<Fragment> listFragment = new Vector<>();
    private Vector<String> listTitle = new Vector<>();
    public FragmentAdapter(@NonNull FragmentManager fm,Vector<Fragment>listFragment,Vector<String>listTitle) {
        super(fm);
        this.listFragment = listFragment;
        this.listTitle = listTitle;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return listTitle.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }
}
