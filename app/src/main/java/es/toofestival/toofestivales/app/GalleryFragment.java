package es.toofestival.toofestivales.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.adapter.GalleryPagerAdapter;
import es.toofestival.toofestivales.adapter.WrapContentHeightViewPager;

public class GalleryFragment extends Fragment {
    private static WrapContentHeightViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<String> ImagesArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View galleryView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ImagesArray = getArguments().getStringArrayList("postersList");
        mPager = (WrapContentHeightViewPager) galleryView.findViewById(R.id.pager);
        mPager.setAdapter(new GalleryPagerAdapter(getActivity().getApplicationContext(),ImagesArray));
        CirclePageIndicator indicator = (CirclePageIndicator)
                galleryView.findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = ImagesArray.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 30000, 30000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        return galleryView;
    }

}