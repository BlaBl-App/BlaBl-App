package com.blablapp.blablapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.aboutus_activity.*
import java.net.URL


class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aboutus_activity)

        imageViewIcePick4.setOnClickListener {
            navigationToGitHub("icepick4")
        }
        imageViewNabileSall.setOnClickListener {
            navigationToGitHub("nabilesall")
        }
        imageViewQypol342.setOnClickListener{
            navigationToGitHub("qypol342")
        }
        imageViewBlaBlAppGit.setOnClickListener {
            navigationToGitHub("BlaBl-App/BlaBl-App")
        }
        imageViewAPI.setOnClickListener {
            navigationToGitHub("BlaBl-App/BlaBl-API")
        }
        imageViewListOfServers.setOnClickListener {
            navigationToGitHub("BlaBl-App/BlaBl-ist")
        }

        iconBackBig.setOnClickListener {
            finish()
        }
    }


    /**
     * Open the browser with the github page of the author
     */
    private fun navigationToGitHub(id: String){
        val url = URL("https://github.com/".plus(id))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        startActivity(intent)
    }
}
