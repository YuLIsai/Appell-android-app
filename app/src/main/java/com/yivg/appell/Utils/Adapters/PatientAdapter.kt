package com.yivg.appell.Utils.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.yivg.appell.R
import com.yivg.appell.Utils.Models.PatientModel
import com.yivg.appell.ui.home.HomeFragment

class PatientAdapter(private val dataSet: MutableList<PatientModel>, private val onDeletePatient: (Int)->Unit) :
    RecyclerView.Adapter<PatientAdapter.ViewHolder>() {

//    private  lateinit var mListener: onItemCliskListener
//
//    interface onItemCliskListener{
//        fun onItemClick(position: Int)
//    }
//
//    fun setOnItemClickListener(listener: onItemCliskListener){
//        mListener = listener
//    }

    fun deleteItem(i : Int) {
        onDeletePatient(i)
        dataSet.removeAt(i)
        notifyDataSetChanged()
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView
        val txtNss: TextView
        val imgP: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            txtNombre = view.findViewById(R.id.txtNombre)
            txtNss = view.findViewById(R.id.txtNSS)
            imgP = view.findViewById(R.id.imageProdRi)

//            view.setOnClickListener {
//                listener.onItemClick(adapterPosition)
//            }
        }
    }

    var dataPatientRecyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        dataPatientRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_patient, parent, false)



        return ViewHolder(view)
    }

    private fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            var objGson = Gson()
            var jsonPatient = objGson.toJson(dataSet[position])
            var navController = Navigation.findNavController(it)
            val bundle = bundleOf("jsonPatient" to jsonPatient)
            navController.navigate(R.id.nav_register, bundle)

//            val bundleDetails = bundleOf("jsonPatient" to jsonPatient)
//            navController.navigate(R.id.nav_home, bundleDetails)
        }
        holder.txtNombre.text = dataSet[position].nombre
        holder.txtNss.text = dataSet[position].nss
        holder.imgP.setImageResource(R.drawable.logo_d_t)

    }

    override fun getItemCount() = dataSet.size


}