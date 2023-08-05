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
import com.example.todooo.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var dialog : AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        //checkuser()
        setEvents()
    }

    private fun setEvents() {
        binding.btnsignup.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.btnnext.setOnClickListener {
            val email = binding.edtemail.text.toString()
            val password = binding.edtpassword.text.toString()
            if(email.isEmpty()&&password.isEmpty()){
                Toast.makeText(context, "Please enter require fileds", Toast.LENGTH_SHORT).show()
            }
            else {
                dialog.show()
                signIn(email, password)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                navController.navigate(R.id.action_signInFragment_to_homeFragment)
                Toast.makeText(context, "Logged on", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

    }


    private fun checkuser() {
        if(auth.currentUser!=null){
            navController.navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }

    private fun init(view : View) {
       // binding.btnnext.visibility= View.GONE
        auth = FirebaseAuth.getInstance()
        navController= Navigation.findNavController(view)
        val builder = AlertDialog.Builder(context,
            R.style.customstyle1)
        val view = layoutInflater.inflate(R.layout.progress, null)
        builder.setView(view)
        dialog= builder.create()
    }


}