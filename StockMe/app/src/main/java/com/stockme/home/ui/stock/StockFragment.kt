package com.stockme.home.ui.stock

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.stockme.R
import com.stockme.databinding.FragmentStockBinding
import com.stockme.home.ui.CartActivity
import com.stockme.home.ui.sales.SalesProductAdapter
import com.stockme.model.Product
import com.stockme.product.RecyclerItemClickListener

class StockFragment : Fragment() {

    private val REQUEST_CART_CONFIRM: Int = 42

    private lateinit var viewModel: StockViewModel
    private var _binding: FragmentStockBinding? = null

    private val binding get() = _binding!!

    private lateinit var productAdapter: SalesProductAdapter

    private val products = ArrayList<Product>()
    private val cart = ArrayList<Product>()

    companion object {
        val CART_TRANSACTION_COMPLETE: Int = 43
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        viewModel =
                ViewModelProvider(this).get(StockViewModel::class.java)

        _binding = FragmentStockBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.productList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchNotFound.root.visibility = View.GONE

        viewModel.fetchProducts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.productList.addOnItemTouchListener(
                RecyclerItemClickListener(
                        context,
                        binding.productList,
                        object : RecyclerItemClickListener.OnItemClickListener {
                            override fun onItemClick(view: View?, position: Int) {
                                val product = productAdapter.productListFiltered[position]
                                promptAddToCart(product)
                            }

                            override fun onLongItemClick(view: View?, position: Int) { }
                        })
        )

        setupObserver()
    }


    private fun promptAddToCart(product: Product) {
        if (product.currentStock == product.maxStock) {
            Snackbar.make(binding.root, R.string.dialog_cart_max_stock_text, Snackbar.LENGTH_SHORT).show()
            return
        }
        val type = InputType.TYPE_CLASS_NUMBER
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_cart_add_text)
            input(inputType = type)
            positiveButton(R.string.dialog_cart_yes) {
                val quantity = it.getInputField().text.toString().toInt()

                if ((product.currentStock + quantity) > product.maxStock) {
                    Snackbar.make(binding.root, R.string.dialog_stock_max_stock, Snackbar.LENGTH_LONG).show()
                    return@positiveButton
                }
                val prodInCart = product.copy(currentStock = quantity)

                val existingInCart = cart.find { it.id == prodInCart.id }

                if (existingInCart == null) {
                    cart.add(prodInCart)
                } else {
                    cart.remove(existingInCart)
                    cart.add(existingInCart.copy(currentStock = existingInCart.currentStock + quantity))
                }
            }
            negativeButton(R.string.dialog_cart_no)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sales_search, menu)
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                productAdapter.filter.filter(newText) {
                    binding.searchNotFound.root.visibility =
                            if (productAdapter.itemCount > 0) View.GONE else View.VISIBLE
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean = false
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan -> {
                initScanner()
                true
            }
            R.id.action_cart -> {
                if (cart.isEmpty()) {
                    Snackbar.make(binding.root, R.string.dialog_cart_empty_text, Snackbar.LENGTH_SHORT).show()
                    return true
                }
                val intent = Intent(requireContext(), CartActivity::class.java).apply {
                    putExtra("cart", cart)
                    putExtra("isSales", false)
                }

                startActivityForResult(intent, REQUEST_CART_CONFIRM)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initScanner() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == CART_TRANSACTION_COMPLETE) {
            cart.clear()
            return
        }

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        // Resultado del scanner
        if (result.contents == null) {
            Snackbar.make(binding.root, "Cancelado", Snackbar.LENGTH_LONG).show()
        }
        else {
            val product = productAdapter.products().find { it.code == result.contents }
            if (product != null) promptAddToCart(product)
            else Snackbar.make(binding.root, "Producto #" + result.contents + " no encontrado", Snackbar.LENGTH_LONG).show()
        }
    }


    private fun setupObserver() {
        viewModel.fetchProductsLiveData.observe(viewLifecycleOwner) {
            binding.productList.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

            if (it != null) {
                products.clear()
                products.addAll(it)
                productAdapter = SalesProductAdapter(products)
                binding.productList.apply {
                    adapter = productAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                    itemAnimator = DefaultItemAnimator()
                }
            } else {
                Snackbar.make(binding.root, R.string.product_detail_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}