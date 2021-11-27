package com.stockme.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.stockme.R
import com.stockme.databinding.FragmentProductListBinding
import com.stockme.model.Product
import com.stockme.productdetail.ProductDetailActivity
import kotlin.random.Random

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var productAdapter: ProductAdapter

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
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onStart() {
        super.onStart()
        binding.productList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchNotFound.root.visibility = View.GONE

        val products : List<Product> = listOf(
            Product(id = "0RXinwZOhmzRf2tJzgQU", code = "1111111", description = "Producto 1", price = "11.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),
            Product(id = "NX5yZjFTqjqBSkrbg6yI", code = "2222222", description = "Producto 2", price = "12.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),
            Product(id = "a6c48369-035f-4a33-91f4-e65507e6c1d0", code = "3333333", description = "Producto 3", price = "13.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),
        )

        productAdapter = ProductAdapter(listener, products)
        binding.productList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        binding.productList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnFragmentInteractionListener {
        fun showFragment(fragment: Fragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProductListFragment()
    }
}