package com.example.socialapp.readapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialapp.R;
import com.example.socialapp.models.Bleep;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BleepsAdapter extends RecyclerView.Adapter<BleepsAdapter.BleepViewHolder> {

    private final List<Bleep> bleepList;
    private final Context context;
    private final OnBleepClickListener bleepClickListener;

    public BleepsAdapter(List<Bleep> bleepList, Context context, OnBleepClickListener bleepClickListener) {
        this.bleepList = bleepList;
        this.context = context;
        this.bleepClickListener = bleepClickListener;
    }

    @NonNull
    @Override
    public BleepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bleep_row, parent, false);
        return new BleepViewHolder(v, bleepClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BleepViewHolder holder, int position) {
        Bleep bleep = bleepList.get(position);

        Glide.with(context).load(bleep.getUser().getImage()).into(holder.bleepPicView);
        holder.bleepNickView.setText(bleep.getUser().getNick());
        holder.bleepTimeView.setText(Bleep.timeStringFromMillis(bleep.getTimeMillis()));
        holder.bleepContentView.setText(bleep.getContent());
    }

    @Override
    public int getItemCount() {
        return bleepList.size();
    }

    public static class BleepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CircularImageView bleepPicView;
        private final TextView bleepNickView;
        private final TextView bleepTimeView;
        private final TextView bleepContentView;
        private final OnBleepClickListener bleepClickListener;

        public BleepViewHolder(@NonNull View itemView, OnBleepClickListener bleepClickListener) {
            super(itemView);
            this.bleepClickListener = bleepClickListener;
            bleepPicView = itemView.findViewById(R.id.bleepDetailsPic);
            bleepNickView = itemView.findViewById(R.id.bleepNickTextView);
            bleepTimeView = itemView.findViewById(R.id.bleepTimeTextView);
            bleepContentView = itemView.findViewById(R.id.bleepContentTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            bleepClickListener.onBleepClick(getAdapterPosition());
        }
    }

    public interface OnBleepClickListener {
        void onBleepClick(int position);
    }
}
