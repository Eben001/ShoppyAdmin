package com.ebenezer.gana.shoppyadmin.ui.activities

import android.os.Bundle
import com.ebenezer.gana.shoppyadmin.R
import com.ebenezer.gana.shoppyadmin.databinding.ActivityProductDetailsBinding
import com.ebenezer.gana.shoppyadmin.firestore.FirestoreClass
import com.ebenezer.gana.shoppyadmin.models.Products
import com.ebenezer.gana.shoppyadmin.utils.Constants
import com.ebenezer.gana.shoppyadmin.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityProductDetailsBinding
    private var mProductId: String = ""
    private lateinit var mProductDetails: Products
    private var mProductOwnerId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_product_details)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

        }
        getProductDetails()

    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductId)
    }



    fun productDetailsSuccess(product: Products) {
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "₦${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity
        binding.tvProductDetailsShippingCharge.text = "₦${product.shipping_charge}"

        hideProgressDialog()


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProductDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }

}