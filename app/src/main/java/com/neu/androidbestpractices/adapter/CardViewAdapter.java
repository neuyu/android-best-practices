package com.neu.androidbestpractices.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neu.androidbestpractices.R;
import com.neu.androidbestpractices.pojo.ItemTest;

import java.util.List;

/**
 * adapter
 * Created by neu on 16/2/2.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder>{
    private Context mContext;
    private List<ItemTest> mList;

    /**
     * 跳转
     * @param position 用户点击的位置
     */
    private void redirectToDetail(int position) {
        Intent intent=new Intent();
        intent.setClassName(mContext,mList.get(position).className);
        mContext.startActivity(intent);
    }
    public CardViewAdapter(List<ItemTest> list, Context context){
        mList=list;
        mContext=context;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(mContext).inflate(R.layout.adapter_item,parent,false);

        return new CardViewHolder(item, new CardViewHolder.ClickListener() {
            @Override
            public void onWholeClick(int position) {
                redirectToDetail(position);
            }
        });
    }


    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final ItemTest title=mList.get(position);
        holder.mTextView.setText(title.itemName);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTextView;
        private ClickListener mClickListener;
        private CardView list_card_view;
        public CardViewHolder(View itemView,ClickListener clickListener) {
            super(itemView);
            this.mClickListener=clickListener;
            mTextView=(TextView)itemView.findViewById(R.id.itemText);
            list_card_view=(CardView)itemView.findViewById(R.id.list_card_view);
            list_card_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.list_card_view:
                    mClickListener.onWholeClick(getAdapterPosition());
                    break;
                default:
                    break;
            }
        }

        public interface ClickListener{
            void onWholeClick(int position);
        }
    }
}
