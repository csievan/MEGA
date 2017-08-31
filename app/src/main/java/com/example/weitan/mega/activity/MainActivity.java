package com.example.weitan.mega.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kanmanus.kmutt.sit.ijoint.MyApplication;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.fragment.ExerciseSampleVideoFragment;
import com.kanmanus.kmutt.sit.ijoint.fragment.ProfileFragment;
import com.kanmanus.kmutt.sit.ijoint.fragment.TreatmentFragment;
import com.kanmanus.kmutt.sit.ijoint.models.PatientProfileViewModel;
import com.kanmanus.kmutt.sit.ijoint.models.TabModel;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpagertab)
    SmartTabLayout viewpagertab;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private ArrayList<TabModel> tabModels;
    PatientProfileViewModel profileViewModel;

    public static Intent callingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        profileViewModel = MyApplication.getInstance().getSession();
        setSupportActionBar(toolbar);
        setupTabLayout();
        getSupportActionBar().setTitle(getString(R.string.tab_treatment));
        toolbar.setSubtitle(profileViewModel.getFullName());
    }

    private void setupTabLayout() {
        setTabs();
        FragmentPagerItems pages = new FragmentPagerItems(this);
        viewpagertab.setCustomTabView(new TabLayoutProvider(tabModels, viewpagertab));
        for (TabModel tabModel : tabModels) {
            pages.add(FragmentPagerItem.of("",tabModel.getFragment(),tabModel.getBundle()));
        }
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewpager.setAdapter(adapter);
        viewpagertab.setViewPager(viewpager);
        viewpagertab.setOnPageChangeListener(pageChangeListener);
    }

    private void setTabs() {
        tabModels = new ArrayList<>();
        TabModel treatmentTabModel = new TabModel();
        treatmentTabModel.setTitle(getString(R.string.tab_treatment));
        treatmentTabModel.setFragment(TreatmentFragment.class);
        treatmentTabModel.setIconResId(R.drawable.ic_enhanced_encryption);

        TabModel videoTabModel = new TabModel();
        videoTabModel.setTitle(getString(R.string.tab_sample_video));
        videoTabModel.setFragment(ExerciseSampleVideoFragment.class);
        videoTabModel.setIconResId(R.drawable.ic_info);

        TabModel profileTabModel = new TabModel();
        profileTabModel.setTitle(getString(R.string.tab_profile));
        profileTabModel.setFragment(ProfileFragment.class);
        profileTabModel.setIconResId(R.drawable.ic_account_box);


        tabModels.add(profileTabModel);
        tabModels.add(videoTabModel);
        tabModels.add(treatmentTabModel);
    }

    private class TabLayoutProvider implements SmartTabLayout.TabProvider {
        private final SmartTabLayout tabLayout;
        private final ArrayList<TabModel> tabModels;

        public TabLayoutProvider(ArrayList<TabModel> tabModels, SmartTabLayout tabLayout) {
            this.tabLayout = tabLayout;
            this.tabModels = tabModels;
        }

        @Override
        public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
            final LayoutInflater inflater = LayoutInflater.from(tabLayout.getContext());
            ImageView icon = (ImageView) inflater.inflate(R.layout.custom_tab_icon, container, false);
            icon.setImageResource(tabModels.get(position).getIconResId());
            return icon;
        }
    }


    /*Listener*/
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            toolbar.setTitle(tabModels.get(position).getTitle());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
