package com.example.fyp.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.CountOrder
import com.example.fyp.PostDetails
import com.example.fyp.R
import com.example.fyp.model.Notification
import com.example.fyp.model.Post
import com.example.fyp.model.Users
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.concurrent.schedule

class NotificationAdapter(var notification : MutableList<Notification>) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>(){

    lateinit var query: Query
    lateinit var query1: Query
    lateinit var postList : MutableList<Post>
    var stop : Boolean = false

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var msg = itemView.findViewById<TextView>(R.id.message)
        var datetime = itemView.findViewById<TextView>(R.id.notitime)
        var notiback = itemView.findViewById<CardView>(R.id.notiBackground)
        var senderImg = itemView.findViewById<CircleImageView>(R.id.proImg)
        var postImage = itemView.findViewById<ImageView>(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.load_notification,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        postList = mutableListOf()
        holder.msg.text = notification[position].type
        holder.datetime.text = notification[position].date
        val sender = notification[position].sender

        if(notification[position].status.equals("false")){
            holder.notiback.setCardBackgroundColor(Color.rgb(135,206,250))
        }

        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(sender)

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists()){
                    for(h in p0.children){
                        val targetUser = h.getValue(Users::class.java)
                        val profilePhoto = targetUser!!.image
                        Picasso.get().load(profilePhoto).into(holder.senderImg)
                    }
                }
            }
        })

        query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postId").equalTo(notification[position].postId)

        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        val post = h.getValue(Post::class.java)
                        val postImage = post!!.postImage
                        Picasso.get().load(postImage).into(holder.postImage)
                    }
                }
            }
        })

        holder.notiback.setOnClickListener {
            stop == false
            query1 = FirebaseDatabase.getInstance().getReference("Posts").child(notification[position].postId)

            query1.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val post = p0.getValue<Post>(
                            Post::class.java)
                        postList.add(post!!)
                        CountOrder.getPost = post

                        //Post ID de user
                        query = FirebaseDatabase.getInstance().getReference("Users")
                            .orderByChild("uid").equalTo(postList[0].userId)

                        query.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if (p0.exists()) {
                                    for (h in p0.children) {
                                        val user = h.getValue(Users::class.java)
                                        CountOrder.getUser = user!!

                                        val intent = Intent(holder.notiback.context,
                                            PostDetails::class.java)
                                        intent.putExtra("PostId", CountOrder.getPost.postId)
                                        intent.putExtra("DateTime", CountOrder.getPost.datetime)
                                        intent.putExtra("Content", CountOrder.getPost.content)
                                        intent.putExtra("UserId", CountOrder.getPost.userId)
                                        intent.putExtra("PostPhoto", CountOrder.getPost.postImage)
                                        intent.putExtra("Username", CountOrder.getUser.username)
                                        intent.putExtra("ProfileImage", CountOrder.getUser.image)
                                        holder.notiback.context.startActivity(intent)
                                    }
                                }
                            }
                        })
                    }
                }
            })


            Timer("SettingUp", false).schedule(1500) {
                query = FirebaseDatabase.getInstance().getReference("Notification")
                    .child(notification[position].notificationId)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.exists()) {
                            val noti = p0.getValue(Notification::class.java)
                            if(stop==false){
                                noti!!.status = "true"
                                stop = true
                            }

                            FirebaseDatabase.getInstance().getReference("Notification")
                                .child(notification[position].notificationId)
                                .setValue(noti)
                        }
                    }
                })
            }
        }
    }
}