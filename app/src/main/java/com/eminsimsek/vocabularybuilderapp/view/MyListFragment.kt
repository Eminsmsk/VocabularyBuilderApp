package com.eminsimsek.vocabularybuilderapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.model.Word
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.eminsimsek.vocabularybuilderapp.service.CallBackInterface
import com.eminsimsek.vocabularybuilderapp.service.MyListDAO
import kotlinx.android.synthetic.main.fragment_my_list.*
import java.io.ByteArrayOutputStream

import kotlin.collections.ArrayList

class MyListFragment : Fragment(), CallBackInterface {
    private var myList: ArrayList<Word> = ArrayList<Word>()
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var localdb: AppDatabase
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var mDialogView: View
    private lateinit var editTextAddImage: EditText
    private var selectedImage: Uri? = null
    private var selectedBitmap: Bitmap? = null
    private val defaultImagePath: String =
        "https://user-images.githubusercontent.com/33187905/120741518-d983de00-c4fd-11eb-8a68-f76f949fcaf0.png"
    private var imagePath: String? = null
    private var mAlertDialog: AlertDialog? = null
    private var flag = false
    private var word: Word? = null

    override fun changeImage(word: Word) {
        this.word = word
        flag = true
        loadImage()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myListRV: RecyclerView = view.findViewById(R.id.myListRV)
        myListRV.layoutManager = LinearLayoutManager(context)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.setRefreshing(false)
            myList.clear()
            loadMyList()
            recyclerAdapter.notifyDataSetChanged()
        }
        mDialogView = LayoutInflater.from(context!!).inflate(R.layout.alertdialog_design, null)


        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );

        fabAddWord.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(context!!).inflate(R.layout.alertdialog_design, null)

            editTextAddImage = mDialogView.findViewById(R.id.editTextAddImage)
            editTextAddImage.setOnClickListener {
                loadImage()


            }

            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Add a new word")
            builder.setView(mDialogView)

            builder.setPositiveButton("OK") { dialog, which ->

                val wordName: String? =
                    mDialogView.findViewById<EditText>(R.id.editTextWord).text.toString()
                val definition: String? =
                    mDialogView.findViewById<EditText>(R.id.editTextDefinition).text.toString()
                val example: String? =
                    mDialogView.findViewById<EditText>(R.id.editTextExample).text.toString()
                val synonyms: String? =
                    mDialogView.findViewById<EditText>(R.id.editTextSynonyms).text.toString()
                val antonyms: String? =
                    mDialogView.findViewById<EditText>(R.id.editTextAntonyms).text.toString()



                if (wordName.isNullOrBlank() || definition.isNullOrBlank() || example.isNullOrBlank())
                    Toast.makeText(
                        context!!,
                        "Fill in necessary fields(word,definition,example)",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    if (selectedBitmap != null && !editTextAddImage.text.isNullOrEmpty()) {
                        val baos: ByteArrayOutputStream = ByteArrayOutputStream()
                        selectedBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                        val b = baos.toByteArray()
                        imagePath = Base64.encodeToString(b, Base64.DEFAULT)
                        word = Word(wordName, definition, example, synonyms, antonyms, imagePath)
                    } else
                        word = Word(
                            wordName,
                            definition,
                            example,
                            synonyms,
                            antonyms,
                            defaultImagePath
                        )

                    MyListDAO().addWord(localdb, word!!)
                    myList.clear()
                    loadMyList()
                    recyclerAdapter.notifyDataSetChanged()
                    Toast.makeText(context!!, "Word is added to the My List", Toast.LENGTH_SHORT)
                        .show()
                }


            }

            builder.setNegativeButton("CANCEL") { dialog, which ->
                Toast.makeText(
                    context!!,
                    "CANCEL", Toast.LENGTH_SHORT
                ).show()

            }
            mAlertDialog = builder.create()
            if (mAlertDialog != null)
                mAlertDialog!!.show()

        }

        loadMyList()
        recyclerAdapter = RecyclerAdapter(context, myList, localdb, this)
        myListRV.adapter = recyclerAdapter

    }

    private fun loadMyList() {
        localdb = AppDatabase(activity?.applicationContext)
        MyListDAO().getAllMyList(localdb)?.let { myList.addAll(it) }

    }

    private fun loadImage() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            val source = ImageDecoder.createSource(
                context!!.applicationContext.contentResolver,
                selectedImage!!
            )

        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)

        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data
            if (selectedImage != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(
                        context!!.applicationContext.contentResolver,
                        selectedImage!!
                    )
                    selectedBitmap = ImageDecoder.decodeBitmap(source)


                } else {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                        context!!.applicationContext.contentResolver,
                        selectedImage
                    )

                }
                //writes selected image file name to edittext area
                if (mAlertDialog != null) {
                    if (mAlertDialog!!.isShowing)
                        editTextAddImage.setText(
                            DocumentFile.fromSingleUri(
                                context!!,
                                selectedImage!!
                            )!!.getName()
                        )
                }
                //if user wants to change existing word's image
                if (flag) {
                    val baos: ByteArrayOutputStream = ByteArrayOutputStream()
                    selectedBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    val b = baos.toByteArray()
                    imagePath = Base64.encodeToString(b, Base64.DEFAULT)
                    MyListDAO().updateWordImage(localdb, word!!, imagePath!!)
                    Toast.makeText(context, "Image is changed for reminder", Toast.LENGTH_SHORT)
                        .show()
                    flag = false
                }
            }


        }


        super.onActivityResult(requestCode, resultCode, data)
    }


}