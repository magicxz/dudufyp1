package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp.adapter.AddressAdapter
import com.example.fyp.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.address.*

class LoadAddress : AppCompatActivity(){

    lateinit var addressList : MutableList<Address>
    lateinit var ref : DatabaseReference
    lateinit var query : Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.address)

        addressList = mutableListOf()
        addressList.clear()
        var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        query = FirebaseDatabase.getInstance().getReference("Address").orderByChild("userId").equalTo(currentUser)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                addressList.clear()
                if(snapshot.exists()){
                    addimg.isVisible = false
                    textAdd.isVisible = false

                    for(h in snapshot.children){
                        val address = h.getValue(Address::class.java)
                        addressList.add(address!!)
                    }

                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    mLayoutManager.reverseLayout = true

                    reAddress.layoutManager = mLayoutManager
                    reAddress.scrollToPosition(addressList.size-1)
                    reAddress.adapter =
                        AddressAdapter(addressList)
                }else{
                    addimg.isVisible = true
                    textAdd.isVisible = true
                    Log.d("asd","here")

                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    mLayoutManager.reverseLayout = true

                    reAddress.layoutManager = mLayoutManager
                    reAddress.scrollToPosition(addressList.size-1)
                    reAddress.adapter =
                        AddressAdapter(addressList)
                }
            }
        })

        addAddress.setOnClickListener {
            startActivity(Intent(this, AddAddress::class.java))
            this.finish()
        }

        back.setOnClickListener {
            this.finish()
        }
    }
}