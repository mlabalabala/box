package com.github.tvbox.osc.bbox.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.github.tvbox.osc.bbox.R;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

public class DescDialog extends BaseDialog {

    public DescDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_desc);
    }

    public void setDescribe(String describe) {
        TextView tvDescribe = findViewById(R.id.describe);
        tvDescribe.setText(describe);
        tvDescribe.requestFocus();
        tvDescribe.requestFocusFromTouch();
    }

    private void init(Context context) {
        EventBus.getDefault().register(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventBus.getDefault().unregister(this);
            }
        });
    }
}