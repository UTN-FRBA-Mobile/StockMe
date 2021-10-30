package com.stockme.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.stockme.R
import com.stockme.databinding.FragmentProductListBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
//            findNavController().navigate(R.id.action_ProductListFragment_to_SecondFragment)
        }
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
        binding.list.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val products : List<Product> = listOf(
            Product("1111111", "Producto 1", null, 11.0, true, Random.nextInt(1, 100), 1, 100),
            Product("2222222", "Producto 2", null, 12.0, true, Random.nextInt(1, 100), 1, 100),
            Product("3333333", "Producto 3", null, 13.0, true, Random.nextInt(1, 100), 1, 100),
            Product("4444444", "Producto 4", null, 14.0, true, Random.nextInt(1, 100), 1, 100),
            Product("5555555", "Producto 5", null, 15.0, true, Random.nextInt(1, 100), 1, 100),
            Product("6666666", "Producto 6", null, 16.0, true, Random.nextInt(1, 100), 1, 100),
            Product("7777777", "Producto 7", null, 17.0, true, Random.nextInt(1, 100), 1, 100),
            Product("8888888", "Producto 8", null, 18.0, true, Random.nextInt(1, 100), 1, 100),
        )

        productAdapter = ProductAdapter(listener, products)
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        binding.list.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu)
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                productAdapter.filter.filter(newText)
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
            Snackbar.make(binding.root, "Producto #" + result.contents, Snackbar.LENGTH_LONG).show()
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