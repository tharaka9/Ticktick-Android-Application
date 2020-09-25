package com.example.ticktick.Adapter;

import android.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.orhanobut.dialogplus.DialogPlus;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticktick.Model.Incomes;
import com.example.ticktick.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends FirebaseRecyclerAdapter<Incomes,MyAdapter.myviewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyAdapter(@NonNull FirebaseRecyclerOptions<Incomes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, final int position, @NonNull final Incomes incomes) {
        holder.incomeName.setText(incomes.getIncomeName());
        holder.incomeAmount.setText(incomes.getIncomeAmount());
        holder.incomeNote.setText(incomes.getIncomeNote());

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.incomeName.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_modal))
                        .setExpanded(true,1100)
                        .create();


                View updateView =dialogPlus.getHolderView();
                final EditText name= updateView.findViewById(R.id.upName);
                final EditText amount = updateView.findViewById(R.id.upAmount);
                final EditText note = updateView.findViewById(R.id.upNote);

                Button update= updateView.findViewById(R.id.upSubmit);

                name.setText(incomes.getIncomeName());
                amount.setText(incomes.getIncomeAmount());
                note.setText(incomes.getIncomeNote());

                dialogPlus.show();

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,Object> map = new HashMap<>();
                            map.put("incomeName",name.getText().toString());
                            map.put("incomeAmount",amount.getText().toString());
                            map.put("incomeNote",note.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Incomes")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                            dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                            dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });


                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.incomeName.getContext());
                        builder.setTitle("Delete Income");
                        builder.setMessage("Delete This Income Record?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("Incomes").child(getRef(position).getKey()).removeValue();

                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        builder.show();
                    }
                });
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder {

        TextView incomeName, incomeAmount, incomeNote;
        ImageView editBtn,deleteBtn;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            incomeName =(TextView)itemView.findViewById(R.id.iName);
            incomeAmount =(TextView)itemView.findViewById(R.id.iAmount);
            incomeNote =(TextView)itemView.findViewById(R.id.iNote);
            editBtn=(ImageView)itemView.findViewById(R.id.editIcon);
            deleteBtn=(ImageView)itemView.findViewById(R.id.deleteIcon);
        }
    }
}
