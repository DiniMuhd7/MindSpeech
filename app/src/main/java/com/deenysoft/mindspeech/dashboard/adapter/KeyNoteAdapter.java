package com.deenysoft.mindspeech.dashboard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.app.MindApplication;
import com.deenysoft.mindspeech.dashboard.model.KeyNoteItem;
import com.deenysoft.mindspeech.database.MindSpeechDBManager;

import java.util.List;

/**
 * Created by shamsadam on 30/08/16.
 */
public class KeyNoteAdapter extends RecyclerView.Adapter<KeyNoteAdapter.ViewHolder>  {

    private final Activity mActivity;
    private final Resources mResources;
    private final String mPackageName;

    private LayoutInflater mInflater;
    private List<KeyNoteItem> mKeyNoteItemList;
    private KeyNoteItem mKeyNoteItem;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);

    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public KeyNoteAdapter(Activity activity){
        mActivity = activity;
        mResources = mActivity.getResources();
        mPackageName = mActivity.getPackageName();
        mInflater = LayoutInflater.from(this.mActivity);
        updateKeyNoteItem(mActivity); // Called update method that retrieved KeyNoteItem from database
        notifyItemChanged(mActivity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.keynote_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        mKeyNoteItem = mKeyNoteItemList.get(position);
        holder.KeyNoteTag.setText(mKeyNoteItem.getKeyNoteTag());
        // ...
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnItemLongClickListener.onItemLongClicked(position);
                return true;
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mKeyNoteItemList.get(position).getKeyNoteTag().hashCode();
    }


    public final void notifyItemChanged(Context context) {
        updateKeyNoteItem(mActivity);
        notifyItemChanged(getItemPositionById(context));
    }

    private int getItemPositionById(Context context) {
        for (int i = 0; i < mKeyNoteItemList.size(); i++) {
            if (mKeyNoteItemList.get(i).getKeyNoteTag().equals(context)) {
                return i;
            }

        }
        return -1;
    }

    public KeyNoteItem getItem(int position) {
        return mKeyNoteItemList.get(position);
    }


    @Override
    public int getItemCount() {
        return mKeyNoteItemList.size();
    }

    public void updateKeyNoteItem(Context context) {
        mKeyNoteItemList = MindSpeechDBManager.getInstance(context).getKeyNoteItem();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    // Anonymous class containing the view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView KeyNoteTag;

        public ViewHolder(View container) {
            super(container);
            KeyNoteTag = (TextView) container.findViewById(R.id.keynoteTitle);
        }
    }


}
