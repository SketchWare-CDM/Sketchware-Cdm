package com.besome.sketch.projects;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.besome.sketch.lib.base.BaseDialogActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.sketchware.remod.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import a.a.a.GB;
import a.a.a.HB;
import a.a.a.LB;
import a.a.a.MA;
import a.a.a.UB;
import a.a.a.VB;
import a.a.a.Zx;
import a.a.a.aB;
import a.a.a.gB;
import a.a.a.iB;
import a.a.a.lC;
import a.a.a.mB;
import a.a.a.nB;
import a.a.a.oB;
import a.a.a.wB;
import a.a.a.wq;
import a.a.a.yB;
import mod.SketchwareUtil;
import mod.hasrat.control.VersionDialog;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.activities.tools.ConfigActivity;

public class MyProjectSettingActivity extends BaseDialogActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PICK_CROPPED_ICON = 216;
    private static final int REQUEST_CODE_PICK_ICON = 207;
    private final String[] themeColorKeys = {"color_accent", "color_primary", "color_primary_dark", "color_control_highlight", "color_control_normal"};
    private final String[] themeColorLabels = {"colorAccent", "colorPrimary", "colorPrimaryDark", "colorControlHighlight", "colorControlNormal"};
    private final int[] projectThemeColors = new int[themeColorKeys.length];
    public TextView projectVersionCodeView;
    public TextView projectVersionNameView;
    private EditText projectAppName;
    private EditText projectPackageName;
    private EditText projectName;
    private LinearLayout themeColorsContainer;
    private ImageView colorGuide;
    private LinearLayout advancedSettingsContainer;
    private ImageView appIcon;
    private UB projectPackageNameValidator;
    private VB projectNameValidator;
    private LB projectAppNameValidator;
    private boolean projectHasCustomIcon = false;
    private boolean updatingExistingProject = false;
    private int projectVersionCode = 1;
    private int projectVersionNameFirstPart;
    private int projectVersionNameSecondPart;
    private boolean shownPackageNameChangeWarning;
    private String sc_id;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            finish();
            return;
        }
        Uri uri = data.getData();
        if (requestCode == REQUEST_CODE_PICK_ICON && resultCode == RESULT_OK) {
            String filename = HB.a(this, uri);
            Bitmap bitmap = iB.a(filename, 96, 96);
            try {
                int orientation = new ExifInterface(filename).getAttributeInt("Orientation", -1);
                Bitmap oriented = iB.a(bitmap, orientation == 3 ? 180 : orientation == 6 ? 90 : orientation == 8 ? 270 : 0);
                appIcon.setImageBitmap(oriented);
                saveBitmapTo(oriented, getCustomIconPath());
                projectHasCustomIcon = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bundle extras = data.getExtras();
            if (requestCode == REQUEST_CODE_PICK_CROPPED_ICON && resultCode == RESULT_OK && extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                appIcon.setImageBitmap(bitmap);
                saveBitmapTo(bitmap, getCustomIconPath());
                projectHasCustomIcon = true;
            }
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.advanced_setting) {
            toggleAdvancedSettings();
        } else if (id == R.id.app_icon_layout) {
            showCustomIconOptions();
        } else if (id == R.id.common_dialog_cancel_button) {
            finish();
        } else if (id == R.id.common_dialog_ok_button) {
            if (isInputValid()) {
                new SaveProjectAsyncTask(getApplicationContext()).execute();
            }
        } else if (id == R.id.img_theme_color_help) {
            toggleColorGuide();
        } else if (id == R.id.ver_code || id == R.id.ver_name) {
            showVersionDialog();
        }
    }

    private void toggleAdvancedSettings() {
        if (advancedSettingsLayout.getVisibility() == View.GONE) {
            advancedSettingsLayout.setVisibility(View.VISIBLE);
        } else {
            advancedSettingsLayout.setVisibility(View.GONE);
        }
    }

    private void showCustomIconOptions() {
        startActivityForResult(new Intent(this, PickIconActivity.class), REQUEST_CODE_PICK_ICON);
    }

    private void toggleColorGuide() {
        if (colorGuide.getVisibility() == View.VISIBLE) {
            colorGuide.setVisibility(View.GONE);
        } else {
            colorGuide.setVisibility(View.VISIBLE);
        }
    }

    private void showVersionDialog() {
        if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_USE_NEW_VERSION_CONTROL)) {
            new VersionDialog(this).show();
        } else {
            showOldVersionControlDialog();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myproject_setting);
        if (!j()) {
            finish();
        }
        String projectId = getIntent().getStringExtra("sc_id");
        boolean isExistingProject = getIntent().getBooleanExtra("is_update", false);
        boolean expandAdvancedOptions = getIntent().getBooleanExtra("advanced_open", false);

        ((TextView) findViewById(R.id.tv_change_icon)).setText(Helper.getResString(R.string.myprojects_settings_description_change_icon));
        findViewById(R.id.contents).setOnClickListener(this);
        findViewById(R.id.app_icon_layout).setOnClickListener(this);
        findViewById(R.id.advanced_setting).setOnClickListener(this);
        versionCodeView = findViewById(R.id.ver_code);
        versionNameView = findViewById(R.id.ver_name);
        TextInputLayout appName = findViewById(R.id.ti_app_name);
        TextInputLayout packageName = findViewById(R.id.ti_package_name);
        TextInputLayout projectName = findViewById(R.id.ti_project_name);
        appName.setHint(Helper.getResString(R.string.myprojects_settings_hint_enter_application_name));
        packageName.setHint(Helper.getResString(R.string.myprojects_settings_hint_enter_package_name));
        projectName.setHint(Helper.getResString(R.string.myprojects_settings_hint_enter_project_name));
        appNameEditText = findViewById(R.id.et_app_name);
        packageNameEditText = findViewById(R.id.et_package_name);
        projectNameEditText = findViewById(R.id.et_project_name);
        ((TextView) findViewById(R.id.tv_advanced_settings)).setText(Helper.getResString(R.string.myprojects_settings_title_advanced_settings));
        appIcon = findViewById(R.id.app_icon);

        appNameValidator = new LB(getApplicationContext(), appName);
        packageNameValidator = new UB(getApplicationContext(), packageName);
        projectNameValidator = new VB(getApplicationContext(), projectName);
        packageNameEditText.setPrivateImeOptions("defaultInputmode=english;");
        projectNameEditText.setPrivateImeOptions("defaultInputmode=english;");
        packageNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !shownPackageNameChangeWarning) {
                showPackageNameChangeWarning();
            }
        });
        themeColorsContainer = findViewById(R.id.layout_theme_colors);
        findViewById(R.id.img_theme_color_help).setOnClickListener(this);
        colorGuide = findViewById(R.id.img_color_guide);
        advancedSettingsContainer = findViewById(R.id.advanced_setting_layout);
        /* Save & Cancel buttons */
        findViewById(R.id.common_dialog_save_button).setOnClickListener(this);
        findViewById(R.id.common_dialog_cancel_button).setOnClickListener(this);

        for (int i = 0; i < themeColorKeys.length; i++) {
            ThemeColorView colorView = new ThemeColorView(getApplicationContext(), i);
            colorView.name.setText(themeColorLabels[i]);
            colorView.color.setBackgroundColor(Color.WHITE);
            themeColorsContainer.addView(colorView);
            colorView.setOnClickListener(v -> {
                if (!mB.a()) {
                    pickColor((Integer) v.getTag());
                }
            });
        }
        /* Set the cancel button's label */
        findViewById(R.id.common_dialog_cancel_button).setTag(Helper.getResString(R.string.common_word_cancel));

        if (isExistingProject) {
            /* Set the dialog's title & save button label */
            setTitle(Helper.getResString(R.string.myprojects_settings_actionbar_title_project_settings));
            findViewById(R.id.common_dialog_save_button).setTag(Helper.getResString(R.string.myprojects_settings_button_save));

            HashMap<String, Object> metadata = lC.b(projectId);
            packageNameEditText.setText(yB.c(metadata, "my_sc_pkg_name"));
            projectNameEditText.setText(yB.c(metadata, "my_ws_name"));
            appNameEditText.setText(yB.c(metadata, "my_app_name"));
            versionCode = parseInt(yB.c(metadata, "sc_ver_code"), 1);
            parseVersion(yB.c(metadata, "sc_ver_name"));
            versionCodeView.setText(yB.c(metadata, "sc_ver_code"));
            versionNameView.setText(yB.c(metadata, "sc_ver_name"));
            hasCustomIcon = yB.a(metadata, "custom_icon");
            if (hasCustomIcon) {
                if (Build.VERSION.SDK_INT >= 24) {
                    appIcon.setImageURI(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", getCustomIcon()));
                } else {
                    appIcon.setImageURI(Uri.fromFile(getCustomIcon()));
                }
            }

            for (int i = 0; i < themeColorKeys.length; i++) {
                themeColors[i] = yB.a(metadata, themeColorKeys[i], themeColors[i]);
            }
        } else {
            /* Set the dialog's title & create button label */
            setTitle(Helper.getResString(R.string.myprojects_settings_actionbar_title_new_projet));
            findViewById(R.id.common_dialog_save_button).setTag(Helper.getResString(R.string.myprojects_settings_button_create_app));

            String newProjectName = getIntent().getStringExtra("my_ws_name");
            String newProjectPackageName = getIntent().getStringExtra("my_sc_pkg_name");
            if (projectId == null || projectId.equals("")) {
                projectId = lC.b();
                newProjectName = lC.c();
                newProjectPackageName = "com.my." + newProjectName.toLowerCase();
            }
            packageNameEditText.setText(newProjectPackageName);
            projectNameEditText.setText(newProjectName);
            appNameEditText.setText(getIntent().getStringExtra("my_app_name"));

            String newProjectVersionCode = getIntent().getStringExtra("sc_ver_code");
            String newProjectVersionName = getIntent().getStringExtra("sc_ver_name");
            if (newProjectVersionCode == null || newProjectVersionCode.isEmpty()) {
                newProjectVersionCode = "1";
            }
            if (newProjectVersionName == null || newProjectVersionName.isEmpty()) {
                newProjectVersionName = "1.0";
            }
            versionCode = parseInt(newProjectVersionCode, 1);
            parseVersion(newProjectVersionName);
            versionCodeView.setText(newProjectVersionCode);
            versionNameView.setText(newProjectVersionName);
            hasCustomIcon = getIntent().getBooleanExtra("custom_icon", false);
            if (hasCustomIcon) {
                if (Build.VERSION.SDK_INT >= 24) {
                    appIcon.setImageURI(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", getCustomIcon()));
                } else {
                    appIcon.setImageURI(Uri.fromFile(getCustomIcon()));
                }
            }
        }
        syncThemeColors();

        if (expandAdvancedOptions) {
            advancedSettingsContainer.setVisibility(View.VISIBLE);
            packageNameEditText.requestFocus();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!canGoBack()) {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        File projectDir = new File(wq.e(), sc_id);
        File imagesDir = new File(wq.g(), sc_id);
        File dataDir = new File(wq.t(), sc_id);
        File backupDir = new File(wq.d(), sc_id);
        File iconFile = getCustomIcon();
        if (!iconFile.exists()) {
            try {
                iconFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        oB projectDirs = new oB();
        projectDirs.add(projectDir);
        projectDirs.add(imagesDir);
        projectDirs.add(dataDir);
        projectDirs.add(backupDir);
    }

    private void showVersionControlDialog() {
        aB dialog = new aB(this);
        dialog.a(R.drawable.numbers_48);
        dialog.b(getString(R.string.myprojects_settings_version_control_title));
        View view = wB.a(getApplicationContext(), R.layout.property_popup_version_control);
        ((TextView) view.findViewById(R.id.tv_code)).setText(getString(R.string.myprojects_settings_version_control_title_code));
        ((TextView) view.findViewById(R.id.tv_name)).setText(getString(R.string.myprojects_settings_version_control_title_name));

        NumberPicker codePicker = view.findViewById(R.id.version_code);
        NumberPicker firstPartPicker = view.findViewById(R.id.version_name1);
        NumberPicker secondPartPicker = view.findViewById(R.id.version_name2);

        codePicker.setWrapSelectorWheel(false);
        firstPartPicker.setWrapSelectorWheel(false);
        secondPartPicker.setWrapSelectorWheel(false);

        int currentCode = Integer.parseInt(projectVersionCodeView.getText().toString());
        int minCode = Math.max(currentCode - 5, 1);
        int maxCode = currentCode + 5;
        codePicker.setMinValue(minCode);
        codePicker.setMaxValue(maxCode);
        codePicker.setValue(currentCode);

        String[] split = projectVersionNameView.getText().toString().split("\\.");
        int currentFirstPart = parseInt(split[0], 1);
        int currentSecondPart = parseInt(split[1], 0);
        int minFirstPart = Math.max(currentFirstPart - 5, 1);
        int maxFirstPart = currentFirstPart + 5;
        firstPartPicker.setMinValue(minFirstPart);
        firstPartPicker.setMaxValue(maxFirstPart);
        firstPartPicker.setValue(currentFirstPart);

        secondPartPicker.setMinValue(Math.max(currentSecondPart - 20, 0));
        secondPartPicker.setMaxValue(currentSecondPart + 20);
        secondPartPicker.setValue(currentSecondPart);
        dialog.a(view);

        codePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (newVal < currentCode) {
                picker.setValue(currentCode);
            }
        });
        firstPartPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (newVal < currentFirstPart) {
                if (newVal < minFirstPart) {
                    picker.setValue(minFirstPart);
                }
                if (newVal == minFirstPart || newVal > currentSecondPart) {
                    secondPartPicker.setValue(currentSecondPart);
                }
            }
        });
        secondPartPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (newVal < currentSecondPart && newVal < minFirstPart) {
                picker.setValue(currentSecondPart);
            }
        });
        dialog.b(getString(R.string.common_word_save), v -> {
            if (!mB.a()) {
                projectVersionCodeView.setText(String.valueOf(codePicker.getValue()));
                projectVersionNameView.setText(firstPartPicker.getValue() + "." + secondPartPicker.getValue());
                dialog.dismiss();
            }
        });
        dialog.a(getString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    private void toggleAdvancedSettings() {
        final int duration = 300;
        final View container = advancedSettingsContainer;
        if (container.getVisibility() != View.VISIBLE) {
            container.setVisibility(View.VISIBLE);
            gB.b(container, duration, null);
            return;
        }
        gB.a(container, duration, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.setVisibility(View.GONE);
            }
        });
    }


    private void syncThemeColors() {
        for (int i = 0; i < themeColorKeys.length; i++) {
            ThemeColorView colorView = (ThemeColorView) themeColorsContainer.getChildAt(i);
            colorView.setBackgroundColor(projectThemeColors[i]);
        }
    }


    private void parseVersion(String version) {
        String[] versionParts = version.split("\\.");
        projectVersionFirst = Integer.parseInt(versionParts[0]);
        projectVersionSecond = Integer.parseInt(versionParts[1]);
    }

    private void chooseColor(int index) {
        View popup = wB.a(this, R.layout.color_picker);
        Zx colorPicker = new Zx(popup, this, projectThemeColors[index], false, false);
        colorPicker.a(chosenColor -> {
            projectThemeColors[index] = chosenColor;
            syncThemeColors();
        });
        colorPicker.setAnimationStyle(R.anim.abc_fade_in);
        colorPicker.showAtLocation(popup, Gravity.CENTER, 0, 0);
    }


    private void showResetIconConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.common_word_settings)
                .setMessage(R.string.myprojects_settings_confirm_reset_icon)
                .setPositiveButton(R.string.common_word_reset, (dialog, which) -> {
                    appIcon.setImageResource(R.drawable.default_icon);
                    projectHasCustomIcon = false;
                })
                .setNegativeButton(R.string.common_word_cancel, null)
                .create().show();
    }


    private File getCustomIcon() {
        return new File(getCustomIconPath());
    }

    private String getCustomIconPath() {
        return wq.e() + File.separator + sc_id + File.separator + "icon.png";
    }

    private boolean isInputValid() {
        return projectPackageNameValidator.b() && projectNameValidator.b() && projectAppNameValidator.b();
    }

    private void pickIcon() {
        Uri iconUri;
        if (Build.VERSION.SDK_INT >= 24) {
            iconUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", getCustomIcon());
        } else {
            iconUri = Uri.fromFile(getCustomIcon());
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 921600); // 1024KB = 1024 x 1024 = 1048576
        startActivityForResult(Intent.createChooser(intent, getString(R.string.common_word_choose)),
                REQUEST_CODE_PICK_ICON);
    }


    private void pickAndCropCustomIcon() {
        Uri uri = getCustomIconUri();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 96);
        intent.putExtra("outputY", 96);
        intent.putExtra("scale", true);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("return-data", true);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.common_word_choose)), REQUEST_CODE_PICK_CROPPED_ICON);
    }

    private Uri getCustomIconUri() {
        File customIconFile = getCustomIcon();
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", customIconFile);
    }

    private void showCustomIconOptionsDialog() {
        String[] options = {
                getString(R.string.myprojects_settings_context_menu_title_choose_gallery),
                getString(R.string.myprojects_settings_context_menu_title_choose_gallery_with_crop),
                getString(R.string.myprojects_settings_context_menu_title_choose_gallery_default)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.myprojects_settings_context_menu_title_choose)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickCustomIcon();
                            break;
                        case 1:
                            pickAndCropCustomIcon();
                            break;
                        case 2:
                            if (projectHasCustomIcon) showResetIconConfirmation();
                            break;
                    }
                })
                .create()
                .show();
    }


    private void showPackageNameChangeWarningDialog() {
        shownPackageNameChangeWarning = true;
        new AlertDialog.Builder(this)
                .setTitle(R.string.common_word_warning)
                .setIcon(R.drawable.break_warning_96_red)
                .setMessage(R.string.myprojects_settings_message_package_rename)
                .setPositiveButton(R.string.common_word_ok, null)
                .create().show();
    }


    private int parseIntOrDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private void saveBitmapTo(Bitmap bitmap, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            // Ignore
        }
    }

    private static class ThemeColorView extends LinearLayout {

        private TextView color;
        private TextView name;

        public ThemeColorView(Context context, int tag) {
            super(context);
            initialize(context, tag);
        }

        private void initialize(Context context, int tag) {
            setTag(tag);
            wB.a(context, this, R.layout.myproject_color);
            color = findViewById(R.id.color);
            name = findViewById(R.id.name);
        }
    }

    private class SaveProjectAsyncTask extends MA {

        public SaveProjectAsyncTask(Context context) {
            super(context);
            addTask(this);
            k();
        }

        @Override
        public void onPostExecute(Void result) {
            Intent data = new Intent();
            data.putExtra("sc_id", scId);
            data.putExtra("isNew", !updatingExistingProject);
            data.putExtra("index", getIntent().getIntExtra("index", -1));
            setResult(RESULT_OK, data);
            finish();
        }

        @Override
        public void c() {
            HashMap<String, Object> values = new HashMap<>();
            values.put("package_name", projectPackageName.getText().toString());
            values.put("workspace_name", projectName.getText().toString());
            values.put("app_name", projectAppName.getText().toString());
            values.put("custom_icon", projectHasCustomIcon);
            values.put("version_code", projectVersionCodeView.getText().toString());
            values.put("version_name", projectVersionNameView.getText().toString());
            values.put("sketchware_ver", GB.d(getApplicationContext()));
            for (int i = 0; i < themeColorKeys.length; i++) {
                values.put(themeColorKeys[i], projectThemeColors[i]);
            }
            if (updatingExistingProject) {
                lC.b(sc_id, values);
            } else {
                values.put("reg_date", new nB().a("yyyyMMddHHmmss"));
                lC.a(sc_id, values);
                wq.a(getApplicationContext(), sc_id);
                new oB().b(wq.b(sc_id));
            }
        }

        @Override
        public void onCancelled(String error) {
            finishTask();
        }

    }
}
