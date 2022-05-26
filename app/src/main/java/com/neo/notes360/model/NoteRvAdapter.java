package com.neo.notes360.model;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.notes360.Constants;
import com.neo.notes360.ui.notedetail.NoteDetailsActivity;
import com.neo.notes360.R;
import com.neo.notes360.dataSource.database.Note;
import com.neo.notes360.ui.addedit.AddEditActivity;

import java.util.Objects;

public class NoteRvAdapter extends PagedListAdapter<Note, NoteRvAdapter.NoteViewHolder> {

    private Idelete mListener;

    private Context mContext;


    private static DiffUtil.ItemCallback<Note> sItemCallback = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public NoteRvAdapter(Context context) {
        super(sItemCallback);
        mContext = context;
        mListener = (Idelete) mContext;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = getItem(position);
        if (note != null) {
            holder.setData(note);
            holder.setListeners();
        }
    }


    class NoteViewHolder extends RecyclerView.ViewHolder {
        private int noteId = 0;
        private Note note;
        TextView noteTitle, noteContent;
        ImageView menuIcon;
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            menuIcon = itemView.findViewById(R.id.menuIcon);
            view = itemView;
        }

        public void setData(Note note) {
            this.note = note;
            noteTitle.setText(note.getNoteTitle());
            noteContent.setText(note.getNoteContent());
            noteId = note.getId();
        }

        private void setListeners() {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, NoteDetailsActivity.class);
                intent.putExtra(Constants.NOTE_ID, noteId);
                intent.putExtra(Constants.NOTE_TITLE, noteTitle.getText().toString());
                intent.putExtra(Constants.NOTE_CONTENT, noteContent.getText().toString());
//                intent.putExtra(Constants.NOTE_TYPE, Constants.EDIT_NOTE);
                ((AppCompatActivity) mContext).startActivity(intent);
                ((AppCompatActivity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

            menuIcon.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(mContext, v);
                menu.getMenu().add("Edit").setOnMenuItemClickListener(menuItem -> {
                    Intent editIntent = new Intent(mContext, AddEditActivity.class);
                    editIntent.putExtra(Constants.NOTE_ID, noteId);
                    editIntent.putExtra(Constants.NOTE_TITLE, noteTitle.getText().toString());
                    editIntent.putExtra(Constants.NOTE_CONTENT, noteContent.getText().toString());
                    ((AppCompatActivity) mContext).startActivityForResult(editIntent, Constants.EDIT_NOTE);
                    ((AppCompatActivity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                });

                menu.getMenu().add("Delete").setOnMenuItemClickListener(menuItem -> {
                    mListener.deleteSingleNote(this.note);
                    return true;
                });
                menu.show();
            });
        }


    }
}
