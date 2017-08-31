package com.example.weitan.mega.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.fragment.YouTubeFragment;
import com.kanmanus.kmutt.sit.ijoint.models.ExerciseVideoModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseSampleVideoDetailActivity extends BaseActivity {

    @BindView(R.id.name_textView)
    TextView nameTextView;
    @BindView(R.id.desc_textView)
    TextView descTextView;

    private ExerciseVideoModel exerciseVideoModel;

    public static Intent callingIntent(Context context, ExerciseVideoModel item) {
        Intent intent = new Intent(context, ExerciseSampleVideoDetailActivity.class);
        intent.putExtra(INTENT_PARAM_MODEL, new Gson().toJson(item));
        return intent;
    }

    public static final String INTENT_PARAM_MODEL = "INTENT_PARAM_MODEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_sample_video_detail);
        ButterKnife.bind(this);
        setupUpButton();

        String json = getIntent().getStringExtra(INTENT_PARAM_MODEL);
        exerciseVideoModel = new Gson().fromJson(json, ExerciseVideoModel.class);
        setTitle(exerciseVideoModel.getName());
        nameTextView.setText(exerciseVideoModel.getName());
        descTextView.setText(exerciseVideoModel.getDescription());

        final YouTubeFragment fragment = (YouTubeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_youtube);
        fragment.setVideoId(exerciseVideoModel.getYoutubeLinkId());
    }

}
