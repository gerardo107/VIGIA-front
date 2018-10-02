package com.example.gerardogarcias.myapplication.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gerardogarcias.myapplication.R;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList) {
        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_single, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String filename = fileNameList.get(i);
        viewHolder.fileNameView.setText(filename);

        /*String fileDone = fileDoneList.get(i);
        if (fileDone.equals("uploading")) {
            viewHolder.fileDoneView.setImageResource(R.drawable.progress);
        } else {
            viewHolder.fileDoneView.setImageResource(R.drawable.checked);
        }*/

        viewHolder.fileDoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileNameList.remove(i);
                fileDoneList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i,fileNameList.size());
                notifyItemRangeChanged(i,fileDoneList.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView fileNameView;
        public ImageView fileDoneView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            fileNameView = (TextView) mView.findViewById(R.id.upload_filename);
            fileDoneView = (ImageView) mView.findViewById(R.id.upload_loading);
        }
    }

}