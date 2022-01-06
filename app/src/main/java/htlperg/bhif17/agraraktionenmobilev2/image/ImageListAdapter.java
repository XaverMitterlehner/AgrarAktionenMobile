package htlperg.bhif17.agraraktionenmobilev2.image;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.model.Image;
import htlperg.bhif17.agraraktionenmobilev2.model.Item;

public class ImageListAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    private Context context;
    private List<Image> imageList;

    public ImageListAdapter (Context mContext, List<Image> imageList) {
        this.context = mContext;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagelist_row_item, parent, false);

        return new ImageViewHolder(mView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        holder.textView.setText("id: " + imageList.get(position).getId());

        byte[] b = imageList.get(position).getBytes();

        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        Bitmap image = Bitmap.createBitmap(bmp);
        holder.imageView.setImageBitmap(image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Optional<Root> images = Optional.ofNullable(null);
                Log.i("ImageRecyclerView", "tagging image as usable");
                try {
                    images = Optional.of(new ObjectMapper()
                            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                            .readValue(new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/image/" + imageList.get(holder.getAdapterPosition()).getId()), Root.class));
                } catch (Exception e) {
                    Log.e("ImageRecyclerView", "Failed", e);
                }
                Log.i("ImageRecyclerView", "successfully");;

                Intent intent = new Intent(context, SimiliarItemsActivity.class);
                context.startActivity(intent);
            }
        });

    }

}

class Root {
    public Image [] images;
}

class ImageViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;
    CardView cardView;

    public ImageViewHolder (View view){
        super(view);
        imageView = itemView.findViewById(R.id.ilImage);
        textView = itemView.findViewById(R.id.ilText);

        cardView = itemView.findViewById(R.id.imageListCardView);
    }

}


