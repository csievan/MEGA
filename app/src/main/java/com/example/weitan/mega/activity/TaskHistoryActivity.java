package com.example.weitan.mega.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.kanmanus.kmutt.sit.ijoint.MyApplication;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.adapter.model.AdapterMapping;
import com.kanmanus.kmutt.sit.ijoint.adapter.model.TaskHistoryRecyclerViewItem;
import com.kanmanus.kmutt.sit.ijoint.datamanager.TreatmentDataManager;
import com.kanmanus.kmutt.sit.ijoint.models.TaskHistoryHeaderModel;
import com.kanmanus.kmutt.sit.ijoint.net.DefaultSubscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class TaskHistoryActivity extends BaseActivity implements TaskHistoryRecyclerViewItem.Listener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.stub)
    ViewStub stub;
    private List<IFlexible> historyFlexibleList;
    private FlexibleAdapter<IFlexible> historyAdapter;
    private TreatmentDataManager dataManager;
    private AdapterMapping adapterMapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        ButterKnife.bind(this);
        setupUpButton();
        initInstances();
    }

    private void initInstances() {
        adapterMapping = new AdapterMapping();
        dataManager = new TreatmentDataManager();
        historyFlexibleList = new ArrayList<>();
        historyAdapter = new FlexibleAdapter<>(historyFlexibleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        loadTaskHistory();
    }
    private void loadTaskHistory(){
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            dataManager.getTaskHistory(MyApplication.getInstance().getSession().getPId(),new LoadTaskHistorySubscriber());
        });
    }

    @Override
    public void onTaskHistoryItemClicked(TaskHistoryHeaderModel item) {
        navigator.navigateToHistoryDetail(this,item);
    }

    @Override
    public void onRefresh() {
        loadTaskHistory();
    }

    class LoadTaskHistorySubscriber extends DefaultSubscriber<List<TaskHistoryHeaderModel>>{
        @Override
        public void onCompleted() {
            swipeRefreshLayout.setRefreshing(false);
            removeRetryConnectView();
            super.onCompleted();

        }

        @Override
        public void onError(Throwable e) {
            showRetryConnect(e, onRetryConnectClickListener);
            swipeRefreshLayout.setRefreshing(false);
            super.onError(e);
        }

        @Override
        public void onNext(List<TaskHistoryHeaderModel> taskHistoryHeaderModels) {
            renderList(taskHistoryHeaderModels);

        }
    }

    private void renderList(List<TaskHistoryHeaderModel> taskHistoryHeaderModels) {
        historyFlexibleList.clear();
        historyFlexibleList.addAll(adapterMapping.transformTaskHistory(taskHistoryHeaderModels,this));
        if(historyFlexibleList.size() == 0){
            stub.setVisibility(View.VISIBLE);
        }else{
            stub.setVisibility(View.GONE);
        }
        historyAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener onRetryConnectClickListener = v -> loadTaskHistory();

}
