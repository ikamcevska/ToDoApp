package com.example.todoapp.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.data.Priority
import com.example.todoapp.data.ToDoData
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.example.todoapp.fragments.SharedViewModel
import com.example.todoapp.viewmodel.ToDoViewModel

class UpdateFragment : Fragment() {

    private lateinit var binding:FragmentUpdateBinding
    private val mSharedViewModel:SharedViewModel by viewModels()
    private val args by navArgs<UpdateFragmentArgs>()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentUpdateBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        binding.currentEtTitle.setText(args.currentitem.title)
        binding.currentEtDescription.setText(args.currentitem.description)
        binding.currentPrioritySpinner.setSelection(mSharedViewModel.parsePriority(args.currentitem.priority))
        binding.currentPrioritySpinner.onItemSelectedListener=mSharedViewModel.listener
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_save){
            updateItem()
        }else if(item.itemId==R.id.menu_delete){
            confirmItemDeletion()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemDeletion() {
    val builder=AlertDialog.Builder(requireContext())
    builder.setPositiveButton("Yes"){_,_->
        mToDoViewModel.deleteItem(args.currentitem)
        Toast.makeText(requireContext(),"Successfully removed: ${args.currentitem.title}",
        Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
    }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete ${args.currentitem.title}")
        builder.setMessage("Are you sure you want to remove ${args.currentitem.title}?")
        builder.create().show()
    }

    private fun updateItem() {
        val title=binding.currentEtTitle.text.toString()
        val description=binding.currentEtDescription.text.toString()
        val getPriority=binding.currentPrioritySpinner.selectedItem.toString()

        val validation=mSharedViewModel.verifyDataFromUser(title,description)
        if(validation){
            val updatedItem=ToDoData(
                args.currentitem.id,
                title,
                mSharedViewModel.parsePriorityObject(getPriority),
                description
            )
            mToDoViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(),"Succesfully updated",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Please fill out all fields",Toast.LENGTH_SHORT).show()
        }
    }


}