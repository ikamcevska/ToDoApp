package com.example.todoapp.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.Priority
import com.example.todoapp.data.ToDoData
import com.example.todoapp.databinding.FragmentAddBinding
import com.example.todoapp.fragments.SharedViewModel
import com.example.todoapp.viewmodel.ToDoViewModel

class AddFragment : Fragment() {
    private val mToDoViewModel:ToDoViewModel by viewModels()
    private val mSharedViewModel:SharedViewModel by viewModels()

    private lateinit var binding:FragmentAddBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddBinding.inflate(layoutInflater,container,false)
        setHasOptionsMenu(true)
        binding?.prioritySpinner?.onItemSelectedListener=mSharedViewModel.listener
        return binding.getRoot()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_add){
            insertDatatoDatabase()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDatatoDatabase() {
        val mTitle=binding?.etTitle?.text.toString()
        val mPriority=binding?.prioritySpinner?.selectedItem.toString()
        val mDescription=binding?.etDescription?.text.toString()

        val validation=mSharedViewModel.verifyDataFromUser(mTitle,mDescription)
        if(validation){
            val newData=ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriorityObject(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(),"Successfully added!",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
        else{
            Toast.makeText(requireContext(),"Please fill out all fields",Toast.LENGTH_SHORT).show()
        }
    }



}