package com.besome.sketch.projects;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.sketchware.remod.R;

import a.a.a.wB;

public class MyProjectButton extends LinearLayout {
    private int id;
    private ImageView icon;
    private TextView name;

    public MyProjectButton(Context context) {
        this(context, null);
    }

    public MyProjectButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    private void setup(Context context) {
        LayoutParams params = new LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f);
        setLayoutParams(params);

        wB.a(context, this, R.layout.myproject_button);
        icon = findViewById(R.id.icon_button);
        name = findViewById(R.id.name_button);
    }

    public int getId() {
        return id;
    }


    public static MyProjectButton newInstance(Context context, int buttonId,
                                              @DrawableRes int iconResId, String buttonLabel) {
        MyProjectButton button = new MyProjectButton(context);
        button.id = buttonId;
        button.icon.setImageResource(iconResId);
        button.name.setText(buttonLabel);
        return button;
    }

}
