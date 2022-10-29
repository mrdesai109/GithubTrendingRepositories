package com.rushi.githubtrending.presentation.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rushi.githubtrending.databinding.RvItemPortBinding
import com.rushi.githubtrending.domain.model.GithubRepo

class MainRVAdapter() : ListAdapter<GithubRepo, MainRVAdapter.MainRVViewHolder>(DiffCallback()) {

    var onItemClick: ((GithubRepo, Int, Long) -> Unit)? = null

    inner class MainRVViewHolder(val binding: RvItemPortBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: GithubRepo, position: Int) {
            binding.apply {
                nameTv.text = repo.name
                forkscountTv.text = repo.forks.toString()
                issuescountTv.text = repo.openIssues.toString()
                val colorToSet: Int =
                    if (repo.isSelected) Color.argb(255, 112, 255, 124) else Color.WHITE
                rootCard.setCardBackgroundColor(colorToSet)
                Glide.with(root.context)
                    .load(repo.avatarURL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            cpb.visibility = View.INVISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            cpb.visibility = View.INVISIBLE
                            return false
                        }
                    })
                    .into(avatarIv)
                openGithubRL.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(repo.htmlURL))
                    startActivity(root.context, i, Bundle())
                }
                topCL?.setOnClickListener {
                    onItemClick?.invoke(repo, position,repo.uid)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRVViewHolder {
        val binding = RvItemPortBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainRVViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainRVViewHolder, position: Int) {
        val currentRepo = getItem(position)
        holder.bind(currentRepo, position)
    }

    class DiffCallback : DiffUtil.ItemCallback<GithubRepo>() {
        override fun areItemsTheSame(oldItem: GithubRepo, newItem: GithubRepo): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: GithubRepo, newItem: GithubRepo): Boolean {
            return oldItem == newItem
        }

    }
}