package com.example.mindwell;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PScoreAdapter extends FirebaseRecyclerAdapter<PScoreModel,PScoreAdapter.myviewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Activity activity;
    public PScoreAdapter(@NonNull FirebaseRecyclerOptions<PScoreModel> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull PScoreModel model) {
        holder.perScore.setText(model.getScore());
        holder.Date.setText(model.getDate());
        holder.progressBar.setProgress(Integer.parseInt(model.getScore()));
        Drawable progressDrawable = holder.progressBar.getProgressDrawable();
        int score=Integer.parseInt(model.getScore());

        if (progressDrawable instanceof LayerDrawable) {
            // Cast the progress drawable to a LayerDrawable
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;

            // Find the item you want to change by its index (0 for the background, 1 for the progress)
            int progressItemIndex = 0;
            Drawable progressItem = layerDrawable.getDrawable(progressItemIndex);

            // Modify the color of the shape inside the progress item
            if (progressItem instanceof GradientDrawable) {
                // Cast the Drawable to a GradientDrawable (assuming it's a shape)
                GradientDrawable shape = (GradientDrawable) progressItem;

                // Set the new color
                if(score < 75 && score > 55)
                {
                    shape.setStroke(3, Color.parseColor("#2fde2d")); // Change to your desired color
                }
                else if(score < 55 && score > 35)
                {
                    shape.setStroke(3, Color.parseColor("#2d88de")); // Change to your desired color
                }
                else{
                    shape.setStroke(3, Color.parseColor("#de2d2d")); // Change to your desired color
                }
            }

            int progressItemIndex_1 = 1;
            Drawable progressItem_1 = layerDrawable.getDrawable(progressItemIndex_1);
            ScaleDrawable scaleDrawable = (ScaleDrawable) progressItem_1;

            // Get the inner drawable
            Drawable innerDrawable = scaleDrawable.getDrawable();

            // If the inner drawable is a GradientDrawable, change its color
            if (innerDrawable instanceof GradientDrawable) {
                GradientDrawable shape = (GradientDrawable) innerDrawable;
                if(score < 75 && score > 55)
                {
                    shape.setColor(Color.parseColor("#2fde2d")); // Change to your desired color
                }
                else if(score < 55 && score > 35)
                {
                    shape.setColor(Color.parseColor("#2d88de")); // Change to your desired color
                }
                else{
                    shape.setColor(Color.parseColor("#de2d2d")); // Change to your desired color
                }
            }
        }

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_score,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView perScore,Date;
        ProgressBar progressBar;

        public myviewholder(View itemView)
        {
            super(itemView);
            perScore = itemView.findViewById(R.id.score_scoreper);
            Date = itemView.findViewById(R.id.score_date);
            progressBar = itemView.findViewById(R.id.score_progress);
        }

    }
}
