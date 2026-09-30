package com.example.lcsr_inventory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.lcsr_inventory.databinding.PopupImageViewerBinding;
import java.io.File;

public class PopupImageViewer extends DialogFragment {

    private PopupImageViewerBinding binding;
    private final String imagePath;
    private final int defaultImageRes;

    public PopupImageViewer(String imagePath, int defaultImageRes) {
        this.imagePath = imagePath;
        this.defaultImageRes = defaultImageRes;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupImageViewerBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Cerrar tocando la "X"
        binding.popupViewerBtnClose.setOnClickListener(v -> dismiss());
        
        // El DialogFragment por defecto ya permite cerrar tocando fuera si le damos la instrucción al Dialog
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }
        
        // HACK: Como el FrameLayout abarca todo el tamaño posible por culpa de la imagen, 
        // vamos a añadir un "click listener" manual a todo el RelativeLayout (que es el fondo transparente)
        binding.popupViewerRoot.setOnClickListener(v -> dismiss());
        
        // Evitamos que al tocar la foto se cierre
        binding.popupViewerImg.setOnClickListener(v -> {});
        
        if (imagePath != null && !imagePath.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(imagePath)
                    .placeholder(defaultImageRes)
                    .error(defaultImageRes)
                    .into(binding.popupViewerImg);
        } else {
            binding.popupViewerImg.setImageResource(defaultImageRes);
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}