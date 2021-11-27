package com.stockme.product

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stockme.R
import com.stockme.model.Product

class ProductAdapter(private val products: List<Product>):
    RecyclerView.Adapter<ProductAdapter.ViewHolder>(), Filterable {

    var productListFiltered: ArrayList<Product> = ArrayList(products)

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_product
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_product -> {
                holder.itemView.findViewById<TextView>(R.id.descriptionText).text = productListFiltered[position].description
                holder.itemView.findViewById<TextView>(R.id.codeText).text = ("#" + productListFiltered[position].code)
                holder.itemView.findViewById<TextView>(R.id.stock).text    = (productListFiltered[position].currentStock.toString()    + " UN")
                holder.itemView.findViewById<TextView>(R.id.stockMin).text = (productListFiltered[position].minStock.toString() + " UN")
                holder.itemView.findViewById<TextView>(R.id.stockMax).text = (productListFiltered[position].maxStock.toString() + " UN")

                if (productListFiltered[position].image != null) {
                    Picasso.get().load(Uri.parse(productListFiltered[position].image)).into(holder.itemView.findViewById<ImageView>(R.id.image))
                }
            }
            else -> {}
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
