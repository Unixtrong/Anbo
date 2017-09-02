package com.unixtrong.anbo.compo.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.unixtrong.anbo.R;
import com.unixtrong.anbo.entity.ApiResult;
import com.unixtrong.anbo.entity.Feed;
import com.unixtrong.anbo.handler.AppInfo;
import com.unixtrong.anbo.handler.WeiboApi;
import com.unixtrong.anbo.tools.Lg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private List<Feed> mFeedList = new ArrayList<>();
    private HomeAdapter mAdapter;
    /**
     * 实例化一个执行器，可以在单一线程中执行任务
     */
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lg.debug("");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Lg.debug("");
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        if (AppInfo.hasSignIn(getContext())) {
            Lg.debug("has sign in.");
            if (mFeedList.isEmpty()) {
                updateFeedList();
            }
        }
        return root;
    }

    private void initView(View root) {
        mAdapter = new HomeAdapter(getActivity(), mFeedList);

        RecyclerView feedsRecycler = (RecyclerView) root.findViewById(R.id.rv_main_feeds);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        feedsRecycler.setLayoutManager(layoutManager);
        feedsRecycler.setAdapter(mAdapter);
        // 设置 RecyclerView 的默认分割线
        DividerItemDecoration decor = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
        decor.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_feed_divider));
        feedsRecycler.addItemDecoration(decor);
        // 设置 RecyclerView 的数据变化动画
        feedsRecycler.setItemAnimator(new DefaultItemAnimator());
    }


    public void updateFeedList() {
        // 在子线程中请求 Feed 数据
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String token = AppInfo.getAccessToken(getContext());
                Lg.debug("token: " + token);
                // 调用封装好的微博 API 来获取 Feed 列表，流的读取以及 JSON 的解析已经封装在内
                ApiResult<List<Feed>> apiResult = WeiboApi.timeLine(token, 50);
                boolean loadResult = false;
                // 如果返回结果成功，则更新内存中 mFeedList 的数据
                if (apiResult != null && apiResult.isSuccess()) {
                    List<Feed> feeds = apiResult.getBody();
                    if (feeds != null && !feeds.isEmpty()) {
                        Lg.debug(String.format(Locale.getDefault(), "result, size: %d, data: %s", feeds.size(), apiResult.getJson()));
                        mFeedList.clear();
                        mFeedList.addAll(feeds);
                        // 更新页面的请求时间，用于每条微博的时间展示（如「5 秒前」「20 分钟前」）
                        mAdapter.updateLastRequestTime(new Date());
                        loadResult = true;
                    }
                }
                final boolean isLoadSuccess = loadResult;
                // 返回主界面刷新 UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoadSuccess) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext().getApplicationContext(), getString(R.string.main_request_failure), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
