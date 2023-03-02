package com.example.todoapp.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.Priority
import com.example.todoapp.data.ToDoData
import com.example.todoapp.databinding.ItemLayoutBinding
import com.example.todoapp.fragments.list.ListFragmentDirections

class ListAdapter:RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    var dataList= emptyList<ToDoData>()
    class MyViewHolder(val binding:ItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_layout,parent,false)
        return MyViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.titleTxt.text=dataList[position].title
        holder.binding.descriptionTxt.text=dataList[position].description
        holder.binding.itemBackground.setOnClickListener {
            val action=
                ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            holder.itemView.findNavController().navigate(action)
        }

        val priority=dataList[position].priority
        when(priority){
            Priority.HIGH->holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.red))
            Priority.MEDIUM->holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.yellow))
            Priority.LOW->holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.green))
        }
    }
    fun setData(toDoData: List<ToDoData>){
        val toDoDiffUtil=ToDoDiffUtil(dataList,toDoData)
        val toDoDiffResult=DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList=toDoData
        toDoDiffResult.dispatchUpdatesTo(this)
    }
}