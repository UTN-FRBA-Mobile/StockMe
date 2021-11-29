package com.stockme.productdetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import com.stockme.R
import com.stockme.databinding.ActivityProductDetailBinding
import com.stockme.model.Product
import com.stockme.productdetail.viewmodel.ProductDetailViewModel
import com.stockme.utils.hideProgress
import com.stockme.utils.showProgress
import com.stockme.utils.showSnackBar
import java.text.NumberFormat
import java.util.*
import pub.devrel.easypermissions.EasyPermissions




class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel = ProductDetailViewModel()
    private var productId: String? = null
    private var photoURL: String? = null
    private var product: Product? = null
    private var price: String? = null

    private val RC_CAMERA_PERM = 123

    private val addTextChangedListener = object: TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val stringText = s.toString()

            if (stringText != price) {
                binding.productPrice.removeTextChangedListener(this)

                val locale: Locale = resources.configuration.locales[0]
                val currency = Currency.getInstance(locale)
                val cleanString = stringText.replace("[${currency.symbol},.]".toRegex(), "")
                val parsed = cleanString.toDouble()
                val formatted = NumberFormat.getCurrencyInstance(locale).format(parsed / 100)

                price = formatted
                binding.productPrice.setText(formatted)
                binding.productPrice.setSelection(formatted.length)
                binding.productPrice.addTextChangedListener(this)
            }
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val photoBitmap = it.data?.extras?.get("data") as Bitmap
                showProgress()
                viewModel.uploadProductPhoto(photoBitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setupViews()
        setupObserver()

        if (intent.extras == null) {
            setTitle(R.string.product_detail_title_new_product)
        }

        intent.extras?.getString(PRODUCT_ID)?.let {
            productId = it
            showProgress()
            viewModel.fetchProduct(it)
            if (productId == null) {
                setTitle(R.string.product_detail_title_new_product)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun setupViews() {
        binding.productScanButton.setOnClickListener {
            scanBarCode()
        }

        binding.productImageView.setOnClickListener {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                openCamera()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_camera),
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA)
            }
        }

        binding.productSaveButton.setOnClickListener {
            saveProduct()
        }

        binding.productPrice.addTextChangedListener(addTextChangedListener)
    }

    private fun setupObserver() {
        viewModel.fetchProductLiveData.observe(this) {
            hideProgress()
            if (it != null) {
                product = it
                fillViewWithProduct(it)
            } else {
                showSnackBar(binding.root, R.string.product_detail_error)
            }
        }

        viewModel.createProductLiveData.observe(this) {
            hideProgress()
            if (it) {
                MaterialDialog(this).show {
                    title(R.string.product_detail_product_created_title)
                    positiveButton(R.string.common_positive_cta) {
                        finish()
                    }
                }
            } else {
                showSnackBar(binding.root, R.string.product_detail_error)
            }
        }

        viewModel.editProductLiveData.observe(this) {
            hideProgress()
            if (it) {
                MaterialDialog(this).show {
                    title(R.string.product_detail_product_modified_title)
                    positiveButton(R.string.common_positive_cta)
                }
            } else {
                showSnackBar(binding.root, R.string.product_detail_error)
            }
        }

        viewModel.uploadProductPhotoLiveData.observe(this) {
            hideProgress()
            if (it != null) {
                photoURL = it
                setPhoto(photoURL)
                MaterialDialog(this).show {
                    title(R.string.product_detail_product_upload_photo_title)
                    positiveButton(R.string.common_positive_cta)
                }
            } else {
                showSnackBar(binding.root, R.string.product_detail_error)
            }
        }
    }

    private fun scanBarCode() {
        IntentIntegrator(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        if (result.contents == null) {
            Snackbar.make(binding.root, "Cancelado", Snackbar.LENGTH_LONG).show()
            return
        }
        val products: List<Product> = listOf()
        val existsCode = products.any { it.code == result.contents }
        if (existsCode) {
            Snackbar.make(binding.root, "Ya existe un producto con código #" + result.contents, Snackbar.LENGTH_LONG).show()
            return
        }
        binding.productBarcode.text = result.contents
        binding.productBarcode.visibility = VISIBLE
        binding.icBarcode.visibility = VISIBLE
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getResult.launch(takePictureIntent)
    }

    private fun saveProduct() {
        if (!productId.isNullOrBlank()) {
            editProduct()
        } else {
            createProduct()
        }
    }

    private fun fillViewWithProduct(product: Product) {
        setPhoto(product.image)
        binding.icBarcode.visibility = VISIBLE
        binding.productBarcode.visibility = VISIBLE
        binding.productBarcode.text = product.code
        binding.productDescription.setText(product.description)
        binding.productPrice.setText(product.price)
        binding.productCheckbox.isChecked = product.isEnable
        binding.productCurrentStockEditText.setText(product.currentStock.toString())
        binding.productMinStockEditText.setText(product.minStock.toString())
        binding.productMaxStockEditText.setText(product.maxStock.toString())
    }

    private fun setPhoto(imageURL: String?) {
        Picasso
            .get()
            .load(imageURL)
            .placeholder(R.drawable.ic_empty_image)
            .error(R.drawable.ic_empty_image)
            .into(binding.productImageView, object: com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.productImageView.scaleType = ImageView.ScaleType.FIT_XY
                }
                override fun onError(e: java.lang.Exception?) {
                    binding.productImageView.setImageResource(R.drawable.ic_empty_image)
                    binding.productImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    Log.e("ProductDetailActivity", e?.localizedMessage.toString())
                }
            })
    }

    private fun createProduct() {
        if (areFieldsValid()) {
            showProgress()
            viewModel.createProduct(createProductFromData())
        } else {
            showSnackBar(binding.root, R.string.product_detail_invalid_parameters)
        }
    }

    private fun editProduct() {
        if (areFieldsValid()) {
            productId?.let {
                showProgress()
                viewModel.editProduct(productFromData(), it)
            }
        } else {
            showSnackBar(binding.root, R.string.product_detail_invalid_parameters)
        }
    }

    private fun productImage(): String? =
        when {
            photoURL != null -> {
                photoURL
            }
            product?.image != null -> {
                product?.image
            }
            else -> {
                null
            }
        }

    private fun createProductFromData(): Product = Product(
        photoURL,
        binding.productBarcode.text.toString(),
        binding.productCurrentStockEditText.text.toString().toInt(),
        binding.productMaxStockEditText.text.toString().toInt(),
        binding.productMinStockEditText.text.toString().toInt(),
        binding.productPrice.text.toString(),
        binding.productCheckbox.isChecked,
        binding.productDescription.text.toString())

    private fun productFromData(): Product = Product(
        productImage(),
        binding.productBarcode.text.toString(),
        binding.productCurrentStockEditText.text.toString().toInt(),
        binding.productMaxStockEditText.text.toString().toInt(),
        binding.productMinStockEditText.text.toString().toInt(),
        binding.productPrice.text.toString(),
        binding.productCheckbox.isChecked,
        binding.productDescription.text.toString())

    private fun areFieldsValid(): Boolean =
        !binding.productBarcode.text.toString().isNullOrBlank() &&
        !binding.productCurrentStockEditText.text.toString().isNullOrBlank() &&
        !binding.productMaxStockEditText.text.toString().isNullOrBlank() &&
        !binding.productMinStockEditText.text.toString().isNullOrBlank() &&
        !binding.productPrice.text.toString().isNullOrBlank() &&
        !binding.productDescription.text.toString().isNullOrBlank()

    companion object {
        const val PRODUCT_ID = "PRODUCT_ID"
    }
}
