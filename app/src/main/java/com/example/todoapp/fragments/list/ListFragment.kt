package com.example.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.fragments.SharedViewModel
import com.example.todoapp.fragments.list.adapter.ListAdapter
import com.example.todoapp.viewmodel.ToDoViewModel

class ListFragment : Fragment(),SearchView.OnQueryTextListener {
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var binding:FragmentListBinding?=null
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mToDoViewModel:ToDoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding= FragmentListBinding.inflate(inflater,container,false)



        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer{ data->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        binding?.floatingActionButton2?.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setHasOptionsMenu(true)
        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer {
            showEmptyDatabaseViews(it)
        })
        setupRecyclerView()
        return binding?.getRoot()
    }

    private fun showEmptyDatabaseViews(emptyDatabase:Boolean) {
        if(emptyDatabase){
            binding?.noDataImageView?.visibility=View.VISIBLE
            binding?.noDataTextView?.visibility=View.VISIBLE
        }else{
            binding?.noDataImageView?.visibility=View.INVISIBLE
            binding?.noDataTextView?.visibility=View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu,menu)
        val search:MenuItem=menu.findItem(R.id.menu_search)
        val searchView:SearchView?=search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled=true
       searchView?.setOnQueryTextListener(this)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete_all->confirmRemovalOfAll()
            R.id.menu_priority_high->mToDoViewModel.sortByHighPriority.observe(this,Observer{adapter.setData(it)})
            R.id.menu_priority_low->mToDoViewModel.sortByLowPriority.observe(this, Observer { adapter.setData(it) })
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupRecyclerView(){
        val recyclerView=binding?.recyclerView
        recyclerView?.adapter=adapter
        recyclerView?.layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        swipeToDelete(recyclerView!!)
    }
    private fun swipeToDelete(recyclerView: RecyclerView){
    val swipeToDeleteCallback=object :SwipeToDelete(){
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val itemToDelete=adapter.dataList[viewHolder.adapterPosition]
            mToDoViewModel.deleteItem(itemToDelete)
            Toast.makeText(requireContext(),"Successfully Removed: '${itemToDelete.title}'",Toast.LENGTH_SHORT).show()
        }
    }
        val itemTouchHelper=ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun confirmRemovalOfAll() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mToDoViewModel.deleteAll()
            Toast.makeText(requireContext(),"Successfully removed Everything",
                Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete Everything?")
        builder.setMessage("Are you sure you want to delete all notes?")
        builder.create().show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true;
    }


    private fun searchThroughDatabase(query: String) {
     val searchQuery="%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this,Observer{
        it?.let{
            adapter.setData(it)
        }
        })
    }

    override fun onQueryTextChange(query: String?): Boolean {
      if(query!=null){
          searchThroughDatabase(query)
      }
        return true
    }

}