package asteroides.example.org.asteroides.views.scoreList;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.views.MainActivity;

/**
 * Created by jmtt_ on 10/21/2017.
 *
 */

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {
    private List<String> scores;
    @Nullable
    private View.OnClickListener onItemClickListener;

    public ScoresAdapter(List<String> scores) {
        this.scores = scores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.score_item, parent, false);

        // Set onClick Listener
        itemView.setOnClickListener(onItemClickListener);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return this.scores.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String score = scores.get(position);
        holder.title.setText(score);

        /*
        switch (Math.round((float)Math.random()*3)){
            case 1:
                holder.icon.setImageResource(R.drawable.asteroide1);
                break;
            case 2:
                holder.icon.setImageResource(R.drawable.asteroide2);
                break;
            default:
                holder.icon.setImageResource(R.drawable.asteroide3);
                break;
        }
        */

        /*
        MainActivity.lectorImagenes.get("http://mmoviles.upv.es/img/moviles.png",
                ImageLoader.getImageListener(holder.icon, R.drawable.asteroide1, R.drawable.asteroide3));
        */


        holder.icon.setImageUrl("http://mmoviles.upv.es/img/moviles.png",
                MainActivity.lectorImagenes);
        holder.icon.setDefaultImageResId(R.drawable.asteroide1);
        holder.icon.setErrorImageResId(R.drawable.asteroide3);
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public NetworkImageView icon;
        public TextView title;
        public TextView subtitle;


        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }
}
