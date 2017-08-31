package com.example.weitan.mega.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.kanmanus.kmutt.sit.ijoint.Contextor;
import com.kanmanus.kmutt.sit.ijoint.MyApplication;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.adapter.model.AdapterMapping;
import com.kanmanus.kmutt.sit.ijoint.adapter.model.TaskRecyclerViewItem;
import com.kanmanus.kmutt.sit.ijoint.datamanager.TreatmentDataManager;
import com.kanmanus.kmutt.sit.ijoint.models.PatientProfileViewModel;
import com.kanmanus.kmutt.sit.ijoint.models.Task;
import com.kanmanus.kmutt.sit.ijoint.models.TreatmentModel;
import com.kanmanus.kmutt.sit.ijoint.net.DefaultSubscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;


public class TasksActivity extends BaseActivity implements TaskRecyclerViewItem.Listener {

    @BindView(R.id.layout_loading)
    LinearLayout layoutLoading;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.lv_tasks)
    ListView lvTasks;
    @BindView(R.id.stub)
    ViewStub stub;

    public static Intent callingIntent(Context context, TreatmentModel treatmentModel) {
        Intent intent = new Intent(context, TasksActivity.class);
        intent.putExtra(INTENT_PARAM_TREATMENT, new Gson().toJson(treatmentModel));
        return intent;
    }


    public static final String INTENT_PARAM_TREATMENT = "INTENT_PARAM_TREATMENT";
    private PatientProfileViewModel profileViewModel;

    private ArrayList<Task> allTasks;
    private String treatmentNo;
    private String treatmentStatus;
    private TreatmentDataManager dataManager;
    private List<IFlexible> taskList;
    private FlexibleAdapter<IFlexible> taskAdapter;
    private AdapterMapping adapterMapping;
    private TreatmentModel treatmentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);

        profileViewModel = MyApplication.getInstance().getSession();
        String treatmentJson = getIntent().getStringExtra(INTENT_PARAM_TREATMENT);
        treatmentModel = new Gson().fromJson(treatmentJson, TreatmentModel.class);

        treatmentNo = String.valueOf(treatmentModel.getTreatmentNo());
        treatmentStatus = treatmentModel.getStatus().getCode();
        setupUpButton();
        initInstances();
    }

    private void initInstances() {
        setTitle(treatmentModel.getSubject());
        getSupportActionBar().setSubtitle(treatmentModel.getCreateDateText() + ", Arm Side : " + treatmentModel.getArmSide().getLabel());
        adapterMapping = new AdapterMapping();
        dataManager = new TreatmentDataManager();
        taskList = new ArrayList<>();
        taskAdapter = new FlexibleAdapter<>(taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncData();
    }

    private void syncData() {
        if (!isThereInternetConnection()) {
            noInternet();
        }
        layoutLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        dataManager.syncTask(profileViewModel.getPId(), treatmentNo, treatmentStatus, new SyncTaskSubscriber(), this);
    }

    @Override
    public void onTaskItemClicked(Task task) {
        if (task.is_synced.equals(Task.TASK_READY)) {
            navigator.navigateToCalibration(this, task);
        }
    }

    class SyncTaskSubscriber extends DefaultSubscriber<List<IFlexible>> {
        @Override
        public void onCompleted() {
            layoutLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(Throwable e) {
            layoutLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNext(List<IFlexible> tasks) {
            //allTasks = (ArrayList<Task>) tasks;
            //renderTaskList();
            taskList.clear();
            taskList.addAll(tasks);
            taskAdapter.notifyDataSetChanged();
            if(taskList.size() == 0){
                stub.setVisibility(View.VISIBLE);
            }else{
                stub.setVisibility(View.GONE);
            }
        }
    }

    private void renderTaskList() {
        if (allTasks != null) {
            taskList.clear();
            taskList.addAll(adapterMapping.transformTask(allTasks, this));
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                syncData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isThereInternetConnection() {
        boolean isConnected;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) Contextor.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }


    public void noInternet() {
        showToast("No Internet Connection. Data cannot be synced to the server.");
    }

}
