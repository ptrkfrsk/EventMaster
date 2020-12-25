package com.example.eventmaster.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eventmaster.LoginActivity
import com.example.eventmaster.R
import com.google.android.material.internal.ContextUtils
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        val buttonLogout: Button = root.findViewById(R.id.buttonLogout)
        buttonLogout.setOnClickListener{
            logoutUser()
        }

        return root
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent (activity, LoginActivity::class.java)
        activity?.startActivity(intent)
        //finish()
        //startActivity(Intent(this, LoginActivity::class.java))
    }

}