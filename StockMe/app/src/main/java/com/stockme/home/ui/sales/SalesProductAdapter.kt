package com.stockme.home.ui.sales

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stockme.R
import com.stockme.model.Product

class SalesProductAdapter(private val products: List<Product>):
    RecyclerView.Adapter<SalesProductAdapter.ViewHolder>(), Filterable {

    var productListFiltered: ArrayList<Product> = ArrayList(products)

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_sales_product
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesProductAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesProductAdapter.ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.descriptionText).text = productListFiltered[position].description
        holder.itemView.findViewById<TextView>(R.id.stock).text = (productListFiltered[position].currentStock.toString()    + " UN")

        if (productListFiltered[position].image != null) {
            Picasso.get().load(Uri.parse(productListFiltered[position].image)).into(holder.itemView.findViewById<ImageView>(R.id.image))
        }
    }

    fun products() = products

    override fun getItemCount(): Int = productListFiltered.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                var productList = products
                if (charSearch.isNotEmpty()) productList = productList.filter { product ->
                    product.description.lowercase().contains(charSearch.lowercase()) || product.code.lowercase().contains(charSearch.lowercase())
                }
                productListFiltered.clear()
                productListFiltered.addAll(productList)
                return FilterResults().apply { values = productListFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productListFiltered = results?.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }
}
