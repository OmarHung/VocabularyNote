package hung.vocabularynote;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Hung on 2016/11/1.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<Map<String, Object>> mData;
    //private ArrayList<Integer> mDataTypes;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtEnglish, txtChinese;
        public ImageButton imgBtnStar;

        public ViewHolder(View v) {
            super(v);
            imgBtnStar = (ImageButton) v.findViewById(R.id.imageButton_Star);
            txtEnglish = (TextView) v.findViewById(R.id.textView_English);
            txtChinese = (TextView) v.findViewById(R.id.textView_Chinese);
        }
    }
    public RecyclerViewAdapter(List<Map<String, Object>> data) {//, ArrayList<Integer> type) {
        mData = data;
        //mDataTypes = type;
    }
    /*
    @Override
    public int getItemViewType(int position) {
        return mDataTypes.get(position);
    }
    */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false));
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtEnglish.setText(mData.get(position).get("English").toString());
        holder.txtChinese.setText(mData.get(position).get("Chinese").toString());
        holder.imgBtnStar.setImageResource(R.drawable.ic_menu_send);
        Log.d("onBindViewHolder", "onBindViewHolder "+ position);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
