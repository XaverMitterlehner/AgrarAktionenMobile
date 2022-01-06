package htlperg.bhif17.agraraktionenmobilev2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import htlperg.bhif17.agraraktionenmobilev2.model.Item;
import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;
import lombok.SneakyThrows;

public class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> implements Filterable {

    private Context mContext;
    private List<Item> itemList;
    private List<Item> itemListFull;

    public RecyclerAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
        itemListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item, parent, false);

        return new ItemViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        if (itemList.get(position).getBildLink().isEmpty()) {
            holder.imageView.setImageResource(R.drawable.image_placeholder);
            holder.imageView.setBackgroundColor(Color.WHITE);
        } else{
            Picasso.get().load(itemList.get(position).getBildLink()).resize(100, 100).into(holder.imageView);
        }

        holder.mTitle.setText(itemList.get(position).getArtikelbezeichnung());
        holder.mHersteller.setText("von: "+itemList.get(position).getHersteller());
        holder.mPrice.setText("€ "+itemList.get(position).getBruttopreis());
        holder.mdiscount.setText("- "+String.format("%.1f", itemList.get(position).getPercentage())+"%");
        //holder.mdiscount.setText("- "+String.format("%.1f", getDiscount(position))+"%");

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("Image", itemList.get(holder.getAdapterPosition()).getBildLink());
                intent.putExtra("Name", itemList.get(holder.getAdapterPosition()).getArtikelbezeichnung());
                intent.putExtra("Producer", itemList.get(holder.getAdapterPosition()).getHersteller());
                intent.putExtra("Description", itemList.get(holder.getAdapterPosition()).getBeschreibungsfeld());
                intent.putExtra("Price", itemList.get(holder.getAdapterPosition()).getBruttopreis());
                intent.putExtra("ActualPrice", itemList.get(holder.getAdapterPosition()).getStattpreis());
                intent.putExtra("Link", itemList.get(holder.getAdapterPosition()).getDeeplink());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter(){
        return itemFilter;
    }

    private Filter itemFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();

            boolean added = false;

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(itemListFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Item item : itemListFull){
                    if(item.getArtikelbezeichnung().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                        added = true;
                    }
                    if (added == false && item.getBeschreibungsfeld().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                    added = false;
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSortFilter(){
        return sortFilter;
    }

    private Filter sortFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Item> sortedList = new ArrayList<>();
            sortedList.addAll(itemListFull);

            if (constraint.equals("Preis aufsteigend")) {
                sortedList = sort(sortedList);

            } else if (constraint.equals("Preis absteigend")) {
                sortedList = sort(sortedList);
                Collections.reverse(sortedList);
            }

            FilterResults results = new FilterResults();
            results.values = sortedList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getPriceFilter(){
        return priceFilter;
    }

    private Filter priceFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> priceList = new ArrayList<>();

            boolean added = false;

            int price1 = MyProperties.getInstance().priceFilter1;
            int price2 = MyProperties.getInstance().priceFilter2;

            int itemPrice;

            if(price1 == 0 && price2 == 0) {
                priceList.addAll(itemListFull);
            } else {

                for (Item item : itemListFull) {
                    itemPrice = Integer.parseInt(item.getBruttopreis());
                    if (itemPrice >= price1) {
                        priceList.add(item);
                        added = true;
                    }
                    if (added == false && itemPrice <= price2) {
                        priceList.add(item);
                    }
                    added = false;
                }
            }

            FilterResults results = new FilterResults();
            results.values = priceList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public List<Item> sort(List<Item> list){

        Collections.sort(list, new Comparator<Item>() {
            @SneakyThrows
            @Override
            public int compare(Item o1, Item o2) {

                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

                Number number = format.parse(o1.getBruttopreis());
                double price1 = number.doubleValue();

                Number number2 = format.parse(o2.getBruttopreis());
                double price2 = number2.doubleValue();

                if(price1 < price2){
                    return -1;
                } else if (price1 > price2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

    // get percentage of the discount of the item
    public double getDiscount(int position){
        String a, b;
        a = itemList.get(position).getBruttopreis();
        a = a.replace(",", ".");

        b = itemList.get(position).getStattpreis();
        b = b.replace(",",".");

        double bruttopreis = Double.parseDouble(a);
        double stattpreis = Double.parseDouble(b);

        double discount = bruttopreis/stattpreis;
        discount = (1 - discount) * 100;

        return discount;

        /*d
        String val1 = String.format("%.2f", dVal);
        String val2 = String.format("%.3f", dVal);
        System.out.println("Value after Formatting: "+val1);
        System.out.println("Value after Formatting: "+val2);*/
    }

}

class ItemViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView mTitle, mHersteller, mPrice, mdiscount;
    CardView mCardView;

    public ItemViewHolder( View itemView){
        super(itemView);
        imageView = itemView.findViewById(R.id.ivImage);
        mTitle = itemView.findViewById(R.id.tvTitle);
        mHersteller = itemView.findViewById(R.id.tvHersteller);
        mPrice = itemView.findViewById(R.id.tvPrice);
        mdiscount = itemView.findViewById(R.id.discount);

        mCardView = itemView.findViewById(R.id.myCardView);
    }

}


