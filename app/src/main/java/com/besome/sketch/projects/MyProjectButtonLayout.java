package com.besome.sketch.projects;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.besome.sketch.lib.base.CollapsibleLayout;
import com.sketchware.remod.R;

import java.util.List;

import mod.hey.studios.util.Helper;

public class MyProjectButtonLayout extends CollapsibleLayout<MyProjectButton> {
    public MyProjectButtonLayout(Context context) {
        this(context, null);
    }

    public MyProjectButtonLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected List<MyProjectButton> initializeButtons(@NonNull Context context) {
        return List.of(
                createButton(context, R.drawable.settings_96, R.string.settings),
                createButton(context, R.drawable.ic_backup, R.string.backup),
                createButton(context, R.drawable.ic_export_grey_48dp, R.string.export),
                createButton(context, R.drawable.ic_delete_grey_48dp, R.string.delete),
                createButton(context, R.drawable.settings_96, R.string.config)
        );
    }

    private MyProjectButton createButton(Context context, @DrawableRes int iconRes,
                                          @StringRes int textRes) {
        return MyProjectButton.create(context, iconRes, textRes);
    }


    @Override
    protected CharSequence getWarningMessage() {
        return Helper.getResString(this, R.string.myprojects_confirm_project_delete);
    }

    @Override
    protected CharSequence getYesLabel() {
        return Helper.getResString(this, R.string.common_word_delete);
    }

    @Override
    protected CharSequence getNegativeLabel() {
        return Helper.getResString(getContext(), R.string.common_word_cancel);
}
