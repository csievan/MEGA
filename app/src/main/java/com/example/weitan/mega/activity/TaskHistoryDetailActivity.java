package com.example.weitan.mega.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.google.gson.Gson;
import com.kanmanus.kmutt.sit.ijoint.MyApplication;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.adapter.model.AdapterMapping;
import com.kanmanus.kmutt.sit.ijoint.adapter.model.TaskRecyclerViewItem;
import com.kanmanus.kmutt.sit.ijoint.datamanager.TreatmentDataManager;
import com.kanmanus.kmutt.sit.ijoint.models.Task;
import com.kanmanus.kmutt.sit.ijoint.models.TaskHistoryHeaderModel;
import com.kanmanus.kmutt.sit.ijoint.net.DefaultSubscriber;
import com.kanmanus.kmutt.sit.ijoint.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class TaskHistoryDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, TaskRecyclerViewItem.Listener {

    public static Intent callingIntent(Context context, TaskHistoryHeaderModel item) {
        Intent intent = new Intent(context,TaskHistoryDetailActivity.class);
        intent.putExtra(INTENT_PARAM_TASKHISTORY,new Gson().toJson(item));
        return intent;
    }

    public static final String INTENT_PARAM_TASKHISTORY = "INTENT_PARAM_TASKHISTORY";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.stub)
    ViewStub stub;
    private List<IFlexible> taskFlexibleList;
    private FlexibleAdapter<IFlexible> taskAdapter;
    private TreatmentDataManager dataManager;
    private AdapterMapping adapterMapping;
    private TaskHistoryHeaderModel taskHistoryHeaderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history_detail);
        ButterKnife.bind(this);
        setupUpButton();
        initInstances();
    }

    private void initInstances() {
        String taskHistoryJson = getIntent().getStringExtra(INTENT_PARAM_TASKHISTORY);
        taskHistoryHeaderModel = new Gson().fromJson(taskHistoryJson,TaskHistoryHeaderModel.class);
        adapterMapping = new AdapterMapping();
        dataManager = new TreatmentDataManager();
        taskFlexibleList = new ArrayList<>();
        taskAdapter = new FlexibleAdapter<>(taskFlexibleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        setTitle(taskHistoryHeaderModel.getDateLabel());
        getSupportActionBar().setSubtitle("Amount "+taskHistoryHeaderModel.getAmountTask()+" Task");
        loadTaskHistoryDetail();
    }
    private void loadTaskHistoryDetail(){
        String currDate  = DateTimeUtils.toFormat(taskHistoryHeaderModel.getDate(),DateTimeUtils.yyyy_MM_dd);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            dataManager.getTaskHistoryDetail(MyApplication.getInstance().getSession().getPId(),currDate,new LoadTaskHistoryDetailSubscriber());
        });
    }



    @Override
    public void onRefresh() {
        loadTaskHistoryDetail();
    }

    @Override
    public void onTaskItemClicked(Task task) {

    }


    class LoadTaskHistoryDetailSubscriber extends DefaultSubscriber<List<Task>>{
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
        public void onNext(List<Task> taskList) {
            renderList(taskList);
        }
    }

    private void renderList(List<Task> taskList) {
        taskFlexibleList.clear();
        taskFlexibleList.addAll(adapterMapping.transformSyncedTask(taskList,this));
        if(taskFlexibleList.size() == 0){
            stub.setVisibility(View.VISIBLE);
        }else{
            stub.setVisibility(View.GONE);
        }
        taskAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener onRetryConnectClickListener = v -> loadTaskHistoryDetail();

}
