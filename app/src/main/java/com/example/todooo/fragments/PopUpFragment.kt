package com.example.todooo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todooo.Note
import com.example.todooo.R
import com.example.todooo.databinding.FragmentPopUpBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


class PopUpFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPopUpBinding
    private var previNote : Note? = null
    private var listener : popupevent?= null

    fun setlistener(listener: popupevent){
        this.listener= listener
    }
    fun initprevious(note: Note){
        this.previNote= note
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(previNote!=null){
            binding.edtnote.setText(previNote!!.name)
            binding.edtdes.setText(previNote!!.des)
        }
        binding.edtdes.requestFocus()
        setevents()
    }

    private fun setevents() {


        binding.btnnext.setOnClickListener {
            val note = binding.edtnote.text.toString()
            val des = binding.edtdes.text.toString()
            if(note.isEmpty()&&des.isEmpty()){
                Toast.makeText(context, "no", Toast.LENGTH_SHORT).show()
            }
            else {
                if(previNote==null){
                    listener!!.oncreatenew(note, des)
                }
                else{
                    listener!!.update(Note(previNote?.id!!, note, des))
                }
                dismiss()

            }
            binding.edtdes.setText("")
            binding.edtnote.setText("")


        }
        binding.btncancel.setOnClickListener {
            dismiss()
        }
    }


    interface popupevent{
        fun oncreatenew(note: String, des : String)
        fun update(note : Note)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }



}