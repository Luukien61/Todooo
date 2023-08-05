package com.example.todooo.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todooo.R
import com.example.todooo.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {

private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var dialog : AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignUpBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intit(view)
        setEvents()
    }

    private fun setEvents() {
        binding.btnsignin.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.btnnext.setOnClickListener {
            checkvalues()
        }
    }

    private fun checkvalues() {
        val email = binding.edtemail.text.toString()
        val password = binding.edtpassword.text.toString()
        val checkpass = binding.edtcheckpass.text.toString()
        if(email.isEmpty()||password.isEmpty()||checkpass.isEmpty()){
            Toast.makeText(context, "Please enter require fileds", Toast.LENGTH_SHORT).show()
        }else {
            if(password!= checkpass){
                Toast.makeText(context, "Password don't match", Toast.LENGTH_SHORT).show()
            }
            else {
                dialog.show()
                createnewuser(email, password)
            }
        }
    }

    private fun intit(view: View) {
        auth = FirebaseAuth.getInstance()
        navController= Navigation.findNavController(view)
        val builder = AlertDialog.Builder(context,
            R.style.customstyle1)
        val view = layoutInflater.inflate(R.layout.progress, null)
        builder.setView(view)
        dialog= builder.create()

    }

    private fun createnewuser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password ).addOnCompleteListener {
            if(it.isSuccessful){
                dialog.dismiss()
                navController.navigate(R.id.action_signUpFragment_to_homeFragment)
                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}