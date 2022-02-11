package com.ebenezer.gana.shoppyadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.ebenezer.gana.shoppyadmin.R
import com.ebenezer.gana.shoppyadmin.databinding.FragmentDashboardBinding
import com.ebenezer.gana.shoppyadmin.firestore.FirestoreClass
import com.ebenezer.gana.shoppyadmin.models.Products
import com.ebenezer.gana.shoppyadmin.ui.activities.LoginActivity
import com.ebenezer.gana.shoppyadmin.ui.adapters.DashboardListAdapter
import com.google.firebase.auth.FirebaseAuth


class DashboardFragment : BaseFragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding?.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }

        }


        return super.onOptionsItemSelected(item)

    }


    fun successDashboardItemsList(dashboardItemList: ArrayList<Products>) {
        hideProgressDialog()
        /* for (item in dashboardItemList){
             Log.i("Item Title", item.title)
         }*/

        binding?.let {
            if (dashboardItemList.size > 0) {
                binding!!.rvDashboardItems.visibility = View.VISIBLE
                binding!!.tvNoDashboardItemsFound.visibility = View.GONE

                binding!!.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
                binding!!.rvDashboardItems.setHasFixedSize(true)
                val allProductsAdapter = DashboardListAdapter(requireActivity(), dashboardItemList)
                binding!!.rvDashboardItems.adapter = allProductsAdapter

                /* allProductsAdapter.setOnclickListener(
                     object:DashboardListAdapter.OnClickListener{
                         override fun onClick(position: Int, product: Products) {
                             val intent = Intent(context, ProductDetailsActivity::class.java)
                             intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                             context.startActivity(intent)
                         }
                     }
                 )*/


            } else {
                binding!!.rvDashboardItems.visibility = View.GONE

                binding!!.tvNoDashboardItemsFound.visibility = View.VISIBLE
            }
        }




    }

    private fun getDashboardItemList() {
        //show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}