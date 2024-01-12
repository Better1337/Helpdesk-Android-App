package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> ticketList;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.textViewDescription.setText(ticket.getDescription());
        holder.textViewStation.setText(ticket.getStation());
        holder.textViewStatus.setText(ticket.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("ticketId", ticket.getTicketId());
            context.startActivity(intent);
        });
        if (ticket.getImageUri() != null && !ticket.getImageUri().isEmpty()) {
            Glide.with(context).load(ticket.getImageUri()).into(holder.imageViewTicket);
        } else {
            holder.imageViewTicket.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDescription, textViewStation, textViewStatus;
        ImageView imageViewTicket;

        public TicketViewHolder(View itemView) {
            super(itemView);
            imageViewTicket = itemView.findViewById(R.id.imageViewTicket);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewStation = itemView.findViewById(R.id.textViewStation);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
