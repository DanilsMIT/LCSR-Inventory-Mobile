package com.example.lcsr_inventory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.lcsr_inventory.databinding.PopupImageOptionsBinding;

public class PopupImageOptions extends DialogFragment {

    private PopupImageOptionsBinding binding;
    private final OnImageOptionSelected listener;

    public interface OnImageOptionSelected {
        void onOptionSelected(int option); // 0 = Camera, 1 = Gallery
    }

    public PopupImageOptions(OnImageOptionSelected listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupImageOptionsBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.popupImgOptBtnClose.setOnClickListener(v -> dismiss());
        binding.popupImgOptBtnCamera.setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(0);
            dismiss();
        });
        binding.popupImgOptBtnGallery.setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(1);
            dismiss();
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}