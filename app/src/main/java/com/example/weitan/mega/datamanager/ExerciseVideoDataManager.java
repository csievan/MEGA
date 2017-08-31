package com.example.weitan.mega.datamanager;

import com.kanmanus.kmutt.sit.ijoint.models.ExerciseVideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Semicolon07 on 7/8/2016 AD.
 */

public class ExerciseVideoDataManager extends BaseDataManager {
    public List<ExerciseVideoModel> getAll(){
        List<ExerciseVideoModel> exerciseVideoModels = new ArrayList<>();
        ExerciseVideoModel videoModel1 = new ExerciseVideoModel();
        ExerciseVideoModel videoModel2 = new ExerciseVideoModel();
        ExerciseVideoModel videoModel3 = new ExerciseVideoModel();

        videoModel1.setName("Horizontal Flexion");
        videoModel1.setDescription("Horizontal Flexion Description");
        videoModel1.setVideoFileName("hflexion");
        videoModel1.setYoutubeLinkId("-IRlSpdgvJY");

        videoModel2.setName("Extension");
        videoModel2.setDescription("Extension Description");
        videoModel2.setVideoFileName("extension");
        videoModel2.setYoutubeLinkId("vE8YtA8AobM");

        videoModel3.setName("Flexion");
        videoModel3.setDescription("Flexion Description");
        videoModel3.setVideoFileName("flexion");
        videoModel3.setYoutubeLinkId("tWoeUymdBAw");

        exerciseVideoModels.add(videoModel1);
        exerciseVideoModels.add(videoModel2);
        exerciseVideoModels.add(videoModel3);
        return exerciseVideoModels;
    }
}
