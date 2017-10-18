/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_offers.store_offers;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.offers.data.results.store.StoreOfferItem;
import com.sessionm.smp_offers.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Adapter class to draw Offers List
public class StoreOffersRecAdapter extends RecyclerView.Adapter<StoreOffersRecAdapter.OffersViewHolder> {

    private final StoreOffersFragment.Callback _callback;
    private List<StoreOfferItem> _offers = new ArrayList<>();
    private StoreOffersFragment _fragment;

    public StoreOffersRecAdapter(StoreOffersFragment fragment, StoreOffersFragment.Callback callback) {
        _fragment = fragment;
        _callback = callback;
    }

    public void setOffers(List<StoreOfferItem> offers) {
        _offers = offers;

        Collections.sort(_offers, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((StoreOfferItem) lhs).getStartDate().compareTo(((StoreOfferItem) rhs).getStartDate());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_offer_item, parent, false);

        return new OffersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OffersViewHolder holder, int position) {
        final StoreOfferItem offer = _offers.get(position);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        holder.name.setText(offer.getName());
        holder.points.setText(String.format("%.0f", offer.getPrice()));
        holder.validDates.setText(String.format("This offer is available %s through %s",
                offer.getStartDate() != null ? df.format(offer.getStartDate()) : " -- ",
                offer.getEndDate() != null ? df.format(offer.getEndDate()) : " -- "
        ));

        Picasso.with(holder.itemView.getContext()).load(Uri.parse(offer.getMedia().get(0).getURI())).into(holder.media);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new PurchaseOffer(_fragment.getActivity(), _callback)).purchase(offer);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _offers.size();
    }

    public class OffersViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView media;
        private final TextView points;
        private final TextView validDates;

        public OffersViewHolder(View v) {
            super(v);
            validDates = (TextView)v.findViewById(R.id.valid_dates);
            name = (TextView)v.findViewById(R.id.offer_name);
            media = (ImageView)v.findViewById(R.id.offer_media);
            points = (TextView)v.findViewById(R.id.offer_points);
        }
    }

}