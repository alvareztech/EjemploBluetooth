package tech.alvarez.ejemplobluetooth;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DispositivosAdapter extends RecyclerView.Adapter<DispositivosAdapter.ViewHolder> {

    private ArrayList<Dispositivo> dataset;
    private OnItemClickListener onItemClickListener;

    public DispositivosAdapter(OnItemClickListener onItemClickListener) {
        this.dataset = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dispositivo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dispositivo dispositivo = dataset.get(position);

        holder.nombreTextView.setText(dispositivo.getNombre());
        holder.descTextView.setText(dispositivo.getDireccion());

        holder.setOnItemClickListener(dispositivo, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView;
        TextView descTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            nombreTextView = (TextView) itemView.findViewById(R.id.nombreTextView);
            descTextView = (TextView) itemView.findViewById(R.id.descTextView);
        }

        public void setOnItemClickListener(final Dispositivo dispositivo, final OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.dispositivoOnClick(dispositivo);
                }
            });
        }
    }

    public void add(Dispositivo dispositivo) {
        dataset.add(dispositivo);
        notifyDataSetChanged();
    }

    public void clear() {
        dataset.clear();
        notifyDataSetChanged();
    }
}
