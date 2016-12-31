package com.lhr.jiandou.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lhr.jiandou.R;
import com.lhr.jiandou.adapter.ActorAdapter;
import com.lhr.jiandou.adapter.LikeMovieAdapter;
import com.lhr.jiandou.adapter.base.BaseRecyclerAdapter;
import com.lhr.jiandou.model.bean.MovieDetailsBean;
import com.lhr.jiandou.model.httputils.MovieHttpMethods;
import com.lhr.jiandou.utils.ImageUtils;
import com.lhr.jiandou.utils.SnackBarUtils;
import com.lhr.jiandou.utils.UIUtils;
import com.lhr.jiandou.utils.jsoupUtils.GetLikeMovie;

import java.util.List;

import rx.Subscriber;

/**
 * Created by ChinaLHR on 2016/12/17.
 * Email:13435500980@163.com
 * 电影详情页
 */

public class MovieDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private android.widget.ImageView activitymdiv;
    private android.support.v7.widget.Toolbar activitymdtoolbar;
    private android.support.design.widget.CollapsingToolbarLayout activitymdcolltl;
    private android.support.design.widget.AppBarLayout activitymdappbar;
    private android.widget.TextView activitymdsummarytitle;
    private android.widget.TextView activitymdsummary;
    private android.widget.TextView activitymdsummartmore;
    private android.widget.TextView activitymdactortitle;
    private android.support.v7.widget.RecyclerView activitymdrvactor;
    private android.widget.LinearLayout activitymdll;
    private android.support.v4.widget.NestedScrollView activitymovienested;
    private android.support.design.widget.FloatingActionButton activitymdfab;
    private android.support.design.widget.CoordinatorLayout activitymdcoorl;
    private android.support.v4.widget.SwipeRefreshLayout activitymdrefresh;
    private TextView activity_md_subject_title;
    private TextView activity_md_subject_aka;
    private TextView activity_md_subject_countries;
    private TextView activity_md_subject_genres;
    private TextView activity_md_subject_year;
    private RatingBar activity_md_ratingbar;
    private TextView activity_md_ratings_count;
    private TextView activity_md_ratingnumber;
    private TextView activity_md_recommend_movie;
    private RecyclerView activity_md_rv_movie;

    private MovieDetailsBean mSubject;
    private Subscriber<MovieDetailsBean> mSubscriber;
    private static final String KEY_MOVIE_ID = "movie_id";
    private static final String KEY_IMAGE_URL = "image_url";

    private List<String> movieTitle;
    private List<String> movieId;
    private List<String> movieimg;
    private String MovieId;
    private String imageUrl;
    private ActorAdapter mAdapter;
    private LikeMovieAdapter mLikeAdapter;
    private AsyncTask masyn;
    private boolean isCollection = false;
    boolean isOpenSummary = false;

    public static void toActivity(Activity activity, String id, String imageUrl) {
        Intent intent = new Intent(activity, MovieDetailsActivity.class);
        intent.putExtra(KEY_MOVIE_ID, id);
        intent.putExtra(KEY_IMAGE_URL, imageUrl);
        activity.startActivity(intent);
    }

    private void init() {
        this.activitymdrefresh = (SwipeRefreshLayout) findViewById(R.id.activity_md_refresh);
        this.activitymdcoorl = (CoordinatorLayout) findViewById(R.id.activity_md_coorl);
        this.activitymdfab = (FloatingActionButton) findViewById(R.id.activity_md_fab);
        this.activitymovienested = (NestedScrollView) findViewById(R.id.activity_movie_nested);
        this.activitymdll = (LinearLayout) findViewById(R.id.activity_md_ll);
        this.activitymdrvactor = (RecyclerView) findViewById(R.id.activity_md_rv_actor);
        this.activitymdactortitle = (TextView) findViewById(R.id.activity_md_actor_title);
        this.activitymdsummartmore = (TextView) findViewById(R.id.activity_md_summart_more);
        this.activitymdsummary = (TextView) findViewById(R.id.activity_md_summary);
        this.activitymdsummarytitle = (TextView) findViewById(R.id.activity_md_summary_title);
        this.activitymdappbar = (AppBarLayout) findViewById(R.id.activity_md_appbar);
        this.activitymdcolltl = (CollapsingToolbarLayout) findViewById(R.id.activity_md_colltl);
        this.activitymdtoolbar = (Toolbar) findViewById(R.id.activity_md_toolbar);
        this.activitymdiv = (ImageView) findViewById(R.id.activity_md_iv);
        View activity_md_include = findViewById(R.id.activity_md_include);
        activity_md_subject_title = (TextView) activity_md_include.findViewById(R.id.activity_md_subject_title);
        activity_md_subject_aka = (TextView) activity_md_include.findViewById(R.id.activity_md_subject_aka);
        activity_md_subject_countries = (TextView) activity_md_include.findViewById(R.id.activity_md_subject_countries);
        activity_md_subject_genres = (TextView) activity_md_include.findViewById(R.id.activity_md_subject_genres);
        activity_md_subject_year = (TextView) activity_md_include.findViewById(R.id.activity_md_subject_year);
        activity_md_ratingbar = (RatingBar) activity_md_include.findViewById(R.id.activity_md_ratingbar);
        activity_md_ratings_count = (TextView) activity_md_include.findViewById(R.id.activity_md_ratings_count);
        activity_md_ratingnumber = (TextView) activity_md_include.findViewById(R.id.activity_md_ratingnumber);
        activity_md_recommend_movie = (TextView) findViewById(R.id.activity_md_recommend_movie);
        activity_md_rv_movie = (RecyclerView) findViewById(R.id.activity_md_rv_movie);

        activitymdrefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        activitymdrefresh.setProgressViewOffset(false, 0, 48);
        activitymdtoolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        activitymdtoolbar.inflateMenu(R.menu.menu_moviedetails_toolbar);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);
        MovieId = getIntent().getStringExtra(KEY_MOVIE_ID);
        imageUrl = getIntent().getStringExtra(KEY_IMAGE_URL);
        init();
        initView();
        initData();
        initListener();
    }

    /**
     * 初始化View
     */
    private void initView() {
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        int color = ImageUtils.getColor(resource);
                        activitymdcolltl.setContentScrimColor(color);
                        activitymdcolltl.setBackgroundColor(color);

                        return false;
                    }
                })
                .into(activitymdiv);


    }

    private void initData() {
        activitymdrefresh.setRefreshing(true);
        mSubscriber = new Subscriber<MovieDetailsBean>() {
            @Override
            public void onCompleted() {
                activitymdrefresh.setRefreshing(false);

            }

            @Override
            public void onError(Throwable e) {
                activitymdrefresh.setRefreshing(false);
                SnackBarUtils.showSnackBar(activitymdcoorl,UIUtils.getString(MovieDetailsActivity.this,R.string.error));
            }

            @Override
            public void onNext(MovieDetailsBean movieDetailsBean) {
                if (movieDetailsBean != null) {
                    mSubject = movieDetailsBean;
                    updateView();
                } else {
                    SnackBarUtils.showSnackBar(activitymdcoorl,UIUtils.getString(MovieDetailsActivity.this,R.string.error));
                }
            }
        };

        MovieHttpMethods.getInstance().getMovieById(mSubscriber, MovieId);
    }

    /**
     * 获取到数据，加载View
     */
    private void updateView() {
        activitymdll.setVisibility(View.VISIBLE);

        if (mSubject.getRating() != null) {
            float rate = (float) (mSubject.getRating().getAverage() / 2);
            activity_md_ratingbar.setRating(rate);
            activity_md_ratingnumber.setText(rate * 2 + "");
            activity_md_ratings_count.setText(mSubject.getRatings_count() + "人评价");
        }

        activity_md_subject_title.setText(mSubject.getTitle());
        if (mSubject.getGenres() != null) {
            activity_md_subject_genres.setText("");
            List<String> genres = mSubject.getGenres();
            activity_md_subject_genres.append(UIUtils.getString(this, R.string.md_movie_type));
            addViewString(genres, activity_md_subject_genres);
        }
        if (mSubject.getCountries() != null) {
            activity_md_subject_countries.setText("");
            List<String> countries = mSubject.getCountries();
            activity_md_subject_countries.append(UIUtils.getString(this, R.string.md_movie_country));
            addViewString(countries, activity_md_subject_countries);

        }
        activity_md_subject_year.setText(UIUtils.getString(this, R.string.md_movie_year) + mSubject.getYear());
        if (mSubject.getAka() != null) {
            activity_md_subject_aka.setText("");
            List<String> aka = mSubject.getAka();
            activity_md_subject_aka.append(UIUtils.getString(this, R.string.md_movie_original));
            addViewString(aka, activity_md_subject_aka);
        }
        if (mSubject.getSummary() != null) {
            activitymdsummarytitle.setText(UIUtils.getString(this, R.string.md_movie_brief));
            activitymdsummary.setText(mSubject.getSummary());
            activitymdsummartmore.setText(UIUtils.getString(this, R.string.md_more));
        }

        activitymdactortitle.setText(UIUtils.getString(this, R.string.md_movie_actor));

        activitymdrvactor.setVisibility(View.VISIBLE);
        activitymdrvactor.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new ActorAdapter(this, mSubject);
        activitymdrvactor.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String url) {
                ActorDetailsActivity.toActivity(MovieDetailsActivity.this,id,url);
            }
        });
        activity_md_recommend_movie.setText(UIUtils.getString(MovieDetailsActivity.this,R.string.md_load_ing));
        masyn = new MAsyncTask().execute();
    }

    private void addViewString(List<String> list, TextView view) {
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                view.append(list.get(i));
            } else {
                view.append(list.get(i) + "/");
            }
        }
    }

    private void initListener() {

        activitymdappbar.addOnOffsetChangedListener(this);

        activitymdtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activitymdtoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (isCollection) {
                    item.setIcon(R.drawable.collection_false);
                    isCollection = false;

                } else {
                    item.setIcon(R.drawable.collection_true);
                    isCollection = true;
                }

                return true;
            }
        });

        activitymdsummartmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpenSummary) {
                    activitymdsummary.setLines(5);
                    activitymdsummary.setEllipsize(TextUtils.TruncateAt.END);
                    activitymdsummartmore.setText(UIUtils.getString(MovieDetailsActivity.this, R.string.md_more));
                    isOpenSummary = false;
                } else {
                    activitymdsummary.setSingleLine(false);
                    activitymdsummary.setEllipsize(null);
                    activitymdsummartmore.setText(UIUtils.getString(MovieDetailsActivity.this, R.string.md_put));
                    isOpenSummary = true;
                }

            }
        });

        activitymdrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activitymdrefresh.setRefreshing(true);
                initView();
                initData();
            }
        });

        activitymdfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity webactivity = new WebViewActivity();
                webactivity.toWebActivity(MovieDetailsActivity.this,mSubject.getMobile_url(),mSubject.getTitle());
            }
        });

    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //当Appbar完全显示时才启用SwipeRefreshLayout
        activitymdrefresh.setEnabled(verticalOffset == 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriber.unsubscribe();
    }

    @Override
    protected void onPause() {
        //判断是否有正在运行的AsyncTask
        if (masyn != null && masyn.getStatus() == AsyncTask.Status.RUNNING) {
            //cancel方法只是将对应的AsynTask标记为cancel状态，并不是真正的取消
            masyn.cancel(true);
        }
        super.onPause();
    }

    class MAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (isCancelled()) {
                return null;
            } else {
                GetLikeMovie movie = new GetLikeMovie(mSubject.getId());
                movieId = movie.getmovieId();
                movieTitle = movie.getMovieTitle();
                movieimg = movie.getmovieimg();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (movieId != null && movieTitle != null && movieimg != null) {
                activity_md_recommend_movie.setText(UIUtils.getString(MovieDetailsActivity.this,R.string.md_load_likemovie));
                activity_md_rv_movie.setVisibility(View.VISIBLE);
                activity_md_rv_movie.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                        LinearLayoutManager.HORIZONTAL, false));
                mLikeAdapter = new LikeMovieAdapter(MovieDetailsActivity.this, movieTitle, movieimg, movieId);
                activity_md_rv_movie.setAdapter(mLikeAdapter);

                mLikeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String id, String url) {
                        if (id != null && url != null) {
                            MovieDetailsActivity.toActivity(MovieDetailsActivity.this, id, url);
                        } else {
                            SnackBarUtils.showSnackBar(activitymdcoorl,UIUtils.getString(MovieDetailsActivity.this,R.string.error));
                        }
                    }
                });

            } else {
                activity_md_recommend_movie.setText(UIUtils.getString(MovieDetailsActivity.this,R.string.md_load_error));
            }
        }
    }
}
