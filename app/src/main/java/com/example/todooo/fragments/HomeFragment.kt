package com.example.todooo.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todooo.Note
import com.example.todooo.NoteFirebase
import com.example.todooo.R
import com.example.todooo.RvAdapter
import com.example.todooo.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class HomeFragment : Fragment(), PopUpFragment.popupevent {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var rvAdapter: RvAdapter
    private var listnote = ArrayList<Note>()
    private lateinit var dataref : DatabaseReference
    private var popUpFragment : PopUpFragment?= null
    private lateinit var dialogg : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getdatafromFirebase()
        setevents()
        setswipe()

    }

    private fun setevents() {
        binding.btnadd.setOnClickListener {
            if(popUpFragment!=null){
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            }
            popUpFragment=PopUpFragment()
            popUpFragment!!.setlistener(this)
            popUpFragment!!.show(
                childFragmentManager,
                "Abc"
            )
        }
        binding.drawer.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.btndelete->{
                    alerdialog("Delete")
                }
                R.id.btnlogout ->{
                    alerdialog("Log out")
                }
            }
            true
        }
    }

    private fun getdatafromFirebase() {
        dataref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listnote.clear()
                if(snapshot.exists()){
                    for(notesnap in snapshot.children){
                       notesnap.key?.let {
                           val notenew = notesnap.getValue(NoteFirebase::class.java)
                           val note = Note(it, notenew?.note!!, notenew?.des!!)
                           listnote.add(note)
                       }
                    }
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun init(view : View) {
        auth = FirebaseAuth.getInstance()
        navController= Navigation.findNavController(view)
        dataref= FirebaseDatabase.getInstance().reference.child("Note")
            .child(auth.currentUser?.uid.toString())
        rvAdapter= RvAdapter()
        rvAdapter.initnote(listnote)
        binding.recycleview.setHasFixedSize(true)
        binding.recycleview.layoutManager= LinearLayoutManager(context)
        binding.recycleview.adapter= rvAdapter
        popUpFragment=PopUpFragment()
        popUpFragment!!.setlistener(this)
    }

    override fun oncreatenew(note: String, des: String) {
        dataref.push().setValue(NoteFirebase(note, des)).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setswipe(){
        ItemTouchHelper(object : ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    , ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = rvAdapter.getnote(viewHolder.adapterPosition)
                if(direction== ItemTouchHelper.LEFT){
                    //delete
                    dataref.child(note.id!!).removeValue().addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            rvAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                            rvAdapter.notifyDataSetChanged()
                        }
                        else Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    //update
                    if(popUpFragment!=null){
                        childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
                    }
                    popUpFragment= PopUpFragment()
                    popUpFragment!!.setlistener(this@HomeFragment)
                    popUpFragment!!.initprevious(note)
                    popUpFragment!!.show(
                        childFragmentManager, "Abc"
                    )
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.white))
                    .addSwipeRightActionIcon(R.drawable.edit)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))
                    .create().decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }).attachToRecyclerView(binding.recycleview)
    }

    override fun update(note: Note) {
        val notefirebase = NoteFirebase(note.name, note.des)
        dataref.child(note.id!!).setValue(notefirebase).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                rvAdapter.notifyDataSetChanged()
            }
            else Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun initdialog(){
        val builder = AlertDialog.Builder(context, R.style.customstyle1)
        val view = layoutInflater.inflate(R.layout.progress, null)
        builder.setView(view)
        dialogg=builder.create()
        dialogg.show()
    }
    private fun alerdialog (task : String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("$task")
        builder.setMessage("Are you sure to ${task.lowercase()} ?")
        builder.setPositiveButton("Yes",object :DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if(task.equals("Delete")){
                    dataref.removeValue().addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Remove All", Toast.LENGTH_SHORT).show()
                            rvAdapter.notifyDataSetChanged()
                        }
                        else Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }

                }
                else {
                    initdialog()
                    auth.signOut()
                    Handler(Looper.myLooper()!!).postDelayed(
                        {
                            dialogg?.dismiss()
                            navController.navigate(R.id.action_homeFragment_to_signInFragment)},
                        1000
                    )
                }

            }
        })
        builder.setNegativeButton("No", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }
        })
        builder.create()
        builder.show()
    }


}