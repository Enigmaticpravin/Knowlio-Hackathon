package com.orpheum.knowlio.Utilities;

import androidx.recyclerview.widget.DiffUtil;

import com.orpheum.knowlio.Class.Post;

import java.util.List;

public class PostDiffCallback extends DiffUtil.Callback {
    private List<Post> oldList;
    private List<Post> newList;

    public PostDiffCallback(List<Post> oldList, List<Post> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getPostid() == newList.get(newItemPosition).getPostid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

