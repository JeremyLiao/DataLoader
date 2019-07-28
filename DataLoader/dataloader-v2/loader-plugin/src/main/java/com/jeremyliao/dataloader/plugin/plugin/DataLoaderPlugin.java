package com.jeremyliao.dataloader.plugin.plugin;

import com.android.build.gradle.AppExtension;
import com.jeremyliao.dataloader.plugin.transform.DataLoaderTransform;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by liaohailiang on 2019-07-24.
 */
public class DataLoaderPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        addTransform(project);
    }

    private void addTransform(Project project) {
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        appExtension.registerTransform(new DataLoaderTransform(project));
    }
}
