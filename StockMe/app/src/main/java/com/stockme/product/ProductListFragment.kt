package com.stockme.product

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.stockme.R
import com.stockme.databinding.FragmentProductListBinding
import com.stockme.model.Product
import com.stockme.productdetail.ProductDetailActivity

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private val viewModel = ProductListViewModel()

    private lateinit var productAdapter: ProductAdapter

    private val products = ArrayList<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun navigateToDetail(productId: String? = null) {
        val intent = Intent(context, ProductDetailActivity::class.java)
        if (productId != null) intent.putExtra(ProductDetailActivity.PRODUCT_ID, productId)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener { navigateToDetail() }
        binding.productList.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                binding.productList,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val product = productAdapter.productListFiltered[position]
                        navigateToDetail(product.id)
                    }

                    override fun onLongItemClick(view: View?, position: Int) { }
                })
        )

        setupObserver()
    }

    override fun onStart() {
        super.onStart()
        binding.productList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchNotFound.root.visibility = View.GONE

        viewModel.fetchProducts()
    }

    private fun setupObserver() {
        viewModel.fetchProductsLiveData.observe(viewLifecycleOwner) {
            binding.productList.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

            if (it != null) {
                products.clear()
                products.addAll(it)
                productAdapter = ProductAdapter(products)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initScanner() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
            if (product != null) navigateToDetail(product.id)
            else Snackbar.make(binding.root, "Producto #" + result.contents + " no encontrado", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProductListFragment()
    }
}