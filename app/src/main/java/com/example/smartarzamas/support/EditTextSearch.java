package com.example.smartarzamas.support;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditTextSearch extends androidx.appcompat.widget.AppCompatEditText {
    private boolean isClosed;

    public EditTextSearch(@NonNull Context context) {
        super(context);
    }
    public EditTextSearch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public EditTextSearch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addTextEditListener(OnTextChangeListener onTextChangeListener) {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isClosed)
                    onTextChangeListener.onChange(editable);
            }
        });
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        closeTextWatcher();
        super.getText().clear();
        super.append(text);
        openTextWatcher();
    }

    public void closeTextWatcher() {
        isClosed = true;
    }
    public void openTextWatcher() {
        isClosed = false;
    }
}
