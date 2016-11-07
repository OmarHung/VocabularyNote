package hung.vocabularynote;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hung on 2016/11/1.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener{
    private List<Map<String, Object>> mData;
    private ArrayList<Integer> mDataType;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private boolean onBind;
    private Context context;
    //private static int nowPosition;
    //private OnRecyclerViewCheckedChangedListener mOnCheckedChangedListener = null;

    //private ArrayList<Integer> mDataTypes;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtEnglish, txtChinese,txtExample;
        public CheckBox checkBox;
        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!onBind) {
                        if(isChecked) new SQLiteHelper(context).updateStar(Long.valueOf(buttonView.getTag().toString()), "1");
                        else new SQLiteHelper(context).updateStar(Long.valueOf(buttonView.getTag().toString()), "0");
                        Log.d("RecyclerViewAdapter",buttonView.getTag().toString());
                        //mData.clear();
                        //mData.addAll(new SQLiteHelper(context).getData());
                        //notifyDataSetChanged();
                    }
                }
            });
            txtEnglish = (TextView) v.findViewById(R.id.textView_English);
            txtChinese = (TextView) v.findViewById(R.id.textView_Chinese);
            txtExample = (TextView) v.findViewById(R.id.textView_Example);
        }
    }
    public RecyclerViewAdapter(Context context, List<Map<String, Object>> data, ArrayList<Integer> type) {//, ArrayList<Integer> type) {
        this.context = context;
        mData = data;
        mDataType = type;
        //mDataTypes = type;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataType.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //Log.d("nowPosition",String.valueOf(nowPosition));
        if(viewType==1)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_end_layout, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtEnglish.setText(mData.get(position).get("English").toString());
        holder.txtChinese.setText(mData.get(position).get("Chinese").toString());
        holder.txtExample.setText(mData.get(position).get("Example").toString());
        onBind = true;
        if(mData.get(position).get("Star").toString().equals("1")) holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);
        onBind = false;
        holder.checkBox.setTag(mData.get(position).get("_id").toString());
        holder.itemView.setTag(mData.get(position).get("_id").toString());

        //Log.d("onBindViewHolder", "onBindViewHolder "+ position);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,v.getTag().toString());
        }
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    /*
    public interface OnRecyclerViewCheckedChangedListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mOnCheckedChangedListener != null) {
            //注意这里使用getTag方法获取数据
            mOnCheckedChangedListener.onCheckedChanged(buttonView, isChecked);
        }
    }
    public void setOnCheckedChangedListener(OnRecyclerViewCheckedChangedListener listener) {
        this.mOnCheckedChangedListener = listener;
    }*/
}
