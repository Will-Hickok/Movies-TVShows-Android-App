package com.app.techvalley.movies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.techvalley.movies.adapters.BannerViewPagerAdapter;
import com.app.techvalley.movies.adapters.TVShowFragmentPager;
import com.app.techvalley.movies.fragments.CastTVShowFragment;
import com.app.techvalley.movies.fragments.InfoAboutTVShowFragment;
import com.app.techvalley.movies.models.BackdropImage;
import com.app.techvalley.movies.models.Cast;
import com.app.techvalley.movies.models.Genre;
import com.app.techvalley.movies.models.Video;
import com.app.techvalley.movies.network.AboutTVShowResponse;
import com.app.techvalley.movies.network.ApiService;
import com.app.techvalley.movies.network.CreditResponse;
import com.app.techvalley.movies.network.ImageResponse;
import com.app.techvalley.movies.network.URLConstants;
import com.app.techvalley.movies.utils.IntentConstants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AboutTVShowActivity extends AppCompatActivity {
    private ViewPager mBannerViewPager;
    private BannerViewPagerAdapter bannerViewPagerAdapter;
    private ArrayList<String> allBannerImageFullLinks;
    ImageView poster;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private TVShowFragmentPager tvShowFragmentPager;
    TextView tvShowNameTextView;
    Video obj;
    boolean doFirst = true;
    int currentPage = 0;
    TextView genreTextView;
    RadioGroup radioGroupTvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_tvshow);
        setTitle("");

        Intent intent = getIntent();
        final int tvShow_id = intent.getIntExtra(IntentConstants.INTENT_KEY_TVSHOW_ID, 0);
        String posterPath = intent.getStringExtra(IntentConstants.INTENT_KEY_POSTER_PATH);
        final String tvShowName = intent.getStringExtra(IntentConstants.INTENT_KEY_TVSHOW_NAME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        allBannerImageFullLinks = new ArrayList<>();
        mBannerViewPager = findViewById(R.id.tvShowpager);
        radioGroupTvShow = findViewById(R.id.radioGroupTvShow);

        bannerViewPagerAdapter = new BannerViewPagerAdapter(this, allBannerImageFullLinks);
        mBannerViewPager.setAdapter(bannerViewPagerAdapter);

        mBannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (currentPage == 0 && doFirst == true) {
                    RadioButton rb = (RadioButton) radioGroupTvShow.getChildAt(0);
                    rb.setButtonDrawable(R.drawable.ic_radio_button_checked);
                    doFirst = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position > currentPage) {
                    RadioButton rb = (RadioButton) radioGroupTvShow.getChildAt(position);
                    rb.setButtonDrawable(R.drawable.ic_radio_button_checked);
                    RadioButton rbi = (RadioButton) radioGroupTvShow.getChildAt(currentPage);
                    rbi.setButtonDrawable(R.drawable.ic_radio_button_unchecked);
                    currentPage = position;
                } else {
                    RadioButton rb = (RadioButton) radioGroupTvShow.getChildAt(position);
                    rb.setButtonDrawable(R.drawable.ic_radio_button_checked);
                    RadioButton rbi = (RadioButton) radioGroupTvShow.getChildAt(currentPage);
                    rbi.setButtonDrawable(R.drawable.ic_radio_button_unchecked);
                    currentPage = position;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        poster = (ImageView) findViewById(R.id.posterWithBannerImageView);
        Picasso.get().load(URLConstants.IMAGE_BASE_URL + posterPath).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int color = palette.getDarkMutedColor(Color.parseColor("#424242"));
                        //Palette.Swatch swatch1 = palette.getDarkVibrantSwatch();
                        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
                        collapsingToolbarLayout.setBackgroundColor(color);
                        collapsingToolbarLayout.setContentScrimColor(color);
                        tabLayout.setBackgroundColor(palette.getMutedColor(Color.parseColor("#424242")));

                        /*Palette.Swatch swatch = palette.getMutedSwatch();
                        if(swatch != null)
                        tabLayout.setTabTextColors(swatch.getTitleTextColor(), Color.parseColor("#FFFFFF"));
                        tabLayout.setSelectedTabIndicatorColor(swatch.getTitleTextColor());*/

                    }
                });

                poster.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


        tvShowNameTextView = findViewById(R.id.tvShowNameTextView);
        tvShowNameTextView.setText(tvShowName);
        genreTextView = findViewById(R.id.tvShowgenreTextView);

        tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.container);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        tvShowFragmentPager = new TVShowFragmentPager(getSupportFragmentManager());

        mViewPager.setAdapter(tvShowFragmentPager);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLConstants.TVSHOW_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<ImageResponse> call = service.getBannerImages(tvShow_id, URLConstants.API_KEY);

        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                ArrayList<BackdropImage> bannerImagesLinksList = response.body().getBannerImageLinks();
                if (bannerImagesLinksList == null) {
                    return;
                }
                for (int i = 0; i < bannerImagesLinksList.size(); i++) {
                    if (i < 8) {
                        allBannerImageFullLinks.add(URLConstants.BANNER_BASE_URL + bannerImagesLinksList.get(i).getBannerImageLink());
                        RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setButtonDrawable(R.drawable.ic_radio_button_unchecked);
                        radioGroupTvShow.addView(radioButton);
                    } else
                        break;
                }
                bannerViewPagerAdapter.refreshBannerUrls(allBannerImageFullLinks);

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });


        Call<AboutTVShowResponse> call1 = service.getAboutTVShow(tvShow_id, URLConstants.API_KEY, "videos");
        call1.enqueue(new Callback<AboutTVShowResponse>() {
            @Override
            public void onResponse(Call<AboutTVShowResponse> call, Response<AboutTVShowResponse> response) {
                ArrayList<Genre> genres = response.body().getGenres();
                for (int i = 0; i < genres.size(); i++) {
                    if (i < genres.size() - 1)
                        genreTextView.append(genres.get(i).getName() + ", ");
                    else
                        genreTextView.append(genres.get(i).getName());
                }

                AboutTVShowResponse aboutTVShowResponse = new AboutTVShowResponse();

                aboutTVShowResponse.setOverview(response.body().getOverview());
                aboutTVShowResponse.setFirstAirDate(response.body().getFirstAirDate());
                aboutTVShowResponse.setLastAirDate(response.body().getLastAirDate());
                aboutTVShowResponse.setEpisodes(response.body().getEpisodes());
                aboutTVShowResponse.setSeasons(response.body().getSeasons());
                aboutTVShowResponse.setTvShowsCreaters(response.body().getTvShowsCreaters());
                aboutTVShowResponse.setShowType(response.body().getShowType());
                aboutTVShowResponse.setStatus(response.body().getStatus());
                aboutTVShowResponse.setVideo(response.body().getVideo());

                obj = response.body().getVideo();
                obj.setTrailers(obj.getTrailers());

                Bundle bundle = new Bundle();
                bundle.putString("OVERVIEW", aboutTVShowResponse.getOverview());
                bundle.putString("FIRST_AIR_DATE", aboutTVShowResponse.getFirstAirDate());
                bundle.putString("LAST_AIR_DATE", aboutTVShowResponse.getLastAirDate());
                bundle.putInt("EPISODES", aboutTVShowResponse.getEpisodes());
                bundle.putInt("SEASONS", aboutTVShowResponse.getSeasons());
                bundle.putString("SHOW_TYPE", aboutTVShowResponse.getShowType());
                bundle.putString("STATUS", aboutTVShowResponse.getStatus());
                bundle.putSerializable("CREATORS", aboutTVShowResponse.getTvShowsCreaters());
                Log.i("DUBAI", String.valueOf(obj.getTrailers()));
                bundle.putSerializable("TRAILER_THUMBNAILS", obj.getTrailers());

                InfoAboutTVShowFragment obj = (InfoAboutTVShowFragment) tvShowFragmentPager.function(0);
                obj.setUIArguements(bundle);

            }

            @Override
            public void onFailure(Call<AboutTVShowResponse> call, Throwable t) {

            }
        });

        Call<CreditResponse> call2 = service.getTvShowCredits(tvShow_id, URLConstants.API_KEY);

        call2.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(Call<CreditResponse> call, Response<CreditResponse> response) {
                ArrayList<Cast> tvShowCast = response.body().getCast();
                if (tvShowCast == null) {
                    return;
                }
                Bundle args = new Bundle();
                args.putSerializable("TV_SHOW_CAST", tvShowCast);
                CastTVShowFragment obj1 = (CastTVShowFragment) tvShowFragmentPager.function(1);
                obj1.setUIArguements(args);
            }

            @Override
            public void onFailure(Call<CreditResponse> call, Throwable t) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
