package com.example.smartarzamas.support;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

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

    public void setHardText(CharSequence text) {
        closeTextWatcher();
        setText(text);
        setSelection(getText().length());
        openTextWatcher();
    }

    public void closeTextWatcher() {
        isClosed = true;
    }
    public void openTextWatcher() {
        isClosed = false;
    }

    public interface OnTextChangeListener {
        void onChange(Editable editable);
    }
}
