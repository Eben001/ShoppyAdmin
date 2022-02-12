package com.ebenezer.gana.shoppyadmin.firestore

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.ebenezer.gana.shoppyadmin.models.*
import com.ebenezer.gana.shoppyadmin.ui.activities.*
import com.ebenezer.gana.shoppyadmin.ui.fragments.DashboardFragment
import com.ebenezer.gana.shoppyadmin.ui.fragments.ProductsFragment
import com.ebenezer.gana.shoppyadmin.ui.fragments.SoldProductsFragment
import com.ebenezer.gana.shoppyadmin.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * A custom class where we will add the operation performed for the FireStore database.
 */
class FirestoreClass {

    // Access a Cloud Firestore instance.
    private val mFirestore = FirebaseFirestore.getInstance()


    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun getCurrentUserId(): String {
        // An instance of currentUser using FirebaseAuth module
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank
        /* var currentUserID = ""
         currentUserID.let {
             currentUserID = currentUser!!.uid
         }*/
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


    // A function to upload the image to the cloud storage.

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "$imageType ${System.currentTimeMillis()}.${
                Constants.getFileExtension(
                    activity, imageFileURI
                )
            }"
        )

        // upload the file to the cloud
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            // Get the downloadable url from the task snapshot

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }

        }
            .addOnFailureListener { exception ->

                when (activity) {
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    /**
     * A function to get the list of sold products from the cloud firestore.
     *
     *  @param fragment Base class
     */
    fun getSoldProductsList(fragment: SoldProductsFragment) {
        mFirestore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {

                val soldProductsList = ArrayList<SoldProduct>()
                for (items in it.documents) {
                    val soldProduct = items.toObject(SoldProduct::class.java)!!
                    soldProduct.id = items.id

                    soldProductsList.add(soldProduct)
                }

                fragment.successSoldProductsList(soldProductsList)


            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error getting sold products list", it
                )
            }

    }

    fun deleteASoldProduct(fragment: SoldProductsFragment, userId: String) {
        mFirestore.collection(Constants.SOLD_PRODUCTS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                fragment.successDeletingASoldProduct()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while deleting all orders", it
                )
            }

    }
    /**
     * A function to make an entry of the user's product in the cloud firestore database.
     */
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Products) {

        mFirestore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {

                // We call the AddProductActivity to handle the upload success
                activity.productUploadSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details", exception
                )
            }
    }

    //Gets the product list for a single user from Firestore
    fun getProductsList(fragment: Fragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Products> = ArrayList()
                for (item in document.documents) {

                    val product = item.toObject(Products::class.java)
                    product!!.product_id = item.id

                    productsList.add(product)

                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }

                }
            }
            .addOnFailureListener { exception ->
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                        Log.e(
                            fragment.javaClass.simpleName,
                            "Something went wrong, couldn't get product details", exception
                        )

                    }

                }

            }

    }

    /**
     * A function to get the product details based on the product id.
     */
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        // The collection name for PRODUCTS

        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener {
                val product = it.toObject(Products::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Something went wrong, couldn't get product details", it
                )
            }
    }
    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product",
                    it
                )
            }

    }

    /**
     * A function to get the dashboard items list. The list will be an overall items list, not based on the user's id.
     */
    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.toString())

                val productList: ArrayList<Products> = ArrayList()
                for (item in document.documents) {
                    val allProducts = item.toObject(Products::class.java)!!
                    allProducts.product_id = item.id

                    productList.add(allProducts)
                }
                fragment.successDashboardItemsList(productList)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error getting item list", it)
            }
    }


}