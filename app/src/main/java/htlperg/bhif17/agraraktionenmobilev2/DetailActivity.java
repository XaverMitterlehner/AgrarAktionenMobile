package htlperg.bhif17.agraraktionenmobilev2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Delayed;

public class DetailActivity extends AppCompatActivity {

    TextView itemDescription, itemPrice, itemBezeichnung, itemHersteller, realPrice;
    ImageView itemImage;
    AppCompatButton button;

    Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemDescription = (TextView)findViewById(R.id.detailDescription);
        itemBezeichnung = (TextView)findViewById(R.id.detailBezeichnung);
        itemHersteller = (TextView)findViewById(R.id.detailHersteller);
        itemImage = (ImageView)findViewById(R.id.detailImage);
        itemPrice = (TextView)findViewById(R.id.detailPrice);
        realPrice = (TextView)findViewById(R.id.detailRealPrice);
        button = (AppCompatButton)findViewById(R.id.buyButton);
        String url;

        mBundle = getIntent().getExtras();

        if(mBundle != null){

            if (mBundle.getString("Image").isEmpty()) {
                itemImage.setImageResource(R.drawable.agraraktionen_a_large);
            } else{
                Picasso.get().load(mBundle.getString("Image")).resize(600,600).into(itemImage);
            }

            getSupportActionBar().setTitle(mBundle.getString("Name").toString());

            itemDescription.setText("Beschreibung:\n\n"+mBundle.getString("Description"));
            itemBezeichnung.setText(mBundle.getString("Name"));
            itemHersteller.setText("Hersteller: "+mBundle.getString("Producer"));

            itemPrice.setText("€ "+mBundle.getString("Price"));
            itemPrice.setAlpha(0f);
            itemPrice.setTranslationY(-50);
            itemPrice.animate().alpha(1f).translationYBy(50).setDuration(500);

            realPrice.setText("€ "+mBundle.getString("ActualPrice"));
            realPrice.setPaintFlags(realPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            realPrice.setAlpha(0f);
            realPrice.setTranslationY(-50);
            realPrice.animate().alpha(1f).translationYBy(50).setDuration(500);


            url = mBundle.getString("Link");

            button.setAlpha(0f);
            button.setTranslationY(-50);
            button.animate().alpha(1f).translationYBy(50).setDuration(500);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

        }

    }
}